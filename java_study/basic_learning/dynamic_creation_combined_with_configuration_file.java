import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Properties;
/* 配置文件内容：
 * classname=com.itheima.myreflect6.Student
 * method=study */

 public class dynamic_creation_combined_with_configuration_file {
    public static void main(String[] args) {
        try {
            //1.读取配置文件中的信息
            Properties prop = new Properties();
            FileInputStream fis = new FileInputStream("myreflect\\prop.properties");
            prop.load(fis);
            fis.close();
            System.out.println(prop);
            //2.获取全类名和方法名
            String className = (String) prop.get("classname");
            String methodName = (String) prop.get("method");
            System.out.println(className);
            System.out.println(methodName);
            //3.利用反射创建对象并运行方法
            Class<?> clazz = Class.forName(className);
            //获取构造方法
            Constructor<?> con = clazz.getDeclaredConstructor();
            Object o = con.newInstance();
            System.out.println(o);
            //获取成员方法并运行
            Method method = clazz.getDeclaredMethod(methodName);
            method.invoke(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}