

package com.nageoffer.shortlink.admin.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis-Plus 元数据自动填充处理器
 * 
 * 设计目的：
 * 1. 自动处理实体类中的通用字段（创建时间、更新时间、删除标记）
 * 2. 减少重复代码，统一字段填充逻辑
 * 3. 确保数据的一致性和完整性
 * 
 * 应用场景：
 * - 在实体类插入时自动填充创建时间、更新时间和删除标记
 * - 在实体类更新时自动更新修改时间
 * 
 * 注意：需要配合实体类中的 @TableField(fill = FieldFill.XXX) 注解使用
 */
@Primary
@Component(value = "myMetaObjectHandlerByAdmin")
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作的字段填充策略
     * 
     * 填充字段：
     * 1. createTime：创建时间，使用当前时间
     * 2. updateTime：更新时间，使用当前时间
     * 3. delFlag：删除标记，默认为0（未删除）
     * 
     * @param metaObject 元数据对象，包含实体类信息
     * 
     * 注意事项：
     * - 使用strictInsertFill确保类型安全
     * - Date::new 用于获取当前时间，避免多次创建Date对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 创建时间和更新时间使用当前时间
        strictInsertFill(metaObject, "createTime", Date::new, Date.class);
        strictInsertFill(metaObject, "updateTime", Date::new, Date.class);
        // 删除标记默认为0（未删除）
        strictInsertFill(metaObject, "delFlag", () -> 0, Integer.class);
    }

    /**
     * 更新操作的字段填充策略
     * 
     * 填充字段：
     * - updateTime：更新时间，使用当前时间
     * 
     * @param metaObject 元数据对象，包含实体类信息
     * 
     * 注意事项：
     * - 只更新修改时间，保持其他字段不变
     * - 使用strictInsertFill而不是updateFill，避免空值覆盖
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时仅修改更新时间
        strictInsertFill(metaObject, "updateTime", Date::new, Date.class);
    }
}
