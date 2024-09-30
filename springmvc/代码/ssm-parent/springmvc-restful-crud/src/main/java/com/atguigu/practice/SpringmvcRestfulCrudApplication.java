package com.atguigu.practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/** @PathVariable
 * @PathVariable:路径变量，从路径中获取参数
 * 示例：
 * @GetMapping("/employee/{id}")
 * public R get(@PathVariable("id") Long id){
 * return R.ok(employeeService.getById(id));
 * }
 * 
 * 示例：
 * @GetMapping("/employee/{*id}")
 * public R get(@PathVariable Map<String, String> id) {
 *     // id 现在是一个 Map，包含了所有匹配的路径段
 *     String fullPath = String.join("/", id.values());
 *     Employee emp = employeeService.getEmpByPath(fullPath);
 *     return R.ok(emp); "1" -> "1" "2" -> "2" "3" -> "3"
 * }
 * 
 * 示例：
 * /resources/{*path}:
 * {}中的值封装到path变量中
 * /resources/image.png：path = /image.png
 * /resources/css/spring.css：path =/css/spring.css
 * 
 * 示例：
 * /resources/{filename:\\w+}.dat:
 * {}中的值封装到filename变量中；filename满足\w+正则要求
 * /resources/xxx.dat：xxx是一个或多个字母
 * 
 * 在controller/EmployeeController中演示
 */

/** @CrossOrigin和跨域概念
 * @CrossOrigin:允许跨域：加在方法上，允许这个请求方法跨域，加在类上，允许这个类下的所有方法跨域
 * CORS policy：同源策略（限制ajax请求，图片，css，js 不限制跳地址）； 跨域问题
 * 跨源资源共享（CORS）（Cross-Origin Resource Sharing）
 *    浏览器为了安全，默认会遵循同源策略（请求要去的服务器和当前项目所在的服务器必须是同一个源[同一个服务器]），如果不是，请求就会被拦截
 * 	  postman啥的，浏览器之外的，不限制
 * 
 * 浏览器页面所在的：http://localhost   /employee/base
 * 页面上要发去的请求：http://localhost:8080   /api/v1/employees
 *  /以前的东西，必须完全一样，一个字母不一样都不行。浏览器才能把请求（ajax）发出去。
 *  跨域问题：
 *    1、前端自己解决：
 *    2、后端解决：允许前端跨域即可
 *          原理：服务器给浏览器的响应头中添加字段：Access-Control-Allow-Origin = *
 * 
 * 复杂的跨域请求会发送2次： 简单的GET POST不会发送预检请求
 *    1、options 请求：预检请求。浏览器会先发送options请求，询问服务器是否允许当前域名进行跨域访问
 *    2、真正的请求：POST、DELETE、PUT等
 */

/** @RequestMapping("/api/v1") @RestController
 * 指定接口路劲：这个类以下的方法自动加前缀/api/vi
 * @RequestMapping("/api/v1")
 * @RestController
 * public class EmployeeRestController{
 * 		@GetMapping("/employee/{id}")
 * 		public R get(@PathVariable("id") Long id){
 * 			return R.ok(employeeService.getById(id));
 * 		}
 * XXX
 * }
 * 在controller/EmployeeController中演示
 */

/** 统一返回对象Result
 * code: 状态码
 * message: 消息
 * data: 数据
 * 在common/R中演示
 * 
 * 示例：
 * @PostMapping("/employee")
 * public R add(@RequestBodyEmployeeemployee){
 * empLoyeeService.saveEmp(employee);
 * return R.ok(): //因为不用返回数据，所以空的R
 * 在controller/EmployeeController中演示
 */

/** @Get/Post/Put/Delete Mapping
 * @GetMapping: 查询 等价于 @RequestMapping(value = "/employee", method = RequestMethod.GET)
 * @PostMapping: 新增 等价于 @RequestMapping(value = "/employee", method = RequestMethod.POST)
 * @PutMapping: 修改 等价于 @RequestMapping(value = "/employee", method = RequestMethod.PUT)
 * @DeleteMapping: 删除 等价于 @RequestMapping(value = "/employee", method = RequestMethod.DELETE)
 */

/** 调用别人的功能？几种方式？
 * 1.API:给第三方发请求，获取响应数据
 * 2.SDK:导入jar包
 */

/** REST风格示例
 * REST风格示例： RESTAPI：别人暴露的API，REST风格
 * /employee/{id} GET 查询
 * /employee POST 新增
 * /employee PUT 修改
 * /employee/{id} DELETE 删除
 * /employees GET 查询所有
 * employees/page GET 分页查询
 * 
 * 如果是以前的：
 * /getEmployee?id=1：查询员工
 * /addEmployee?name=zhangsan&age=18:亲新增员工
 * /updateEmployee?id=1&age=20:修改员工
 * /deleteEmployee?id=1：删除员工
 * /getEmployeeList:获取所有员工
 */

/** REST的概念 
 * ·REST（Representational State Transfer表现层状态转移）是一种软件架构风格；
 * ·官网：https://restfulapi.net/
 * ·完整理解：Resource Representational State Transfer
 * ·Resource：资源
 * ·Representational：表现形式：比如用JsON，XML，JPEG筹
 * ·State Transfer：状态变化：通过HTTP的动词 （GET、POST、PUT、DELETE）实现
 * 一句话：使用资源名作为URI，使用HTTP的请求方式表示对资源的操作
 * 满足REST风格的系统，我们称为是RESTfuI系统
 */

/** 掌握的源码：
 * 1、SpringBoot 自动配置原理
 * 2、SpringMVC DispatcherServlet 流程
 * 3、Spring IOC容器（三级缓存机制）
 * 4、Spring 事务原理（TransactionManager、TransactionInterceptor）
 *
 * 掌握的开发技巧：RESTful CRUD 
 */
@SpringBootApplication
public class SpringmvcRestfulCrudApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringmvcRestfulCrudApplication.class, args);
	}

}
