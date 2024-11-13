

package com.nageoffer.shortlink.admin.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据库持久层配置类
 * 
 * 设计目的：
 * 1. 配置MyBatis-Plus的核心功能
 * 2. 提供分页插件支持
 * 3. 确保在Spring容器中只有一个分页拦截器实例
 * 
 * 配置特点：
 * 1. 使用条件注解确保单例
 * 2. 支持MySQL数据库的分页查询优化
 * 3. 可通过配置扩展更多功能插件
 */
@Configuration(value = "dataBaseConfigurationByAdmin")
public class DataBaseConfiguration {

    /**
     * 配置MyBatis-Plus分页插件
     * 
     * 功能说明：
     * 1. 自动优化MySQL分页查询
     * 2. 支持count查询优化
     * 3. 可以防止内存溢出（通过分页限制）
     * 
     * @return 配置好的MyBatisPlusInterceptor实例
     * 
     * 注意事项：
     * 1. 使用@ConditionalOnMissingBean确保容器中只有一个实例
     * 2. 特定于MySQL数据库的配置，如需支持其他数据库需要修改
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptorByAdmin() {
        // 创建MyBatis-Plus拦截器
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件，并指定数据库类型为MySQL
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
