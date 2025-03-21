package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 地区统计实体类
 * 
 * 用于存储和管理短链接在不同地理位置的访问统计数据，继承自BaseDO基础实体类。
 * 
 * Args:
 *     id (Long): 主键ID，唯一标识一条地区统计记录
 *     fullShortUrl (String): 完整短链接URL
 *     date (Date): 统计日期
 *     cnt (Integer): 当日访问量计数
 *     province (String): 省份名称
 *     city (String): 城市名称
 *     adcode (String): 城市编码，用于地理位置标识
 *     country (String): 国家标识
 *     
 * Notes:
 *     - 表名映射为t_link_locale_stats
 *     - 包含创建时间、更新时间等基础字段(来自BaseDO)
 *     - 支持多级地理位置分析(国家-省份-城市)
 */
@Data
@TableName("t_link_locale_stats")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkLocaleStatsDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 日期
     */
    private Date date;

    /**
     * 访问量
     */
    private Integer cnt;

    /**
     * 省份名称
     */
    private String province;

    /**
     * 市名称
     */
    private String city;

    /**
     * 城市编码
     */
    private String adcode;

    /**
     * 国家标识
     */
    private String country;
}
