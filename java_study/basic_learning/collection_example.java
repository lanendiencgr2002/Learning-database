import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.Iterator;
@SuppressWarnings("all")
public class collection_example {
    public static void main(String[] args) {
        /* 集合体系：
         *     Collection -- 单列集合顶层接口
         *      往下分成2个接口
         *         Set -- 接口  无序，存储元素自动去重，无索引
         *             HashSet 类  无序，不重复，无索引
         *             TreeSet 类  排序，不重复，无索引
         *             LinkedHashSet 类  有序，不重复，有索引
         *         List -- 接口  有序，不去重，有索引 
         *             ArrayList 类  有序，不重复，有索引
         *             LinkedList 类  有序，不重复，有索引
         *     Map -- 双列集合顶层接口  
         *      往下分成2个接口
         *         HashMap 类  无序，不重复，无索引
         *         TreeMap 类  排序，不重复，无索引
         */
        
         // Collection 的共性功能  是接口 都是抽象方法
        /* boolean add(E e)               添加元素       为什么是布尔类型？因为去重涉及，添加成功返回true，添加失败返回false
         * boolean remove(Object o)       从集合中移除指定的元素 底层是n遍历每个元素
         * boolean removeIf(Predicate o)  根据条件进行删除
         * void clear()                   清空集合
         * boolean contains(Object o)     判断集合中是否存在指定的元素
         * boolean isEmpty()              判断集合是否为空
         * int size()                     集合的长度，也就是集合中元素的个数 */
        // Collection<String> collection = new ArrayList<>(); 是接口不能直接创建对象
        System.out.println("collection演示：");
        Collection<String> collection = new ArrayList<>(); //父类没有的调不了
        collection.add("122222");
        collection.add("22");
        collection.remove("2");  // 注意：这里应该使用双引号，因为我们在处理字符串
        /* removeif 底层实现  p.test(this.get(i))转为一个要求
        class ArrayList {
            public void removeIf(Predicate p) {
                for (int i = 0; i < this.size(); i++) {
                    if (p.test(this.get(i))) {
                        this.remove(i);
                        i--;
                    }
                }
            }
        } */
        // collection.removeIf(new Predicate<String>() {
        //     @Override
        //     public boolean test(String s) {
        //         return s.equals("1");
        //     }
        // });
        collection.removeIf(s -> s.length() <4); //lambda表达式 时间复杂度O(n)
        System.out.println(collection);
        
        System.out.println("ArrayList演示：");
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("1");
        for(int i = 0; i < arrayList.size(); i++){
            System.out.println(arrayList.get(i)); //collection中没有
        }

        // 迭代器遍历 所有集合都可以使用
        System.out.println("迭代器遍历：");
        Iterator<String> iterator = collection.iterator();
        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
        // 迭代器删除元素只用遍历一遍 时间复杂度O(n) 不然底层remove也是遍历一遍 合起来就是O(n^2)
        System.out.println("迭代器删除元素后：");
        Iterator<String> iterator1 = collection.iterator();
        while(iterator1.hasNext()){
            String s = iterator1.next();
            if(s.equals("1")){
                iterator1.remove();
            }
        }
        System.out.println(collection);


        // 增强for循环遍历 实现了Iterable接口的类可以被增强for遍历 implements Iterable<String> 底层是迭代器
        System.out.println("增强for循环遍历：");
        for(String s : collection){
            System.out.println(s);
        }

        System.out.println("自定义类增强for循环遍历：");
        MyArrayList myArrayList = new MyArrayList();
        myArrayList.add("苹果");
        myArrayList.add("香蕉");
        myArrayList.add("橙子");
        for(String s : myArrayList){
            System.out.println(s);
        }

    }
    // 自定义一个类 可以实现增强for循环 只需要实现Iterable接口 重写iterator方法 返回一个迭代器
    public static class MyArrayList implements Iterable<String> {
        private String[] elements;
        private int size;

        public MyArrayList() {
            elements = new String[10];
            size = 0;
        }

        public void add(String element) {
            if (size == elements.length) {
                String[] newElements = new String[elements.length * 2];
                System.arraycopy(elements, 0, newElements, 0, elements.length);
                elements = newElements;
            }
            elements[size++] = element;
        }

        @Override
        public Iterator<String> iterator() {
            return new Iterator<String>() {
                private int currentIndex = 0;

                @Override
                public boolean hasNext() {
                    return currentIndex < size;
                }

                @Override
                public String next() {
                    return elements[currentIndex++];
                }
            };
        }
    }
}
