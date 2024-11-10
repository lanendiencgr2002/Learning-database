package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;



/** Nacos数据模型之Namespace-Group-Datald
 * 问题1:
 * 实际开发中，通常一个系统会准备
 * dev开发环境
 * test测试环境
 * prod生产环境。
 * 如何保证指定环境启动时服务能正确读取到Nacos上相应环境的配置文件呢？
 * 
 * 问题2：
 * 一个大型分布式微服务系统会有很多微服务子项目，
 * 每个微服务项目又都会有相应的开发环境、测试环境、预发环境、正式环境...
 * 那怎么对这些微服务配置进行扩组和命名空间管理呢？
 * 
 * nacos官网：nacos.io/zh-cn/docs/architecture.html 数据模型
 * Nacos数据模型Key由三元组唯一确定，Namespace默认是空串，公共命名空间（public），分组默认是DEFAULT_GROUP。
 * 
 * 可以根据这三者来区分不同的配置，来读取配置，在配置文件中指定这三者
 * 
 * Namespace+Group+Datald三者关系？为什么这么设计？  
 * 1. 是什么？
 * 类似Java里面的package名和类名，最外层的Namespace是可以用于区分部署环境的，Group和DatalD逻辑上区分两个目标对象
 * 2. 默认值默认情况：Namespace=public，Group=DEFAULT_GROUP
 * Nacos默认的命名空间是public，Namespace主要用来实现隔离。比方说我们现在有三个环境：开发、测试、生产环境，我
 * 们就可以创建三个Namespace，不同的Namespace之间是隔离的。Group默认是DEFAULT_GROUP，Group可以把不同的
 * 微服务划分到同一个分组里面去
 * 3. Service就是微服务
 * 一个Service可以包含一个或者多个Cluster（集群），Nacos默认Cluster是DEFAULT，Cluster是对指定微服务的一个虚拟
 * 划分。
 */

/** 在nacos中怎么搞配置
 * nacos端配置文件DataId的命名规则是：
 * ${spring.application.name}-${spring.profiles.active}.${spring.cloud.nacos.config.file-extension}
 * 本案例的DataID是:nacos-config-client-dev.yaml  nacos-config-client-test.yaml （生产环境在application.yml中配置）
 * 
 * 1. 查看yml文件
 * cloudalibaba-config-nacos-client3377\src\main\resources\bootstrap.yml
 * 2. 创建配置
 * 在nacos面板中，创建配置
 * dataId: nacos-config-client-dev.yaml
 * group: DEFAULT_GROUP （默认）
 * 选yaml格式 （在这里有说明cloudalibaba-config-nacos-client3377\src\main\resources\bootstrap.yml）
 * 3. 业务类
 * cloudalibaba-config-nacos-client3377\src\main\java\com\atguigu\cloud\controller\NacosConfigClientController.java
 * 4. 启动主启动类
 * 5. 调用接口查看配置信息http://localhost:3377/config/info
 * 
 * 历史配置： 在面板中，配置管理的历史版本，可以看保留30天的配置（修改的等等都会有存）
 * Nacos会记录配置文件的历史版本默认保留30天，此外还有一键回滚功能，回滚操作将会触发配置更新
 */

/** 如何引入NacosConfig进行配置管理
 * 1. 引入依赖 <!--nacos-config-->  <!--nacos-discovery--> <!--bootstrap,从配置服务器获取配置-->
 * cloudalibaba-config-nacos-client3377\pom.xml
 * 2. 在bootstrap.yml配置nacos地址
 * cloudalibaba-config-nacos-client3377\src\main\resources\bootstrap.yml
 * 3. 在启动类上添加@EnableDiscoveryClient注解
 * 4. 业务类
 * cloudalibaba-config-nacos-client3377\src\main\java\com\atguigu\cloud\controller\NacosConfigClientController.java
 * 
 * 为什么要引入<!--bootstrap,从配置服务器获取配置-->？
 * consul也有，因为bootstrap.yml 比 application.yml 优先加载
 * 这确保了在应用程序启动前就能获取到配置中心的配置
 */

/** nacos2.x
 * 是通过grpc通信长连接，性能高
 * 客户端和服务器在启动后，会建立长连接，如果服务挂了，会感知到
 */

/** nacos1.4
 * 是通过http请求，每隔5秒发心跳包检测服务是否宕机
 * 性能比较低
 */

/**
 * @auther zzyy
 * @create 2024-01-01 16:54
 */
@EnableDiscoveryClient
@SpringBootApplication
public class Main3377
{
    public static void main(String[] args)
    {
        SpringApplication.run(Main3377.class,args);
    }
}