package com.atguigu.spring.tx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*
 * @EnableTransactionManagement 开启基于注解的自动化事务管理
 * 在@SpringBootApplication上面加上
 * 然后在需要加事务的方法上面加上@Transactional
 * 事务细节：注解里边源码的属性
 * 
 * 声明式事务的底层原理：
 * transactionManager(接口)：事务管理器  控制事务的获取、提交、回滚 默认使用 JdbcTransactionManager；
 * TransactionInterceptor(切面)： 控制何时提交和回滚
 * 
 * 属性：
 * propagation：传播行为； 事务的传播行为。
 * isolation：隔离级别
 * timeout（同 timeoutString）：超时时间； 事务超时，秒为单位；
 * 
 * 在service/impl/userServiceImpl中
 * 
 */

/*
 * jdbcTemplate.增删改查 
 * 在test/spring03txapplicationtests 和 dao/bookdao 中演示
 */

/*
 * HikariDataSource 是最快的数据源，但只是增删改查快，springboot 默认的数据源
 * DruidDataSource 是生态最丰富的数据源
 */

/**
 * 操作数据库：
 * 1、导入包： spring-boot-starter-data-jdbc、mysql-connector-java
 * 2、配置数据库连接信息：在application.properties 中  spring.datasource.*
 * 3、可以直接使用  DataSource、  JdbcTemplate
 */
@EnableTransactionManagement // 开启基于注解的自动化事务管理
@SpringBootApplication
public class Spring03TxApplication {

    public static void main(String[] args) {
        SpringApplication.run(Spring03TxApplication.class, args);
    }

}
