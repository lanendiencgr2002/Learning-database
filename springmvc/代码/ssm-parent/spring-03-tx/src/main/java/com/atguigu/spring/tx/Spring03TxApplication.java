package com.atguigu.spring.tx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** 事务@Transactional：加在方法头上(前提是有@service等)，
 * 要在启动类那边开启@EnableTransactionManagement
 * 只要报错了，就回滚
 */

/** 事务细节：@Transactional注解的一些属性
 * 1、transactionManager：事务管理器; 控制事务的获取、提交、回滚。
 *     底层默认使用哪个事务管理器？默认使用 JdbcTransactionManager；
 *     原理：
 *     1、事务管理器：TransactionManager； 控制提交和回滚
 *     2、事务拦截器：TransactionInterceptor： 控制何时提交和回滚
 *              completeTransactionAfterThrowing(txInfo, ex);  在这个时候回滚
 *              commitTransactionAfterReturning(txInfo);  在这个时候提交
 *
 * 2、propagation：传播行为(用的比较多)； 事务的传播行为。 大事务里边有小事务
 * 定义：当一个事务方法被另一个事务方法调用时，事务该以何种状态存在？事务属性该如何传播下去？
 *      REQUIRED(用最多)：大事务有，小事务用大事务，大事务没有，小事务自己创建一个事务。
 *      SUPPORTS：大事务有，小事务用大事务，大事务没有，小事务不创建事务。
 *      MANDATORY：大事务有，小事务用大事务，大事务没有，小事务报错。
 *      REQUIRES_NEW(用最多)：创建一个新事务，并在存在大事务时挂起大事务
 *      NOT_SUPPORTED：不支持事务，如果存在大事务，则暂停。
 *      NEVER：不支持事务，如果存在大事务，则抛出异常
 *      NESTED（保存点）：如果当前存在事务，则在嵌套事务中执行，可以回滚到某一个点，而不是开头
 * 
 * 场景：扣减额度和余额都回滚，但是库存不回滚
 * 实现： 在方法的头上加@Transactional(propagation = Propagation.xxx)
 *    checkout(){
 *        //自己的操作；
 *        扣减金额： //REQUIRED
 *        扣减库存： //REQUIRES_NEW
 *    }
 * 
 * 例子：   异常后不会执行以后代码，异常都会往父传，如果同级是REQUIRES_NEW，不会传
 * checkout(){
 *        //自己的操作；
 *        扣减金额： //REQUIRED
 *        扣减库存： //REQUIRES_NEW 如果发生了报错，会把错误告诉给大事务，大事务回滚，然后全体回滚
 *    }
 * 
 *      *  A {
 *      B(){  //REQUIRED
 *          F();//REQUIRES_NEW
 *          G();//REQUIRED
 *          H();//REQUIRES_NEW
 *      }
 *      C(){  //REQUIRES_NEW
 *         I();//REQUIRES_NEW
 *         J();//REQUIRED
 *      }
 *      D(){   //REQUIRES_NEW
 *          K();//REQUIRES_NEW
 *          L();//REQUIRES_NEW //点位2： 10/0； K,F,H,C(i,j) = ok, E整个代码走不到，剩下炸
 *      }
 *      E(){   //REQUIRED
 *          M();//REQUIRED
 *          //点位3：10/0；  F,H,C(i,j),D(K,L)= ok
 *          N();//REQUIRES_NEW
 *      }
 *
 *      点位1 int i = 10/0;  //点位1：C（I，J）,D(K，L) ，F，H,N= ok 其他都回滚
 *  }
 * 
 * 属性传播：传播行为：参数设置项也会传播：如果小事务和大事务共用一个事务，小事务要按照大事务的设置，小事务自己的设置失效
 * 比如timeout，如果大事务没有设置timeout，小事务的timeout设置就会失效
 * 大事务设置多少timeout，小事务就按照大事务的timeout来
 * 如果此时小事务是REQUIRES_NEW，那么就按小事务自己的来
 * 
 * 3、isolation：隔离级别(用的比较少)  mysql默认是可重复读，oracle默认是读已提交
 * @Transactional(isolation =Isolation.READ_UNCOMMITTED)
 * 读未提交：read uncommitted
 *      在mysql中，Begin 然后改一个数据，此时还没commit，可以读到这个未提交的数据。
 *      会产生脏读：读到回滚的数据
 *      会产生不可重复读：在同一个事务中，两次读取的数据不一致
 * 读已提交：read committed
 *      会产生不可重复读：在同一个事务中，两次读取的数据不一致
 *       读下次的时候，可能已经提交了，这就造成了重复读
 * 可重复读：repeatable read 也叫快照读
 *      多次读到的数据是一致的，都是第一次读到的数据。
 * 串行化：serializable
 *
 * 4、timeout（同 timeoutString）：超时时间； 事务超时，秒为单位；
 *      一旦超过约定时间，事务就会回滚。
 *      ！！！超时时间是指：从方法开始，到最后一次数据库操作结束的时间。
 *      场景：
 *      大事务(timeout=3){
 *          小事务(timeout=3){占用2秒}
 *          小事务2(timeout=3,propagation=Propagation.REQUIRES_NEW){占用3秒}
 *      } 此时小事务2会挂起大事务，不算大事务的时间，小事务2执行完
 *        大事务此时超时时间=2，不算超时
 * 
 * 5、readOnly：只读优化  数据库操作只是查询，不修改，可以设置为只读，提高效率。
 * 
 * 6、rollbackFor（同rollbackForClassName）：指明哪些异常需要回滚。不是所有异常都一定引起事务回滚。
 *     异常：
 *          运行时异常（unchecked exception【非受检异常】）
 *          编译时异常（checked exception【受检异常】）
 *     回滚的默认机制:
 *          运行时异常：回滚
 *          编译时异常：不回滚
 *
 *    【可以指定哪些异常需要回滚】；@Transactional（timeout=3,rollbackFor={IoException.class})加上别的回滚异常(文件读写异常)
 *      所有异常都回滚 rollbackFor={Exception.class}
 *  也可以用@Transactional(timeout = 3,
 *  rollbackForClassName ={"java.lang.Exception","java.lang.RuntimeException"})
 *    【回滚 = 运行时异常 + 指定回滚异常】
 *
 * 7、noRollbackFor（同 noRollbackForClassName）：指明哪些异常不需要回滚。
 *    【不回滚 = 编译时异常 + 指定不回滚异常】
 *
 * 场景：用户结账，炸了以后，金额扣减回滚，库存不回滚。
 * 注意：【一定关注异常的传播链】
 * 实现：
 *    checkout(){
 *        //自己的操作；
 *        扣减金额： //REQUIRED
 *        扣减库存： //REQUIRES_NEW
 *    }
 *
 *  A {
 *      B(){  //REQUIRED
 *          F();//REQUIRES_NEW
 *          G();//REQUIRED
 *          H();//REQUIRES_NEW
 *      }
 *      C(){  //REQUIRES_NEW
 *         I();//REQUIRES_NEW
 *         J();//REQUIRED
 *      }
 *      D(){   //REQUIRES_NEW
 *          K();//REQUIRES_NEW
 *          L();//REQUIRES_NEW //点位2： 10/0； K,F,H,C(i,j) = ok, E整个代码走不到，剩下炸
 *      }
 *      E(){   //REQUIRED
 *          M();//REQUIRED
 *          //点位3：10/0；  F,H,C(i,j),D(K,L)= ok
 *          N();//REQUIRES_NEW
 *      }
 *
 *      int i = 10/0;  //点位1：C（I，J）,D(K，L) ，F，H,N= ok
 *  }
 *
 * @param username  用户名
 * @param bookId    图书id
 * @param buyNum    购买数量
 *
 * 传播行为：参数设置项也会传播：如果小事务和大事务公用一个事务，小事务要按照大事务的设置，小事务自己的设置失效
 */

/** @EnableTransactionManagement 开启基于注解的自动化事务管理
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
 * 在以下文件中演示：
 * spring-03-tx\src\main\java\com\atguigu\spring\tx\service\impl\UserServiceImpl.java
 */

/** jdbcTemplate.增删改查 
 * 
 * 在以下文件中演示：
 * spring-03-tx\src\test\java\com\atguigu\spring\tx\Spring03TxApplicationTests.java
 * spring-03-tx\src\main\java\com\atguigu\spring\tx\dao\BookDao.java
 */

/** HikariDataSource 是最快的数据源，但只是增删改查快，springboot 默认的数据源
 * DruidDataSource 是生态最丰富的数据源
 */

/** 操作数据库：
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
