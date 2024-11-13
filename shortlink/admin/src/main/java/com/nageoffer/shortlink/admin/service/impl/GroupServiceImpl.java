package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import com.nageoffer.shortlink.admin.common.convention.exception.ClientException;
import com.nageoffer.shortlink.admin.common.convention.exception.ServiceException;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.admin.dao.entity.GroupUniqueDO;
import com.nageoffer.shortlink.admin.dao.mapper.GroupMapper;
import com.nageoffer.shortlink.admin.dao.mapper.GroupUniqueMapper;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.ShortLinkGroupRespDTO;
import com.nageoffer.shortlink.admin.remote.ShortLinkActualRemoteService;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.admin.service.GroupService;
import com.nageoffer.shortlink.admin.toolkit.RandomGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant.LOCK_GROUP_CREATE_KEY;

/**
 * 短链接分组服务实现
 * 
 * 核心职责：
 * 1. 管理用户的短链接分组
 * 2. 提供分组的增删改查操作
 * 3. 确保用户分组数量和唯一性
 * 
 * 设计特点：
 * - 使用分布式锁保证并发安全
 * - 利用布隆过滤器防止缓存穿透
 * - 实现严格的分组数量限制
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDO> implements GroupService {

    // 布隆过滤器：用于快速判断分组ID是否已存在，降低数据库查询压力
    private final RBloomFilter<String> gidRegisterCachePenetrationBloomFilter;

    // 分组唯一性校验mapper
    private final GroupUniqueMapper groupUniqueMapper;

    // 远程短链接服务，用于获取分组下的链接数量
    private final ShortLinkActualRemoteService shortLinkActualRemoteService;

    // 分布式锁客户端
    private final RedissonClient redissonClient;

    // 最大分组数量，通过配置文件注入
    @Value("${short-link.group.max-num}")
    private Integer groupMaxNum;

    /**
     * 为当前登录用户保存新的短链接分组
     * 
     * 关键实现：
     * 1. 使用当前登录用户上下文
     * 2. 自动生成唯一分组ID
     * 3. 限制用户最大分组数量
     * 
     * @param groupName 分组名称
     */
    @Override
    public void saveGroup(String groupName) {
        saveGroup(UserContext.getUsername(), groupName);
    }

    /**
     * 为指定用户保存新的短链接分组
     * 
     * 复杂性处理：
     * - 分布式锁防止并发创建
     * - 限制用户最大分组数
     * - 重试机制确保分组ID唯一性
     * 
     * @param username 用户名
     * @param groupName 分组名称
     */
    @Override
    public void saveGroup(String username, String groupName) {
        // 创建用户维度的分布式锁，防止并发创建分组
        RLock lock = redissonClient.getLock(String.format(LOCK_GROUP_CREATE_KEY, username));
        lock.lock();
        try {
            // 检查用户当前分组数量是否超过限制
            LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                    .eq(GroupDO::getUsername, username)
                    .eq(GroupDO::getDelFlag, 0);
            List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
            
            // 超过最大分组数量则抛出客户端异常
            if (CollUtil.isNotEmpty(groupDOList) && groupDOList.size() == groupMaxNum) {
                throw new ClientException(String.format("已超出最大分组数：%d", groupMaxNum));
            }

            // 生成分组ID的重试机制
            int retryCount = 0;
            int maxRetries = 10;
            String gid = null;
            
            // 尝试生成唯一分组ID  因为 id 可能会重复 所以这里要重复几次
            while (retryCount < maxRetries) {
                gid = saveGroupUniqueReturnGid();
                
                // 成功生成分组ID则创建分组
                if (StrUtil.isNotEmpty(gid)) {
                    GroupDO groupDO = GroupDO.builder()
                            .gid(gid)
                            .sortOrder(0)
                            .username(username)
                            .name(groupName)
                            .build();
                    baseMapper.insert(groupDO);
                    
                    // 将分组ID加入布隆过滤器
                    gidRegisterCachePenetrationBloomFilter.add(gid);
                    break;
                }
                retryCount++;
            }

            // 重试后仍无法生成分组ID，抛出服务异常
            if (StrUtil.isEmpty(gid)) {
                throw new ServiceException("生成分组标识频繁");
            }
        } finally {
            // 确保锁被释放
            lock.unlock();
        }
    }

    /**
     * 获取用户的短链接分组列表
     * 
     * 复杂查询逻辑：
     * 1. 查询用户未删除的分组
     * 2. 按排序字段和更新时间倒序排列
     * 3. 远程获取每个分组的短链接数量
     * 
     * @return 用户分组列表，包含分组信息和链接数量
     */
    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        // 构建查询条件：当前用户、未删除分组，按排序和更新时间排序
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getDelFlag, 0)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDO::getSortOrder, GroupDO::getUpdateTime);
        
        // 查询用户分组
        List<GroupDO> groupDOList = baseMapper.selectList(queryWrapper);
        
        // 远程获取每个分组的短链接数量
        Result<List<ShortLinkGroupCountQueryRespDTO>> listResult = shortLinkActualRemoteService
                .listGroupShortLinkCount(groupDOList.stream().map(GroupDO::getGid).toList());
        
        // 将数据库实体对象转换为DTO对象
        List<ShortLinkGroupRespDTO> shortLinkGroupRespDTOList = BeanUtil.copyToList(groupDOList, ShortLinkGroupRespDTO.class);
        
        // 遍历分组列表,为每个分组设置短链接数量
        shortLinkGroupRespDTOList.forEach(each -> {
            // 在远程调用结果中查找匹配的分组ID
            Optional<ShortLinkGroupCountQueryRespDTO> first = listResult.getData().stream()
                    .filter(item -> Objects.equals(item.getGid(), each.getGid())) // 通过分组ID匹配
                    .findFirst(); // 获取第一个匹配结果
                    
            // 如果找到匹配的分组,则设置其短链接数量
            first.ifPresent(item -> each.setShortLinkCount(first.get().getShortLinkCount()));
        });
        
        return shortLinkGroupRespDTOList;
    }

    /**
     * 更新短链接分组信息
     * 
     * 业务逻辑：
     * 1. 仅允许用户更新自己创建的分组
     * 2. 只能更新未删除的分组
     * 3. 只更新分组名称,保持其他字段不变
     * 
     * 安全考虑：
     * - 通过 UserContext 验证用户身份
     * - 使用 delFlag 确保只能修改有效分组
     * 
     * @param requestParam 分组更新请求参数,包含分组ID和新名称
     */
    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO requestParam) {
        // 构建更新条件:当前用户、指定分组ID、未删除状态
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, requestParam.getGid())
                .eq(GroupDO::getDelFlag, 0);
        
        // 创建更新对象,只更新分组名称
        GroupDO groupDO = new GroupDO();
        groupDO.setName(requestParam.getName());
        
        // 执行更新操作
        baseMapper.update(groupDO, updateWrapper);
    }

    /**
     * 删除短链接分组
     * 
     * 业务逻辑：
     * 1. 采用软删除策略,通过更新删除标志实现
     * 2. 仅允许用户删除自己创建的分组
     * 3. 只能删除未被标记为删除的分组
     * 
     * 安全考虑：
     * - 通过 UserContext 验证用户身份
     * - 使用 delFlag 确保不重复删除
     * 
     * 实现步骤：
     * 1. 构建更新条件,确保操作安全性
     * 2. 设置删除标志
     * 3. 执行更新操作
     * 
     * @param gid 待删除的分组标识
     */
    @Override
    public void deleteGroup(String gid) {
        // 构建更新条件:当前用户、指定分组ID、未删除状态
        LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername())
                .eq(GroupDO::getGid, gid)
                .eq(GroupDO::getDelFlag, 0);
                
        // 创建更新对象,设置删除标志为1
        GroupDO groupDO = new GroupDO();
        groupDO.setDelFlag(1);
        
        // 执行软删除更新操作
        baseMapper.update(groupDO, updateWrapper);
    }

    /**
     * 更新短链接分组的排序顺序
     * 
     * 业务功能：
     * - 根据前端传入的排序参数批量更新分组顺序
     * - 支持多个分组同时调整顺序
     * 
     * 实现细节：
     * 1. 遍历前端传入的排序请求列表
     * 2. 为每个分组构建更新对象,仅更新排序字段
     * 3. 确保只能更新当前用户的未删除分组
     * 
     * 安全考虑：
     * - 通过 UserContext 验证用户身份
     * - 使用 delFlag 确保只操作有效分组
     * 
     * @param requestParam 包含分组ID和目标排序值的请求列表
     */
    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> requestParam) {
        requestParam.forEach(each -> {
            // 构建更新对象,只设置排序字段
            GroupDO groupDO = GroupDO.builder()
                    .sortOrder(each.getSortOrder())
                    .build();
                    
            // 构建更新条件:当前用户、指定分组ID、未删除状态
            LambdaUpdateWrapper<GroupDO> updateWrapper = Wrappers.lambdaUpdate(GroupDO.class)
                    .eq(GroupDO::getUsername, UserContext.getUsername())
                    .eq(GroupDO::getGid, each.getGid())
                    .eq(GroupDO::getDelFlag, 0);
                    
            // 执行更新操作
            baseMapper.update(groupDO, updateWrapper);
        });
    }

    /**
     * 生成唯一分组ID
     * 
     * 关键算法：
     * 1. 随机生成分组ID
     * 2. 使用布隆过滤器快速判断ID是否已存在
     * 3. 通过数据库唯一约束确保全局唯一
     * 
     * @return 唯一分组ID，生成失败返回null
     */
    private String saveGroupUniqueReturnGid() {
        // 生成随机分组ID
        String gid = RandomGenerator.generateRandom();
        
        // 使用布隆过滤器快速判断ID是否已存在
        if (gidRegisterCachePenetrationBloomFilter.contains(gid)) {
            return null;
        }
        
        // 尝试插入唯一性校验表
        GroupUniqueDO groupUniqueDO = GroupUniqueDO.builder()
                .gid(gid)
                .build();
        
        try {
            groupUniqueMapper.insert(groupUniqueDO);
        } catch (DuplicateKeyException e) {
            // 数据库唯一约束冲突，返回null
            return null;
        }
        
        return gid;
    }
}
