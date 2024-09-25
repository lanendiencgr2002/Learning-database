@SuppressWarnings("all")
public class interface_example {
    public static void main(String[] args) {
        // 测试默认方法
        Inter obj1 = new InterImpl();
        obj1.method();

        // 测试静态方法
        A.function();

        // 测试多接口默认方法冲突
        MultiInterfaceImpl obj2 = new MultiInterfaceImpl();
        obj2.method();

        // 测试带有私有静态方法的接口
        PrivateMethodInterfaceImpl obj3 = new PrivateMethodInterfaceImpl();
        obj3.start();
        obj3.end();
    }
}

interface Inter {
    void show();
    void print();
    // 之后的实现接口 可以不用重写方法 可以直接调用method方法  省略了public
    default void method() {
        System.out.println("Inter...method");
    }
}

class InterImpl implements Inter {
    @Override
    public void show() {
        System.out.println("InterImpl...show");
    }

    @Override
    public void print() {
        System.out.println("InterImpl...print");
    }

    // 可以选择重写默认方法
    @Override
    public void method() {
        System.out.println("InterImpl...method");
    }
}

interface A {
    // 静态方法 ： 接口名.方法名
    static void function() {
        System.out.println("A...static...function");
    }
}

interface MyInterface {
    default void method() {
        System.out.println("MyInterface...method");
    }
}

// 处理多接口默认方法冲突 ：一定要重写默认重复的方法
class MultiInterfaceImpl implements Inter, MyInterface {
    @Override
    public void show() {
        System.out.println("MultiInterfaceImpl...show");
    }

    @Override
    public void print() {
        System.out.println("MultiInterfaceImpl...print");
    }

    @Override
    public void method() {
        Inter.super.method(); // 选择调用 Inter 接口的默认方法
    }
}

interface PrivateMethodInterface {
    void show();
    void print();
    
    default void start() {
        System.out.println("start方法执行...");
        log();
    }
    
    default void end() {
        System.out.println("end方法执行...");
        log();
    }
    
    private static void log() {
        System.out.println("日志记录");
    }
}

class PrivateMethodInterfaceImpl implements PrivateMethodInterface {
    @Override
    public void show() {
        System.out.println("PrivateMethodInterfaceImpl...show");
    }

    @Override
    public void print() {
        System.out.println("PrivateMethodInterfaceImpl...print");
    }
}
