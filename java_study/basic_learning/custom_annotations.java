/* 自定义注解
 * public @interface 注解名 { // 等同public interface 注解名 extends annotation
 *     // 注解属性等同于接口中的抽象方法 // public abstract 返回值类型 方法名();
 *     public 属性类型 属性名() default 默认值;
 * }
 * @注解(...)：其实就是一个实现类对象，实现了该注解以及Annotation接口
 * 特殊属性value
 * 如果只有注解一个属性，且属性名为value，则可以省略属性名和()
 *  @custom_annotations("张三")
 *  @custom_annotations(value = "张三")
 * 
 * 元注解：注解的注解
 * @Retention(RetentionPolicy.RUNTIME)：指定注解的生命周期
 *    SOURCE 源码时有效，编译时丢弃 CLASS(默认值) 保留到字节码，运行时丢弃 RUNTIME 保留到运行时
 *    RUNTIME(开发中常用) 一直保留到运行阶段
 * 
 * @Target(ElementType.METHOD)：指定注解可以在哪些位置上使用
 *    TYPE类 接口, FIELD成员变量, METHOD成员方法，PARAMETER方法参数
 *    CONSTRUCTOR构造器，LOCAL_VARIABLE局部变量，ANNOTATION_TYPE注解类型，PACKAGE包
 * 
 * @Inherited：指定注解可以被继承
 *    @Inherited
 *    @Retention(RetentionPolicy.RUNTIME)
 *    @Target(ElementType.TYPE)
 *    public @interface MyInheritedAnnotation {}
 *    @MyInheritedAnnotation
 *    public class ParentClass {}
 *    public class ChildClass extends ParentClass {}
 *    // ChildClass 自动继承 @MyInheritedAnnotation
 * 
 * @Documented：指定注解可以被文档化
 *    @Documented
 *    @Retention(RetentionPolicy.RUNTIME)
 *    @Target(ElementType.TYPE)
 *    public @interface MyDocumentedAnnotation {}
 *    @MyDocumentedAnnotation
 *    public class MyClass {}
 *    // 在生成的API文档中，@MyDocumentedAnnotation 会出现在文档中
 * 
 * pulic @interface 注解名 {
 * }
 * 
 * 注解的解析：就是判断类上、方法上成员变量上否存在注解并把注解里的内容给解析出来。
 * 解析类：
 * class AnnotationDemo {
    public void parseClass() throws Exception {
        // 1. 获取类对象
        Class c1 = Demo.class;
        // 如果是解析方法，则获取方法对象
        // Method method = Demo.class.getMethod("show");
        // 2. 使用isAnnotationPresent判断这个类上是否存在指定注解MyTest2
        if (c1.isAnnotationPresent(MyTest2.class)) {
            // 3. 获取注解对象
            MyTest2 myTest2 = (MyTest2) c1.getAnnotation(MyTest2.class);
            // 如果解析方法，则获取方法对象
            // MyTest2 myTest2 = (MyTest2) method.getAnnotation(MyTest2.class);
            // 4. 获取注解属性值
            String[] address = myTest2.address();
            double height = myTest2.height();
            String value = myTest2.value();
            
            // 5. 打印注解属性值
            System.out.println(Arrays.toString(address));
            System.out.println(height);
            System.out.println(value);
        }
    }
}
 * 
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ElementType.METHOD, ElementType.TYPE}) // 指定注解只能用在方法上
@Retention(RetentionPolicy.RUNTIME) // 指定注解的生命周期
public @interface custom_annotations { // 等同public interface custom_annotations extends annotation
    String name();
    int age() default 18; // custom_annotations()括号内可以不写
    String[] address();
}
