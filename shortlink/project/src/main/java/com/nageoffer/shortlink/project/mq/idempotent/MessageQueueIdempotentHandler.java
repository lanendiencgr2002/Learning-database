package com.nageoffer.shortlink.project.mq.idempotent;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 消息队列幂等处理器
 * 该类用于确保消息队列中的消息不会被重复处理
 * 通过Redis实现分布式锁和状态追踪来保证消息的幂等性
 */
@Component
@RequiredArgsConstructor
public class MessageQueueIdempotentHandler {

    /**
     * Redis操作模板，用于处理Redis中的键值对操作
     * 使用final修饰确保其不可变性
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Redis中存储幂等标识的键前缀
     * 用于区分不同业务的幂等标识
     */
    private static final String IDEMPOTENT_KEY_PREFIX = "short-link:idempotent:";

    /**
     * 判断当前消息是否正在被消费
     * 
     * @param messageId 消息唯一标识
     * @return true:消息正在被消费 false:消息未被消费
     * 
     * 实现原理：
     * 1. 尝试在Redis中设置一个key，使用setIfAbsent(相当于SETNX)确保原子性
     * 2. 如果设置成功，表示消息未被消费，返回false
     * 3. 如果设置失败，表示消息正在被消费，返回true
     * 4. 设置2分钟的过期时间，防止死锁
     */
    public boolean isMessageBeingConsumed(String messageId) {
        String key = IDEMPOTENT_KEY_PREFIX + messageId;
        // 使用Boolean.FALSE.equals避免空指针异常   key不存在时才能设置
        return Boolean.FALSE.equals(stringRedisTemplate.opsForValue().setIfAbsent(key, "0", 2, TimeUnit.MINUTES));
    }

    /**
     * 检查消息是否已经处理完成
     * 
     * @param messageId 消息唯一标识
     * @return true:消息处理完成 false:消息未处理完成
     * 
     * 实现原理：
     * 1. 获取Redis中的值
     * 2. 如果值为"1"表示消息处理完成
     * 3. 其他情况（包括key不存在）都表示未处理完成
     */
    public boolean isAccomplish(String messageId) {
        String key = IDEMPOTENT_KEY_PREFIX + messageId;
        return Objects.equals(stringRedisTemplate.opsForValue().get(key), "1");
    }

    /**
     * 标记消息处理完成
     * 
     * @param messageId 消息唯一标识
     * 
     * 实现原理：
     * 1. 将Redis中对应key的值设置为"1"
     * 2. 设置2分钟过期时间，避免占用过多内存
     */
    public void setAccomplish(String messageId) {
        String key = IDEMPOTENT_KEY_PREFIX + messageId;
        stringRedisTemplate.opsForValue().set(key, "1", 2, TimeUnit.MINUTES);
    }

    /**
     * 删除消息的幂等标识
     * 通常在消息处理发生异常时调用，用于清理幂等标识
     * 
     * @param messageId 消息唯一标识
     * 
     * 实现原理：
     * 直接删除Redis中的key，允许消息重新被消费
     */
    public void delMessageProcessed(String messageId) {
        String key = IDEMPOTENT_KEY_PREFIX + messageId;
        stringRedisTemplate.delete(key);
    }
}
