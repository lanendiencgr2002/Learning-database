

package com.nageoffer.shortlink.admin.common.serialize;

import cn.hutool.core.util.DesensitizedUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * 手机号脱敏序列化器
 * 
 * 设计目的：
 * 1. 在数据序列化为JSON时自动对手机号进行脱敏处理
 * 2. 保护用户隐私，符合数据安全要求
 * 3. 通过继承JsonSerializer实现自定义序列化逻辑
 * 
 * 使用方式：
 * 在需要脱敏的手机号字段上添加注解：
 * @JsonSerialize(using = PhoneDesensitizationSerializer.class)
 * 
 * 脱敏规则：
 * - 保留前3位和后4位
 * - 中间数字使用*代替
 * 示例：18812345678 -> 188****5678
 */
public class PhoneDesensitizationSerializer extends JsonSerializer<String> {
    
    /**
     * 序列化方法：将手机号转换为脱敏格式
     * 
     * @param phone 原始手机号
     * @param jsonGenerator JSON生成器，用于写入脱敏后的值
     * @param serializerProvider 序列化提供者，提供序列化上下文
     * @throws IOException 如果写入JSON时发生IO异常
     * 
     * 注意事项：
     * 1. 输入的phone可能为null，由hutool工具类处理
     * 2. 使用DesensitizedUtil确保脱敏的一致性和安全性
     */
    @Override
    public void serialize(String phone, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        // 使用hutool工具类进行手机号脱敏，确保处理的统一性和安全性
        String phoneDesensitization = DesensitizedUtil.mobilePhone(phone);
        // 将脱敏后的手机号写入JSON流
        jsonGenerator.writeString(phoneDesensitization);
    }
}