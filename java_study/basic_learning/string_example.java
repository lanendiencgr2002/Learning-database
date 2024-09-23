public class string_example {
    public static void main(String[] args) {
        // String类特点
        // System.out.println("## String类特点：");
        // System.out.println("1. Java程序中所有双引号字符串都是String类对象");
        // System.out.println("2. 字符串一旦创建不可更改，内容不可改变");
        // System.out.println("3. String字符串虽不可改变，但可以被共享");
        // System.out.println("\n### 字符串常量池：");
        // System.out.println("- 当使用双引号创建字符串对象时，会检查常量池中是否存在");
        // System.out.println("  - 不存在：创建");
        // System.out.println("  - 存在：复用");
        /*  string 自带的 equals 方法  
        public boolean equals(Object anObject) {
            if (this == anObject) { // this是 xx.equals(yy) 中的 xx ， anObject 是 yy
                return true;
            }
            return (anObject instanceof String aString)  // 判断是否是 String 类型，并且将值赋给 aString
                    && (!COMPACT_STRINGS || this.coder == aString.coder) // 判断是否是同一个编码
                    && StringLatin1.equals(value, aString.value); // StringLatin1工具类 
        }
            StringLatin1工具类 中的 equals 方法
        @IntrinsicCandidate
        public static boolean equals(byte[] value, byte[] other) {
            if (value.length == other.length) {
                for (int i = 0; i < value.length; i++) {
                if (value[i] != other[i]) {
                    return false;
                }
            }
            return true;
        }
            return false;
        }
         */
        // 字符串比较
        System.out.println("\n### 字符串比较：");
        String s1 = "abc";
        String s2 = "abc";
        System.out.println("s1 == s2: " + (s1 == s2)); // true
        System.out.println("s1.equals(s2): " + s1.equals(s2)); // true
        
        // 使用new创建
        System.out.println("\n### 使用new创建：");
        String s3 = new String("abc");
        System.out.println("s1 == s3: " + (s1 == s3)); // false
        // string自带的equals方法比较的是内容 而不是地址
        System.out.println("s1.equals(s3): " + s1.equals(s3)); // true
        
        // 字符串拼接 对象+"字符串" 字符串拼接会创建一个新的 String 对象。编译器通常会将这种拼接优化为使用 StringBuilder。
        System.out.println("\n### 字符串拼接：");
        String s4 = "ab";
        String s5 = s4 + "c"; //s1是"abc" 在常量池stringtable中 s5是"abc" 是stringbuilder对象在堆中创建
        //s5= new StringBuilder().append(s4).append("c").toString() 最终在堆内存中  常量池也在堆内存
        System.out.println("s1 == s5: " + (s1 == s5)); // false
        System.out.println("s1.equals(s5): " + s1.equals(s5)); // true


        
        // 常量拼接 这种情况下，"aa" + "bb" + "cc" 会在编译时被直接优化为 "aabbcc"  
        System.out.println("\n### 常量拼接：");
        String s6 = "a" + "b" + "c";
        System.out.println("s1 == s6: " + (s1 == s6)); // true
        System.out.println("s1.equals(s6): " + s1.equals(s6)); // true
        System.out.println("\n注意：比较字符串内容应使用equals()方法，而不是==");
    
        // String中的hashcode  是根据字符串内容计算出来的
        System.out.println("\n### String中的hashcode：");
        String s7 = "abc";
        System.out.println("s7.hashCode(): " + s7.hashCode()); // 96354
    }
}
