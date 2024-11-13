package com.nageoffer.shortlink.project.common.constant;

/**
 * Redis Key 常量类
 * 
 * 该类定义了系统中所有Redis键的格式规范，主要用途：
 * 1. 统一管理所有Redis键，避免键名冲突
 * 2. 通过前缀区分不同业务场景
 * 3. 提供清晰的命名约定，便于维护和调试
 * 
 * 命名规范：
 * - 使用 'short-link' 作为全局前缀
 * - 使用冒号(:)分隔不同级别的键名
 * - 使用下划线(_)连接同级别的关键词
 */
public class RedisKeyConstant {

    /**
     * 短链接跳转键
     * 格式：short-link:goto:{短链接}
     * 用途：存储短链接与原始URL的映射关系
     * 示例：short-link:goto:abc123 -> https://original-url.com
     */
    public static final String GOTO_SHORT_LINK_KEY = "short-link:goto:%s";

    /**
     * 空值短链接标记键
     * 格式：short-link:is-null:goto_{短链接}
     * 用途：缓存不存在的短链接，防止缓存穿透
     * 有效期：通常设置较短的过期时间
     */
    public static final String GOTO_IS_NULL_SHORT_LINK_KEY = "short-link:is-null:goto_%s";

    /**
     * 短链接跳转锁
     * 格式：short-link:lock:goto:{短链接}
     * 用途：防止并发访问时的缓存重建，实现缓存击穿保护
     * 特点：通常使用Redis的SETNX操作实现分布式锁
     */
    public static final String LOCK_GOTO_SHORT_LINK_KEY = "short-link:lock:goto:%s";

    /**
     * 分组ID更新锁
     * 格式：short-link:lock:update-gid:{分组ID}
     * 用途：确保分组ID更新操作的原子性
     * 场景：当需要修改短链接所属分组时使用
     */
    public static final String LOCK_GID_UPDATE_KEY = "short-link:lock:update-gid:%s";

    /**
     * 延迟队列统计键
     * 用途：记录延迟队列的处理状态和统计信息
     * 数据结构：建议使用Hash存储多个统计指标
     */
    public static final String DELAY_QUEUE_STATS_KEY = "short-link:delay-queue:stats";

    /**
     * UV统计缓存键前缀
     * 用途：统计短链接的独立访客数
     * 数据结构：通常使用HyperLogLog或Set实现
     */
    public static final String SHORT_LINK_STATS_UV_KEY = "short-link:stats:uv:";

    /**
     * UIP统计缓存键前缀
     * 用途：统计短链接的独立IP访问数
     * 数据结构：通常使用HyperLogLog或Set实现
     */
    public static final String SHORT_LINK_STATS_UIP_KEY = "short-link:stats:uip:";

    /**
     * 监控消息流Topic
     * 用途：存储短链接访问日志和监控数据
     * 数据结构：使用Redis Stream，支持消息持久化和消费组模式
     */
    public static final String SHORT_LINK_STATS_STREAM_TOPIC_KEY = "short-link:stats-stream";

    /**
     * 监控消息消费组
     * 用途：定义处理监控数据的消费组
     * 特点：支持多消费者协同工作，确保消息被正确处理
     */
    public static final String SHORT_LINK_STATS_STREAM_GROUP_KEY = "short-link:stats-stream:only-group";

    /**
     * 创建短链接锁
     * 用途：防止短链接创建时的并发冲突
     * 实现：分布式锁，确保全局唯一性
     */
    public static final String SHORT_LINK_CREATE_LOCK_KEY = "short-link:lock:create";
}
