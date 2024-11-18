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

    payload = {
        "messages": [
            {"role": "system", "content": 提示词},
            {"role": "user", "content": 问题},
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
    回答 = 问gpt问题()
    print(回答)

''' 模型：
OpenAI	gpt-4o-mini,gpt-4o-mini-free,gpt-4o,gpt-4o-free
开源大模型	glm3,glm4,yi1.5,deepseek,qwen2,qwen1.5,internlm
Grok	grok-2,grok-2-mini
solar	solar-1-mini-chat
智谱	glm-4,glm-4v,glm-3-turbo
360	360gpt-pro,360gpt-turbo
Moonshot	moonshot-v1-8k,moonshot-v1-32k,moonshot-v1-128k
零一万物	yi-large,yi-medium,yi-vision,yi-medium-200k,yi-spark,yi-large-rag,yi-large-turbo
百川大模型	Baichuan2-Turbo,Baichuan2-Turbo-192k
DeepSeek	deepseek-coder,deepseek-chat
书生	internlm2.5-latest
商汤	SenseChat,SenseChat-5
讯飞星火	SparkDesk
千问、混元、豆包	待上
flux绘画	draw
'''