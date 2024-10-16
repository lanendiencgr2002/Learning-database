package com.atguigu.springmvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** springmvc的响应测试 
 * @ResponseBody(注释方法或者类)：将方法的返回值直接写入HTTP响应正文中，也可以标注在类上，表示该类中的所有方法的返回值都是直接写入HTTP响应正文中
 * @RestController(注释类)：是@Controller和@ResponseBody的组合，表示该类中的所有方法的返回值都是直接写入HTTP响应正文中
 * 1. 给json数据 返回类会自动把类转为json格式 灵活的话返回map，也会自动转为json
 * @RequestMapping("/handle01")
 * public String handle01(){
 *     Person person = new Person("xxx,xxx");
 *     return person;
 * }
 * 
 * 在以下文件中演示：
 * springmvc-01-helloworld\src\main\java\com\atguigu\springmvc\controller\RequestTestController.java
 * 2. 文件下载
 *      HttpEntity：拿到整个请求数据
 *      ResponseEntity：拿到整个响应数据（状态码，响应头，响应体）
 * 在响应头告诉浏览器下载文件，在响应体中写入文件内容
 * Content-Disposition响应头指定文件名信息，文件名如果有中文还需要URLEncoder进行编码
 * ContentType响应头指定响应内容类型，是一个oCTET_STREAM（8位字节流）
 * ContentLength响应头指定内容大小
 * body指定具体响应内容（文件字节流）：也可以用InputStreamResource替换bytel]，防止oom
 *
 * 示例
 * @GetMapping("/download")
 * public ResponseEntity<byte[]> handleDownload() throws IOException {
 *     //一口气读会oom溢出
 *     byte[] bytes = Files.readAllBytes(Paths.get(new File("aaaa.png").toURI()));
 *     return ResponseEntity
 *             .ok()
 *             .header("Content-Disposition", "attachment; filename=aaaa.png")
 *             .contentType(MediaType.APPLICATION_OCTET_STREAM)
 *             .contentLength(bytes.length)
 *             .body(bytes);
 * }
 * bug：
 * ①. 文件下载，文件名有中文，需要URLEncoder.encode(filename, "UTF-8")
 * String encode = URLEncoder.encode("哈哈.jpg", "UTF-8");
 * .header(headerName:"Content-Disposition",..headerValues:"attachment;filename="+encode)
 * ②. 文件太大会oom(内容溢出)
 * InputStreamResource resource =new InputStreamResource(inputStream);
 * .body(resource)
 * .contentLength(inputStream.available())
 * ③. 给页面，并携带数据
 * @RequestMapping("/handle03")
 * public String handle03(String username,String password)
 * 
 * 在以下文件中演示：
 * springmvc-01-helloworld\src\main\java\com\atguigu\springmvc\controller\RequestTestController.java
 * 3. 页面跳转
 * 
 * 在以下文件中演示：
 * springmvc-01-helloworld\src\main\java\com\atguigu\springmvc\controller\PageTestController.java
 */

/** springmvc请求测试 
 * 1. 在请求方法中，参数名如果没接收到，会自动封装为null，基本类型封装为默认值
 * @RequestMapping("/handle01")
 * public String handle01(String username,String password)
 * 
 * 2. @RequestParam：将请求参数绑定到方法参数上，如果请求参数名和方法参数名不一致，可以使用@RequestParam注解
 * 无论请求参数带到了请求体，还是路劲上，都可以用@RequestParam获取到
 * @RequestMapping("/handle02") 
 * public String handle02(@RequestParam(value = "name",required = false,defaultValue = "zhangsan") String username,String password)
 *      required = false 表示这个参数可以为空
 *      defaultValue = "zhangsan" 表示这个参数的默认值为zhangsan
 * 
 * 3. 在请求方法中，如果参数名是pojo类，SpringMVc会自动把请求参数和pojo属性进行匹配：
 * 参数名如果没接收到，会自动封装成null，基本类型封装为默认值
 * 用不着@RequestParam怎么办？在类中操作
 * Person{String name="zhangsan"} 此时如果name没接收到，默认值是zhangsan
 * 
 * 4. @RequestHeader：将请求头绑定到方法参数上，如果请求头名和方法参数名不一致，可以使用@RequestHeader注解
 * @RequestMapping("/handle03")
 * public String handle03(@RequestHeader(value = "Accept",default="xx") String accept)
 * 
 * 5. @CookieValue：将cookie绑定到方法参数上，如果cookie名和方法参数名不一致，可以使用@CookieValue注解
 * @RequestMapping("/handle04")  value是cookie的key 然后这样来获取他的v
 * public String handle04(@CookieValue(value = "JSESSIONID", required = false, defaultValue = "") String sessionId) {
 *     // 方法体
 * }
 * 一般是页面开发用的，跟后端关系不大
 * 
 * 6. 使用POJO，级联封装复杂对象
 * 各种各样的数据类型能不能被接到？
 * public String handle06(Person person)
 * 
 * 在以下文件中演示：
 * springmvc-01-helloworld\src\main\java\com\atguigu\springmvc\bean\Person.java
 * 
 * 7. @RequestBody(注释请求参数):获取请求体json数据
 * public String handle07(@RequestBody(required = false) Person person) 获取请求体json数据，自动转为person对象，required = false表示这个参数可以为空
 * public String handle08(@RequestBody String body(随意名)) 获取请求体json数据，自动转为字符串，后续需要手动转换为对象等等
 * 
 * 8. 文件上传： @RequestParam取出文件项，封装为MuLtipartFile，就可以拿到文件内容
 * @RequestParam("headerImg") MultipartFile headerImgFile
 * 前端：表单：method="post" enctype="multipart/form-data"
 * SpringMVC对上传文件有大小限制（默认1MB），整体请求最大(默认10MB)
 * 可以配置：
 * spring.servlet.multipart.max-file-size=1GB #单个文件大小
 * spring.servlet.multipart.max-request-size=10GB #整体请求大小
 * 示例：
 * @RequestMapping（~"/handle08")
 * public String handle08(Person person,  因为不是json格式了，而是文件，所以不能用@RequestBody
 * @RequestParam("headerImg") MultipartFile headerImgFile, 来接收单个文件
 * @RequestPart(和上面一样的其实)("LifeImg"）MultipartFile[] LifeImgFiles){ 来接收多个文件，源中，名相同的多个文件
 * 
 * 在以下文件中演示：
 * springmvc-01-helloworld\src\main\java\com\atguigu\springmvc\controller\RequestTestController.java
 * 
 * 9. 获取原始请求信息：
 * HttpEntity：封装请求头、请求体；把整个请求拿过来
 * public String handle09(HttpEntity<String> httpEntity){ //这样是json的字符串格式
 *     String requestEntity = httpEntity.getBody(); //获取请求体
 *     HttpHeaders headers = httpEntity.getHeaders(); //获取请求头
 *     return requestEntity;
 * }
 * 自动转为类：
 * public String handle10(HttpEntity<Person> httpEntity){ //这样是json的类格式
 *     Person person = httpEntity.getBody(); //获取请求体
 *     HttpHeaders headers = httpEntity.getHeaders(); //获取请求头
 *     return person.toString();
 * }
 * 
 * 10. 原生api：
 * publicvoidhandle10(HttpServletRequest request,
 * HttpServletResponse response,还可以加接收请求方法等 HttpMethod method, HttpSession session, Model model, ModelMap modelMap){
 *     String requestUrl = request.getRequestURL().toString(); //获取请求的url
 *     String requestURI = request.getRequestURI(); //获取请求的uri
 *     String queryString = request.getQueryString(); //获取请求的参数
 *     String method = request.getMethod(); //获取请求的方法
 *     String contextPath = request.getContextPath(); //获取请求的上下文路径
 *     String servletPath = request.getServletPath(); //获取请求的servlet路径
 *     String remoteAddr = request.getRemoteAddr(); //获取请求的远程地址
 *     String remoteHost = request.getRemoteHost(); //获取请求的远程主机
 *     int remotePort = request.getRemotePort(); //获取请求的远程端口
 * 
 * 在以下文件中演示：
 * springmvc-01-helloworld\src\main\java\com\atguigu\springmvc\controller\RequestTestController.java
 */

/** json和序列化概念 
 * json：JavaScript Object Notation(JavaScript对象表示法)
 * json可以作为对象，或者字符串存在，json是一种纯数据格式
 * 将字符串转换为原生对象称为反序列化（deserialization）
 * 将原生对象转换为可以通过网络传输的字符串称为序列化（serialization）。
 */

/** http:xxx/xxx#xxx #号解释 
 * http:xxx/xxx#xxx 
 * 这个#号是锚点，表示页面中的一个位置，浏览器会自动跳转到这个位置
 * 不会发给服务器，由前端解决
 */

/** @RequestMapping
 * @Controller：告诉Spring这是一个控制器（处理请求的组件）
 * 
 * @RestController：是@Controller和@ResponseBody的组合，表示该类中的所有方法的返回值都是直接写入HTTP响应正文中
 * 
 * @ResponseBody(注释方法或者类)：将方法的返回值直接写入HTTP响应正文中，也可以标注在类上，表示该类中的所有方法的返回值都是直接写入HTTP响应正文中
 *   - 通常用于返回JSON或XML格式的数据
 *   - 每次请求都会执行被注解的方法
 *   如果没有这个，@RequestMapping注解的方法返回值默认是跳转页面（返回一个url）
 * 
 * @RequestMapping("/hell?/**")：用于映射Web请求，包括访问路径和HTTP方法
 * 路劲限定：
 *   - *：匹配任意多个字符，但不包括路径分隔符
 *   - **：匹配任意多层路径
 *   - ?：匹配单个字符
 * 请求方式限定：
 * @RequestMapping(value = "/hellocontroller", method = RequestMethod.GET)多的用{xx.Get,xx.Post,xx.Put,xx.Delete}
 *   - GET：获取资源 -Post：创建资源 -Put：更新资源 -Delete：删除资源
 * 请求参数限定：
 * @RequestMapping(value = "/hellocontroller", params = {"name", "age=18","gender!=1"})
 * 表示name和age=18和gender!=1是必须的请求参数，如果请求中不包含这两个参数，则不匹配
 * header限定：
 * @RequestMapping(value = "/hellocontroller", headers = {"Accept=text/html"})
 * 表示Accept=text/html是必须的请求头，如果请求中不包含这个头，则不匹配
 * 请求内容限定：
 * @RequestMapping(value = "/hellocontroller", consumes = {"application/json"})
 * 表示consumes=application/json是必须的请求头，如果请求中不包含这个头，则不匹配
 * 响应内容限定：
 * @RequestMapping(value = "/hellocontroller", produces = {"text/html;charset=UTF-8"})
 * 表示produces=text/html;charset=UTF-8是必须的响应头，如果响应中不包含这个头，则不匹配
 * 例如：返回<h1>Hello World</h1>，浏览器会显示Hello World 如果是text/plain;charset=UTF-8，浏览器会显示<h1>Hello World</h1>
 * 
 * 在以下文件中演示：
 * springmvc-01-helloworld\src\main\java\com\atguigu\springmvc\controller\HelloController.java
 */

/** springmvc 
 * 效果：其实是 SpringBoot 做的。
 * 1、tomcat不用整合
 * 2、servlet开发变得简单，不用实现任何接口
 * 3、自动解决了乱码等问题
 */
@SpringBootApplication
public class Springmvc01HelloworldApplication {

    public static void main(String[] args) {
        SpringApplication.run(Springmvc01HelloworldApplication.class, args);
    }

}
