package com.nageoffer.shortlink.project.mq.producer;

import cn.hutool.core.lang.UUID;
import com.nageoffer.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.DELAY_QUEUE_STATS_KEY;

/**
 * 短链接统计延迟消息生产者
 * 该类负责将短链接统计数据发送到Redis延迟队列中
 * 使用Redisson实现延迟队列功能
 * 注：该类已被标记为废弃，可能在未来版本中移除
 */
@Component
@Deprecated
@RequiredArgsConstructor
public class DelayShortLinkStatsProducer {

    /**
     * Redisson客户端实例，用于操作Redis
     * 通过构造器注入的方式注入依赖
     */
    private final RedissonClient redissonClient;

    /**
     * 发送短链接统计数据到延迟队列
     * 
     * @param statsRecord 需要发送的短链接统计记录对象
     */
    public void send(ShortLinkStatsRecordDTO statsRecord) {
        // 为统计记录生成唯一标识符
        statsRecord.setKeys(UUID.fastUUID().toString());
        
        // 获取Redis阻塞队列实例
        RBlockingDeque<ShortLinkStatsRecordDTO> blockingDeque = redissonClient.getBlockingDeque(DELAY_QUEUE_STATS_KEY);
        
        // 基于阻塞队列创建延迟队列
        RDelayedQueue<ShortLinkStatsRecordDTO> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
        
        // 将统计数据放入延迟队列，设置5秒后可被消费
        delayedQueue.offer(statsRecord, 5, TimeUnit.SECONDS);
    }
}
