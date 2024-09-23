import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public class bigdecimal {
    public static void main(String[] args) {
        System.out.println(0.1+0.7); //0.7999999999999999 
        System.out.println(0.25+0.25); //0.5 因为0.25在计算机中可以精确表示

        // 使用BigDecimal进行精确计算
        BigDecimal bd1 = new BigDecimal("0.1");
        BigDecimal bd2 = new BigDecimal("0.7");

        // 加法
        BigDecimal add = bd1.add(bd2);
        System.out.println("加法结果：" + add); // 0.8

        // 减法
        BigDecimal subtract = bd1.subtract(bd2);
        System.out.println("减法结果：" + subtract); // -0.6

        // 乘法
        BigDecimal multiply = bd1.multiply(bd2);
        System.out.println("乘法结果：" + multiply); // 0.07

        // 除法 setprecision 设置精度， roundingMode 设置舍入模式
        BigDecimal divide = bd2.divide(bd1, new MathContext(2, RoundingMode.HALF_UP));
        System.out.println("除法结果：" + divide); // 7.0
    }
}
