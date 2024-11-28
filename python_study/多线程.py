'''
进程和线程：
线程，是计算机中可以被cpu调度的最小单元（真正在工作）  适合io密集 适合单核
进程，是计算机资源分配的最小单元（进程为线程提供资源）  适合计算密集 适合多核
一个进程中可以有多个线程，同一个进程中的线程可以共享此进程中的资源。
'''

'''
GIL：全局解释器锁
python中一个进程中只有一个线程在执行，无法利用多核cpu
'''

'''
列表是大部分是线程安全的，不安全的也有，看官方文档
Lock不支持嵌套，RLock支持嵌套（可重入锁，锁多次解多次）
'''

'''
线程池：线程不是开的越多越好，开的多了可能会导致系统的性能更低。
建议用线程池来管理线程，线程池中的线程数可以根据系统的性能来动态调整。
'''

import threading
from concurrent.futures import ThreadPoolExecutor, as_completed
import time
import random

def 获取当前执行的线程():
    print(f'当前线程：{threading.current_thread()}')

def 线程的函数(n):
    time.sleep(1)
    print(f'{n} is running')
    return random.randint(1, 10)

class 线程类(threading.Thread):
    def run(self):
        print(f'{self.name} is running')
        参数1,参数2 = self._args
        print(f'参数1：{参数1}，参数2：{参数2}')

def 线程池回调函数(future):
    print(f'线程池回调函数：{future.result()}')

def 线程池最终统一获取结果():
    任务列表=[]
    pool = ThreadPoolExecutor(50) # 创建一个线程池，最大线程数为10
    for i in range(100):
        future = pool.submit(线程的函数, i) # 系统会自动分配线程来执行线程的函数
        任务列表.append(future)
    pool.shutdown(wait=True) # 等待所有线程执行完毕
    print('线程池执行完毕')
    for future in 任务列表:
        print(future.result()) # 获取线程的返回值
def 线程池回调测试():
    pool = ThreadPoolExecutor(10) # 创建一个线程池，最大线程数为10
    for i in range(100):
        future = pool.submit(线程的函数, i) # 系统会自动分配线程来执行线程的函数
        future.add_done_callback(线程池回调函数) # 回调函数会在线程执行完毕后执行
        # print(future.result()) # 获取线程的返回值 这个会阻塞直到线程执行完毕得到返回值
    pool.shutdown(wait=True) # 等待所有线程执行完毕   pool.shutdown是关闭线程池的意思
    print('线程池执行完毕')

def 线程池简单测试():
    pool = ThreadPoolExecutor(10) # 创建一个线程池，最大线程数为10
    for i in range(100):
        pool.submit(线程的函数, i) # 系统会自动分配线程来执行线程的函数
    pool.shutdown(wait=True) # 等待所有线程执行完毕
    print('线程池执行完毕')
    # with ThreadPoolExecutor(max_workers=5) as pool:
    #     pool.submit(线程的函数, 1)
    #     pool.submit(线程的函数, 2)
    #     pool.submit(线程的函数, 3)


def 线程锁测试():
    lock = threading.RLock() # 申请锁，没申请到就在这阻塞
    lock.acquire()
    print('线程锁')
    lock.release() # 释放锁，释放后，其他线程才能申请到锁
    # 也可以使用with lock: 来申请锁，释放锁
    # with lock:
    #     代码块

def 线程类测试():
    t1 = 线程类(args=(1,2))
    t1.start()

def 线程简单案例():
    t1 = threading.Thread(target=线程的函数, args=(1,))
    t1.setName('线程1') # 设置线程名称
    # t1.setDaemon(True) # 守护线程 主线程结束，守护线程也会结束
    # t1.setDaemon(False) # 非守护线程 主线程结束，非守护线程还会继续执行
    t1.start() # 当前线程准备就绪（等待CPU调度，具体时间是由CPU来决定）
    # t1.join() # 等待当前线程的任务执行完毕(t1)后再向下继续执行。 放start后面

def 线程等待案例():
    任务列表 = []
    pool = ThreadPoolExecutor(5)  # 创建一个只有5个线程的线程池
    
    # 提交10个任务
    for i in range(10):
        future = pool.submit(线程的函数, i)
        任务列表.append(future)
    
    # 方法1：使用as_completed()等待 - 谁先完成先处理谁的结果
    for future in as_completed(任务列表):
        print(f'任务完成，结果：{future.result()}')
    
def 简单动态任务分配_方法1():
    待处理数据 = list(range(20))
    with ThreadPoolExecutor(max_workers=3) as pool:
        # map会自动管理线程池，维持3个活动线程
        for result in pool.map(线程的函数, 待处理数据):
            print(f'任务完成，结果：{result}')

def 简单动态任务分配_方法2():
    待处理数据 = list(range(20))
    with ThreadPoolExecutor(max_workers=3) as pool:
        futures = [pool.submit(线程的函数, i) for i in 待处理数据]
        for future in as_completed(futures):
            print(f'任务完成，结果：{future.result()}')

if __name__ == '__main__':
    # 线程类测试()
    # 获取当前执行的线程()
    # 线程池简单测试()
    # 线程池回调测试()
    线程池最终统一获取结果()
    # 线程等待案例()
    简单动态任务分配_方法1()
    简单动态任务分配_方法2()
