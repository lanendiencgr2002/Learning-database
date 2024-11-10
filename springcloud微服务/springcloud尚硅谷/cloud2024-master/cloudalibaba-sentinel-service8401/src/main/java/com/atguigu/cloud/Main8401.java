package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** @SentinelResource注解
 * 
 * 
 * 
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