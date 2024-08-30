## Lilishop 技术栈

##### 官方公众号 & 开源不易，如有帮助请点Star
![image-20210511171611793](https://pickmall.cn/assets/imgs/h5-qrcode.png)




### 介绍

**官网**：https://pickmall.cn

Lilishop 是一款Java开发，基于SpringBoot研发的B2B2C多用户商城，前端使用 Vue、uniapp开发 **系统全端全部代码开源**

本系统用于教大家如何运用系统中的每一个细节，如：支付、第三方登录、日志收集、分布式事务、秒杀场景等各个场景学习方案



###  git地址 https://gitee.com/beijing_hongye_huicheng/lilishop-spring-learning



## 本文学习 分布式延时任务 



#### 延时任务介绍

即指定一个时间，执行提前约定好的任务，例如：定时取消订单，定时上下架商品，定时开启活动等。



#### 延时任务与定时任务的区别

延时任务适用于个性化的业务场景，比如某订单自动取消，某活动自动开启，某商品自动上下架子。还有一个就是较为精确的，需要实时的事情。

而定时任务适用于全平台的业务，比如计算商品评分统一结算，分销中的可提现金额批量结算，平台统计/店铺统计数据生成等。总的来说就是定时扫描，每天，每小时，每分钟，每个月，不管怎么样都要执行。比如定时上下架，用定时任务也可以，但是要实现精确的任务调度，创建一个每秒任务，是不太理智的。

两个场景需要互补，具体应用什么场景，可以再自己斟酌斟酌。



#### 思路介绍

1. 项目启动时启用一个线程，线程用于间隔一定时间去查询redis的待执行任务。其任务id为对象json格式化之后的字符串，值为要执行的时间。
2. 查询到执行的任务时，将其从redis的信息中进行删除。（删除成功才执行延时任务，否则不执行，这样可以避免分布式系统延时任务多次执行。）
3. 删除redis中的记录之后，启用子线程执行任务。将执行id，也就是json的字符串翻转回要执行的任务信息，这样可以得到用什么执行器去执行任务，参数有哪些。
4. 执行延时任务



#### 实际使用

实际场景中，还会设计延时任务修改，删除等，这些场景建议在执行任务创建时，redis标记要执行的任务，如果删除或者修改任务时，修改redis中的标识即可，当然也可以在业务逻辑中做补充的条件判定，都可以。

另外具体执行任务建议使用mq去实现，相当于在执行任务时，线程只是发布一个mq，交给消费者去消费具体的事情。

代码中的进程扫描5秒，也就代表一个延时任务最多延迟5秒去执行，实战场景中可以调整至1秒，或者更低，但是不太建议。另外redis的性能杠杠的，不用太担心redis的连接数导致性能问题。



#### 使用步骤

1. 启用redis，可以本地启动，也可以用ELK中docker-compose启动。

2. 启动springboot应用。

3. 请求springboot 应用   http://127.0.0.1:8080 

4. 查看控制台输出内容

   > 2021-06-09 12:41:33.168  INFO 40730 --- [nio-8888-exec-1] l.t.p.d.AbstractDelayQueueMachineFactory : 增加延时任务, 缓存key test_delay, 等待时间 10
   > 2021-06-09 12:41:33.168  INFO 40730 --- [nio-8888-exec-1] c.l.t.p.i.impl.RedisTimerTrigger         : 定时执行在【2021-06-09 12:41:43】，消费【test params】
   > 2021-06-09 12:41:44.399  INFO 40730 --- [       Thread-5] l.t.p.d.AbstractDelayQueueMachineFactory : 延时任务开始执行任务:[{"score":1.623213703E9,"value":"{\"triggerTime\":1623213703,\"triggerExecutor\":\"testTimeTriggerExecutor\",\"param\":\"test params\"}"}]
   > 2021-06-09 12:41:44.403  INFO 40730 --- [pool-2-thread-2] c.l.t.p.i.e.TestTimeTriggerExecutor      : 执行器具执行任务test params





### 关键类介绍

##### 缓存操作类 用于延时任务的核型逻辑，间隔查询需要执行的延时任务，考的就是redis的Sorted Set属性来试下排序，执行任务。

```java
/**
 * 向Zset里添加成员
 *
 * @param key   key值
 * @param score 分数，通常用于排序
 * @param value 值
 * @return 增加状态
 */
@Override
public boolean zAdd(String key, long score, String value) {
    Boolean result = redisTemplate.opsForZSet().add(key, value, score);
    return result;

}


/**
 * 获取 某key 下 某一分值区间的队列
 *
 * @param key  缓存key
 * @param from 开始时间
 * @param to   结束时间
 * @return 数据
 */
@Override
public Set<ZSetOperations.TypedTuple<Object>> zRangeByScore(String key, int from, long to) {
    Set<ZSetOperations.TypedTuple<Object>> set = redisTemplate.opsForZSet().rangeByScoreWithScores(key, from, to);
    return set;
}

/**
 * 移除 Zset队列值
 *
 * @param key   key值
 * @param value 删除的集合
 * @return 删除数量
 */
@Override
public Long zRemove(String key, String... value) {
    return redisTemplate.opsForZSet().remove(key, value);
}
```

##### 延时队列 抽象类，具体延时队列需继承

```java
package cn.lili.trigger.plugin.delay;

import cn.hutool.json.JSONUtil;
import cn.lili.trigger.plugin.cache.Cache;
import cn.lili.trigger.plugin.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 延时队列工厂
 *
 * @author paulG
 * @since 2020/11/7
 **/
@Slf4j
public abstract class AbstractDelayQueueMachineFactory {

    @Autowired
    private Cache cache;

    /**
     * 插入任务id
     *
     * @param jobId 任务id(队列内唯一)
     * @param time  延时时间(单位 :秒)
     * @return 是否插入成功
     */
    public boolean addJob(String jobId, Integer time) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.SECOND, time);
        long delaySeconds = instance.getTimeInMillis() / 1000;
        boolean result = cache.zAdd(setDelayQueueName(), delaySeconds, jobId);
        log.info("增加延时任务, 缓存key {}, 等待时间 {}", setDelayQueueName(), time);
        return result;

    }

    /**
     * 延时队列机器开始运作
     */
    private void startDelayQueueMachine() {
        log.info("延时队列机器{}开始运作", setDelayQueueName());

        // 监听redis队列
        while (true) {
            try {
                // 获取当前时间的时间戳
                long now = System.currentTimeMillis() / 1000;
                // 获取当前时间前的任务列表
                Set<DefaultTypedTuple> tuples = cache.zRangeByScore(setDelayQueueName(), 0, now);

                // 如果任务不为空
                if (!CollectionUtils.isEmpty(tuples)) {
                    log.info("延时任务开始执行任务:{}", JSONUtil.toJsonStr(tuples));

                    for (DefaultTypedTuple tuple : tuples) {
                        String jobId = (String) tuple.getValue();
                        // 移除缓存，如果移除成功则表示当前线程处理了延时任务，则执行延时任务
                        Long num = cache.zRemove(setDelayQueueName(), jobId);
                        // 如果移除成功, 则执行
                        if (num > 0) {
                            ThreadPoolUtil.execute(() -> invoke(jobId));
                        }
                    }
                }

            } catch (Exception e) {
                log.error("处理延时任务发生异常,异常原因为{}", e.getMessage(), e);
            } finally {
                // 间隔5秒钟搞一次
                try {
                    TimeUnit.SECONDS.sleep(5L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    /**
     * 最终执行的任务方法
     *
     * @param jobId 任务id
     */
    public abstract void invoke(String jobId);


    /**
     * 要实现延时队列的名字
     */
    public abstract String setDelayQueueName();


    @PostConstruct
    public void init() {
        new Thread(this::startDelayQueueMachine).start();
    }

}
```

##### 延时队列示例实现

```java
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
```

##### 延时任务接口

```java
package cn.lili.trigger.plugin.interfaces;


import cn.lili.trigger.plugin.model.TimeTriggerMsg;

/**
 * 延时执行接口
 *
 * @author Chopper
 */
public interface TimeTrigger {


    /**
     * 添加延时任务
     *
     * @param timeTriggerMsg 延时任务信息
     */
    void add(TimeTriggerMsg timeTriggerMsg);

}
```

##### Redis延时任务实现类

```java
package cn.lili.trigger.plugin.interfaces.impl;

import cn.hutool.json.JSONUtil;
import cn.lili.trigger.plugin.delay.TestDelayQueue;
import cn.lili.trigger.plugin.interfaces.TimeTrigger;
import cn.lili.trigger.plugin.model.TimeTriggerMsg;
import cn.lili.trigger.plugin.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * redis 延时任务
 *
 * @author Chopper
 * @version v1.0
 * 2021-06-09 11:00
 */
@Component
@Slf4j
public class RedisTimerTrigger implements TimeTrigger {

    @Autowired
    private TestDelayQueue testDelayQueue;

    @Override
    public void add(TimeTriggerMsg timeTriggerMsg) {
        //计算延迟时间 执行时间-当前时间
        Integer delaySecond = Math.toIntExact(timeTriggerMsg.getTriggerTime() - DateUtil.getDateline());
        //设置延时任务
        if (Boolean.TRUE.equals(testDelayQueue.addJob(JSONUtil.toJsonStr(timeTriggerMsg), delaySecond))) {
            log.info("定时执行在【" + DateUtil.toString(timeTriggerMsg.getTriggerTime(), "yyyy-MM-dd HH:mm:ss") + "】，消费【" + timeTriggerMsg.getParam().toString() + "】");
        } else {
            log.error("延时任务添加失败:{}", timeTriggerMsg);
        }
    }
}
```

##### 延时任务执行器接口

```java
package cn.lili.trigger.plugin.interfaces;

/**
 * 延时任务执行器接口
 *
 * @author Chopper
 */
public interface TimeTriggerExecutor {


    /**
     * 执行任务
     *
     * @param object 任务参数
     */
    void execute(Object object);

}
```

##### 延时任务实现

```java
package cn.lili.trigger.plugin.interfaces.execute;

import cn.lili.trigger.plugin.interfaces.TimeTriggerExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * test执行器
 *
 * @author Chopper
 * @version v1.0
 * 2021-06-09 10:49
 */
@Component
@Slf4j
public class TestTimeTriggerExecutor implements TimeTriggerExecutor {

    @Override
    public void execute(Object object) {
        log.info("执行器具执行任务{}", object);
    }
}
```

##### 延时任务消5息模型

```java
package cn.lili.trigger.plugin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 延时任务消息
 *
 * @author Chopper
 * @version v1.0
 * @since 2019-02-12 下午5:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeTriggerMsg implements Serializable {


    private static final long serialVersionUID = 8897917127201859535L;

    /**
     * 执行器 执行时间
     */
    private Long triggerTime;
    /**
     * 执行器beanId
     */
    private String triggerExecutor;


    /**
     * 执行器参数
     */
    private Object param;


}
```

##### 控制器

```java
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
```