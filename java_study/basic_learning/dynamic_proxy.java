import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
// 动态代理 比如明星唱歌，跳舞，经纪人帮忙准备话筒，舞台 经纪人就是代理
public class dynamic_proxy {
    public static void main(String[] args) {
        // 1. 获取代理的对象
        BigStar bigStar = new BigStar("大明星");
        Star proxy = createProxy(bigStar);
 
        // 2. 调用代理的唱歌方法 会帮忙准备话筒，舞台
        proxy.sing("一首歌"); // 会自动调用invoke方法 
        proxy.dance();
    }

    public static class BigStar implements Star {
        private String name;

        // 添加构造函数
        public BigStar(String name) {
            this.name = name;
        }

        @Override
        public String sing(String song){
            System.out.println(this.name+"正在唱歌"+song);
            return "唱完歌了";
        }
        @Override
        public String dance(){
            System.out.println(this.name+"正在跳舞");
            return "跳完舞了";

        }
    }

    public interface Star{
        public String sing(String song);
        public String dance();
    }

    public static Star createProxy(BigStar bigStar){
    /* java.lang.reflect.Proxy类：提供了创建动态代理类和实例的静态方法，它也是由这些方法创建的所有动态代理类的超类。
     *  public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)
     *  - 方法作用：在指定类加载器中，为制定的接口组生成一个动态代理类
     *  - 参数：
     *      - ClassLoader loader：指定用哪个类加载器，去加载生成的代理类
     *      - Class<?>[] interfaces：指定接口数组，这些接口将被代理类实现，指定接口，这些接口用于指定生成的代理长什么，也就是有哪些方法
     *      - InvocationHandler h：用来指定生成的代理对象要干什么事情
     *  - 返回值：代理类对象
     */
        Star star = (Star) Proxy.newProxyInstance(
            dynamic_proxy.class.getClassLoader(),
            new Class[]{Star.class}, 
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    /*
                     * 参数：
                     *  - Object proxy：代理对象
                     *  - Method method：代理对象调用的方法 比如sing方法,dance方法
                     *  - Object[] args：代理对象调用的方法的参数 比如sing方法的参数是"一首歌"，dance方法的参数是null
                     *  - Object result：方法的返回值
                     */
                    // 在这里可以添加代理逻辑，比如打印日志等
                    if("sing".equals(method.getName())){
                        System.out.println("代理准备话筒");
                    }else if("dance".equals(method.getName())){
                        System.out.println("代理准备舞台");
                    }
                    Object result = method.invoke(bigStar, args);
                    return result;
                }
            }

        );
        return star;
    
    }

}
