package com.nageoffer.shortlink.admin.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 布隆过滤器配置类
 * 
 * 该配置类用于创建两个不同用途的布隆过滤器：
 * 1. 用户注册防缓存穿透过滤器
 * 2. 分组标识防缓存穿透过滤器
 * 
 * 布隆过滤器的主要作用：
 * - 快速判断一个元素是否可能存在于集合中
 * - 可以显著减少对数据库的无效查询
 * - 用于解决缓存穿透问题，提高系统性能
 */
@Configuration(value = "rBloomFilterConfigurationByAdmin")
public class RBloomFilterConfiguration {
    /**
     * 创建用户注册防缓存穿透布隆过滤器
     * 
     * @param redissonClient Redisson客户端实例
     * @return 初始化后的布隆过滤器
     * 
     * 配置说明：
     * - 预期数据量：1亿条数据
     * - 误判率：0.1%（0.001）
     * - 使用场景：用户注册时快速判断用户名是否已存在
     * 
     * 性能考虑：
     * - 较低的误判率会增加内存占用
     * - 数据量预估要适当，过大会浪费内存，过小会增加误判率
     */
    @Bean
    public RBloomFilter<String> userRegisterCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("userRegisterCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(100000000L, 0.001);
        return cachePenetrationBloomFilter;
    }

    /**
     * 创建分组标识防缓存穿透布隆过滤器
     * 
     * @param redissonClient Redisson客户端实例
     * @return 初始化后的布隆过滤器
     * 
     * 配置说明：
     * - 预期数据量：2亿条数据（比用户注册过滤器容量更大）
     * - 误判率：0.1%（0.001）
     * - 使用场景：快速判断分组标识是否已存在
     * 
     * 注意事项：
     * - 布隆过滤器一旦初始化，其容量和误判率就无法修改
     * - 添加的元素数量不应超过预期数据量，否则会导致误判率上升
     */
    @Bean
    public RBloomFilter<String> gidRegisterCachePenetrationBloomFilter(RedissonClient redissonClient) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("gidRegisterCachePenetrationBloomFilter");
        cachePenetrationBloomFilter.tryInit(200000000L, 0.001);
        return cachePenetrationBloomFilter;
    }
}