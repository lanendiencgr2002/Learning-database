package com.nageoffer.shortlink.project.common.biz.user;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Optional;

/**
 * 用户上下文管理类
 * 
 * 该类用于在线程级别存储和管理用户信息，主要功能包括：
 * 1. 使用ThreadLocal存储用户信息，确保线程安全
 * 2. 提供用户信息的存取接口
 * 3. 支持在父子线程间传递用户信息
 */
public final class UserContext { // 使用final修饰，确保类不可被继承，不可修改

    /**
     * 使用阿里巴巴的TransmittableThreadLocal来存储用户信息
     * 相比普通的ThreadLocal，TTL支持父子线程间的数据传递
     * 这在使用线程池等异步场景下特别有用
     * 
     * @see <a href="https://github.com/alibaba/transmittable-thread-local">TTL详细文档</a>
     */
    private static final ThreadLocal<UserInfoDTO> USER_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 将用户信息存储到当前线程的上下文中
     * 通常在用户登录或请求开始时调用
     *
     * @param user 用户详情信息对象，包含用户ID、用户名等信息
     */
    public static void setUser(UserInfoDTO user) {
        USER_THREAD_LOCAL.set(user);
    }

    /**
     * 从当前线程上下文中获取用户ID
     * 使用Optional处理可能的空值情况，避免NPE（NullPointerException空指针异常）
     *
     * @return 当前用户ID，如果用户未登录则返回null
     */
    public static String getUserId() {
        UserInfoDTO userInfoDTO = USER_THREAD_LOCAL.get();
        return Optional.ofNullable(userInfoDTO).map(UserInfoDTO::getUserId).orElse(null);
    }

    /**
     * 从当前线程上下文中获取用户名
     * 使用Optional处理可能的空值情况，避免NPE
     *
     * @return 当前用户名，如果用户未登录则返回null
     */
    public static String getUsername() {
        UserInfoDTO userInfoDTO = USER_THREAD_LOCAL.get();
        return Optional.ofNullable(userInfoDTO).map(UserInfoDTO::getUsername).orElse(null);
    }

    /**
     * 从当前线程上下文中获取用户真实姓名
     * 使用Optional处理可能的空值情况，避免NPE
     *
     * @return 用户真实姓名，如果用户未登录则返回null
     */
    public static String getRealName() {
        UserInfoDTO userInfoDTO = USER_THREAD_LOCAL.get();
        return Optional.ofNullable(userInfoDTO).map(UserInfoDTO::getRealName).orElse(null);
    }

    /**
     * 清理当前线程中的用户信息
     * 重要：在请求结束时调用此方法，防止内存泄漏
     * 特别是在使用线程池的场景下，必须确保及时清理
     */
    public static void removeUser() {
        USER_THREAD_LOCAL.remove();
    }
}