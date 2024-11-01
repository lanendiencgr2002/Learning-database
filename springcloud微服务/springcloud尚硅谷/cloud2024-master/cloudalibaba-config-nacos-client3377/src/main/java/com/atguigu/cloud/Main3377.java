package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** nacos2.x
 * 是通过grpc通信长连接，性能高
 * 客户端和服务器在启动后，会建立长连接，如果服务挂了，会感知到
 */

/** nacos1.4
 * 是通过http请求，每隔5秒发心跳包检测服务是否宕机
 * 性能比较低
 */

/**
 * @auther zzyy
 * @create 2024-01-01 16:54
 */
@EnableDiscoveryClient
@SpringBootApplication
public class Main3377
{
    public static void main(String[] args)
    {
        SpringApplication.run(Main3377.class,args);
    }
}