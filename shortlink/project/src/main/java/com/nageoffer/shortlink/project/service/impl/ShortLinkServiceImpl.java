package com.nageoffer.shortlink.project.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.project.common.convention.exception.ClientException;
import com.nageoffer.shortlink.project.common.convention.exception.ServiceException;
import com.nageoffer.shortlink.project.common.enums.VailDateTypeEnum;
import com.nageoffer.shortlink.project.config.GotoDomainWhiteListConfiguration;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkDO;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkMapper;
import com.nageoffer.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkBatchCreateReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkPageReqDTO;
import com.nageoffer.shortlink.project.dto.req.ShortLinkUpdateReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkBaseInfoRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkBatchCreateRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkPageRespDTO;
import com.nageoffer.shortlink.project.mq.producer.ShortLinkStatsSaveProducer;
import com.nageoffer.shortlink.project.service.ShortLinkService;
import com.nageoffer.shortlink.project.toolkit.HashUtil;
import com.nageoffer.shortlink.project.toolkit.LinkUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.GOTO_IS_NULL_SHORT_LINK_KEY;
import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.GOTO_SHORT_LINK_KEY;
import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.LOCK_GID_UPDATE_KEY;
import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.LOCK_GOTO_SHORT_LINK_KEY;
import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.SHORT_LINK_CREATE_LOCK_KEY;
import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.SHORT_LINK_STATS_UIP_KEY;
import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.SHORT_LINK_STATS_UV_KEY;

/**
 * 短链接接口实现层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLinkDO> implements ShortLinkService {

    /**
     * 布隆过滤器,用于防止短链接创建时的缓存穿透
     * 通过将已存在的短链接URL存入过滤器中,可以快速判断URL是否已存在
     */
    private final RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter;

    /**
     * 短链接跳转关系Mapper接口
     * 用于操作短链接跳转关系表,存储短链接与原始URL的映射关系
     */
    private final ShortLinkGotoMapper shortLinkGotoMapper;

    /**
     * Redis操作模板
     * 用于处理短链接相关的缓存操作,如统计数据、临时存储等
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Redisson客户端
     * 提供分布式锁等功能,用于处理并发场景下的数据一致性
     */
    private final RedissonClient redissonClient;

    /**
     * 短链接统计数据保存生产者
     * 通过消息队列异步处理短链接的访问统计数据
     */
    private final ShortLinkStatsSaveProducer shortLinkStatsSaveProducer;

    /**
     * 跳转域名白名单配置
     * 用于控制短链接可跳转的目标域名范围,防止恶意跳转
     */
    private final GotoDomainWhiteListConfiguration gotoDomainWhiteListConfiguration;

    /**
     * 默认短链接域名
     * 从配置文件中注入,用于生成短链接时的默认域名部分
     */
    @Value("${short-link.domain.default}")
    private String createShortLinkDefaultDomain;

    /**
     * 创建短链接
     * 
     * 该方法实现了短链接的创建逻辑,主要包含以下步骤:
     * 1. 验证目标URL是否在白名单内,防止恶意跳转
     * 2. 生成唯一的短链接后缀(6位62进制)
     * 3. 构建并保存短链接相关数据
     * 4. 设置缓存并更新布隆过滤器
     * 
     * 关键考虑点:
     * - 使用布隆过滤器防止缓存穿透
     * - 通过数据库唯一索引保证短链接唯一性
     * - 采用缓存预热机制提高访问性能
     * - 异常处理确保数据一致性
     *
     * @param requestParam 包含原始URL、分组ID、有效期等创建参数的DTO对象
     * @return 返回创建成功的短链接信息,包含完整短链接地址
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public ShortLinkCreateRespDTO createShortLink(ShortLinkCreateReqDTO requestParam) {
        // 验证目标URL是否在白名单内,防止恶意跳转
        verificationWhitelist(requestParam.getOriginUrl());
        
        // 生成唯一的6位62进制短链接后缀
        String shortLinkSuffix = generateSuffix(requestParam);
        
        // 拼接完整短链接URL(域名+后缀)
        String fullShortUrl = StrBuilder.create(createShortLinkDefaultDomain) 
                .append("/")
                .append(shortLinkSuffix)
                .toString();
        
        // 构建短链接实体对象,设置基础信息和统计数据初始值
        ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                .domain(createShortLinkDefaultDomain)     // 设置短链接域名
                .originUrl(requestParam.getOriginUrl())   // 设置原始URL
                .gid(requestParam.getGid())              // 设置分组标识
                .createdType(requestParam.getCreatedType()) // 设置创建方式
                .validDateType(requestParam.getValidDateType()) // 设置有效期类型
                .validDate(requestParam.getValidDate())   // 设置具体有效期
                .describe(requestParam.getDescribe())     // 设置链接描述
                .shortUri(shortLinkSuffix)               // 设置短链接后缀
                .enableStatus(0)                         // 设置为可用状态
                .totalPv(0)                             // 初始化页面访问量
                .totalUv(0)                             // 初始化独立访客数
                .totalUip(0)                            // 初始化独立IP数
                .delTime(0L)                            // 设置删除时间标记
                .fullShortUrl(fullShortUrl)             // 设置完整短链接
                .favicon(getFavicon(requestParam.getOriginUrl())) // 获取目标网站图标
                .build();

        // 构建短链接跳转关系对象,用于存储映射关系
        ShortLinkGotoDO linkGotoDO = ShortLinkGotoDO.builder()
                .fullShortUrl(fullShortUrl)
                .gid(requestParam.getGid())
                .build();

        try {
            // 保存短链接基本信息和跳转关系到数据库
            baseMapper.insert(shortLinkDO);
            shortLinkGotoMapper.insert(linkGotoDO);
        } catch (DuplicateKeyException ex) {
            // 处理短链接重复情况:更新布隆过滤器并抛出异常
            if (!shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl)) {
                shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);
            }
            throw new ServiceException(String.format("短链接：%s 生成重复", fullShortUrl));
        }
        
        // 将短链接映射关系缓存到Redis,提高访问性能
        stringRedisTemplate.opsForValue().set(
                String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),
                requestParam.getOriginUrl(),
                LinkUtil.getLinkCacheValidTime(requestParam.getValidDate()), 
                TimeUnit.MILLISECONDS
        );
        
        // 将短链接添加到布隆过滤器,用于防止缓存穿透
        shortUriCreateCachePenetrationBloomFilter.add(fullShortUrl);

        // 返回创建成功的短链接信息
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl("http://" + shortLinkDO.getFullShortUrl())
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    /**
     * 创建短链接(加锁版本)
     * 
     * 该方法通过分布式锁确保在高并发场景下生成短链接的原子性和唯一性。
     * 主要流程:
     * 1. 验证原始URL是否在白名单中
     * 2. 获取分布式锁,防止并发生成重复短链接
     * 3. 生成短链接后缀
     * 4. 构建并保存短链接相关数据
     * 5. 缓存短链接到Redis
     * 
     * @param requestParam 创建短链接的请求参数,包含原始URL、分组ID等信息
     * @return 返回创建成功的短链接信息,包含完整短链接地址
     */
    @Override
    public ShortLinkCreateRespDTO createShortLinkByLock(ShortLinkCreateReqDTO requestParam) {
        // 验证原始URL是否在白名单中,防止恶意URL
        verificationWhitelist(requestParam.getOriginUrl());
        String fullShortUrl;
        
        // 获取分布式锁,确保生成短链接过程的原子性
        RLock lock = redissonClient.getLock(SHORT_LINK_CREATE_LOCK_KEY);
        lock.lock();
        try {
            // 在锁保护下生成短链接后缀,避免重复
            String shortLinkSuffix = generateSuffixByLock(requestParam);
            
            // 拼接完整短链接地址
            fullShortUrl = StrBuilder.create(createShortLinkDefaultDomain)
                    .append("/")
                    .append(shortLinkSuffix)
                    .toString();
                    
            // 构建短链接DO对象,包含完整的短链接信息
            ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                    .domain(createShortLinkDefaultDomain)
                    .originUrl(requestParam.getOriginUrl())
                    .gid(requestParam.getGid())
                    .createdType(requestParam.getCreatedType())
                    .validDateType(requestParam.getValidDateType())
                    .validDate(requestParam.getValidDate())
                    .describe(requestParam.getDescribe())
                    .shortUri(shortLinkSuffix)
                    .enableStatus(0)                // 0表示启用状态
                    .totalPv(0)                    // 初始化访问量
                    .totalUv(0)                    // 初始化独立访客数
                    .totalUip(0)                   // 初始化独立IP数
                    .delTime(0L)                   // 初始化删除时间
                    .fullShortUrl(fullShortUrl)
                    .favicon(getFavicon(requestParam.getOriginUrl()))
                    .build();
                    
            // 构建跳转关系DO对象,用于存储短链接与目标URL的映射
            ShortLinkGotoDO linkGotoDO = ShortLinkGotoDO.builder()
                    .fullShortUrl(fullShortUrl)
                    .gid(requestParam.getGid())
                    .build();
                    
            try {
                // 将短链接信息持久化到数据库
                baseMapper.insert(shortLinkDO);
                shortLinkGotoMapper.insert(linkGotoDO);
            } catch (DuplicateKeyException ex) {
                // 如果发生唯一键冲突,说明短链接已存在
                throw new ServiceException(String.format("短链接：%s 生成重复", fullShortUrl));
            }
            
            // 将短链接映射关系缓存到Redis,提高访问性能  这也是缓存预热 过期时间为有效期
            stringRedisTemplate.opsForValue().set(
                    String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),
                    requestParam.getOriginUrl(),
                    LinkUtil.getLinkCacheValidTime(requestParam.getValidDate()), 
                    TimeUnit.MILLISECONDS
            );
        } finally {
            // 确保锁一定会被释放
            lock.unlock();
        }
        
        // 返回创建成功的短链接信息
        return ShortLinkCreateRespDTO.builder()
                .fullShortUrl("http://" + fullShortUrl)
                .originUrl(requestParam.getOriginUrl())
                .gid(requestParam.getGid())
                .build();
    }

    /**
     * 批量创建短链接
     * 该方法用于一次性创建多个短链接,提高批量处理效率
     *
     * @param requestParam 包含原始URL列表和描述列表的批量创建请求参数
     * @return 返回批量创建的结果,包含创建成功的短链接信息列表和总数
     */
    @Override
    public ShortLinkBatchCreateRespDTO batchCreateShortLink(ShortLinkBatchCreateReqDTO requestParam) {
        // 获取原始URL列表和描述列表
        List<String> originUrls = requestParam.getOriginUrls();
        List<String> describes = requestParam.getDescribes();
        // 用于存储创建成功的短链接信息
        List<ShortLinkBaseInfoRespDTO> result = new ArrayList<>();
        
        // 遍历原始URL列表,逐个创建短链接
        for (int i = 0; i < originUrls.size(); i++) {
            // 将批量请求参数转换为单个创建请求
            ShortLinkCreateReqDTO shortLinkCreateReqDTO = BeanUtil.toBean(requestParam, ShortLinkCreateReqDTO.class);
            shortLinkCreateReqDTO.setOriginUrl(originUrls.get(i));
            shortLinkCreateReqDTO.setDescribe(describes.get(i));
            
            try {
                // 调用单个短链接创建方法
                ShortLinkCreateRespDTO shortLink = createShortLink(shortLinkCreateReqDTO);
                // 构建基础信息响应对象
                ShortLinkBaseInfoRespDTO linkBaseInfoRespDTO = ShortLinkBaseInfoRespDTO.builder()
                        .fullShortUrl(shortLink.getFullShortUrl())
                        .originUrl(shortLink.getOriginUrl())
                        .describe(describes.get(i))
                        .build();
                result.add(linkBaseInfoRespDTO);
            } catch (Throwable ex) {
                // 单个短链接创建失败时记录日志,继续处理下一个
                log.error("批量创建短链接失败，原始参数：{}", originUrls.get(i));
            }
        }
        
        // 构建并返回批量创建结果
        return ShortLinkBatchCreateRespDTO.builder()
                .total(result.size())           // 设置成功创建的总数
                .baseLinkInfos(result)          // 设置创建成功的短链接信息列表
                .build();
    }

    /**
     * 更新短链接信息
     * 该方法用于修改已存在短链接的相关属性,包括:
     * - 原始URL
     * - 有效期设置 
     * - 描述信息
     * - 分组信息等
     *
     * 主要流程:
     * 1. 验证原始URL是否在白名单中
     * 2. 查询并验证短链接是否存在
     * 3. 根据是否跨分组采用不同更新策略
     * 4. 更新缓存确保数据一致性
     *
     * @param requestParam 更新短链接的请求参数,包含新的链接信息
     * @throws ClientException 当短链接不存在时抛出异常
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateShortLink(ShortLinkUpdateReqDTO requestParam) {
        // 验证原始URL是否在白名单中,防止恶意URL
        verificationWhitelist(requestParam.getOriginUrl());
        
        // 查询当前短链接记录
        LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                .eq(ShortLinkDO::getGid, requestParam.getOriginGid())
                .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                .eq(ShortLinkDO::getDelFlag, 0)
                .eq(ShortLinkDO::getEnableStatus, 0);
        ShortLinkDO hasShortLinkDO = baseMapper.selectOne(queryWrapper);
        
        // 如果短链接不存在,抛出异常
        if (hasShortLinkDO == null) {
            throw new ClientException("短链接记录不存在");
        }
        
        // 判断是否要切换为其他分组
        if (Objects.equals(hasShortLinkDO.getGid(), requestParam.getGid())) {
            // 同分组更新:直接修改原记录
            LambdaUpdateWrapper<ShortLinkDO> updateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                    .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0)
                    .set(Objects.equals(requestParam.getValidDateType(), VailDateTypeEnum.PERMANENT.getType()), 
                        ShortLinkDO::getValidDate, null);
            
            // 构建更新对象
            ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                    .domain(hasShortLinkDO.getDomain())
                    .shortUri(hasShortLinkDO.getShortUri())
                    // 仅当原始URL变更时才重新获取favicon
                    .favicon(Objects.equals(requestParam.getOriginUrl(), hasShortLinkDO.getOriginUrl()) ? 
                            hasShortLinkDO.getFavicon() : getFavicon(requestParam.getOriginUrl()))
                    .createdType(hasShortLinkDO.getCreatedType())
                    .gid(requestParam.getGid())
                    .originUrl(requestParam.getOriginUrl())
                    .describe(requestParam.getDescribe())
                    .validDateType(requestParam.getValidDateType())
                    .validDate(requestParam.getValidDate())
                    .build();
            baseMapper.update(shortLinkDO, updateWrapper);
        } else {
            // ⭐ 跨分组短链接更新：高并发安全处理 使用读写锁的写锁，因为跨分组更新需要先删除原记录，再创建新记录
            // 使用写锁的原因：
            // 这是一个涉及多步骤的复杂更新操作
            // 需要确保在更新过程中：
            // 标记原记录为删除
            // 创建新分组记录
            // 更新跳转关系表
            // 这些步骤需要原子性，确保数据一致性
            // 使用读写锁的写锁可以确保这些步骤是原子性的
            RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(
                    String.format(LOCK_GID_UPDATE_KEY, requestParam.getFullShortUrl()));
            RLock rLock = readWriteLock.writeLock();
            rLock.lock();
            try {
                // == 标记原记录为删除 ==
                LambdaUpdateWrapper<ShortLinkDO> linkUpdateWrapper = Wrappers.lambdaUpdate(ShortLinkDO.class)
                        .eq(ShortLinkDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(ShortLinkDO::getGid, hasShortLinkDO.getGid())
                        .eq(ShortLinkDO::getDelFlag, 0)
                        .eq(ShortLinkDO::getDelTime, 0L)
                        .eq(ShortLinkDO::getEnableStatus, 0);
                ShortLinkDO delShortLinkDO = ShortLinkDO.builder()
                        .delTime(System.currentTimeMillis())
                        .build();
                delShortLinkDO.setDelFlag(1);
                baseMapper.update(delShortLinkDO, linkUpdateWrapper);
                
                // == 创建新分组记录 ==
                ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                        .domain(createShortLinkDefaultDomain)
                        .originUrl(requestParam.getOriginUrl())
                        .gid(requestParam.getGid())
                        .createdType(hasShortLinkDO.getCreatedType())
                        .validDateType(requestParam.getValidDateType())
                        .validDate(requestParam.getValidDate())
                        .describe(requestParam.getDescribe())
                        .shortUri(hasShortLinkDO.getShortUri())
                        .enableStatus(hasShortLinkDO.getEnableStatus())
                        .totalPv(hasShortLinkDO.getTotalPv())
                        .totalUv(hasShortLinkDO.getTotalUv())
                        .totalUip(hasShortLinkDO.getTotalUip())
                        .fullShortUrl(hasShortLinkDO.getFullShortUrl())
                        .favicon(Objects.equals(requestParam.getOriginUrl(), hasShortLinkDO.getOriginUrl()) ? 
                                hasShortLinkDO.getFavicon() : getFavicon(requestParam.getOriginUrl()))
                        .delTime(0L)
                        .build();
                baseMapper.insert(shortLinkDO);
                
                // == 更新跳转关系表 ==
                LambdaQueryWrapper<ShortLinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                        .eq(ShortLinkGotoDO::getFullShortUrl, requestParam.getFullShortUrl())
                        .eq(ShortLinkGotoDO::getGid, hasShortLinkDO.getGid());
                ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);
                shortLinkGotoMapper.delete(linkGotoQueryWrapper);
                shortLinkGotoDO.setGid(requestParam.getGid());
                shortLinkGotoMapper.insert(shortLinkGotoDO);
            } finally {
                rLock.unlock();
            }
        }
        
        // 处理缓存更新
        // 当关键信息变更时,删除相关缓存,采用删除而非更新策略确保数据一致性
        if (!Objects.equals(hasShortLinkDO.getValidDateType(), requestParam.getValidDateType())
                || !Objects.equals(hasShortLinkDO.getValidDate(), requestParam.getValidDate())
                || !Objects.equals(hasShortLinkDO.getOriginUrl(), requestParam.getOriginUrl())) {
            stringRedisTemplate.delete(String.format(GOTO_SHORT_LINK_KEY, requestParam.getFullShortUrl()));
            
            // 处理有效期变更导致的空链接缓存
            Date currentDate = new Date();
            if (hasShortLinkDO.getValidDate() != null && hasShortLinkDO.getValidDate().before(currentDate)) {
                if (Objects.equals(requestParam.getValidDateType(), VailDateTypeEnum.PERMANENT.getType()) 
                        || requestParam.getValidDate().after(currentDate)) {
                    stringRedisTemplate.delete(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, requestParam.getFullShortUrl()));
                }
            }
        }
    }

    // 分页查询短链接
    @Override
    public IPage<ShortLinkPageRespDTO> pageShortLink(ShortLinkPageReqDTO requestParam) {
        IPage<ShortLinkDO> resultPage = baseMapper.pageLink(requestParam);
        return resultPage.convert(each -> {
            ShortLinkPageRespDTO result = BeanUtil.toBean(each, ShortLinkPageRespDTO.class);
            result.setDomain("http://" + result.getDomain());
            return result;
        });
    }

    // 查询短链接分组数量
    @Override
    public List<ShortLinkGroupCountQueryRespDTO> listGroupShortLinkCount(List<String> requestParam) {
        QueryWrapper<ShortLinkDO> queryWrapper = Wrappers.query(new ShortLinkDO())
                .select("gid as gid, count(*) as shortLinkCount")
                .in("gid", requestParam)
                .eq("enable_status", 0)
                .eq("del_flag", 0)
                .eq("del_time", 0L)
                .groupBy("gid");
        List<Map<String, Object>> shortLinkDOList = baseMapper.selectMaps(queryWrapper);
        return BeanUtil.copyToList(shortLinkDOList, ShortLinkGroupCountQueryRespDTO.class);
    }

    /**
     * 短链接跳转实现方法
     * 该方法处理短链接的跳转逻辑,包含以下主要步骤:
     * 1. 获取并构建完整短链接
     * 2. 多级缓存查询原始链接
     * 3. 统计访问数据
     * 4. 重定向到原始链接
     *
     * 缓存策略:
     * - 使用Redis作为主缓存
     * - 使用布隆过滤器防止缓存穿透
     * - 使用分布式锁防止缓存击穿
     * - 设置空值缓存防止缓存穿透
     *
     * @param shortUri 短链接URI部分
     * @param request Servlet请求对象
     * @param response Servlet响应对象
     */
    @SneakyThrows
    @Override
    public void restoreUrl(String shortUri, ServletRequest request, ServletResponse response) {
        // 获取请求的域名 xx:8080.com 或者 xx.com 这种情况是包含80端口的
        String serverName = request.getServerName();
        // 80->'' 其他  x->':x'
        String serverPort = Optional.of(request.getServerPort())
                .filter(each -> !Objects.equals(each, 80)) // 过滤掉80端口
                .map(String::valueOf) //转为字符串
                .map(each -> ":" + each) //拼接冒号
                .orElse(""); 
        // 构建完整短链接URL
        String fullShortUrl = serverName + serverPort + "/" + shortUri;

        // 第一级缓存:直接查询Redis
        String originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(originalLink)) {
            // 缓存命中,记录访问统计后直接跳转
            shortLinkStats(buildLinkStatsRecordAndSetUser(fullShortUrl, request, response));
            ((HttpServletResponse) response).sendRedirect(originalLink);
            return;
        }

        // 第二级缓存:布隆过滤器 防止缓存穿透
        boolean contains = shortUriCreateCachePenetrationBloomFilter.contains(fullShortUrl);
        if (!contains) {
            // 布隆过滤器判定链接不存在,直接返回404
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
            return;
        }

        // 检查是否有空值缓存
        String gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
        if (StrUtil.isNotBlank(gotoIsNullShortLink)) {
            ((HttpServletResponse) response).sendRedirect("/page/notfound");
            return;
        }

        // 使用分布式锁防止缓存击穿 保证这波操作完 下一次是读到的缓存的数据
        RLock lock = redissonClient.getLock(String.format(LOCK_GOTO_SHORT_LINK_KEY, fullShortUrl));
        lock.lock();
        try {
            // 双重检查
            originalLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_SHORT_LINK_KEY, fullShortUrl));
            if (StrUtil.isNotBlank(originalLink)) {
                shortLinkStats(buildLinkStatsRecordAndSetUser(fullShortUrl, request, response));
                ((HttpServletResponse) response).sendRedirect(originalLink);
                return;
            }

            // 再次检查空值缓存
            gotoIsNullShortLink = stringRedisTemplate.opsForValue().get(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl));
            if (StrUtil.isNotBlank(gotoIsNullShortLink)) {
                ((HttpServletResponse) response).sendRedirect("/page/notfound");
                return;
            }

            // 查询数据库获取短链接映射关系
            // 根据完全的url来查询gid 因为完整的短链接数据库是通过gid分表的 ShortLinkGotoDO可以通过这个简单的表来查询到gid
            LambdaQueryWrapper<ShortLinkGotoDO> linkGotoQueryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(linkGotoQueryWrapper);
            if (shortLinkGotoDO == null) {
                // 设置空值缓存防止缓存穿透
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30, TimeUnit.MINUTES);
                ((HttpServletResponse) response).sendRedirect("/page/notfound");
                return;
            }

            // 查询短链接详细信息 根据gid和fullShortUrl来查询短链接详细信息
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, shortLinkGotoDO.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, fullShortUrl)
                    .eq(ShortLinkDO::getDelFlag, 0)
                    .eq(ShortLinkDO::getEnableStatus, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);

            // 验证短链接是否有效 如果短链接有效期过了 或者短链接不存在 则设置空值缓存 防止缓存穿透
            if (shortLinkDO == null || (shortLinkDO.getValidDate() != null && shortLinkDO.getValidDate().before(new Date()))) {
                stringRedisTemplate.opsForValue().set(String.format(GOTO_IS_NULL_SHORT_LINK_KEY, fullShortUrl), "-", 30, TimeUnit.MINUTES);
                ((HttpServletResponse) response).sendRedirect("/page/notfound");
                return;
            }

            // 将有效短链接写入缓存 缓存过期时间就是有效期
            stringRedisTemplate.opsForValue().set(
                    String.format(GOTO_SHORT_LINK_KEY, fullShortUrl),
                    shortLinkDO.getOriginUrl(),
                    LinkUtil.getLinkCacheValidTime(shortLinkDO.getValidDate()), TimeUnit.MILLISECONDS
            );

            // 记录访问统计并重定向
            shortLinkStats(buildLinkStatsRecordAndSetUser(fullShortUrl, request, response));
            ((HttpServletResponse) response).sendRedirect(shortLinkDO.getOriginUrl());
        } finally {
            lock.unlock();
        }
    }

    // 构建短链接统计记录并设置用户
    private ShortLinkStatsRecordDTO buildLinkStatsRecordAndSetUser(String fullShortUrl, ServletRequest request, ServletResponse response) {
        // 定义用户标识是否首次访问
        AtomicBoolean uvFirstFlag = new AtomicBoolean();
        // 获取请求中的Cookie
        Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        // 定义用户标识uuid
        AtomicReference<String> uv = new AtomicReference<>();
        // 线程任务：设置用户标识
        Runnable addResponseCookieTask = () -> {
            uv.set(UUID.fastUUID().toString()); // 设置用户标识uuid
            Cookie uvCookie = new Cookie("uv", uv.get()); // 设置用户标识cookie
            uvCookie.setMaxAge(60 * 60 * 24 * 30); // 设置用户标识cookie有效期
            uvCookie.setPath(StrUtil.sub(fullShortUrl, fullShortUrl.indexOf("/"), fullShortUrl.length())); // 设置用户标识cookie路径
            ((HttpServletResponse) response).addCookie(uvCookie); // 设置用户标识cookie
            uvFirstFlag.set(Boolean.TRUE); // 设置用户标识是否首次访问
            stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UV_KEY + fullShortUrl, uv.get()); // 设置用户标识到Redis中
        };
        // 如果请求中的Cookie不为空，则从Cookie中获取用户标识
        if (ArrayUtil.isNotEmpty(cookies)) {
            Arrays.stream(cookies)
                    .filter(each -> Objects.equals(each.getName(), "uv")) // 过滤出用户标识cookie
                    .findFirst() // 获取第一个用户标识cookie
                    .map(Cookie::getValue) // 获取用户标识cookie的值
                    .ifPresentOrElse(each -> { // 如果用户标识cookie的值不为空，则设置用户标识
                        // 设置用户标识
                        uv.set(each);
                        // 设置用户标识到Redis中
                        Long uvAdded = stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UV_KEY + fullShortUrl, each);
                        // 设置用户标识是否首次访问
                        uvFirstFlag.set(uvAdded != null && uvAdded > 0L);
                    }, addResponseCookieTask);
        } else { // 如果请求中的Cookie为空，则设置用户标识
            addResponseCookieTask.run();
        }
        // 获取用户真实IP
        String remoteAddr = LinkUtil.getActualIp(((HttpServletRequest) request));
        // 获取用户操作系统
        String os = LinkUtil.getOs(((HttpServletRequest) request));
        // 获取用户浏览器
        String browser = LinkUtil.getBrowser(((HttpServletRequest) request));
        // 获取用户设备
        String device = LinkUtil.getDevice(((HttpServletRequest) request));
        // 获取用户网络
        String network = LinkUtil.getNetwork(((HttpServletRequest) request));
        // 设置用户IP到Redis中
        Long uipAdded = stringRedisTemplate.opsForSet().add(SHORT_LINK_STATS_UIP_KEY + fullShortUrl, remoteAddr);
        boolean uipFirstFlag = uipAdded != null && uipAdded > 0L;
        return ShortLinkStatsRecordDTO.builder()
                .fullShortUrl(fullShortUrl)
                .uv(uv.get())
                .uvFirstFlag(uvFirstFlag.get())
                .uipFirstFlag(uipFirstFlag)
                .remoteAddr(remoteAddr)
                .os(os)
                .browser(browser)
                .device(device)
                .network(network)
                .currentDate(new Date())
                .build();
    }

    // 短链接统计
    @Override
    public void shortLinkStats(ShortLinkStatsRecordDTO statsRecord) {
        Map<String, String> producerMap = new HashMap<>();
        producerMap.put("statsRecord", JSON.toJSONString(statsRecord));
        // 消息队列为什么选用RocketMQ？详情查看：https://nageoffer.com/shortlink/question
        shortLinkStatsSaveProducer.send(producerMap);
    }

    // 生成短链接后缀
    private String generateSuffix(ShortLinkCreateReqDTO requestParam) {
        int customGenerateCount = 0;
        String shorUri;
        while (true) {
            if (customGenerateCount > 10) {
                throw new ServiceException("短链接频繁生成，请稍后再试");
            }
            String originUrl = requestParam.getOriginUrl();
            originUrl += UUID.randomUUID().toString();
            // 短链接哈希算法生成冲突问题如何解决？详情查看：https://nageoffer.com/shortlink/question
            shorUri = HashUtil.hashToBase62(originUrl);
            // 判断短链接是否存在为什么不使用Set结构？详情查看：https://nageoffer.com/shortlink/question
            // 如果布隆过滤器挂了，里边存的数据全丢失了，怎么恢复呢？详情查看：https://nageoffer.com/shortlink/question
            if (!shortUriCreateCachePenetrationBloomFilter.contains(createShortLinkDefaultDomain + "/" + shorUri)) {
                break;
            }
            customGenerateCount++;
        }
        return shorUri;
    }

    // 生成短链接后缀（加锁）
    private String generateSuffixByLock(ShortLinkCreateReqDTO requestParam) {
        int customGenerateCount = 0;
        String shorUri;
        while (true) {
            if (customGenerateCount > 10) {
                throw new ServiceException("短链接频繁生成，请稍后再试");
            }
            String originUrl = requestParam.getOriginUrl();
            originUrl += UUID.randomUUID().toString();
            // 短链接哈希算法生成冲突问题如何解决？详情查看：https://nageoffer.com/shortlink/question
            shorUri = HashUtil.hashToBase62(originUrl);
            LambdaQueryWrapper<ShortLinkDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkDO.class)
                    .eq(ShortLinkDO::getGid, requestParam.getGid())
                    .eq(ShortLinkDO::getFullShortUrl, createShortLinkDefaultDomain + "/" + shorUri)
                    .eq(ShortLinkDO::getDelFlag, 0);
            ShortLinkDO shortLinkDO = baseMapper.selectOne(queryWrapper);
            if (shortLinkDO == null) {
                break;
            }
            customGenerateCount++;
        }
        return shorUri;
    }

    // 获取图标
    @SneakyThrows
    private String getFavicon(String url) {
        URL targetUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        int responseCode = connection.getResponseCode();
        if (HttpURLConnection.HTTP_OK == responseCode) {
            Document document = Jsoup.connect(url).get();
            Element faviconLink = document.select("link[rel~=(?i)^(shortcut )?icon]").first();
            if (faviconLink != null) {
                return faviconLink.attr("abs:href");
            }
        }
        return null;
    }

    // 验证白名单
    private void verificationWhitelist(String originUrl) {
        Boolean enable = gotoDomainWhiteListConfiguration.getEnable();
        if (enable == null || !enable) {
            return;
        }
        String domain = LinkUtil.extractDomain(originUrl);
        if (StrUtil.isBlank(domain)) {
            throw new ClientException("跳转链接填写错误");
        }
        List<String> details = gotoDomainWhiteListConfiguration.getDetails();
        if (!details.contains(domain)) {
            throw new ClientException("演示环境为避免恶意攻击，请生成以下网站跳转链接：" + gotoDomainWhiteListConfiguration.getNames());
        }
    }
}
