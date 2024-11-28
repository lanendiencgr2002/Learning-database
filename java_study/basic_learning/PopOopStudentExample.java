/**
 * 这个示例通过学生成绩管理系统展示面向过程(POP)和面向对象(OOP)编程的区别：
 * 
 * 1. 面向过程编程(POP - Procedure Oriented Programming)：
 *    - 把解决问题的过程拆分成一个个方法
 *    - 数据和方法是分离的
 *    - 按照解决问题的步骤来编写程序
 *    - 代码结构是线性的，按照处理流程组织
 * 
 * 2. 面向对象编程(OOP - Object Oriented Programming)：
 *    - 先抽象出对象，然后用对象执行方法解决问题
 *    - 数据和方法封装在一起
 *    - 具有封装性、继承性和多态性
 *    - 更容易维护和扩展
 */

// 面向过程示例：通过静态方法处理数据
class PopExample {
    // 面向过程的特点：
    // 1. 方法都是静态的，直接通过类名调用
    // 2. 数据需要从外部传入方法
    // 3. 方法之间相对独立，需要手动传递数据
    
    public static void calculateAndPrintAverage(String studentName, double[] scores) {
        // 计算平均分
        double sum = 0;
        for (double score : scores) {
            sum += score;
        }
        double average = sum / scores.length;
        
        // 打印结果
        System.out.println(studentName + "的平均分是：" + average);
    }
    
    public static void printGrade(String studentName, double average) {
        // 判断等级
        String grade;
        if (average >= 90) {
            grade = "A";
        } else if (average >= 80) {
            grade = "B";
        } else if (average >= 70) {
            grade = "C";
        } else {
            grade = "D";
        }
        System.out.println(studentName + "的等级是：" + grade);
    }
}

// 面向对象示例：将数据（属性）和行为（方法）封装在一起
class OopStudent {
    // 面向对象的特点：
    // 1. 将数据作为对象的属性
    // 2. 使用private实现封装，保护数据安全
    // 3. 方法可以直接访问对象的属性
    
    private String name;
    private double[] scores;
    
    // 构造方法，初始化对象
    public OopStudent(String name, double[] scores) {
        this.name = name;
        this.scores = scores;
    }
    
    // 计算平均分的方法
    public double calculateAverage() {
        double sum = 0;
        for (double score : scores) {
            sum += score;
        }
        return sum / scores.length;
    }
    
    // 获取等级的方法
    public String getGrade() {
        double average = calculateAverage();
        if (average >= 90) return "A";
        if (average >= 80) return "B";
        if (average >= 70) return "C";
        return "D";
    }
    
    // 打印学生信息的方法
    public void printStudentInfo() {
        System.out.println("学生姓名：" + name);
        System.out.println("平均分：" + calculateAverage());
        System.out.println("等级：" + getGrade());
    }
}

public class PopOopStudentExample {
    public static void main(String[] args) {
        // 面向过程的方式：需要手动传递数据，调用独立的方法
        System.out.println("==== 面向过程(POP)的方式 ====");
        String name = "张三";
        double[] scores = {85, 90, 78};
        PopExample.calculateAndPrintAverage(name, scores);
        PopExample.printGrade(name, 84.3);
        
        // 面向对象的方式：创建对象后，数据和行为都在对象内部，调用更简单
        System.out.println("\n==== 面向对象(OOP)的方式 ====");
        OopStudent student = new OopStudent("李四", new double[]{85, 90, 78});
        student.printStudentInfo();
    }
} 