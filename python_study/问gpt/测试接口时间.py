import cxm_us_kg免费的
import chatanywhere要钱的
import eaiapi免费的
import llmapi免费的

import time
from concurrent.futures import ThreadPoolExecutor



def 测试单个接口不是异步版(api模块, 问题="你好"):
    开始 = time.time()
    try:
        回答 = api模块.问gpt问题(问题)
        用时 = time.time() - 开始
        return 回答
        return {
            "接口": api模块.__name__,
            "状态": "成功",
            "用时": round(用时, 2),
            "回答长度": len(回答) if 回答 else 0
        }
    except Exception as e:
        return f"失败: {str(e)}"
        return {
            "接口": api模块.__name__,
            "状态": f"失败: {str(e)}",
            "用时": -1
        }

def 测试所有接口():
    所有模块 = [cxm_us_kg免费的, chatanywhere要钱的, eaiapi免费的, llmapi免费的]
    结果列表 = []
    
    with ThreadPoolExecutor(max_workers=4) as executor:
        任务列表 = [executor.submit(测试单个接口, 模块) for 模块 in 所有模块]
        for 任务 in 任务列表:
            结果列表.append(任务.result())
    
    return 结果列表

def 显示测试结果(结果列表):
    print("\n接口测试结果:")
    print("-" * 50)
    for 结果 in 结果列表:
        print(f"接口: {结果['接口']}")
        print(f"状态: {结果['状态']}")
        print(f"用时: {结果['用时']}秒")
        if '回答长度' in 结果:
            print(f"回答长度: {结果['回答长度']}字符")
        print("-" * 50)

def 测试所有接口时间():
    开始 = time.time()
    结果 = 测试所有接口()
    用时 = time.time() - 开始
    显示测试结果(结果)  
    print(f"总用时: {用时:.2f}秒")


if __name__ == '__main__':
    测试所有接口时间()