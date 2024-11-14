package com.nageoffer.shortlink.project.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 短链接创建请求数据传输对象
 * 用于封装创建短链接时的请求参数
 * 
 * @Data - 自动生成getter/setter/toString等方法
 * @Builder - 启用建造者模式，方便对象创建
 * @NoArgsConstructor/@AllArgsConstructor - 提供无参和全参构造方法
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortLinkCreateReqDTO {

    /**
     * 短链接域名
     * 示例：nurl.ink
     * 用于生成完整的短链接URL
     */
    private String domain;

    /**
     * 原始链接URL
     * 需要被转换成短链接的完整URL地址
     * 示例：https://www.example.com/very/long/url/path
     */
    private String originUrl;

    /**
     * 分组标识符
     * 用于对短链接进行分类管理
     * 通常是一个唯一的字符串标识
     */
    private String gid;

    /**
     * 创建方式
     * 0：通过API接口创建
     * 1：通过Web控制台创建
     * 用于区分短链接的来源，便于统计和管理
     */
    private Integer createdType;

    /**
     * 有效期类型
     * 0：永久有效
     * 1：自定义有效期
     * 决定短链接的生命周期管理方式
     */
    private Integer validDateType;
    /**
     * 自定义有效期截止时间
     * 仅当validDateType=1时有效
     * 使用GMT+8时区
     * 格式：yyyy-MM-dd HH:mm:ss
     * 
     * @JsonFormat 注解用于:
     * 1. 序列化: 将Date对象转换为指定格式的字符串
     * 2. 反序列化: 将字符串转换为Date对象
     * - pattern: 定义日期时间的格式模式
     * - timezone: 指定时区,这里使用GMT+8(北京时间)
     * 这样在前后端交互时可以保证日期格式的统一性和准确性
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date validDate;

    /**
     * 短链接描述信息
     * 可选字段，用于添加额外的备注说明
     * 帮助管理者理解该短链接的用途
     */
    private String describe;
}
