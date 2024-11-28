import requests
import urllib.parse
from bs4 import BeautifulSoup
import aiohttp
import asyncio

def ai_response(content):
    """
    使用本地 FastAPI 接口发送请求获取 AI 回答
    """
    async def get_response():
        async with aiohttp.ClientSession() as session:
            async with session.post(
                "http://localhost:5000/chat",
                json={"问题": content}
            ) as response:
                if response.status == 200:
                    result = await response.json()
                    return result["data"]["message"]
                else:
                    return None

    # 在同步函数中运行异步代码
    try:
        loop = asyncio.get_event_loop()
    except RuntimeError:
        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
    
    return loop.run_until_complete(get_response())

def search_general_manager(company_name):
    headers = {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36'
    }
    
    # 构建搜索查询
    query = f"{company_name} General Manager"
    encoded_query = urllib.parse.quote(query)
    url = f"https://www.google.com/search?q={encoded_query}"
    
    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        
        soup = BeautifulSoup(response.text, 'html.parser')
        search_results = soup.find_all('div', class_='g')
        标题=[]
        详情=[]
        # print(f"正在搜索 {company_name} 的总经理...")
        for result in search_results[:10]:  # 增加到前10条结果
            try:
                title = result.find('h3').text
                snippet = result.find('div', class_='VwiC3b').text if result.find('div', class_='VwiC3b') else ""
                标题.append(title)
                详情.append(snippet)
                # print(f"标题: {title}")
                # print(f"详情: {snippet}")
            except Exception as e:
                continue
        res=ai_response(f"帮我从以下信息中，提取{company_name}的总经理的名字!全名,还有电话号码，邮件，如果没用None,用json的方式返回给我，键名要英文! 如果实在找不到返回None，标题: {标题}\n详情: {详情}")
        print(res)
        return res
                
    except Exception as e:
        print(f"搜索出错: {str(e)}")
        return None

if __name__ == "__main__":
    # 示例使用
    company = "Hilton Kuala Lumpur"  # 可以换成任何公司名
    gm_name = search_general_manager(company)
    if gm_name:
        print(f"\n{company} 的总经理是: {gm_name}") 