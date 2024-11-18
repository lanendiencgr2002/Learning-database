import asyncio
import aiohttp
from typing import List

"""
核心概念：
- 是什么：Python异步编程是一种非阻塞的编程方式
- 为什么：提高程序处理I/O操作的效率
- 应用于：网络请求、文件操作等耗时操作
"""

# 1. 基础异步函数
async def simple_task(task_id: int) -> str:
    """简单的异步任务"""
    print(f"任务 {task_id} 开始")
    await asyncio.sleep(1)  # 模拟耗时操作
    print(f"任务 {task_id} 完成")
    return f"任务 {task_id} 的结果"

# 2. 异步网络请求
async def fetch_data(url: str) -> dict:
    """发起异步HTTP请求"""
    async with aiohttp.ClientSession() as session:  #async with， session最后会自动关闭
        async with session.get(url) as response:
            return await response.json()

# 3. 并发执行多个任务
async def run_tasks(urls: List[str]):
    """并发执行多个网络请求"""
    tasks = [fetch_data(url) for url in urls]
    results = await asyncio.gather(*tasks)
    return results

async def main():
    # 1. 运行简单任务
    print("\n=== 简单任务示例 ===")
    tasks = [simple_task(i) for i in range(3)]
    await asyncio.gather(*tasks)
    
    # 2. 运行网络请求
    print("\n=== 网络请求示例 ===")
    test_urls = [
        "https://api.github.com/users/python",
        "https://api.github.com/users/pallets"
    ]
    results = await run_tasks(test_urls)
    print(f"获取到 {len(results)} 个结果")

if __name__ == "__main__":
    """
    ✅ 推荐做法：
    - 使用异步上下文管理器
    - 适当控制并发数量
    
    ❌ 常见错误：
    - 在异步函数中使用同步操作
    - 忘记使用await关键字
    
    💡 优化建议：
    - 合理使用异常处理
    - 避免过多并发任务
    """
    asyncio.run(main()) 