public class code_block {
    private static int staticVar;
    private int instanceVar;

    // 构造代码块
    {
        instanceVar = 10;
        System.out.println("这是构造代码块: 初始化实例变量 instanceVar = " + instanceVar);
    }

    // 静态代码块
    static {
        staticVar = 100;
        System.out.println("这是静态代码块: 初始化静态变量 staticVar = " + staticVar);
    }

    // 构造方法
    public code_block() {
        System.out.println("这是构造方法: instanceVar = " + instanceVar);
    }

    // 演示局部代码块
    public void localBlockDemo() {
        System.out.println("localBlockDemo 方法开始");
        // 局部代码块
        {
            int x = 5;
            System.out.println("这是局部代码块: x = " + x);
        }
        // x 在这里已经不可访问
        System.out.println("局部代码块外部: x 不再可访问");
    }

    public static void main(String[] args) {
        System.out.println("main 方法开始");
        
        System.out.println("创建第一个对象");
        code_block obj1 = new code_block();
        System.out.println("---");
        
        System.out.println("创建第二个对象");
        @SuppressWarnings("unused")
        code_block obj2 = new code_block();
        System.out.println("---");
        
        obj1.localBlockDemo();
    }
}
