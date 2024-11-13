package com.nageoffer.shortlink.admin.config;

import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenFeign 配置类
 * 
 * 该配置类用于在微服务调用时传递用户信息
 * 通过 RequestInterceptor 在每个 Feign 请求中添加用户相关的请求头
 * 确保微服务之间可以传递和获取用户上下文信息
 */
@Configuration
public class OpenFeignConfiguration {

    /**
     * 创建 Feign 请求拦截器
     * 
     * @return RequestInterceptor 请求拦截器实例
     * 
     * 拦截器功能：
     * 1. 从 UserContext 获取当前用户信息
     * 2. 将用户信息（用户名、用户ID、真实姓名）添加到请求头中
     * 3. 确保下游服务可以获取到调用方的用户信息
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            // 添加用户名到请求头
            template.header("username", UserContext.getUsername());
            // 添加用户ID到请求头
            template.header("userId", UserContext.getUserId());
            // 添加用户真实姓名到请求头
            template.header("realName", UserContext.getRealName());
        };
    }
}
