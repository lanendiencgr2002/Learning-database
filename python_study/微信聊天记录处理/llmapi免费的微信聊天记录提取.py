import time
import requests
import json
import os
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

def 提取单个文件的文本(文件路径):
    with open(文件路径, 'r', encoding='utf-8') as file:
        content = file.read()
    return content

def ai提示词():
    return '''请分析以下聊天记录，根据以下方面提取关键信息：

1. 个人信息提取
- 年龄/生日/星座相关描述
- 身高体型/外貌特征
- 学历/学校/专业信息
- 工作/职业相关信息
- 居住地/家乡信息

2. 感情经历分析
- 恋爱时长和时间线
- 分手过程和原因
- 复合/和好经历
- 感情观念和态度
- 处理感情问题的方式
- 与前任的互动细节

3. 性格特征归纳
- 性格描述语句
- 处事方式描述
- 兴趣爱好表现
- 生活习惯描述
- 情绪表达方式

4. 关键事件记录
- 重要生活转折点
- 特殊约会经历
- 印象深刻的互动
- 重要决定时刻

5. 价值观察
- 对感情的看法
- 择偶标准
- 人生规划
- 对重要议题的态度

输出要求：
1. 提取的每条信息需标注上下文
2. 对模糊或矛盾信息进行标注
3. 按时间顺序或逻辑关系组织信息
4. 标注信息的可信度级别'''

def ai提取微信聊天记录(文件路径):
    content = 提取单个文件的文本(文件路径)
    回答 = 问gpt问题(问题="这是女朋友微信中给我发的所有话，请帮我提取出她的兴趣爱好习惯，各种信息"+content, 提示词=ai提示词())
    return 回答

def 单个文件生成回答(文件路径):
    '''
    单个文件生成回答,传入文件路径，会在文件路径下的回答文件夹中，生成一个回答的txt文件
    '''
    回答 = ai提取微信聊天记录(文件路径)
    # 获取文件名
    文件名 = os.path.basename(文件路径)
    # 确保回答文件夹存在
    回答文件夹 = os.path.join("拆分后的文件", "回答")
    if not os.path.exists(回答文件夹):
        os.makedirs(回答文件夹)
    # 构建回答文件路径
    回答文件路径 = os.path.join(回答文件夹, f"{文件名}的回答.txt")
    with open(回答文件路径, "w", encoding="utf-8") as file:
        file.write(回答)

def 测试():
    回答 = 问gpt问题()
    print(回答)

def 批量文件生成回答(文件夹路径):
    # 确保文件夹存在
    if not os.path.exists(文件夹路径):
        os.makedirs(文件夹路径)
    
    for 文件名 in os.listdir(文件夹路径):
        if 文件名.endswith('.txt'):  # 只处理txt文件
            # 构建完整的文件路径
            文件路径 = os.path.join(文件夹路径, 文件名)
            # 确保回答文件夹存在
            if not os.path.exists(os.path.join("拆分后的文件", "回答")):
                os.makedirs(os.path.join("拆分后的文件", "回答"))
            # 构建回答文件路径
            回答文件路径 = os.path.join("拆分后的文件", "回答", f"{文件名}的回答.txt")
            if os.path.exists(回答文件路径):
                print(f"已经回答过：{文件名}")
                continue
            try:
                print(f"正在处理文件：{文件名}")
                单个文件生成回答(文件路径)
                print(f"完成处理：{文件名}")
            except PermissionError as e:
                print(f"处理文件 {文件名} 时出现权限错误：{str(e)}")
            except Exception as e:
                print(f"处理文件 {文件名} 时出现错误：{str(e)}")

if __name__ == "__main__":
    文件夹路径 = os.path.join(os.getcwd(), "拆分后的文件")
    try:
        批量文件生成回答(文件夹路径)
    except Exception as e:
        print(f"程序执行出错：{str(e)}")


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