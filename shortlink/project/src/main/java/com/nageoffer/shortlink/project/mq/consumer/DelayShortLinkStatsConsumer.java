package com.nageoffer.shortlink.project.mq.consumer;

import com.nageoffer.shortlink.project.common.convention.exception.ServiceException;
import com.nageoffer.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.nageoffer.shortlink.project.mq.idempotent.MessageQueueIdempotentHandler;
import com.nageoffer.shortlink.project.service.ShortLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.locks.LockSupport;

import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.DELAY_QUEUE_STATS_KEY;

/**
 * 延迟短链接统计消费者组件
 * 该组件负责从Redis延迟队列中消费短链接统计数据，并进行处理
 * 已标记为@Deprecated，表示该类已过时，建议使用新的实现方式
 */
@Deprecated
@Slf4j
@Component
@RequiredArgsConstructor
public class DelayShortLinkStatsConsumer implements InitializingBean {

    private final RedissonClient redissonClient;
    private final ShortLinkService shortLinkService;
    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;

    public void onMessage() {
        // 创建单线程执行器，用于处理延迟队列消息
        Executors.newSingleThreadExecutor(
                        runnable -> {
                            Thread thread = new Thread(runnable);
                            thread.setName("delay_short-link_stats_consumer");
                            thread.setDaemon(Boolean.TRUE);  // 设置为守护线程
                            return thread;
                        })
                .execute(() -> {
                    // 获取Redis阻塞队列和延迟队列
                    RBlockingDeque<ShortLinkStatsRecordDTO> blockingDeque = redissonClient.getBlockingDeque(DELAY_QUEUE_STATS_KEY);
                    RDelayedQueue<ShortLinkStatsRecordDTO> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
                    
                    // 无限循环处理队列消息
                    for (; ; ) {
                        try {
                            // 尝试从延迟队列中获取消息
                            ShortLinkStatsRecordDTO statsRecord = delayedQueue.poll();
                            if (statsRecord != null) {
                                // 检查消息是否正在被消费（幂等性检查）
                                if (messageQueueIdempotentHandler.isMessageBeingConsumed(statsRecord.getKeys())) {
                                    // 检查消息是否已经处理完成
                                    if (messageQueueIdempotentHandler.isAccomplish(statsRecord.getKeys())) {
                                        return;
                                    }
                                    throw new ServiceException("消息未完成流程，需要消息队列重试");
                                }
                                
                                try {
                                    // 处理短链接统计数据
                                    shortLinkService.shortLinkStats(statsRecord);
                                } catch (Throwable ex) {
                                    // 处理失败时，删除消息处理状态并记录错误日志
                                    messageQueueIdempotentHandler.delMessageProcessed(statsRecord.getKeys());
                                    log.error("延迟记录短链接监控消费异常", ex);
                                }
                                
                                // 标记消息处理完成
                                messageQueueIdempotentHandler.setAccomplish(statsRecord.getKeys());
                                continue;
                            }
                            // 如果没有消息，线程休眠500毫秒
                            LockSupport.parkUntil(500);
                        } catch (Throwable ignored) {
                            // 忽略所有异常，确保循环继续运行
                        }
                    }
                });
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 初始化时不自动启动消费者
        // onMessage();
    }
}
