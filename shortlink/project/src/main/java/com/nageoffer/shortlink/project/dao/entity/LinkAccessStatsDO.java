package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短链接基础访问监控实体
 * 
 * 该实体类用于存储短链接的访问统计数据,包括PV、UV、UIP等关键指标
 * 
 * Attributes:
 *     id (Long): 主键ID
 *     fullShortUrl (String): 完整的短链接URL
 *     date (Date): 统计日期
 *     pv (Integer): 页面访问量(Page View)
 *     uv (Integer): 独立访客数(Unique Visitor)
 *     uip (Integer): 独立IP数(Unique IP)
 *     hour (Integer): 访问小时数(0-23)
 *     weekday (Integer): 访问星期数(1-7)
 *
 * Table: t_link_access_stats
 * 
 * See Also:
 *     BaseDO: 继承基础实体类获取通用字段
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_link_access_stats")
public class LinkAccessStatsDO extends BaseDO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 完整短链接URL
     */
    private String fullShortUrl;

    /**
     * 统计日期
     */
    private Date date;

    /**
     * 页面访问量(PV)
     */
    private Integer pv;

    /**
     * 独立访客数(UV)
     */
    private Integer uv;

    /**
     * 独立IP数
     */
    private Integer uip;

    /**
     * 访问小时数(0-23)
     */
    private Integer hour;

    /**
     * 访问星期数(1-7)
     */
    private Integer weekday;
}
