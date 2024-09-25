package com.atguigu.spring.aop.aspect;


import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
/* 
 * 弄好了某类的切面后，new 接口 然后调用getclass时，会发现有$符号
 * 比如class com.atguigu.spring.aop.calculator.impl.MathCalculatorImpl$$SpringCGLIB$$0
 * 这是由于Spring 的动态代理机制，使用了CGLIB代理，而不是JDK动态代理。
 * 动态代理是jdk，必须要有接口才能代理
 */
@Order(100)
@Aspect
@Component 
public class AuthAspect {

    @Pointcut("execution(int com.atguigu.spring.aop.calculator.MathCalculator.*(..))")
    public void pointCut(){};



    @Before("pointCut()")
    public void before(){
        System.out.println("【切面 - 权限】前置");
    }

    @After("pointCut()")
    public void after(){
        System.out.println("【切面 - 权限】后置");
    }

    @AfterReturning("pointCut()")
    public void afterReturning(){
        System.out.println("【切面 - 权限】返回");
    }

    @AfterThrowing("pointCut()")
    public void afterThrowing(){
        System.out.println("【切面 - 权限】异常");
    }

}
