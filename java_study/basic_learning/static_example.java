public class static_example {
    // 静态变量
    public static String schoolName = "学院";
    
    // 非静态变量
    private String studentName;
    
    // 私有构造方法
    private static_example(String name) {
        this.studentName = name;
    }
    
    // 静态方法
    public static void printSchoolName() {
        System.out.println("学校名称：" + schoolName);
        // 下面这行会导致编译错误，因为静态方法不能直接访问非静态成员
        // System.out.println(studentName);
    }
    
    // 非静态方法
    public void printStudentInfo() {
        System.out.println("学生姓名：" + this.studentName);
        System.out.println("学校名称：" + schoolName); // 静态成员可以在非静态方法中访问
    }
    
    // 静态工具方法
    public static int getMax(int[] arr) {
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }
    
    // main方法
    public static void main(String[] args) {
        // 通过类名调用静态方法
        static_example.printSchoolName();
        
        // 使用静态工具方法
        int[] numbers = {5, 3, 8, 1, 9, 2};
        System.out.println("最大值：" + static_example.getMax(numbers));
        
        // 创建实例（通常情况下，如果构造方法是私有的，我们会提供一个静态工厂方法来创建实例）
        // static_example student = new static_example("张三"); // 这行会导致编译错误
        
        // 修改静态变量
        static_example.schoolName = "学校";
        static_example.printSchoolName();
    }
}
