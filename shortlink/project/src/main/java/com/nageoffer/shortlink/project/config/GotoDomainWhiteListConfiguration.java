package com.nageoffer.shortlink.project.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 跳转域名白名单配置类
 * 
 * 该配置类用于管理短链接跳转时的域名安全控制，主要功能：
 * 1. 控制是否启用域名白名单验证
 * 2. 管理允许跳转的域名列表
 * 3. 提供域名白名单的元数据
 * 
 * 配置示例：
 * short-link:
 *   goto-domain:
 *     white-list:
 *       enable: true
 *       names: "谷歌,百度"
 *       details: 
 *         - "google.com"
 *         - "baidu.com"
 */
@Data
@Component
@ConfigurationProperties(prefix = "short-link.goto-domain.white-list")
public class GotoDomainWhiteListConfiguration {

    /**
     * 白名单验证开关
     * 
     * true: 启用域名白名单验证
     * false: 允许跳转到任意域名
     * 
     * 安全建议：
     * - 生产环境建议启用
     * - 测试环境可以根据需要关闭
     */
    private Boolean enable;

    /**
     * 白名单域名说明
     * 
     * 用途：
     * - 为白名单域名提供可读的描述
     * - 便于管理和识别域名用途
     * 
     * 格式：
     * - 多个名称用逗号分隔
     * - 建议使用有意义的业务名称
     */
    private String names;

    /**
     * 白名单域名列表
     * 
     * 特点：
     * - 支持完整域名匹配
     * - 区分大小写
     * - 不支持通配符
     * 
     * 注意：
     * - 域名格式必须合法
     * - 建议不要包含协议前缀(http/https)
     * - 可以包含子域名
     */
    private List<String> details;
}
