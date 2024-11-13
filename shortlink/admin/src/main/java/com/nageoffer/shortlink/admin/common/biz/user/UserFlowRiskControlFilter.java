package com.nageoffer.shortlink.admin.common.biz.user;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.nageoffer.shortlink.admin.common.convention.exception.ClientException;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.config.UserFlowRiskControlConfiguration;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import static com.nageoffer.shortlink.admin.common.convention.errorcode.BaseErrorCode.FLOW_LIMIT_ERROR;

/**
 * 用户操作流量风控过滤器
 * 
 * 主要功能：
 * 1. 实现基于Redis的分布式限流功能
 * 2. 防止用户短时间内发送过多请求，保护系统免受攻击
 * 3. 使用Lua脚本确保限流操作的原子性
 */
@Slf4j
@RequiredArgsConstructor
public class UserFlowRiskControlFilter implements Filter {

    // 注入Redis操作模板，用于执行限流逻辑
    private final StringRedisTemplate stringRedisTemplate;
    // 注入限流配置，包含时间窗口和最大访问次数等参数
    private final UserFlowRiskControlConfiguration userFlowRiskControlConfiguration;

    /**
     * 用户操作流量风控LUA脚本路径
     * 
     * 实现原理：
     * 1. 使用Redis作为计数器存储
     * 2. 通过Lua脚本保证计数和过期时间设置的原子性
     * 3. 在指定时间窗口内限制用户的访问次数
     * 4. 超过限制则拒绝请求
     */
    private static final String USER_FLOW_RISK_CONTROL_LUA_SCRIPT_PATH = "lua/user_flow_risk_control.lua";

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 初始化Redis脚本执行器
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        // 设置Lua脚本路径
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(USER_FLOW_RISK_CONTROL_LUA_SCRIPT_PATH)));
        // 设置脚本返回值类型为Long
        redisScript.setResultType(Long.class);
        
        // 获取当前用户名，如果未登录则使用"other"
        String username = Optional.ofNullable(UserContext.getUsername()).orElse("other");
        
        // 执行限流脚本
        Long result;
        try {
            // 调用Redis执行Lua脚本
            // 参数1：用户标识
            // 参数2：时间窗口大小（秒）
            result = stringRedisTemplate.execute(redisScript, 
                Lists.newArrayList(username), 
                userFlowRiskControlConfiguration.getTimeWindow());
        } catch (Throwable ex) {
            // 脚本执行异常时记录日志并返回限流错误
            log.error("执行用户请求流量限制LUA脚本出错", ex);
            returnJson((HttpServletResponse) response, 
                JSON.toJSONString(Results.failure(new ClientException(FLOW_LIMIT_ERROR))));
            return;
        }

        // 检查限流结果
        // result为null表示脚本执行失败
        // result大于最大访问次数表示已超过限制
        if (result == null || result > userFlowRiskControlConfiguration.getMaxAccessCount()) {
            returnJson((HttpServletResponse) response, 
                JSON.toJSONString(Results.failure(new ClientException(FLOW_LIMIT_ERROR))));
            return;
        }

        // 未超过限流阈值，继续处理请求
        filterChain.doFilter(request, response);
    }

    /**
     * 将JSON响应写回客户端
     * 
     * @param response HTTP响应对象
     * @param json 要返回的JSON字符串
     */
    private void returnJson(HttpServletResponse response, String json) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        try (PrintWriter writer = response.getWriter()) {
            writer.print(json);
        }
    }
}
