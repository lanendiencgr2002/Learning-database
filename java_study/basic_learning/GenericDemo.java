/**
 * Java泛型详解示例
 * 演示泛型的各种使用场景：泛型类、泛型接口、泛型方法、泛型边界、泛型通配符
 * 
 * 此处T可以随便写为任意标识，常见的如T、E、K、V等形式的参数常用于表示泛型
 * 在实例化泛型类时，必须指定T的具体类型
 */
public class GenericDemo {
    public static void main(String[] args) {
        // 1. 泛型类使用示例
        System.out.println("=== 泛型类示例 ===");
        Container<String> stringContainer = new Container<>("Hello泛型");
        Container<Integer> intContainer = new Container<>(100);
        
        System.out.println(stringContainer.getData());
        System.out.println(intContainer.getData());

        // 2. 泛型接口使用示例
        System.out.println("\n=== 泛型接口示例 ===");
        DataProcessor<String> stringProcessor = new StringProcessor();
        stringProcessor.processData("处理字符串数据");

        DataProcessor<Integer> numberProcessor = new NumberProcessor();
        numberProcessor.processData(42);

        // 3. 泛型方法使用示例
        System.out.println("\n=== 泛型方法示例 ===");
        Utilities utilities = new Utilities();
        String[] strArray = {"苹果", "香蕉", "橙子"};
        Integer[] intArray = {1, 2, 3};
        
        utilities.printArray(strArray);
        utilities.printArray(intArray);

        // 4. 泛型边界示例
        System.out.println("\n=== 泛型边界示例 ===");
        NumberContainer<Integer> intNumberContainer = new NumberContainer<>(123);
        NumberContainer<Double> doubleNumberContainer = new NumberContainer<>(123.456);
        System.out.println("整数计算：" + intNumberContainer.square());
        System.out.println("小数计算：" + doubleNumberContainer.square());

        // 5. 泛型通配符示例
        System.out.println("\n=== 泛型通配符示例 ===");
        Container<Integer> intBox = new Container<>(123);
        Container<Double> doubleBox = new Container<>(123.456);
        Container<String> stringBox = new Container<>("test");

        // 使用上界通配符 <? extends Number>
        System.out.println("数字容器的和：" + sumOfNumberContainer(intBox, doubleBox));
        
        // 使用下界通配符 <? super Integer>
        Container<Number> numberBox = new Container<>(Integer.valueOf(100));
        addNumbers(numberBox);
    }

    // 使用上界通配符示例方法
    public static double sumOfNumberContainer(Container<? extends Number> c1, 
                                           Container<? extends Number> c2) {
        return c1.getData().doubleValue() + c2.getData().doubleValue();
    }

    // 使用下界通配符示例方法
    public static void addNumbers(Container<? super Integer> container) {
        container.setData(100);
        System.out.println("添加数字到容器：" + container.getData());
    }
}

// 1. 泛型类示例
class Container<T> {
    private T data;

    public Container(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

// 2. 泛型接口示例
interface DataProcessor<T> {
    void processData(T data);
}

// 实现泛型接口的具体类
class StringProcessor implements DataProcessor<String> {
    @Override
    public void processData(String data) {
        System.out.println("字符串处理器：" + data.toUpperCase());
    }
}

class NumberProcessor implements DataProcessor<Integer> {
    @Override
    public void processData(Integer data) {
        System.out.println("数字处理器：" + (data * 2));
    }
}

// 3. 泛型方法示例
class Utilities {
    // 泛型方法
    public <T> void printArray(T[] array) {
        System.out.print("数组内容：");
        for (T element : array) {
            System.out.print(element + " ");
        }
        System.out.println();
    }

    // 多类型参数的泛型方法
    public <K, V> void printPair(K key, V value) {
        System.out.println("键：" + key + "，值：" + value);
    }
}

// 4. 泛型边界示例 - 限制类型必须是Number或其子类
class NumberContainer<T extends Number> {
    private T number;

    public NumberContainer(T number) {
        this.number = number;
    }

    public double square() {
        return number.doubleValue() * number.doubleValue();
    }
} 