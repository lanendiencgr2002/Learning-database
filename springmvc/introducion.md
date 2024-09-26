# 容器

先看main方法的所有，再看注释

```markdown
xx bean=ioc.getBean(xx.class)
xx bean=ioc.getBeansOfType(xx.class)
xx bean=ioc.getBean("zhangsan", Person.class);
ConfigurableApplicationContext ioc=xx;
@configuration
@Controller@Service@Repository@Component
@Scope@Lazy@Bean
@Conditional
@Autowire
@Resource
@primary
@Qualifier
@Value
构造器注入set注入
ResourceUtils获取配置文件内容
@Profile
FactoryBean接口和重写3个方法
@Bean
```

# 生命周期+aop

直接看main方法的注释

```markdown
new ClassPathXmlApplicationContext
Bean生命周期
postProcessBeforeInitialization
AutowiredAnnotationBeanPostProcessor
InitializingBean
@Bean.init
postProcessAfterInitialization
@PreDestroy
DisposableBean.destroy()
@Bean.destroy
@Autowired是如何实现的？
自定义@UUID然后自动扫描到这个注解自动赋值
依赖倒置
@Aspect
@Component
@Before
@After
@AfterReturning
@AfterThrowing
execution
args
@args
within
@annotation
动态代理
$$SpringCGLIB$$0
增强器链
advisedInterceptor
advisors
joinpoint
returning="result"
throwing = "e"
@Pointcut
@Order
```



# aop环绕通知+事务

```markdown
AnnotationUtils
ClassUtils
ReflectionUtils
beanDefinitiopMap
singletonobjects
BeanFactory
三级缓存
getSingleton()
singletonObjects
earlySingletonObjects
singletonFactories
spring.main.allow-circular-references=true
@Around
@Before
@AfterReturning
@AfterThrowing
@After
环绕通知的一个bug
throws Throwable{}
catch(Throwable e){throw e;}
HikariDataSource
DruidDataSource
JdbcTemplate
@EnableTransactionManagement
@Transactional
transactionManager
TransactionInterceptor
propagation
isolation
timeout
propagation
REQUIRED
SUPPORTS
MANDATORY
REQUIRES_NEW
NOT_SUPPORTED
NEVER
NESTED
isolation
timeout
readOnly
rollbackFor
rollbackForClassName
noRollbackFor
```







三级缓存解决循环依赖

<img src="introducion.assets/image-20240925212544914.png" alt="image-20240925212544914" style="zoom:25%;" />

然后结束都到成品区去了

<img src="introducion.assets/image-20240925212714855.png" alt="image-20240925212714855" style="zoom:25%;" />







# 1