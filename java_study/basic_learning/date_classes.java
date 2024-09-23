import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
public class date_classes {
    public static void main(String[] args) {

        // 构造方法 两个
        System.out.println("两个构造方式演示：");
        Date d=new Date();
        System.out.println("当前时间："+d);
        Date d2=new Date(0L); // 传的是毫秒值
        System.out.println("距离 1970年1月1日00:00:00 的毫秒值："+d2);
        
        // 方法
        // 1. 获取时间戳
        System.out.println("获取时间戳演示：");
        long time=d.getTime();
        System.out.println("date对象.getTime()当前时间戳："+time);
        System.out.println("System.currentTimeMillis()当前时间戳："+System.currentTimeMillis());

        // 2. 设置时间戳
        System.out.println("设置时间戳演示：");
        d.setTime(0L);
        System.out.println("date对象.setTime(0L)当前时间："+d);

        // 格式化时间
        // 1. 创建SimpleDateFormat对象，使用默认格式
        System.out.println("格式化时间演示：");
        SimpleDateFormat sdf1 = new SimpleDateFormat();
        System.out.println("SimpleDateFormat.format(d)当前时间：" + sdf1.format(d));

        // 2. 创建SimpleDateFormat对象，指定自定义格式 
        System.out.println("指定自定义格式演示：");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        System.out.println("SimpleDateFormat.format(d)当前时间：" + sdf2.format(d));
        
        // 解析时间
        System.out.println("解析时间演示：");
        String dateStr = "2024年07月05日 16:30:00";
        try {
            Date parsedDate = sdf2.parse(dateStr);
            System.out.println("解析后的日期：" + parsedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 两个时间间隔多少天
        System.out.println("两个时间间隔多少天演示：");
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        String dateStr2 = "2021年03月05日 12:30:00";
        String dateStr3 = "2024年07月06日 16:30:00";
        try {
            Date parsedDate2 = sdf3.parse(dateStr2);
            Date parsedDate3 = sdf3.parse(dateStr3);
            long diff = parsedDate3.getTime() - parsedDate2.getTime();
            System.out.println("两个时间间隔多少天：" + diff / (1000 * 60 * 60 * 24)); 
            System.out.println("两个时间间隔多少小时：" + diff / (1000 * 60 * 60));
            System.out.println("两个时间间隔多少分钟：" + diff / (1000 * 60));
            System.out.println("两个时间间隔多少秒：" + diff / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
