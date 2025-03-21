package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** @Validated 校验失败：
 * ❗ 会抛出MethodArgumentNotValidException异常
 * 
 * == 工作原理 ==
 * 当使用@Validated注解进行参数校验时，若校验不通过：
 * - Spring框架会自动抛出MethodArgumentNotValidException异常
 * - 可通过全局异常处理器(@ControllerAdvice)捕获并处理
 * 
 * 💡 关联：通常与@Valid配合使用，@Validated是Spring的注解，@Valid是JSR-303规范
 */

/** GateWay之自研统计接口性能 → 
 * 
 * == 功能说明 ==
 * ⭐ 通过自定义全局过滤器实现接口调用时间统计
 * ❗ 重要应用：性能监控、接口优化的数据支持
 * 
 * 💡 工作原理：
 * - 在请求处理前记录开始时间
 * - 在请求处理后计算耗时差值
 * - 输出或记录接口调用耗时数据
 * 
 * 📍 示例代码位置：
 * cloud-gateway9527\src\main\java\com\atguigu\cloud\mygateway\MyGlobalFilter.java
 * 
 * === 关联知识 ===
 * 「GlobalFilter」Spring Cloud Gateway全局过滤器接口
 * 「Ordered」确定过滤器执行顺序的接口
 */

/** ⭐ Gateway Filter过滤器机制
 * == 概念对比 ==
 * 类似于SpringMvc的拦截器Interceptor和Servlet的过滤器Filter
 * "pre"和"post"分别在请求执行前和执行后调用，用于修改请求和响应信息
 * 
 * == 主要应用场景 ==
 * 1. 鉴权 - 验证请求的合法性
 * 2. 异常处理 - 统一处理服务异常
 * 3. 💡 接口调用时长统计 - 性能监控（重点，大厂面试设计题）
 * 
 * == 过滤器类型 ==
 * 1. ⭐ 全局默认过滤器(Global Filters)
 *    - Gateway出厂默认提供，直接使用
 *    - 作用于所有路由，实现GlobalFilter接口即可
 *    - 不需要在配置文件中配置
 * 
 * 2. ⭐ 单个服务过滤器(Per-Route Filters)
 *    - 也称为网关过滤器，作用于单一路由或路由分组
 *    - 需要在配置文件中显式配置，格式：
 *      gateway:
 *        routes:
 *          - id: payment_routh
 *            uri: lb://cloud-payment-service
 *            filters: 
 *              - AddRequestHeader=X-Request-red,blue  # 添加请求头
 * 
 * == 实际应用示例 ==
 * 💡 测试请求头示例：
 * - 实现文件：cloud-provider-payment8001/src/main/java/com/atguigu/cloud/controller/PayGateWayController.java
 * - 配置文件：cloud-gateway9527/src/main/resources/application.yml
 *   配置示例：AddRequestHeader=X-Request-atguigu1,atguiguValue1
 * 
 * 💡 自定义全局过滤器配置：
 * spring:
 *   cloud:
 *     gateway:
 *       default-filters:
 *         - AddResponseHeader=X-Response-Default-Red, Default-Blue
 *         - PrefixPath=/httpbin
 * 
 * 3. ⭐ 自定义过滤器
 *    1) 自定义全局filter
 *       - 实现文件：cloud-gateway9527/src/main/java/com/atguigu/cloud/mygateway/MyGlobalFilter.java
 *       - 用途：统计接口性能
 * 
 *    2) 自定义条件filter
 *       - 实现文件：cloud-gateway9527/src/main/java/com/atguigu/cloud/mygateway/MyGatewayFilterFactory.java
 *       - 配置示例：- My=atguigu (对应MyGatewayFilterFactory，匹配status=atguigu)
 *       - 配置文件：cloud-gateway9527/src/main/resources/application.yml
 */

/** ⭐ Gateway Predicate 断言机制 =====
 * 
 * 💡 概述：
 * Predicate是Gateway的核心功能之一，用于确定请求是否匹配特定路由规则。
 * 类似于"if条件"，满足条件则路由到指定服务。
 * 
 * === 配置格式（两种方式） ===
 * 1. 逗号分隔格式（常用）
 *    predicates:
 *     - Cookie=mycookie,mycookievalue
 * 
 * 2. 键值对格式
 *    predicates:
 *     - name: Cookie
 *       args:
 *         name: mycookie
 *         regexp: mycookievalue  # cookie名为mycookie，值为mycookievalue
 * 
 * === 内置断言类型 ===
 * - After：在指定时间之后的请求才会路由
 * - Before：在指定时间之前的请求才会路由
 * - Between：在指定时间区间内的请求才会路由
 * - Cookie：请求必须包含指定Cookie
 * - Header：请求必须包含指定请求头
 * - Method：请求方法必须匹配指定方法
 * - Path：请求路径必须匹配指定模式
 * - Query：请求参数必须包含指定参数
 * 
 * === 自定义断言工厂 ===
 * 
 * ❗ 命名规则：
 * 必须以"RoutePredicateFactory"结尾，例如：MyRoutePredicateFactory
 * 
 * 📝 实现步骤：
 * 1. 创建类继承AbstractRoutePredicateFactory
 * 2. 添加@Component注解
 * 3. 实现构造方法调用父类
 * 4. 重写apply方法定义断言逻辑
 * 5. 实现shortcutFieldOrder方法映射配置参数
 * 
 * 代码示例：
 * @Component
 * public class MyRoutePredicateFactory extends AbstractRoutePredicateFactory<MyRoutePredicateFactory.Config> {
 *     // 调用父类构造方法，传入配置类
 *     public MyRoutePredicateFactory() {
 *         super(MyRoutePredicateFactory.Config.class);
 *     }
 *     
 *     // 重写apply方法实现断言逻辑
 *     // 实现shortcutFieldOrder方法映射配置参数
 * }
 * 
 * 📚 相关示例文件：
 * - 配置文件：cloud-gateway9527\src\main\resources\application.yml
 * - 自定义断言工厂：cloud-gateway9527\src\main\java\com\atguigu\cloud\mygateway\MyRoutePredicateFactory.java
 */

/** gateway高级特效
 * 1. 按服务名动态路由 lb://cloud-payment-service
 * 在cloud-gateway9527\src\main\resources\application.yml中演示
 * 
 */

/** ===== Spring Cloud Gateway 实战指南 =====
 * 
 * 
 * 📌 基本实现步骤：
 * 1. 导入依赖 - cloud-gateway9527\pom.xml
 * 2. 编写配置文件 - cloud-gateway9527\src\main\resources\application.yml
 * 3. 编写启动类 - 当前文件
 * 
 * 🎯 核心目标：
 * - 安全隐藏内部服务端口(8001)
 * - 通过网关层(9527)统一管理服务访问
 * 
 * 💡 关键实现：
 * - 配置文件：cloud-gateway9527\src\main\resources\application.yml
 *   ⭐ 路由规则决定请求如何转发，确保路径匹配才能正确访问
 * 
 * - 服务调用：cloud-api-commons\src\main\java\com\atguigu\cloud\apis\PayFeignApi.java
 *   ⭐ 通过OpenFeign实现微服务间的HTTP调用
 * 
 * 📝 工作原理：
 * 1. 外部请求先到达Gateway(9527)
 * 2. Gateway根据路由规则转发到内部服务(8001)
 * 3. 响应通过相同路径返回给客户端
 */

/** ===== Spring Cloud Gateway 核心概念 =====
 * 
 * 📌 基本介绍
 * Gateway是Spring Cloud提供的新一代API网关服务，基于Spring Boot、WebFlux和Project Reactor构建。
 * 
 * 🎯 核心功能：
 * - 统一管理微服务入口，实现API聚合
 * - 提供动态路由、安全防护、流量控制等能力
 * - 支持请求监控、日志记录和链路追踪
 * 
 * ⭐ 三大核心组件：
 * 1. 路由(Route)「网关的基本构建块」
 *    - 网关的基本构建单元
 *    - 定义了请求转发的目标地址
 *    - 由ID、目标URI、断言集合和过滤器集合组成
 * 
 * 2. 断言(Predicate)「请求匹配条件」
 *    - 匹配规则引擎，决定请求是否符合路由条件
 *    - 支持多种匹配方式：路径、时间、Cookie、Header等
 *    - 💡 思考点：断言可组合使用形成复杂条件逻辑
 * 
 * 3. 过滤器(Filter)「请求/响应处理器」
 *    - 请求/响应修改器，实现横切关注点
 *    - 分为全局过滤器和局部过滤器两种类型
 *    - 执行顺序：pre过滤器 → 微服务调用 → post过滤器
 * 
 * 📝 工作原理详解：
 * =====================
 * 1. 客户端请求到达Gateway服务
 * 2. Gateway通过断言判断请求是否匹配某个路由
 * 3. 匹配成功后依次经过pre类型过滤器处理
 * 4. 转发到目标微服务并获取响应
 * 5. 返回途中经过post类型过滤器处理
 * 6. 最终响应返回给客户端
 * 
 * ❗ 常见应用场景：
 * -----------------
 * - 身份认证与授权「控制服务访问安全」
 * - 请求限流与熔断「保护后端服务稳定性」
 * - 请求/响应内容转换「数据格式适配」
 * - 服务聚合与API版本管理「简化前端调用」
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