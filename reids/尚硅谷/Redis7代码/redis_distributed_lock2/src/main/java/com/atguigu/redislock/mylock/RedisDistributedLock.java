package com.atguigu.redislock.mylock;

import cn.hutool.core.util.IdUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @auther zzyy
 * @create 2023-01-09 16:41
 * 我们自研的redis分布式锁，实现了Lock接口
 */
/*
加锁逻辑：
1. 使用Lua脚本实现原子操作
2. 检查锁是否存在或当前线程是否已持有锁
3. 增加锁计数并设置过期时间
4. 如果获取锁失败，进行自旋重试
5. 获取锁成功后，启动自动续期机制
解锁逻辑：
1. 使用Lua脚本实现原子操作
2. 检查锁是否存在且为当前线程所持有
3. 减少锁计数，如果计数为0则删除锁
4. 如果锁不存在或不属于当前线程，抛出异常
自动续期：
1. 使用定时器定期检查锁的剩余时间
2. 如果锁仍然存在且属于当前线程，则延长过期时间
3. 续期操作在锁过期时间的1/3处进行
*/
//@Component 引入DistributedLockFactory工厂模式，从工厂获得即可
public class RedisDistributedLock implements Lock
{
    private StringRedisTemplate stringRedisTemplate;

    private String lockName;//KEYS[1]
    private String uuidValue;//ARGV[1]
    private long   expireTime;//ARGV[2]

    /*public RedisDistributedLock(StringRedisTemplate stringRedisTemplate, String lockName)
    {
        this.stringRedisTemplate = stringRedisTemplate;
        this.lockName = lockName;
        this.uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId(); //UUID应该与线程关联，而不是与锁对象实例关联。 因为可重入锁 要上锁n次 解锁n次 n次创建锁对象会导致这里uuid不同
        this.expireTime = 25L;
    }*/

    public RedisDistributedLock(StringRedisTemplate stringRedisTemplate, String lockName, String uuid)
    {
        this.stringRedisTemplate = stringRedisTemplate;
        this.lockName = lockName;
        this.uuidValue = uuid+":"+Thread.currentThread().getId();
        this.expireTime = 30L;
    }

    @Override
    public void lock()
    {
        tryLock();
    }
    @Override
    public boolean tryLock()
    {
        try {tryLock(-1L,TimeUnit.SECONDS);} catch (InterruptedException e) {e.printStackTrace();}
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException
    {
        if(time == -1L)
        {
            String script =
                    "if redis.call('exists',KEYS[1]) == 0 or redis.call('hexists',KEYS[1],ARGV[1]) == 1 then    " +
                            "redis.call('hincrby',KEYS[1],ARGV[1],1)    " +
                            "redis.call('expire',KEYS[1],ARGV[2])    " +
                            "return 1  " +
                    "else   " +
                            "return 0 " +
                    "end";
            System.out.println("lockName:"+lockName+"\t"+"uuidValue:"+uuidValue);

            while(!stringRedisTemplate.execute(new DefaultRedisScript<>(script,Boolean.class), Arrays.asList(lockName), uuidValue,String.valueOf(expireTime)))
            {
                //暂停60毫秒
                try { TimeUnit.MILLISECONDS.sleep(60); } catch (InterruptedException e) { e.printStackTrace(); }
            }
            //新建一个后台扫描程序，来坚持key目前的ttl，是否到我们规定的1/2 1/3来实现续期
            renewExpire();
            return true;
        }
        return false;
    }


    @Override
    public void unlock()
    {
        System.out.println("unlock(): lockName:"+lockName+"\t"+"uuidValue:"+uuidValue);
        String script =
                "if redis.call('HEXISTS',KEYS[1],ARGV[1]) == 0 then    " +
                        "return nil  " +
                "elseif redis.call('HINCRBY',KEYS[1],ARGV[1],-1) == 0 then    " +
                        "return redis.call('del',KEYS[1])  " +
                "else    " +
                        "return 0 " +
                "end";
        // nil = false 1 = true 0 = false Lua 脚本可能返回多种类型的值 所以用long
        Long flag = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList(lockName), uuidValue, String.valueOf(expireTime));

        if(null == flag)
        {
            throw new RuntimeException("this lock doesn't exists，o(╥﹏╥)o");
        }
    }

    private void renewExpire()
    {
        String script =
                "if redis.call('HEXISTS',KEYS[1],ARGV[1]) == 1 then     " +
                        "return redis.call('expire',KEYS[1],ARGV[2]) " +
                "else     " +
                        "return 0 " +
                "end";

        new Timer().schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                if (stringRedisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Arrays.asList(lockName), uuidValue, String.valueOf(expireTime)))
                {
                    renewExpire();
                }
            }
        },(this.expireTime * 1000)/3);  // 过三分之一的时间后执行一次递归
    }



    //====下面两个暂时用不到，不再重写
    //====下面两个暂时用不到，不再重写
    //====下面两个暂时用不到，不再重写
    @Override
    public void lockInterruptibly() throws InterruptedException
    {

    }
    @Override
    public Condition newCondition()
    {
        return null;
    }
}
