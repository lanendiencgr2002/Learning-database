from flask import Flask
import time
import threading
import requests

app = Flask(__name__)

@app.route('/api')
def api():
    """模拟耗时API"""
    print(f"开始处理请求: {threading.current_thread().name}")
    time.sleep(3)  # 模拟耗时3秒
    print(f"请求处理完成: {threading.current_thread().name}")
    return {"status": "done"}

def test_concurrent_requests():
    """测试并发请求"""
    def make_request(i):
        start_time = time.time()
        response = requests.get('http://localhost:5000/api')
        end_time = time.time()
        print(f"请求 {i} 耗时: {end_time - start_time:.2f} 秒")

    # 同时发起3个请求
    threads = []
    for i in range(3):
        t = threading.Thread(target=make_request, args=(i,))
        threads.append(t)
        t.start()

    for t in threads:
        t.join()

if __name__ == '__main__':
    # 在一个线程中运行Flask服务器
    server_thread = threading.Thread(target=lambda: app.run(threaded=False))
    server_thread.start()
    
    # 等待服务器启动
    time.sleep(2)
    
    # 测试并发请求
    test_concurrent_requests()