import java.util.*;
import java.util.function.Function;
import java.util.stream.*;
@SuppressWarnings("all")
public class stream_example {
    /* 
     * 流（Stream）是Java 8引入的一种新的数据处理方式，可以用于操作集合或者数组的数据
     * 优势：Stream流大量的结合了Lambda的语法风格来编程，功能强大，性能高效，代码简洁，可读性好。
     */
    public static void main(String[] args) {
        // stream是一个接口，有个方法叫stream()，返回一个Stream流对象

        // 1. 获取集合的Stream流
        Collection<String> list2 = new ArrayList<>();
        Stream<String> s1 = list2.stream();

        // 2. Map集合，怎么获取Stream流
        Map<String, Integer> map = new HashMap<>();
        // 获取键流
        Stream<String> s2 = map.keySet().stream();
        // 获取值流
        Stream<Integer> s3 = map.values().stream();
        // 获取键值对流
        Stream<Map.Entry<String, Integer>> s4 = map.entrySet().stream();

        // 3. 获取数组的流
        String[] names = {"张三丰", "张无忌", "张翠山", "张良", "张学友"};
        Stream<String> s5 = Arrays.stream(names);
        System.out.println("数组中元素的数量：" + s5.count());
        // 演示Stream的常用操作
        System.out.println("Stream的常用操作示例：");
        List<String> nameList = Arrays.asList(names);
        // filter 和 foreach 遍历
        System.out.println("只输出长度为2的名字：");
        nameList.stream()
            .filter(name -> name.length() == 2)
            .forEach(System.out::println);
        // filter 和 collect  过滤和收集转为List集合
        List<String> filteredNames = nameList.stream()
            .filter(name -> name.length() == 3)
            .collect(Collectors.toList());
        System.out.println("筛选长度为3的名字：" + filteredNames);
        // map 把元素映射为另一个元素
        List<Integer> nameLengths = nameList.stream()
            .map(String::length)
            .collect(Collectors.toList());
        System.out.println("名字的长度列表：" + nameLengths);
        // sorted
        List<String> sortedNames = nameList.stream()
            .sorted()
            .collect(Collectors.toList());
        System.out.println("排序后的名字列表：" + sortedNames);
        // distinct 去重
        List<String> distinctNames = Arrays.asList("张三", "李四", "张三", "王五")
            .stream()
            .distinct()
            .collect(Collectors.toList());
        System.out.println("去重后的名字列表：" + distinctNames);
        // limit 限制流的长度 获取前n个元素
        List<String> limitedNames = nameList.stream()
            .limit(2)
            .collect(Collectors.toList());
        System.out.println("限制流的长度后的名字列表：" + limitedNames);
        // skip 跳过流的长度 跳过前n个元素
        List<String> skippedNames = nameList.stream()
            .skip(2)
            .collect(Collectors.toList());
        System.out.println("跳过流的长度后的名字列表：" + skippedNames);
        // concat 合并流 把两个流合并成一个流
        List<String> concatNames = Stream.concat(nameList.stream(), nameList.stream())
            .collect(Collectors.toList());
        System.out.println("合并流后的名字列表：" + concatNames);

        // stream 的终止方法：统计收集操作，流就结束了
        

        // 把集合中所有以“张”开头，且是3个字的元素存储到一个新的集合。
        System.out.println("把集合中所有以“张”开头，且是3个字的元素存储到一个新的集合。");
        List<String> list = Arrays.asList("张三", "张四", "张额额", "张三丰", "张无忌");
        // 使用Stream方法
        List<String> newList1 = list.stream()
            .filter(s -> s.startsWith("张"))
            .filter(s -> s.length() == 3)
            .collect(Collectors.toList()); // 把Stream流转换为List集合
        System.out.println("使用Stream方法的结果：" + newList1);
        // 使用传统循环方法
        List<String> newList2 = new ArrayList<>();
        for (String name : list) {
            if (name.startsWith("张") && name.length() == 3) {
                newList2.add(name);
            }
        }
        System.out.println("使用传统循环方法的结果：" + newList2);


        // 演示Stream提供的常用终结方法 也就是调用完不会再返回Stream流的方法
        System.out.println("\nStream提供的常用终结方法：");
        List<String> nameList2 = Arrays.asList("张三丰", "张无忌", "张翠山", "张良", "张学友");

        // 1. forEach: 对流中的每个元素执行操作
        System.out.println("1. forEach示例：");
        nameList2.stream().forEach(name -> System.out.println("Hello, " + name));

        // 2. count: 返回流中元素的数量
        long count = nameList2.stream().count();
        System.out.println("\n2. count示例：");
        System.out.println("流中元素的数量：" + count);

        // 3. max: 返回流中的最大元素
        //max((t1,t2)->t1.compareTo(t2));
        //max((t1,t2)->Double.compare(t1.length(),t2.length()));
        Optional<String> max = nameList2.stream().max(Comparator.naturalOrder()); 
        System.out.println("\n3. max示例：");
        max.ifPresent(name -> System.out.println("字典序最大的名字：" + name));

        // 4. min: 返回流中的最小元素
        Optional<String> min = nameList2.stream().min(Comparator.naturalOrder());
        System.out.println("\n4. min示例：");
        min.ifPresent(name -> System.out.println("字典序最小的名字：" + name));

        // 补充：使用自定义比较器
        Optional<String> longestName = nameList2.stream().max(Comparator.comparingInt(String::length));
        System.out.println("\n使用自定义比较器找最长的名字：");
        longestName.ifPresent(name -> System.out.println("最长的名字：" + name));
    
        // 演示流的收集
        System.out.println("\nStream的收集操作：");
        List<String> nameList1 = Arrays.asList("张三丰", "张无忌", "张翠山", "张良", "张学友");
        // 1. collect方法：使用Collector收集器
        System.out.println("1. collect方法示例：");
        // 收集为List
        List<String> collectedList = nameList1.stream()
                                             .collect(Collectors.toList());
        System.out.println("收集为List：" + collectedList);
        // 收集为Set
        Set<String> collectedSet = nameList1.stream()
                                           .collect(Collectors.toSet());
        System.out.println("收集为Set：" + collectedSet);
        // 收集为Map（使用名字长度作为key）
        Map<Integer, String> collectedMap = nameList1.stream()
                                                    .collect(Collectors.toMap(
                                                        String::length,
                                                        Function.identity(),
                                                        (existing, replacement) -> existing
                                                    ));
        System.out.println("收集为Map：" + collectedMap);
        // 2. toArray方法：将流转换为数组
        System.out.println("\n2. toArray方法示例：");
        Object[] nameArray = nameList1.stream().toArray();
        System.out.println("转换为Object数组：" + Arrays.toString(nameArray));
        // 指定数组类型
        String[] stringArray = nameList1.stream().toArray(String[]::new);
        System.out.println("转换为String数组：" + Arrays.toString(stringArray));

        System.out.println("\nCollectors工具类的其他收集方法：");
        // 3. joining：连接字符串
        String joined = nameList1.stream()
                                .collect(Collectors.joining(", "));
        System.out.println("3. joining：" + joined);
        // 4. counting：计数
        long count1 = nameList1.stream()
                             .collect(Collectors.counting());
        System.out.println("4. counting：" + count1);
        // 5. summarizingInt：汇总统计
        IntSummaryStatistics stats = nameList1.stream()
                                             .collect(Collectors.summarizingInt(String::length));
        System.out.println("5. summarizingInt：" + stats);
        // 6. groupingBy：分组
        Map<Integer, List<String>> grouped = nameList1.stream()
                                                     .collect(Collectors.groupingBy(String::length));
        System.out.println("6. groupingBy：" + grouped);
    
    }
}
