package com.atguigu.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** sentinel
 * 底层会在代码中加上try catch，如果抛出异常，会进入降级方法
 * 就在@feignclient的fallback属性中指定降级方法
 */

/**
 * @auther zzyy
 * @create 2024-01-02 12:22
 */
@EnableDiscoveryClient
@SpringBootApplication
public class Main8401
{
    public static void main(String[] args)
    {
        SpringApplication.run(Main8401.class,args);
    }
}