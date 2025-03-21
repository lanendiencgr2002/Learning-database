package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 操作系统统计实体类
 * 
 * 用于存储和管理短链接在不同操作系统上的访问统计数据，继承自BaseDO基础实体类。
 * 
 * Args:
 *     id (Long): 主键ID，唯一标识一条操作系统统计记录
 *     fullShortUrl (String): 完整短链接URL
 *     date (Date): 统计日期
 *     cnt (Integer): 当日访问量计数
 *     os (String): 操作系统类型标识(如Windows、macOS、iOS、Android等)
 *     
 * Notes:
 *     - 表名映射为t_link_os_stats
 *     - 包含创建时间、更新时间等基础字段(来自BaseDO)
 *     - 用于分析不同操作系统用户的访问行为
 */
@Data
@TableName("t_link_os_stats")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkOsStatsDO extends BaseDO {

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
     * 操作系统
     */
    private String os;
}
