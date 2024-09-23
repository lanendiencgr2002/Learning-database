public class systemapi {
    public static void main(String[] args) {
        // 输出当前时间的毫秒数 返回的是当前时间减去1970-1-18：00：00中间差所换算成的毫秒值。
        System.out.println("当前时间(毫秒)：" + System.currentTimeMillis());
        
        // 输出当前时间的纳秒数
        System.out.println("当前时间(纳秒)：" + System.nanoTime());
        
        // 演示System.arraycopy()方法的使用
        // arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
        // arraycopy(源数组, 起始索引, 目标数组, 起始索引, 复制个数)
        int[] arr = {10, 20, 30, 40, 50, 60, 70};
        int[] brr = new int[10];
        // 将arr数组从索引2开始的4个元素复制到brr数组的索引3开始的位置
        System.arraycopy(arr, 2, brr, 3, 4);
        
        System.out.println("复制后的brr数组:");
        for (int i = 0; i < brr.length; i++) {
            System.out.print(brr[i] + " ");
        }
        System.out.println(); // 换行
        
        // 注释掉System.exit()调用,因为它会立即终止程序
        // System.exit(0); // 0表示正常退出,非0值表示异常退出
    }
}






