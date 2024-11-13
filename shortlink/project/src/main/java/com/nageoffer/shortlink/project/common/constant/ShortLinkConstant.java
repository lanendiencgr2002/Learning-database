package com.nageoffer.shortlink.project.common.constant;

/**
 * 短链接系统常量类
 * 
 * 该类定义了短链接系统中的核心常量，包括：
 * 1. 缓存相关的时间配置
 * 2. 外部服务API地址
 * 3. 系统默认参数
 * 
 * 注意：这些常量影响系统的核心行为，修改时需要谨慎
 */
public class ShortLinkConstant {

    /**
     * 永久短链接的默认缓存时间
     * 
     * 值: 2626560000L (约等于一个月的毫秒数)
     * 用途: 
     * - 控制永久短链接在缓存中的保留时间
     * - 避免缓存无限增长
     * 
     * 注意：
     * 1. 虽然叫"永久"，但仍设置了缓存时间以防止内存溢出
     * 2. 过期后会从数据库重新加载，不影响功能
     */
    public static final long DEFAULT_CACHE_VALID_TIME = 2626560000L;

    /**
     * 高德地图IP定位服务API地址
     * 
     * 用途:
     * - 通过IP地址获取访问者的地理位置信息
     * - 用于统计短链接访问的地理分布
     * 
     * 调用注意:
     * 1. 需要配合高德地图的API密钥使用
     * 2. 建议做好请求限流和错误处理
     * 3. 考虑接口的可用性和超时设置
     */
    public static final String AMAP_REMOTE_URL = "https://restapi.amap.com/v3/ip";
}
