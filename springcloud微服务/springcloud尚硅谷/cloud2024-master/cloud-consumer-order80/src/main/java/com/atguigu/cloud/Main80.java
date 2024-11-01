package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** LoadBalancer负载均衡策略
 * 负载均衡策略：
 * 默认是轮询，可以修改
 * 
 * 在以下文件中演示：
 * cloud-consumer-order80\src\main\java\com\atguigu\cloud\config\RestTemplateConfig.java
 */

/** 使用DiscoveryClient动态获取所有上线的服务列表
 * @Autowired
 * private DiscoveryClient discoveryClient;
 * 
 * 在以下文件中演示：
 * cloud-consumer-order80\src\main\java\com\atguigu\cloud\controller\OrderController.java
 */

/** LoadBalancer跟nginx的区别
 * Nginx负载均衡（服务器负载均衡）：
 * - 客户端所有请求都会交给nginx
 * - 由nginx实现转发请求
 * - 负载均衡是由服务端实现的
 * 
 * LoadBalancer负载均衡本地负载均衡：
 * - 在调用微服务接口时，会在注册中心上获取注册信息服务列表
 * - 缓存到JVM本地
 * - 在本地实现RPC远程服务调用技术
 */

/** LoadBalancer介绍
 * Spring Cloud Ribbon是Netflix开源的客户端负载均衡工具，主要特点：
 * 1. 提供客户端负载均衡算法
 * 2. 服务调用支持
 * 3. 内置多种配置项：
 *    - 连接超时
 *    - 重试机制
 *    - 负载均衡规则(如轮询、随机等)
 * 但是Ribbon已经停止维护了，所以现在用Nacos或者Consul
 * 
 * LoadBalancer：
 * 是Spring Cloud的负载均衡接口，定义了负载均衡策略的规范
 * 在RestTemplate上加上@LoadBalanced注解，赋予RestTemplate负载均衡的能力
 * 
 * 在以下文件中演示：
 * cloud-consumer-order80\src\main\java\com\atguigu\cloud\config\RestTemplateConfig.java
 */

/** 入驻consul，以及服务发现
 * 加pom：cloud-consumer-order80\pom.xml
 * 改yml：cloud-consumer-order80\src\main\resources\application.yml
 * 启动类加上@EnableDiscoveryClient // 开启服务发现
 * RestTemplate就可以请求服务名而不是ip地址了
 */

/** @LoadBalanced
 * @Bean // 注入到spring容器中，以后用不用每次都new一个
 * @LoadBalanced //ribbon的负载均衡注解，配合nacos,或者是consul（默认负载均衡，不加就会找不着微服务名），使得请求url中可以不填ip，而是服务名，赋予RestTemplate负载均衡的能力
 * public RestTemplate restTemplate(){
 *     return new RestTemplate();
 * }
 * 
 * ribbon的底层原理：LoadBalancerlnterceptor.intercept会拦截请求，根据服务名，从nacos中获取服务列表，然后根据负载均衡算法选择一个服务
 * 找到服务列表后，会放内存
 * 
 * 在以下文件中演示：
 * cloud-consumer-order80\src\main\java\com\atguigu\cloud\config\RestTemplateConfig.java
 */

/** RestTemplate
 * 官方文档: https://docs.spring.io/spring-framework/docs/6.0.11/javadoc-api/org/springframework/web/client/RestTemplate.html
 * 依赖：spring-boot-starter自带的
 * 这是 Spring 提供的用于进行 HTTP 请求的模板类。
 * 先在配置类中注入（以后不用每次都new一个）
 * 并且加上@LoadBalanced注解，赋予RestTemplate负载均衡的能力（因为consul默认负载均衡，不加注解就会找不着服务名）
 * @Resource
 * private RestTemplate restTemplate;
 * 主要参数说明：
 * 1. getForObject
 *  发送GET请求获取对象 
 *  参数: (String url, Class<T> responseType, Object... uriVariables)
 *  返回值: T
 * 2. postForObject
 *  发送POST请求获取对象
 *  参数: (String url, Object request, Class<T> responseType, Object... uriVariables)
 *  返回值: T
 * 3. getForEntity()：
 *  发送GET请求获取响应实体
 *  参数: (String url, Class<T> responseType)
 *  返回值: ResponseEntity<T>
 *  最后.getBody()获取响应体
 * 
 * 可以请求服务名（前提是服务名在consul中注册了，并且配置了负载均衡@）
 * public static final String PaymentSrv_URL = "http://cloud-payment-service";//服务注册中心上的微服务名称
 * 
 * 用第二个多
 * 在以下文件中演示：
 * 控制反转，配置等：
 * cloud-consumer-order80\src\main\java\com\atguigu\cloud\config\RestTemplateConfig.java
 * 使用演示：
 * cloud-consumer-order80\src\main\java\com\atguigu\cloud\controller\OrderController.java
 */

/** @EnableDiscoveryClient
 * @EnableDiscoveryClient：
 * 是 Spring Cloud 提供的通用服务发现注解
 * 支持多种注册中心（Nacos、Eureka、Consul等）
 * 使服务可以被注册和发现
 * 
 * 在以下文件中演示：
 * cloud-consumer-order80\src\main\java\com\atguigu\cloud\Main80.java
 */

/**
 * @auther zzyy
 * @create 2023-12-22 22:21
 */
@SpringBootApplication
@EnableDiscoveryClient // 开启服务发现
public class Main80
{
    public static void main(String[] args)
    {
        SpringApplication.run(Main80.class,args);
    }
}