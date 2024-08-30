package cn.lili.trigger.controller;

import cn.lili.trigger.plugin.interfaces.TimeTrigger;
import cn.lili.trigger.plugin.model.TimeTriggerMsg;
import cn.lili.trigger.plugin.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TestController {

    @Autowired
    private TimeTrigger timeTrigger;

    @GetMapping
    public void test(Integer seconds) {
        Long executeTime = DateUtil.getDateline() + 5;
        if (seconds != null) {
            executeTime = DateUtil.getDateline() + seconds;
        }
        TimeTriggerMsg timeTriggerMsg = new TimeTriggerMsg(executeTime, "testTimeTriggerExecutor", "test params");
        timeTrigger.add(timeTriggerMsg);

    }

}
