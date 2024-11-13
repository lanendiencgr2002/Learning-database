package com.nageoffer.shortlink.project.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redis布隆过滤器配置类
 * 
 * 该配置类用于初始化和管理基于Redisson的布隆过滤器，主要用途：
 * 1. 防止缓存穿透
 * 2. 提前过滤不存在的短链接请求
 * 3. 减轻数据库压力
 * 
 * 布隆过滤器特点：
 * - 空间效率高
 * - 有一定的误判率
 * - 不支持删除元素
 */
@Configuration
public class RBloomFilterConfiguration {

    /**
     * 配置短链接创建的布隆过滤器
     * 
     * 参数设置说明：
     * - 预计元素数量：100000000（1亿）
     * - 误判率：0.001（0.1%）
     * 
     * 性能考虑：
     * - 容量设置充分考虑了业务增长空间
     * - 误判率设置在性能和准确性之间做了平衡
     * - 布隆过滤器的大小和hash函数数量会根据这两个参数自动优化
     * 
     * 使用场景：
     * - 创建短链接前检查是否已存在
     * - 减少对数据库的无效查询
     * 
     * @param redissonClient Redisson客户端实例
     * @return 配置好的布隆过滤器实例
     */
    @Bean
    public RBloomFilter<String> shortUriCreateCachePenetrationBloomFilter(RedissonClient redissonClient) {
        // 获取布隆过滤器实例，指定唯一名称 会在 redis 中持久化，如果redis已经存在，则获取已存在的实例
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter("shortUriCreateCachePenetrationBloomFilter");
        
        // 初始化布隆过滤器，设置预期元素数量和误判率
        // 这些参数会影响布隆过滤器的大小和性能
        cachePenetrationBloomFilter.tryInit(100000000L, 0.001);
        
        return cachePenetrationBloomFilter;
    }
}
