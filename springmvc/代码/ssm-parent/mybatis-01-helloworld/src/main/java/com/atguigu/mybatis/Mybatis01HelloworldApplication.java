package com.atguigu.mybatis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

/** 传参 new BigDecimal("10000.00") 给mybatis的方法
 * 可以传入精准的数字，不会丢失精度
 * 要求是属性是BigDecimal类型 对应数据库的decimal类型
 */

/** @MapperScan("包名 xxx.mapper")
 * 只扫描mapper，那个mapper包下不要放其他的，全放mapper
 * 然后就可以不用@mapper注解来指定mapper了
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\config\MyBatisConfig.java
 */

/** mybatis逆向生成
 * 首先要下载MybatisX插件
 * idea连数据库，然后右键表，点击MybatisX-Generator
 * 1. module path: 生成在哪个模块下
 * 2. base package: 包名 生成在这个包的下面
 * 3. relative package: 生成包含类的包(指定包名)，在base package的下面生成
 * 4. 选lombok 选default all 点完成
 */

/** mybatis分页插件 设置参数 页数合理（传超过返回最后一页）
 * @Configuration
 * public class MyBatisConfig {
 *     @Bean
 *     PageInterceptor pageInterceptor() {
 *         // 1、创建 分页插件 对象
 *         PageInterceptor interceptor = new PageInterceptor();
 *         // 2、设置 参数
 *         Properties properties = new Properties();
 *         properties.setProperty("reasonable", "true");   // 页数合理（传第一页以前的返回第一页，传超过返回最后一页）
 *         interceptor.setProperties(properties);
 *         
 *         return interceptor;
 *     }
 * }
 * 
 */

/** mybatis分页插件 简单用法
 * @GetMapping("/emp/page")
 * public PageInfo getPage(@RequestParam("pageNum") Integer pageNum) {
 *     PageHelper.startPage(pageNum, 5);
 *     List<Emp> all = empService.getAll();
 *     return new PageInfo<>(all);
 * }
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\controller\OrderRestController.java
 */

/** mybatis分页插件 pagehelper 详细解释
 * 文档url：https://pagehelper.github.io/docs/howtouse
 * 1. 导包 <dependency><groupId>com.github.pagehelper</groupId><artifactId>pagehelper</artifactId><version>6.1.0</version></dependency>
 * 2. 配置分页插件
 * 在config包下创建MyBatisConfig.java
 * @Configuration
 * public class MyBatisConfig {
 *     @Bean
 *     PageInterceptor pageInterceptor() {
 *         // 1、创建 分页插件 对象
 *         PageInterceptor interceptor = new PageInterceptor();
 *         // 2、设置 参数
 *         // .......
 *         return interceptor;
 *     }
 * }
 * 
 * 3. 使用分页插件
 * @Test
 * void test01() {
 *     原理：拦截器；
 *       原业务底层：select * from emp;
 *     拦截做两件事：
 *     1）、统计这个表的总数量
 *     2）、给原业务底层SQL 动态拼接上 limit 0,5;
 * 怎么知道第二次getAll() （不会分页查询）的？
 * ThreadLocal:同一个线程共享数据
 * 1、第一个查询从ThreadLocal中获取到共享数据，执行分页
 * 2、第一个执行完会把ThreadLocal分页数据删除
 * 3、以后的查询，从ThreadLocal中拿不到分页数据，就不会分页
 * 
 * //后端收到前端传来的页码
 * //响应前端需要的数据：
 * //1、总页码、总记录数
 * //2、当前页码
 * //3、本页数据
 * PageHelper.startPage(pageNum: 3, pageSize: 5);
 * // 紧跟着 startPage 之后 的方法会执行分页 SQL 分页查询
 * List<Emp> all = empService.getAll();
 * for (Emp emp : all) {
 *     System.out.println(emp);
 * }
 * System.out.println("================");
 * List<Emp> all1 = empService.getAll();
 * System.out.println(all1.size());
 * 
 * // 以后转前端返回它
 * PageInfo<Emp> info = new PageInfo<>(all);
 * // 当前第几页
 * System.out.println("当前页码: " + info.getPageNum());
 * // 总页码
 * System.out.println("总页码: " + info.getPages());
 * // 总记录
 * System.out.println("总记录数: " + info.getTotal());
 * // 有没有下一页
 * System.out.println("有没有下一页: " + info.isHasNextPage());
 * // 有没有上一页
 * System.out.println("有没有上一页: " + info.isHasPreviousPage());
 * // 本页数据
 * System.out.println("本页数据: " + info.getList());
 * }
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\test\java\com\atguigu\mybatis\PageTest.java
 */

/** mybatis插件机制 -了解 不咋用得到
 * 在mybatis的jar包下的plugin的interceptor包下
 * 
 * MyBatis底层使用拦截器机制提供插件功能，方便用户在SQL执行
 * 前后进行拦截增强。
 * 拦截器：Interceptor
 * 拦截器可以拦截四大对象的执行
 * ParameterHandler：处理SQL的参数对象
 * ResultSetHandler：处理SQL的返回结果集
 * StatementHandler：数据库的处理对象，用于执行SQL语句
 * Executor：MyBatis的执行器，用于执行增删改查操作
 * 
 */

/** mybatis扩展 缓存机制
 * 缓存机制的理解：cpu三级缓存：  L1缓存：512KB  L2缓存：4MB  L3缓存：16MB  电脑越好缓存越大
 * 一级缓存：每个cpu里面都有自己的一级缓存，存即将要执行的指令。
 * 二级缓存：多个cpu共享一个二级缓存，存执行过的指令。
 * 三级缓存：多个cpu共享一个三级缓存，存执行过的指令。
 * 缓存都没就去内存拿  缓存的目的：缓存就是为了加速系统访问速度
 * 
 * 如果缓存中没有
 * 就要去数据库查询原始数据然后放到缓存
 * 
 * mybatis的缓存机制(比较不一样其他从一级开始)：先查二级缓存，二级缓存没有就查一级缓存，一级缓存没有就查数据库
 * 
 * MyBatis拥有二级缓存机制：
 * 一级缓存默认开启；事务级别：当前事务共享 @Transactional 默认可重复读  只会发一次sql  同一个事务，前面查了后面不用再查数据库
 * 一般用redis：二级缓存需要手动配置开启(只需要在mapper.xml中配置)：所有事务共享，一级缓存事务结束后，会把数据丢给二级缓存
 * 
 * 缓存中有就不用查数据库；
 * L1~LN:N级缓存
 * 数字越小离我越近，查的越快。存储越小，造价越高。
 * 数字越大离我越远，查的越慢。存储越大，造价越低。
 * 
 * 示例：一级缓存
 * 默认事务期间，会开启事务级别缓存；
 * 同一个事务期间，前面查询的数据，后面如果再要执行相同查询，会从一级缓存中获取数据，不会给数据库发送SQL
 * @Transactional 默认可重复读  只会发一次sql
 * public void find() {
 *     Emp empById = empMapper.getEmpById(1);
 *     System.out.println("员工：" + empById);
 *     System.out.println("================");
 * // 有时候缓存会失效（缓存不命中）。
 * // 失效几种情况：
 * // 1、查询的东西不一样。
 * // 2、两次查询之间，进行了一次增删改（由于增删改会引起数据库变化，Mybatis认为，数据有可能变了，它就要再发一次查询）
 *     Emp emp = empMapper.getEmpById(1);
 *     System.out.println("员工：" + emp);
 *     System.out.println("================");
 * }
 * 
 * 示例：二级缓存
 * 在mapper.xml中配置：
 *  <mapper namespace="com.atguigu.mybatis.mapper.EmpMapper">
 *     <cache/>   加上这个标签，表示这个mapper下的所有sql都会使用二级缓存
 * </mapper>
 * 底层是序列化，转成文件到磁盘中，如果对象没有实现序列化，会报错
 * @Data
 * public class Emp implements Serializable {
 *     // 开启驼峰命名自动映射封装
 *     private Integer id;
 *     private String empName;  // emp_name，驼峰命名规则
 *     private Integer age;
 *     private Double empSalary;  // emp_salary
 * }
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\bean\Emp.java
 * mybatis-01-helloworld\src\test\java\com\atguigu\mybatis\CacheTest.java
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\EmpMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\EmpMapper.xml
 */

/** 动态sql xml的转义字符
 * 原始字符转义字符 不要漏了;
 * 原始字符& 转义字符 &amp;
 * 原始字符< 转义字符 &lt;
 * 原始字符> 转义字符 &gt;
 * 原始字符" 转义字符 &quot;
 * 原始字符' 转义字符 &apos;
 * 
 * <update id="updateEmp">
 *    update t_emp
 *    <trim prefix="set" suffixOverrides=",">
 *        <if test="empName != null">
 *            emp_name = #{empName},
 *        </if>
 *        <if test="empSalary != null">
 *            emp_salary < #{empSalary},  这里用大于可以，用小于不行
 *            emp_salary &lt; #{empSalary},  换为转义字符
 *        </if>
 *        <if test="age != null">
 *            age = #{age}
 *        </if>
 *    </trim>
 *    where id = #{id}
 * </update>
 * 
 */

/** 动态sql 抽取可复用的sql片段 
 * sql：抽取可复用的sql片段
 * include：引用sql片段，refid属性：sql片段的id
 * 
 * <sql id="column_names">
 *     id, emp_name empName, age, emp_salary empSalary
 * </sql>
 * 
 * <select id="getEmpsById" resultType="com.atguigu.mybatis.bean.Emp">
 *     select
 *     <include refid="column_names"></include>
 *     from t_emp
 *     <if test="ids != null">
 *         <foreach collection="ids" item="id" separator="," open="where id IN (" close=")">
 *             #{id}
 *         </foreach>
 *     </if>
 * </select>
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\EmpDynamicSQLMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\EmpDynamicSQLMapper.xml
 */

/** mybatis开启事务
 * 分布式项目情况下，分布式事务很多不支持多SQL批量操作的回滚；
 * 在启动类上添加@EnableTransactionManagement
 * 因为mapper用不了@Transactional，所以用service再包装一次，调用mapper方法
 * @Service
 * public class EmpService {
 * 
 *     @Autowired
 *     EmpDynamicSQLMapper empDynamicSQLMapper;
 * 
 *     @Transactional
 *     void updateBatch(List<Emp> emps) {
 *         empDynamicSQLMapper.updateBatchEmp(emps);
 *         int i = 1/0;
 *     }
 * }
 * 
 */

/** 动态sql <foreach> 增删改(要开启支持多SQL)查
 * mybatis接口方法：List<Emp> getEmpsByIdIn(List<Integer> ids);
 * 示例一个个传参：一个一个查询
 * <select id="getEmpsByIdIn" resultType="com.atguigu.mybatis.bean.Emp">
 *      select * from t_emp where id IN (#{ids[0]},#{ids[1]},#{ids[2]})
 * </select>
 * 示例：foreach遍历集合传参，批量查询
 * <!--
 * foreach: 遍历List,Set,Map,数组
 * collection: 指定要遍历的集合名
 * item: 将当前遍历出的元素赋值给指定的变量
 * separator: 指定在每次遍历时，元素之间拼接的分隔符
 * open: 指定在遍历的开始拼接的sql，不开始遍历就没这个
 * close: 指定在遍历的结束拼接的sql，不开始遍历就没这个
 * -->
 * <select id="getEmpsById" resultType="com.atguigu.mybatis.bean.Emp">
 *     select * from t_emp
 *     <foreach collection="ids" item="id" separator="," open="where id IN (" close=")">
 *         #{id}
 *     </foreach>
 * </select>
 * 示例：foreach遍历集合传参加上if判断 为空就查所有，批量查询
 * <select id="getEmpsById" resultType="com.atguigu.mybatis.bean.Emp">
 *     select * from t_emp
 *     <if test="ids != null">
 *         <foreach collection="ids" item="id" separator="," open="where id IN (" close=")">
 *             #{id}
 *         </foreach>
 *     </if>
 * </select>
 * 示例：foreach遍历对象集合，批量插入对象
 * mapper方法：void addEmps(List<Emp> emps);
 * <insert id="addEmps">
 *     insert into t_emp (emp_name,emp_salary,age) values
 *     <foreach collection="emps" item="emp" separator=",">
 *         (#{emp.empName},#{emp.empSalary},#{emp.age})
 *     </foreach>
 * </insert>
 * 示例：foreach遍历对象集合，批量更新对象  这里是一条一条的用;接一起（发一次sql效率高），数据库默认不支持
 * 在application.properties中配置：批量多个SQL
 * jdbc:mysql:///mybatis-example?allowMultiQueries=true
 * allowMultiQueries:允许多个SQL用；隔开，批量发送给数据库执行
 * application.properties:
 * spring.datasource.url=jdbc:mysql:///mybatis-example?allowMultiQueries=true
 * 
 * mapper方法：void updateEmps(List<Emp> emps);
 * <update id="updateBatchEmp">
 *     <foreach collection="emps" item="e" separator=";">
 *         update t_emp
 *         <set>
 *             <if test="e.empName != null">
 *                 emp_name = #{e.empName},
 *             </if>
 *             <if test="e.empSalary != null">
 *                 emp_salary = #{e.empSalary},
 *             </if>
 *             <if test="e.age != null">
 *                 age = #{e.age}
 *             </if>
 *         </set>
 *         where id = #{e.id}
 *     </foreach>
 * </update>
 * 
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\EmpDynamicSQLMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\EmpDynamicSQLMapper.xml
 */

/** 动态sql <choose> <when> <otherwise> 
 * <choose> <when> <otherwise> 示例： 
 * <select id="queryEmpByNameAndSalaryWhen" resultType="com.atguigu.mybatis.bean.Emp">
 *     select * from t_emp
 *     <where>
 *         <choose>     开始进入分支选择
 *             <when test="name != null">       当这个条件成立时，执行这个
 *                 emp_name= #{name}
 *             </when>
 *             <when test="salary > 3000">   当这个条件成立时，执行这个
 *                 emp_salary = #{salary}
 *             </when>
 *             <otherwise>   当以上条件都不成立时，执行这个
 *                 id = 1
 *             </otherwise>
 *         </choose>
 *     </where>
 * </select>
 * 
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\EmpDynamicSQLMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\EmpDynamicSQLMapper.xml
 */

/** 动态sql <if> <where> <set> <trim>
 * 1. <if>：示例：判断参数是否为空，如果不为空则拼接sql 但是第一个参数为空时，第二个带and也会拼接，最后有bug
 * <select id="queryEmpByNameAndSalary" resultType="com.atguigu.mybatis.bean.Emp">
 *    select * from t_emp where
 *    <if test="name != null">
 *        emp_name= #{name}
 *    </if>
 *    <if test="salary != null">
 *        and emp_salary = #{salary}
 *    </if>
 * </select>
 * 2. <where>：示例：解决where后面语法错误问题(多and，or，无任何条件多where)
 * <select id="queryEmpByNameAndSalary" resultType="com.atguigu.mybatis.bean.Emp">
 *     select * from t_emp
 *     <where>
 *         <if test="name != null">
 *             emp_name= #{name}
 *         </if>
 *         <if test="salary != null">
 *             and emp_salary = #{salary}
 *         </if>
 *     </where>
 * </select>
 * 3. <set>：示例：解决set后面语法错误问题(多逗号) 比如：update t_emp set age=?, where id=?
 * <update id="updateEmp">
 *     update t_emp
 *     <set>
 *         <if test="empName != null">
 *             emp_name = #{empName},
 *         </if>
 *         <if test="empSalary != null">
 *             emp_salary = #{empSalary},
 *         </if>
 *         <if test="age != null">
 *             age = #{age}
 *         </if>
 *     </set>
 *     where id = #{id}
 * </update>
 * 4. <trim>：示例：自定义，可以替换<where><set>问题
 * <!--
 * prefix: 前缀；如果标签体中有内容，就给它们拼一个前缀
 * suffix: 后缀
 * prefixOverrides: 前缀覆盖；标签体中最终生成的字符串，如果以指定前缀开头，就覆盖成空串
 * suffixOverrides: 后缀覆盖
 * -->
 * <select id="queryEmpByNameAndSalary" resultType="com.atguigu.mybatis.bean.Emp">
 *     select * from t_emp
 *     <trim prefix="where" prefixOverrides="and || or"> 如果where后有and或or，就覆盖成空串
 *         <if test="name != null">
 *             emp_name= #{name}
 *         </if>
 *         <if test="salary != null">
 *             and emp_salary = #{salary}
 *         </if>
 *     </trim>
 * </select>
 * 示例：用来处理set后缀多逗号问题 例如：update t_emp set age=?, where id=? 
 * <update id="updateEmp">
 *     update t_emp
 *     <trim prefix="set" suffixOverrides="," suffix="where id = #{id}"> 处理完后给后面拼接where id =#{id}
 *         <if test="empName != null">
 *             emp_name = #{empName},
 *         </if>
 *         <if test="empSalary != null">
 *             emp_salary = #{empSalary},
 *         </if>
 *         <if test="age != null">
 *             age = #{age}
 *         </if>
 *     </trim>
 * 也可以把suffix="where id = #{id}"去掉，在这加上where id = #{id}
 * </update>
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\EmpDynamicSQLMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\EmpDynamicSQLMapper.xml
 */

/** 多对多咋查？
 * 多对多相当于两个一对多
 * 
 * 示例：
 * 查询所有客户所有订单
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\bean\Customer.java
 * mybatis-01-helloworld\src\test\java\com\atguigu\mybatis\StepTest.java
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\CustomerMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\CustomerMapper.xml
 */

/** 延迟加载 -了解
 * 分步查询有时候并不需要立即运行，我们希望在用到的时候再去查询，可以开启延迟加载的功能
 * 例如：查询订单的时候，并不需要立即查询客户，可以在用到客户的时候再去查询
 * 我们需要的是订单的信息，但是在分布查询会查到客户的情况，可以开启延迟加载
 * 在application.properties中配置：
 * mybatis.configuration.lazy-loading-enabled=true  # 开启延迟加载
 * mybatis.configuration.aggressive-lazy-loading=false # 是不是立即延迟加载
 * 
 * 示例：  先用到订单信息，再用到客户信息（延迟加载，这时才会发起对客户查询的sql）
 * Order order = orderCustomerStepMapper.getOrderByIdAndCustomerStep(1L);
 * // System.out.println("order = " + order); 此时执行3条sql，因为order的toString方法会调用getCustomer方法
 * System.out.println("order = " + order.getAmount()); // 此时只会执行一条sql
 * System.out.println("==============================");
 * Thread.sleep(3000);
 * //用到客户信息了，才会继续发送分步查询sql
 * Customer customer = order.getCustomer();
 * System.out.println("customer = " + customer.getCustomerName());
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\bean\Customer.java
 * mybatis-01-helloworld\src\test\java\com\atguigu\mybatis\StepTest.java
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\OrderCustomerStepMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\OrderCustomerStepMapper.xml
 */

/** 超级分布查询 按照id查询订单以及下单的客户以及此客户的所有订单
 * 最后一步要截止住：比如不能再自定义结果集然后有到关联
 * 查询订单 => 自定义结果集订单包含客户 => 查询客户 => 自定义结果集客户包含其他单
 * 如果此时 自定义结果集客户包含其他单 =>  自定义结果集订单(根据其他单号查) 就会导致死循环，方法无终结，stackoverflow
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\bean\Customer.java
 * mybatis-01-helloworld\src\test\java\com\atguigu\mybatis\StepTest.java
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\OrderCustomerStepMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\OrderCustomerStepMapper.xml
 */

/** MyBatis自动分布查询： 按照id查询订单以及下单的客户
 * 分步查询：自动做两步=按照id查询订单+查询下单的客户
 * 客户的bean
 * @Data
 * public class Customer {
 *     private Long id;
 *     private String customerName;
 *     private String phone;
 *     //保存所有订单
 *     private List<Order> orders;
 * }
 * 客户的mapper
 * <!-- 按照id查询客户 -->
 * <select id="getCustomerById" resultType="com.atguigu.mybatis.bean.Customer">
 *     select *
 *     from t_customer
 *     where id = #{id}
 * </select>
 * 
 * <!-- 分步查询：自定义结果集，封装订单的分步查询 -->
 * <resultMap id="OrderCustomerStepRM" type="com.atguigu.mybatis.bean.Order">
 *     <id column="id" property="id"/>
 *     <result column="address" property="address"/>
 *     <result column="amount" property="amount"/>
 *     <result column="customer_id" property="customerId"/>
 *     <!-- customer属性关联一个对象，启动下一次查询，查询这个客户 -->
 *     <association property="customer"
 *                  select="com.atguigu.mybatis.mapper.OrderCustomerStepMapper.getCustomerById"
 *                  column="customer_id"/>
 * </resultMap>
 * 
 * <select id="getOrderByIdAndCustomerStep" resultMap="OrderCustomerStepRM">
 *     select *
 *     from t_order
 *     where id = #{id}
 * </select>
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\bean\Customer.java
 * mybatis-01-helloworld\src\test\java\com\atguigu\mybatis\StepTest.java
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\OrderCustomerStepMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\OrderCustomerStepMapper.xml
 */

/** MyBatis自动分步查询机制：自动调用  按照id查询客户以及他下的所有订单
 * 分步查询：自动做两步 = 查询客户 + 查询客户下的订单 会发两次sql
 * 示例：查询客户以及他下的所有订单
 * 客户的bean
 * @Data
 * public class Customer {
 *     private Long id;
 *     private String customerName;
 *     private String phone;
 *     //保存所有订单
 *     private List<Order> orders;
 * }
 * 客户的mapper
 * <!-- 按照客户id查询他的所有订单 -->
 * <select id="getOrdersByCustomerId" resultType="com.atguigu.mybatis.bean.Order">
 *     select *
 *     from t_order
 *     where customer_id = #{cId}
 * </select>
 * 
 * <!--   分步查询的自定义结果集： -->
 * <resultMap id="CustomerOrdersStepRM" type="com.atguigu.mybatis.bean.Customer">
 *     <id column="id" property="id"></id>
 *     <result column="customer_name" property="customerName"></result>
 *     <result column="phone" property="phone"></result>
 *     <collection property="orders"
 *                 select="com.atguigu.mybatis.mapper.OrderCustomerStepMapper.getOrdersByCustomerId"
 *                 column="id">
 *     </collection>
 *     <!--    这个collection会用getOrdersByCustomerId方法查，结果再来封装orders属性 告诉MyBatis，封装 orders 属性的时候，是一个集合，
 *             但是这个集合需要调用另一个 方法 进行查询；select：来指定我们要调用的另一个方法
 *             column：来指定我们要调用方法时，把哪一列的值作为传递的参数，交给这个方法
 *                1）、column="id"： 单传参：id传递给方法
 *                2）、column="{cid=id,name=customer_name}"：多传参（属性名=列名）；
 *                     cid=id：cid是属性名，它是id列的值
 *                     name=customer_name：name是属性名，它是customer_name列的值
 *     -->
 * </resultMap>
 * 
 * <select id="getCustomerByIdAndOrdersStep" resultMap="CustomerOrdersStepRM">
 *     select *
 *     from t_customer
 *     where id = #{id}
 * </select>
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\bean\Customer.java
 * mybatis-01-helloworld\src\test\java\com\atguigu\mybatis\StepTest.java
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\OrderCustomerStepMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\OrderCustomerStepMapper.xml
 */

/** 分步查询 - 了解  按照id查询客户以及他下的所有订单
 * 原生分布：需要手动调用两次查询
 * 示例：查询客户以及他下的所有订单
 * 根据id查询客户，再根据客户id查询订单，最后把订单和客户组合一起
 * // 1. 按照id查询客户
 * Customer customer = orderCustomerStepMapper.getCustomerById(1L);
 * // 2. 根据客户id查询订单
 * List<Order> orders = orderCustomerStepMapper.getOrdersByCustomerId(customer.getId());
 * // 3. 组合一起
 * customer.setOrders(orders);
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\bean\Customer.java
 * mybatis-01-helloworld\src\test\java\com\atguigu\mybatis\StepTest.java
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\OrderCustomerStepMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\OrderCustomerStepMapper.xml
 */

/** 自定义结果集-ResultMap(mybatis返回值拓展) 一对一和一对多（用订单表和客户表举例子）按照订单id查询客户和按照id查询客户以及他下的所有订单
 * id标签：必须指定主键列映射规则
 * result标签：指定普通列映射规则
 * collection标签：指定自定义集合封装规则
 * association标签：指定自定义对象封装规则
 * 示例：一对一 按照订单id查询客户（订单表和客户表） 用association：
 * 订单的bean
 * @Data
 * public class Order {
 *     private Long id;
 *     private String address;
 *     private BigDecimal amount;
 *     private Long customerId;
 *     // 订单对应的客户
 *     private Customer customer;
 * }
 * 订单的mapper 
 * <resultMap id="OrderRM" type="com.atguigu.mybatis.bean.Order">
 *     <id column="id" property="id"/>
 *     <result column="address" property="address"/>
 *     <result column="amount" property="amount"/>
 *     <result column="customer_id" property="customerId"/>
 *     <!-- 一对一关联封装 -->
 *     <association property="customer" javaType="com.atguigu.mybatis.bean.Customer">
 *         <id column="c_id" property="id"/>
 *         <result column="customer_name" property="customerName"/>
 *         <result column="phone" property="phone"/>
 *     </association>
 * </resultMap>
 * 这时要用resultMap 而不是resultType，因为对象里有对象
 * //<select id="getOrderByIdWithCustomer" resultType="com.atguigu.mybatis.bean.Order">
 * <select id="getOrderByIdWithCustomer" resultMap="OrderRM">
 *     select o.*, 
 *            o.id as c_id, 
 *            c.customer_name, 
 *            c.phone
 *     from t_order o
 *     left join t_customer c on o.customer_id = c.id
 *     where o.id = #{id}
 * </select>
 * left join 左连接，把左边的表(t_order)全部查出来，右边的表(t_customer)如果有就查，没有就null
 * on 连接条件 o.customer_id = c.id表示
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\bean\Order.java
 * mybatis-01-helloworld\src\test\java\com\atguigu\mybatis\JoinQueryTest.java
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\OrderMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\OrderMapper.xml
 * 示例：一对多 按照id查询客户以及下的所有订单（客户表和订单表）用 collection：
 * @Data
 * public class Customer {
 *     private Long id;
 *     private String customerName;
 *     private String phone;
 *     // 保存所有订单
 *     private List<Order> orders;
 * }
 * 
 * <resultMap id="CutomerRM" type="com.atguigu.mybatis.bean.Customer">
 *     <id column="c_id" property="id"></id>
 *     <result column="customer_name" property="customerName"></result>
 *     <result column="phone" property="phone"></result>
 * <!--
 * collection：说明 一对N 的封装规则
 * ofType: 集合中元素的类型
 * -->
 *     <collection property="orders" ofType="com.atguigu.mybatis.bean.Order">
 *         <id column="id" property="id"></id>
 *         <result column="address" property="address"></result>
 *         <result column="amount" property="amount"></result>
 *         <result column="c_id" property="customerId"></result>
 *     </collection>
 * </resultMap>
 * 
 * <select id="getCustomerByIdWithOrders" resultMap="CutomerRM">
 *     select c.id c_id,
 *            c.customer_name,
 *            c.phone,
 *            o.*
 *     from t_customer c
 *     left join t_order o on c.id = o.customer_id
 *     where c.id = #{id}
 * </select>
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\bean\Customer.java
 * mybatis-01-helloworld\src\test\java\com\atguigu\mybatis\JoinQueryTest.java
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\CustomerMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\CustomerMapper.xml
 */

/** n对n的概念
 * 1. 一对一
 * 例子：订单表 -> 客户表
 * 2. 一对多 外键放多的一方
 * 例子：客户表 -> 订单表  外键放订单表里
 * 3. 多对多 如果是多对多需要一个中间表
 * 例子：学生表 -> 老师表  外键放学生_老师表 (sid tid)
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\EmpParamMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\EmpParamMapper.xml
 */

/** MyBatis返回值
 * 1. 返回普通数据
 * - 返回基本类型、普通对象都只需要在 resultType 中声明返回值类型全类名即可
 * - 对象封装建议全局开启驼峰命名规则：mapUnderscoreToCamelCase = true;
 *   例如：a_column 会被映射为bean的 aColumn 属性
 * 示例1：返回自定义对象
 * <select id="getEmp" resultType="com.atguigu.mybatis.entity.Employee">
 *     select * from 't_emp' where id = #{id}
 * </select>
 * 示例2：返回基本类型
 * <select id="countEmp" resultType="java.lang.Long">
 *     select count(*) from 't_emp'
 * </select>
 * 小提示：（有便利别名但不用）MyBatis 为 java.lang 下的很多数据类型都起了别名，只需要用Long, String, Double 等这些表示即可，不用写全类名
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\EmpReturnValueMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\EmpReturnValueMapper.xml
 * 2. 返回List、Map
 * ①. 返回集合：resuLtType="集合中元素全类名"
 * 比如：mapper：List<Emp> 配置文件：resultType="com.atguigu.mybatis.bean.Emp
 * ②. 返回map：
 * @MapKey("id") 加在返回值为map的方法上，指定用哪个字段作为map的key
 * 如果不加resuLtType，默认返回hashmap，列名+值
 * Map<Integer, Emp> resultType="com.atguigu.mybatis.bean.Emp"
 * 返回的是Emp{id=1, name="张三", age=30},Emp{id=2, name="李四", age=25},Emp{id=3, name="王五", age=35}
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\EmpReturnValueMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\EmpReturnValueMapper.xml
 * 3. 自定义结果集(1-1,1-N)
 * mybatis先用无参构造器，然后封装规则JavaBean中的属性名去数据库表中找对应列名的值。一一映射封装。
 * 和数据库对不上的字段封装为null，
 * 如何解决？
 * JavaBean和数据库一样【不推荐】
 * 使用列别名
 * 使用驼峰命名自动映射：
 * 使用ResuLtMap（自定义结果集）
 * 
 * 数据库的字段 如果和 Bean的属性 不能一一对应, 有两种办法:
 * ①、如果符合驼峰命名, 则开启驼峰命名规则
 * ②、编写自定义结果集 (ResultMap) 进行封装
 * 示例：
 * <resultMap id="EmpResultMap" type="com.atguigu.mybatis.entity.Employee">
 *   <id column="emp_id" property="empId"/>
 *   <result column="emp_name" property="empName"/>
 *   <result column="emp_salary" property="empSalary"/>
 * </resultMap>
 * 
 * 示例：自定义结果集
 * <resultMap id="EmpRM" type="com.atguigu.mybatis.bean.Emp">
 *   <!-- id: 声明主键映射规则 -->
 *   <id column="id" property="id"/>
 *   <!-- result: 声明普通列映射规则 -->
 *   <result column="emp_name" property="empName" javaType="java.lang.String" jdbcType="VARCHAR"/>
 *   <result column="age" property="age"/>
 *   <result column="emp_salary" property="empSalary"/>
 * </resultMap>
 * <select id="getEmpById" resultMap="EmpRM">
 *     select *
 *     from t_emp
 *     where id = #{id}
 * </select>
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\bean\Emp.java
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\EmpReturnValueMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\EmpReturnValueMapper.xml
 * 4. 分步查询
 * 5. 延迟加载
 * 
 * 最佳实践：
 * 1、开启驼峰命名
 * 2、1搞不定的，用自定义映射(ResultMap)(自定义结果集)
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\EmpReturnValueMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\EmpReturnValueMapper.xml
 */

/** MyBatis参数取值
 * 1. 单个参数 - 普通类型
 *    示例: getEmploy(Long id)
 *    取值方式: #{变量名}
 * 2. 单个参数 - List类型
 *    示例: getEmploy(List<Long> id)
 *    取值方式: #{变量名[0]}
 * 3. 单个参数 - 对象类型
 *    示例: addEmploy(Employ e)
 *    取值方式: #{对象中属性名}
 * 4. 单个参数 - Map类型
 *    示例: addEmploy(Map<String,Object> m)
 *    取值方式: #{map中属性名}
 * 5. 多个参数 - 无@Param
 *    示例: getEmploy(Long id,String name)
 *    取值方式: #{变量名} // 新版兼容
 * 6. 多个参数 - 有@Param
 *    示例: getEmploy(@Param("id")Long id, @Param("name")String name)
 *    取值方式: #{param指定的名}
 * 7. 扩展用法
 *    示例: getEmploy(@Param("id")Long id,
 *                   @Param("ext")Map<String,Object> m,
 *                   @Param("ids")List<Long> ids,
 *                   @Param("emp")Employ e)
 *    取值方式: 
 *    - #{id}
 *    - #{ext.name}, #{ext.age}
 *    - #{ids[0]}, #{ids[1]}
 *    - #{e.email}, #{e.age}
 *
 * 最佳实践：即使只有一个参数，也用 @Param 指定参数名
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\EmpParamMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\EmpParamMapper.xml
 */

/** 如果方法中传参想传其他表的名字用#{}会报错，要用${}
 * 是${}有sql注入风险，如果要传表名，做一个防sql注入处理，可以让ai生成防sql注入的参数校验等等
 * from ${tableName}
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\test\java\com\atguigu\mybatis\Mybatis01HelloworldApplicationTests.java
 */

/** 配置文件中传参 #{}和${}
 * #{}: 会做预编译处理，防止sql注入
 * 底层：
 * PreparedStatement preparedStatement = connection.prepareStatement("select * from t_emp where id = ?");
 * preparedStatement.setInt(1, 2);
 * 如果刻意传特殊的值没啥事
 * ${}: 不会做预编译处理，拼接方式，会有sql注入风险
 * 底层：
 * Statement statement = connection.createStatement();
 * statement.execute("select * from t_emp where id = " + 2);
 * sql注入风险： '' or 1=1 or 1=''
 * String sql2 = "select * from user where username = 'admin' and password = '' or 1=1 or 1='' ";
 */

/** 开启自动驼峰命名
 * @Data
 * public class Emp {
 *     // 开启配置后会自动驼峰命名
 *     private Integer id;
 *     private String empName;  // emp_name: 数据库的字段名称}
 * 
 * 在application.properties中配置：
 * mybatis.configuration.map-underscore-to-camel-case=true
 */

/** 让mybatis生成的啥sql显示出来，弄日志
 * 加入包名和logging.level到配置文件中：
 * 在application.properties配置logging.level.com.atguigu.mybatis.mapper=debug
 */

/** 初次实现简单的增删改查
 * 演示了：
 * 1. 增加数据可以返回id
 * 2. 数据库字段名和类属性名不一致
 * 3. 方法中传对象，sql语句中参数用对象里边的属性（会自动做映射）
 * 4. 方法的返回类型为集合List<Emp>，配置返回类型为类Emp
 * 
 * 在以下文件中演示：
 * mybatis-01-helloworld\src\main\java\com\atguigu\mybatis\mapper\EmpMapper.java
 * mybatis-01-helloworld\src\main\resources\mapper\EmpMapper.xml
 */

/** 为什么@Mapper接口而不搞实现类？
 * MyBatis会为每个Mapper接口创建的代理对象
 */

/** 初次使用mybatis和mybatisx插件：
 * 在dao下，创建一个接口EmpMapper.java（不用实现）
 * @Mapper  // 告诉Spring，这是MyBatis操作数据库使用的接口
 * public interface EmpMapper {
 *     // 没有使用
 *     Emp getEmpById(Integer id);
 * }
 * 然后通过mybatisx插件，按alt+enter键，[MybatisX]Generate mapper of xml生成到resources/mapper/EmpMapper.xml文件
 * xml路劲：resources/mapper/EmpMapper.xml
 * 
 * 还要在application.properties中配置mybatis 指定mapper文件位置
 * mybatis.mapper-locations=classpath:mapper/**.xml
 */

/** 使用mybatis
 * 步骤：
 * 1、导入mybatis依赖
 * 2、配置数据源信息
 * 3、编写一个JavaBean对应数据库一个表模型
 * 4、以前: Dao接口 --> Dao实现 --> 标注 @Repository注解（用jdbcTemplate）
 *    现在: Mapper接口 --> Mapper.xml实现;  --> 标注 @Mapper注解
 *      安装mybatisx插件，自动为 mapper类生成 mapper文件
 *      在mapper文件中配置方法的实现sql
 * 5、告诉MyBatis去哪里找Mapper文件；mybatis.mapper-locations=classpath:mapper/**.xml
 * 6、编写单元测试
 */
@EnableTransactionManagement
@SpringBootApplication
public class Mybatis01HelloworldApplication {

    public static void main(String[] args) {
        SpringApplication.run(Mybatis01HelloworldApplication.class, args);
    }

}
