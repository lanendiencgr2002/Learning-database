from fastapi import FastAPI
import asyncio
import time
import aiohttp
import uvicorn
from threading import Thread

app = FastAPI()

@app.get('/api')
async def api():
    """模拟耗时API"""
    print(f"开始处理请求")
    await asyncio.sleep(3)  # 异步等待3秒
    print(f"请求处理完成")
    return {"status": "done"}

async def make_request(i):
    """发起单个异步请求"""
    start_time = time.time()
    async with aiohttp.ClientSession() as session:
        async with session.get('http://localhost:5000/api') as response:
            await response.json()
    end_time = time.time()
    print(f"请求 {i} 耗时: {end_time - start_time:.2f} 秒")

async def test_concurrent_requests():
    """测试并发请求"""
    # 同时发起3个异步请求
    tasks = [make_request(i) for i in range(3)]
    await asyncio.gather(*tasks)

def run_server():
    """在单独的线程中运行服务器"""
    uvicorn.run(app, host="localhost", port=5000)

if __name__ == '__main__':
    # 启动服务器线程
    server_thread = Thread(target=run_server)
    server_thread.start()
    
    # 等待服务器启动
    time.sleep(2)
    
    # 运行异步测试
    asyncio.run(test_concurrent_requests())