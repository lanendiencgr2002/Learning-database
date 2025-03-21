package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短链接实体类
 * 
 * 核心实体类，用于存储和管理短链接的基本信息及统计数据，继承自BaseDO基础实体类。
 * 
 * Args:
 *     id (Long): 主键ID，唯一标识一条短链接记录
 *     domain (String): 短链接域名
 *     shortUri (String): 短链接URI部分
 *     fullShortUrl (String): 完整短链接URL(domain+shortUri)
 *     originUrl (String): 原始链接URL
 *     clickNum (Integer): 总点击量
 *     gid (String): 分组标识
 *     enableStatus (Integer): 启用状态(0:启用 1:未启用)
 *     createdType (Integer): 创建类型(0:接口创建 1:控制台创建)
 *     validDateType (Integer): 有效期类型(0:永久有效 1:自定义)
 *     validDate (Date): 有效期截止日期
 *     describe (String): 短链接描述
 *     favicon (String): 网站图标标识
 *     totalPv (Integer): 历史总PV(页面访问量)
 *     totalUv (Integer): 历史总UV(独立访客数)
 *     totalUip (Integer): 历史总UIP(独立IP数)
 *     todayPv (Integer): 今日PV(非持久化字段)
 *     todayUv (Integer): 今日UV(非持久化字段)
 *     todayUip (Integer): 今日UIP(非持久化字段)
 *     delTime (Long): 删除时间戳
 *     
 * Notes:
 *     - 表名映射为t_link
 *     - 包含创建时间、更新时间等基础字段(来自BaseDO)
 *     - 今日统计数据(todayPv/todayUv/todayUip)为非持久化字段，仅用于运行时
 *     - describe字段使用特殊注解处理SQL关键字冲突
 */
@Data
@Builder
@TableName("t_link")
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * 域名
     */
    private String domain;

    /**
     * 短链接
     */
    private String shortUri;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 原始链接
     */
    private String originUrl;

    /**
     * 点击量
     */
    private Integer clickNum;

    /**
     * 分组标识
     */
    private String gid;

    /**
     * 启用标识 0：启用 1：未启用
     */
    private Integer enableStatus;

    /**
     * 创建类型 0：接口创建 1：控制台创建
     */
    private Integer createdType;

    /**
     * 有效期类型 0：永久有效 1：自定义
     */
    private Integer validDateType;

    /**
     * 有效期
     */
    private Date validDate;

    /**
     * 描述
     */
    @TableField("`describe`")
    private String describe;

    /**
     * 网站标识
     */
    private String favicon;

    /**
     * 历史PV
     */
    private Integer totalPv;

    /**
     * 历史UV
     */
    private Integer totalUv;

    /**
     * 历史UIP
     */
    private Integer totalUip;

    /**
     * 今日PV
     */
    @TableField(exist = false)
    private Integer todayPv;

    /**
     * 今日UV
     */
    @TableField(exist = false)
    private Integer todayUv;

    /**
     * 今日UIP
     */
    @TableField(exist = false)
    private Integer todayUip;

    /**
     * 删除时间
     */
    private Long delTime;
}
