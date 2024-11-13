package com.nageoffer.shortlink.admin.toolkit;

import java.security.SecureRandom;

/**
 * 分组ID随机生成器
 * 
 * 该工具类用于生成安全的随机分组ID，主要特点：
 * 1. 使用SecureRandom确保随机性的安全性
 * 2. 支持可变长度的ID生成
 * 3. 生成的ID由数字和大小写字母组成
 * 4. 线程安全，可在多线程环境下使用
 */
public final class RandomGenerator {

    /**
     * 定义字符集包含数字、大写字母和小写字母
     * 总长度为62个字符(10个数字 + 26个大写字母 + 26个小写字母)
     * 使用静态常量提高性能并确保不可变性
     */
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    
    /**
     * 使用SecureRandom而不是Random
     * 原因：SecureRandom提供加密级别的随机数，更适合生成不可预测的标识符
     * 静态实例可以重复使用，避免频繁创建新实例带来的性能开销
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 生成默认长度(6位)的随机分组ID
     * 这是一个便捷方法，适用于大多数场景
     *
     * @return 6位长度的随机字符串
     */
    public static String generateRandom() {
        return generateRandom(6);
    }

    /**
     * 生成指定长度的随机分组ID
     * 
     * 实现说明：
     * 1. 使用StringBuilder而不是String，避免字符串拼接带来的性能问题
     * 2. 通过循环从字符集中随机选择字符
     * 3. 每个位置的字符都是独立随机选择的
     *
     * @param length 要生成的随机字符串长度
     * @return 指定长度的随机字符串
     * @implNote 当length较大时要注意内存使用
     */
    public static String generateRandom(int length) {
        // 预分配StringBuilder容量，避免动态扩容
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            // 使用SecureRandom生成随机索引
            int randomIndex = RANDOM.nextInt(CHARACTERS.length());
            // 将随机选择的字符追加到结果中
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }
}
