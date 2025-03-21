public class test {
    public static void main(String[] args) {
        Parent p = new Child();
        p.test();  // 输出: "Parent's static method"
        
        Child c = new Child();
        c.test();  // 输出: "Child's static method"
    }
}

class Parent {
    public static void test() {
        System.out.println("Parent's static method");
    }
}

class Child extends Parent {
    public static void test() {
        System.out.println("Child's static method");
    }
}
