/**
 * 动物抽象基类
 * 演示多态的基础类
 */
public abstract class Animal {
    protected String name;
    
    public Animal(String name) {
        this.name = name;
    }
    
    // 抽象方法，强制子类实现
    public abstract void makeSound();
    
    // 普通方法，可以被子类继承或重写
    public void move() {
        System.out.println(name + "正在移动");
    }
    
    // getter方法
    public String getName() {
        return name;
    }
} 