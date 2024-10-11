package com.nageoffer.springbootladder.rocketmq4x.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Swagger3 文档配置
 *
 * @公众号：马丁玩编程，回复：加群，添加马哥微信（备注：ladder）获取更多项目资料
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class Swagger3Configuration implements ApplicationRunner {

    private final ConfigurableEnvironment environment;

    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("SpringDoc API Test")
                .description("拿个offer-开源&项目实战，RocketMQ发送示例程序，让Demo变得简单。校招&社招没项目？项目没亮点？<a href='https://nageoffer.com' target='_blank'>https://nageoffer.com</a>")
                .version("0.0.1"));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("API Document: http://127.0.0.1:{}{}/swagger-ui.html", environment.getProperty("server.port", "8080"), environment.getProperty("server.servlet.context-path", ""));
    }
}
