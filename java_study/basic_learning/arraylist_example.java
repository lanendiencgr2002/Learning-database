import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("all")
public class arraylist_example {
    /*
     * ArrayList的特点:
     * 1. 底层是基于数组存储数据的。
     * 2. 查询速度快（注意：是根据索引查询数据快）：
     *    查询数据通过地址值和索引定位，查询任意数据耗时相同。
     * 3. 增删数据效率低：可能需要把后面很多的数据进行前移。
     * 4. 线程不安全。
     */

    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        /* ArrayList底层源码解析:
         * 1. 构造函数:
         *    public ArrayList() {
         *        this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
         *    }
         *    // 初始化时创建一个空数组
         *
         * 2. 添加元素:
         *    public boolean add(E e) {
         *        modCount++;
         *        add(e, elementData, size);
         *        return true;
         *    }
         *
         * 3. 内部添加元素:
         *    private void add(E e, Object[] elementData, int s) {
         *        if (s == elementData.length) // 元素个数和数组个数一样就扩容
         *            elementData = grow(); // 第一次添加数据时扩容
         *        elementData[s] = e;
         *        size = s + 1;
         *    }
         *
         * 4. 扩容方法:
         *    private Object[] grow() {
         *        return grow(size + 1);
         *    }
         *
         *    private Object[] grow(int minCapacity) {
         *        int oldCapacity = elementData.length;
         *        if (oldCapacity > 0 || elementData != DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
         *            // 不是首次扩容，新容量为原容量的1.5倍
         *            int newCapacity = ArraysSupport.newLength(oldCapacity,
         *                    minCapacity - oldCapacity, // minimum growth
         *                    oldCapacity >> 1           // preferred growth
         *            );
         *            return elementData = Arrays.copyOf(elementData, newCapacity);
         *        } else {
         *            // 首次扩容，新容量为默认容量10或minCapacity中的较大值
         *            return elementData = new Object[Math.max(DEFAULT_CAPACITY, minCapacity)];
         *        }
         *    }
         *
         * 注意: 
         * - 一开始是空数组，加入数据后才会扩容到默认长度10
         * - 扩容时，如果原容量大于0或不是默认空数组，新容量会是原容量的1.5倍
         * - 如果是首次扩容，新容量会是DEFAULT_CAPACITY（10）和minCapacity中的较大值
         */
    }
}
