/**
 * 猫类 - Animal的具体实现类
 */
public class Cat extends Animal {
    public Cat(String name) {
        super(name);
    }
    
    @Override
    public void makeSound() {
        System.out.println(name + "说：喵喵喵~");
    }
    
    // 猫类特有的方法
    public void catchMouse() {
        System.out.println(name + "正在抓老鼠");
    }
} 