package com.nageoffer.shortlink.project.config;

import com.nageoffer.shortlink.project.mq.consumer.ShortLinkStatsSaveConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.Subscription;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.SHORT_LINK_STATS_STREAM_GROUP_KEY;
import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.SHORT_LINK_STATS_STREAM_TOPIC_KEY;

/**
 * Redis Stream 消息队列配置类
 * 该配置类用于设置短链接统计数据的异步处理机制
 * 通过 Redis Stream 实现消息的生产和消费
 */
@Configuration
@RequiredArgsConstructor
public class RedisStreamConfiguration {
    // Redis连接工厂，用于创建Redis连接
    private final RedisConnectionFactory redisConnectionFactory;
    // 短链接统计数据保存的消费者，处理具体的消息消费逻辑
    private final ShortLinkStatsSaveConsumer shortLinkStatsSaveConsumer;

    /**
     * 创建自定义的异步消费者线程池
     * 用于处理Stream消息的消费任务
     * 采用单线程模式，避免并发处理带来的数据一致性问题
     */
    @Bean
    public ExecutorService asyncStreamConsumer() {
        AtomicInteger index = new AtomicInteger();
        return new ThreadPoolExecutor(
                1,                          // 核心线程数
                1,                          // 最大线程数
                60,                         // 空闲线程存活时间
                TimeUnit.SECONDS,           // 时间单位
                new SynchronousQueue<>(),   // 使用同步队列，确保任务即时处理
                runnable -> {
                    // 自定义线程工厂，设置线程名称和守护线程属性
                    Thread thread = new Thread(runnable);
                    thread.setName("stream_consumer_short-link_stats_" + index.incrementAndGet());
                    thread.setDaemon(true);
                    return thread;
                },
                new ThreadPoolExecutor.DiscardOldestPolicy() // 当队列满时，丢弃最老的任务
        );
    }

    /**
     * 配置短链接统计数据保存的消费者订阅
     * 设置消息监听容器的各项参数，并启动监听
     */
    @Bean
    public Subscription shortLinkStatsSaveConsumerSubscription(ExecutorService asyncStreamConsumer) {
        // 配置消息监听容器的选项
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .batchSize(10)                      // 每次批量获取10条消息
                        .executor(asyncStreamConsumer)      // 使用自定义的线程池执行消费任务
                        .pollTimeout(Duration.ofSeconds(3)) // 拉取消息的超时时间，需小于Redis的超时时间
                        .build();

        // 配置Stream读取请求
        StreamMessageListenerContainer.StreamReadRequest<String> streamReadRequest =
                StreamMessageListenerContainer.StreamReadRequest
                        .builder(StreamOffset.create(
                                SHORT_LINK_STATS_STREAM_TOPIC_KEY,    // Stream的主题key
                                ReadOffset.lastConsumed()))           // 从上次消费的位置开始读取
                        .cancelOnError(throwable -> false)           // 发生错误时不取消订阅
                        .consumer(Consumer.from(
                                SHORT_LINK_STATS_STREAM_GROUP_KEY,    // 消费者组名称
                                "stats-consumer"))                    // 消费者名称
                        .autoAcknowledge(true)                       // 自动确认消息
                        .build();

        // 创建并配置监听容器
        StreamMessageListenerContainer<String, MapRecord<String, String, String>> listenerContainer = 
                StreamMessageListenerContainer.create(redisConnectionFactory, options);
        
        // 注册消息监听器并获取订阅对象
        Subscription subscription = listenerContainer.register(streamReadRequest, shortLinkStatsSaveConsumer);
        
        // 启动监听容器
        listenerContainer.start();
        
        return subscription;
    }
}
