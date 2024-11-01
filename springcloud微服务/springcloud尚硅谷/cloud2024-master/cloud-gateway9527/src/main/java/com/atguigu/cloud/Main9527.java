package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** @Validated 校验失败：
 * 会抛出MethodArgumentNotValidException异常
 */

/** 网关预言工厂
 * 开发人员可以匹配http请求的所有内容，如果请求和配置的断言规则匹配，则进行路由
 * 
 * 1. 创建一个类，继承AbstractRoutePredicateFactory<Config>
 * @compenent
 * public class xx extends AbstractRoutePredicateFactory<MyRoutePredicateFactory.Config>
 * public MyGatewayFilterFactory()
 *  {
 *      super(MyGatewayFilterFactory.Config.class);
 *  }
 * 
 * 2. 在配置文件中添加断言规则
 * 
 * 在以下文件中使用：
 * cloud-gateway9527\src\main\java\com\atguigu\cloud\mygateway\MyRoutePredicateFactory.java
 * cloud-gateway9527\src\main\resources\application.yml
 */


/**
 * @auther zzyy
 * @create 2023-12-28 22:17
 */
@SpringBootApplication
@EnableDiscoveryClient //服务注册和发现
public class Main9527
{
    public static void main(String[] args)
    {
        SpringApplication.run(Main9527.class,args);
    }
}