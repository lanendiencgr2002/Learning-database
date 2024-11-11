package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** Sentinel整合openfeign 实现fallback服务降级
 * 83通过openfeign调用9001时，异常访问，访问者要有fallback服务降级处理，但是通过feign方法调用的各有不同，每个都有独自的fallback就会乱
 * 
 * 1. 修改通过Sentinel提供服务couldalibaba-provider-payment9001
 *  1. 改pom <!--openfeign-->   <!--alibaba-sentinel-->
 *   cloudalibaba-provider-payment9001\pom.xml
 *  2. 写yml sentinel
 *  cloudalibaba-provider-payment9001\src\main\resources\application.yml
 *  3. 主启动类 添加@EnableFeignClients注解
 *  cloudalibaba-provider-payment9001\src\main\java\com\atguigu\cloud\Main9001.java
 *  4. 业务类  openfeign+sentinel进行服务降级和流量监控的整合处理case
 *  cloudalibaba-provider-payment9001\src\main\java\com\atguigu\cloud\controller\PayAlibabaController.java
 * 2. 修改cloud-api-commons  定义feign接口
 *  1. 改pom <!--openfeign--> <!--alibaba-sentinel--> 
 *  cloud-api-commons\pom.xml
 *  2. 新建接口类  PayFeignSentinelApi
 *  cloud-api-commons\src\main\java\com\atguigu\cloud\apis\PayFeignSentinelApi.java 
 *  3. 统一处理异常类 PayFeignSentinelApiFallBack
 *  cloud-api-commons\src\main\java\com\atguigu\cloud\apis\PayFeignSentinelApiFallBack.java
 * 3. 修改couldalibaba-consumer-nacos-order83  通过openfeign调用9001
 *  1. 改pom  <!-- 引入自己定义的api通用包 --> <!--openfeign--> <!--alibaba-sentinel-->
 *  cloudalibaba-consumer-nacos-order83\pom.xml
 *  2. 改yml  激活Sentinel对Feign的支持
 *  cloudalibaba-consumer-nacos-order83\src\main\resources\application.yml
 *  3. 启动类开启openfeign注解 @EnableFeignClients
 *  cloudalibaba-consumer-nacos-order83\src\main\java\com\atguigu\cloud\Main83.java
 *  4. 业务类  OrderNacosController
 *  cloudalibaba-consumer-nacos-order83\src\main\java\com\atguigu\cloud\controller\OrderNacosController.java
 * 
 * 4. 试着运行下 如果报错，springboot+springcloud版本太高导致和阿里巴巴Sentinel不兼容 要么降低springboot和springcloud版本
 * 要么升级Sentinel版本（如果能解决）
 * 
 * 5. 在Sentinel控制台配置限流规则 给服务提供方couldalibaba-provider-payment9001
 * 
 * 在cloudalibaba-sentinel-service8401\src\main\java\com\atguigu\cloud\service\PaymentService.java
 * 添加openfeign注解
 */

/** Sentinel规则持久化
 * 微服务重启后，配置好的就会消失
 * 
 * 怎么解决？  （nacos自带持久化到本地数据库derby，微服务可能经常重启，所以需要持久化到nacos）
 * 将限流配置规则持久化进Nacos保存，只要刷新8401某个rest地址，
 * sentinel控制台的流控规则就能看到，只要Nacos里面的配置不删除， （nacos自带持久化到本地数据库derby）
 * 针对8401上sentinel上的流控规则持续有效
 * 
 * 1. 导入依赖  <!--SpringCloud ailibaba sentinel-datasource-nacos -->
 * cloudalibaba-sentinel-service8401\pom.xml
 * 2. 配置文件application.yml    datasource
 * cloudalibaba-sentinel-service8401\src\main\resources\application.yml
 * 
 * 配置文件中的 rule-type：
 * 在RuleType类中： 有flow、authority、degrade、system、paramFlow
 * flow：流量控制规则
 * authority：授权规则
 * degrade：熔断降级规则
 * system：系统保护规则
 * paramFlow：热点规则
 * 3. 添加nacos业务规则
 * dataid：填{cloudalibaba-sentinel-service}  在配置文件application.yml中
 * group：填DEFAULT_GROUP  在配置文件application.yml的ds1 groupId中
 * 选json
 * 内容为
{
    "resource": "/rateLimit/byUrl",
    "limitApp": "default",
    "grade": 1,
    "count": 1,
    "strategy": 0,
    "controlBehavior": 0,
    "clusterMode": false
}   
 * resource: 资源名称
 * limitApp: 来源应用
 * grade: 阈值类型，0表示线程数，1表示QPS
 * count: 单机阈值
 * strategy: 流控模式，0表示直接，1表示关联，2表示链路
 * controlBehavior: 流控效果，0表示快速失败，1表示预热，2表示排队等待
 * clusterMode: 是否集群
 * 
 * 4. 重启微服务一看还是没，再请求下接口，会发现还在
 */

/** Sentinel授权规则
 * 有授权，可以访问，没有授权，不能访问  也就是黑白名单
 * 
 * 1. 测试接口
 * cloudalibaba-sentinel-service8401\src\main\java\com\atguigu\cloud\controller\EmpowerController.java
 * 2. 授权规则类 实现接口重写方法 设置对应要检查的参数 如果这个参数是test1，test2就不能访问 
 * cloudalibaba-sentinel-service8401\src\main\java\com\atguigu\cloud\handler\MyRequestOriginParser.java
 * 3. sentinel面板上配置
 * 资源名称：empower
 * 流控应用：test1，test2
 * 授权模式：黑名单
 */

/** Sentinel热点规则
 * - 热点参数限流：
 * 普通正常限流：含有第一个参数，超过1秒钟一个后，达到阈值1后马上被限流
 * 1. 编写方法等，找到对应的资源名称，配置热点参数限流
 * cloudalibaba-sentinel-service8401\src\main\java\com\atguigu\cloud\controller\RateLimitController.java
 * 2. 在sentinel控制台配置限流规则876754345678965435678976544567890-76544567890-
 * 新增热点规则：
 *  参数索引：0 表示监控第一个参数  （只要含有第一个参数 访问xx次 就会触发限流）
 * 
 * - 参数例外项：
 * 参数等于某个值触发限流
 * 假如当第一个参数为5时它的阈值可以达到200或其它值
 * 1. 在sentinel控制台配置限流规则
 * 参数类型：String等等
 * 参数值：5
 * 限流阈值：200
 */

/** @SentinelResource注解
 * SentinelResource是一个流量防卫防护组件注解用于指定防护资源，对配置的资源进行流量控制、熔断降级等功能。
 * 主要参数：
 * - value：指定要保护的资源名称，必需项（不能为空）
 * - blockHandler：指定限流降级时调用的方法
 * - fallback：指定业务异常时调用的方法
 *  返回类型与原方法一致
 *  参数类型需要和原方法匹配
 *  默认需要和原方法在同一个类中
 * - exceptionsToIgnore：指定哪些异常被忽略，不会计入异常统计
 *  不指定时，所有异常都会被统计
 * 
 * 在以下文件中演示：
 * cloudalibaba-sentinel-service8401\src\main\java\com\atguigu\cloud\controller\RateLimitController.java
 */

/** Sentinel熔断规则：慢调用比例、异常比例、异常数
 * 1. 慢调用比例（SLOW_REQUEST_RATIO）
 * ---------------------------------------------------------------------------------------------------------------------------------------
 * - 定义：以慢调用比例作为阈值
 * - 配置项：
 *   · 慢调用RT：允许的最大响应时间ms，超过算慢调用
 *   · 最小请求数：启动熔断的最小请求数
 *   · 比例阈值：慢调用占总请求数的比例阈值
 *   · 统计时长：单位统计时间（statIntervalMs）
 *   · 熔断时长：触发熔断后持续的时间
 * - 工作流程：
 *   · 熔断触发：统计时长内请求数>最小请求数 且 慢调用比例>阈值
 *   · 熔断状态：请求被自动熔断（类比保险丝断电）
 *   · 探测恢复：熔断时长后进入探测恢复状态
 *   · 恢复正常：新请求RT<设定值时结束熔断
 * - 演示接口：/testF
 * 2. 异常比例（ERROR_RATIO）
 * ---------------------------------------------------------------------------------------------------------------------------------------------------------------
 * - 定义：以异常比例作为阈值
 * - 配置项：
 *   · 最小请求数：启动熔断的最小请求数
 *   · 比例阈值：异常占总请求数的比例[0.0-1.0]
 *   · 统计时长：单位统计时间（statIntervalMs）
 *   · 熔断时长：触发熔断后持续的时间
 * - 工作流程：
 *   · 熔断触发：统计时长内请求数>最小请求数且异常比例>阈值
 *   · 熔断状态：请求被自动熔断
 *   · 探测恢复：熔断时长后进入探测恢复状态
 *   · 恢复正常：新请求成功完成时结束熔断
 * - 演示接口：/testG
 * 3. 异常数（ERROR_COUNT）
 * -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * - 定义：以异常数作为阈值
 * - 配置项：
 *   · 异常数阈值：允许的最大异常数
 *   · 统计时长：单位统计时间（statIntervalMs）
 *   · 熔断时长：触发熔断后持续的时间
 * - 工作流程：
 *   · 熔断触发：统计时长内异常数>阈值
 *   · 熔断状态：请求被自动熔断
 *   · 探测恢复：熔断时长后进入探测恢复状态
 *   · 恢复正常：新请求成功完成时结束熔断
 * - 演示接口：/testH
 * 
 * 在以下文件中演示：
 * cloudalibaba-sentinel-service8401\src\main\java\com\atguigu\cloud\controller\FlowLimitController.java
 */

/** Sentinel流控效果 预热(WarmUp)模式，排队等待模式，并发线程数控制
 * 1. 预热(WarmUp)模式
 * - 目的：系统从空闲到繁忙状态的缓慢切换
 * - 应用场景：需要预热的场景，如建立数据库连接
 * - 计算公式：阈值 / coldFactor(默认为3)
 * - 示例：
 *   · 设置：单机阈值=10，预热时长=5秒
 *   · 初始QPS：10/3≈3
 *   · 5秒后：逐渐恢复到10
 * - 实际应用：如秒杀系统启动时的流量控制
 * 
 * 2. 排队等待模式
 * - 目的：处理突发流量，实现匀速处理
 * - 应用场景：间隔性突发流量，如消息队列
 * - 特点：
 *   · 匀速排队处理请求
 *   · 暂不支持QPS>1000的场景
 * - 示例：QPS=2时，每500ms处理一个请求
 * 
 * 3. 并发线程数控制
 * - 定义：控制指定资源的并发线程数量
 * - 特点：
 *   · 精确度不如QPS控制
 *   · 适合服务端响应时间波动较大的场景
 *   · 默认流控效果是快速失败
 * - 应用场景：
 *   · 用于保护服务提供方的线程资源
 *   · 防止服务因线程耗尽而宕机
 *   · 适合涉及第三方慢调用的场景
 * - 工作原理：
 *   · 当前线程数 > 阈值时，新请求将被拒绝
 *   · 统计的线程数包括正在执行的和等待执行的线程
 * - 配置建议：
 *   · 阈值设置建议为服务器核心数 * 2
 *   · 考虑服务的平均响应时间来调整
 *   · 建议与超时时间配合使用
 * - 注意事项：
 *   · 超过阈值的请求会直接被拒绝
 *   · 不支持预热和排队等待模式
 *   · 需要合理设置阈值，避免资源浪费
 */

/** sentinel流控模式 直接模式,关联模式,链路模式
 * Sentinel能够对流量进行控制，主要是监控应用的QPS流量或者并发线程数等指标，如果达到指定的阈值时，就会被流量进行控制，以避免服务被
 * 瞬时的高并发流量击垮，保证服务的高可靠性。
 * 
 * 1. 直接模式：
 * 默认的流控模式，当接口达到限流条件时，直接开启限流功能。 报错页面默认是blocked by sentinel（sentinel自己报错）
 * 
 * 2. 关联模式：
 * 当关联的资源达到限流条件时，就限流自己。
 * 当与A关联的资源B达到限流条件时，就限流A。
 * 关联资源填 /api 某个接口，某个接口挂了被限流了 当前的节点就会失效
 * 
 * 3. 链路模式：
 * 来自不同链路的请求对同一个目标访问时，实施针对性的不同限流措施，比如C请求来访问就限流，D请求来访问就是OK
 *  1. 给方法添加注解@SentinelResource(value="resourceName")  
 *  @SentinelResource(value="common") 这里resourceName填的是common
 *  cloudalibaba-sentinel-service8401\src\main\java\com\atguigu\cloud\service\FlowLimitService.java
 *  
 *  2. /testC使用这个加了注解的方法
 *  cloudalibaba-sentinel-service8401\src\main\java\com\atguigu\cloud\controller\FlowLimitController.java
 *  
 *  3. 配置yaml配置文件  web-context-unify: false
 *  cloudalibaba-sentinel-service8401\src\main\resources\application.yml
 *  
 *  4. 配置sentinel 在localhost:8080在添加流控规则    资源名称common 方法名
 *  流控模式选链路，入口资源填/testC  表示/testC访问common方法会被限流
 *  /testD也调用common方法，但是没有限流，此时/testC会限流
 * 
 */

/** 微服务8401整合sentinel
 * 1. 启动nacos8848
 * startup.cmd -m standalone
 * 访问：http://localhost:8848/nacos/#/login 默认账号密码nacos/nacos
 * 
 * 2. 启动sentinel8080
 * java -jar sentinel-dashboard-1.8.6.jar
 * 
 * 3. 新建微服务cloudalibaba-sentinel-service8401
 *  1. 导入依赖pom.xml
 *  <!--SpringCloud alibaba sentinel -->   <!--nacos-discovery--> 当前服务注册到nacos （可以）不用这个】
 *  cloudalibaba-sentinel-service8401\pom.xml
 *  2. 修改application.yml
 *  cloudalibaba-sentinel-service8401\src\main\resources\application.yml
 *  3. 主启动类
 *  cloudalibaba-sentinel-service8401\src\main\java\com\atguigu\cloud\Main8401.java
 *  4. 业务类 testA testB
 *  cloudalibaba-sentinel-service8401\src\main\java\com\atguigu\cloud\controller\FlowLimitController.java
 * 
 * 4. sentinel懒加载（不访问不加载，访问后加载），测试
 * 想使用Sentinel对某个接口进行限流和降级等操作，一定要先访问下接口，使Sentinel检测出相应的接口
 * 
 * 5. 根据sentinel的规则，添加限流规则
 * 在sentinel控制台（http://localhost:8080/），添加限流规则
 */

/** Sentinel之分布式常见面试题
 * 讲讲什么是缓存穿透？击穿？雪崩？如何解决？
 * 
 * 服务雪崩：多个微服务之间调用的时候，假设微服务A调用微服务B和微服务C，微服务B和微服务C又调用其它的微服务，
 * 这就是所谓的"扇出"。如果扇出的链路上某个微服务的调用响应时间过长或者不可用，对微服务A的调用就会占用越来越多的系统资源，
 * 进而引起系统崩溃，所谓的"雪崩效应"。
 * 
 * 服务降级：就是一种服务托底方案，如果服务无法完成正常的调用流程，就使用默认的托底方案来返回数据。
 * 服务熔断：当某个服务出现问题，通过断路器的故障监控，暂时切断服务调用，快速返回错误的响应信息
 * 服务限流：限制并发的请求访问量，超过阈值则拒绝访问
 * 服务隔离：将系统按照一定的原则划分为若干个服务模块，各个模块之间相对独立
 * 服务超时：设置服务超时时间，超时的服务调用将被终止
 */

/** sentinel介绍和下载使用
 * 官网：https://sentinelguard.io/zh-cn/
 * 从流量路由、流量控制、流量整形、熔断降级、系统自适应过载保护、热点流量防护等多个维度来帮助开发者保障微服务的稳定性
 * 
 * 安装使用
 * 1. 下载jar包
 * 面板：下载地址：https://github.com/alibaba/Sentinel/releases
 * 2. 启动jar包 (8080端口不能被占用)
 * java -jar sentinel-dashboard-1.8.6.jar
 * 3. 访问面板
 * http://localhost:8080/
 * 
 * Sentinel规则（Rule）的三大组成部分：
 * 1. Target（目标对象）：
 *    - 资源名称(resourceName)
 *    - 按HTTP请求路径、请求头划分
 *    - 按流量方向(IN/OUT)划分
 * 2. Strategy（策略）：
 *    - 流量控制
 *    - 流量守门
 *    - 并发请求控制
 *    - 熔断降级
 *    - 系统自适应保护
 * 3. FallbackAction（降级处理）：
 *    - HTTP请求返回特定状态码
 *    - 返回预设的header/body信息
 *    - RPC调用返回预定义的降级值
 *    - 流量调度等其他行为
 */

/** sentinel
 * 底层会在代码中加上try catch，如果抛出异常，会进入降级方法
 * 就在@feignclient的fallback属性中指定降级方法
 */

/**
 * @auther zzyy
 * @create 2024-01-02 12:22
 */
@EnableDiscoveryClient
@SpringBootApplication
public class Main8401
{
    public static void main(String[] args)
    {
        SpringApplication.run(Main8401.class,args);
    }
}