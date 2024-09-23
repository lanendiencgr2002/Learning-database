import java.util.Optional;

/*
Optional 类的介绍：
1. Optional 是一个容器类，它可以包含一个非空值，也可以不包含任何值。
2. Optional 类是 Java 8 引入的，用于解决空指针异常（NullPointerException）的问题。
3. Optional 类提供了一系列方法来处理可能为空的对象，从而避免空指针异常。
4. 使用Optional可以简化非空判断操作。
*/

public class optional_example {

    public static void main(String[] args) {
        // 创建一个包含值的 Optional
        Optional<String> optionalWithValue = Optional.of("Hello, Optional!");
        
        // 创建一个空的 Optional
        Optional<String> emptyOptional = Optional.empty();
        
        // 使用 isPresent() 和 get()
        if (optionalWithValue.isPresent()) {
            System.out.println("值存在: " + optionalWithValue.get());
        }
        
        // 使用 orElse()
        String result = emptyOptional.orElse("默认值");
        System.out.println("空 Optional 的结果: " + result);
        
        // 使用 ofNullable() 和 orElse()
        String nullableValue = null;
        String safeResult = Optional.ofNullable(nullableValue).orElse("安全的默认值");
        System.out.println("可能为空的值的结果: " + safeResult);
    }
}
