package com.atguigu.spring.aop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/** 使用场景：
 * 1、日志记录【√】：
 * 2、事务管理【√】：
 * 3、权限检查【√】：
 * 4、性能监控：专业框架
 * 5、异常处理【√】：
 * 6、缓存管理【√】：
 * 7、安全审计：
 * 8、自动化测试：
 * 
 * 在aspect/aroundaspect中演示
 */

/** 环绕通知的一个bug：关于异常的
 * 环绕通知的一个bug，环绕通知里面的异常，外边的日志是不知道的，返回值就会是null
 * 但是我们不想要外边的日志返回null，而是直接抛异常，所以在环绕通知中需要在外边抛异常
 * 如果是事务来切面环绕通知，捕获不到异常就回滚不了事务了
 * 方法加个throws Throwable{} 然后在try中catch(Throwable e){throw e;}
 */

/** 感知通知和环绕通知对比
 * 感知通知：不会修改目标方法
 *      @Before：方法执行之前运行。
 *      @AfterReturning：方法执行正常返回结果运行。
 *      @AfterThrowing：方法抛出异常运行。
 *      @After：方法执行之后运行
 * 环绕通知：可以控制目标方法是否执行，修改目标方法参数、执行结果等。
 *      @Around：相当于上面4个结合 返回值是目标方法的返回值 
 *      
 * 在aspect/aroundaspect中演示
 */

/** @Order
 * 多切面的顺序：@Order(数字) 数字越小，优先级越高
 * 
 * 在aspect/authaspect,logaspect中演示
 */

/** @Pointcut
 * 切入点表达式的简化：@Pointcut  可以让后边的切入点表达式复用 不然每次都要写execution等
 * @Pointcut("execution(int com.atguigu.spring.aop.calculator.MathCalculator.*(..))")
 * public void pointCut(){}
 * 
 * @Before("pointCut()")
 * public void logStart(JoinPoint joinPoint){
 *  MethodSignature signature = (MethodSignature) joinPoint.getSignature();
 *  String name = signature.getName();
 *  System.out.println("【切面 - 日志】【" + name + "】开始：参数：" + Arrays.toString(joinPoint.getArgs()));
 * }
 * 
 * 在aspect/logaspect中演示
 */

/** 在切点表达式中，拿到连接点信息，返回值，异常信息
 * @Before("execution(xxx) && args(int)") public void xxx(JoinPoint joinpoint)
 * 其中joinpoint包装了当前目标方法的所有信息
 * 前置通知：
 * // 拿到方法全签名
 * MethodSignature signature = (MethodSignature) joinPoint.getSignature();
 * // 方法名
 * String name = signature.getName();
 * // 目标方法传来的参数值
 * Object[] args = joinPoint.getArgs();
 * 后置通知：可以拿到返回值
 * @AfterReturning(value ="execution(int com.atguigu.spring.aop.calculator.Mathcalculator.*(..))",
 * returning="result")//returning="result"获取目标方法返回值
 * public void logReturn(JoinPoint joinPoint,Object result){
 *  MethodSignature signature =(MethodSignature)joinPoint.getSignature();
 *  String name = signature.getName();
 *  System.out.println("【切面－日志】【"+name+"】返回：值："+result);
 *  }
 * 异常通知：可以拿到异常信息
 * @AfterThrowing(
 * value = "execution(int com.atguigu.spring.aop.calculator.MathCalculator.*(..))",
 * throwing = "e" //throwing="e" 获取目标方法抛出的异常
 * )
 * public void logException(Throwable e, JoinPoint joinPoint) {
 *  MethodSignature signature = (MethodSignature) joinPoint.getSignature();
 *  String name = signature.getName();
 *  System.out.println("【切面 - 日志】【" + name + "】异常：错误信息：【" + e.getMessage() + "】");
 * }
 * 在aspect/logaspect中演示
 */

/** 连接点,切入点,通知概念
 * 连接点：
 * 1. 程序执行的某个特定位置：如某个方法的执行前后，抛出异常时
 * 2. 通常是一个方法的执行
 * 
 * 切入点：
 * 1. 匹配连接点的条件
 * 2. 可以匹配多个连接点。一个连接点，可以被多个切入点匹配
 * 
 * 通知：
 * 1. 增强器，切面中的所有通知方法
 * 2. 增强器链：增强器被组织成一个链路放到集合中
 */

/** AOP的底层原理:
 * 1. Spring会为每个被切面切入的组件创建代理对象（SpringCGLIB创建的代理对象，无视接口）。
 * 2. 代理对象中保存了切面类里面所有通知方法构成的增强器链。
 * 3. 目标方法执行时，会先去执行增强器链中拿到需要提前执行的通知方法去执行。
 * 
 * 增强器链：
 * - 切面中的所有通知方法其实就是增强器。它们被组织成一个链路放到集合中。
 * - 目标方法真正执行前后，会去增强器链中执行。
 * - 在AOP切的代理对象中的第0个回调方法中，有一个advisedInterceptor，里面的advised里有增强器链。
 * - 再里边有advisors有几个切入表达式的写法，这个advisors就是增强器链。
 * 
 * 通知方法的执行顺序：
 * 1、正常链路：前置通知->目标方法->返回通知->后置通知
 * 2、异常链路：前置通知->目标方法->异常通知->后置通知
 */

/** 关于CGLIB代理对象和$符号
 * 弄好了某类的切面后，new 接口 然后调用getclass时，会发现有$符号
 * 也就是bean被aop切了就不是原来的自己了，而是代理对象
 * 比如class com.atguigu.spring.aop.calculator.impl.MathCalculatorImpl$$SpringCGLIB$$0
 * 这是由于Spring 的动态代理机制，使用了CGLIB代理，而不是JDK动态代理。
 * 动态代理是jdk，必须要有接口才能代理
 */

/** 加日志打印功能示例：
 * 1. 硬编码：在每个方法中都写日志打印
 * 2. 静态代理：
 * 定义：定义一个代理对象，包装这个组件。以后业务的执行，从代理开始，不直接调用组件：
 * 特点：定义期间就指定好了互相代理关系
 * 也就是实现一样的接口，调目标对象的方法，在方法前后加日志打印
 * 优点：同一种类型的所有对象都能代理
 * 缺点：范围太小了，只能负责部分接口代理功能
 * 在proxy/static/calculator中演示
 * 3. 动态代理（拦截器）：
 * 定义：在运行期间，根据需要，动态的指定代理关系。一个代理，代理很多对象
 * 缺点：不好写
 * 在test/mathtest 和 proxy/dynamic/dynamicproxy中演示
 * 4. aop
 * 步骤：
 * 1. 导入AOP 依赖 <dependency><groupId>org.springframework.boot</groupId><artifactId>spring-boot-starter-aop</artifactId></dependency>
 * 2. 编写切面 Aspect类 用@Aspect注解 和@Component注解
 * 3. 编写通知方法 public void xxx(){}
 * 4. 指定切入点表达式 
 * 何时？
 *      @Before 在方法执行之前执行
 *      @After 在方法执行之后执行
 *      @AfterReturning 在方法返回之后执行
 *      @AfterThrowing 在方法抛出异常之后执行
 * 何地？
 * 可以混一起 比如 @Before("execution(xxx) && args(int,int) && within(xxx)")
 * execution：中括号可以不写
 *      execution(方法的全签名：返回值类型 包名.类名.方法名(参数列表))
 *      execution([public] int [com.atguigu.spring.aop.MathCalculator].方法(int,参数类型))
 *      省略写法：@Before("execution(int *(int,int))") 在执行所有int类型，两个参数，返回值为int的方法前执行
 *      通配符：.. 表示任意个参数/代表多个层级(包位置)，* 表示任意类型，* 表示任意返回值类型
 *      最省略：* *(..)
 *      最好精确：* com.atguigu.spring.aop.MathCalculator.*(int,int)
 * args：只要方法中参数是这个就切
 *      @Before("args(int,int)") 在执行方法参数是int,int的方法前执行
 * @args: 只要方法中参数的注解是这个就切，两个参数的注解是，就切两个参数的注解
 *      @Before("@args(com.atguigu.spring.aop.annotation.Check)") 在执行方法参数的注解是@Check的方法前执行
 * within：在指定包下，指定类下，指定方法下
 *      @Before("within(com.atguigu.spring.aop.calculator.MathCalculator)") 在执行MathCalculator类中的方法前执行
 * @annotation：只要方法上有这个注解就切
 *      @Before("@annotation(com.atguigu.spring.aop.annotation.Check)") 在执行方法上有@Check注解的方法前执行
 * 5. 测试 AOP 动态织入
 * 在aspect/logaspect中演示
 */

/** 设计模式：依赖倒置
 * 依赖接口，而不是依赖实现。实现可能会经常变。
 * 控制反转@component的是实现类，依赖注入@Autowired的是接口。
 * 在test/Spring02AopApplicationTests中演示
 */

@SpringBootApplication
public class Spring02AopApplication {

    public static void main(String[] args) {
        SpringApplication.run(Spring02AopApplication.class, args);
    }

}
