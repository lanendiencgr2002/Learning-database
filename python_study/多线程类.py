import threading
from concurrent.futures import ThreadPoolExecutor, as_completed
from typing import Callable, List, Any, Optional

class ThreadManager:
    """线程管理器类,提供线程池管理、任务提交、回调等功能"""
    
    def __init__(self, max_workers: Optional[int] = None):
        """
        初始化线程管理器
        :param max_workers: 线程池最大线程数,默认None(自动设置)
        """
        self.pool = ThreadPoolExecutor(max_workers=max_workers)
        self.futures = []  # 存储提交的任务
        self._lock = threading.RLock()  # 可重入锁用于线程安全
        
    def get_current_thread(self) -> threading.Thread:
        """获取当前执行的线程"""
        return threading.current_thread()
        
    def submit(self, fn: Callable, *args, **kwargs) -> Any:
        """
        提交单个任务到线程池
        :param fn: 要执行的函数
        :param args: 位置参数
        :param kwargs: 关键字参数
        :return: Future对象
        """
        with self._lock:
            future = self.pool.submit(fn, *args, **kwargs)
            self.futures.append(future)
            return future
            
    def submit_with_callback(self, fn: Callable, callback: Callable, *args, **kwargs) -> Any:
        """
        提交任务并设置回调函数
        :param fn: 要执行的函数
        :param callback: 回调函数
        :param args: 位置参数
        :param kwargs: 关键字参数
        :return: Future对象
        """
        future = self.submit(fn, *args, **kwargs)
        future.add_done_callback(callback)
        return future
        
    def map(self, fn: Callable, *iterables, timeout: Optional[float] = None) -> List[Any]:
        """
        将函数映射到一组可迭代对象
        :param fn: 要执行的函数
        :param iterables: 可迭代对象
        :param timeout: 超时时间
        :return: 结果列表
        """
        return list(self.pool.map(fn, *iterables, timeout=timeout))
        
    def wait_all(self) -> List[Any]:
        """
        等待所有任务完成并获取结果
        :return: 所有任务的结果列表
        """
        results = []
        for future in as_completed(self.futures):
            results.append(future.result())
        return results
        
    def submit_batch(self, fn: Callable, args_list: List[tuple]) -> List[Any]:
        """
        批量提交任务
        :param fn: 要执行的函数
        :param args_list: 参数列表,每个元素是一个参数元组
        :return: Future对象列表
        """
        futures = []
        for args in args_list:
            future = self.submit(fn, *args)
            futures.append(future)
        return futures
        
    def shutdown(self, wait: bool = True):
        """
        关闭线程池
        :param wait: 是否等待所有线程完成
        """
        self.pool.shutdown(wait=wait)
        
    def __enter__(self):
        return self
        
    def __exit__(self, exc_type, exc_val, exc_tb):
        self.shutdown()

if __name__ == '__main__':
    pass