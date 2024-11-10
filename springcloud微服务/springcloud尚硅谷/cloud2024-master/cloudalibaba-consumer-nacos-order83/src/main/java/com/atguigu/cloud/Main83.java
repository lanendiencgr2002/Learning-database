package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;



/** nacos服务消费，对应服务注册
 * 1. 导入依赖 <!--nacos-discovery--> <!--loadbalancer-->
 * cloudalibaba-consumer-nacos-order83\pom.xml
 * 2. 在application.yml配置nacos地址
 * cloudalibaba-consumer-nacos-order83\src\main\resources\application.yml
 * 3. 主启动类添加@EnableDiscoveryClient注解
 * cloudalibaba-consumer-nacos-order83\src\main\java\com\atguigu\cloud\Main83.java
 * 4. 配置restTemplate
 * cloudalibaba-consumer-nacos-order83\src\main\java\com\atguigu\cloud\config\RestTemplateConfig.java
 * 5. 编写消费代码
 * cloudalibaba-consumer-nacos-order83\src\main\java\com\atguigu\cloud\controller\OrderNacosController.java
 * 6. 启动项目 在nacos管理页面查看
 * 
 * 为什么要导入loadbalancer？
 * 因为nacos自带负载均衡，所以需要导入loadbalancer
 */

/** nacos下载安装
 * 1. 先从官网下载nacos
 *  官方网站：https://nacos.io/zh-cn/index.html
 *  快速开始指南：https://nacos.io/zh-cn/docs/v2/quickstart/quick-start.html
 *  GitHub发布页：https://github.com/alibaba/nacos/releases
 * 2. 解压安装包,直接运行bin目录下的startup.cmd
 * 3. 访问nacos管理页面
 *  http://localhost:8848/nacos
 *  命令运行成功后直接访问默认账号密码都是nacos
 */

/** Nacos是什么
 * 官网：nacos.io/zh-cn/docs/v2/quickstart/quick-start.html
 * Nacos:Dynamic Naming and Configuration Service
 * 动态命名和配置服务 Nacos就是注册中心+配置中心的组合 = spring cloud consul
 */
    
/** 引入spring cloud alibaba依赖
 * 官网：https://github.com/alibaba/spring-cloud-alibaba/blob/2022.x/README-zh.md
 * 引入2022.0.0.0-RC2版本 支持jdk17+以上
 * 
 * Spring Cloud Alibaba 致力于提供微服务开发的一站式解决方案。此项目包含开发分布式应用服务的必需组件，方便开发者通过 Spring Cloud 编程模型轻松使用这些组件来开发分布式应用服务。
 * 依托 Spring Cloud Alibaba，您只需要添加一些注解和少量配置，就可以将 Spring Cloud 应用接入阿里分布式应用解决方案，通过阿里中间件来迅速搭建分布式应用系统。
 * 此外，阿里云同时还提供了 Spring Cloud Alibaba 企业版服务解决方案，包括无侵入服务治理(全链路灰度、无损下线、高可用保护等)、企业级 Nacos 注册配置中心和企业级云原生完整等多个产品。
 * 
 * 组件：
 * Sentinel：把流量作为切入点，从流量控制、熔断降级、系统负载保护等多个维度保护服务的稳定性。
 * Nacos：一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。
 * Seata：阿里巴巴开源产品，一个易于使用的高性能微服务分布式事务解决方案。
 * 
 * 在以下文件中演示：<!--springcloud alibaba 2022.0.0.0-RC2-->  rc2表示release candidate候选版本 等没问题了就会发布正式版本
 * pom.xml
 */

/** spring cloud alibaba版本
 * | Spring Cloud 版本 | Spring Boot 版本 | 最低支持 JDK 版本 |
 * |-----------------|-----------------|----------------|
 * | 2022.x | 3.0.x | JDK 17 |
 * | 2021.x | 2.6.x | JDK 1.8 |
 * | 2020.0 | 2.4.x | JDK 1.8 |
 * | Hoxton | 2.2.x | JDK 1.8 |
 * | Greenwich | 2.1.x | JDK 1.8 |
 * | Finchley | 2.0.x | JDK 1.8 |
 * | Edgware | 1.x | JDK 1.7 | 
 */

/**
 * @auther zzyy
 * @create 2024-01-01 15:38
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients
public class Main83
{
    public static void main(String[] args)
    {
        SpringApplication.run(Main83.class,args);
    }
}