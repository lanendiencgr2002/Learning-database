package com.nageoffer.shortlink.project.initialize;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.*;

/**
 * 短链接访问统计的Redis Stream初始化任务
 * 
 * 核心功能：
 * 1. 应用启动时自动初始化Redis Stream消费者组
 * 2. 确保用于短链接访问统计的Stream主题和消费者组存在
 * 
 * 技术实现：
 * - 实现InitializingBean接口，在Spring容器启动完成后自动执行初始化
 * - 使用Redis Stream作为消息队列，支持消息持久化和消费组模式
 * - 采用@RequiredArgsConstructor简化依赖注入
 * 
 * 重要说明：
 * - 该任务确保了短链接访问统计功能所需的Redis Stream基础设施的可用性
 * - 通过消费者组模式支持多实例部署时的消息消费
 */
@Component
@RequiredArgsConstructor
public class ShortLinkStatsStreamInitializeTask implements InitializingBean {

    /**
     * Redis操作模板
     * - 使用StringRedisTemplate进行Redis操作
     * - 通过@RequiredArgsConstructor注解实现构造器注入
     * - final修饰确保线程安全
     */
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 初始化方法，在Spring容器启动完成后自动执行
     * 
     * 执行流程：
     * 1. 检查Stream主题是否存在
     * 2. 如果主题不存在，创建新的消费者组（会自动创建主题）
     * 
     * 异常处理：
     * - Redis连接异常时hasKey()返回null
     * - 方法声明throws Exception处理潜在异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 检查Stream主题是否存在
        Boolean hasKey = stringRedisTemplate.hasKey(SHORT_LINK_STATS_STREAM_TOPIC_KEY);
        
        // 主题不存在或检查失败时，创建新的消费者组
        if (hasKey == null || !hasKey) {
            stringRedisTemplate.opsForStream().createGroup(
                SHORT_LINK_STATS_STREAM_TOPIC_KEY,  // Stream主题key
                SHORT_LINK_STATS_STREAM_GROUP_KEY   // 消费者组名称
            );
        }
    }
}
