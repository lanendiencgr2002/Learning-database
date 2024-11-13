package com.nageoffer.shortlink.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** try catch(Throwable t) 捕获异常
 * Throwable 是 Java 中的顶级类，表示任何可以被抛出的错误或异常。
 * 它包括 Error 和 Exception 两个子类。
 * 
 * 使用 try-catch 捕获 Throwable 的好处：
 * 1. 捕获所有类型的异常，包括 Error 和 Exception。
 * 2. 可以处理一些无法在编译时捕获的异常，如 OutOfMemoryError 等。
 */

/** 空指针异常
 * 1. 最基础的空指针异常
 * String str = null;
 *    str.length();  // NullPointerException! 因为 null 没有 length() 方法
 * 2. 调用对象方法时的空指针
 * User user = null;
 *    user.getName();  // NullPointerException! 因为 user 是 null
 * 
 * 最佳实践：
 * 1. 使用 Objects 工具类
 *    Objects.equals(str1, str2);  // 安全的比较
 *    // 不好的写法：
 *    if (str1.equals(str2)) {}  // 如果 str1 为 null 会抛 NPE
 *    // 好的写法：
 *    if (Objects.equals(str1, str2)) {}  // 即使 str1 或 str2 为 null 也安全
 * 2. 参数校验
 *    Objects.requireNonNull(input, "Input cannot be null");
 * 3. 返回空集合而不是 null
 *    return Collections.emptyList();  // 而不是 return null
 */

/** 消息队列为什么会出现重复消息？
 * 1. 网络问题导致
 *    生产者 ----消息----> MQ服务器  [网络超时]
 *    生产者：没收到确认，重发消息
 *    结果：同一消息被发送两次
 * 2. 服务重启导致
 *    消费者 ----处理消息----> [服务突然重启]
 *    消费者重启后：重新消费未确认的消息
 *    结果：同一消息被处理两次
 * 3. 消息队列自身机制
 *    // 消息队列为了保证消息至少被消费一次（At Least Once）
 *    可能导致消息被重复投递
 *    MQ服务器 ----消息----> 消费者A [处理成功但确认消息失败]
 *    MQ服务器：没收到确认，重新投递消息
 *    结果：消息被重复消费
 * 常见解决方案
 * 1. 使用消息ID去重
 *    // 在Redis中记录已处理的消息ID
 *    String key = "processed:message:" + messageId;
 *    if (redisTemplate.hasKey(key)) {
 *       return; // 已处理过，直接返回
 *   }
 * 2. 业务字段唯一索引
 *    // 使用唯一索引确保订单号不会重复插入
 *    CREATE UNIQUE INDEX idx_order_no ON orders(order_no);
 * 3. 状态机控制
 *    // 确保状态只能按照特定顺序转换
 *    if (order.getStatus() == OrderStatus.UNPAID) {
 *       order.setStatus(OrderStatus.PAID);
 *   }
 */

/** 使用消息队列后的一些问题
 * 1. 幂等性
 * 幂等性（Idempotence）是指对同一个操作执行一次或多次，产生的结果是相同的。简单来说，就是一个操作重复执行多次，不会产生额外的影响。
 * 例子：   x = 1 // 幂等操作            x = x + 1 // 非幂等操作
 * 比如重复下单同一个订单，第二次下单时，订单状态还是未支付，又会扣款
 * 解决方案：
 * 获取一批消息，判断是否处理过，如果是，则查redis，如果否，则写入一个预占标识，然后查redis
 * 
 * 在以下文件中演示：
 * project\src\main\java\com\nageoffer\shortlink\project\mq\idempotent\MessageQueueIdempotentHandler.java
 * 
 * 2. 消息延迟
 * 消息队列1秒处理10万消息，这时有100万消息，100万消息需要10秒
 * 比如监控啥的就会延迟10秒才展示正确结果
 */

/** redis消息队列 redis stream
 * 1. 创建出redis stream config类
 * 
 * 在以下文件中演示：
 * 消息队列配置：
 * project\src\main\java\com\nageoffer\shortlink\project\config\RedisStreamConfiguration.java
 * 使用消息队列发送：
 * project\src\main\java\com\nageoffer\shortlink\project\service\impl\ShortLinkServiceImpl.java\shortLinkStats
 */

/** @SentinelResource sentinel限流使用 
 * 参数：
 * 1. value：资源名称，必须唯一 （包含规则比如qps等要求）
 * 2. blockHandler：限流处理方法名称，必须和blockHandlerClass一起使用
 * 3. blockHandlerClass：限流处理类，必须和blockHandler一起使用
 * 
 * 在以下文件中演示：
 * project\src\main\java\com\nageoffer\shortlink\project\controller\ShortLinkController.java\createShortLink
 */

/** sentinel限流配置
 * 
 * 在以下文件中演示：
 * project\src\main\java\com\nageoffer\shortlink\project\config\SentinelRuleConfig.java
 */

/** 获取目标网站图标
 * 引入jar包：<dependency>org.jsoup</groupId><artifactId>jsoup</artifactId><version>1.14.3</version></dependency>
 * 
 * 在以下文件中演示：
 * project\src\main\java\com\nageoffer\shortlink\project\service\impl\ShortLinkServiceImpl.java\getFavicon
 */

/** 获取目标网站标题
 * 引入jar包：<dependency>org.jsoup</groupId><artifactId>jsoup</artifactId><version>1.14.3</version></dependency>
 * 
 * 在以下文件中演示：
 * project\src\main\java\com\nageoffer\shortlink\project\service\impl\UrlTitleServiceImpl.java
 */

/** 缓存穿透
 * 先布隆过滤器，再查数据库
 */

/** 缓存预热
 * 在大量请求下刚创建出来的数据库还没放到redis中
 * 解决：创建后设置到redis中，设置个有效期（这里是一个月）
 * 
 * 在以下文件中演示：
 * project\src\main\java\com\nageoffer\shortlink\project\service\impl\ShortLinkServiceImpl.java\createShortLink
 */

/** 缓存击穿
 * 大量请求同时查询一个key时，该key正好失效
 * 解决：这里使用双重检测锁
 * 
 * 在以下文件中演示：
 * project\src\main\java\com\nageoffer\shortlink\project\service\impl\ShortLinkServiceImpl.java\restoreUrl
 * 
 */

/** Optional 类
 * 意义：解决空指针异常
 * 
 * 创建 Optional 对象：
 * // 创建包含值的 Optional
 * Optional<String> optional1 = Optional.of("Hello");
 * // 创建空的 Optional
 * Optional<String> optional2 = Optional.empty();
 * // 创建可能为 null 的 Optional
 * String nullableValue = null;
 * Optional<String> optional3 = Optional.ofNullable(nullableValue);
 * 
 * 使用 Optional：
 * Optional<String> name = Optional.of("Alice");
 * // 如果值存在，打印它
 * name.ifPresent(n -> System.out.println("Name: " + n));
 * // 获取值，如果不存在则返回默认值
 * String result = name.orElse("Unknown");
 * // 如果值不存在，抛出异常
 * String value = name.orElseThrow(() -> new RuntimeException("Name not found"));
 * 
 * 链式操作：
 * Optional<String> name = Optional.of("Alice");
 * String result = name
 *     .filter(n -> n.length() > 3)
 *     .map(String::toUpperCase)
 *     .orElse("Name too short");
 * System.out.println(result); // 输出：ALICE
 * 
 * 在方法中返回 Optional：
 * public Optional<User> findUserById(int id) {
 *     // 假设这是从数据库查询用户
 *     User user = database.query(id);
 *     return Optional.ofNullable(user);
 * }
 * // 使用
 * Optional<User> user = findUserById(1);
 * user.ifPresent(u -> System.out.println("User found: " + u.getName()));
 * 
 * 结合 Stream 使用：
 * List<Optional<String>> listOfOptionals = Arrays.asList(
 *     Optional.of("A"),
 *     Optional.empty(),
 *     Optional.of("B")
 * );
 * List<String> filteredList = listOfOptionals.stream()
 *     .filter(Optional::isPresent)
 *     .map(Optional::get)
 *     .collect(Collectors.toList());
 * System.out.println(filteredList); // 输出：[A, B]
 * 
 * Objects.equals() 方法：
 * Java 7 引入的一个工具方法
 * 它用于安全地比较两个对象是否相等，可以处理其中一个或两个对象为 null 的情况
 * 返回true或者false
 * 如果不使用Objects.equals()方法，直接使用==比较，当任意一个为null时，会抛出NullPointerException
 * 
 * // 不使用 Objects.equals()
 * boolean isDefault = port.equals(80);  // 抛出 NullPointerException
 * // 使用 Objects.equals()
 * boolean isDefault = Objects.equals(port, 80);  // 返回 false
 */

/** @TableName("数据库中对应的表名") 指定实体类对应的数据库表名
 * 
 * 在以下文件中演示：
 * project\src\main\java\com\nageoffer\shortlink\project\dao\entity\ShortLinkDO.java
 */

/** 短链接应用
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.nageoffer.shortlink.project.dao.mapper")
public class ShortLinkApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortLinkApplication.class, args);
    }
}
