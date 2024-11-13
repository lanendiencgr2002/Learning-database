package com.nageoffer.shortlink.project.config;

import com.nageoffer.shortlink.project.common.biz.user.UserTransmitInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 用户信息传递配置类
 * 
 * 主要功能：
 * 1. 配置全局用户信息传递拦截器
 * 2. 实现用户上下文在整个请求链路中的传递
 * 3. 通过拦截器自动处理用户信息，避免在每个接口中手动处理
 * 
 * 注意：
 * - value 属性指定了特定的 bean 名称，避免与其他配置类冲突
 * - 使用 @RequiredArgsConstructor 自动注入拦截器实例
 */
@Configuration(value = "userConfigurationByProject")
@RequiredArgsConstructor
public class UserConfiguration implements WebMvcConfigurer {

    /**
     * 用户信息传递拦截器实例
     * 由 Spring 通过构造器注入，处理具体的用户信息传递逻辑
     */
    private final UserTransmitInterceptor userTransmitInterceptor;

    /**
     * 配置拦截器
     * 
     * 实现说明：
     * 1. 将用户信息传递拦截器添加到拦截器链中
     * 2. 通过 /** 匹配所有请求路径，确保所有接口都经过用户信息处理
     * 
     * @param registry 拦截器注册表，由 Spring MVC 自动注入
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userTransmitInterceptor)
                .addPathPatterns("/**");  // 拦截所有请求，实现全局用户信息传递
    }
}