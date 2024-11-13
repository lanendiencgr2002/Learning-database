package com.nageoffer.shortlink.gateway.filter;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.nageoffer.shortlink.gateway.config.Config;
import com.nageoffer.shortlink.gateway.dto.GatewayErrorResult;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Token验证网关过滤器工厂
 * 主要职责：
 * 1. 验证请求中的Token有效性
 * 2. 处理白名单路径请求
 * 3. 将用户信息注入到请求头中
 */
@Component
public class TokenValidateGatewayFilterFactory extends AbstractGatewayFilterFactory<Config> {

    private final StringRedisTemplate stringRedisTemplate;

    public TokenValidateGatewayFilterFactory(StringRedisTemplate stringRedisTemplate) {
        super(Config.class);
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 实现网关过滤器逻辑
     * 处理流程：
     * 1. 检查请求路径是否在白名单中
     * 2. 验证Token的有效性
     * 3. 注入用户信息到请求头
     * 4. 处理验证失败的情况
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            // 获取请求路径
            String requestPath = request.getPath().toString();
            // 获取请求方法
            String requestMethod = request.getMethod().name();
            
            // 白名单路径直接放行
            if (!isPathInWhiteList(requestPath, requestMethod, config.getWhitePathList())) {
                String username = request.getHeaders().getFirst("username");
                String token = request.getHeaders().getFirst("token");
                Object userInfo;
                
                // 验证Token并获取用户信息
                // Redis key格式: short-link:login:{username}
                // StringUtils.hasText 判断字符串是否不为空且不为null 至少一个非空白字符
                if (StringUtils.hasText(username) && 
                    StringUtils.hasText(token) && 
                    (userInfo = stringRedisTemplate.opsForHash().get("short-link:login:" + username, token)) != null) {
                    
                    // Token验证成功，将用户信息注入请求头
                    // 注意：realName需要URL编码以处理中文字符
                    JSONObject userInfoJsonObject = JSON.parseObject(userInfo.toString());
                    ServerHttpRequest.Builder builder = exchange.getRequest().mutate().headers(httpHeaders -> {
                        httpHeaders.set("userId", userInfoJsonObject.getString("id"));
                        httpHeaders.set("realName", URLEncoder.encode(userInfoJsonObject.getString("realName"), StandardCharsets.UTF_8));
                    });
                    
                    return chain.filter(exchange.mutate().request(builder.build()).build());
                }
                
                // Token验证失败，返回401未授权错误
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.writeWith(Mono.fromSupplier(() -> {
                    DataBufferFactory bufferFactory = response.bufferFactory();
                    GatewayErrorResult resultMessage = GatewayErrorResult.builder()
                            .status(HttpStatus.UNAUTHORIZED.value())
                            .message("Token validation error")
                            .build();
                    return bufferFactory.wrap(JSON.toJSONString(resultMessage).getBytes());
                }));
            }
            return chain.filter(exchange);
        };
    }

    /**
     * 检查请求路径是否在白名单中
     * 白名单规则：
     * 1. 路径前缀匹配白名单列表中的任意项
     * 2. 特殊处理用户注册接口 (/api/short-link/admin/v1/user POST方法)
     */
    // whitePathList.stream() 转为不是引用
    // .anyMatch(whitePath -> requestPath.startsWith(whitePath))
    private boolean isPathInWhiteList(String requestPath, String requestMethod, List<String> whitePathList) {
        return (!CollectionUtils.isEmpty(whitePathList) && 
                whitePathList.stream().anyMatch(requestPath::startsWith)) || 
               (Objects.equals(requestPath, "/api/short-link/admin/v1/user") && 
                Objects.equals(requestMethod, "POST"));
    }
}
