public class stringbuilder_example {
    /*
    StringBuilder的介绍：
    1.一个可变的字符序列
    2. StringBuilder是字符串缓冲区，将其理解是容器，这个容器可以存储任意数据类型，但是只要进入到这个容器，全部变成字符
    3. 线程不安全，效率高
    4. StringBuffer：线程安全，效率低
    */

    public static void main(String[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append("hello");
        sb.append("world");
        sb.append(123);
        sb.append(true);
        sb.append(new char[]{'a', 'b', 'c'});
        sb.append(new Object());
        sb.append(new int[]{1, 2, 3});
        System.out.println(sb);
    }
        
}
