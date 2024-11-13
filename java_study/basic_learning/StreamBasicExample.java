import java.util.*;
import java.util.stream.*;

/**
 * Java Stream基础操作示例
 * 展示最常用的Stream操作方法
 */
public class StreamBasicExample {
    
    public static void main(String[] args) {
        // 1. 基础过滤操作
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 演示filter：过滤偶数
        List<Integer> evenNumbers = numbers.stream()
                .filter(num -> {
                    System.out.println("过滤元素: " + num); // 用于演示懒加载特性
                    return num % 2 == 0;
                })
                .collect(Collectors.toList());
        System.out.println("偶数列表: " + evenNumbers);
        
        // 2. 转换操作
        List<String> numberStrings = numbers.stream()
                .map(num -> "数字" + num)
                .collect(Collectors.toList());
        System.out.println("转换后的字符串: " + numberStrings);
        
        // 3. 排序操作
        List<Integer> sortedNumbers = numbers.stream()
                .sorted(Comparator.reverseOrder())  // 倒序排序
                .collect(Collectors.toList());
        System.out.println("排序后的数字: " + sortedNumbers);
        
        // 4. 聚合操作
        int sum = numbers.stream()
                .reduce(0, Integer::sum);
        System.out.println("总和: " + sum);
        
        // 5. 统计操作
        IntSummaryStatistics stats = numbers.stream()
                .mapToInt(Integer::intValue)
                .summaryStatistics();
        System.out.println("统计信息:");
        System.out.println("  最大值: " + stats.getMax());
        System.out.println("  最小值: " + stats.getMin());
        System.out.println("  平均值: " + stats.getAverage());
        System.out.println("  总和: " + stats.getSum());
        System.out.println("  数量: " + stats.getCount());
        
        // 6. 查找操作
        Optional<Integer> firstEven = numbers.stream()
                .filter(n -> n % 2 == 0)
                .findFirst();
        System.out.println("第一个偶数: " + firstEven.orElse(-1));
        
        // 7. 匹配操作
        boolean allGreaterThanZero = numbers.stream()
                .allMatch(n -> n > 0);
        System.out.println("是否所有数字都大于0: " + allGreaterThanZero);
        
        // 8. 去重操作
        List<Integer> duplicateNumbers = Arrays.asList(1, 1, 2, 2, 3, 3);
        List<Integer> distinctNumbers = duplicateNumbers.stream()
                .distinct()
                .collect(Collectors.toList());
        System.out.println("去重后的数字: " + distinctNumbers);
    }
} 