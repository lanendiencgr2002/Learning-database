import time
import requests
import json

def 计时(f):
    def wrapper(*args, **kwargs):
        start = time.time()
        result = f(*args, **kwargs)
        print(f'{f.__name__} 耗时：{time.time()-start:.4f}秒')
        return result
    return wrapper

@计时
def 问gpt问题(问题="我帅不？", 提示词="You are a helpful assistant. Use Chinese to respond."):
    url = 'https://api.cymru/v1/chat/completions'

    headers = {
        'accept': 'application/json, text/event-stream',
        'accept-language': 'zh-CN,zh;q=0.9,en;q=0.8',
        'authorization': 'Bearer sk-a8YX4CupVLXx2cgJ6b24C0B83aFa49C6Aa37881c2bAd9129',
        'content-type': 'application/json',
        'origin': 'https://app.nextchat.dev',
        'priority': 'u=1, i',
        'referer': 'https://app.nextchat.dev/',
        'sec-ch-ua': '"Google Chrome";v="129", "Not=A?Brand";v="8", "Chromium";v="129"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"Windows"',
        'sec-fetch-dest': 'empty',
        'sec-fetch-mode': 'cors',
        'sec-fetch-site': 'cross-site',
        'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36'
    }

    payload = {
        "messages": [
            {"role": "system", "content": 提示词},
            {"role": "user", "content": 问题},
        ],
        "stream": True,
        "model": "gpt-4o-2024-08-06", # 有claude-3-5-sonnet,gpt-4o,gemini-1.5-pro
        "temperature": 0.5,
        "presence_penalty": 0,
        "frequency_penalty": 0,
        "top_p": 1,
        "max_tokens": 8000
    }

    response = requests.post(url, headers=headers, json=payload, stream=True)

    full_response = ""
    if response.status_code == 200:
        for line in response.iter_lines():
            if line:
                line = line.decode('utf-8')
                if line.startswith('data: '):
                    data = line[6:]
                    if data != '[DONE]':
                        try:
                            json_data = json.loads(data)
                            content = json_data['choices'][0]['delta'].get('content', '')
                            full_response += content
                            # print(content, end='', flush=True)
                        except json.JSONDecodeError:
                            print(f"无法解析JSON: {data}")
    else:
        print(f"请求失败，响应内容: {response.text}")
    return full_response

if __name__ == "__main__":
    回答 = 问gpt问题()
    print(回答)

