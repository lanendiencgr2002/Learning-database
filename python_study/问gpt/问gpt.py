import requests
import json
import time

# 只用配这两个
bearer = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJNZW1iZXJJZCI6MTYzODE1NTQyMzMwMjksIkFjY291bnRUeXBlIjoxLCJOaWNrTmFtZSI6ImxhbmVuZGllbmNnciIsIkFjY291bnQiOiIxMzEwNjkwOTA4MiIsIkxvZ2luTW9kZSI6MSwiaWF0IjoxNzI2OTI5OTU5LCJuYmYiOjE3MjY5Mjk5NTksImV4cCI6MTcyODEzOTU1OSwiaXNzIjoiQUlUb29scyIsImF1ZCI6IkFJVG9vbHMifQ.laAcKnWCd6woAnQXrLm2KJHzO66x8g6a8DFu_5RCjLc'
topicid = 17539056428741
提示词 = '''你是一个智能助手，能够回答各种问题并提供帮助。请遵循以下规则：
1. 保持回答简洁明了。
2. 如果不确定答案，请诚实地说"我不确定"。
3. 避免使用冒犯性或不适当的语言。
4. 如果被问到个人信息或敏感话题，请礼貌地拒绝回答。
5. 尽量提供有用和相关的信息。'''
# 提示词 = '你必须严格遵守以下规则：只能用一个字回答问题，回答必须是"是"或"否"。不允许有任何其他解释或额外信息或者标点符号。'
def 计时(f):
    def wrapper(*args, **kwargs):
        start = time.time()
        result = f(*args, **kwargs)
        print(f'{f.__name__} 耗时：{time.time()-start:.4f}秒')
        return result
    return wrapper
@计时
def 问gpt(问题,提示词=提示词):
    第一个网址 = "https://bot.tgmeng.com/adminapi/chatapi/chat/message"
    headers = {
        "Authorization": f"Bearer {bearer}",
        "Content-Type": "application/json",
        "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Mobile Safari/537.36"
    }
    
    payload = {
        "topicId": topicid,
        "contentFiles":[],
        "messages": [
            {
                "content": 提示词,
                "role": "system"  # 将角色改为 "system"
            }
        ],
        "content": 问题
    }
    
    请求第一个网址的响应 = requests.post(第一个网址, json=payload, headers=headers)
    if 请求第一个网址的响应.status_code != 200:
        return f"错误: {请求第一个网址的响应.status_code}, {请求第一个网址的响应.text}"
    
    一坨数字 = 请求第一个网址的响应.json()['result'][1]
    第二个网址 = f'https://bot.tgmeng.com/adminapi/chatapi/chat/message/{一坨数字}'
    headers.update({
        "accept": "*/*",
        "accept-language": "zh-CN",
        "content-length": "0",
        "origin": "https://bot.tgmeng.com",
        "priority": "u=1, i",
        "referer": f"https://bot.tgmeng.com/chat/{topicid}",
        "sec-ch-ua": '"Chromium";v="128", "Not;A=Brand";v="24", "Google Chrome";v="128"',
        "sec-ch-ua-mobile": "?0",
        "sec-ch-ua-platform": '"Windows"',
        "sec-fetch-dest": "empty",
        "sec-fetch-mode": "cors",
        "sec-fetch-site": "same-origin"
    })
    
    gpt回答 = requests.post(第二个网址, headers=headers)
    if gpt回答.status_code != 200:
        return f"错误: {gpt回答.status_code}, {gpt回答.text}"
    
    # 尝试解码响应文本
    return gpt回答.content.decode('utf-8')


if __name__ == '__main__':
    前置词=提示词
    问题=前置词+'我是不是最帅的'
    回答=问gpt(问题)
    print(回答)
