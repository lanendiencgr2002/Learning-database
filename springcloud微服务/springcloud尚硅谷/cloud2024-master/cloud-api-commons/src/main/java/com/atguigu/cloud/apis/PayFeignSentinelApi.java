package com.atguigu.cloud.apis;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.atguigu.cloud.resp.ResultData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @auther zzyy
 * @create 2024-01-05 13:05
 */

// 异常情况下，服务降级，比如要sentinel那边调用这个feign远程调用，但是sentinel那边挂了，再访问
@FeignClient(value = "nacos-payment-provider",fallback = PayFeignSentinelApiFallBack.class)
public interface PayFeignSentinelApi
{
    @GetMapping(value = "/pay/nacos/get/{orderNo}")
    public ResultData getPayByOrderNo(@PathVariable("orderNo") String orderNo);
}
