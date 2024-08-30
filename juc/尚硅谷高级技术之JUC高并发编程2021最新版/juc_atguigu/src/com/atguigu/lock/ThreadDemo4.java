package com.atguigu.lock;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 演示线程安全的集合类
 */
public class ThreadDemo4 {
    public static void main(String[] args) {
        // 演示List
//        demonstrateList();
        // 演示Set
        demonstrateSet();
        // 演示Map
//        demonstrateMap();
    }

    /**
     * 演示List的线程安全问题及解决方案
     */
    private static void demonstrateList() {
        // 1. ArrayList - 线程不安全
         List<String> list = new ArrayList<>();

        // 2. Vector - 线程安全但性能较差 毕竟老
        // List<String> list = new Vector<>();

        // 3. Collections.synchronizedList - 同步包装 也毕竟老
        // List<String> list = Collections.synchronizedList(new ArrayList<>());

        // 4. CopyOnWriteArrayList - 适用于读多写少的场景
//        List<String> list = new CopyOnWriteArrayList<>();

        runThreads(list);
    }

    /**
     * 演示Set的线程安全问题及解决方案
     */
    private static void demonstrateSet() {
        // 1. HashSet - 线程不安全
         Set<String> set = new HashSet<>();

        // 2. CopyOnWriteArraySet - 线程安全
//        Set<String> set = new CopyOnWriteArraySet<>();

        runThreads(set);
    }

    /**
     * 演示Map的线程安全问题及解决方案
     */
    private static void demonstrateMap() {
        // 1. HashMap - 线程不安全
        // Map<String, String> map = new HashMap<>();

        // 2. ConcurrentHashMap - 线程安全
        Map<String, String> map = new ConcurrentHashMap<>();

        for (int i = 0; i < 30; i++) {
            String key = String.valueOf(i);
            new Thread(() -> {
                // 向Map添加内容
                map.put(key, UUID.randomUUID().toString().substring(0, 8));
                // 打印Map内容
                System.out.println(map);
            }, String.valueOf(i)).start();
        }
    }

    /**
     * 通用方法：创建多个线程并向集合添加元素
     */
    private static void runThreads(Collection<String> collection) {
        for (int i = 0; i < 300; i++) {
            new Thread(() -> {
                // 向集合添加内容
                collection.add(UUID.randomUUID().toString().substring(0, 8));
                // 打印集合内容
                System.out.println(collection); // 如果线程不安全的数据结构，可能会没添加进去，就要打印就会报错，因为list.add没加锁
            }, String.valueOf(i)).start();
        }
    }
}
