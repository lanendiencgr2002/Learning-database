package cn.lili.trigger.plugin.delay;

import cn.hutool.json.JSONUtil;
import cn.lili.trigger.plugin.interfaces.TimeTrigger;
import cn.lili.trigger.plugin.interfaces.TimeTriggerExecutor;
import cn.lili.trigger.plugin.model.TimeTriggerMsg;
import cn.lili.trigger.plugin.util.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 测试延时队列
 *
 * @author paulG
 * @version v4.1
 * @date 2020/11/17 7:19 下午
 * @description
 * @since 1
 */
@Component
public class TestDelayQueue extends AbstractDelayQueueMachineFactory {

    @Autowired
    private TimeTrigger timeTrigger;

    @Override
    public void invoke(String jobId) {
        TimeTriggerMsg timeTriggerMsg = JSONUtil.toBean(jobId, TimeTriggerMsg.class);

        TimeTriggerExecutor executor = (TimeTriggerExecutor) SpringContextUtil.getBean(timeTriggerMsg.getTriggerExecutor());
        executor.execute(timeTriggerMsg.getParam());

    }

    @Override
    public String setDelayQueueName() {
        return "test_delay";
    }
}
