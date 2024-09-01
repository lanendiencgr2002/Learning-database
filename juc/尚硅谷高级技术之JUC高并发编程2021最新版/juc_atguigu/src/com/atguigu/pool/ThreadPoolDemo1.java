package com.atguigu.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//演示线程池三种常用分类
public class ThreadPoolDemo1 {
    public static void main(String[] args) {
        // 创建三种不同类型的线程池
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(5);
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

        // 定义任务数量
        int taskCount = 20;

        // 测试固定大小线程池
        testThreadPool(fixedThreadPool, "固定大小线程池", taskCount);

        // 测试单线程线程池
        testThreadPool(singleThreadExecutor, "单线程线程池", taskCount);

        // 测试可缓存线程池
        testThreadPool(cachedThreadPool, "可缓存线程池", taskCount);

        // 关闭所有线程池
        fixedThreadPool.shutdown();
        singleThreadExecutor.shutdown();
        cachedThreadPool.shutdown();
    }

    private static void testThreadPool(ExecutorService threadPool, String poolName, int taskCount) {
        System.out.println("测试 " + poolName + " 开始");
        long startTime = System.currentTimeMillis();

        try {
            for (int i = 1; i <= taskCount; i++) {
                final int taskId = i;
                threadPool.execute(() -> {
                    System.out.println(poolName + ": " + Thread.currentThread().getName() + " 执行任务 " + taskId);
                    try {
                        Thread.sleep(200); // 模拟任务执行时间
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        threadPool.shutdown();
        while (!threadPool.isTerminated()) {
            // 等待所有任务完成
        }

        long endTime = System.currentTimeMillis();
        System.out.println(poolName + " 执行完毕，耗时：" + (endTime - startTime) + "ms\n");
    }
}
