package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 网络类型统计实体类
 * 
 * 用于存储和管理短链接在不同网络环境下的访问统计数据，继承自BaseDO基础实体类。
 * 
 * Args:
 *     id (Long): 主键ID，唯一标识一条网络统计记录
 *     fullShortUrl (String): 完整短链接URL
 *     date (Date): 统计日期
 *     cnt (Integer): 当日访问量计数
 *     network (String): 访问网络类型标识(如WiFi、4G、5G等)
 *     
 * Notes:
 *     - 表名映射为t_link_network_stats
 *     - 包含创建时间、更新时间等基础字段(来自BaseDO)
 *     - 用于分析不同网络环境下的用户访问行为
 */
@Data
@TableName("t_link_network_stats")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkNetworkStatsDO extends BaseDO {

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
     * 访问网络
     */
    private String network;
}
