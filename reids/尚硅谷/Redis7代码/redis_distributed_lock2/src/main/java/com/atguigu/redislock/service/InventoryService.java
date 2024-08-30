package com.atguigu.redislock.service;

import cn.hutool.core.util.IdUtil;
import com.atguigu.redislock.mylock.DistributedLockFactory;
import com.atguigu.redislock.mylock.RedisDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.redisson.Redisson;
import org.redisson.api.RLock;

import javax.annotation.Resource;

/**
 * @auther zzyy
 * @create 2023-01-04 15:59
 */
/*
版本演进及问题说明：
V2.0 - 单机版加锁
问题：不适用于分布式环境，无法解决多服务器间的并发问题。
V3.1 - 引入Redis分布式锁，使用递归重试
问题：递归重试可能导致栈溢出（StackOverflowError），且高并发下if判断不够严谨。
V3.2 - 改用自旋方式重试
问题：无过期时间设置，如果服务器宕机，可能导致锁无法释放。
V4.0 - 加锁时设置过期时间，保证原子性
问题：解锁时可能误删其他线程的锁。
V5.0 - 解锁时增加锁的拥有者校验
问题：校验和删除操作非原子性，高并发下可能产生问题。
V6.0 - 使用Lua脚本确保解锁操作的原子性
问题：不支持可重入性，同一个线程无法多次获取同一把锁。
V7.0 - 实现可重入锁
问题：缺少自动续期机制，长时间操作可能导致锁过期。
V8.0 - 添加自动续期功能
问题：实现较为复杂，可能存在边界情况未处理。
V9.1 - 引入Redisson实现RedLock算法 解决单例宕机锁丢失，从机上位，导致两把锁
遗留问题：
1. 性能和可靠性可能还需进一步优化。
2. 缺少对更复杂业务场景（如多商品库存）的支持。
3. 错误处理和异常情况的处理可能不够完善。
4. 缺少详细的日志和监控机制。
5. 分布式环境下的一致性和性能平衡可能需要进一步优化。
已解决的核心问题：
- 实现了分布式环境下的锁机制
- 解决了锁的原子性、可重入性、自动续期等关键问题
- 通过引入成熟的Redisson库，提高了实现的可靠性
*/
@Service
@Slf4j
public class InventoryService
{
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value("${server.port}")
    private String port;
    @Autowired //只有一个工厂，保证只有 一个uuid
    private DistributedLockFactory distributedLockFactory;


    //V9.1,引入Redisson对应的官网推荐RedLock算法实现类
    @Autowired
    private Redisson redisson;
    public String saleByRedisson()
    {
        String retMessage = "";

        RLock redissonLock = redisson.getLock("zzyyRedisLock");
        redissonLock.lock();

        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            //改进点，只能删除属于自己的key，不能删除别人的 一定要加 不然很小可能会报错 但是一定要加
            //redissonLock底层lua脚本已经是原子性的
            if(redissonLock.isLocked() && redissonLock.isHeldByCurrentThread())
            {
                redissonLock.unlock();
            }
        }
        return retMessage+"\t"+"服务端口号"+port;
    }







    //V8.0，实现自动续期功能的完善，后台自定义扫描程序，如果规定时间内没有完成业务逻辑，会调用加钟自动续期的脚本
    public String sale()
    {
        String retMessage = "";

        Lock redisLock = distributedLockFactory.getDistributedLock("redis");
        redisLock.lock();
        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
                //暂停120秒钟线程,故意的，演示自动续期的功能。。。。。。
                try { TimeUnit.SECONDS.sleep(120); } catch (InterruptedException e) { e.printStackTrace(); }
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            redisLock.unlock();
        }
        return retMessage+"\t"+"服务端口号"+port;
    }


    //V7.0版本，如何将我们的lock/unlock+lua脚本自研版的redis分布式锁搞定？
    /*public String sale()
    {
        String retMessage = "";

        Lock redisLock = distributedLockFactory.getDistributedLock("redis");
        redisLock.lock();
        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
                testReEntry();
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            redisLock.unlock();
        }
        return retMessage+"\t"+"服务端口号"+port;
    }

    private void testReEntry()//用在V7.0版本程序作为测试可重入性
    {
        Lock redisLock = distributedLockFactory.getDistributedLock("redis");
        redisLock.lock();
        try
        {
            System.out.println("===========测试可重入锁========");
        }finally {
            redisLock.unlock();
        }
    }
*/

    //V6.0 ，不满足可重入性，需要重新修改为V7.0 已经满足独占性高可用防死锁（加过期时间）不乱抢（原子）
    /*public String sale()
    {
        String retMessage = "";

        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();

        while(!stringRedisTemplate.opsForValue().setIfAbsent(key,uuidValue,30L,TimeUnit.SECONDS))
        {
            //暂停20毫秒，进行递归重试.....
            try { TimeUnit.MILLISECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        //redislock();
        //抢锁成功的请求线程，进行正常的业务逻辑操作，扣减库存
        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
                testReEnter();
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            //unredislock();
            //改进点，修改为Lua脚本的redis分布式锁调用，必须保证原子性，参考官网脚本案例
            String luaScript =
                    "if redis.call('get',KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del',KEYS[1]) " +
                    "else " +
                        "return 0 " +
                    "end";
            stringRedisTemplate.execute(new DefaultRedisScript(luaScript,Boolean.class), Arrays.asList(key),uuidValue);
        }
        return retMessage+"\t"+"服务端口号"+port;
    }

    private void testReEnter()
    {
       *//* String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();

        while(!stringRedisTemplate.opsForValue().setIfAbsent(key,uuidValue,30L,TimeUnit.SECONDS))
        {
            //暂停20毫秒，进行递归重试.....
            try { TimeUnit.MILLISECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        }
        redislock();
        //biz......
        unredislock();
        //改进点，修改为Lua脚本的redis分布式锁调用，必须保证原子性，参考官网脚本案例
        String luaScript =
                "if redis.call('get',KEYS[1]) == ARGV[1] then " +
                        "return redis.call('del',KEYS[1]) " +
                        "else " +
                        "return 0 " +
                        "end";
        stringRedisTemplate.execute(new DefaultRedisScript(luaScript,Boolean.class), Arrays.asList(key),uuidValue);*//*
    }*/


    //V5.0 ,存在问题就是最后的判断+del不是一行原子命令操作，需要用lua脚本进行修改
    /*public String sale()
    {
        String retMessage = "";

        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();

        while(!stringRedisTemplate.opsForValue().setIfAbsent(key,uuidValue,30L,TimeUnit.SECONDS))
        {
            //暂停20毫秒，进行递归重试.....
            try { TimeUnit.MILLISECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        //抢锁成功的请求线程，进行正常的业务逻辑操作，扣减库存
        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            //改进点，只能删除属于自己的key，不能删除别人的
            // v5.0判断加锁与解锁是不是同一个客户端，同一个才行，自己只能删除自己的锁，不误删他人的
            if(stringRedisTemplate.opsForValue().get(key).equalsIgnoreCase(uuidValue))
            {
                stringRedisTemplate.delete(key);
            }
        }
        return retMessage+"\t"+"服务端口号"+port;
    }*/



    /*
    V4.0,存在问题：stringRedisTemplate.delete(key);只能自己删除自己的锁，不可以删除别人的，需要添加判断
    是否是自己的锁来进行操作
    public String sale()
    {
        String retMessage = "";

        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();

        //改进点：加锁和过期时间设置必须同一行，保证原子性
        while(!stringRedisTemplate.opsForValue().setIfAbsent(key,uuidValue,30L,TimeUnit.SECONDS))
        {
            //暂停20毫秒，进行递归重试.....
            try { TimeUnit.MILLISECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        //stringRedisTemplate.expire(key,30L,TimeUnit.SECONDS);

        //抢锁成功的请求线程，进行正常的业务逻辑操作，扣减库存
        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            stringRedisTemplate.delete(key);
        }
        return retMessage+"\t"+"服务端口号"+port;
    }
     */



    /**
     * V3.2，存在的问题
     * 部署了微服务的Java程序机器挂了，代码层面根本没有走到finally这块，
     * 没办法保证解锁(无过期时间该key一直存在)，这个key没有被删除，需要加入一个过期时间限定key
     * @return
     */
    /*public String sale()
    {
        String retMessage = "";

        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();

        //不用递归了，高并发下容易出错，我们用自旋替代递归方法重试调用;也不用if了，用while来替代
        while(!stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue))
        {
            //暂停20毫秒，进行递归重试.....
            try { TimeUnit.MILLISECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
        }

        //抢锁成功的请求线程，进行正常的业务逻辑操作，扣减库存
        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            stringRedisTemplate.delete(key);
        }
        return retMessage+"\t"+"服务端口号"+port;
    }*/


    /*
    V3.1，递归重试，容易导致stackoverflowerror，所以不太推荐；另外，高并发唤醒后推荐用while判断而不是if
    public String sale()
    {
        String retMessage = "";
        String key = "zzyyRedisLock";
        String uuidValue = IdUtil.simpleUUID()+":"+Thread.currentThread().getId();

        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, uuidValue);
        //flag=false,抢不到的线程要继续重试。。。。。。
        if(!flag)
        {
            //暂停20毫秒，进行递归重试.....
            try { TimeUnit.MILLISECONDS.sleep(20); } catch (InterruptedException e) { e.printStackTrace(); }
            sale();
        }else{
            //抢锁成功的请求线程，进行正常的业务逻辑操作，扣减库存
            try
            {
                //1 查询库存信息
                String result = stringRedisTemplate.opsForValue().get("inventory001");
                //2 判断库存是否足够
                Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
                //3 扣减库存，每次减少一个
                if(inventoryNumber > 0)
                {
                    stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                    retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                    System.out.println(retMessage+"\t"+"服务端口号"+port);
                }else{
                    retMessage = "商品卖完了,o(╥﹏╥)o";
                }
            }finally {
                stringRedisTemplate.delete(key);
            }
        }
        return retMessage+"\t"+"服务端口号"+port;
    }*/

    /*V2.0,单机版加锁配合Nginx和Jmeter压测后，不满足高并发分布式锁的性能要求，出现超卖（分布式就不行了）
    private Lock lock = new ReentrantLock();
    public String sale()
    {
        String retMessage = "";

        lock.lock();
        try
        {
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存，每次减少一个
            if(inventoryNumber > 0)
            {
                stringRedisTemplate.opsForValue().set("inventory001",String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品,库存剩余:"+inventoryNumber;
                System.out.println(retMessage+"\t"+"服务端口号"+port);
            }else{
                retMessage = "商品卖完了,o(╥﹏╥)o";
            }
        }finally {
            lock.unlock();
        }
        return retMessage+"\t"+"服务端口号"+port;
    }*/
}
