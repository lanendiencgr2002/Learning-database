package com.nageoffer.shortlink.project.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * MyBatis-Plus 自动填充处理器
 * 
 * 该类用于在数据库操作时自动填充特定字段，主要功能：
 * 1. 在插入时自动填充创建时间、更新时间和删除标志
 * 2. 在更新时自动更新修改时间
 * 3. 确保审计字段的一致性和完整性
 * 
 * 应用场景：
 * - 需要记录数据创建和修改时间
 * - 实现软删除功能
 * - 维护数据审计信息
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入时的自动填充逻辑
     * 
     * 填充规则：
     * - createTime：当前时间
     * - updateTime：当前时间
     * - delFlag：0（表示未删除）
     * 
     * 注意：
     * - 使用strictInsertFill确保类型安全
     * - Date::new 提供当前时间戳
     * 
     * @param metaObject 元数据对象，包含要操作的字段信息
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 创建时间和更新时间使用当前时间
        strictInsertFill(metaObject, "createTime", Date::new, Date.class);
        strictInsertFill(metaObject, "updateTime", Date::new, Date.class);
        // 删除标志默认为0（未删除）
        strictInsertFill(metaObject, "delFlag", () -> 0, Integer.class);
    }

    /**
     * 更新时的自动填充逻辑
     * 
     * 填充规则：
     * - 仅更新updateTime为当前时间
     * 
     * 特点：
     * - 只在显式调用更新方法时触发
     * - 不影响其他字段的值
     * 
     * @param metaObject 元数据对象，包含要操作的字段信息
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时间设置为当前时间
        strictInsertFill(metaObject, "updateTime", Date::new, Date.class);
    }
}
