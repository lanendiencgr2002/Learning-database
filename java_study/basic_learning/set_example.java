import java.util.Set;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.TreeSet;
import java.util.Comparator;
/**
 * Java集合选择指南: 一般用1和3就行了
 * 1. 如果希望记住元素的添加顺序，需要存储重复的元素，又要频繁的根据索引查询数据：
 *    - 用ArrayList集合（有序、可重复、有索引），底层基于数组的。（常用）
 * 2. 如果希望记住元素的添加顺序，且增删首尾数据的情况较多？
 *    - 用LinkedList集合（有序、可重复、有索引），底层基于双链表实现的。
 * 3. 如果不在意元素顺序，也没有重复元素需要存储，只希望增删改查都快？
 *    - 用HashSet集合（无序，不重复，无索引），底层基于哈希表实现的。（常用）
 * 4. 如果希望记住元素的添加顺序，也没有重复元素需要存储，且希望增删改查都快？
 *    - 用LinkedHashSet集合（有序，不重复，无索引），底层基于哈希表和双链表。
 * 5. 如果要对元素进行排序，也没有重复元素需要存储？且希望增删改查都快？
 *    - 用TreeSet集合，基于红黑树实现。
 */
@SuppressWarnings("all")
public class set_example {
    public static void main(String[] args) {
        // 无序，不重复，无索引
        // Set<String> set = new HashSet<>();
        // 有序（按照存储顺序），不重复，无索引
        // Set<String> set = new LinkedHashSet<>();
        // 排序（默认升序），不重复，无索引
        Set<String> set = new TreeSet<>();
        set.add("122222");
        set.add("122222");
        set.add("22");
        set.remove("2");  // 注意：这里应该使用双引号，因为我们在处理字符串
        System.out.println(set);
        /*  Hashset 底层是哈希表结构，增删改查都快
            哈希值：就是一个随机的int数字，通过一定的算法，可以算出
            jdk8之前：数组 + 链表   哈希冲突，新元素占老元素位置，老元素挂下面
            jdk8之后：数组 + 链表/红黑树 哈希冲突，新元素会插在老元素后边
            1：创建一个默认长度为16的数组，当存储的元素个数超过16*0.75时，数组会扩容，扩容为原来的2倍，数组名为table
            2：jdk8开始，链表长度>8，数组长度>=64，链表才会转红黑树（自平衡的二叉树）
        */
        System.out.println("### 测试HashSet去重：");
        reflection_student student1 = new reflection_student("张三",17);
        reflection_student student2 = new reflection_student("李四",18);
        reflection_student student3 = new reflection_student("张三",17);
        reflection_student student4 = new reflection_student("李四",18);

        Set<reflection_student> set2 = new HashSet<>();
        set2.add(student1);
        set2.add(student2);
        set2.add(student3);
        set2.add(student4);
        System.out.println(set2); // 如果没有重写hashcode和equals方法，是不会去重的，因为默认的hashcode是根据对象计算，所以不同的对象的hashcode都不一样


        /* LinkedHashSet 底层是哈希表结构 + 链表结构，增删改查都快
         * 哈希表：数组 + 链表/红黑树
         * 链表：记录元素的存储顺序
         * 但是，它的每个元素都额外的多了一个双链表的机制记录它前后元素的位置。
         */
        System.out.println("### 测试LinkedHashSet去重：");
        Set<reflection_student> set3 = new LinkedHashSet<>();
        set3.add(student1);
        set3.add(student2);
        set3.add(student3);
        set3.add(student4);
        System.out.println(set3); // 如果没有重写hashcode和equals方法，是不会去重的，因为默认的hashcode是根据对象计算，所以不同的对象的hashcode都不一样

        /* TreeSet 底层是基于红黑树实现的排序，增删改查都快  可排序，不重复，无索引
         * 对于Integer，String，Date，File，包装类等，TreeSet会自动排序
         * 对于自定义类，需要实现Comparable接口，并重写compareTo方法，才能排序
         */
        System.out.println("### 测试TreeSet去重：");
        // Set<reflection_student> set4 = new TreeSet<>();
        // 优先使用匿名内部类的方式实现Comparator接口，来指定比较规则
        // Set<reflection_student> set4 = new TreeSet<>(new Comparator<reflection_student>() {
        //     @Override
        //     public int compare(reflection_student o1, reflection_student o2) {
        //     //     if(o1.getAge() > o2.getAge()) {
        //     //         return 1;
        //     //     } else if(o1.getAge() < o2.getAge()) {
        //     //         return -1;
        //     //     } else {
        //     //         return 0;
        //     //     }
        //     return Double.compare(o1.getAge(), o2.getAge());
        //     }
        // });
        // 使用lambda表达式实现Comparator接口，来指定比较规则
        Set<reflection_student> set4 = new TreeSet<>((o1, o2) -> Double.compare(o1.getAge(), o2.getAge()));
        set4.add(student1);
        set4.add(student2);
        set4.add(student3);
        set4.add(student4);
        System.out.println(set4); // 如果没有重写hashcode和equals方法，是不会去重的，因为默认的hashcode是根据对象计算，所以不同的对象的hashcode都不一样

    }
}
