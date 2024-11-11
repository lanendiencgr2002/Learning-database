package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import tk.mybatis.spring.annotation.MapperScan;

/** Seata原理小总结
 * 前提条件：
 * - 基于支持本地ACID事务的关系型数据库
 * - Java应用，通过JDBC访问数据库
 * 
 * 两阶段提交协议的演变：
 * 1. 一阶段：
 *    - 业务数据和回滚日志在同一个本地事务中提交
 *    - 释放本地锁和连接资源
 *    
 *    Seata拦截"业务SQL"的处理流程：
 *    1) 解析SQL语句
 *       - 找到"业务SQL"要更新的业务数据
 *       - 在业务数据被更新前将其保存成"before image"（前镜像）
 *    2) 执行"业务SQL"更新业务数据
 *    3) 保存after image（后镜像）并生成行锁
 *    
 *    注意：以上操作全部在一个数据库事务内完成
 * 
 * 2. 二阶段：
 *    - 提交：异步化处理，快速完成
 *    - 回滚：通过一阶段的回滚日志进行反向补偿
 *    - 原因：因为"业务SQL"在一阶段已经提交到数据库，
 *           所以Seata框架只需将一阶段保存的快照数据进行回滚操作，完成数据清理即可
 * 
 * 示例代码：
 * public class OrderService {
 *     @GlobalTransactional  // TM (Transaction Manager)
 *     public void saveOrder() {
 *         //1. 下订单，本地事务 --> RM (Resource Manager)
 *         //2. 减库存，本地事务 --> RM
 *     }
 * }
 */

/** Seata案例实战
 * 启动nacos 8848 和 seata-server-2.0.0
 * 1. 创建3个业务数据库DATABASE
 * CREATE DATABASE seata_order;
 * CREATE DATABASE seata_storage;
 * CREATE DATABASE seata_account;
 * 
 * 2. 按照上述3库分别建对应的undo_log回滚日志表
 * 订单-库存-账户3个库下都需要建各自的undo_log回滚日志表  undo_log建表SQL:AT模式专业其他模式不需要
 * https://github.com/seata/seata/blob/2.x/script/client/at/db/mysql.sql
 * 
 * 3. 按照上述3库分别建对应业务表
 * 在笔记中
 * 
 * 4. 生成mybatis代码
 * 改 然后运行插件 mybatis-generator:generate
 * mybatis_generator2024\src\main\resources\config.properties
 * mybatis_generator2024\src\main\resources\generatorConfig.xml
 * 
 * 5. openfeign接口
 * cloud-api-commons\src\main\java\com\atguigu\cloud\apis\StorageFeignApi.java
 * 
 * 6. 新建订单order微服务 storage微服务 account微服务 pom 配置文件 启动类 业务类等
 * 
 * 7. 业务类解释
 * seata-order-service2001\src\main\java\com\atguigu\cloud\serivce\impl\OrderServiceImpl.java
 * 
 * 8. 在order业务类上添加@GlobalTransactional注解  如果异常会回滚，正常会提交
 */

/** Seata下载安装
 * 下载地址：https://seata.io/zh-cn/unversioned/download/seata-server
 * 下载版本：https://github.com/seata/seata/releases/tag/v2.0.0
 * 新手部署指南：https://seata.io/zh-cn/docs/ops/deploy-guide-beginner
 * 
 * TC需要自己的专属库专属表
 * CREATE DATABASE seata;
 * USE seata;
 * 
 * 在上一步seata库里建表
 * 在seata库下执行sql脚本
 * https://github.com/seata/seata/blob/develop/script/server/db/mysql.sql
 * 
 * 更改seata-server.yml配置文件  里边有用户名密码等等 还有数据库ip密码各种等等
 * 修改seata-server-2.0.0\conf\application.yml配置文件,记得先备份
 * 
 * 启动nacos 8848
 * 
 * 启动seata-server-2.0.0
 * 
 * 命令运行成功后直接访问http://localhost:8848/nacos
 * 如果服务注册成功 看 http://localhost:7091
 */

/** Seata AT模式
 * 自动模式
 * 官网：https://seata.io/zh-cn/docs/user/mode/at/
 * 日常工作+企业调研+本次课时安排限制，以AT模式作为入手突破
 * 
 * 
 */

/** seata流程
 * 1.TM向TC申请开启一个全局事务，全局事务创建成功并生成一个全局唯一的XID；
 * 2.XID在微服务调用链路的上下文中传播；
 * 3.RM向TC注册分支事务，将其纳入XID对应全局事务的管辖；
 * 4.TM向TC发起针对XID的全局提交或回滚决议；
 * 5.TC调度XID下管辖的全部分支事务完成提交或回滚请求。
 */

/** Seata TC，TM，RM的解释
 * （TC→TM→RM）分别什么意思
 * 纵观整个分布式事务的管理，就是全局事务ID的传递和变更，要让开发者无感知
 * 
 * Seata对分布式事务的协调和控制  就是1+3
 * 1个XID：XID是全局事务的唯一标识，它可以在服务的调用链路中传递，绑定到服务的事务上下文中。
 * 3个组件：
 * TC：事务协调者：维护全局和分支事务的状态，驱动全局事务提交或回滚。
 * TM：事务管理器：定义全局事务的范围，发起全局事务、提交或回滚全局事务。
 * RM：资源管理器：管理分支事务处理的资源，与TC交谈以注册分支事务和报告分支事务的状态，并驱动分支事务提交或回滚。
 * TC可以有多个，RM也可以有多个，但是TM只能有一个
 * 
 * 更精简的解释：
 * TC：就是Seata，
 * 负责维护全局事务和分支事务的状态，驱动全局事务提交或回滚。（管全局xid的）
 * TM：标注全局@GlobalTransactional启动入口动作的微服务模块（比如订单模块)，
 * 它是事务的发起者，负责定义全局事务的范围，并根据TC维护的全局事务和分支事务状态，做出开始事务、提交事务、回滚事务的决议
 * RM：就是mysgl数据库本身，
 * 可以是多个RM，负责管理分支事务上的资源，向TC注册分支事务，汇报分支事务状态，驱动分支事务的提交或回滚
 */

/** 分布式事务seata介绍
 * Seata是一款开源的分布式事务解决方案，致力于在微服务架构下提供高性能和简单易用的分布式事务服务。
 * 官网：https://seata.io/zh-cn/index.html
 * 源码：https://github.com/seata/seata/releases
 * 
 * 单体应用被拆分成微服务应用，原来的三个模块被拆分成三个独立的应用，分别使用三个独立的数据源，业务操作需要调用三个服务来完成。
 * 此时每个服务自己内部的数据一致性由本地事务来保证，但是全局的数据一致性问题没法保证。
 * 
 * 怎么用？
 * 本地@Transactional注解，分布式事务@GlobalTransactional注解
 * 
 */

/**
 * @auther zzyy
 * @create 2024-01-06 15:22
 */
@SpringBootApplication
@MapperScan("com.atguigu.cloud.mapper") //import tk.mybatis.spring.annotation.MapperScan;
@EnableDiscoveryClient //服务注册和发现
@EnableFeignClients
public class SeataOrderMainApp2001
{
    public static void main(String[] args)
    {
        SpringApplication.run(SeataOrderMainApp2001.class,args);
    }
}