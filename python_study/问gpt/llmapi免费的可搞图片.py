import time
import requests
import json
import base64
import os
当前工作目录 = os.path.dirname(os.path.abspath(__file__))
def 计时(f):
    def wrapper(*args, **kwargs):
        start = time.time()
        result = f(*args, **kwargs)
        print(f'{f.__name__} 耗时：{time.time()-start:.4f}秒')
        return result
    return wrapper

def 编码图片(图片路径):
    with open(图片路径, "rb") as image_file:
        return base64.b64encode(image_file.read()).decode('utf-8')

def 读入文件夹下的第一张图片(当前文件夹=None):
    图片目录 = os.path.join(当前工作目录, 当前文件夹)
    for 文件 in os.listdir(图片目录):
        if 文件.endswith('.png') or 文件.endswith('.jpg'):
            图片路径 = os.path.join(图片目录, 文件)
            return 图片路径
    return None

@计时
def 问gpt图片问题(问题="这张图片里有什么?", 图片路径="path/to/your/image.jpg", 提示词="You are a helpful assistant. Use Chinese to respond."):
    url = 'https://llm.indrin.cn/v1/chat/completions'

    headers = {
        'accept': 'application/json, text/event-stream',
        'accept-language': 'zh-CN,zh;q=0.9,en;q=0.8',
        'authorization': 'Bearer sk-qnhHDUFS4TitBZHh3a159fD7566543F586135eBe673f1d44',
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

    编码后的图片 = 编码图片(图片路径)

    payload = {
        "messages": [
            {"role": "system", "content": 提示词},
            {
                "role": "user", 
                "content": [
                    {"type": "text", "text": 问题},
                    {"type": "image_url", "image_url": {"url": f"data:image/jpeg;base64,{编码后的图片}"}}
                ]
            },
        ],
        "stream": True,
        "model": "gpt-4o",
        "temperature": 0.5,
        "presence_penalty": 0,
        "frequency_penalty": 0,
        "top_p": 1,
        "max_tokens": 4096
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
    图片路径 = 读入文件夹下的第一张图片("./图片")
    回答 = 问gpt图片问题(问题="这张图片里有什么?", 图片路径=图片路径)
    print("\n完整回答:", 回答)