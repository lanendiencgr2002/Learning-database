import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
public class reflection {
    /* 获取Class对象的方式  全类名：包名+类名  获取到的都是同一个对象
     * 1. Class.forName("类名")   运行阶段 
     * 2. 类名.class   编译为字节码阶段   对象名.getClass()   
     * 3. 加载字节码class到内存阶段  */
    public static void main(String[] args) throws ClassNotFoundException {
        // 使用完整的类名（包括包名）
        // 最常用的
        Class<?> clazz1 = Class.forName("reflection_student");
        System.out.println(clazz1);
        // 使用类名.class
        // 一般更多是当做参数传递  synchronized (clazz2) {}
        Class<?> clazz2 = reflection_student.class;
        System.out.println(clazz2);
        // 使用对象名.getClass()
        // 当已经有这个类的对象时，才可以使用
        reflection_student student = new reflection_student();
        Class<?> clazz3 = student.getClass();
        System.out.println(clazz3);

        /*  利用反射获取构造方法
        * Constructor<?>[] getConstructors()：返回所有公共构造方法对象的数组
        * Constructor<?>[] getDeclaredConstructors()：返回所有构造方法对象的数组，包括私有
        * Constructor<?> getConstructor(Class<?>... parameterTypes)：返回单个公共构造方法对象，参数为构造方法的参数类型
        * Constructor<?> getDeclaredConstructor(Class<?>... parameterTypes)：返回单个构造方法对象，参数为构造方法的参数类型，包括私有
        */
        System.out.println("获取所有公共构造方法：");
        Constructor<?>[] constructors = clazz1.getConstructors();
        for (Constructor<?> constructor : constructors) {System.out.println(constructor);}

        System.out.println("获取所有构造方法：");
        Constructor<?>[] declaredConstructors = clazz1.getDeclaredConstructors();
        for (Constructor<?> constructor : declaredConstructors) {System.out.println(constructor);}

        System.out.println("根据参数类型 获取单个公共构造方法：");
        try {
            Constructor<?> constructor = clazz1.getConstructor(String.class);
            System.out.println(constructor);
        } catch (NoSuchMethodException e) {
            System.out.println("未找到匹配的构造方法：" + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("根据参数类型 获取单个私有构造方法：");
        try {
            Constructor<?> constructor = clazz1.getDeclaredConstructor(String.class);
            System.out.println(constructor);
            /* 权限 int modifier = constructor.getModifiers(); 1是public 2是protected 4是private
             * 获取构造方法的参数个数 int parameterCount = constructor.getParameterCount();
             * 获取构造方法的参数类型 Class<?>[] parameterTypes = constructor.getParameterTypes();
             * 获取构造方法的所有参数 parameter[] parameters = constructor.getParameters();
             * 根据构造对象创建对象 */
            constructor.setAccessible(true); // 暴力反射，忽略访问权限 构造器的参数是啥 这里创建填啥
            try {
                reflection_student stu = (reflection_student) constructor.newInstance("张三");
                System.out.println("根据反射获取的构造方法constructor创建对象："+stu);
            } catch (ReflectiveOperationException e) {

                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            System.out.println("未找到匹配的构造方法：" + e.getMessage());
            e.printStackTrace();
        }
        /* 利用反射获取成员变量
         * Field[] getFields()：返回所有公共成员变量对象的数组
         * Field[] getDeclaredFields()：返回所有成员变量对象的数组，包括私有
         * Field getField(String name)：返回单个公共成员变量对象，参数为成员变量的名称
         * Field getDeclaredField(String name)：返回单个成员变量对象，参数为成员变量的名称，包括私有
         */
        System.out.println("获取所有公共成员变量：");
        Field[] fields = clazz1.getFields();
        for (Field field : fields) {System.out.println(field);}

        System.out.println("获取所有成员变量：");
        Field[] declaredFields = clazz1.getDeclaredFields();
        for (Field field : declaredFields) {System.out.println(field);}

        System.out.println("根据成员变量名 获取单个公共成员变量：");
        try {
            Field field = clazz1.getField("name");
            System.out.println(field);
        } catch (NoSuchFieldException e) {
            System.out.println("未找到匹配的成员变量：" + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("根据成员变量名获取单个私有成员变量，权限，类型，名称，值，修改");
        try {
            Field field = clazz1.getDeclaredField("name");
            // 获取权限修饰符 1是public 2是protected 4是private
            int modifier = field.getModifiers();
            reflection_student student1 = new reflection_student();
            field.setAccessible(true);
            try {
                String value = (String)field.get(student1);
                System.out.println("权限："+modifier+" 类型："+field.getType()+" 名称："+field.getName()+" 值："+value);
            } catch (IllegalAccessException e) {System.out.println("无法访问字段：" + field.getName());}
            try {
                field.set(student, "李四");
                System.out.println("设置值："+field.get(student));
            } catch (IllegalAccessException e) {e.printStackTrace();}
        } catch (NoSuchFieldException e) {
            System.out.println("未找到匹配的成员变量：" + e.getMessage());
            e.printStackTrace();
        }
            

        /* 利用反射获取成员方法
         * Method[] getMethods()：返回所有公共方法对象的数组
         * Method[] getDeclaredMethods()：返回所有方法对象的数组，包括私有
         * Method getMethod(String name, Class<?>... parameterTypes)：返回单个公共方法对象，参数为方法的名称和参数类型
         * Method getDeclaredMethod(String name, Class<?>... parameterTypes)：返回单个方法对象，参数为方法的名称和参数类型，包括私有*/
        System.out.println("获取所有公共方法（包括父类）：");
        Method[] methods = clazz1.getMethods();
        for (Method method : methods) {System.out.println(method);}

        System.out.println("获取所有方法（不包括父类）：");
        Method[] declaredMethods = clazz1.getDeclaredMethods();
        for (Method method : declaredMethods) {System.out.println(method);}

        System.out.println("根据方法名 获取单个公共方法（包括父类）：");
        try {
            // 获取方法的参数类型，可以获取到重载的方法
            Method method = clazz1.getDeclaredMethod("eat", String.class);
            // 获取方法的权限修饰符 1是public 2是protected 4是private
            int modifier = method.getModifiers();
            // 获取方法的名字
            String name = method.getName();
            // 获取方法的形参
            Class<?>[] parameterTypes = method.getParameterTypes();
            System.out.println("权限："+modifier+" 名称："+name);
            for (Class<?> parameterType : parameterTypes) {
                System.out.println("参数类型："+parameterType);
            }
            // 获取方法的返回类型
            Class<?> returnType = method.getReturnType();
            System.out.println("返回类型："+returnType);
            // 获取方法的异常类型
            Class<?>[] exceptionTypes = method.getExceptionTypes();
            for (Class<?> exceptionType : exceptionTypes) {System.out.println("异常类型："+exceptionType);}
            // 获取方法的注解
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {System.out.println("注解："+annotation);}
        } catch (NoSuchMethodException e) {e.printStackTrace();}        

        /* 方法运行
         * Object invoke(Object obj, Object... args)：运行方法，参数为方法的调用者对象和参数
         * 参数1：方法的调用者对象 "在这个对象中运行方法"
         * 参数2：方法的参数
         */

        try {
            reflection_student student2 = new reflection_student("sb");
            Method method = clazz1.getDeclaredMethod("eat", String.class);
            method.setAccessible(true);
            method.invoke(student2, "苹果");
            // 如果有返回值 可以接收
            Object result = method.invoke(student2, "苹果");
            System.out.println("方法的返回值："+result);
        } catch (Exception e) {e.printStackTrace();}


    }

}
