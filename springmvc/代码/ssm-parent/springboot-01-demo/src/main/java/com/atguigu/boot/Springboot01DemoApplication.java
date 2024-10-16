package com.atguigu.boot;

import com.atguigu.boot.properties.DogProperties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;


/** 
 * 
 */

/** 可观测性
 * 1. 导入依赖 <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-actuator</artifactId></dependency>
 * 2. 访问地址：http://localhost:8080/actuator
 * 3. 这将展示出所有可以用的监控端点
 * 4. 配置文件示例：
 *    management:
 *      endpoints:
 *        enabled-by-default: true
 *        web:
 *          exposure:
 *            include: '*'  # 表示所有端点都暴露
 * 5. 可以查看beans，查看配置文件，查看环境变量，查看健康状况，查看日志文件，等等
 * 最重要的端点：
 * /actuator/threaddump：查看线程dump（每个线程信息）
 * /actuator/heapdump：查看堆dump（堆内存快照）
 * /actuator/metrics：查看项目指标（磁盘，内存，cpu，网络，文件句柄，线程池，等等）
 * 
 */

/** 单元测试-断言机制
 * | 方法 | 说明 |
 * |------|------|
 * | assertEquals | 判断两个对象或两个原始类型是否相等 |
 * | assertNotEquals | 判断两个对象或两个原始类型是否不相等 |
 * | assertSame | 判断两个对象引用是否指向同一个对象 |
 * | assertNotSame | 判断两个对象引用是否指向不同的对象 |
 * | assertTrue | 判断给定的布尔值是否为 true |
 * | assertFalse | 判断给定的布尔值是否为 false |
 * | assertNull | 判断给定的对象引用是否为 null |
 * | assertNotNull | 判断给定的对象引用是否不为 null |
 * | assertArrayEquals | 数组断言 |
 * | assertAll | 组合断言 |
 * | assertThrows | 异常断言 |
 * | assertTimeout | 超时断言 |
 * | fail | 快速失败 |
 * 
 * 示例： 测试单元中返回hello字符串才算成功，否则就是失败
 * @Test
 * void test02() {
 *     // 1. 业务规定：返回hello字符串才算成功，否则就是失败
 *     String result = helloService.sayHello();
 *     // 2. 断言：判断字符串是否等于hello
 *     Assertions.assertEquals("hello", result, "helloservice并没有返回hello");
 * }
 */

/** 单元测试
 * !@Test - 表示这是一个测试方法
 * @ParameterizedTest - 表示这是一个参数化测试,下方会有详细介绍
 * @RepeatedTest - 表示这个测试方法可以重复执行,下方会有详细介绍
 * !@DisplayName - 为测试类或测试方法设置展示名称
 * !@BeforeEach - 表示在每个单元测试之前执行
 * !@AfterEach - 表示在每个单元测试之后执行
 * !@BeforeAll - 表示在所有单元测试之前执行
 * !@AfterAll - 表示在所有单元测试之后执行
 * @Tag - 表示单元测试类别,类似于JUnit4中的@Categories
 * @Disabled - 表示测试类或测试方法不执行,类似于JUnit4中的@Ignore
 * @Timeout - 表示测试方法运行超过指定时间将会返回错误
 * @ExtendWith - 为测试类或测试方法提供扩展引用
 * 
 * 示例： 展示测试方法名称
 * @DisplayName("第一个测试")
 * @Test
 * void test01() {
 *    log.info("测试通过");
 * }
 * 
 * 
 * 
 */

/** 外部化配置
 * 外部配置优先于内部配置
 * 最高优先级是：命令行参数
 * 
 * 1. 项目打成jar包后，同级目录下如果有一个配置文件，会优先使用这个配置文件
 * 2. 项目打成jar包后，同级目录下如果有config文件夹，会优先使用config文件夹下的配置文件（比1优先级高）
 * 3. 项目打成jar包后，同级目录下如果有config文件夹，config下还有文件夹，会优先最里层文件夹中的配置文件（比2优先级高）
 * 
 * 如果内部没有application-dev.properties但是外部有，会优先使用外部的（配置了环境激活环境下）
 * 如果外部用application.properties，内部用application-dev.properties，会优先使用内部的激活
 * 
 * 总结：优先级：命令行参数 > 外部化配置 > 内部配置 （内部激活>外部不激活）
 */

/** Profiles环境隔离-配置文件
 * 1. application.properties: 主配置文件，任意时候都生效
 * 2. application-{profile}.properties: 指定环境配置文件，激活指定环境时生效
 * 优先级: application-{profile}.properties > application.properties
 * 
 * 激活方式:
 * - 配置文件中设置: spring.profiles.active={profile}
 * - 命令行参数: --spring.profiles.active={profile}
 * 
 * 多环境配置文件可以实现不同环境（如开发、测试、生产）的配置隔离，
 * 便于管理和切换不同环境的配置。
 */

/** Profiles环境隔离-分组
 * 创建prod组，指定包含db和mq配置
 * spring.profiles.group.prod[o]=db
 * spring.profiles.group.prod[1]=mq
 * 使用--spring·profiles.active=prod，激活prod，db，mq配置文件
 * 
 */

/** Profiles环境隔离-基础用法
 * 1. 定义环境： dev、test、prod；
 * 2. 定义这个环境下生效哪些组件或者哪些配置？
 *      1）、生效哪些组件： 给组件 @Profile("dev")
 *      2）、生效哪些配置： 弄多几个配置文件：application-{环境标识}.properties
 * 3. 激活这个环境：这些组件和配置就会生效
 *      1）、application.properties:  配置项：spring.profiles.active=dev/v1,v2(可以指定多个环境)
 *      2）、命令行：java -jar xxx.jar --spring.profiles.active=dev/v1,v2(可以指定多个环境)
 * 
 * 注意：激活的配置优先级高于默认配置
 * 生效的配置 = 默认配置 + 激活的配置(profiles.active) +  包含的配置(profiles.include)
 * 
 * spring.profiles.include=common/v1,v2(可以指定多个环境)
 * 表示当前这个配置文件包含common这个配置文件(application-common.properties)
 * 
 * 在当前resources下演示
 */

/** 日志系统-切换日志组合
 * 日志是由基础start导入的，spring-boot-starter
 * 在当前项目的pom种，排除掉spring-boot-starter-logging 然后导入其他日志系统的starter
 * <!-- 排除默认日志 -->
 * <dependency>
 *     <groupId>org.springframework.boot</groupId>
 *     <artifactId>spring-boot-starter</artifactId>
 *     <exclusions>
 *         <exclusion>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-starter-logging</artifactId>
 *         </exclusion>
 *     </exclusions>
 * </dependency>
 * 
 * <!-- 引入 Log4j2 -->
 * <dependency>
 *     <groupId>org.springframework.boot</groupId>
 *     <artifactId>spring-boot-starter-log4j2</artifactId>
 * </dependency>
 * 
 * 配置文件中还能用的：
 * logging.level.root
 * logging.file.name
 * 需要改的：
 * logging.logback.XXX
 * logback.xml
 * 改为
 * logging.log4j2.XXX
 * log4j2.xml
 * 
 */

/** 日志系统-自定义配置
 * 如果有配置文件，以配置文件为准，，如果没有配置文件，以默认配置为准
 * 直接将日志配置文件放类路劲下：
 * 如果日志系统是logback，则配置文件名为logback.xml/logback-spring.xml （默认）
 * 如果日志系统是log4j2，则配置文件名为log4j2.xml/log4j2-spring.xml
 * 如果日志系统是jdk，则配置文件名为logging.properties
 */

/** 日志系统-文件归档与滚动切割
 * 归档：每天的日志单独存到一个文件中
 * 切割：每个文件10MB，超过大小切割成另一个文件
 * 
 * 配置项及说明：
 * +------------------------------------------------+------------------------------------------+-----------------------------------+
 * | 配置项                                         | 描述                                     | 默认值                            |
 * +------------------------------------------------+------------------------------------------+-----------------------------------+
 * | logging.logback.rollingpolicy.file-name-pattern| 日志存档的文件名格式                     | ${LOG_FILE}.%d{yyyy-MM-dd}.%i.gz  |
 * | logging.logback.rollingpolicy.clean-history-on-start | 应用启动时是否清除以前存档          | false                             |
 * | logging.logback.rollingpolicy.max-file-size    | 每个日志文件的最大大小                   | 10MB                              |
 * | logging.logback.rollingpolicy.total-size-cap   | 日志文件被删除之前，可以容纳的最大大小   | 0B                                |
 * | logging.logback.rollingpolicy.max-history      | 日志文件保存的最大天数                   | 7                                 |
 * +------------------------------------------------+------------------------------------------+-----------------------------------+
 * 
 * 例子：
 * 
 * 日志存档的文件名格式：
 * logging.logback.rollingpolicy.file-name-pattern=app.log.%d{yyyy-MM-dd}.%i.gz
 * app.log.2013-10-11.1.gz  app.log.2013-10-11.2.gz ... app.log.2013-10-11.n.gz
 * app.log.2013-10-12.1.gz  app.log.2013-10-12.2.gz ... app.log.2013-10-12.n.gz
 * 
 * 启动时清除以前存档：
 * logging.logback.rollingpolicy.clean-history-on-start=true
 * 
 * 每个日志的最大大小： 如果到了这个值，就会切割成另一个文件
 * logging.logback.rollingpolicy.max-file-size=10MB
 * 
 * 日志文件被删除之前，可以容纳的最大大小：
 * logging.logback.rollingpolicy.total-size-cap=100MB
 * 
 * 在以下文件中演示：
 * springboot-01-demo\src\test\java\com\atguigu\boot\LogTest.java
 * 
 * 日志文件保存的最大天数：
 * logging.logback.rollingpolicy.max-history=30
 */

/** 日志系统-文件输出
 * SpringBoot 默认只将日志输出到控制台。如果想额外记录到文件，
 * 
 * 可以在 application.properties 中添加以下配置项：
 * 1. logging.file.name：指定日志文件名
 * logging.file.name=boot.log #当前项目所在的根文件夹下生成一个指定名字的日志文件
 * 2. logging.file.path：指定日志文件路径
 * logging.file.path=D://aaa.log #在指定路径下生成一个指定名字的日志文件
 * 
 * 配置效果如下：
 * +-------------------+-------------------+----------+----------------------------------+
 * | logging.file.name | logging.file.path | 示例     | 效果                             |
 * +-------------------+-------------------+----------+----------------------------------+
 * | 未指定            | 未指定            |          | 仅控制台输出                     |
 * | 指定              | 未指定            | my.log   | 写入指定文件，可以加路径         |
 * | 未指定            | 指定              | /var/log | 写入指定目录，文件名为 spring.log |
 * | 指定              | 指定              |          | 以 logging.file.name 为准        |
 * +-------------------+-------------------+----------+----------------------------------+
 */

/** 日志组配置 
 * # 设置日志组
 * logging.group.biz=com.atguigu.service,com.atguigu.dao
 * # 整组批量设置日志级别
 * logging.level.biz=debug
 * 
 * logging.level.web 包含以下包：
 * org.springframework.core.codec,
 * org.springframework.http,
 * org.springframework.web,
 * org.springframework.boot.actuate.endpoint.web,
 * org.springframework.boot.web.servlet.ServletContextInitializerBeans
 * 
 * logging.level.sql 包含以下包：
 * org.springframework.jdbc.core,
 * org.hibernate.SQL,
 * org.jooq.tools.LoggerListener
 */

/** 日志级别的简单使用
 * 例子：logging.level.com.atguigu.mybatis.mapper=debug 让mybatis的sql语句打印出来
 * 
 * if ("1".equals(log)) {
 *    log.debug("调试日志......");
 *    // 业务流程
 *    try {
 *        // 关键点
 *        log.info("信息日志........");
 *
 *        // 容易出问题点
 *        aa.bb() {
 *            log.warn("警告日志........");
 *        }
 *    } catch (Exception e) {
 *        log.error("错误日志......" + e.getMessage());
 *    }
 * }
 * 
 * 在以下文件中演示：
 * springboot-01-demo\src\test\java\com\atguigu\boot\LogTest.java
 */

/** 日志级别：
 * #如果哪个包、哪个类不说日志级别，就用默认root的级别
 * 在配置文件中设置：logging.level.root=info  #(root 表示根日志(所有文件)，默认级别为 info)
 * #指定某个包的日志级别：logging.level.${包:com.atguigu.boot}=debug
 * 
 * 默认级别：info  只会打印info及以上级别的日志
 * 级别从低到高：ALL < TRACE < DEBUG < INFO < WARN < ERROR < OFF
 * 级别越高，记录的日志越少
 * 级别越低，记录的日志越多
 * all：记录所有级别的日志 （所有信息）
 * trace：追踪日志（追踪代码到哪一步）
 * debug：调试日志 （运行到某一步比较重要，调试一下，看核心步骤）
 * info：信息日志，级别一般，常用  （普通信息）
 * warn：警告日志，级别较高，常用  （警告信息，jar包快过时了等等）
 * error：错误日志，级别最高，常用 （错误信息）
 * fetal：致命错误，级别最高，不常用 （致命错误）
 * none：不记录日志
 * 
 * 在以下文件中演示：
 * springboot-01-demo\src\test\java\com\atguigu\boot\LogTest.java
 */

/** @Slf4j 注解的简单使用
 * @Slf4j
 * @SpringBootTest
 * public class LogTest {
 *     // 1、获取一个日志记录器
 *     // Logger logger = LoggerFactory.getLogger(LogTest.class);
 *     @Test
 *     void test01() {
 *         System.out.println("=====");
 *         // 2、记录日志
 *         log.trace("追踪日志......");
 *         log.debug("调试日志......");
 *         log.info("信息日志......");
 *         log.warn("警告日志......");
 *         log.error("错误日志......");
 *     }
 * }
 */

/** 日志解释 和用日志记录器对象来进行简单使用
 * 1、SLF4j：日志抽象层
 * 2、Logback：日志实现类
 * 3、SpringBoot 默认使用 SLF4j + Logback
 * // 1、获取一个日志记录器
 * Logger logger = LoggerFactory.getLogger(LogTest.class);
 * // 2、记录日志
 * logger.trace("追踪日志......");
 * logger.debug("调试日志......");
 * logger.info("信息日志......");
 * logger.warn("警告日志......");
 * logger.error("错误日志......");
 * 格式：时间 级别  进程id  ---项目名 ---线程名 --- 当前类名：日志内容
 * 
 * # 默认输出格式
 * 时间和日期：毫秒级精度
 * 日志级别：ERROR, WARN, INFO, DEBUG, 或 TRACE
 * 进程 ID
 * ---：消息分割符
 * - 线程名：使用[]包含
 * - Logger 名：通常是产生日志的类名
 * - 消息：日志记录的内容
 * 
 * 注意：logback 没有 FATAL 级别，对应的是 ERROR 级别
 */

/** 场景启动器：spring-boot-starter-xxx
 * 官方写的：spring-boot-starter-*
 * 第三方的：*-spring-boot-starter
 * 在场景启动器中，有各种相关依赖
 * 
 * 把当前场景用的jar包都引入进来；
 * 每个场景启动器都有一个基础依赖：spring-boot-starter 然后还有其他各种依赖
 * spring-boot-starter-web
 * spring-boot-starter-aop
 * spring-boot-starter-data-jdbc
 * spring-boot-starter-test
 * spring-boot-starter-tomcat
 * spring-boot-starter-json
 */

/** 环境隔离：
 * 1、定义环境： dev、test、prod；
 * 2、定义这个环境下生效哪些组件或者哪些配置？
 *      1）、生效哪些组件： 给组件 @Profile("dev")
 *      2）、生效哪些配置： application-{环境标识}.properties
 * 3、激活这个环境：这些组件和配置就会生效
 *      1）、application.properties:  配置项：spring.profiles.active=dev
 *      2）、命令行：java -jar xxx.jar --spring.profiles.active=dev
 *
 * 注意：激活的配置优先级高于默认配置
 * 生效的配置 = 默认配置 + 激活的配置(profiles.active) +  包含的配置(profiles.include)
 *
 */
@EnableAsync //开启基于注解的自动异步
@Slf4j
@EnableConfigurationProperties(DogProperties.class) 
//自动配置
@SpringBootApplication
public class Springboot01DemoApplication {


    //1、以前： war 包； webapps
    public static void main(String[] args) {
        //应用启动
        //SpringApplication.run(Springboot01DemoApplication.class, args);

        SpringApplicationBuilder builder = new SpringApplicationBuilder();

        //链式调用
        builder
                .sources(Springboot01DemoApplication.class)
                .bannerMode(Banner.Mode.CONSOLE) //Banner.Mode.OFF 关闭banner
                .environment(null)
//                .listeners(null) 不能为空，会空指针异常
                .run(args);

        // 以下是自定义 SpringApplication 的示例代码
        // 1. 创建 SpringApplication 对象
        // SpringApplication application = new SpringApplication(Springboot01DemoApplication.class);
        // 2. 关闭 banner
        // application.setBannerMode(Banner.Mode.OFF);
        // 3. 设置监听器
        // application.setListeners(Arrays.asList(new MyCustomListener()));
        // 4. 设置环境
        // application.setEnvironment(new StandardEnvironment());
        // 5. 启动应用
        // application.run(args);


    }


    @Bean
    CommandLineRunner commandLineRunner(){
        return new CommandLineRunner() {
            @Override
            public void run(String... args) throws Exception {
                log.info("CommandLineRunner...run...");
                //项目启动后的一次性任务

            }
        };
    }

}
