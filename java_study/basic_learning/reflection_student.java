import java.util.Objects;

public class reflection_student implements Comparable<reflection_student> {
    private String name;
    private int age;

    public void eat(String food) {
        System.out.println(name + " is eating " + food);
    }

    public reflection_student() {
    }


    public reflection_student(String name) {
        this.name = name;
    }

    public reflection_student(int age) {
        this.age = age;
    }

    public reflection_student(String name, int age) {

        this.name = name;

        this.age = age;
    }
    
    // 添加 getter 和 setter 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // 添加 toString 方法
    @Override
    public String toString() {
        return "reflection_student{" +
               "name='" + name + '\'' +
               ", age=" + age +
               '}';
    }

    // 添加 equals 和 hashCode 方法
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        reflection_student that = (reflection_student) o;
        return age == that.age && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age); // 会被自动装箱为对象
    }


    
    @Override
    public int compareTo(reflection_student o) {
        // t2.compareTo(t1)
        // t2 == this 比较者
        // t1 == o 被比较者
        // 规定1: 如果你认为左边大于右边，请返回正整数
        // 规定2: 如果你认为左边小于右边，请返回负整数
        // 规定3: 如果你认为左边等于右边，请返回0
        // 默认就会升序。
        return this.age - o.age;
    }
}
