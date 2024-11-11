package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** GateWay和Sentinel集成实现服务限流
 * 
 * 1. 导入依赖 <!--spring cloud gateway--> <!--alibaba-sentinel-->
 * cloudalibaba-sentinel-gateway9528\pom.xml
 * 2. 改yml
 * cloudalibaba-sentinel-gateway9528\src\main\resources\application.yml
 * 3. 主启动类
 * cloudalibaba-sentinel-gateway9528\src\main\java\com\atguigu\cloud\Main9528.java
 * 4. 配置类  配置限流规则等等
 * cloudalibaba-sentinel-gateway9528\src\main\java\com\atguigu\cloud\config\GatewayConfiguration.java
 * 
 * 从Sentinel 1.6.0版本开始，Sentinel为Spring Cloud Gateway提供了配置项，可以提供两种资源粒度的限流：
 * route维度:
 * 即在Spring配置文件中配置的路由条目
 * 资源名为对应的routeId
 * 自定义API维度:
 * 用户可以利用Sentinel提供的API来自定义一些API分组
 */

/**
 * @auther zzyy
 * @create 2024-01-05 13:35
 */
@SpringBootApplication
@EnableDiscoveryClient
public class Main9528
{
    public static void main(String[] args)
    {
        SpringApplication.run(Main9528.class,args);
    }
}