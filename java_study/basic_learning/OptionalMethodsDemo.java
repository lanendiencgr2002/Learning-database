import java.util.Optional;
import java.util.List;
import java.util.Arrays;
@SuppressWarnings("all")
public class OptionalMethodsDemo {
    // 自定义类用于演示
    static class User {
        private String name;
        public User(String name) { this.name = name; }
        public String getName() { return name; }
    }
    
    public static void main(String[] args) {
        // 0. Optional 可以包含的值类型示例
        // 0.1 基本类型的包装类
        Optional<Integer> optionalInt = Optional.of(100);
        Optional<Double> optionalDouble = Optional.of(3.14);
        Optional<Boolean> optionalBool = Optional.of(true);
        System.out.println("0.1 基本类型: " + optionalInt.get() + ", " + 
            optionalDouble.get() + ", " + optionalBool.get());
        // 输出: 0.1 基本类型: 100, 3.14, true
        
        // 0.2 字符串
        Optional<String> optionalString = Optional.of("Hello");
        System.out.println("0.2 字符串: " + optionalString.get());
        // 输出: 0.2 字符串: Hello
        
        // 0.3 自定义对象
        User user = new User("张三");
        Optional<User> optionalUser = Optional.of(user);
        System.out.println("0.3 用户名: " + optionalUser.map(User::getName).orElse("未知"));
        // 输出: 0.3 用户名: 张三
        
        // 0.4 集合类型
        List<String> list = Arrays.asList("A", "B", "C");
        Optional<List<String>> optionalList = Optional.of(list);
        System.out.println("0.4 列表大小: " + optionalList.map(List::size).orElse(0));
        // 输出: 0.4 列表大小: 3

        // 1. 创建 Optional 对象的方法
        // 1.1 创建包含非空值的 Optional
        Optional<String> optional1 = Optional.of("Hello");
        System.out.println("1.1 of()方法: " + optional1.get());
        // 输出: 1.1 of()方法: Hello
        
        // 1.2 创建可以包含 null 的 Optional
        String nullableValue = null;
        Optional<String> optional2 = Optional.ofNullable(nullableValue);
        System.out.println("1.2 ofNullable()方法: " + optional2.orElse("空值"));
        // 输出: 1.2 ofNullable()方法: 空值
        
        // 1.3 创建空的 Optional
        Optional<String> optional3 = Optional.empty();
        
        // 2. 判断值是否存在
        // 2.1 使用 isPresent()
        if (optional1.isPresent()) {
            System.out.println("2.1 值存在: " + optional1.get());
        }
        // 输出: 2.1 值存在: Hello
        
        // 2.2 使用 isEmpty() (Java 11+)
        if (optional3.isEmpty()) {
            System.out.println("2.2 值为空");
        }
        // 输出: 2.2 值为空
        
        // 3. 获取值的方法
        // 3.1 使用 orElse() - 提供默认值
        String result1 = optional3.orElse("默认值");
        System.out.println("3.1 orElse(): " + result1);
        // 输出: 3.1 orElse(): 默认值
        
        // 3.2 使用 orElseGet() - 提供默认值的供应商
        String result2 = optional3.orElseGet(() -> "动态生成的默认值");
        System.out.println("3.2 orElseGet(): " + result2);
        // 输出: 3.2 orElseGet(): 动态生成的默认值
        
        // 3.3 使用 orElseThrow() - 抛出异常
        try {
            String result3 = optional3.orElseThrow(() -> 
                new IllegalStateException("没有值"));
        } catch (IllegalStateException e) {
            System.out.println("3.3 orElseThrow(): " + e.getMessage());
        }
        // 输出: 3.3 orElseThrow(): 没有值
        
        // 4. 值的转换和处理
        // 4.1 使用 map() 转换值
        Optional<Integer> lengthOptional = optional1.map(String::length);
        System.out.println("4.1 map(): " + lengthOptional.orElse(0));
        // 输出: 4.1 map(): 5
        
        // 4.2 使用 filter() 过滤值
        Optional<String> filtered = optional1.filter(s -> s.length() > 3);
        System.out.println("4.2 filter(): " + filtered.orElse("值被过滤"));
        // 输出: 4.2 filter(): Hello
        
        // 4.3 使用 flatMap() 处理返回 Optional 的映射
        Optional<String> flatMapped = optional1.flatMap(s -> 
            Optional.of(s.toUpperCase()));
        System.out.println("4.3 flatMap(): " + flatMapped.orElse(""));
        // 输出: 4.3 flatMap(): HELLO
        
        // 5. 条件执行
        // 5.1 使用 ifPresent() 在值存在时执行操作
        optional1.ifPresent(s -> 
            System.out.println("5.1 ifPresent(): 值存在: " + s));
        // 输出: 5.1 ifPresent(): 值存在: Hello
        
        // 5.2 使用 ifPresentOrElse() (Java 9+)
        optional3.ifPresentOrElse(
            s -> System.out.println("值存在: " + s),
            () -> System.out.println("5.2 ifPresentOrElse(): 值不存在")
        );
        // 输出: 5.2 ifPresentOrElse(): 值不存在
        
        // 6. 链式调用示例
        String result = Optional.ofNullable("Hello")
            .filter(s -> s.length() > 3)
            .map(String::toUpperCase)
            .flatMap(s -> Optional.of(s + " World"))
            .orElse("默认值");
        System.out.println("6. 链式调用结果: " + result);
        // 输出: 6. 链式调用结果: HELLO World
    }
} 