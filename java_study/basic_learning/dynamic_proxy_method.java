import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
public class dynamic_proxy_method {
    public static void main(String[] args) {
        UserInterface1 user = new user666("user666", "123456");
        UserInterface1 proxy = (UserInterface1) getProxyInstance(user);
        proxy.show();
    }

    public static Object getProxyInstance(Object target) {
        return Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    System.out.println("代理开始: " + method.getName());
                    Object result = method.invoke(target, args);
                    System.out.println("代理结束: " + method.getName());
                    return result;
                }
            }
        );
    }
    public static class user666 implements UserInterface1{
        String name;
        String password;
        public user666(String name, String password){
            this.name = name;
            this.password = password;
        }
        @Override
        public void show(){
            System.out.println("user666 show");
        }
    }
    
}



