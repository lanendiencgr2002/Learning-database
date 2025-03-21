package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 访问设备统计实体类
 * 
 * 用于存储和管理短链接在不同设备上的访问统计数据，继承自BaseDO基础实体类。
 * 
 * Args:
 *     id (Long): 主键ID，唯一标识一条设备统计记录
 *     fullShortUrl (String): 完整短链接URL
 *     date (Date): 统计日期
 *     cnt (Integer): 当日访问量计数
 *     device (String): 访问设备类型标识
 *     
 * Notes:
 *     - 表名映射为t_link_device_stats
 *     - 包含创建时间、更新时间等基础字段(来自BaseDO)
 */
@Data
@TableName("t_link_device_stats")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkDeviceStatsDO extends BaseDO {

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
     * 访问设备
     */
    private String device;
}
