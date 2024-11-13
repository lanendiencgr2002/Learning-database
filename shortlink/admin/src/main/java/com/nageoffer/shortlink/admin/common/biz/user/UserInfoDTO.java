 package com.nageoffer.shortlink.admin.common.biz.user;

 import com.alibaba.fastjson2.annotation.JSONField;
 import lombok.AllArgsConstructor;
 import lombok.Builder;
 import lombok.Data;
 import lombok.NoArgsConstructor;
 
 /**
  * 用户信息数据传输对象（DTO）
  * 
  * 主要用途：
  * 1. 在系统各层之间传递用户基本信息
  * 2. 用于前后端数据交互时的用户信息封装
  * 3. 避免直接暴露数据库实体，提供信息安全性
  *
  * 设计考虑：
  * 1. 使用 Lombok 注解简化代码，提高开发效率
  * 2. 字段命名遵循业务语义，提高代码可读性
  * 3. 通过 JSONField 注解处理字段序列化差异
  */
 @Data               // 自动生成 getter、setter、toString 等方法
 @NoArgsConstructor  // 生成无参构造函数，便于框架实例化
 @AllArgsConstructor // 生成全参构造函数，便于对象创建
 @Builder            // 提供建造者模式支持，方便对象构建
 public class UserInfoDTO {
 
     /**
      * 用户唯一标识
      * 使用 JSONField 注解处理序列化时的字段名映射，
      * 数据库中使用 userId，而 API 接口中使用 id
      */
     @JSONField(name = "id")
     private String userId;
 
     /**
      * 用户名
      * 用于用户登录的唯一标识符，通常是字母、数字的组合
      */
     private String username;
 
     /**
      * 真实姓名
      * 用户的实际姓名，用于实名认证和管理功能
      */
     private String realName;
 }