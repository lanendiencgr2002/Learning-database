package com.atguigu.spring.ioc;

import ch.qos.logback.core.CoreConstants;
import com.atguigu.spring.ioc.bean.Car;
import com.atguigu.spring.ioc.bean.Cat;
import com.atguigu.spring.ioc.bean.Dog;
import com.atguigu.spring.ioc.bean.Person;
import com.atguigu.spring.ioc.controller.UserController;
import com.atguigu.spring.ioc.dao.DeliveryDao;
import com.atguigu.spring.ioc.dao.UserDao;
import com.atguigu.spring.ioc.service.HahaService;
import com.atguigu.spring.ioc.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

/* @Profile("环境标识")多数据源也是特殊的条件注解，基础是@Conditional
 * 可以标在方法和类上
 * 当这个环境被激活的时候，才会加入如下组件  默认是default环境 可以指定多个环境{x1,x2}
 * 在配置文件中指定环境：spring.profiles.active=x1,x2 //spring.profiles.active=dev
 */

/*
 * ResourceUtils：获取资源文件
 * 例子：File file = ResourceUtils.getFile("classpath:abc.jpg");
 * 还可以传url:File file = ResourceUtils.getFile("https://www.baidu.com/abc.jpg");
 */
/* 
 * @value在@Component/Service（将类标识为组件）等注解上，用于给组件的属性赋值
 * @value('字面值')：直接复制 
 * 例如：@Value("123") public int num;
 * @Value("${自定义的属性名}:取不到的默认值自动转类型")：从配置文件中获取值 
 * 默认application.properties文件在resources目录下 
 * 可以用@PropertySource("classpath:自定义的配置文件名")指定配置文件位置，加在类上面
 * 如果用别的jar包下的配置文件，可以用@PropertySource("classpath*:xxx/配置文件名")指定
 * 例如@Value("${dog.age}") public String osName; 在application.properties中配置dog.age=123
 * @value("#{}")：SpEL表达式填可执行代码，获取容器中的组件对象 最终值返回给类型变量
 *   //T()：获取类对象 .randomUUID调用类中的静态方法
 * 例如：@Value("#{T(java.util.UUID).randomUUID()}") public Dog dog; 自动生成随机的uuid
 * 更多写法：https://docs.spring.io/spring-framework/reference/core/expressions.html
 */
/* 构造器注入：在dao/userdao下演示
 * 在@controller/Repository/Service/Component等分层注解上都可以使用
 * 构造方法里边传入的参数，spring会自动去容器中找到对应的bean（可加@Autowire注解可不加），然后注入到构造方法 
 * 或者用setXxx方法注入，在方法上使用@Autowired注解（不加也可以），可以用@Qualifier("自定义bean的名字")指定bean的名字+变量
 * */
// @Repository如同@controller等（将类标识为组件） 在dao的userdao中演示
// @Resource在service的userservice中演示
// @primary 在config中的personconfig中演示 在@Bean上面，用@Autowire时，存在多个类型一样名字不同的组件，会优先用这个注解的
// @Qualifier("自定义bean的名字（@bean public 类 '对象名'{}）")在@Autowire上面用，类型多个可以指定bean名字在service中的userservice中演示
/* 
 * @Autowired先根据类型去找，如果类型一样，再根据名字去找，找不到报错
 * 可以用@Qualifier("自定义bean的名字（@bean public 类 '对象名'{}）")指定bean的名字+变量
 * 或者一个类型多个名字中，可以在bean的时候给那些bean用@Primary指定优先使用,就只用一个不会报错了
 * @Autowire在controller的usercontroller中演示
 */
/*  
 * @Conditional(MacCondition.class)条件装配，当满足条件的时候（MacCondition.class中实现Condition接口重写match方法），这个组件才会被注册到容器中
 * 在config中的dogconfig中演示
 */
/* 
 * @Bean('自定义bean的名字') 没有的话bean的名字是（@bean public 类 '对象名'{})这里的对象名
 * 在@SpringBootApplication同级目录中能扫到@bean
 * 标注在方法上，返回值是组件类型，返回值就是组件类型，方法名就是组件的名字
 * 在config中的personconfig中演示
 */
// @Import(xx.class) 可以用于导入第三方bean（实现一般简单的bean导入）  这个注解放别的地方（比如controller），也一样可以 推荐单独放appconfig
/* 
 * FactoryBean一个接口：@Component 然后public class xxx implements FactoryBean<Bean类型>
 * 可以实现复杂的bean导入，包括第三方bean导入，实现3个方法：
 * 1、getObject()：返回要制造的bean对象
 * 2、getObjectType()：返回要制造的bean对象的类型
 * 3、isSingleton()：返回要制造的bean对象是否是单实例的
 * 工厂也可以导入第三方bean（可以实现复杂的bean导入） 
 * 在factory/BYDFactory中演示
 */ 
@SpringBootApplication
public class Spring01IocApplication {
    public static void main(String[] args) {
        // ConfigurableApplicationContext父类是ApplicationContext：Spring应用上下文对象；IoC容器
        // 跑起一个Spring的应用
        ConfigurableApplicationContext ioc = SpringApplication.run(Spring01IocApplication.class, args);
        System.out.println("=================ioc容器创建完成===================");
        DeliveryDao dao = ioc.getBean(DeliveryDao.class);
        dao.saveDelivery();
    }

    /** 演示了用spring的ResourceUtils，来获取类路劲的文件比如配置文件内容
     *
     * @param args
     * @throws IOException
     */
    public static void test11(String[] args) throws IOException {
        ConfigurableApplicationContext ioc = SpringApplication.run(Spring01IocApplication.class, args);
        System.out.println("=================ioc容器创建完成===================");

        Dog bean = ioc.getBean(Dog.class);
        System.out.println("bean = " + bean);

        Cat bean1 = ioc.getBean(Cat.class);
        System.out.println("cat = " + bean1);

        File file = ResourceUtils.getFile("classpath:abc.jpg");
        System.out.println("file = " + file);


        int available = new FileInputStream(file).available();
        System.out.println("available = " + available);

    }

    /** 演示了用构造器注入，也就是在@Service下的一个set方法中，还有感知接口（Aware）的环境变量使用
     *
     * @param args
     */
    public static void test10(String[] args) {
        ConfigurableApplicationContext ioc = SpringApplication.run(Spring01IocApplication.class, args);
        System.out.println("=================ioc容器创建完成===================");


        HahaService hahaService = ioc.getBean(HahaService.class);
        System.out.println("hahaService = " + hahaService);


        String osType = hahaService.getOsType();
        System.out.println("osType = " + osType);


        String myName = hahaService.getMyName();
        System.out.println("myName = " + myName);
    }

    public static void test09(String[] args) {
        ConfigurableApplicationContext ioc = SpringApplication.run(Spring01IocApplication.class, args);
        System.out.println("=================ioc容器创建完成===================");

        UserDao bean = ioc.getBean(UserDao.class);
        System.out.println("bean = " + bean);

    }
    /**@Resource，@Autowire对比
     *
     * @param args
     */
    public static void test08(String[] args) {
        ConfigurableApplicationContext ioc = SpringApplication.run(Spring01IocApplication.class, args);
        System.out.println("=================ioc容器创建完成===================");

        UserService bean = ioc.getBean(UserService.class);
        System.out.println("UserService = " + bean);

        Map<String, Dog> beansOfType = ioc.getBeansOfType(Dog.class);
        System.out.println("dogs = " + beansOfType);


    }
    /**
     * 测试自动注入@Autowire: 代码在 UserController 中
     * @param args
     */
    public static void test07(String[] args) {
        ConfigurableApplicationContext ioc = SpringApplication.run(Spring01IocApplication.class, args);
        System.out.println("=================ioc容器创建完成===================");

        UserController userController = ioc.getBean(UserController.class);

        System.out.println("userController = " + userController);
    }
    /**
     * 条件注册 @Conditional config/dogcofig有演示
     * @param args
     */
    public static void test06(String[] args) {
        ConfigurableApplicationContext ioc = SpringApplication.run(Spring01IocApplication.class, args);
        Map<String, Person> beans = ioc.getBeansOfType(Person.class);
        System.out.println("beans = " + beans);
        //拿到环境变量
        ConfigurableEnvironment environment = ioc.getEnvironment();
        String property = environment.getProperty("OS");
        System.out.println("property = " + property);
        Map<String, Dog> beansOfType = ioc.getBeansOfType(Dog.class);
        System.out.println("dogs = " + beansOfType);
        Map<String, UserService> ofType = ioc.getBeansOfType(UserService.class);
        System.out.println("ofType = " + ofType);
    }
    /** FactoryBean在容器中放的组件的类型，是接口中泛型指定的类型，组件的名字是 工厂自己的名字
     *
     * @param args
     */
    public static void test05(String[] args) {
        ConfigurableApplicationContext ioc = SpringApplication.run(Spring01IocApplication.class, args);
        System.out.println("=================ioc容器创建完成===================");


        Car bean1 = ioc.getBean(Car.class);
        Car bean2 = ioc.getBean(Car.class);
        System.out.println(bean1 == bean2);

        Map<String, Car> beansOfType = ioc.getBeansOfType(Car.class);
        System.out.println("beansOfType = " + beansOfType);
    }
    /**
     * @Scope 调整组件的作用域：以下注解放Bean注解上面
     * 1、@Scope("prototype")：非单实例:
     *      容器启动的时候不会创建非单实例组件的对象。
     *      什么时候获取，什么时候创建
     * 2、@Scope("singleton")：单实例： 默认值
     *      容器启动的时候会创建单实例组件的对象。
     *      容器启动完成之前就会创建好
     *    @Lazy：懒加载
     *      容器启动完成之前不会创建懒加载组件的对象
     *      什么时候获取，什么时候创建
     * 3、@Scope("request")：同一个请求单实例
     * 4、@Scope("session")：同一次会话单实例
     *
     * @return
     */
    public static void test04(String[] args) {
        // @Scope("singleton")
        ConfigurableApplicationContext ioc = SpringApplication.run(Spring01IocApplication.class, args);
        System.out.println("=================ioc容器创建完成===================");
        Object zhangsan1 = ioc.getBean("zhangsan");
        System.out.println("zhangsan1 = " + zhangsan1);
        Object zhangsan2 = ioc.getBean("zhangsan");
        System.out.println("zhangsan2 = " + zhangsan2);
        //容器创建的时候（完成之前）就把所有的单实例对象创建完成
        System.out.println(zhangsan1 == zhangsan2);
        System.out.println("=========================================");
//        Dog bean = ioc.getBean(Dog.class);
//        System.out.println("dog = " + bean);

    }
    /**
     * 默认，分层注解能起作用的前提是：这些组件必须在主程序所在的包及其子包结构下
     * Spring 为我们提供了快速的 MVC分层注解
     *      1、@Controller 控制器
     *      2、@Service 服务层
     *      3、@Repository 持久层
     *      4、@Component 组件
     * @param args
     */
    public static void test03(String[] args) {
        ConfigurableApplicationContext ioc = SpringApplication.run(Spring01IocApplication.class, args);

        System.out.println("========");

        UserController bean = ioc.getBean(UserController.class);
        System.out.println("bean = " + bean);

        UserService bean1 = ioc.getBean(UserService.class);
        System.out.println("bean1 = " + bean1);

        CoreConstants bean2 = ioc.getBean(CoreConstants.class);
        System.out.println("bean2 = " + bean2);

    }
    /**
     * 组件：框架的底层配置；@configuration
     *   配置文件：指定配置
     *   配置类：分类管理组件的配置，配置类也是容器中的一种组件。（用到了config里的DogConfig，PersonConfig（这两个也是组件（bean）））
     *
     * 创建时机：容器启动过程中就会创建组件对象
     * 单实例特性：所有组件默认是单例的，每次获取直接从容器中拿。容器提前会创建组件
     * @param args
     */
    public static void test02(String[] args) {
        //1、跑起一个Spring的应用；  ApplicationContext：Spring应用上下文对象； IoC容器
        ConfigurableApplicationContext ioc = SpringApplication.run(Spring01IocApplication.class, args);
        System.out.println("=================ioc容器创建完成===================");
        //2、获取组件
        Dog bean = ioc.getBean(Dog.class);
        System.out.println("bean = " + bean);
        Dog bean1 = ioc.getBean(Dog.class);
        System.out.println("bean1 = " + bean1);
        Dog bean2 = ioc.getBean(Dog.class);
        System.out.println("bean2 = " + bean2);
        Person zhangsan = (Person) ioc.getBean("zhangsan");
        System.out.println("对象 = " + zhangsan);
        System.out.println("=============================");
        for (String definitionName : ioc.getBeanDefinitionNames()) {
            System.out.println("definitionName = " + definitionName);
        }
    }
    /** 演示了ioc对象，容器的获取
     * @param args
     */
    public static void test01BeanAnnotation(String[] args) {
        //1、跑起一个Spring的应用；  ApplicationContext：Spring应用上下文对象； IoC容器
        ConfigurableApplicationContext ioc = SpringApplication.run(Spring01IocApplication.class, args);
        System.out.println("ioc = " + ioc);

        System.out.println("=============================");
        //2、获取到容器中所有组件的名字；容器中装了哪些组件； Spring启动会有很多默认组件
//        String[] names = ioc.getBeanDefinitionNames();
//        for (String name : names) {
//            System.out.println("name = " + name);
//        }


        //4、获取容器中的组件对象；精确获取某个组件
        // 组件的四大特性：(名字、类型)、对象、作用域
        // 组件名字全局唯一；组件名重复了，一定只会给容器中放一个最先声明的哪个。

        //小结：
        //从容器中获取组件，
        //  1）、组件不存在，抛异常：NoSuchBeanDefinitionException
        //  2）、组件不唯一，
        //      按照类型只要一个：抛异常：NoUniqueBeanDefinitionException
        //      按照名字只要一个：精确获取到指定对象
        //      按照类型获取多个：返回所有组件的集合（Map）
        //  3）、组件唯一存在，正确返回。


        //4.1、按照组件的名字获取对象
        Person zhangsan = (Person) ioc.getBean("zhangsan");
        System.out.println("对象 = " + zhangsan);

        //4.2、按照组件类型获取对象
//        Person bean = ioc.getBean(Person.class);
//        System.out.println("bean = " + bean);
        
        //4.3、按照组件类型获取这种类型的所有对象
        Map<String, Person> type = ioc.getBeansOfType(Person.class);
        System.out.println("type = " + type);

        //4.4、按照类型+名字
        Person bean = ioc.getBean("zhangsan", Person.class);
        System.out.println("bean = " + bean);


        //5、组件是单实例的....：获取的总是一个

    }

}
