package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接跳转实体类
 * 
 * 用于存储短链接的跳转关系信息，不继承BaseDO基础实体类。
 * 
 * Args:
 *     id (Long): 主键ID，唯一标识一条跳转记录
 *     gid (String): 分组标识
 *     fullShortUrl (String): 完整短链接URL
 *     
 * Notes:
 *     - 表名映射为t_link_goto
 *     - 不包含创建时间、更新时间等基础字段
 *     - 主要用于短链接跳转路由和分组管理
 *     - 相比其他实体类结构简单，仅包含必要的跳转信息
 */
@Data
@Builder
@TableName("t_link_goto")
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkGotoDO {

    /**
     * ID
     */
    private Long id;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 完整短链接
     */
    private String fullShortUrl;
}
