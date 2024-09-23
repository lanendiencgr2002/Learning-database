import java.util.ArrayList;
import java.util.Objects;

public class object {
    // 静态变量示例
    public static String schoolName = "学院";

    public static void main(String[] args) {
        // static 示例
        System.out.println("=== static 示例 ===");
        System.out.println("学校名称：" + schoolName);
        int[] numbers = {5, 3, 8, 1, 9, 2};
        System.out.println("最大值：" + getMax(numbers));

        // Object类方法示例
        System.out.println("\n=== Object类方法示例 ===");
        
        // toString() 演示
        A a = new A();
        // return getclass().getName() + '@' + Integer.toHexString(hashcode());
        System.out.println("默认toString(): " + a); // 默认toString() 是对象的内存地址
        
        B b = new B();

        System.out.println("重写toString(): " + b);
        
        // 原始equals() 演示 比较的是地址
        DefaultEquals de1 = new DefaultEquals(1);
        DefaultEquals de2 = new DefaultEquals(1);

        DefaultEquals de3 = de1;
        System.out.println("de1.equals(de2): " + de1.equals(de2)); // 应该返回false
        System.out.println("de1.equals(de3): " + de1.equals(de3)); // 应该返回true
        
        // 重写equals() 演示
        Person p1 = new Person("张三", 20);
        Person p2 = new Person("张三", 20);
        Person p3 = new Person("李四", 25);
        
        System.out.println("p1.equals(p2): " + p1.equals(p2));
        System.out.println("p1.equals(p3): " + p1.equals(p3));
        
        // ArrayList toString() 演示
        ArrayList<String> list = new ArrayList<>();
        list.add("苹果");
        list.add("香蕉");
        list.add("橙子");
        System.out.println("ArrayList: " + list);
    }

    // 静态方法示例
    public static int getMax(int[] arr) {
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }

    // 将A类移到这里，作为静态内部类
    static class A {
        // 使用默认的toString()方法
    }
}

class B {
    @Override
    public String toString() {
        return "这是B类的对象";
    }
}
@SuppressWarnings("unused")
// 新增：使用默认equals()方法的类
class DefaultEquals {
    private int value;

    public DefaultEquals(int value) {
        this.value = value;
    }
    // 不重写equals()方法，使用Object类的默认实现
}

class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return age == person.age && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age);
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
}
