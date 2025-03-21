package com.nageoffer.shortlink.project.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.nageoffer.shortlink.project.common.database.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 短链接访问日志监控实体类
 * 
 * 用于记录和存储短链接访问的详细日志信息,包括访问者信息、设备信息、网络信息等。
 * 该实体类映射到数据库表t_link_access_logs。
 *
 * Attributes:
 *     id (Long): 主键ID
 *     fullShortUrl (String): 完整的短链接URL
 *     user (String): 访问用户的唯一标识
 *     browser (String): 用户使用的浏览器类型
 *     os (String): 用户的操作系统
 *     ip (String): 访问者的IP地址
 *     network (String): 访问者使用的网络类型
 *     device (String): 访问者使用的设备类型
 *     locale (String): 访问者所在地区信息
 *
 * See Also:
 *     BaseDO: 继承自基础实体类,包含通用字段
 */
@Data
@TableName("t_link_access_logs")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkAccessLogsDO extends BaseDO {

    /**
     * id
     */
    private Long id;

    /**
     * 完整短链接
     */
    private String fullShortUrl;

    /**
     * 用户信息
     */
    private String user;

    /**
     * 浏览器
     */
    private String browser;

    /**
     * 操作系统
     */
    private String os;

    /**
     * ip
     */
    private String ip;

    /**
     * 访问网络
     */
    private String network;

    /**
     * 访问设备
     */
    private String device;

    /**
     * 地区
     */
    private String locale;
}
