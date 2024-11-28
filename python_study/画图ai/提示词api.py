import requests
import json

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
    url = "https://europe-west1-aiplatform.googleapis.com/v1/projects/my-project-999-427810/locations/europe-west1/publishers/anthropic/models/claude-3-5-sonnet@20240620:streamRawPredict"
    payload = {
        "anthropic_version": "vertex-2023-10-16",
        "messages": [
            {
                "role": "user",
                "content": [
                    {
                        "type": "text",
                        "text": system_prompt + "\n\n" + 文本
                    }
                ]
            }
        ],
        "max_tokens": 256,
        "stream": False
    }
    headers = {
        'Authorization': 'Bearer ya29.a0AcM612wofWPFzkbQ4gGH46kKNqg6SJXDSNkXBr6pFQyKHhU5ZTaQq_X0YdncYcFWUNP1G-cVZrs-FMcUZQbg5tUVJSH5VTnbIGvVktealOKxWkIiz-RWpjb0bO4NDB_U3d7CuMN9JCvOrkU3rpGBnL92C864SySjEQEgICJasjNyPs0KejMaAhP6D_JIwPx1wpIr2pWtDggYqW7kHjB1nHYQHzXhnPv9x8zvrvN47Nt-IB1whaNZrELkfwmTgSNMplFHi-_PqVzUaUjNnHy7KmXbqrTEegge0a5uwco2WR3EJvrkhgsq0RiI3tChq_aWYLiPZe-ThVVVZ5q8Xo0spFQ2pIPD4KyY-4R5sqdi2_ihfnKS8Lc1lfWTcIqOdzhVMUxR_qeqTHrJa94sUUbseLNjqGzEsKp3aCgYKAW4SARMSFQHGX2MiFGUW34JxztMMh2o7E8rmtQ0423',
        'Content-Type': 'application/json; charset=utf-8'
    }
    response = requests.post(url, headers=headers, data=json.dumps(payload))
    if response.status_code == 200:
        response_data = json.loads(response.text)
        assistant_text = response_data['content'][0]['text']
        return assistant_text
    else:
        print(f"Error: {response.status_code}")
        return response.text

if __name__ == '__main__':
    回答 = 问gpt问题('一个在樱花树下弹吉他的女孩')
    print(回答)
