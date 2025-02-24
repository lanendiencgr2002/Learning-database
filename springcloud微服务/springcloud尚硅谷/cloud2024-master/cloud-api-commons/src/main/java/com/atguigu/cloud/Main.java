package com.atguigu.cloud;

import java.time.ZonedDateTime;

/** DiscoveryClient
 * @Resource
 * private DiscoveryClient discoveryClient;
 * 这是 Spring Cloud 提供的用于服务发现的客户端接口。
 * 主要参数说明：
 * 1. getServices()：获取所有服务名称
 * 2. getInstances(String serviceId)：获取指定服务名称的所有实例
 */

/** @LoadBalancerClient
 * 这是 Spring Cloud LoadBalancer 的核心注解，用于声明负载均衡客户端。
 * 主要参数说明：
 * 1. value：指定要调用的服务名称
 * 2. configuration：负载均衡配置类
 */

/** @FeignClient
 * <!--openfeign--><dependency><groupId>org.springframework.cloud</groupId><artifactId>spring-cloud-starter-openfeign</artifactId></dependency>
 * 这是 Spring Cloud OpenFeign 的核心注解，用于声明远程服务调用的客户端。
 * 主要参数说明：
 * 1. value/name：指定要调用的服务名称
 * 2. url：直接指定服务地址（可选）  必须要和指定的一样
 * 3. fallback：服务降级实现类 一个类，实现接口，重写接口方法就可以了
 * 4. configuration：Feign配置类
 * 
 * 服务注册：seata-account-service 服务注册到注册中心
 * 服务发现：Feign通过服务名找到对应服务
 * 动态代理：Spring 会为接口创建代理对象
 * 远程调用：转换为 HTTP 请求
 * 
 * 在启动类加上@EnableFeignClients注解
 * 在接口上使用@FeignClient注解，在接口中使用@GetMapping注解来指定远程调用的服务名路劲
 * 
 * 底层就是个动态代理：代理将所有需求拼装好，最后发起http请求
 * 
 * 在以下文件中使用：
 * cloud-api-commons\src\main\java\com\atguigu\cloud\apis\AccountFeignApi.java
 * cloud-api-commons\src\main\java\com\atguigu\cloud\apis\*
 */

/** @Builder
 * 构建后不能修改属性
 * 使用 @Accessors 的场景：
 * 1. 需要频繁修改对象属性
 * 2. 追求简单的链式调用
 * 3. 对象状态可变的场景
 * 
 * @Data
 * @Builder
 * public class User {
 *     private String name;
 *     private Integer age;
 * }
 * 
 * // 使用方式
 * User user = User.builder()
 *     .name("张三")
 *     .age(18)
 *     .build();
 */

/** @Accessors(chain = true)
 * 构建后可以修改属性 调用set依然可以
 * 使用 @Builder 的场景：
 * 1. 创建复杂对象
 * 2. 需要不可变对象
 * 3. 需要设置默认值
 * 4. 构建过程需要严格控制
 * 
 * @Data
 * @Accessors(chain = true)
 * public class User {
 *     private String name;
 *     private Integer age;
 * }
 * // 使用方式
 * User user = new User()
 *     .setName("张三")
 *     .setAge(18);
 * 
 */

/** Serializable接口
 * 支持fastjson，将类序列化成json字符串等
 * 
 * 一个对象序列化的接口，一个类只有实现了Serializable接口，它的对象才能被序列化成字节流，
 * 或者从字节流中反序列化成对象。
 * 是一个标记接口，没有方法，实现该接口的类可以被序列化。
 * 可以确保只有可序列化的对象才能被传输和存储。
 * 
 * 什么是序列化？
 * 序列化是将对象状态转换为可保持或传输的格式的过程。与序列化相对的是反序列化，它将流转换为对象。这两个过程结合起来，可以轻松地存储和传输数据。
 * 
 * 为什么要序列化对象？
 * 把对象转换为字节序列的过程称为对象的序列化
 * 把字节序列恢复为对象的过程称为对象的反序列化
 */

/**
 * @auther zzyy
 * @create 2023-12-22 23:08
 */
public class Main
{
    public static void main(String[] args)
    {
        ZonedDateTime zbj = ZonedDateTime.now(); // 默认时区
        System.out.println(zbj);
    }
}
