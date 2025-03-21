package com.atguigu.cloud.mygateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * ⭐ 自定义全局过滤器 - 接口性能统计
 * 
 * == 工作原理 ==
 * 1. 记录请求开始时间
 * 2. 请求处理完成后计算耗时
 * 3. 输出详细的请求信息和性能数据
 * 
 * 💡 自动注册机制：
 * - @Component：Spring Boot自动将过滤器注册到应用上下文
 * - GlobalFilter接口：Gateway自动将其应用于所有请求，无需配置文件配置
 * 
 * ❗ 关联：实现了Gateway的性能监控功能，为接口优化提供数据支持
 * 
 * @auther zzyy
 * @create 2023-12-31 21:05
 */

@Component
@Slf4j
public class MyGlobalFilter implements GlobalFilter, Ordered
{
    public static final String BEGIN_VISIT_TIME = "begin_visit_time";//开始调用方法的时间

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
    {
        // ⭐ 第一步：记录请求开始时间
        // exchange「请求-响应交互上下文」用于在过滤器链中传递数据
        exchange.getAttributes().put(BEGIN_VISIT_TIME, System.currentTimeMillis());
        
        // ⭐ 第二步：继续过滤器链并添加后置处理逻辑
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // 从上下文中获取开始时间
            Long beginVisitTime = exchange.getAttribute(BEGIN_VISIT_TIME);
            if(beginVisitTime != null)
            {
                // 输出详细的请求信息和性能统计
                log.info("访问接口主机："+exchange.getRequest().getURI().getHost());
                log.info("访问接口端口："+exchange.getRequest().getURI().getPort());
                log.info("访问接口URL："+exchange.getRequest().getURI().getPath());
                log.info("访问接口URL后面参数："+exchange.getRequest().getURI().getRawQuery());
                log.info("访问接口时长："+(System.currentTimeMillis() - beginVisitTime) + "毫秒");
                log.info("============分割线==========================");
                System.out.println();
            }
        }));
    }

    /**
     * ⭐ 过滤器优先级设置
     * 数字越小，优先级越高
     * @return 优先级值
     */
    @Override
    public int getOrder()
    {
        return -1;
    }
}
