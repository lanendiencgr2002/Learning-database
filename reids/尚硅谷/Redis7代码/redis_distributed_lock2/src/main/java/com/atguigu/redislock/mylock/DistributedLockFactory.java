package com.atguigu.redislock.mylock;

import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

/**
 * @auther zzyy
 * @create 2023-01-09 17:28
 */
@Component
public class DistributedLockFactory
{
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private String lockName;
    private String uuid;
    // 把uuid放工厂方法里，这样创建每个redis锁的uuid也是一样的，因为只会创建一个工厂（redis），多个redis锁
    public DistributedLockFactory()
    {
        this.uuid = IdUtil.simpleUUID();
    } // 保证是同一个uuid

    public Lock getDistributedLock(String lockType)
    {
        if(lockType == null) return null;

        if(lockType.equalsIgnoreCase("REDIS")){
            this.lockName = "zzyyRedisLock";
            return new RedisDistributedLock(stringRedisTemplate,lockName,uuid);
        }else if(lockType.equalsIgnoreCase("ZOOKEEPER")){
            this.lockName = "zzyyZookeeperLockNode";
            //TODO zookeeper版本的分布式锁
            return null;
        }else if(lockType.equalsIgnoreCase("MYSQL")){
            //TODO MYSQL版本的分布式锁
            return null;
        }

        return null;
    }
}
