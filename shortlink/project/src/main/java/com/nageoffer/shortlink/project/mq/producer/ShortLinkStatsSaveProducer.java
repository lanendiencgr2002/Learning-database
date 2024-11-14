package com.nageoffer.shortlink.project.mq.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.SHORT_LINK_STATS_STREAM_TOPIC_KEY;

/**
 * 短链接监控统计数据持久化消息生产者
 * 
 * 该类负责将短链接的统计数据发送到Redis Stream中
 * 选择Redis Stream而不是传统消息队列的原因：
 * 1. Stream提供了消息持久化能力，即使消费者宕机也不会丢失数据
 * 2. 支持消费组模式，可以实现消息的广播和分组消费
 * 3. 自带消息ID和时间戳，便于消息追踪和处理
 */
@Component
@RequiredArgsConstructor
public class ShortLinkStatsSaveProducer {

    /**
     * StringRedisTemplate实例通过构造器注入
     * 使用StringRedisTemplate而不是RedisTemplate的原因是：
     * 我们主要处理的是字符串类型的键值对，StringRedisTemplate更加轻量和高效
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 发送短链接统计数据到Redis Stream
     * 
     * @param producerMap 包含统计数据的Map，key-value都是字符串类型
     *                    通常包含：短链接ID、访问时间、用户标识等信息
     * 
     * 注意：该方法是同步执行的，如果Redis响应较慢可能会影响性能
     * 考虑在调用方使用异步方式处理
     */
    public void send(Map<String, String> producerMap) {
        // 直接将统计数据添加到Redis Stream中
        // Stream的Key通过常量类管理，便于统一维护
        stringRedisTemplate.opsForStream().add(SHORT_LINK_STATS_STREAM_TOPIC_KEY, producerMap);
    }
}
