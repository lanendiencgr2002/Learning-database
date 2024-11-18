import java.util.Map;
import java.util.HashMap;

/**
 * 泛型方法示例类
 * 演示了Java泛型方法的几种常见使用场景：
 * 1. 基础泛型方法
 * 2. 多类型参数泛型方法
 * 3. 带类型限定的泛型方法
 */
public class GenericMethodExample {
    /**
     * 基础泛型方法：将Object类型转换为指定类型
     * 
     * 关于<T>的位置说明：
     * 1. 语法要求：<T>必须放在修饰符(public)和返回类型之间
     * 2. 这样放置的原因：
     *    - 让编译器能够在解析方法时就知道T是什么类型
     *    - 区分泛型方法和使用类泛型参数的普通方法
     *    - 如果放在返回类型后面，编译器无法确定返回类型中的T
     * 3. 与泛型类的区别：
     *    - 泛型类的<T>是放在类名后面：class Name<T>
     *    - 泛型方法的<T>是放在方法修饰符后面：public <T> T method()
     * 
     * @param obj 需要转换的对象
     * @param <T> 目标类型参数
     * @return 转换后的T类型对象
     * 
     * 注意：这种转换可能产生ClassCastException，实际使用时需要确保类型安全
     */
    public <T> T convertData(Object obj) {
        return (T) obj;
    }

    /**
     * 多类型参数泛型方法：创建并返回包含键值对的Map
     * 
     * 这里的<K, V>同样遵循泛型方法的语法规则：
     * - 放在修饰符后，返回类型前
     * - 可以声明多个类型参数，用逗号分隔
     * 
     * @param key 键对象
     * @param value 值对象
     * @param <K> 键的类型参数
     * @param <V> 值的类型参数
     * @return 包含指定键值对的新Map对象
     */
    public <K, V> Map<K, V> createMap(K key, V value) {
        Map<K, V> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    /**
     * 带类型限定的泛型方法：计算数字数组的总和
     * 
     * 这里的<T extends Number>展示了泛型方法的类型限定：
     * - 仍然遵循位置规则：放在修饰符后，返回类型前
     * - extends关键字用于限定T必须是Number或其子类
     * - 这种限定让编译器知道T类型的对象可以调用Number类的方法
     * 
     * @param numbers 数字数组
     * @param <T> 限定为Number子类的类型参数
     * @return 数组元素的总和
     */
    public <T extends Number> double sumNumbers(T[] numbers) {
        double sum = 0.0;
        for (T num : numbers) {
            sum += num.doubleValue(); // 因为T extends Number，所以可以调用doubleValue()
        }
        return sum;
    }

    public static void main(String[] args) {
        GenericMethodExample example = new GenericMethodExample();
        
        // 演示1：显式指定泛型类型
        // 这里的<String>是在调用时明确指定类型参数
        String str = example.<String>convertData("测试");
        Integer num = example.<Integer>convertData(100);
        
        // 演示2：利用类型推断机制
        // Java 7之后，编译器可以根据上下文推断泛型类型，使代码更简洁
        // 所以这里可以省略<String>
        String str2 = example.convertData("测试");

        // 演示3：使用带限定的泛型方法
        Integer[] numbers = {1, 2, 3, 4, 5};
        double sum = example.sumNumbers(numbers); // Integer是Number的子类，符合类型限定
        System.out.println("数组总和：" + sum);
    }
} 