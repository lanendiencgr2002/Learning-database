/**
 * 用于反射演示的学生类
 */
public class Student {
    private String name;
    
    public Student() {
        this.name = "默认名字";
    }
    
    private Student(String name) {
        this.name = name;
    }
    
    private String eat(String food) {
        return name + "正在吃" + food;
    }
    
    @Override
    public String toString() {
        return "Student{name='" + name + "'}";
    }
} 