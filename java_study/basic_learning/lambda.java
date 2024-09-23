/*
 *  简化匿名内部类的代码写法 
 *  Lambda表达式，只允许操作函数式编程接口：有且仅有一个抽象方法的接口
 * 	使用限制不同匿名内部类：可以操作类，接口。
 * 	Lambda表达式：只能操作函数式接口。
 *  不同匿名内部类：编译之后，产生一个单独的 .class 字节码文件
 *  Lambda 表达式：编译之后，没有一个单独的 .class 字节码文件
 */
// 函数式接口 在Java 8中引入 如果接口不符合函数式接口的定义（即只有一个抽象方法），编译器会报错。


@FunctionalInterface
interface InterA {
    void show();
}

@FunctionalInterface
interface StringHandler {
    void printMessage(String msg);
}

public class lambda {
    public static void main(String[] args) {
        // 使用匿名内部类
        useInterA(new InterA() {
            @Override
            public void show() {
                System.out.println("匿名内部类，重写后的show方法...");
            }
        });

        // 使用Lambda表达式 只有一个没有参数的函数
        useInterA(() -> System.out.println("Lambda表达式，重写后的show方法..."));

        // 案例：使用匿名内部类
        useStringHandler(new StringHandler() {
            @Override
            public void printMessage(String msg) {
                System.out.println("匿名内部类打印：" + msg);
            }
        });

        // 案例：使用Lambda表达式 只有一个有参数的函数
        useStringHandler(msg -> System.out.println("Lambda表达式打印：" + msg));
    }

    public static void useInterA(InterA a) {
        a.show();
    }

    public static void useStringHandler(StringHandler stringHandler) {
        stringHandler.printMessage("itheima");
    }
}
