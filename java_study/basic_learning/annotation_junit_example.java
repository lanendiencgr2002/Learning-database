import java.lang.reflect.Method;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 自定义注解 可以在别的类上定义，然后在同一包下使用
@Retention(RetentionPolicy.RUNTIME)  // 指定注解的生命周期
@Target(ElementType.METHOD)  // 指定注解可以在哪些位置上使用
@interface MyTest {
    int count() default 1; // 默认执行一次
}

public class annotation_junit_example {
    // 目标：搞清楚注解的应用场景：模拟junit框架，有MyTest注解的方法就执行，没有的就不执行。
    public static void main(String[] args) throws Exception {
        annotation_junit_example ad = new annotation_junit_example();
        // 1. 获取类对象
        Class<?> c = annotation_junit_example.class;
        // 2. 获取所有方法(通过反射获取)
        Method[] methods = c.getMethods();
        // 3. 遍历所有方法，判断方法上是否有MyTest注解，有就执行，没有就不执行。
        for (Method method : methods) {
            // 4. 判断方法上是否有MyTest注解
            if (method.isAnnotationPresent(MyTest.class)) {
                // 获取到这个方法的注解
                MyTest myTest = method.getDeclaredAnnotation(MyTest.class);
                int count = myTest.count();
                // 5. 有就执行这个method方法 //这个方法是在当前类中，所以用当前类作为调用者，如果不在，不能调用别人的方法
                for (int i = 0; i < count; i++) {
                    method.invoke(ad);
                }
            }
        }
    }

    // 测试方法: public 无参 无返回值
    @MyTest
    public void test1(){
        System.out.println("test1方法执行了");
    }

    public void test2(){
        System.out.println("test2方法执行了");
    }

    @MyTest(count = 2)
    public void test3(){
        System.out.println("test3方法执行了");
    }

    @MyTest
    public void test4(){
        System.out.println("test4方法执行了");
    }
}
