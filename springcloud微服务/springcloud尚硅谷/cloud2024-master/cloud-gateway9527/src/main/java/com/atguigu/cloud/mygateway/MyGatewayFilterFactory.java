package com.atguigu.cloud.mygateway;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.SetPathGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;


/**
 * @auther zzyy
 * @create 2023-12-31 21:41
 */
@Component //名字要为 xxGatewayFilterFactory
public class MyGatewayFilterFactory extends AbstractGatewayFilterFactory<MyGatewayFilterFactory.Config>
{
    // 构造器，必须调用super(Config.class)
    public MyGatewayFilterFactory()
    {
        super(MyGatewayFilterFactory.Config.class);
    }

    // 必须实现apply方法
    @Override
    public GatewayFilter apply(MyGatewayFilterFactory.Config config)
    {
        return new GatewayFilter()
        {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain)
            {
                // 获取请求     
                ServerHttpRequest request = exchange.getRequest();
                System.out.println("进入了自定义网关过滤器MyGatewayFilterFactory，status："+config.getStatus());
                // 判断请求中是否包含atguigu参数
                if(request.getQueryParams().containsKey("atguigu")){ 
                    // 如果包含，则继续执行下一个filter
                    return chain.filter(exchange);
                }else{
                    // 如果不包含，则返回400状态码
                    exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                    return exchange.getResponse().setComplete();
                }
            }
        };
    }

    // 实现shortcutFieldOrder方法，可以在配置文件中 用逗号分隔的key=value形式，指定配置参数 不然只能用kv格式
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("status");
    }

    public static class Config //匹配的是status=atguigu
    {
        @Getter@Setter
        private String status;//设定一个状态值/标志位，它等于多少，匹配和才可以访问
    }
}
//单一内置过滤器GatewayFilter