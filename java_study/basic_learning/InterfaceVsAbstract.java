/**
 * 1. 概念分析
 * 核心定义：演示接口(Interface)和抽象类(Abstract Class)的区别与使用场景
 * 解决问题：代码复用与多态实现的不同方式
 * 重要性：是Java面向对象设计的基础概念
 */

/**
 * 2. 技术要点：接口定义 - 纯粹的行为契约
 * - 只能包含抽象方法和默认方法
 * - 不能有状态(成员变量)
 * - 支持多实现
 */
interface Payment {
    void pay(double amount);
    
    default void validate(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("支付金额必须大于0");
    }
}

/**
 * 3. 技术要点：抽象类定义 - 通用实现的基类
 * - 可以有构造器
 * - 可以有成员变量
 * - 可以有具体实现方法
 * - 只支持单继承
 */
abstract class PaymentProcessor {
    protected String merchantId;
    
    public PaymentProcessor(String merchantId) {
        this.merchantId = merchantId;
    }
    
    protected void logPayment(double amount) {
        System.out.println("记录支付: " + amount + ", 商户ID: " + merchantId);
    }
    
    abstract protected boolean processPayment(double amount);
}

/**
 * 4. 实践应用：具体实现类
 * - 展示如何同时使用接口和抽象类
 * - 体现代码复用
 * - 展示多态特性
 */
class AlipayPayment extends PaymentProcessor implements Payment {
    public AlipayPayment(String merchantId) {
        super(merchantId);
    }
    
    @Override
    public void pay(double amount) {
        validate(amount);  // 使用接口的默认方法
        if (processPayment(amount)) {
            logPayment(amount);
        }
    }
    
    @Override
    protected boolean processPayment(double amount) {
        System.out.println("通过支付宝支付: " + amount);
        return true;
    }
}

class WechatPayment extends PaymentProcessor implements Payment {
    public WechatPayment(String merchantId) {
        super(merchantId);
    }
    
    @Override
    public void pay(double amount) {
        validate(amount);
        if (processPayment(amount)) {
            logPayment(amount);
        }
    }
    
    @Override
    protected boolean processPayment(double amount) {
        System.out.println("通过微信支付: " + amount);
        return true;
    }
}

/**
 * 5. 案例演示：客户端使用
 * - 使用接口类型引用具体实现
 * - 展示多态调用
 */
public class InterfaceVsAbstract {
    public static void main(String[] args) {
        // 通过接口类型引用,体现多态
        Payment alipay = new AlipayPayment("MERCHANT_001");
        Payment wechat = new WechatPayment("MERCHANT_002");
        
        // 演示支付流程
        alipay.pay(100.0);  // 输出: 通过支付宝支付: 100.0
        wechat.pay(200.0);  // 输出: 通过微信支付: 200.0
    }
} 