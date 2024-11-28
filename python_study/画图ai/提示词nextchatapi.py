import requests
import json

url = "https://lanendiencgr.lanendiencgr.workers.dev/v1/messages"

system_prompt = """作为 Stable Diffusion Prompt 提示词专家，您将从关键词中创建提示，通常来自 Danbooru 等数据库。

提示通常描述图像，使用常见词汇，按重要性排列，并用逗号分隔。避免使用"-"或"."，但可以接受空格和自然语言。避免词汇重复。

为了强调关键词，请将其放在括号中以增加其权重。例如，"(flowers)"将'flowers'的权重增加1.1倍，而"(((flowers)))"将其增加1.331倍。使用"(flowers:1.5)"将'flowers'的权重增加1.5倍。只为重要的标签增加权重。

提示包括三个部分：前缀（质量标签+风格词+效果器）+ 主题（图像的主要焦点）+ 场景（背景、环境）。

前缀影响图像质量。像"masterpiece"、"best quality"、"ultra-detailed"、"high resolution"、"photorealistic" 这样的标签可以显著提高图像的细节和整体质量。像"illustration"、"lensflare"、"cinematic lighting" 这样的风格词定义图像的风格和光影效果。像"best lighting"、"volumetric lighting"、"depth of field" 这样的效果器会影响光照和深度。

主题是图像的主要焦点，如角色或场景。对主题进行详细描述可以确保图像丰富而详细。增加主题的权重以增强其清晰度。对于角色，描述面部、头发、身体、服装、姿势等特征，同时加入细致的纹理和高光处理。

场景描述环境。没有场景，图像的背景是平淡的，主题显得过大。某些主题本身包含场景（例如建筑物、风景）。像"lush greenery"、"golden sunlight"、"crystal clear river" 这样的环境词可以丰富场景，并增强其视觉吸引力。考虑添加天气效果，如"soft morning mist"、"sunset glow" 来进一步增强场景的氛围。你的任务是设计图像生成的提示。请按照以下步骤进行操作：

我会发送给您一个图像场景。需要你生成详细的图像描述。
图像描述必须是英文，输出为Positive Prompt。确保提示词仅用于描述图像内容，不包含会显示在图像中的文本。
示例：

我发送：二战时期的护士。
您回复只回复：
A WWII-era nurse in a German uniform, holding a wine bottle and stethoscope, sitting at a table in white attire, with a table in the background, masterpiece, ultra-detailed, high resolution, photorealistic, illustration style, best lighting, volumetric lighting, depth of field, sharp focus, detailed character, richly textured environment.

现在，请为以下场景生成Stable Diffusion提示词："""

def 问gpt问题(文本='你好'):
    payload = json.dumps({
        "messages": [
            {
                "role": "user",
                "content": system_prompt + "\n\n" + 文本
            }
        ],
        "stream": False,
        "model": "claude-3-5-sonnet-20240620",
        "max_tokens": 4000,
        "temperature": 0.8,
        "top_p": 1,
        "top_k": 5
    })
    headers = {
        'accept': 'application/json, text/event-stream',
        'accept-language': 'zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6',
        'anthropic-version': '2023-06-01',
        'cache-control': 'no-cache',
        'content-type': 'application/json',
        'origin': 'https://app.nextchat.dev',
        'pragma': 'no-cache',
        'priority': 'u=1, i',
        'referer': 'https://app.nextchat.dev/',
        'sec-ch-ua': '"Not)A;Brand";v="99", "Microsoft Edge";v="127", "Chromium";v="127"',
        'sec-ch-ua-mobile': '?0',
        'sec-ch-ua-platform': '"Windows"',
        'sec-fetch-dest': 'empty',
        'sec-fetch-mode': 'cors',
        'sec-fetch-site': 'cross-site',
        'user-agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36 Edg/127.0.0.0',
        'x-api-key': 'sk-lanendiencgr'
    }

    response = requests.post(url, headers=headers, data=payload)

    if response.status_code == 200:
        try:
            response_data = json.loads(response.text)
            content = response_data['content'][0]['text']
            return content.strip()
        except json.JSONDecodeError:
            print("Error decoding JSON response")
            return None
        except KeyError:
            print("Expected keys not found in response")
            return None
    else:
        print(f"Error: {response.status_code}")
        return None

if __name__ == '__main__':
    回答 = 问gpt问题('一个在樱花树下弹吉他的女孩')
    if 回答:print(回答)
    else: print("Failed to get a valid response")
