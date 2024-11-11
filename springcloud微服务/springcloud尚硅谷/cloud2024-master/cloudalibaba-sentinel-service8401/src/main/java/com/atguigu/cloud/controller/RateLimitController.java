package com.atguigu.cloud.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther zzyy
 * @create 2024-01-03 17:25
 * 自定义限流后返回的东西，不自定义返回的是默认的 Blocked by Sentinel (Flow Limiting)
 */
@RestController
@Slf4j
public class RateLimitController
{
    // 在sentinel控制台配置限流规则，然后访问这个地址，就会触发限流
    @GetMapping("/rateLimit/byUrl")
    public String byUrl()
    {
        return "按rest地址限流测试OK"; 
    }


    //按SentinelResource资源名称限流+自定义限流返回
    //blockHandler：自定义的限流返回内容 在8848还是要指定限流规则啥的
    //value：byResourceSentinelResource   资源名称+SentinelResource  然后在sentinel里就可以凭借这个找到服务
    @GetMapping("/rateLimit/byResource")
    @SentinelResource(value = "byResourceSentinelResource",blockHandler = "handlerBlockHandler")
    public String byResource()
    {
        return "按照资源名称SentinelResource限流测试OK，O(∩_∩)O";
    }
    public String handlerBlockHandler(BlockException blockException)
    {
        return "服务不可用触发了@SentinelResource启动，/(ㄒoㄒ)/~~";
    }


    //按SentinelResource资源名称限流+自定义限流返回+服务降级处理      在sentinel控制台配置中找资源名为那个的节点然后配置
    //流量过了会触发服务限流，然后调用blockHandler，自定义限流返回内容
    //出异常了会触发服务降级，然后调用fallback，自定义降级返回内容  如果有全局异常捕获，就不会有这个
    @GetMapping("/rateLimit/doAction/{p1}")
    @SentinelResource(value = "doActionSentinelResource",
            blockHandler = "doActionBlockHandler", fallback = "doActionFallback")
    public String doAction(@PathVariable("p1") Integer p1) {
        if (p1 == 0){
            throw new RuntimeException("p1等于零直接异常");
        }
        return "doAction";
    }

    //参数 原来的，blockexception
    public String doActionBlockHandler(@PathVariable("p1") Integer p1,BlockException e){
        log.error("sentinel配置自定义限流了:{}", e);
        return "sentinel配置自定义限流了";
    }

    public String doActionFallback(@PathVariable("p1") Integer p1,Throwable e){
        log.error("程序逻辑异常了:{}", e);
        return "程序逻辑异常了"+"\t"+e.getMessage();
    }


    /**
     * 热点参数限流
     * @param p1
     * @param p2
     * @return
     * 限流模式只支持QPS模式，固定写死了。（这才叫热点）
     * @SentinelResource注解的方法参数索引，0代表第一个参数，1代表第二个参数，
     * 长推单机阀值以及统计窗口时长表示在此窗口时间超过阀值就限流。
     * 只要url中有参数就会触发对应的限流规则
     */
    @GetMapping("/testHotKey")
    @SentinelResource(value = "testHotKey",blockHandler = "dealHandler_testHotKey")
    public String testHotKey(@RequestParam(value = "p1",required = false) String p1,
                             @RequestParam(value = "p2",required = false) String p2){
        return "------testHotKey";
    }
    public String dealHandler_testHotKey(String p1,String p2,BlockException exception)
    {
        return "-----dealHandler_testHotKey";
    }
}
