public class exception_example {
    /* 异常体系：
     *  只让编译通过，用throws 抛出异常
     *  不止让编译通过，还要防止后续报错，用throw 抛出异常
     * 异常的设计理念：
     *  运行期异常：其实并不是程序员出的错，而是用户出的错
     * Throwable类的子类：
     *  Error：严重问题，遇到一个Error，JVM会直接停止执行
     *      Error的子类：
     *          StackOverflowError：栈溢出错误
     *          OutOfMemoryError：内存溢出错误
     *  Exception：异常，不太严重的错误，可能会有很多很多
     *      RuntimeException：运行时异常，可以处理，也可以不处理
     *      RuntimeException的子类：
     *          NullPointerException：空指针异常
     *          ArrayIndexOutOfBoundsException：数组索引越界异常
     *          ClassCastException：类型转换异常
     *          NumberFormatException：数字格式异常
     *          InputMismatchException：输入不匹配异常
     *          ArithmeticException：算术异常
     *          IllegalArgumentException：非法参数异常
     *          FileNotFoundException：文件未找到异常
     *          SQLException：SQL异常
     */

    public static void main(String[] args) {
        int[] arr = new int[3];

        try { // 这样就能捕获到异常 并且处理异常往后执行代码
            System.out.println(arr[100]);
            System.out.println("aaa");
        } catch (ArrayIndexOutOfBoundsException e) { // Exception e = new ArrayIndexOutOfBoundsException(); 也行，是他的父类
            System.out.println("哎呀，索引越界啦！");
            e.printStackTrace(); // 打印异常的堆栈信息
        }

        System.out.println("helloworld");
    }

    /* throw 认出 异常对象 一个
     * throws 声明异常的类型 可以跟多个
     */
    

}
