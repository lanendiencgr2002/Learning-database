/**
 * 演示三种对象拷贝方式的区别:
 * 
 * 1. 引用拷贝: person1 = person2
 *    - 两个引用指向同一个对象
 *    - 修改任意一个引用都会影响另一个
 * 
 * 2. 浅拷贝: super.clone()   当前拷贝的对象是新的，子属性对象是旧的，引用类型属性仍指向原对象
 *    - 创建新对象，复制基本类型属性
 *    - 引用类型属性仍指向原对象
 * 
 * 3. 深拷贝: 递归clone()   当前拷贝的对象是新的，子属性对象也是新的，修改完全独立，互不影响
 *    - 创建全新对象，包括所有嵌套对象
 *    - 修改完全独立，互不影响
 */
public class CopyDemo {
    public static void main(String[] args) {
        // 1. 引用拷贝示例
        DemoPerson_R84721 person1 = new DemoPerson_R84721("张三", new DemoAddress_R84721("北京"));
        DemoPerson_R84721 refCopy = person1;  // 引用拷贝
        
        // 2. 浅拷贝示例
        DemoPerson_R84721 shallowCopy = person1.shallowClone();
        
        // 3. 深拷贝示例
        DemoPerson_R84721 deepCopy = person1.deepClone();
        
        // 验证三种拷贝的区别
        System.out.println("引用拷贝: " + (person1 == refCopy));  // true
        System.out.println("浅拷贝: " + (person1 == shallowCopy));  // false
        System.out.println("深拷贝: " + (person1 == deepCopy));  // false
        System.out.println("浅拷贝-地址对象: " + (person1.getAddress() == shallowCopy.getAddress()));  // true
        System.out.println("深拷贝-地址对象: " + (person1.getAddress() == deepCopy.getAddress()));  // false
    }
} 

class DemoPerson_R84721 implements Cloneable {
    private String name;
    private DemoAddress_R84721 address;
    
    public DemoPerson_R84721(String name, DemoAddress_R84721 address) {
        this.name = name;
        this.address = address;
    }
    
    // 浅拷贝实现
    public DemoPerson_R84721 shallowClone() {
        try {
            return (DemoPerson_R84721) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
    
    // 深拷贝实现
    public DemoPerson_R84721 deepClone() {
        try {
            DemoPerson_R84721 cloned = (DemoPerson_R84721) super.clone();
            cloned.address = this.address.clone();  // 递归调用clone()方法，复制Address对象
            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public DemoAddress_R84721 getAddress() {
        return address;
    }
}

class DemoAddress_R84721 implements Cloneable {
    private String city;
    
    public DemoAddress_R84721(String city) {
        this.city = city;
    }
    
    @Override
    public DemoAddress_R84721 clone() {
        try {
            return (DemoAddress_R84721) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public String getCity() {
        return city;
    }
} 