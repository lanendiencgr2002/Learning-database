import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Java反射示例
 * 演示反射的基本用法，包括：
 * 1. 获取Class对象
 * 2. 构造方法操作
 * 3. 成员变量操作
 * 4. 成员方法操作
 */
public class ReflectionDemo {
    public static void main(String[] args) {
        // 1. 演示获取Class对象的三种方式
        classDemo();
        
        // 2. 演示构造方法的反射操作
        constructorDemo();
        
        // 3. 演示成员变量的反射操作
        fieldDemo();
        
        // 4. 演示成员方法的反射操作
        methodDemo();
    }

    /**
     * 演示获取Class对象的三种方式
     */
    private static void classDemo() {
        try {
            // 方式1：Class.forName() - 最常用，运行时加载
            Class<?> class1 = Class.forName("basic_learning.Student");
            
            // 方式2：类名.class - 编译时加载，常用于参数传递
            Class<?> class2 = Student.class;
            
            // 方式3：对象.getClass() - 对象实例已存在时使用
            Student student = new Student();
            Class<?> class3 = student.getClass();
            
            System.out.println("三种方式获取的Class对象是否相同：");
            System.out.println(class1 == class2); // true
            System.out.println(class1 == class3); // true
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 演示构造方法的反射操作
     */
    private static void constructorDemo() {
        try {
            Class<?> studentClass = Class.forName("Student");
            
            // 获取公共构造方法
            Constructor<?> publicConstructor = studentClass.getConstructor(String.class);
            
            // 获取所有构造方法（包括私有）
            Constructor<?> privateConstructor = studentClass.getDeclaredConstructor(String.class);
            // 设置私有构造方法可访问
            privateConstructor.setAccessible(true);
            
            // 使用构造方法创建对象
            Student student = (Student) privateConstructor.newInstance("张三");
            System.out.println("通过反射创建的学生：" + student);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 演示成员变量的反射操作
     */
    private static void fieldDemo() {
        try {
            Class<?> studentClass = Class.forName("Student");
            Student student = new Student();

            // 获取私有成员变量
            Field nameField = studentClass.getDeclaredField("name");
            nameField.setAccessible(true);
            
            // 获取和设置成员变量的值
            nameField.set(student, "李四");
            String name = (String) nameField.get(student);
            System.out.println("修改后的名字：" + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 演示成员方法的反射操作
     */
    private static void methodDemo() {
        try {
            Class<?> studentClass = Class.forName("Student");
            Student student = new Student();

            // 获取私有方法
            Method eatMethod = studentClass.getDeclaredMethod("eat", String.class);
            eatMethod.setAccessible(true);
            
            // 调用方法
            Object result = eatMethod.invoke(student, "苹果");
            System.out.println("方法返回值：" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 