package com.nageoffer.shortlink.project.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据库配置类
 * 
 * 该配置类主要用于设置MyBatis-Plus的相关功能，包括：
 * 1. 分页插件的配置
 * 2. 数据库类型的指定
 * 3. 性能优化相关配置
 * 
 * 注意：这些配置会影响所有使用MyBatis-Plus的数据库操作
 */
@Configuration
public class DataBaseConfiguration {

    /**
     * 配置MyBatis-Plus分页插件
     * 
     * 功能特点：
     * 1. 支持多种数据库的分页查询
     * 2. 自动优化分页SQL
     * 3. 提供了完整的分页参数
     * 
     * 使用场景：
     * - 列表查询需要分页时
     * - 大数据量查询需要分批处理时
     * 
     * @return 配置好的MybatisPlusInterceptor实例
     */
    @Bean
    @ConditionalOnMissingBean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 创建MyBatis-Plus拦截器
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 添加分页插件，并指定数据库类型为MySQL
        // 分页插件会自动优化SQL，无需手动编写count查询
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        
        return interceptor;
    }
}
