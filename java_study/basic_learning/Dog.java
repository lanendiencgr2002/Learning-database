/**
 * 狗类 - Animal的具体实现类
 */
public class Dog extends Animal {
    public Dog(String name) {
        super(name);
    }
    
    @Override
    public void makeSound() {
        System.out.println(name + "说：汪汪汪！");
    }
    
    // 狗类特有的方法
    public void fetchBall() {
        System.out.println(name + "正在接球");
    }
} 