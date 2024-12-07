import requests
import json

def translate_text(text):
    url = "https://api.deeplx.org/nZyuEfvNhWk8Xfh2BP_0Dk3ZEnk5SdFIwA_8jaaAM8Q/translate"
    
    headers = {
        "accept": "*/*",
        "accept-language": "zh-CN,zh;q=0.9,en;q=0.8",
        "content-type": "application/json",
        "origin": "https://libretranslator.pages.dev",
        "referer": "https://libretranslator.pages.dev/",
        "sec-ch-ua": '"Google Chrome";v="131", "Chromium";v="131", "Not_A Brand";v="24"',
        "sec-ch-ua-mobile": "?0",
        "sec-ch-ua-platform": '"Windows"',
        "sec-fetch-dest": "empty",
        "sec-fetch-mode": "cors",
        "sec-fetch-site": "cross-site",
        "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"
    }
    
    data = {
        "text": text,
        "target_lang": "ZH"
    }
    
    try:
        response = requests.post(url, headers=headers, json=data)
        if response.status_code == 200:
            result = response.json()
            return result.get("data", "翻译失败")
        else:
            return f"请求失败: {response.status_code}"
    except Exception as e:
        return f"发生错误: {str(e)}"

# 测试代码
if __name__ == "__main__":
    test_text = "libretranslator pages dev"
    result = translate_text(test_text)
    print(f"原文: {test_text}")
    print(f"译文: {result}") 