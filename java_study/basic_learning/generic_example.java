/*
 * 泛型： 泛型不支持基本数据类型，只能支持对象类型型（引用数据类型） 可以用包装类代替
 * 泛型类、泛型方法、泛型接口(未演示)
 * 泛型上界、泛型下界
 * 泛型通配符
 */
import java.util.ArrayList;

public class generic_example {
    /* 定义类、接口、方法时，同时声明了一个或者多个类型变量（如：<E>），这些类型变量可以在类、接口、方法中使用
     * 泛型的本质把具体的数据类型作为参数传给类型变量
     * 类型变量建议用大写的英文字母，常用的有：E、T、K、V等
     * E：元素 element T：类型 type K：关键字 key V：值 value ?：表示任意类型
    */
    public static void main(String[] args) {
        ArrayList<String> list = new ArrayList<>();
        list.add("hello");
        String s = list.get(0); // 不需要进行类型转换 因为类型变量已经声明为String类型
        System.out.println(s);

        ArrayList<Integer> list2 = new ArrayList<>();
        list2.add(1);
        list2.add(2);
        list2.add(3);
        // 使用泛型方法
        System.out.println("使用泛型方法 打印list2");
        printArray(list2);

        // 使用新的MyArrayList类
        System.out.println("使用新的MyArrayList类,泛型类");
        MyArrayList<String> myList = new MyArrayList<>();
        myList.add("世界");
        myList.add("你好");
        System.out.println(myList.toString());

        // 新增的汽车游戏示例
        ArrayList<Xiaomi> xiaomis = new ArrayList<>();
        xiaomis.add(new Xiaomi());
        xiaomis.add(new Xiaomi());
        xiaomis.add(new Xiaomi());
        go(xiaomis);

        ArrayList<BYD> byds = new ArrayList<>();
        byds.add(new BYD());
        byds.add(new BYD());
        byds.add(new BYD());
        go(byds);

        ArrayList<Pet> pets = new ArrayList<>();
        pets.add(new Pet());
        pets.add(new Pet());
        pets.add(new Pet());
        // go(pets); // 这行会导致编译错误，因为Pet不是Vehicle的子类型

        // 演示泛型上界
        System.out.println("演示泛型上界：");
        ArrayList<Vehicle> Vehicles = new ArrayList<>();
        Vehicles.add(new Vehicle());
        Vehicles.add(new Xiaomi());
        Vehicles.add(new BYD());
        go(Vehicles);
    }

    public static <T> void printArray(ArrayList<T> list) { //或者public static void printArray(ArrayList<?> list) {
        for (Object o : list) {
            System.out.println(o);
        }
    }

    // 新增的使用泛型上界的方法 能接受的必须是Vehicle自己或子类  泛型上界： ? super Vehicle 能接受的必须是Vehicle自己或父类
    public static void go(ArrayList<? extends Vehicle> Vehicles) { 
        System.out.println("处理汽车列表，包含 " + Vehicles.size() + " 辆车");
        // 这里可以安全地读取Vehicles中的元素，因为它们都是Vehicle类型
        for (Vehicle Vehicle : Vehicles) {
            Vehicle.drive();
        }
    }

    public static<T> T printArray2(T[]names){
        for(T name:names){
            System.out.println(name);
        }
        return null; // 返回值类型为T 
    }
}

// 泛型类
class MyArrayList<E> {
    private ArrayList<E> list = new ArrayList<>();

    public boolean add(E e) {
        list.add(e);
        return true;
    }

    public boolean remove(E e) {
        return list.remove(e);
    }

    @Override
    public String toString() {
        return list.toString();
    }
}

// 为示例添加必要的类
class Vehicle {
    public void drive() {
        System.out.println("汽车行驶中");
    }
}

class Xiaomi extends Vehicle {
    @Override
    public void drive() {
        System.out.println("小米汽车行驶中");
    }
}

class BYD extends Vehicle {
    @Override
    public void drive() {
        System.out.println("比亚迪汽车行驶中");
    }
}

// 将 Dog 类改名为 Pet
class Pet {
    public void makeSound() {
        System.out.println("宠物发出声音!");
    }
}
