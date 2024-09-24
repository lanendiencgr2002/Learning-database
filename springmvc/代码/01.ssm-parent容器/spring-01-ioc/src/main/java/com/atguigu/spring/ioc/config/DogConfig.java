package com.atguigu.spring.ioc.config;

import ch.qos.logback.core.CoreConstants;
import com.atguigu.spring.ioc.bean.Dog;
import com.atguigu.spring.ioc.bean.Person;
import com.atguigu.spring.ioc.service.UserService;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;


/**
 * 第三方组件想要导入容器中：没办法快速标注分层注解
 * 1、@Bean：自己new，注册给容器
 * 2、@Component 等分层注解
 * 3、@Import：快速导入组件
 */
//@Configuration
public class DogConfig {
//    @Lazy //单例模式，可以继续调整为懒加载
//    @ConditionalOnMissingBean(value = {UserService.class}) //当容器中没有UserService这个组件的时候，注册一个
//    注：只要容器中有这样类型的person类(中有@Bean('joseph')组件，虽然(bean)没单独注册到容器中，但是只要person类其中有注册到容器中就判定为true)
//    @ConditionalOnMissingBean(name="joseph",value = {Person.class}) //当容器中没有Person这个组件的时候，注册一个
//    @ConditionalOnBean(name = "bill") //当容器中有Person这个组件的时候，注册一个
//    @ConditionalOnResource(resources="classpath:haha.abc") //当容器中有haha.abc这个资源的时候，注册一个
    // Spring在底层会有多组件名字判定bug。 （涉及到他名字的同一类中，但是没有注册到容器中，所以判定为true）
//    @ConditionalOnMissingBean(name="joseph")
    @Bean
    public Dog dog01(){
        Dog dog = new Dog();
        dog.setName("大狗");
        return dog;
    }

    @Bean
    public Dog dog02(){
        Dog dog = new Dog();
        dog.setName("2狗");
        return dog;
    }

}
