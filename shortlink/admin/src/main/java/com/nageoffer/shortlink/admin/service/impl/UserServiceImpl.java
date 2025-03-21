package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import com.nageoffer.shortlink.admin.common.convention.exception.ClientException;
import com.nageoffer.shortlink.admin.common.convention.exception.ServiceException;
import com.nageoffer.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.nageoffer.shortlink.admin.dao.entity.UserDO;
import com.nageoffer.shortlink.admin.dao.mapper.UserMapper;
import com.nageoffer.shortlink.admin.dto.req.UserLoginReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserLoginRespDTO;
import com.nageoffer.shortlink.admin.dto.resp.UserRespDTO;
import com.nageoffer.shortlink.admin.service.GroupService;
import com.nageoffer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant.LOCK_USER_REGISTER_KEY;
import static com.nageoffer.shortlink.admin.common.constant.RedisCacheConstant.USER_LOGIN_KEY;
import static com.nageoffer.shortlink.admin.common.enums.UserErrorCodeEnum.USER_EXIST;
import static com.nageoffer.shortlink.admin.common.enums.UserErrorCodeEnum.USER_NAME_EXIST;
import static com.nageoffer.shortlink.admin.common.enums.UserErrorCodeEnum.USER_SAVE_ERROR;

/**
 * 用户服务实现类
 * 
 * 提供用户相关的核心业务逻辑，包括用户注册、登录、信息查询和更新等功能
 * 使用 MyBatis-Plus 简化数据库操作，并集成 Redis 进行用户状态管理
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    // 布隆过滤器，用于快速判断用户名是否已存在，防止缓存穿透
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    
    // Redisson 分布式锁客户端，用于并发控制
    private final RedissonClient redissonClient;
    
    // Redis 模板，用于存储和管理用户登录状态
    private final StringRedisTemplate stringRedisTemplate;
    
    // 用户分组服务，用于在注册时创建默认分组
    private final GroupService groupService;

    /**
     * 根据用户名获取用户信息
     * 
     * @param username 用户名
     * @return 用户响应数据传输对象
     * @throws ServiceException 当用户不存在时抛出异常
     */
    @Override
    public UserRespDTO getUserByUsername(String username) {
        // 构建查询条件：根据用户名精确查询
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, username);
        
        // 执行数据库查询
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        
        // 如果用户不存在，抛出服务异常
        if (userDO == null) {
            throw new ServiceException(UserErrorCodeEnum.USER_NULL);
        }
        
        // 将数据库实体转换为响应DTO
        UserRespDTO result = new UserRespDTO();
        BeanUtils.copyProperties(userDO, result);
        return result;
    }

    /**
     * 检查用户名是否可用（未被注册）
     * 
     * @param username 待检查的用户名
     * @return 是否可用（true 表示可用，false 表示已存在）
     */
    @Override
    public Boolean hasUsername(String username) {
        // 使用布隆过滤器快速判断用户名是否已存在
        return !userRegisterCachePenetrationBloomFilter.contains(username);
    }

    /**
     * 用户注册
     * 
     * 提供用户注册功能，包括：
     * 1. 检查用户名是否可用
     * 2. 使用分布式锁防止并发注册
     * 3. 插入用户数据
     * 4. 创建默认用户分组
     * 
     * @param requestParam 用户注册请求参数
     * @throws ClientException 注册失败时抛出异常
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void register(UserRegisterReqDTO requestParam) {
        // 检查用户名是否已存在
        if (!hasUsername(requestParam.getUsername())) {
            throw new ClientException(USER_NAME_EXIST);
        }
        
        // 获取分布式锁，防止并发注册
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY + requestParam.getUsername());
        if (!lock.tryLock()) {
            throw new ClientException(USER_NAME_EXIST);
        }
        
        try {
            // 插入用户数据
            int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDO.class));
            
            // 检查数据插入是否成功
            if (inserted < 1) {
                throw new ClientException(USER_SAVE_ERROR);
            }
            
            // 为新用户创建默认分组
            groupService.saveGroup(requestParam.getUsername(), "默认分组");
            
            // 将用户名添加到布隆过滤器中，防止缓存穿透
            userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
        } catch (DuplicateKeyException ex) {
            throw new ClientException(USER_EXIST);
        } finally {
            // 释放分布式锁
            lock.unlock();
        }
    }

    /**
     * 更新用户信息
     * 
     * 安全性考虑：
     * - 只允许用户更新自己的信息
     * - 通过 UserContext 验证当前登录用户身份
     * 
     * @param requestParam 用户更新请求参数
     * @throws ClientException 当尝试修改其他用户信息时抛出异常
     */
    @Override
    public void update(UserUpdateReqDTO requestParam) {
        // 安全检查：确保用户只能修改自己的信息
        if (!Objects.equals(requestParam.getUsername(), UserContext.getUsername())) {
            throw new ClientException("当前登录用户修改请求异常");
        }
        
        // 构建更新条件，确保只更新指定用户的数据
        LambdaUpdateWrapper<UserDO> updateWrapper = Wrappers.lambdaUpdate(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername());
        
        // 执行更新操作
        baseMapper.update(BeanUtil.toBean(requestParam, UserDO.class), updateWrapper);
    }

    /**
     * 用户登录处理
     * 
     * 登录流程：
     * 1. 验证用户名密码
     * 2. 检查是否已登录，如果已登录则续期并返回现有token
     * 3. 未登录则创建新的登录会话
     * 
     * Redis存储结构：
     * Key: login_用户名
     * Value: Hash结构
     *   - Key: token标识
     *   - Value: 用户信息JSON字符串
     * 
     * @param requestParam 登录请求参数
     * @return 登录响应，包含访问令牌
     * @throws ClientException 当用户不存在或登录异常时抛出
     */
    @Override
    public UserLoginRespDTO login(UserLoginReqDTO requestParam) {
        // 验证用户账号密码
        LambdaQueryWrapper<UserDO> queryWrapper = Wrappers.lambdaQuery(UserDO.class)
                .eq(UserDO::getUsername, requestParam.getUsername())
                .eq(UserDO::getPassword, requestParam.getPassword())
                .eq(UserDO::getDelFlag, 0);  // 确保账号未被注销
        UserDO userDO = baseMapper.selectOne(queryWrapper);
        
        // 用户不存在或密码错误
        if (userDO == null) {
            throw new ClientException("用户不存在");
        }

        // 检查是否已登录，如果已登录则续期并返回现有token
        Map<Object, Object> hasLoginMap = stringRedisTemplate.opsForHash()
                .entries(USER_LOGIN_KEY + requestParam.getUsername());
        // 如果已登录，延长登录有效期 重置过期时间
        if (CollUtil.isNotEmpty(hasLoginMap)) {
            stringRedisTemplate.expire(
                USER_LOGIN_KEY + requestParam.getUsername(), 
                30L, 
                TimeUnit.MINUTES
            );
            
            // 返回现有token
            String token = hasLoginMap.keySet().stream()
                    .findFirst()
                    .map(Object::toString)
                    .orElseThrow(() -> new ClientException("用户登录错误"));
            return new UserLoginRespDTO(token);
        }
        // 如果已经过期了，创建新的登录会话
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().put(
            USER_LOGIN_KEY + requestParam.getUsername(), 
            uuid, 
            JSON.toJSONString(userDO)
        );
        
        // 设置登录会话有效期
        stringRedisTemplate.expire(
            USER_LOGIN_KEY + requestParam.getUsername(), 
            30L, 
            TimeUnit.MINUTES
        );
        
        return new UserLoginRespDTO(uuid);
    }

    /**
     * 验证用户登录状态
     * 
     * @param username 用户名
     * @param token 登录令牌
     * @return 是否处于登录状态
     */
    @Override
    public Boolean checkLogin(String username, String token) {
        return stringRedisTemplate.opsForHash().get(USER_LOGIN_KEY + username, token) != null;
    }

    /**
     * 用户登出处理
     * 
     * 安全考虑：
     * - 验证token有效性
     * - 清除登录会话信息
     * 
     * @param username 用户名
     * @param token 登录令牌
     * @throws ClientException 当token无效或用户未登录时抛出
     */
    @Override
    public void logout(String username, String token) {
        // 验证用户是否处于登录状态
        if (checkLogin(username, token)) {
            // 清除登录会话信息
            stringRedisTemplate.delete(USER_LOGIN_KEY + username);
            return;
        }
        throw new ClientException("用户Token不存在或用户未登录");
    }
}
