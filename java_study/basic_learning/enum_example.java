public class enum_example {
    
    // 定义枚举
    public enum DayOfWeek {
        MONDAY("星期一"),
        TUESDAY("星期二"),
        WEDNESDAY("星期三"),
        THURSDAY("星期四"),
        FRIDAY("星期五"),
        SATURDAY("星期六"),
        SUNDAY("星期日");

        private final String chineseName;

        DayOfWeek(String chineseName) {
            this.chineseName = chineseName;
        }

        public String getChineseName() {
            return chineseName;
        }
    }

    // 使用枚举的方法
    public static void printDayInfo(DayOfWeek day) {
        System.out.println("英文名: " + day.name());
        System.out.println("中文名: " + day.getChineseName());
        System.out.println("序号: " + day.ordinal());
    }

    // 演示枚举的各种用法
    public static void main(String[] args) {
        // 1. 直接使用枚举常量
        System.out.println("\n演示直接使用枚举常量: " );
        DayOfWeek today = DayOfWeek.MONDAY;
        System.out.println("今天是: " + today);

        // 2. 使用 values() 方法遍历所有枚举常量
        System.out.println("\n演示使用 values() 方法遍历所有枚举常量: ");
        System.out.println("所有的工作日:");
        for (DayOfWeek day : DayOfWeek.values()) { //values() 方法返回一个包含所有枚举常量的数组
            if (day.ordinal() < 5) {  // 只打印工作日 //ordinal() 方法返回枚举常量的序号
                System.out.println(day.getChineseName());
            }
        }

        // 3. 使用 valueOf() 方法
        System.out.println("\n演示使用 valueOf() 方法: ");
        try {
            DayOfWeek day = DayOfWeek.valueOf("FRIDAY"); //valueOf() 方法将字符串转换为枚举常量
            System.out.println("" + day.getChineseName() + "是一周的第 " + (day.ordinal() + 1) + " 天");
        } catch (IllegalArgumentException e) {
            System.out.println("无效的日期名称");
        }

        // 4. 在 switch 语句中使用枚举
        System.out.println("\n在 switch 语句中使用枚举: ");
        DayOfWeek weekendDay = DayOfWeek.SATURDAY;
        switch (weekendDay) {
            case SATURDAY:
                System.out.println("周六是休息日");
                break;
            case SUNDAY:
                System.out.println("周日是休息日");
                break;
            default:
                System.out.println("这是工作日");
        }

        // 5. 使用自定义方法
        System.out.println("演示使用自定义方法: ");
        System.out.println("星期四的信息:");
        printDayInfo(DayOfWeek.THURSDAY);
    }
}
