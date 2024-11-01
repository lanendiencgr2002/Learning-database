package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


/** 断路器3大状态
 * CLOSED: 闭合状态，表示服务正常，可以正常接收请求
 * OPEN: 打开状态，表示服务不可用，请求会被立即拒绝
 * HALF_OPEN: 半开状态，表示服务正在尝试恢复，可能接收部分请求
 */

/** Resilience4J
 * 1. Resilience4J是一个轻量级容错库，专为函数式编程设计
 * 2. 提供高阶函数(装饰器)，可通过断路器、速率限制器、重试等功能增强任何方法
 * 3. 支持在任何函数式接口、lambda表达式或方法引用上堆叠多个装饰器
 * 4. 优势在于可以按需选择所需的装饰器，灵活性高
 * 5. Resilience4J 2 运行环境要求：Java 17+
 * 
 * 核心模块：
 * - resilience4j-circuitbreaker: 断路器
 * - resilience4j-ratelimiter: 速率限制
 * - resilience4j-bulkhead: 舱壁隔离
 * - resilience4j-retry: 自动重试(同步和异步) （逻辑自己写）
 * - resilience4j-timelimiter: 超时处理 （有其他框架）
 * - resilience4j-cache: 结果缓存 （用redis）
 * 
 * 同时还支持用于指标、Feign、Kotlin、Spring、Ratpack、Vertx、RxJava2等的附加模块
 */

/** CircuitBreaker断路器
 * CircuitBreaker的目的是保护分布式系统免受故障和异常，提高系统的可用性和健壮性。
 * 
 * 当服务出现故障时，CircuitBreaker会像保险丝一样自动"跳闸"（切换到OPEN状态），暂停对该服务的请求，防止连锁故障，
 * 保护整个系统的稳定运行。
 * 
 * CircuitBreaker只是一套规范和接口，落落地实现者是Resilience4J
 */

/** 服务预热
 * 在启动时或低流量时，提前对系统进行压力测试（先放少量请求进来），确保系统能够承受预期的流量
 */

/** 服务限时
 * 在规定时间范围内，只允许指定数量的请求通过
 */

/** 服务限流
 * 秒杀高并发等操作，严禁一窝蜂的过来拥挤，大家排队，一秒钟N个，有序进行
 */

/** 服务降级
 * 服务器忙，请稍后再试，
 * 不让客户端等待并立刻返回一个友好提示，fallback
 */

/** 服务熔断
 * 类比保险丝，保险丝闭合状态（CLOSE)可以正常使用，当达到最大服务访问后，直接拒绝访问跳闸限电(OPEN)，此刻调用方会接受服务
 * 降级的处理并返回友好兜底提示
 * 
 * 就是家里保险丝，从闭合CLOSE供电状态→跳闸OPEN打开状态
 */

/** 服务雪崩
 * 多个微服务之间调用的时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其它的微服务，这就是所谓的"扇出
 * 如果扇出的链路上某个微服务的调用响应时间过长或者不可用，对微服务A的调用就会占用越来越多的系统资源，进而引起系统
 * 崩溃，所谓的"雪崩效应"
 * 
 * 通常当你发现一个模块下的某个实例失败后，这时候这个模块依然还会接收流量，然后这个有问题的模块还调用了其他的模块，这
 * 样就会发生级联故障，或者叫雪崩。
 * 
 * 解决：
 * -有问题的节点，快速熔断（快速返回失败处理或者返回默认兜底数据【服务降级】）。
 * -服务降级：断路器会返回一个符合预期的、可处理的备选响应（fallback），这样就保证了服务调用方的线程不会被长时间占用，
 * 从而避免了故障蔓延的风险。
 */

/** OpenFeign之feign日志打印
 * OpenFeign提供了日志打印功能，可以通过配置来调整日志级别，观察feign接口的调用情况
 * 
 * 日志级别说明：
 * 1. NONE：默认级别，不显示任何日志
 * 2. BASIC：仅记录请求方法、URL、响应状态码及执行时间
 * 3. HEADERS：除了BASIC中定义的信息之外，还有请求和响应的头信息
 * 4. FULL：除了HEADERS中定义的信息之外，还有请求和响应的正文及元数据
 * 
 * 使用步骤：
 * 1. 配置日志bean：
 *    @Configuration
 *    public class FeignConfig {
 *        @Bean
 *        Logger.Level feignLoggerLevel() {
 *            return Logger.Level.FULL;
 *        }
 *    }
 * 
 * 公式(三段)：logging.level+含有@FeignClient注解的完整带包名的接口名+debug
 * 2. 在application.yml中开启对应Feign客户端的日志：
 *    logging:
 *      level:
 *        com.atguigu.cloud.apis.PayFeignApi: debug
 * 
 * 在以下文件中演示：
 * # feign日志以什么级别监控哪个接口
 * cloud-consumer-feign-order80\src\main\resources\application.yml
 */

/** OpenFeign之请求回应压缩
 * 节约带宽，提高传输效率，节省性能
 * Spring Cloud OpenFeign支持对请求和响应进行GZIP压缩，以减少通信过程中的性能损耗
 * 配置方法:
 * 1. 在application.yml中添加以下配置开启压缩功能：
 *    feign:
 *      compression:
 *        request:
 *          enabled: true  # 开启请求压缩
 *        response:
 *          enabled: true  # 开启响应压缩
 * 
 * 2. 细粒度配置：
 *    feign:
 *      compression:
 *        request:
 *          enabled: true
 *          mime-types: text/xml,application/xml,application/json  # 配置压缩的数据类型
 *          min-request-size: 2048  # 设置压缩的大小下限，只有超过2048字节的请求才会被压缩
 * 
 * 在以下文件中演示：
 * compression: # 配置压缩
 * cloud-consumer-feign-order80\src\main\resources\application.yml
 */

/** OpenFeign之性能优化HttpClient5
 * 如果不做特殊配置，OpenFeign默认使用JDK自带的HttpURLConnection发送HTTP请求，
 * 由于默认HttpURLConnection没有连接池、性能和效率比较低，如果采用默认，性能上不是最牛B的，所以加到最大。
 * 官方介意使用HttpClient5，所以使用HttpClient5
 * 1. 引入两个pom：<!-- httpclient5--> <!-- feign-hc5--> cloud-consumer-feign-order80\pom.xml
 * 2. yml文件中开启配置：feign: httpclient: enabled: true cloud-consumer-feign-order80\src\main\resources\application.yml
 * 
 */

/** OpenFeign之重试机制
 * 默认是关闭的，也就是不会调用失败啥的不会重试
 * 新建一个配置类，自己定义这个的控制反转public Retryer myRetryer()return xxx
 * 
 * 在以下文件中演示：
 * 开启重试机制：
 * cloud-consumer-feign-order80\src\main\java\com\atguigu\cloud\config\FeignConfig.java
 */

/** OpenFeign之超时控制
 * OpenFeign默认等待60秒钟，超过后报错
 * yml文件中开启配置：
 *  connectTimeout连接超时时间
 *  readTimeout请求处理超时时间
 * 在以下文件中演示：
 * cloud-payment-service: #单个可以覆盖默认配置 这里是个服务名:
 * cloud-consumer-feign-order80\src\main\resources\application.yml
 */

/** 使用OpenFeign的步骤
 * 1. 引入依赖：<!--openfeign--> cloud-consumer-feign-order80\pom.xml
 * 2. 在主启动类上添加@EnableFeignClients注解 开启OpenFeign cloud-consumer-feign-order80\src\main\java\com\atguigu\cloud\MainOpenFeign80.java
 * 3. 修改cloud-api-commons通用模块：因为通过feign对其他服务提供接口调用
 *  在cloud-api-commons中也要引入依赖
 *  新建服务接口PayFeignApi，头上配置@FeignClient注解
 * 4. 在controller中使用：
 *  @Resource
 *  private PayFeignApi payFeignApi;
 *  @PostMapping(value = "/feign/pay/add")
 *  public ResultData addOrder(@RequestBody PayDTO payDTO)
 *  { // 已经在api通用模块中定义了addPay方法
 *      ResultData resultData = payFeignApi.addPay(payDTO);
 *      return resultData;
 *  }
 * 在以下文件中演示：
 * api通用模块编写feign接口
 * cloud-api-commons\src\main\java\com\atguigu\cloud\apis\PayFeignApi.java
 * 使用演示：
 * cloud-consumer-feign-order80\src\main\java\com\atguigu\cloud\controller\OrderController.java
 */

/** OpenFeign能干什么
 * RestTemplate更啰嗦（每个微服务都要写restTemplate.get/postxxx，不统一不规范不方便），要调用很多代码，
 * OpenFeign更简单，只需要定义接口，然后使用注解@FeignClient(value = "CLOUD-PAYMENT-SERVICE")
 * 
 * OpenFeign同时还集成SpringCloud LoadBalancer
 * 可以在使用OpenFeign时提供Http客户端的负载均衡，也可以集成阿里巴巴Sentinel来提供熔断、降级等功能。
 * 而与SpringCloud LoadBalancer不同的是，通过OpenFeign只需要定义服务绑定接口且以声明式的方法，优雅而简单的实现了服务调用。
 */

/**
 * @auther zzyy
 * @create 2023-12-25 11:22
 */
@SpringBootApplication
@EnableDiscoveryClient //该注解用于向使用consul为注册中心时注册服务
@EnableFeignClients//启用feign客户端,定义服务+绑定接口，以声明式的方法优雅而简单的实现服务调用
public class MainOpenFeign80
{
    public static void main(String[] args)
    {
        SpringApplication.run(MainOpenFeign80.class,args);
    }
}