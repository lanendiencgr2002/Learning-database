package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** @Validated 校验失败：
 * 会抛出MethodArgumentNotValidException异常
 */

/** GateWay之自研统计接口性能 
 * 在以下文件中演示：
 * cloud-gateway9527\src\main\java\com\atguigu\cloud\mygateway\MyGlobalFilter.java
 */

/** gateway filter过滤器
 * SpringMvc里面的的拦截器Intereptor，Servlet的过滤器Filter 
 * “pre"和“pos”分别会在请求被执行前调用和被执行后调用，用来修改请求和响应信息
 * 
 * 能干嘛？
 * 1. 鉴权
 * 2. 异常处理
 * 3. 记录接口调用时长统计，重点，大厂面试设计题
 * 
 * 类型：
 * 1. 全局默认过滤器Global Filters
 * gateway出厂默认已有的，直接用即可主要作用于所有的路由
 * 不需要在配置文件中配置，作用在所有路由上，实现GlobalFilter接口即可
 * 2. 单个服务过滤器Per-Route Filters
 * 也可以称为网关过滤器这种过滤器主要是作用于单一路由或者某个路由分组
 * 需要再配置文件中配置，格式：gateway: routes:
 *  - id: payment_routh
 *    uri: lb://cloud-payment-service
 *    filters: # 过滤器 意思是：在请求头中添加一个名为X-Request-red，值为blue的请求头
 *    - AddRequestHeader=X-Request-red,blue
 * 
 * 在以下文件中演示：
 * 测试请求头中是否存在X-Request-red，值为blue：也可以搜gateway/filter
 * cloud-provider-payment8001\src\main\java\com\atguigu\cloud\controller\PayGateWayController.java
 * 配置文件中配置： AddRequestHeader=X-Request-atguigu1,atguiguValue1
 * cloud-gateway9527\src\main\resources\application.yml
 * 
 * 自定义整为全局过滤器，作用于所有路由
 * spring:
 *  cloud:
 *    gateway:
 *      default-filters:
 *        - AddResponseHeader=X-Response-Default-Red, Default-Blue
 *        - PrefixPath=/httpbin
 * 3. 自定义过滤器
 *  1. 自定义全局filter
 *  GateWay之自研统计接口性能 
 * 
 *  在以下文件中演示：
 *  cloud-gateway9527\src\main\java\com\atguigu\cloud\mygateway\MyGlobalFilter.java
 *  2. 自定义条件filter
 *  自定义，单一内置过滤器GatewayFilter，主要就是实现apply方法
 *  要带上参数：
 *  - My=atguigu 对应的就是MyGatewayFilterFactory 匹配的是status=atguigu
 *  在以下文件中演示：
 *  自定义，单一内置过滤器GatewayFilter，主要就是实现apply方法
 *  cloud-gateway9527/src/main/java/com/atguigu/cloud/mygateway/MyGatewayFilterFactory.java
 *  配置文件： - My=atguigu 对应的就是MyGatewayFilterFactory 匹配的是status=atguigu
 *  cloud-gateway9527/src/main/resources/application.yml
 */

/** gateway predicate预言
 * 两种配置格式：
 * 1. 逗号分隔(用的最多)
 * predicates:
 *  - Cookie=mycookie,mycookievalue
 * 2. kv格式
 * predicates:
 *  - name: Cookie
 *    args:
 *      name: mycookie
 *      regexp: mycookievalue # cookie名字为mycookie，值为mycookievalue
 * 
 * - After：在指定时间之后
 * - Before：在指定时间之前
 * 各种等等
 * 3. 自定义
 * xxRoutePredicateFactory 这是命名规矩
 * @Component
 * public class MyRoutePredicateFactory extends AbstractRoutePredicateFactory<MyRoutePredicateFactory.Config>
 * {   // 调用父类的构造方法，传入config类的信息
 *     public MyRoutePredicateFactory(){
 *        super(MyRoutePredicateFactory.Config.class);
 *     }
 * }
 * 然后构造方法调用父类，然后重写apply方法，然后加shortcutFieldOrder方法，保证在配置文件中生效
 * 
 * 在以下文件中演示：predicates:
 * cloud-gateway9527\src\main\resources\application.yml
 * 自定义类：
 * cloud-gateway9527\src\main\java\com\atguigu\cloud\mygateway\MyRoutePredicateFactory.java
 */

/** gateway高级特效
 * 1. 按服务名动态路由 lb://cloud-payment-service
 * 在cloud-gateway9527\src\main\resources\application.yml中演示
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
 * 配置文件：# 找得到对应的路劲访问，不然不能访问 
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