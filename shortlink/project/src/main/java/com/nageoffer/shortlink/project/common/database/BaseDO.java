package com.nageoffer.shortlink.project.common.database;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * 数据库持久层对象基础属性类
 * 
 * 该类作为所有数据库实体类的基类，提供了通用的审计字段：
 * - 创建时间
 * - 更新时间
 * - 删除标识
 * 
 * 通过继承此类，所有实体类都将自动包含这些基础字段，
 * 配合MyBatis-Plus的自动填充功能实现字段的自动管理
 */
@Data   // Lombok注解，自动生成getter、setter、toString等方法
public class BaseDO {

    /**
     * 创建时间
     * 
     * @TableField注解指定字段自动填充策略
     * FieldFill.INSERT: 表示在插入时自动填充当前时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 修改时间
     * 
     * FieldFill.INSERT_UPDATE: 表示在插入和更新时都会自动填充当前时间
     * 用于跟踪记录的最后修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标识
     * 
     * 采用逻辑删除策略：
     * 0：未删除（默认值）
     * 1：已删除
     * 
     * 在插入时自动填充默认值0
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;
}
