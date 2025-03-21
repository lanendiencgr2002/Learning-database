package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短链接今日统计实体类
 * 
 * 用于存储和管理短链接当日访问统计数据，包括PV、UV和UIP指标，继承自BaseDO基础实体类。
 * 
 * Args:
 *     id (Long): 主键ID，唯一标识一条今日统计记录
 *     fullShortUrl (String): 完整短链接URL
 *     date (Date): 统计日期(当天)
 *     todayPv (Integer): 今日页面访问量(Page View)
 *     todayUv (Integer): 今日独立访客数(Unique Visitor)
 *     todayUip (Integer): 今日独立IP数(Unique IP)
 *     
 * Notes:
 *     - 表名映射为t_link_stats_today
 *     - 包含创建时间、更新时间等基础字段(来自BaseDO)
 *     - 专门用于记录和分析当日实时统计数据
 *     - PV反映总访问量，UV反映独立用户数，UIP反映独立IP数
 */
@TableName("t_link_stats_today")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkStatsTodayDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * 短链接
     */
    private String fullShortUrl;

    /**
     * 日期
     */
    private Date date;

    /**
     * 今日pv
     */
    private Integer todayPv;

    /**
     * 今日uv
     */
    private Integer todayUv;

    /**
     * 今日ip数
     */
    private Integer todayUip;
}
