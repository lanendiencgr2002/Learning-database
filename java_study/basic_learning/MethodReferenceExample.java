import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

@SuppressWarnings("all")
/**
 * Java方法引用示例
 * 展示四种主要的方法引用使用方式：
 * 1. 静态方法引用 (类名::静态方法名)
 * 2. 实例方法引用 (对象实例::实例方法名)
 * 3. 特定类型实例方法引用 (类名::实例方法名)
 * 4. 构造方法引用 (类名::new)
 */
public class MethodReferenceExample {
    public static void main(String[] args) {
        // 1. 静态方法引用示例
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        numbers.forEach(System.out::println);  // 替代 num -> System.out.println(num)

        // 2. 实例方法引用示例
        StringBuilder sb = new StringBuilder();
        Consumer<String> appendReference = sb::append;  // 替代 str -> sb.append(str)
        appendReference.accept("Hello ");
        appendReference.accept("World!");
        // 结果: Hello World!

        // 3. 特定类型实例方法引用示例
        List<String> names = Arrays.asList("张三", "李四", "王五");
        names.sort(String::compareTo);  // 替代 (s1, s2) -> s1.compareTo(s2)
        // 结果: [李四, 王五, 张三]

        // 4. 构造方法引用示例
        Supplier<List<String>> listSupplier = ArrayList::new;  // 替代 () -> new ArrayList<>()
        List<String> newList = listSupplier.get();

        // 实际应用示例
        List<Person> people = Arrays.asList(
            new Person("张三", 25),
            new Person("李四", 30),
            new Person("王五", 28)
        );

        // 使用方法引用获取名字列表
        List<String> personNames = people.stream()
            .map(Person::getName)  // 替代 person -> person.getName()
            .collect(Collectors.toList());

        // 使用方法引用排序
        people.sort(Comparator.comparing(Person::getAge));
    }
}

/**
 * 示例用的Person类
 */
class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() { return name; }
    public int getAge() { return age; }

    @Override
    public String toString() {
        return name + "(" + age + "岁)";
    }
} 