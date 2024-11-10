package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** 服务注册进nacos
 * 1. 导入依赖 <!--nacos-discovery-->
 * cloudalibaba-provider-payment9001\pom.xml
 * 2. 在application.yml配置nacos地址
 * cloudalibaba-provider-payment9001\src\main\resources\application.yml
 * 3. 主启动类添加@EnableDiscoveryClient注解
 * cloudalibaba-provider-payment9001\src\main\java\com\atguigu\cloud\Main9001.java
 * 4. 编写服务提供代码
 * cloudalibaba-provider-payment9001\src\main\java\com\atguigu\cloud\controller\PayAlibabaController.java
 * 5. 启动项目 在nacos管理页面查看
 */

/**
 * @auther zzyy
 * @create 2024-01-01 15:28
 */
@SpringBootApplication
@EnableDiscoveryClient //自动注册到服务发现服务器，并且可以发现其他已注册的服务。
public class Main9001
{
    public static void main(String[] args)
    {
        SpringApplication.run(Main9001.class,args);
    }
}