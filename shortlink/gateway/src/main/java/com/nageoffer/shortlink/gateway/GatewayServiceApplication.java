package com.nageoffer.shortlink.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/** 没有网关存在的一些问题：
 * 1. 路由管理&服务发现困难
 * 2. 安全性难以管理：
 *    - HTTPS访问 （网关是https，服务是http请求 不然要把每个服务都配置成https）
 *    - 黑白名单 （网关统一管理）
 *    - 用户登录 （网关统一管理，不用各个服务自己管理）
 *    - 数据请求加密防篡改等 （网关统一管理）
 * 3. 负载均衡问题 （网关统一管理）
 * 4. 监控和日志难以集中管理 （网关统一管理）
 * 5. 缺乏统一的API管理 （网关统一管理）
 */

/**
 * 网关服务应用启动器
 */
@SpringBootApplication
public class GatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
    }
}
