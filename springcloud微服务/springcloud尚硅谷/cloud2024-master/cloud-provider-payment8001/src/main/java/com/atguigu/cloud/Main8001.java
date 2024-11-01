package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import tk.mybatis.spring.annotation.MapperScan;



/** consul持久化配置
 * 1. 在consul目录中创建consul_start.bat
 *  内容信息为：
 *   ```@echo 服务启动......
@echo off
@sc create Consul binpath= "D:\devSoft\consul_1.17.0_windows_386\consul.exe agent -server -ui -bind=127.0.0.1 -client=0.0.0.0 -bootstrap-expect 1 -data-dir D:\devSoft\consul_1.17.0_windows_386\mydata "
@net start Consul
@sc config Consul start= AUTO
@echo.Consul start is OK.....success
@pause```
 * 命令解释：
 *  @echo off 关闭回显 也就是在执行批处理文件时，不会显示正在执行的命令本身
 *  @sc create Consul 创建consul服务
 *  @net start Consul 启动consul服务
 *  @sc config Consul start= AUTO 设置consul服务开机自启
 *  @echo.Consul start is OK.....success 输出成功信息
 *  @pause 暂停
 * 2. 启动consul_start.bat，之后都会自动启动
 *  后续consul的配置数据会保存进mydata文件夹，重启后依然存在
 * 3. 后续如果consul进程挂了，可以手动启动consul_start.bat，启动后配置依然存在的
 */

/** consul分布式配置，bootstrap.yml
 * 为什么需要bootstrap.yml?
 *  因为bootstrap.yml是SpringCloud Config的配置文件，用于加载配置中心中的配置文件
 *  而application.yml是SpringBoot的配置文件，用于加载本地配置文件
 *  原理：
 *   Spring Cloud会创建一个"Bootstrap Context"作为Spring应用的"Application Context"的父上下文。初始化时，Bootstrap Context负责从外部源加载配置属性并解析配置。这两个上下文共享一个从外部获取的Environment。
 *   Bootstrap属性有高优先级，默认情况下，它们不会被本地配置覆盖。因此新增了一个bootstrap.yml文件，保证Bootstrap Context和Application Context配置的分离。
 * consul分布式配置：
 *  创建config文件夹，以/结尾：
 *  在localhost:8500中 key/value -> create -> key or folder输入：config/cloud-payment-service-dev/data  (因为在application.yml中指定了环境是dev)
 *  然后内容 比如:
 *  xxx: info: welcome
 *  然后正常用@Value("${xxx}")获取
 * consul分布式配置及时刷新：
 *  当配置文件发生变化时，consul会及时通知微服务，微服务会及时更新配置
 *  主启动类添加@RefreshScope注解
 *  默认是55秒刷新一次，可以修改
 * 在以下文件中演示：
 *  consul配置：
 *  cloud-provider-payment8001\src\main\resources\bootstrap.yml
 *  主启动类：
 *  cloud-provider-payment8001\src\main\java\com\atguigu\cloud\Main8001.java
 *  使用dev环境，consul中创建文件夹对应：
 *  cloud-provider-payment8001\src\main\resources\application.yml
 *  导入pom <!--SpringCloud consul config-->：
 *  cloud-provider-payment8001\pom.xml
 */

/** consul对比eureka
 * eureka是SpringCloud Netflix子模块中的一个组件，只提供服务注册与发现的功能，不支持配置管理
 * 已经被淘汰（因为不维护了，对初学者不友好有自我保护机制，不解耦单独作为一个微服务要部署，consul提供了更强大的功能，并且consul是SpringCloud官方推荐的组件）
 */

/** consul介绍：
 * consul是服务注册与发现的组件，提供服务注册，发现，配置管理（健康检查，kv存储）等功能
 * 下载：https://developer.hashicorp.com/consul/install#windows
 * 运行：consul agent -dev
 * 访问可视化页面：http://localhost:8500/
 * 快速入门文档：https://docs.spring.io/spring-cloud-consul/docs/current/reference/html/#quick-start
 */

/**
 * @auther zzyy
 * @create 2023-12-21 17:15
 */
@SpringBootApplication
@MapperScan("com.atguigu.cloud.mapper") //import tk.mybatis.spring.annotation.MapperScan;
@EnableDiscoveryClient
@RefreshScope // kv动态刷新 当consul中配置文件发生变化时，微服务会及时更新配置
public class Main8001
{
    public static void main(String[] args)
    {
        SpringApplication.run(Main8001.class,args);
    }
}