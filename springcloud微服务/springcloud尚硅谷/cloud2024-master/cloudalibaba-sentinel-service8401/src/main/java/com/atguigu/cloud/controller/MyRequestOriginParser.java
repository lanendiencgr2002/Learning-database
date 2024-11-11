package com.atguigu.cloud.handler;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * @auther zzyy
 * @create 2024-01-04 10:42
 */
//sentinel授权规则，用来处理请求的来源，会自动载入sentinel，不需要额外配置
@Component
public class MyRequestOriginParser implements RequestOriginParser
{
    //从请求中解析出名为 serverName 的参数值并返回。 表示serverName参数如果是test1，test2就不给通过
    @Override
    public String parseOrigin(HttpServletRequest request)
    {
        return request.getParameter("serverName");
    }
}