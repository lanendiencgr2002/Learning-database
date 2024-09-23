import java.util.LinkedList;
import java.util.List;
@SuppressWarnings("all")
public class linkedlist_example {
    /*
     * LinkedList的特点:
     * 1. 底层是基于链表存储数据的。
     * 2. 查询数据效率低：
     *    查询数据需要遍历链表，查询任意数据耗时不同。
     * 3. 增删数据效率高：
     *    增删数据只需要修改链表的指针，效率较高。
     * 4. 对首尾元素进行增删改查的速度是极快的。
     * 5. 线程不安全。
     * 6. LinkedList新增了：很多首尾操作的特有方法
     */
    public static void main(String[] args) {
        // 目标：用LinkedList做一个队列对象。
        System.out.println("演示队列：");
        LinkedList<String> queue = new LinkedList<>();
        
        // 入队
        queue.addLast("赵敏");
        queue.addLast("西门吹雪");
        queue.addLast("陆小凤");
        queue.addLast("石观音");
        System.out.println(queue); // [赵敏, 西门吹雪, 陆小凤, 石观音]
        
        // 出队
        System.out.println(queue.removeFirst());
        System.out.println(queue.removeFirst());
        System.out.println(queue);

        // 做一个栈
        System.out.println("演示栈：");
        LinkedList<String> stack = new LinkedList<>();
        // 压栈 或者用push
        stack.addFirst("第1颗子弹"); 
        stack.addFirst("第2颗子弹");
        stack.addFirst("第3颗子弹");
        stack.addFirst("第4颗子弹");
        System.out.println(stack); // [第4颗子弹, 第3颗子弹, 第2颗子弹, 第1颗子弹]

        // 出栈
        System.out.println(stack.removeFirst());
        System.out.println(stack.removeFirst());
        System.out.println(stack);
    }
}
