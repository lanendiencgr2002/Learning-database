import requests
import json
import time
from typing import Dict, List, Tuple
import statistics
import pathlib

class TranslationService:
    def __init__(self, config_file: str = "translation_apis.json"):
        # 获取当前文件所在目录
        current_dir = pathlib.Path(__file__).parent
        self.config_path = current_dir / config_file
        
        self.headers = {
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
        self.load_apis(self.config_path)
        
    def load_apis(self, config_file: pathlib.Path):
        try:
            with config_file.open('r', encoding='utf-8') as f:
                self.apis = json.load(f)['translation_apis']
        except Exception as e:
            print(f"加载配置文件失败: {str(e)}")
            self.apis = []

    def translate_with_api(self, text: str, api: Dict) -> Tuple[str, float, str]:
        start_time = time.time()
        try:
            data = {
                "text": text,
                "target_lang": "ZH"
            }
            response = requests.post(api['url'], headers=self.headers, json=data)
            elapsed_time = time.time() - start_time
            
            if response.status_code == 200:
                result = response.json()
                return result.get("data", "翻译失败"), elapsed_time, ""
            else:
                error_msg = f"请求失败: {response.status_code}"
                return "", elapsed_time, error_msg
                
        except Exception as e:
            elapsed_time = time.time() - start_time
            return "", elapsed_time, str(e)

    def translate_text(self, text: str) -> Dict:
        results = []
        successful_times = []
        
        for api in self.apis:
            translation, elapsed_time, error = self.translate_with_api(text, api)
            results.append({
                "api_name": api['name'],
                "translation": translation,
                "time": round(elapsed_time, 3),
                "error": error
            })
            if not error:
                successful_times.append(elapsed_time)

        # 计算平均响应时间
        avg_time = round(statistics.mean(successful_times), 3) if successful_times else 0
        
        # 按响应时间排序
        sorted_results = sorted(results, key=lambda x: x['time'])
        
        return {
            "results": sorted_results,
            "average_time": avg_time,
            "success_count": len(successful_times),
            "total_apis": len(self.apis)
        }

# 测试代码
if __name__ == "__main__":
    translator = TranslationService()
    test_text = "libretranslator pages dev"
    
    result = translator.translate_text(test_text)
    
    print(f"原文: {test_text}")
    print(f"\n翻译结果:")
    print(f"平均响应时间: {result['average_time']}秒")
    print(f"成功率: {result['success_count']}/{result['total_apis']}")
    
    print("\n各API响应详情 (按响应时间排序):")
    for api_result in result['results']:
        if api_result['error']:
            print(f"{api_result['api_name']}: 失败 - {api_result['error']} (耗时: {api_result['time']}秒)")
        else:
            print(f"{api_result['api_name']}: {api_result['translation']} (耗时: {api_result['time']}秒)") 