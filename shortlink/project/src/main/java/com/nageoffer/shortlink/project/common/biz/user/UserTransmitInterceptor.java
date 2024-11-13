package com.nageoffer.shortlink.project.common.biz.user;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户信息传输拦截器
 * 
 * 该拦截器的主要职责：
 * 1. 从HTTP请求头中提取用户信息
 * 2. 将用户信息存储到ThreadLocal中，方便后续业务使用
 * 3. 请求完成后清理ThreadLocal，防止内存泄漏
 * 
 * 使用场景：
 * - 用于微服务间的用户信息传递
 * - 实现用户上下文的自动传播
 * - 支持分布式系统中的用户信息追踪
 */
@Component
public class UserTransmitInterceptor implements HandlerInterceptor {

    /**
     * 请求预处理方法，在Controller处理之前执行
     * 
     * 工作流程：
     * 1. 从请求头获取用户相关信息
     * 2. 当username存在时，构建UserInfoDTO对象
     * 3. 将用户信息存储到ThreadLocal中
     * 
     * @param request HTTP请求对象，包含用户信息的请求头
     * @param response HTTP响应对象
     * @param handler 处理器对象
     * @return true表示继续执行，false表示中断请求
     */
    @Override
    public boolean preHandle(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler) throws Exception {
        // 优先检查username，作为用户信息存在的标志
        String username = request.getHeader("username");
        if (StrUtil.isNotBlank(username)) {
            // 用户存在时，获取其他用户信息
            String userId = request.getHeader("userId");
            String realName = request.getHeader("realName");
            // 构建并存储用户信息
            UserInfoDTO userInfoDTO = new UserInfoDTO(userId, username, realName);
            UserContext.setUser(userInfoDTO);
        }
        return true;    // 始终返回true，表示不中断请求处理
    }

    /**
     * 请求完成后的清理工作，在视图渲染后执行
     * 
     * 注意：
     * - 该方法在finally块中被调用，确保即使发生异常也能执行
     * - 清理ThreadLocal是防止内存泄漏的关键步骤
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param handler 处理器对象
     * @param exception 处理过程中发生的异常，可能为null
     */
    @Override
    public void afterCompletion(@Nullable HttpServletRequest request, @Nullable HttpServletResponse response, @Nullable Object handler, Exception exception) throws Exception {
        // 清理当前线程的用户信息，防止内存泄漏
        UserContext.removeUser();
    }
}