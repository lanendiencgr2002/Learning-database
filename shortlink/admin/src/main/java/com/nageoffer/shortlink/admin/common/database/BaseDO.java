package com.nageoffer.shortlink.admin.common.database;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.util.Date;

/**
 * 数据库持久层对象基础属性
 * 作为实体类的基类，提供通用字段，其他实体类可以通过继承获得这些基础字段
 */
@Data  // Lombok注解，自动生成getter、setter、equals、hashCode和toString方法
public class BaseDO {

    /**
     * 创建时间
     * 在数据插入时自动填充当前时间
     * FieldFill.INSERT: 表示仅在插入记录时进行填充
     */
    @TableField(fill = FieldFill.INSERT)  // MyBatis-Plus注解，指定字段的自动填充策略
    private Date createTime;

    /**
     * 修改时间
     * 在数据插入和更新时都会自动填充为当前时间
     * FieldFill.INSERT_UPDATE: 表示在插入和更新记录时都进行填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标识 0：未删除 1：已删除
     * 用于实现逻辑删除功能，避免物理删除数据
     * 在数据插入时自动填充为0（未删除状态）
     * FieldFill.INSERT: 表示仅在插入记录时进行填充
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer delFlag;
}