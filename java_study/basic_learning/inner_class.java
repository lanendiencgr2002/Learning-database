public class inner_class {
    public static void main(String[] args) {
        // 创建内部类对象
        Outer.Inner oi = new Outer().new Inner();
        System.out.println(oi.num);
        oi.show();
        // 演示内部类调用成员变量
        MyOuter.MyInner myInner = new MyOuter().new MyInner();
        myInner.show();
        // 演示汽车和引擎的例子
        Car.Engine engine = new Car().new Engine();
        engine.engineName = "V8";
        System.out.println("引擎名称: " + engine.engineName);
    }
}

class Outer {
    private void method() {System.out.println("Outer private method");}
    class Inner {
        int num = 10;
        public void show() {
            System.out.println("Inner show method");
            method(); // 内部类可以直接访问外部类的私有方法
        }
    }
}

class MyOuter {
    int num = 10;
    class MyInner {
        int num = 20;
        public void show() {
            int num = 30;
            System.out.println("局部变量 num: " + num); // 30
            System.out.println("内部类成员变量 num: " + this.num); // 20
            System.out.println("外部类成员变量 num: " + MyOuter.this.num); // 10
        }
    }
}

class Car {
    String carName;
    int carAge;
    String carColor;
    class Engine {
        String engineName;
        int engineAge;
    }
}
