import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Date;

/**
 * Object类核心概念演示
 * - 是什么：Java中所有类的父类，提供基础的对象行为
 * - 为什么：统一对象的基本行为，便于多态和通用算法实现
 * - 应用于：对象比较、字符串表示、哈希计算等基础场景
 */
@SuppressWarnings("all")
public class ObjectDemo {
    // 1. 标准实现部分
    
    /**
     * ✅ 最佳实践：产品实体类
     * - 完整实现equals和hashCode
     * - 提供有意义的toString
     * - 使用lombok简化代码（实际项目中）
     */
    static class Product {
        private String name;
        private double price;
        private String category;

        public Product(String name, double price, String category) {
            this.name = Objects.requireNonNull(name, "产品名称不能为空");
            this.price = price;
            this.category = Objects.requireNonNull(category, "产品类别不能为空");
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Product product = (Product) obj;
            return Double.compare(product.price, price) == 0 
                && Objects.equals(name, product.name)
                && Objects.equals(category, product.category);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, price, category);
        }

        @Override
        public String toString() {
            return String.format("Product{name='%s', price=%.2f, category='%s'}", 
                name, price, category);
        }
    }

    /**
     * ❌ 常见错误示例：错误的equals实现
     * 违反了equals的对称性原则
     */
    static class BadEquals {
        private String value;
        
        public BadEquals(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            // 错误实现：没有检查对象类型
            return obj instanceof String && value.equals(obj);
        }
    }

    public static void main(String[] args) {
        // 2. 实战示例
        
        // 2.1 对象比较示例
        Product p1 = new Product("iPhone", 999.99, "Electronics");
        Product p2 = new Product("iPhone", 999.99, "Electronics");
        Product p3 = new Product("iPad", 799.99, "Electronics");

        System.out.println("产品比较示例：");
        System.out.println("p1.equals(p2): " + p1.equals(p2));  // true
        System.out.println("p1.equals(p3): " + p1.equals(p3));  // false
        
        // 2.2 HashSet中使用示例
        Set<Product> products = new HashSet<>();
        products.add(p1);
        products.add(p2);  // 不会被添加，因为equals返回true
        System.out.println("HashSet大小: " + products.size());  // 1
        
        // 2.3 toString使用示例
        System.out.println("\n对象字符串表示：");
        System.out.println(p1);  // 输出格式化的产品信息
        
        // 3. 性能优化建议演示
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            productList.add(new Product("Product" + i, i * 100, "Category" + (i % 5)));
        }
        
        // ✅ 推荐：使用StringBuilder进行字符串拼接
        StringBuilder sb = new StringBuilder();
        for (Product p : productList) {
            sb.append(p.toString()).append("\n");
        }
        
        // ❌ 不推荐：在循环中直接使用字符串连接
        String result = "";
        for (Product p : productList) {
            result += p.toString() + "\n";  // 性能差
        }
        
        // requireNonNull 详细示例
        demonstrateRequireNonNull();
    }

    // requireNonNull 详细示例 都会抛出异常 第2个参数是定义抛出异常的信息
    public static void demonstrateRequireNonNull() {
        System.out.println("\n=== requireNonNull 详细示例 ===");
        
        // 1. 模拟用户输入场景
        String userInput = null;
        String username = "张三";
        Integer age = null;
        
        // 2. 基础检查示例  
        try {
            String result = Objects.requireNonNull(userInput);
        } catch (NullPointerException e) {
            System.out.println("基础检查：" + e.getMessage());  // 输出: null
        }
        
        // 3. 带静态消息的检查 
        try {
            String result = Objects.requireNonNull(userInput, "用户输入不能为空");
        } catch (NullPointerException e) {
            System.out.println("静态消息检查：" + e.getMessage());  // 输出: 用户输入不能为空
        }
        
        // 4. 带动态消息的检查（使用 Supplier）  
        try {
            String result = Objects.requireNonNull(
                userInput,
                () -> String.format(
                    "错误详情：\n- 字段：userInput\n- 当前用户：%s\n- 时间戳：%d\n- 线程：%s",
                    username,
                    System.currentTimeMillis(),
                    Thread.currentThread().getName()
                )
            );
        } catch (NullPointerException e) {
            System.out.println("动态消息检查：" + e.getMessage());
        }
        
        // 5. 复杂对象检查示例  
        try {
            Integer validatedAge = Objects.requireNonNull(
                age,
                () -> {
                    StringBuilder error = new StringBuilder();
                    error.append("年龄验证失败\n");
                    error.append("┌─────────────────────────\n");
                    error.append("│ 用户信息:\n");
                    error.append(String.format("│ - 用户名: %s\n", username));
                    error.append(String.format("│ - 检查时间: %tF %<tT\n", new Date()));
                    error.append(String.format("│ - 错误字段: age\n"));
                    error.append("└─────────────────────────");
                    return error.toString();
                }
            );
        } catch (NullPointerException e) {
            System.out.println("复杂检查：\n" + e.getMessage());
        }
        
        // 6. 实际业务场景示例  
        try {
            validateUserData(null, null);
        } catch (NullPointerException e) {
            System.out.println("业务验证：" + e.getMessage());
        }
    }

    // 辅助方法：模拟实际业务场景的数据验证
    private static void validateUserData(String username, String email) {
        // 用户名验证
        Objects.requireNonNull(
            username,
            () -> String.format("用户注册失败：用户名不能为空 [请求时间：%tF %<tT]", new Date())
        );
        
        // 邮箱验证
        Objects.requireNonNull(
            email,
            () -> {
                String template = """
                    邮箱验证失败
                    - 用户名：%s
                    - 时间：%s
                    - 操作：用户注册
                    - 提示：邮箱地址为必填项
                    """;
                return String.format(template, username, new Date());
            }
        );
    }
}
