package com.atguigu.lock;

import java.util.concurrent.locks.ReentrantLock;

class LTicket {
    private int number = 30;
    private final ReentrantLock fairLock = new ReentrantLock(true);
    private final ReentrantLock unfairLock = new ReentrantLock(false);

    public void sale(boolean fair) {
        ReentrantLock lock = fair ? fairLock : unfairLock;
        lock.lock();
        try {
            if(number > 0) {
                System.out.println(Thread.currentThread().getName() +
                        (fair ? " (公平锁)" : " (非公平锁)") +
                        " ：卖出" + (number--) + " 剩余：" + number);
            }
        } finally {
            lock.unlock();
        }
    }
}

public class LSaleTicket {
    public static void main(String[] args) {
        LTicket ticket = new LTicket();

        Runnable task = () -> {
            for (int i = 0; i < 40; i++) {
                ticket.sale(i % 2 == 0); // 偶数次使用公平锁，奇数次使用非公平锁
            }
        };

        new Thread(task, "AA").start();
        new Thread(task, "BB").start();
        new Thread(task, "CC").start();
    }
}
