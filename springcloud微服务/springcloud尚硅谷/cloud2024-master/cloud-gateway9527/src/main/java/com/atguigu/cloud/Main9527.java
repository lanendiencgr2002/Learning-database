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

/** gateway高级特效
 * 
 * 
 * 
 */

/** spring cloud gateway使用
 * 1. 导入依赖
 * cloud-gateway9527\pom.xml
 * 2. 编写配置文件
 * cloud-gateway9527\src\main\resources\application.yml
 * 3. 编写启动类
 * 
 * 诉求：我们目前不想暴露8001端口
 * 希望在8001真正的支付微服务外面套一层9527网关
 * 
 * 在以下文件中演示：
 * 配置文件：
 * cloud-gateway9527\src\main\resources\application.yml
 * 调用gateway的feign接口：
 * cloud-api-commons\src\main\java\com\atguigu\cloud\apis\PayFeignApi.java
 */

/** spring cloud gateway概念，作用
 * Gateway是在Spring生态系统之上构建的API网关服务，基于Spring6，SpringBoot3和ProjectReactor等技术。它旨在为微服务架构提供
 * 一种简单有效的统一的的API路由管理方式，并为它们提供跨领域的关注点，例如：安全性、监控/度量和恢复能力。
 * Gateway本事也要注册到注册中心
 * 所有请求都要经过gateway，gateway转发请求到后端服务
 * 
 * 能干嘛？
 * 反向代理、鉴权、流量监控、熔断、负载均衡、静态响应处理
 * 
 * 三大概念：
 * 1. 路由(Route) 找对人，就是判断这个请求url能否找到对应路由（配置文件中配置了很多路由）
 *    路由是构建网关的基本模块，由ID、目标URI、一系列断言和过滤器组成。
 *    如果断言为true则匹配该路由。
 * 
 * 2. 断言(Predicate)
 *    参考的是Java8的java.util.function.Predicate
 *    开发人员可以匹配HTTP请求中的所有内容(例如请求头或请求参数)，如果请求与断言相匹配则进行路由
 * 
 * 3. 过滤器(Filter)
 *    指的是Spring框架中GatewayFilter的实例，使用过滤器
 *    可以在请求被路由前或者之后对请求进行修改。
 * 
 * Gateway工作流程：
 * 1. 客户端向Spring Cloud Gateway发出请求
 * 2. Gateway Handler Mapping中找到与请求相匹配的路由，将其发送到Gateway Web Handler
 * 3. Handler通过指定的过滤器链来将请求发送到实际的服务执行业务逻辑，然后返回
 * 
 * 过滤器的执行时机：
 * 过滤器可以在发送代理请求之前("pre")或之后("post")执行业务逻辑
 * - "pre"类型的过滤器可以做：
 *   参数校验、权限校验、流量监控、日志输出、协议转换等
 * - "post"类型的过滤器可以做：
 *   响应内容、响应头的修改，日志的输出，流量监控等
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