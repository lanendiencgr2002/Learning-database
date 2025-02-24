import requests
import json
import os

class DeepLTranslator:
    '''
    api_index从0开始
    '''
    def __init__(self, api_name=None, api_index=None):
        self.headers = {
            "User-Agent": "Apifox/1.0.0 (https://apifox.com)",
            "Content-Type": "application/json"
        }
        self.apis = self._load_apis()
        self.current_api = self._select_api(api_name, api_index)
        
    def _load_apis(self):
        """从 JSON 文件加载 API 配置"""
        try:
            json_path = os.path.join(os.path.dirname(__file__), 'translation_apis.json')
            with open(json_path, 'r', encoding='utf-8') as f:
                config = json.load(f)
                return config.get('translation_apis', [])
        except Exception as e:
            print(f"加载 API 配置失败: {str(e)}")
            return []

    def _select_api(self, api_name=None, api_index=None):
        """
        选择指定的 API
        
        参数:
            api_name (str): API 名称
            api_index (int): API 序号（从0开始）
            
        返回:
            str: API URL
        """
        # 默认使用第一个API
        if not self.apis:
            return "https://deeplx.doi9.top/translate"
            
        # 通过名称选择
        if api_name:
            for api in self.apis:
                if api['name'].lower() == api_name.lower():
                    return api['url']
                    
        # 通过序号选择
        if api_index is not None:
            if 0 <= api_index < len(self.apis):
                return self.apis[api_index]['url']
                
        # 如果没有指定或找不到，使用第一个API
        return self.apis[0]['url']
        
    def set_api(self, api_name=None, api_index=None):
        """
        更改当前使用的 API
        
        参数:
            api_name (str): API 名称
            api_index (int): API 序号（从0开始）
        """
        self.current_api = self._select_api(api_name, api_index)

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
                self.current_api,
                headers=self.headers,
                data=json.dumps(payload),
                timeout=10
            )
            
            if response.status_code == 200:
                result = response.json()
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
    # 通过名称选择API
    translator = DeepLTranslator(api_name="DeepLX-1")
    # 或通过序号选择API
    # translator = DeepLTranslator(api_index=0)
    
    test_text = "Hello, world!"
    result = translator.translate(test_text)
    print(f"原文: {test_text}")
    print(f"译文: {result}")
    
    # 切换到其他API
    translator.set_api(api_name="DeepLX-2")
    # 或使用序号切换
    # translator.set_api(api_index=1)