public class polymorphism {
    public static void main(String[] args){
        System.out.println("向上转型示例");  
        Animal a = new Dog();
        a.eat(); // 调用Dog的eat方法
        // a.fetchStick(); // 错误：Animal类型没有fetchStick方法
        System.out.println("");

        System.out.println("向下转型示例");
        Dog d = (Dog)a;
        d.eat(); // 调用Dog的eat方法
        d.fetchStick(); // 现在可以调用Dog特有的fetchStick方法
        System.out.println("");

        System.out.println("向下转型的另一个例子");
        Animal animal = new Cat();
        // animal.catchMouse(); 不行
        if (animal instanceof Cat) {
            Cat cat = (Cat) animal;
            cat.eat();
            cat.catchMouse(); // 调用Cat特有的方法
        }
        System.out.println("");

        System.out.println("使用多态");
        useAnimal(new Dog());
        useAnimal(new Cat());
        useAnimal(new Bird());
        System.out.println("");

        System.out.println("instanceof 示例");
        Animal animal2 = new Dog();
        checkAnimalType(animal2);
        System.out.println("");

        System.out.println("多态数组示例");
        Animal[] animals = {new Dog(), new Cat(), new Bird()};
        for (Animal ani : animals) {
            ani.eat();
            ani.makeSound();
            if (ani instanceof Playable) {
                ((Playable) ani).play();
            }
            System.out.println();
        }
        System.out.println("");

        System.out.println("动物互动示例");
        interactWithAnimals(new Dog(), new Cat());
        interactWithAnimals(new Bird(), new Dog());
        System.out.println("");
    }

    public static void useAnimal(Animal a) {
        a.eat();
        a.makeSound();
    }

    public static void checkAnimalType(Animal animal) {
        if (animal instanceof Dog) {
            System.out.println("这是一只狗");
            ((Dog) animal).play();
        } else if (animal instanceof Cat) {
            System.out.println("这是一只猫");
            ((Cat) animal).play();
        } else if (animal instanceof Bird) {
            System.out.println("这是一只鸟");
        } else {
            System.out.println("这是一种未知的动物");
        }
    }

    public static void interactWithAnimals(Animal a1, Animal a2) {
        System.out.println("两只动物相遇了：");
        a1.makeSound();
        a2.makeSound();
        System.out.println("它们开始互动：");
        a1.interact(a2);
        a2.interact(a1);
        System.out.println();
    }
}

abstract class Animal {
    public abstract void eat();
    public abstract void makeSound();
    public abstract void interact(Animal other);
}

interface Playable {
    void play();
}

class Dog extends Animal implements Playable {
    @Override
    public void eat() {
        System.out.println("狗吃肉");
    }

    @Override
    public void makeSound() {
        System.out.println("狗汪汪叫");
    }

    @Override
    public void play() {
        System.out.println("狗在玩飞盘");
    }

    @Override
    public void interact(Animal other) {
        System.out.println("狗试图和" + (other instanceof Dog ? "另一只狗" : "其他动物") + "玩耍");
    }

    // Dog特有的方法
    public void fetchStick() {
        System.out.println("狗在捡木棍");
    }
}

class Cat extends Animal implements Playable {
    @Override
    public void eat() {
        System.out.println("猫吃鱼");
    }

    @Override
    public void makeSound() {
        System.out.println("猫喵喵叫");
    }

    @Override
    public void play() {
        System.out.println("猫在玩毛线球");
    }

    @Override
    public void interact(Animal other) {
        System.out.println("猫对" + (other instanceof Cat ? "另一只猫" : "其他动物") + "保持警惕");
    }

    // Cat特有的方法
    public void catchMouse() {
        System.out.println("猫在捉老鼠");
    }
}

class Bird extends Animal {
    @Override
    public void eat() {
        System.out.println("鸟吃虫子");
    }

    @Override
    public void makeSound() {
        System.out.println("鸟叽叽喳喳");
    }

    @Override
    public void interact(Animal other) {
        System.out.println("鸟飞到树上观察" + (other instanceof Bird ? "另一只鸟" : "其他动物"));
    }
}