import requests
import json

class DeepLTranslator:
    def __init__(self):
        self.api_url = "https://deeplx.doi9.top/translate"
        self.headers = {
            "User-Agent": "Apifox/1.0.0 (https://apifox.com)",
            "Content-Type": "application/json"
        }

    def translate(self, text, source_lang="EN", target_lang="ZH"):
        """
        使用DeepL API翻译文本
        
        参数:
            text (str): 要翻译的文本
            source_lang (str): 源语言代码，默认为'EN'
            target_lang (str): 目标语言代码，默认为'ZH'
            
        返回:
            str: 翻译后的文本
        """
        try:
            payload = {
                "text": text,
                "source_lang": source_lang,
                "target_lang": target_lang
            }
            
            response = requests.post(
                self.api_url,
                headers=self.headers,
                data=json.dumps(payload),
                timeout=10
            )
            
            if response.status_code == 200:
                result = response.json()
                # 直接获取data字段的值作为翻译结果
                translated_text = result.get("data", "")
                return translated_text if translated_text else "翻译失败"
            else:
                return f"翻译请求失败: HTTP {response.status_code}"
                
        except requests.exceptions.Timeout:
            return "翻译请求超时"
        except requests.exceptions.RequestException as e:
            return f"翻译请求错误: {str(e)}"
        except json.JSONDecodeError:
            return "解析翻译结果失败"
        except Exception as e:
            return f"翻译过程出现错误: {str(e)}"

# 使用示例
if __name__ == "__main__":
    translator = DeepLTranslator()
    test_text = "Hello, world!"
    result = translator.translate(test_text)
    print(f"原文: {test_text}")
    print(f"译文: {result}")
