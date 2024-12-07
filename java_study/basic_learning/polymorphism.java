/**
 * Java多态中的向上转型和向下转型说明：
 * 
 * 1. 向上转型（Upcasting）
 *    - 定义：将子类对象赋值给父类引用
 *    - 示例：Animal dog = new Dog();
 *    - 特点：
 *      a) 自动进行，不需要强制类型转换
 *      b) 安全，不会出现异常
 *      c) 只能访问父类定义的方法和属性
 *      d) 如果子类重写了父类方法，调用时执行子类的方法（多态）
 * 
 * 2. 向下转型（Downcasting）
 *    - 定义：将父类引用转换为子类引用
 *    - 示例：Dog dog = (Dog)animal;
 *    - 特点：
 *      a) 需要显式的强制类型转换
 *      b) 有风险，可能出现ClassCastException异常
 *      c) 可以访问子类特有的方法和属性
 * 
 * 3. 注意事项：
 *    - 向下转型前必须用instanceof检查类型（安全，避免ClassCastException异常）5
 *    - 只能转换有继承关系的类型
 *    - 不能将父类对象转为子类类型
 * 
 * 4. 最佳实践：
 *    if (animal instanceof Dog) {
 *        Dog dog = (Dog)animal;    // 安全的向下转型
 *        dog.fetchStick();         // 调用子类特有方法
 *    }
 */

/**
 * 多态演示类
 * 展示Java多态的核心概念：继承、接口实现、向上转型、向下转型
 */
public class polymorphism {
    public static void main(String[] args) {
        // 1. 基本多态示例
        Animal dog = new Dog();  // 向上转型
        dog.eat();
        ((Dog)dog).fetchStick(); // 向下转型

        // 2. instanceof使用和安全转型
        Animal cat = new Cat();
        if (cat instanceof Cat) {
            ((Cat)cat).catchMouse();
        }

        // 3. 多态数组示例
        Animal[] animals = {new Dog(), new Cat(), new Bird()};
        for (Animal animal : animals) {
            animal.makeSound();
            if (animal instanceof Playable) {
                ((Playable) animal).play();
            }
        }

        // 4. 动物互动示例
        interactWithAnimals(new Dog(), new Cat());
    }

    // 动物互动方法
    private static void interactWithAnimals(Animal a1, Animal a2) {
        a1.interact(a2);
        a2.interact(a1);
    }
}

// 动物基类
abstract class Animal {
    public abstract void eat();
    public abstract void makeSound();
    public abstract void interact(Animal other);
}

// 可玩耍接口
interface Playable {
    void play();
}

// 狗类实现
class Dog extends Animal implements Playable {
    public void eat() { System.out.println("狗吃肉"); }
    public void makeSound() { System.out.println("汪汪叫"); }
    public void play() { System.out.println("玩飞盘"); }
    public void interact(Animal other) { 
        System.out.println("狗想和" + (other instanceof Dog ? "另一只狗" : "其他动物") + "玩耍"); 
    }
    public void fetchStick() { System.out.println("捡木棍"); }
}

// 猫类实现
class Cat extends Animal implements Playable {
    public void eat() { System.out.println("猫吃鱼"); }
    public void makeSound() { System.out.println("喵喵叫"); }
    public void play() { System.out.println("玩毛线球"); }
    public void interact(Animal other) { 
        System.out.println("猫对" + (other instanceof Cat ? "另一只猫" : "其他动物") + "保持警惕"); 
    }
    public void catchMouse() { System.out.println("捉老鼠"); }
}

// 鸟类实现
class Bird extends Animal {
    public void eat() { System.out.println("鸟吃虫"); }
    public void makeSound() { System.out.println("叽叽喳喳"); }
    public void interact(Animal other) { 
        System.out.println("鸟飞到树上观察" + (other instanceof Bird ? "另一只鸟" : "其他动物")); 
    }
}