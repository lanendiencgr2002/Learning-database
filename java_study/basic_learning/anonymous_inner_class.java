public class anonymous_inner_class {
    public static void main(String[] args) {
        // 使用实现类
        useInter(new InterImpl());

        // 使用匿名内部类
        useInter(new Inter() {
            @Override
            public void show() {
                System.out.println("匿名内部类...show...");
            }
        });

        // 使用匿名内部类实现抽象类
        useAbstractClass(new AbstractClass() {
            @Override
            void abstractMethod() {
                System.out.println("匿名内部类实现抽象方法");
            }
        });
    }
    // 传InterImpl 向上转型
    public static void useInter(Inter inter) {
        inter.show();
    }

    public static void useAbstractClass(AbstractClass ac) {
        ac.abstractMethod();
    }
}

interface Inter {
    void show();
}

class InterImpl implements Inter {
    @Override
    public void show() {
        System.out.println("InterImpl...show...");
    }
}

abstract class AbstractClass {
    abstract void abstractMethod();
}
