package com.nageoffer.springbootladder.rocketmq4x;

import com.nageoffer.springbootladder.rocketmq4x.event.GeneralMessageEvent;
import com.nageoffer.springbootladder.rocketmq4x.produce.GeneralMessageDemoProduce;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@SpringBootApplication
@Tag(name = "RocketMQ发送示例", description = "RocketMQ发送示例启动器")
public class RocketMQDemoApplication {

    private final GeneralMessageDemoProduce generalMessageDemoProduce;

    @PostMapping("/test/send/general-message")
    @Operation(summary = "发送RocketMQ普通消息")
    public String sendGeneralMessage() {
        String keys = UUID.randomUUID().toString();
        GeneralMessageEvent generalMessageEvent = GeneralMessageEvent.builder()
                .body("消息具体内容，可以是自定义对象，最终都会序列化为字符串。如果是取消订单，这里应该是订单ID或者相关联的信息")
                .keys(keys)
                .build();
        SendResult sendResult = generalMessageDemoProduce.sendMessage(
                "rocketmq-demo_common-message_topic",
                "general",
                keys,
                generalMessageEvent
        );
        return sendResult.getSendStatus().name();
    }

    public static void main(String[] args) {
        SpringApplication.run(RocketMQDemoApplication.class, args);
    }
}
