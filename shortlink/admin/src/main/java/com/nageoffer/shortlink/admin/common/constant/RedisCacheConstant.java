

package com.nageoffer.shortlink.admin.common.constant;

/**
 * 短链接后台管理系统的Redis缓存键常量类
 * 
 * 该类定义了系统中所有Redis缓存键的格式规范，主要用于：
 * 1. 统一管理所有缓存键，避免键名冲突
 * 2. 提供清晰的命名规范，便于理解每个键的用途
 * 3. 集中维护缓存键，方便后期修改和管理
 * 
 * 命名规范：
 * - 所有键都以"short-link:"作为前缀
 * - 使用下划线分隔不同的逻辑部分
 * - 对于需要动态参数的键，使用%s作为占位符
 */
public class RedisCacheConstant {

    /**
     * 用户注册分布式锁的键前缀
     * 
     * 使用场景：
     * - 在用户注册过程中防止用户名重复注册
     * - 确保并发情况下的注册流程安全性
     * 
     * 完整键格式: short-link:lock_user-register:{username}
     */
    public static final String LOCK_USER_REGISTER_KEY = "short-link:lock_user-register:";

    /**
     * 分组创建分布式锁的键格式
     * 
     * 使用场景：
     * - 防止同一用户重复创建相同名称的分组
     * - 需要在使用时通过String.format()方法传入用户标识
     * 
     * 完整键格式: short-link:lock_group-create:{userId}_{groupName}
     */
    public static final String LOCK_GROUP_CREATE_KEY = "short-link:lock_group-create:%s";

    /**
     * 用户登录状态缓存键前缀
     * 
     * 使用场景：
     * - 存储用户的登录令牌或会话信息
     * - 用于实现用户登录状态的验证和维护
     * 
     * 完整键格式: short-link:login:{userId}
     */
    public static final String USER_LOGIN_KEY = "short-link:login:";
}
