package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短链接浏览器访问统计实体类
 * 
 * 用于记录和统计短链接在不同浏览器上的访问情况,包括访问量、日期等信息。
 * 该实体类映射到数据库表t_link_browser_stats。
 *
 * Attributes:
 *     id (Long): 主键ID
 *     fullShortUrl (String): 完整的短链接URL
 *     date (Date): 统计日期
 *     cnt (Integer): 访问计数
 *     browser (String): 浏览器类型
 *
 * Table: t_link_browser_stats
 *
 * See Also:
 *     BaseDO: 继承基础实体类获取通用字段
 *     LinkAccessLogsDO: 访问日志详细记录
 */
@Data
@TableName("t_link_browser_stats")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkBrowserStatsDO extends BaseDO {

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
     * 访问计数
     */
    private Integer cnt;

    /**
     * 浏览器类型
     */
    private String browser;
}
