package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接分组实体类
 * 
 * 该实体类用于存储短链接分组的相关信息,包括分组标识、名称、创建者等。
 * 该实体类映射到数据库表t_group。
 *
 * Attributes:
 *     id (Long): 主键ID
 *     gid (String): 分组唯一标识
 *     name (String): 分组名称
 *     username (String): 创建该分组的用户名
 *     sortOrder (Integer): 分组的排序顺序
 *
 * Table: t_group
 * 
 * See Also:
 *     BaseDO: 继承基础实体类获取通用字段
 */
@Data
@TableName("t_group")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupDO extends BaseDO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 创建分组用户名
     */
    private String username;

    /**
     * 分组排序
     */
    private Integer sortOrder;
}
