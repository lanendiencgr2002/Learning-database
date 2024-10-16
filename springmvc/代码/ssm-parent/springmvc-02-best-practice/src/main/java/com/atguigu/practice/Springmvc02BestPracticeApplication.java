package com.atguigu.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** springmvc原理： DispatcherServlet 九大组件
 * DispatcherServlet：用于接收所有请求，再根据注解判断哪个方法来处理这个注解，找到了用反射去调用
 * 九大组件包括： #号的是很少用的上了
 * 1. MultipartResolver：处理文件上传等多部分请求
 * # 2. LocaleResolver：解析用户的区域信息，用于国际化
 * # 3. ThemeResolver：解析应用的主题 （像圣诞节有独自的页面风格，已经用不上了前端用的）
 * 4. List<HandlerMapping>：处理器映射，将请求映射到对应的处理器（请求路劲谁处理）
 * 5. List<HandlerAdapter>：处理器适配器，用于调用实际的处理器 （保存controller的映射关系）
 * 6. List<HandlerExceptionResolver>：异常解析器，处理请求过程中产生的异常
 * # 7. RequestToViewNameTranslator：当处理器未明确指定视图名时，将请求转换为视图名
 * # 8. FlashMapManager：管理 FlashMap，用于在重定向时传递数据
 * # 9. List<ViewResolver>：视图解析器，将逻辑视图名解析为实际的 View 对象
 * 这些组件共同构成了 Spring MVC 的核心处理流程，每个组件在请求处理的不同阶段发挥作用
 */

/** @JsonFormat：日期处理
 * 在前端传日期，然后后端接收会做反序列化操作，然后会报错不能反序列化 反序列化：把json转为对象
 * 
 * 在vo上的类标： 比如传给前端的vo标上
 * @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
 * private Date birth;  这样传给前端的日期就没问题了
 * 
 * 在vo上的类标： 比如前端传给后端的vo标上
 * @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
 * private Date birth;  这样传给前端的日期就没问题了
 */

/** Knife4j：接口文档  是swagger的增强版
 * 官网：https://doc.xiaominfo.com/docs/quick-start
 * 访问：http://localhost:8080/doc.html  如果要看swagger的：http://localhost:8080/swagger-ui.html
 * 1. 导包 <dependency><groupId>com.github.xiaoymin</groupId><artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId><version>4.4.0</version></dependency>
 * 2. 导入配置：springdoc: {swagger-ui: {path: /swagger-ui.html, tags-sorter: alpha, operations-sorter: alpha}, api-docs: {path: /v3/api-docs}, group-configs: [{group: 'default', paths-to-match: '/**', packages-to-scan: com.xiaominfo.knife4j.demo.web}]}, knife4j: {enable: true, setting: {language: zh_cn}}
 * 3. 把controller的reference加到packages-to-scan（配置文件中） packages-to-scan:com.xiaominfo.knife4j.demo.web
 * 
 * 常用注解说明：
 * @Tag              - 用于controller类，描述controller作用
 * @Parameter        - 用于标识参数作用
 * @Parameters       - 用于参数多重说明
 * @Schema           - 在vo上的类上用 或者 参数- 用于model层的JavaBean，描述模型作用及每个属性
 * @Operation        - 用于方法，描述方法作用
 * @ApiResponse      - 用于方法，描述响应状态码等
 * 
 * 在以下文件中演示：
 * springmvc-02-best-practice\src\main\java\com\atguigu\practice\controller\EmployeeRestController.java
 * springmvc-02-best-practice\src\main\java\com\atguigu\practice\vo\req\EmployeeUpdateVo.java
 */
 
/** 属性拷贝：BeanUtils.copyProperties(source, target)
 * 示例：
 * Employee employee = new Employee();
 * BeanUtils.copyProperties(vo,employee); 将vo中的属性拷贝到employee中
 * 
 * 在以下文件中演示：
 * springmvc-02-best-practice\src\main\java\com\atguigu\practice\controller\EmployeeRestController.java
 */

/** 设计模式：单一职责
 * 各种xxo， 比如负责数据校验可以在vo.req中（前端传过来的数据）  vo用的最多
 * Pojo：Plain Ordinary Java Object 简单普通Java对象
 * Dao：Data Access Object 数据访问对象
 * TO：Transfer Object 传输对象  从service层到controller层
 * VO：View/Value Object 视图/值对象  （专门用来封装前端数据的对象）(给前端返回数据要脱敏)
 * DTO：Data Transfer Object 数据传输对象  从controller层到service层 其实和TO差不多
 * BO：Business Object 业务对象 （service层）
 * PO：Persistent Object 持久化对象 （数据库表对应的实体类）
 */

/** 国际化（internationalization) i18n
 * 取名相似的还有 (Kubernetes) k8s
 * @Gender(message="{gender.message}")  会自动去messages.properties文件中找gender.message
 * 
 * 在resources下新建messages.properties文件 内容添加：
 * gender.message=性别只能为：男，女
 * 
 * 在resources下新建messages_zh_CN.properties文件 内容添加：
 * gender.message=性别只能为：男，女
 * 
 * 在resources下新建messages_en_US.properties文件 内容添加：
 * gender.message=Gender must be: male, female
 * 
 * 想测试别的语言，可以改浏览器的语言，或者改请求头：Accept-Language：zh-CN 或者 en-US
 * 
 * 在以下文件中演示：
 * springmvc-02-best-practice\src\main\java\com\atguigu\practice\advice\GlobalExceptionHandler.java
 */

/** 自定义校验器：
 * 自定义校验注解：
 * 1. 定义注解：@interface Gender
 * 2. 指定校验器：@Constraint(validatedBy = {GenderValidator.class})
 * 
 * 在以下文件中演示：
 * springmvc-02-best-practice\src\main\java\com\atguigu\practice\annotation\Gender.java
 * 
 * 自定义校验器：
 * 定义校验器类实现ConstraintValidator<Gender, String>接口，重写isValid方法
 * 
 * 在以下文件中演示：
 * springmvc-02-best-practice\src\main\java\com\atguigu\practice\validator\GenderValidator.java
 */

/** 全局数据校验：
 * 1、导入校验包
 * 2、JavaBean编写校验注解 （@NotNull、@Max等）
 * 3、使用@Valid告诉SpringMVC进行校验 （@Valid 参数）
 * 4、使用BindingResult封装校验结果 （不用了！！！）
 * 5、结合全局异常处理，统一处理数据校验错误  （全局异常捕获MethodArgumentNotValidException.class）
 * @ExceptionHandler(value = MethodArgumentNotValidException.class)
 * public R methodArgumentNotValidException(MethodArgumentNotValidException ex) {
 *     // 1、result 中封装了所有错误信息
 *     BindingResult result = ex.getBindingResult();
 *     List<FieldError> errors = result.getFieldErrors();
 *     Map<String, String> map = new HashMap<>();
 *     for (FieldError error : errors) {
 *         String field = error.getField();
 *         String message = error.getDefaultMessage();
 *         map.put(field, message);}
 *     return R.error(code: 500, msg: "参数错误", map);}
 */

/** 数据校验： 
 * JSR 303是Java为Bean数据合法性校验提供的标准框架，它已经包含在JavaEE 6.0标准中。
 * JSR 303通过在Bean属性上标注类似于@NotNull、@Max等标准的注解指定校验规则，
 * 并通过标准的验证接口对Bean进行验证。
 *
 * 数据校验使用流程：
 * 1. 引入校验依赖：spring-boot-starter-validation
 * 2. 定义封装数据的Bean
 * 3. 给Bean的字段标注校验注解，并指定校验错误消息提示
 * 4. 使用@Valid、@Validated开启校验
 * 5. 使用BindingResult封装校验结果   注：如果没加这个，校验错误会抛异常
 * 
 * 示例：
 * @PostMapping("/employee")
 * public R add(@RequestBody @Valid Employee employee, BindingResult bindingResult(用于返回啥校验错误，可以不写)) {
 *    // if (bindingResult.hasErrors()) {
 *    //     return R.error(400, bindingResult.getFieldError().getDefaultMessage());
 *    // }
 *    // employeeService.saveEmp(employee);
 *    // return R.ok();
 * 		if (bindingResult.hasErrors()) {
 * 			employeeService.saveEmp(employee);
 * 			return R.ok();}
 * 		// 说明校验错误：拿到所有属性错误的信息
 * 		Map<String, String> errorsMap = new HashMap<>();
 * 		for (FieldError fieldError : result.getFieldErrors()) {
 * 			// 1、获取到属性名
 * 			String field = fieldError.getField();
 * 			// 2、获取到错误信息
 * 			String message = fieldError.getDefaultMessage();
 * 			errorsMap.put(field, message);}
 * 		return R.error(code: 500, msg: "校验失败", errorsMap);
 * }
 * 
 * @Data
 * public class Employee {
 *     @NotNull(message = "id不能为空") //message：出错信息
 *     private Integer id;
 *     @NotBlank(message = "name不能为空")
 *     private String name;
 * 	   @Max(value = 100, message = "年龄不能超过100岁")
 *     private Integer age;
 * 	   @Min(value = 18, message = "年龄不能小于18岁")
 *     private Integer age;
 * 	   @Email(message = "邮箱格式不正确")
 *     private String email;
 * }
 */

/** 枚举类来管理异常码：
 * 大型系统出现以下异常：异常处理文档，如果没枚举，那个异常码都不能改。。
 * throw new EBizException(BizExceptionEnume.ORDER_CLOSED);
 * 
 * exception/BizExceptionEnume：
 * public enum BizExceptionEnume {
 * ORDER_CLOSED(10001, "订单已关闭"),
 * ORDER_NOT_EXIST(10002, "订单不存在"),
 *  @Getter
 *  private Integer code;
 *  @Getter
 *  private String msg;
 *  private BizExceptionEnume(Integer code, String msg) {
 *      this.code = code;
 *      this.msg = msg;
 *  }}
 * 
 * exception/BizException：
 * @Data
 * public class BizException extends RuntimeException {
 * public BizException(BizExceptionEnume exceptionEnume) {
 *      super(exceptionEnume.getMsg());
 *      this.code = exceptionEnume.getCode();
 *      this.msg = exceptionEnume.getMsg();
 * }}
 * 
 * 在以下文件中演示：
 * springmvc-02-best-practice\src\main\java\com\atguigu\practice\advice\GlobalExceptionHandler.java
 * springmvc-02-best-practice\src\main\java\exception\BizExceptionEnume.java
 * springmvc-02-best-practice\src\main\java\exception\BizException.java
 */

/** 处理异常的业务逻辑：
 * 前端关心异常状态，后端正确业务流程。
 * 推荐：后端只编写正确的业务逻辑，如果出现业务问题，后端通过抛异常的方式提前中断业务逻辑。前端感知异常；
 * 
 * 一个简单示例：
 * if（id==nulL){//页面没有带id
 * //中断的业务的时候，必须让上层及以上的链路知道中断原因。推荐抛出业务异常
 * thrownewRuntimeException（"id不能为空"）;
 * 
 * 规范做法：
 * 在exception包下，定义一些业务异常类，系统异常类等等
 * public class BizException extends RuntimeException{有错误码，错误信息}
 * 然后就可以抛BizException  throw new BizException(5000,"错误信息");
 * 
 * 在以下文件中演示：
 * springmvc-02-best-practice\src\main\java\exception\BizException.java
 */

/** springboot底层异常处理默认行为：
 * springboot依然使用springmvc的异常处理机制
 * 不过SpringBoot编写了一些默认的处理配置
 * 默认行为：
 * 自适应的异常处理：
 * 浏览器发的请求，出现异常返回默认错误页面
 * 移动端发的请求，出现异常返回默认json错误数据；项目开发的时候错误模型需要
 * 
 * 如果出现了异常：本类和全局都不能处理
 * SpringBoot底层对SpringMVC有兜底处理机制：自适应处理（浏览器响应页面、移动端会响应json
 * 最好还是编写全局异常处理，自适应处理只是兜底
 */

/** 全局异常处理：
 * 先在本类的异常处理，本类没有，再去找@ControllerAdvice
 * 在全局异常处理中也是精确优先
 * 
 * @ControllerAdvice：可以集中处理所有Controller的异常
 * @ExceptionHandler + @ControllerAdvice：可以完成全局统一异常处理
 * @RestControllerAdvice = @ResponseBody + @ControllerAdvice
 * 标注在类上，然后用@ExceptionHandler标注方法，表示这个方法可以处理异常
 * 
 * 注意：要打印异常堆栈
 * // 最终的兜底
 * @ExceptionHandler(Throwable.class)
 * public R error(Throwable e) {
 *     System.out.println("【全局】- Exception处理" + e.getClass());
 *     e.printStackTrace();
 *     return R.error(500, e.getMessage());
 * }
 * 
 * （advice有增强的意思）
 * 在以下文件中演示：
 * springmvc-02-best-practice\src\main\java\com\atguigu\practice\advice\GlobalExceptionHandler.java
 */

/** 类中的异常处理：
 * 会优先匹配精确异常，不行再匹配父类异常
 * 
 * 捕获某个异常示例：
 * 在同一个类下，一个方法出现了异常(比如除0异常)，另一个方法可以处理这个异常
 * 如果Controller本类出现异常，会自动在本类中找有没有@ExceptionHandler标注的方法
 * 如果有，执行这个方法，它的返回值，就是客户端收到的结果
 * @ResponseBody 注解在方法上，表示这个方法的返回值会作为响应体返回给客户端
 * @ExceptionHandler(value = {异常类型.class}(比如ArithmeticException.class除0异常)) 注解在方法上，表示这个方法可以处理异常
 * public R handleArithmeticException(ArithmeticException e(可以不填)){
 * 		returnR.error（code:100，msg:"执行异常"+e.getMessage()）;
 * }
 * 
 * 捕获所有异常示例：
 * @ExceptionHandler(Throwable.class)
 * public R handleException02(Throwable ex) {
 *     return R.error(
 *         code: 500, 
 *         msg: "其他异常: " + ex.getMessage()
 *     );
 * }
 */

/** 过滤器：
 * @WebFilter("/hello") //这个注解不能给过滤器使用到（因为这是servlet的注解，在springboot失效）
 * @Component //注释在类上，就可以直接用了，默认拦截所有请求
 * 弄一个类实现Filter接口，重写3个方法，init，doFilter，destroy
 * 只用在doFilter()方法中调用chain.doFilter(request, response)放行;
 * 
 * 在以下文件中演示：
 * springmvc-02-best-practice\src\main\java\com\atguigu\practice\filter\HelloFilter.java
 */

/** 拦截器和过滤器区别：
 * 1. 接口：拦截器使用HandlerInterceptor，过滤器使用Filter
 * 2. 定义：拦截器属于Spring框架，过滤器属于Servlet规范
 * 3. 放行：拦截器通过preHandle返回true放行请求，过滤器通过chain.doFilter()放行请求
 * 4. 整合性：拦截器可以直接整合Spring容器的所有组件，过滤器不受Spring容器管理，无法直接使用容器中组件
 * 5. 拦截范围：拦截器拦截SpringMVC能处理的请求，过滤器拦截Web应用所有请求
 * 6. 总结：在SpringMVC的应用中，推荐使用拦截器
 */

/** 拦截器执行顺序：
 * 顺序preHandle=>目标方法=>倒序postHandle=>渲染=>倒序afterCompletion
 * 两个拦截器执行顺序：
 * preHandle01=>preHandle02=>目标方法=>postHandle02=>postHandle01
 * 如果其中不管哪异常或中断，则afterCompletion2=>afterCompletion1 !!(preHandle返回true afterCompletion方法才会执行)
 * 
 * 
 * 在以下文件中演示：
 * springmvc-02-best-practice\src\main\java\com\atguigu\practice\interceptor\MyHandlerInterceptor0.java
 * springmvc-02-best-practice\src\main\java\com\atguigu\practice\interceptor\MyHandlerInterceptor1.java
 * springmvc-02-best-practice\src\main\java\com\atguigu\practice\interceptor\MyHandlerInterceptor2.java
 */

/** 拦截器基础使用：
 * SpringMVc内置拦截器机制，允许在请求被目标方法处理的前后进行拦截，执行一些额外操作；比如：权限验证、日志记录（aop也可以）、数据共享（在线程池放数据）等..
 * 使用步骤：
 * 1. 创建拦截器类，实现HandlerInterceptor接口 @Component注解上  或者@Bean 载入webMvcConfigurer
 * 2. 重写接口中的三个方法：
 *      preHandle：在目标方法执行前执行，返回true表示放行，返回false表示拦截
 *      postHandle：在目标方法执行后执行
 *      afterCompletion：在目标方法执行完毕后执行  （以前做页面开发页面渲染完触发这个）
 * 3. 在SpringMVC配置类中，注册拦截器
 * 		在实现方法中：
 * 			registry.addInterceptor(拦截器类名.class(或者@Autowired拦截器类)).addPathPatterns("/**表示所有路径，拦截路径");
 * 
 * 在以下文件中演示：
 * springmvc-02-best-practice\src\main\java\com\atguigu\practice\interceptor\MyHandlerInterceptor0.java
 * springmvc-02-best-practice\src\main\java\com\atguigu\practice\config\MySpringMVCConfig.java
 */

@SpringBootApplication
public class Springmvc02BestPracticeApplication {

	public static void main(String[] args) {
		SpringApplication.run(Springmvc02BestPracticeApplication.class, args);
	}

}
