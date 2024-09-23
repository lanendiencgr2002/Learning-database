import java.util.HashMap;
import java.util.Map;

public class map_example {
    public static void main(String[] args) {
        // map双列集合，key-value结构 演示
        Map<String, Integer> map = new HashMap<>();
        map.put("apple", 1);
        map.put("banana", 2);
        map.put("orange", 3);
        System.out.println("map的基本功能：");
        System.out.println(map);
        System.out.println("获取apple的value：" + map.get("apple"));
        System.out.println("map是否包含apple：" + map.containsKey("apple"));
        System.out.println("map是否包含value为1的键值对：" + map.containsValue(1));
        System.out.println("map的大小：" + map.size());
        System.out.println("map是否为空：" + map.isEmpty());
        System.out.println("删除apple键值对：" + map.remove("apple"));
        System.out.println("删除后的map：" + map);

        System.out.println("用keyset遍历map："); // 时间复杂度O(n^2)
        for (String key : map.keySet()) { // keyset时间复杂度O(n)
            System.out.println("key: " + key + ", value: " + map.get(key));
        }

        System.out.println("用entryset遍历map："); // 把每个键值对看作一个entry， 时间复杂度O(n)
        for (Map.Entry<String, Integer> entry : map.entrySet()) { // entryset时间复杂度O(n)
            System.out.println("key: " + entry.getKey() + ", value: " + entry.getValue());
        }

        System.out.println("用lambda表达式遍历map："); // 时间复杂度O(n)
        map.forEach((key, value) -> System.out.println("key: " + key + ", value: " + value));

        // map的子类 hashmap 保证key唯一，value不唯一  hashset单例集合 底层是使用hashmap的键完成的 直接置为null
        // map的子类 treemap 保证key唯一，value不唯一，key有序


      
    }
}
