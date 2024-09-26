package com.atguigu.spring.aop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * 使用场景：
 * 1、日志记录【√】：
 * 2、事务管理【√】：
 * 3、权限检查【√】：
 * 4、性能监控：专业框架
 * 5、异常处理【√】：
 * 6、缓存管理【√】：
 * 7、安全审计：
 * 8、自动化测试：
 * 在aspect/aroundaspect中演示
 */

/*
 * 环绕通知的一个bug，环绕通知里面的异常，外边的日志是不知道的，返回值就会是null
 * 但是我们不想要外边的日志返回null，而是直接抛异常，所以在环绕通知中需要在外边抛异常
 * 如果是事务来切面环绕通知，捕获不到异常就回滚不了事务了
 * 方法加个throws Throwable{} 然后在try中catch(Throwable e){throw e;}
 */

/*
 * 感知通知：不会修改目标方法
 *      @Before：方法执行之前运行。
 *      @AfterReturning：方法执行正常返回结果运行。
 *      @AfterThrowing：方法抛出异常运行。
 *      @After：方法执行之后运行
 * 环绕通知：可以控制目标方法是否执行，修改目标方法参数、执行结果等。
 *      @Around：相当于上面4个结合 返回值是目标方法的返回值 
 *      在aspect/aroundaspect中演示
 * 
 */

@SpringBootApplication
public class Spring02AopApplication {

    public static void main(String[] args) {
        SpringApplication.run(Spring02AopApplication.class, args);
    }

}
