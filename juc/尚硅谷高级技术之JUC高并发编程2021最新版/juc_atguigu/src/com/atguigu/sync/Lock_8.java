package com.atguigu.sync;

import java.util.concurrent.TimeUnit;

class Phone {

    public static synchronized void sendSMS() throws Exception {
        //停留4秒
        TimeUnit.SECONDS.sleep(4);
        System.out.println("------sendSMS");
    }

    public synchronized void sendEmail() throws Exception {
        System.out.println("------sendEmail");
    }

    public void getHello() {
        System.out.println("------getHello");
    }
}

/**
 * @Description: 8锁
 *
1 标准访问，先打印短信还是邮件
synchronized sendSMS 会把对象phone锁住，不让访问synchronized sendEmail方法
------sendSMS
------sendEmail

2 停4秒在短信方法内，先打印短信还是邮件
synchronized sendSMS 会把对象phone锁住，不让访问synchronized sendEmail方法
------sendSMS
------sendEmail

3 新增普通的hello方法，是先打短信还是hello
锁住了phone对象后，getHello没加锁，可以访问，但是sendSMS要等4秒，后访问
------getHello
------sendSMS

4 现在有两部手机，先打印短信还是邮件
锁的只是当前对象，另一个对象会先执行sendEmail
------sendEmail
------sendSMS

5 两个静态同步方法，1部手机，先打印短信还是邮件
锁的是字节码对象class，不只是单纯的phone，所有phone对象都会被锁，两个对象也没用
------sendSMS
------sendEmail

6 两个静态同步方法，2部手机，先打印短信还是邮件
锁的是字节码对象class，不只是单纯的phone，所有phone对象都会被锁，两个对象也没用
------sendSMS
------sendEmail

7 1个静态同步方法,1个普通同步方法，1部手机，先打印短信还是邮件
------sendEmail
------sendSMS

8 1个静态同步方法,1个普通同步方法，2部手机，先打印短信还是邮件
------sendEmail
------sendSMS

 */

public class Lock_8 {
    public static void main(String[] args) throws Exception {

        Phone phone = new Phone();
        Phone phone2 = new Phone();

        new Thread(() -> {
            try {
                phone.sendSMS();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "AA").start();

        Thread.sleep(100); // 主线程暂停执行100毫秒

        new Thread(() -> {
            try {
               // phone.sendEmail();
               // phone.getHello();
                phone2.sendEmail();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "BB").start();
    }
}
