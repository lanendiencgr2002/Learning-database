public class wrapper_class {
    // 增强了普通数据类型的功能
    /*
     *  基本数据类型 包装类
     *  byte    Byte
     *  short   Short
     *  int     Integer
     *  long    Long
     *  float   Float
     *  double  Double
     *  char    Character
     *  boolean Boolean
     */
    @SuppressWarnings("all") // 抑制未使用变量警告
    public static void main(String[] args) {
        // Integer
        /* valueOf源码  int值为-127-128时，返回缓存中的对象，否则返回new Integer(i)
         * @HotSpotIntrinsicCandidate
         * public static Integer valueOf(int i) {  
         *     if (i >= IntegerCache.low && i <= IntegerCache.high)
         *         return IntegerCache.cache[i + (-IntegerCache.low)];
         *     return new Integer(i);
         * }
         * 
         */
        Integer in = new Integer(10);
        Integer in1 = new Integer("10");  // 必须是数字形式的字符串
        Integer integer = Integer.valueOf(20);
        Integer integer1 = Integer.valueOf("20");
        // 比较：这时了解推荐使用valueOf方法吗？为什么不推荐使用new？为什么又推荐使用valueOf方法呢？
        Integer in2 = new Integer(127);
        Integer in3 = new Integer(127);
        System.out.println("new 比较：" + (in2 == in3)); // false

        Integer in4 = Integer.valueOf(127);
        Integer in5 = Integer.valueOf(127);
        System.out.println("valueOf 比较：" + (in4 == in5)); // true

        System.out.println("--------------------");
        Integer in6 = new Integer(128);
        Integer in7 = new Integer(128);
        System.out.println("new 比较：" + (in6 == in7)); // false

        Integer in8 = Integer.valueOf(128);
        Integer in9 = Integer.valueOf(128);

        System.out.println("valueOf 比较：" + (in8 == in9)); // false
        //Interger自动装箱 自动调用valueOf方法
        Integer in10 = 10;
        // int in11 = in10.intValue();// 拆箱
        //Interger自动拆箱 自动调用intValue方法
        int in12 = in10;
        System.out.println("自动拆箱in12:" + in12);
        //Integer integer13 = null;
        // int in14 = integer13; // 报错 NullPointerException
        // int最大值 最小值
        // System.out.println("int最大值：" + Integer.MAX_VALUE);
        // System.out.println("int最小值：" + Integer.MIN_VALUE);
        // 进制转换
        // System.out.println("十进制转二进制：" + Integer.toBinaryString(10));
        // System.out.println("十进制转八进制：" + Integer.toOctalString(10));
        // System.out.println("十进制转十六进制：" + Integer.toHexString(10));
        String str = Integer.toBinaryString(17);
        System.out.println("17的二进制：" + str);
        // int 转string
        String str1 = 10+""; //底层用的是String.valueOf(10)
        String str2 = String.valueOf(10);
        String str3 = Integer.toString(10);
        // System.out.println("str1:" + str1);
        // System.out.println("str2:" + str2);
        // System.out.println("str3:" + str3);
        // string 转int
        String str4 = "10";
        Integer integer15 = Integer.valueOf(str4);
        int i=integer15;
        int i1=Integer.parseInt(str4);
        System.out.println("i:" + i);
        System.out.println("i1:" + i1);
    
    
    }
}

