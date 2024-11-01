import requests
import random
import string
import time
from bs4 import BeautifulSoup

# 文件名，用于持久化查询过的字符
持久化文件 = '已查询参数.txt'


# 生成随机两个字母的字符串
def 生成随机字符串():
    return ''.join(random.choices(string.ascii_lowercase, k=2))


# 检查返回的 HTML 是否包含特定字符串
def 检查域名可用性(html):
    soup = BeautifulSoup(html, 'html.parser')
    头部文本 = soup.select_one('.header__text')
    if 头部文本:
        print(头部文本.text)
        return "unavailable" in 头部文本.text
    return False


# 发送请求并解析返回的 HTML
def 发送请求(查询参数):
    url = f''
    print(f"正在发送请求，查询参数为: {查询参数}")
    响应 = requests.get(url, headers=headers)
    return 响应.text


# 读取持久化的查询参数
def 加载已查询参数():
    try:
        with open(持久化文件, 'r') as 文件:
            return set(文件.read().splitlines())
    except FileNotFoundError:
        return set()


# 保存查询参数到文件
def 保存查询参数(查询参数):
    with open(持久化文件, 'a') as 文件:
        文件.write(f"{查询参数}\n")


# 主函数
def 主函数():
    已查询参数 = 加载已查询参数()
    while True:
        查询参数 = 生成随机字符串()

        # 确保查询参数是唯一的
        while 查询参数 in 已查询参数:
            查询参数 = 生成随机字符串()
        已查询参数.add(查询参数)

        # 立即保存查询参数到文件
        保存查询参数(查询参数)

        # 发送请求并检查域名可用性
        html = 发送请求(查询参数)
        if not 检查域名可用性(html):
            print(f"成功的查询参数: {查询参数}")
            break

        # 随机间隔 0 到 1 秒
        休眠时间 = random.uniform(0, 1)
        print(f"休眠 {休眠时间} 秒后进行下一次检查...")
        time.sleep(休眠时间)


if __name__ == "__main__":
    主函数()
