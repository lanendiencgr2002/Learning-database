package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import tk.mybatis.spring.annotation.MapperScan;

/** ⭐ Seata分布式事务原理详解
 * == 基本前提 ==
 * - 基于支持本地ACID事务的关系型数据库
 * - Java应用，通过JDBC访问数据库
 * 
 * == 两阶段提交协议演变 ==
 * 1. 一阶段「业务数据处理」：
 *    - 业务数据和回滚日志在同一个本地事务中提交
 *    - 释放本地锁和连接资源
 *    
 *    💡 Seata拦截"业务SQL"处理流程：
 *    1) 解析SQL语句
 *       - 找到要更新的业务数据
 *       - 在更新前保存"before image"（前镜像）
 *    2) 执行"业务SQL"更新数据
 *    3) 保存after image（后镜像）并生成行锁
 *    
 *    ❗ 注意：全部操作在单一数据库事务内完成
 * 
 * 2. 二阶段「事务提交/回滚」：
 *    - 提交：异步化处理，快速完成
 *    - 回滚：通过一阶段回滚日志进行反向补偿
 *    - 原理：一阶段SQL已提交，仅需还原保存的快照数据
 * 
 * 💡 代码示例：
 * public class OrderService {
 *     @GlobalTransactional  // TM (Transaction Manager)
 *     public void saveOrder() {
 *         //1. 下订单，本地事务 --> RM (Resource Manager)
 *         //2. 减库存，本地事务 --> RM
 *     }
 * }
 */

/** ⭐ Seata案例实战部署步骤
 * == 准备工作 ==
 * 1. 启动nacos 8848 和 seata-server-2.0.0
 * 
 * 2. 创建业务数据库
 * CREATE DATABASE seata_order;
 * CREATE DATABASE seata_storage;
 * CREATE DATABASE seata_account;
 * 
 * 3. 创建undo_log回滚日志表
 * - 在订单、库存、账户3个库下建立undo_log表
 * - 参考SQL：https://github.com/seata/seata/blob/2.x/script/client/at/db/mysql.sql
 * 
 * == 详细部署步骤 ==
 * 4. 生成mybatis代码
 * 5. 配置openfeign接口
 * 6. 创建微服务（order、storage、account）
 * 7. 添加@GlobalTransactional注解
 * 
 * 💡 关键文件：
 * - mybatis配置：mybatis_generator2024/resources/config.properties
 * - Feign接口：cloud-api-commons/apis/StorageFeignApi.java
 * - 业务实现：seata-order-service2001/serivce/impl/OrderServiceImpl.java
 */

/** ⭐ Seata服务器安装指南
 * == 下载资源 ==
 * - 官网：https://seata.io/zh-cn/unversioned/download/seata-server
 * - 版本：https://github.com/seata/seata/releases/tag/v2.0.0
 * 
 * == 数据库准备 ==
 * 1. 创建专属数据库
 * CREATE DATABASE seata;
 * USE seata;
 * 
 * 2. 执行建表脚本
 * - 脚本地址：https://github.com/seata/seata/blob/develop/script/server/db/mysql.sql
 * 
 * == 配置与启动 ==
 * - 修改seata-server.yml配置文件
 * - 启动nacos 8848
 * - 启动seata-server-2.0.0
 * 
 * 💡 验证：
 * - Nacos控制台：http://localhost:8848/nacos
 * - Seata控制台：http://localhost:7091
 */

/** ⭐ Seata AT模式简介
 * == 基本特征 ==
 * - 自动模式
 * - 官方文档：https://seata.io/zh-cn/docs/user/mode/at/
 * 
 * 💡 课程选择：基于工作实践和课程限制，重点学习AT模式
 */

/** ⭐ Seata事务流程详解
 * 1. TM向TC申请全局事务，生成唯一XID
 * 2. XID在微服务调用链路中传播
 * 3. RM向TC注册分支事务，纳入XID管理
 * 4. TM向TC发起全局事务提交/回滚决议
 * 5. TC调度分支事务完成提交/回滚
 */

/** ⭐ Seata核心组件解析
 * == 组件职责 ==
 * TC：事务协调者
 *   - 维护全局和分支事务状态
 *   - 驱动全局事务提交/回滚
 * 
 * TM：事务管理器
 *   - 定义全局事务范围
 *   - 发起、提交或回滚全局事务
 * 
 * RM：资源管理器
 *   - 管理分支事务资源
 *   - 与TC交互注册和报告事务状态
 *   - 驱动分支事务提交/回滚
 * 
 * 💡 关键特点：
 * - TC可多个，RM可多个
 * - TM唯一
 */

/** ⭐ 分布式事务Seata概述
 * == 基本介绍 ==
 * - 开源分布式事务解决方案
 * - 官网：https://seata.io/zh-cn/index.html
 * - 源码：https://github.com/seata/seata/releases
 * 
 * == 微服务事务挑战 ==
 * - 单体应用拆分为微服务
 * - 每个服务使用独立数据源
 * - 全局数据一致性成为关键问题
 * 
 * 💡 使用方式：
 * - 本地事务：@Transactional
 * - 分布式事务：@GlobalTransactional
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