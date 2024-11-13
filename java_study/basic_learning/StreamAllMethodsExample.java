import java.util.*;
import java.util.stream.*;

@SuppressWarnings("all")
/**
 * Stream API 所有主要方法的详细示例
 * 
 * 方法引用说明：
 * 1. String::length 是方法引用的语法，等同于 str -> str.length()
 * 2. 方法引用有四种形式：
 *    - 静态方法引用：ClassName::staticMethod
 *    - 实例方法引用：instance::method
 *    - 对象方法引用：ClassName::instanceMethod
 *    - 构造方法引用：ClassName::new
 * 
 * 本例中的 String::length 是对象方法引用，它会自动将流中的每个元素作为调用对象：
 * - 完整写法：.map(str -> str.length())
 * - 简化写法：.map(String::length)
 * 两种写法效果完全相同，但方法引用更简洁优雅
 * 3. 方法引用与Lambda表达式对比示例：
 * 方法引用写法：
 * whitePathList.stream().anyMatch(requestPath::startsWith)
 * 等价的Lambda表达式写法：
 * whitePathList.stream().anyMatch(whitePath -> requestPath.startsWith(whitePath))
 */
public class StreamAllMethodsExample {
    
    public static void main(String[] args) {
        // 准备测试数据
        List<String> fruits = Arrays.asList("apple", "banana", "orange", "watermelon", "grape");
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 1. 创建Stream的方法
        demonstrateStreamCreation();
        
        // 2. 中间操作演示
        demonstrateIntermediateOperations(fruits, numbers);
        
        // 3. 终端操作演示
        demonstrateTerminalOperations(fruits, numbers);
        
        // 4. 特殊Stream操作
        demonstrateSpecialStreamOperations();
    }

    /**
     * 展示创建Stream的各种方法
     */
    private static void demonstrateStreamCreation() {
        // === Stream创建方法演示 ===
        
        List<String> list = Arrays.asList("a", "b", "c");
        Stream<String> streamFromCollection = list.stream();
        // 从集合创建结果: [a, b, c]
        
        String[] array = {"x", "y", "z"};
        Stream<String> streamFromArray = Arrays.stream(array);
        // 从数组创建结果: [x, y, z]
        
        Stream<Integer> streamFromValues = Stream.of(1, 2, 3);
        // 使用Stream.of创建结果: [1, 2, 3]
        
        Stream<Integer> infiniteStream = Stream.iterate(0, n -> n + 2).limit(5);
        // 创建无限流(前5个)结果: [0, 2, 4, 6, 8]
    }

    /**
     * 展示Stream的中间操作
     */
    private static void demonstrateIntermediateOperations(List<String> fruits, List<Integer> numbers) {
        // === Stream中间操作演示 ===
        
        List<String> longFruits = fruits.stream()
                .filter(f -> f.length() > 5)
                .collect(Collectors.toList());
        // filter过滤长度>5的水果结果: [banana, watermelon]
        
        List<Integer> fruitLengths = fruits.stream()
                .map(String::length)
                .collect(Collectors.toList());
        // map转换水果名称长度结果: [5, 6, 6, 10, 5]
        
        List<List<Integer>> nestedNumbers = Arrays.asList(
                Arrays.asList(1, 2), 
                Arrays.asList(3, 4)
        );
        List<Integer> flattenedNumbers = nestedNumbers.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        // flatMap扁平化结果: [1, 2, 3, 4]
        
        List<Integer> distinctNumbers = Stream.of(1, 1, 2, 2, 3)
                .distinct()
                .collect(Collectors.toList());
        // distinct去重结果: [1, 2, 3]
        
        List<String> sortedFruits = fruits.stream()
                .sorted()
                .collect(Collectors.toList());
        // sorted排序结果: [apple, banana, grape, orange, watermelon]
        
        List<String> peekResult = fruits.stream()
                .peek(f -> System.out.println("Peeking: " + f))
                .collect(Collectors.toList());
        // peek查看元素时会打印每个元素
    }

    /**
     * 展示Stream的终端操作
     */
    private static void demonstrateTerminalOperations(List<String> fruits, List<Integer> numbers) {
        // === Stream终端操作演示 ===
        
        Set<String> fruitsSet = fruits.stream()
                .collect(Collectors.toSet());
        // collect转Set结果: [orange, banana, apple, watermelon, grape]
        
        Map<String, Integer> fruitsMap = fruits.stream()
                .collect(Collectors.toMap(
                        fruit -> fruit,
                        String::length,
                        (e1, e2) -> e1
                ));
        // collect转Map结果: {orange=6, banana=6, apple=5, watermelon=10, grape=5}
        
        Optional<Integer> sum = numbers.stream()
                .reduce(Integer::sum);
        // reduce求和结果: 55
        
        long count = fruits.stream().count();
        Optional<String> min = fruits.stream().min(Comparator.naturalOrder());
        Optional<String> max = fruits.stream().max(Comparator.naturalOrder());
        // count计数结果: 5
        // min最小值结果: apple
        // max最大值结果: watermelon
        
        // forEach遍历前3个元素: apple banana orange
        fruits.stream().limit(3).forEach(f -> System.out.print(f + " "));
        
        boolean anyLongFruit = fruits.stream().anyMatch(f -> f.length() > 6);
        boolean allShortFruit = fruits.stream().allMatch(f -> f.length() < 10);
        boolean noEmptyFruit = fruits.stream().noneMatch(String::isEmpty);
        // anyMatch结果(是否有长度>6的水果): true
        // allMatch结果(是否所有水果长度<10): false
        // noneMatch结果(是否没有空字符串): true
    }

    /**
     * 展示特殊的Stream操作
     */
    private static void demonstrateSpecialStreamOperations() {
        // === 特殊Stream操作演示 ===
        
        // 准备数据...
        List<Integer> largeList = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            largeList.add(i);
        }
        
        long serialSum = largeList.stream()
                .mapToLong(i -> i)
                .sum();
        // 串行流求和结果: 499999500000
        
        long parallelSum = largeList.parallelStream()
                .mapToLong(i -> i)
                .sum();
        // 并行流求和结果: 499999500000
        
        IntStream intStream = IntStream.range(1, 5);
        // IntStream求和结果: 10
        
        DoubleStream doubleStream = DoubleStream.of(1.1, 2.2, 3.3);
        // DoubleStream平均值结果: 2.2
        
        List<String> words = Arrays.asList("hello", "world", "java", "stream", "api");
        
        Map<Integer, List<String>> groupByLength = words.stream()
                .collect(Collectors.groupingBy(String::length));
        // 按长度分组结果: {3=[api], 4=[java], 5=[hello, world], 6=[stream]}
        
        Map<Boolean, List<String>> partitioned = words.stream()
                .collect(Collectors.partitioningBy(w -> w.length() > 4));
        // 按长度分区结果: {false=[api, java], true=[hello, world, stream]}
    }
} 