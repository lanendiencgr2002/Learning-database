import requests
import json

class AITranslator:
    def __init__(self, api_url="http://localhost:5000/chat"):
        self.api_url = api_url

    def translate(self, text):
        """
        调用AI接口进行翻译
        
        Args:
            text: 要翻译的文本
            
        Returns:
            str: 翻译结果
        """
        try:
            prompt = self._create_translation_prompt(text)
            response = requests.post(
                self.api_url,
                json={"问题": prompt},
                headers={'Content-Type': 'application/json'}
            )
            
            if response.status_code == 200:
                result = response.json()
                translation = result.get('data', {}).get('message', '未获取到翻译')
                return translation
            else:
                return f"翻译服务器错误: {response.status_code}"
        except Exception as e:
            return f"翻译失败: {str(e)}"
    
    def _create_translation_prompt(self, text):
        """创建翻译提示词"""
        return f"""你是一��专业的翻译专家，请将以下英文翻译成中文：

{text}

要求：
1. 准确传达原文含义
2. 使用地道的中文表达
3. 保持专业术语的准确性
4. 只返回翻译结果，无需解释
""" 