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
@计时 # 等价于 计时(问gpt2(参数))
def 问gpt问题(问题="你好啊",提示词="You are a helpful assistant."):
   url = "https://api.chatanywhere.tech/v1/chat/completions"
   payload = json.dumps({
      "model": "gpt-3.5-turbo",
      "messages": [
         {
            "role": "system",
            "content": 提示词
         },
         {
            "role": "user",
            "content": 问题
         }
      ]
   })
   headers = {
      'Authorization': 'Bearer sk-ATXhpDsohKCPBxdGfLsHXFUf0Q8zDhNxUNjwQOvLWwOLoJMP',
      'User-Agent': 'Apifox/1.0.0 (https://apifox.com)',
      'Content-Type': 'application/json',
      'Accept': '*/*',
      'Host': 'api.chatanywhere.tech',
      'Connection': 'keep-alive'
   }
   response = requests.post(url, headers=headers, data=payload)
   response_data = response.json()
   return response_data['choices'][0]['message']['content']

if __name__ == "__main__":
    print(问gpt问题())