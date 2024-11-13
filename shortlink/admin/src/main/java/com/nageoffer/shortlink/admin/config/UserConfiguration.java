package com.nageoffer.shortlink.admin.config;

import com.nageoffer.shortlink.admin.common.biz.user.UserFlowRiskControlFilter;
import com.nageoffer.shortlink.admin.common.biz.user.UserTransmitFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * 用户配置自动装配类
 * 
 * 该配置类主要提供两个核心功能：
 * 1. 用户信息在请求间的传递（通过 UserTransmitFilter）
 * 2. 用户操作的流量风控（通过 UserFlowRiskControlFilter）
 * 
 * 通过 FilterRegistrationBean 注册过滤器，确保过滤器能够按照指定顺序执行，
 * 并且可以通过配置文件灵活控制风控功能的启用/禁用
 */
@Configuration
public class UserConfiguration {

    /**
     * 配置用户信息传递过滤器
     * 
     * @return FilterRegistrationBean 过滤器注册对象
     * 
     * 过滤器职责：
     * - 在请求处理链中传递用户上下文信息
     * - 确保后续处理器能够获取到当前用户信息
     * 
     * 执行顺序说明：
     * - order=0 确保该过滤器最先执行
     * - 这样可以保证后续的过滤器都能获取到用户信息
     */
    @Bean
    public FilterRegistrationBean<UserTransmitFilter> globalUserTransmitFilter() {
        FilterRegistrationBean<UserTransmitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserTransmitFilter());
        // 拦截所有请求，确保用户信息在整个系统中都可用
        registration.addUrlPatterns("/*");
        // 最高优先级执行
        registration.setOrder(0);
        return registration;
    }

    /**
     * 配置用户操作流量风控过滤器
     * 
     * @param stringRedisTemplate Redis操作模板
     * @param userFlowRiskControlConfiguration 风控配置参数
     * @return FilterRegistrationBean 过滤器注册对象
     * 
     * 特点：
     * - 条件装配：只有在配置文件中启用时才会创建该过滤器
     * - 依赖Redis：用于记录和控制用户操作频率
     * - 执行顺序：在用户信息传递过滤器之后执行（order=10）
     * 
     * 风控目的：
     * - 防止用户短时间内频繁操作
     * - 保护系统免受恶意请求的影响
     * - 确保系统资源的合理使用
     */
    @Bean
    @ConditionalOnProperty(name = "short-link.flow-limit.enable", havingValue = "true")
    public FilterRegistrationBean<UserFlowRiskControlFilter> globalUserFlowRiskControlFilter(
            StringRedisTemplate stringRedisTemplate,
            UserFlowRiskControlConfiguration userFlowRiskControlConfiguration) {
        FilterRegistrationBean<UserFlowRiskControlFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new UserFlowRiskControlFilter(stringRedisTemplate, userFlowRiskControlConfiguration));
        registration.addUrlPatterns("/*");
        // 在用户信息传递过滤器之后执行
        registration.setOrder(10);
        return registration;
    }
}