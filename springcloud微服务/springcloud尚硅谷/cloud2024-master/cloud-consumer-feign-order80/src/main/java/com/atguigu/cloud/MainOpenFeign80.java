package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/** Micrometer+ZipKin搭建链路监控案例步骤
 * 1. 导入依赖
 * 由于 Micrometer Tracing 是一个门面工具自身并没有实现完整的链路追踪系统，具体的链路追踪还需要引入第三方链路追踪系统的依赖：
 * | 序号 | 依赖名称 | 说明 |
 * |-----|----------|------|
 * | 1 | micrometer-tracing-bom | 做数据收集的 导入链路追踪版本中心，体系化说明 |
 * | 2 | micrometer-tracing |  产生traceid等 指标追踪 |
 * | 3 | micrometer-tracing-bridge-brave | 一个Micrometer模块，用于与分布式跟踪工具 Brave 集成，以收集应用程序的分布式跟踪数据。Brave是一个开源的分布式跟踪工具，它可以帮助用户在分布式系统中跟踪请求的流转，它使用一种称为"跟踪上下文"的机制，将请求的跟踪信息存储在请求的头部，然后将请求传递给下一个服务。在整个请求链中，Brave会将每个服务处理请求的时间和其他信息存储到跟踪数据中，以便用户可以了解整个请求的路径和性能。 |
 * | 4 | micrometer-observation | 一个基于度量库 Micrometer的观测模块，用于收集应用程序的度量数据。 |
 * | 5 | feign-micrometer | 一个Feign HTTP客户端的Micrometer模块，用于收集客户端请求的度量数据。 |
 * | 6 | zipkin-reporter-brave | 一个用于将 Brave 跟踪数据报告到Zipkin 跟踪系统的库。 |
 * 在父工程中定义版本：
 * <!--链路追踪-->
 * <properties>
 * <micrometer-tracing.version>1.2.0</micrometer-tracing.version>
 * <micrometer-observation.version>1.12.0</micrometer-observation.version>
 * <feign-micrometer.version>12.5</feign-micrometer.version>
 * <zipkin-reporter-brave.version>2.17.0</zipkin-reporter-brave.version>
 * </properties>
 * pom.xml
 * 在父工程中导入6个依赖pom：<!--micrometer-tracing-bom导入链路追踪版本中心  1-->
 * pom.xml
 * 在子工程导入5个依赖pom：<!--micrometer-tracing指标追踪  1-->  在调用到的微服务中也要导入 比如这里的8001
 * cloud-consumer-feign-order80\pom.xml
 * 
 * 使用：
 * 打开localhost:9411/zipkin/
 * 在导入依赖的发送请求，就能看到链路追踪信息
 */

/** zipkin概述和下载安装
 * 官网：https://zipkin.io/
 * Zipkin是一种分布式链路跟踪系统图形化的工具，Zipkin是Twitter开源的分布式跟踪系统，能够收集微服务运行过程中的实时调用链路信息，并能够
 * 将这些调用链路信息展示到Web图形化界面上供开发人员分析，开发人员能够从ZipKin中分析出调用链路中的性能瓶颈，识别出存在问题的应用程序
 * 进而定位问题和解决问题。
 * 
 * 单有Sleuth(Micrometer)行不行?
 * 可以但是全是啥traceid，spanid啥的，没有图形化界面
 * 
 * 下载安装：
 * 快速上手
 * 网址：zipkin.io/pages/quickstart.html
 * 启动后访问：http://localhost:9411/zipkin/ 
 */

/** 分布式链路追踪 Micrometer Tracing
 * 什么是分布式链路追踪？
 * 在微服务框架中，一个由客户端发起的请求在后站经过多个不同的的服务节点调用来协同产生最后的请求结果，每一个前
 * 段请求都会形成一条复杂的分布式服务调用链路，链路中的任何一环出现高延时或错误都会引起整个请求最后的失败。
 * 
 * zipkin？
 * 用于图形化展示。
 * Spring Cloud Sleuth(micrometer)提供了一套完整的分布式链路追踪 (Distributed Tracing)解决方案且兼容支持了zipkin展现
 * 
 * 是靠什么进行链路追踪绑定等？
 * 那么一条链路追踪会在每个服务调用的时候加上TraceID（链路唯一标识） 和Span ID（一次请求唯一标识）
 * 链路通过Traceld唯一标识，
 * Span标识发起的请求信息，各span通过parentid 关联起来(Span:表示调用链路来源，通俗的理解span就是一次请求信息)
 * 
 * 一些指标：
 * CS：Client Sent 客户端发送
 * SR：Server Received 服务端接收
 * SS：Server Send 服务端发送
 * CR：Client Received 客户端接收
 * SR-CS：网络传输时间
 * SS-SR：业务处理时间
 * CR-CS：远程调用时间
 * CR-SS：网络传输时间
 * 
 * 调用链路示例
 * 1. Service1接收请求：
 *    SpanID=A, ParentID=null (链路起点)
 * 2. Service1调用Service2：
 *    SpanID=B, ParentID=A
 * 3. Service2处理过程：
 *    SpanID=C, ParentID=B
 * 4. Service2调用Service3：
 *    SpanID=D, ParentID=C
 * 5. Service3处理过程：
 *    SpanID=E, ParentID=D
 * 6. Service3调用Service4：
 *    SpanID=F, ParentID=C
 * 
 * 技术实现
 * - Spring Cloud Sleuth(micrometer)：提供追踪实现
 * - Zipkin：提供图形化界面展示
 * - 共同作用：实现完整的分布式追踪解决方案
 */

/** 面试题：说常见限流算法
 * 要么用令牌桶算法，要么用滑动时间窗口
 * 1. 漏斗算法
 * 一个固定容量的漏桶，按照设定常量固定速率流出水滴，类似医院打吊针，不管你源头流量多大，我设定匀速流出。
 * 如果流入水滴超出了桶的容量，则流入的水滴将会溢出了(被丢弃)，而漏桶容量是不变的。
 *  算法特点：
 * - 有两个关键参数：桶的容量(burst)和流出速率(rate)
 * - 流入速率不固定，但流出速率恒定
 * - 当桶满时，新请求会被丢弃
 * - 起到流量整形和限流的作用
 *  缺点：
 * - 无法应对突发流量
 * - 即使系统此时有能力处理更多请求，也会被限制在固定速率
 * 2. 令牌桶算法（spring cloud默认 也是最推荐的算法）
 * 来一个请求，先从桶里拿一个令牌，如果桶里没有令牌，则拒绝请求。如果有令牌，则加入到处理队列中。
 * 3. 滚动时间窗口
 * 如果1秒内请求数超过xx，则拒绝请求。
 * 有大bug
 * 缺点：间隔临界的一段时间内的请求就会超过系统限制，可能导致系统被压垮
 * 由于计数器算法存在时间临界点缺陷，因此在时间临界点左右的极短时间段内容易遭到攻击。
 * 在时间临界点前后，请求数会突然增加，会导致double kill（双倍攻击），如何避免？滑动时间窗口
 * 4. 滑动时间窗口
 * 顾名思义，该时间窗口是滑动的。所以，从概念上讲，这里有两个方面的概念需要理解：
 * -窗口：需要定义窗口的大小
 * -滑动：需要定义在窗口中滑动的大小，但理论上讲滑动的大小不能超过窗口大小
 * 滑动窗口算法是把固定时间片进行划分并且随着时间移动，移动方式为开始时间点变为时间列表中的第2个时间点，结束时间点增加一个时间点，
 * 
 * 
 */

/** Resilience4j之限流
 * 限流就是限制最大访问流量。系统能提供的最大并发是有限的，同时来的请求又太多，就需要限流。
 * 比如商城秒杀业务，瞬时大量请求涌入，服务器忙不过就只好排队限流了，和去景点排队买票和去医院办理业务排队等号道理相同。
 * 
 * 使用：
 * 1. 导pom：<!--resilience4j-ratelimiter--> 
 * cloud-consumer-feign-order80\pom.xml
 * 2. 写yml resilience4j ratelimiter 限流的例子： 
 * cloud-consumer-feign-order80\src\main\resources\application.yml
 * 3. 使用@RateLimiter注解，指定name 
 * cloud-consumer-feign-order80\src\main\java\com\atguigu\cloud\controller\OrderCircuitController.java
 */

/** Resilience4j之舱壁隔离
 * 隔板来自造船行业，船仓内部存在多个隔舱，隔舱之间相互独立，互不干扰，防止连坐，故障蔓延
 * 依赖隔离&负载保护：用来限制对于下游服务的最大并发数量的限制
 * 
 * Resilience4j提供了如下两种隔离的实现方式，可以限制并发执行的数量：
 * 1. 实现SemaphoreBulkhead(信号量舱壁）
 *    - 当信号量有空闲时，进入系统的请求会直接获取信号量并开始业务处理
 *    - 当信号量全被占用时，接下来的请求将会进入阻塞状态
 *    - SemaphoreBulkhead提供了一个阻塞计时器，如果在阻塞计时内无法获取到信号量则系统会拒绝这些请求
 *    - 若请求在阻塞时间内获取到了信号量，则将直接获取信号量并执行相应的业务处理
 * 2. 实现ThreadPoolBulkhead(固定线程池舱壁）
 *    - 线程池隔离，使用一个固定大小的线程池来执行请求
 *    - 当线程池满时，新来的请求会被阻塞，直到线程池中有空闲线程
 *    - ThreadPoolBulkhead提供了超时机制，如果请求在等待时间内无法获取到线程池资源，则会被拒绝
 *    - 先进core线程池（maxthreadpoolsize包含它），满了进阻塞队列，阻塞队列满了进最大线程池，最大线程池也满了则触发拒绝策略
 *  1. core-thread-pool-size：
 *    核心线程池大小
 *    这是线程池中会一直存活的基本线程数量
 *    即使这些线程处于空闲状态，也不会被销毁
 *    设置为1表示线程池会维持1个核心线程
 *  2. max-thread-pool-size：
 *    最大线程池大小
 *    线程池中允许存在的最大线程数
 *    当核心线程都在忙碌时，可以创建新的线程，但总数不会超过这个值
 *    设置为1表示线程池最多只能有1个线程
 *  3. queue-capacity：
 *    任务队列容量
 *    当所有线程都在忙碌时，新来的任务会放入这个队列等待执行
 *    设置为1表示队列最多只能等待1个任务
 * 
 * 底子是juc 
 * 
 * 在以下文件中演示：
 * 引入依赖pom：<!--resilience4j-bulkhead-->：
 * cloud-consumer-feign-order80\pom.xml
 * 配置文件：resilience4j bulkhead 的例子：
 * cloud-consumer-feign-order80\src\main\resources\application.yml
 * 使用演示用@Bulkhead注解的信号量舱壁：
 * cloud-consumer-feign-order80\src\main\java\com\atguigu\cloud\controller\OrderCircuitController.java
 * 配置文件：resilience4j bulkhead -THREADPOOL的例子
 * cloud-consumer-feign-order80\src\main\resources\application.yml
 * 使用演示用@Bulkhead注解的线程池舱壁：
 * cloud-consumer-feign-order80\src\main\java\com\atguigu\cloud\controller\OrderCircuitController.java
 * 
 */

/** Resilience4j之熔断降级TIME_BASED
 * 适合慢查询之类场景
 * 1. 导pom：<!--resilience4j-circuitbreaker--> <!-- 由于断路保护等需要AOP实现，所以必须导入AOP包 --> cloud-consumer-feign-order80\pom.xml
 * 2. 写yml：TIME_BASED（基于时间的滑动窗口） cloud-consumer-feign-order80\src\main\resources\application.yml
 * 3. 使用@CircuitBreaker注解，指定name和fallbackMethod cloud-consumer-feign-order80\src\main\java\com\atguigu\cloud\controller\OrderCircuitController.java
 */

/** 默认CircuitBreaker.java配置类
 * io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
 */

/** Resilience4j之熔断降级COUNT_BASED
 * 按次数是最好的
 * 1. 导pom：<!--resilience4j-circuitbreaker--> <!-- 由于断路保护等需要AOP实现，所以必须导入AOP包 --> cloud-consumer-feign-order80\pom.xml
 * 2. 写yml：COUNT_BASED（计数器） cloud-consumer-feign-order80\src\main\resources\application.yml
 * 3. 使用@CircuitBreaker注解，指定name和fallbackMethod cloud-consumer-feign-order80\src\main\java\com\atguigu\cloud\controller\OrderCircuitController.java
 */

/** 断路器3大状态
 * CLOSED: 闭合状态，表示服务正常，可以正常接收请求
 * OPEN: 打开状态，表示服务不可用，请求会被立即拒绝
 * HALF_OPEN: 半开状态，表示服务正在尝试恢复，可能接收部分请求
 * 还有两种特殊状态：
 * DISABLED: 禁用状态，断路器不工作
 * FORCED_OPEN: 强制打开状态，忽略失败率，直接打开
 * 
 * 
 * 如果失败率超过阈值，断路器会切换到OPEN状态，拒绝请求，当过了一段时间后，断路器会切换到HALF_OPEN状态，尝试恢复
 * 如果失败率低于阈值，断路器会切换到CLOSED状态，恢复正常，失败率超过阈值，依旧会切换到OPEN状态
 * 
 * 断路器使用滑动窗口来存储和统计调用的结果，可以选择基于数量或者基于时间的串口窗口
 *  基于数量：
 *   最近n次调用
 *   基于时间最n秒错了一定次数
 *  基于时间：
 *   最近时间窗口内
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