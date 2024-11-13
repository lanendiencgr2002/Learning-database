package com.nageoffer.shortlink.project.config;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sentinel 限流规则配置类
 * 实现 InitializingBean 接口，确保在 Spring 容器启动时自动初始化限流规则
 * 
 * 主要功能：
 * 1. 为短链接创建接口配置 QPS 限流规则
 * 2. 通过 Sentinel 实现分布式限流控制
 * 3. 在应用启动时自动加载限流规则
 */
@Component
public class SentinelRuleConfig implements InitializingBean {

    /**
     * Spring 容器初始化完成后自动执行该方法
     * 用于设置和加载 Sentinel 限流规则
     * 
     * 当前配置：
     * - 限制创建短链接接口的访问频率
     * - 采用 QPS 限流模式
     * - 限流阈值设置为每秒 1 次请求
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 创建限流规则集合
        List<FlowRule> rules = new ArrayList<>();
        // 添加创建短链接的限流规则
        rules.add(createShortLinkFlowRule());
        // 加载限流规则
        FlowRuleManager.loadRules(rules);
    }

    /**
     * 创建短链接接口的限流规则
     * @return FlowRule 限流规则对象
     */
    private FlowRule createShortLinkFlowRule() {
        FlowRule createOrderRule = new FlowRule();
        // 设置被限流的资源名称，与 @SentinelResource 注解值保持一致
        createOrderRule.setResource("create_short-link");
        // 设置限流模式为 QPS 模式，适用于高并发场景
        createOrderRule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        // 设置 QPS 阈值为 1，即每秒最多处理 1 个请求
        createOrderRule.setCount(1);
        return createOrderRule;
    }
}
