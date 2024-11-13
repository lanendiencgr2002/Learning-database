import java.util.*;
import java.util.stream.*;

public class stream_somuchkind {
    public static void main(String[] args) {
        List<String> names = Arrays.asList("张三丰", "张无忌", "张翠山", "张良", "张学友");
        System.out.println("Stream操作示例：");

        // 过滤和遍历
        System.out.println("过滤和遍历示例：");
        names.stream()
            .filter(name -> name.length() == 3)
            .forEach(System.out::println);

        // 映射和收集
        System.out.println("映射和收集示例：");
        List<Integer> nameLengths = names.stream()
            .map(String::length)
            .collect(Collectors.toList());
        System.out.println("名字长度列表：" + nameLengths);

        // 排序
        System.out.println("排序示例：");
        List<String> sortedNames = names.stream()
            .sorted()
            .collect(Collectors.toList());
        System.out.println("排序后的名字：" + sortedNames);

        // 去重
        System.out.println("去重示例：");
        List<String> distinctNames = Stream.of("张三", "李四", "张三", "王五")
            .distinct()
            .collect(Collectors.toList());
        System.out.println("去重后的名字：" + distinctNames);

        // 统计
        System.out.println("统计示例：");
        long count = names.stream().count();
        System.out.println("名字总数：" + count);

        // 查找
        System.out.println("查找示例：");
        Optional<String> firstThreeCharName = names.stream()
            .filter(name -> name.length() == 3)
            .findFirst();
        System.out.println("第一个长度为3的名字：" + firstThreeCharName.orElse("无"));

        // 收集为Map
        System.out.println("收集为Map示例：");
        Map<Integer, String> nameMap = names.stream()
            .collect(Collectors.toMap(
                String::length,
                name -> name,
                (existing, replacement) -> existing
            ));
        System.out.println("名字长度Map：" + nameMap);
    }
}
