public class static_inner_class {
    public static void main(String[] args) {
        // 创建静态内部类对象
        OuterClass.InnerClass innerObj = new OuterClass.InnerClass();
        innerObj.show();

        // 直接调用静态内部类的静态方法
        OuterClass.InnerClass.staticShow();
    }
}
@SuppressWarnings("unused")
class OuterClass {
    private int num1 = 10;
    private static int num2 = 20;
    
    static class InnerClass {
        public void show() {
            // System.out.println(num1); // 错误：静态内部类不能直接访问外部类的非静态成员
            System.out.println("外部类的静态成员 num2: " + num2);
        }

        public static void staticShow() {
            System.out.println("这是静态内部类的静态方法");
            System.out.println("外部类的静态成员 num2: " + num2);
        }
    }
}
