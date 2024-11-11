package com.atguigu.cloud.config; // 定义包名

import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule; // 导入限流规则类
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager; // 导入规则管理类
import com.alibaba.csp.sentinel.adapter.gateway.sc.SentinelGatewayFilter; // 导入 Sentinel 网关过滤器
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler; // 导入阻塞请求处理器
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager; // 导入网关回调管理器
import com.alibaba.csp.sentinel.adapter.gateway.sc.exception.SentinelGatewayBlockExceptionHandler; // 导入限流异常处理器
import org.springframework.beans.factory.ObjectProvider; // 导入对象提供者
import org.springframework.cloud.gateway.filter.GlobalFilter; // 导入全局过滤器
import org.springframework.context.annotation.Bean; // 导入 Bean 注解
import org.springframework.context.annotation.Configuration; // 导入配置类注解
import org.springframework.core.Ordered; // 导入有序接口
import org.springframework.core.annotation.Order; // 导入排序注解
import org.springframework.http.HttpStatus; // 导入 HTTP 状态
import org.springframework.http.MediaType; // 导入媒体类型
import org.springframework.http.codec.ServerCodecConfigurer; // 导入服务器编码配置
import org.springframework.web.reactive.function.BodyInserters; // 导入请求体插入工具
import org.springframework.web.reactive.function.server.ServerResponse; // 导入服务器响应类
import org.springframework.web.reactive.result.view.ViewResolver; // 导入视图解析器
import org.springframework.web.server.ServerWebExchange; // 导入服务器交换类
import reactor.core.publisher.Mono; // 导入响应式单值类型

import javax.annotation.PostConstruct; // 导入构造后注解
import java.util.*; // 导入所有集合类

/**
 * @auther zzyy
 * @create 2024-01-05 14:02
 */
@Configuration // 声明该类为配置类
public class GatewayConfiguration {
    private final List<ViewResolver> viewResolvers; // 视图解析器列表
    private final ServerCodecConfigurer serverCodecConfigurer; // 服务器编码配置
    // 构造函数，初始化视图解析器和编码配置
    public GatewayConfiguration(ObjectProvider<List<ViewResolver>> viewResolversProvider,
                                ServerCodecConfigurer serverCodecConfigurer) {
        this.viewResolvers = viewResolversProvider.getIfAvailable(Collections::emptyList); // 获取视图解析器
        this.serverCodecConfigurer = serverCodecConfigurer; // 初始化编码配置
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE) // 设置优先级为最高
    public SentinelGatewayBlockExceptionHandler sentinelGatewayBlockExceptionHandler() {
        // 注册 Spring Cloud Gateway 的限流异常处理器
        return new SentinelGatewayBlockExceptionHandler(viewResolvers, serverCodecConfigurer);
    }

    @Bean
    @Order(-1) // 设置优先级为最低
    public GlobalFilter sentinelGatewayFilter() {
        return new SentinelGatewayFilter(); // 创建 Sentinel 网关过滤器
    }

    @PostConstruct
    public void doInit() {
        // 初始化方法
        // 自己动手，丰衣足食
        // initGatewayRules(); // 可能用于初始化网关规则
        initBlockHandler(); // 初始化限流处理器
    }

    // 处理和自定义返回的异常信息
    private void initBlockHandler() {
        Set<GatewayFlowRule> rules = new HashSet<>(); // 创建限流规则集合
        rules.add(new GatewayFlowRule("pay_routh1")
                .setCount(2)  // 每秒最多 2 次请求
                .setIntervalSec(1)); // 添加规则：每秒最多 2 次请求

        GatewayRuleManager.loadRules(rules); // 加载限流规则

        BlockRequestHandler handler = new BlockRequestHandler() {
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
                Map<String, String> map = new HashMap<>(); // 创建错误信息映射

                map.put("errorCode", HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase()); // 设置错误代码
                map.put("errorMessage", "请求太过频繁，系统忙不过来，触发限流(sentinel+gateway整合Case)"); // 设置错误信息

                return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS) // 返回 429 状态
                        .contentType(MediaType.APPLICATION_JSON) // 设置内容类型为 JSON
                        .body(BodyInserters.fromValue(map)); // 返回错误信息
            }
        };

        GatewayCallbackManager.setBlockHandler(handler); // 设置自定义限流处理器
    }
}