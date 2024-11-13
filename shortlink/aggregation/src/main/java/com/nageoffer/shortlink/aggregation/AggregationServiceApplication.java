package com.nageoffer.shortlink.aggregation;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 短链接聚合服务应用程序
 * 
 * 这是一个Spring Boot应用程序的主入口类，用于启动短链接聚合服务。
 * 该服务整合了admin和project两个子模块的功能，实现了统一的服务入口。
 * 
 * 关于Maven构建配置的重要说明：
 * 1. 如果需要分布式部署admin和project模块，需要在各自的pom.xml中添加spring-boot-maven-plugin构建配置
 * 2. 如果只启动聚合服务，则只需在aggregation模块的pom.xml中添加构建配置
 * 3. 错误的构建配置可能导致404错误
 */

@EnableDiscoveryClient  // 启用服务发现功能，使服务可以注册到注册中心
@SpringBootApplication(scanBasePackages = {
        "com.nageoffer.shortlink.admin",    // 扫描admin模块的组件
        "com.nageoffer.shortlink.project"   // 扫描project模块的组件
})
@MapperScan(value = {
        "com.nageoffer.shortlink.project.dao.mapper",  // 扫描project模块的MyBatis映射接口
        "com.nageoffer.shortlink.admin.dao.mapper"     // 扫描admin模块的MyBatis映射接口
})
public class AggregationServiceApplication {

    /**
     * 应用程序入口方法
     * 负责启动Spring Boot应用程序，初始化Spring容器，
     * 并加载所有配置的组件和服务
     */
    public static void main(String[] args) {
        SpringApplication.run(AggregationServiceApplication.class, args);
    }
}