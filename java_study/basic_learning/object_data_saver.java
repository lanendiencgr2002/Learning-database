import java.io.*;
import java.lang.reflect.*;
/* 反射的作用
 * 获取一个类里面所有的信息，获取到了之后，再执行其他的业务逻辑
 * 结合配置文件，动态的创建对象并调用方法 */
// 对于任意一个对象，都可以把对象所有的字段名和值，保存到文件中去

public class object_data_saver {
    public static void main(String[] args) throws IllegalAccessException, IOException {
        //创建一个对象
        Person_R1234 p = new Person_R1234("张三", 20);
        //把对象里面所有的成员变量名和值保存到本地文件中
        saveObject(p);
    }
    @SuppressWarnings("unused")
    //把对象里面所有的成员变量名和值保存到本地文件中
    public static void saveObject(Object obj) throws IllegalAccessException, IOException {
        //1.获取字节码文件的对象
        Class<?> clazz = obj.getClass();
        
        //生成随机数
        String randomNum = String.valueOf(System.currentTimeMillis() % 10000);
        
        //2. 创建IO流 - 使用带随机数的文件名
        // BufferedWriter bw = new BufferedWriter(new FileWriter("myreflect\\person" + randomNum + ".txt"));

        //3. 获取所有的成员变量
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            //获取成员变量的名字
            String name = field.getName();
            //获取成员变量的值
            Object value = field.get(obj);
            System.out.println(name + "=" + value);
            //写出数据
            // bw.write(name + "=" + value);
            // bw.newLine();
        }

        // bw.close();
    }
    // 将 Person 改名为 Person_R1234 (R代表Random)
    public static class Person_R1234 {
        private String name;
        private int age;
        public Person_R1234(String name, int age) {
            this.name = name;
            this.age = age;
        }
        public String getName() {
            return name;

        }
        public void setName(String name) {
            this.name = name;
        }
        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            this.age = age;
        }
    }
}

