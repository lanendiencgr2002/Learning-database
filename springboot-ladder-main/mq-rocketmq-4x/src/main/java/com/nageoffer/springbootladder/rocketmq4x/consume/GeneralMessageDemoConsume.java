package com.nageoffer.springbootladder.rocketmq4x.consume;

import com.alibaba.fastjson.JSON;
import com.nageoffer.springbootladder.rocketmq4x.event.GeneralMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 普通消息消费者
 *
 * @公众号：马丁玩编程，回复：加群，添加马哥微信（备注：ladder）获取更多项目资料
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "rocketmq-demo_common-message_topic",
        selectorExpression = "general",
        consumerGroup = "rocketmq-demo_general-message_cg"
)
public class GeneralMessageDemoConsume implements RocketMQListener<GeneralMessageEvent> {

    @Override
    public void onMessage(GeneralMessageEvent message) {
        log.info("接到到RocketMQ消息，消息体：{}", JSON.toJSONString(message));
    }
}
