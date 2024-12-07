/**
 * 多态示例演示类
 */
public class PolymorphismDemo {
    public static void main(String[] args) {
        // 使用多态方式创建对象
        Animal cat = new Cat("小花");
        Animal dog = new Dog("旺财");
        
        // 演示多态调用
        System.out.println("=== 多态方法调用 ===");
        cat.makeSound();  // 调用Cat的方法
        dog.makeSound();  // 调用Dog的方法
        
        // 所有动物都可以移动
        cat.move();
        dog.move();
        
        System.out.println("\n=== 类型转换 ===");
        // 向下转型访问子类特有方法
        if(cat instanceof Cat) {
            Cat realCat = (Cat) cat;
            realCat.catchMouse();
        }
        
        if(dog instanceof Dog) {
            Dog realDog = (Dog) dog;
            realDog.fetchBall();
        }
        
        // 演示多态数组
        System.out.println("\n=== 多态数组 ===");
        Animal[] animals = {
            new Cat("咪咪"),
            new Dog("大黄"),
            new Cat("花花")
        };
        
        // 循环调用方法
        for(Animal animal : animals) {
            animal.makeSound();
        }
    }
} 