import pandas as pd
import json
from 谷歌搜公司经理 import search_general_manager
import time
import os

def find_missing_gm(需要找经理的缺少总经理的csv文件):
    当前目录 = os.path.dirname(os.path.abspath(__file__))
    
    # 读取公司名.txt文件
    companies_file = os.path.join(当前目录, '公司名.txt')
    with open(companies_file, 'r', encoding='utf-8') as f:
        txt_companies = [line.strip() for line in f.readlines() if line.strip()]
    
    # 读取CSV文件
    df = pd.read_csv(需要找经理的缺少总经理的csv文件) if os.path.exists(需要找经理的缺少总经理的csv文件) else pd.DataFrame(columns=['酒店名称', '电话号码', '姓名', '职位', '电子邮件'])
    
    # 获取所有需要处理的公司
    companies_to_process = set()
    
    # 添加CSV中没有总经理的公司
    for hotel in df['酒店名称'].unique():
        # 获取该酒店的所有行
        hotel_data = df[df['酒店名称'] == hotel]
        # 检查职位中是否包含'general manager'
        positions = hotel_data['职位'].fillna('')  # 处理NaN值
        has_gm = any('general manager' in position.lower().strip() for position in positions)
        # 如果职位中不包含总经理相关职位，则添加到需要处理的公司列表
        if not has_gm:
            companies_to_process.add(hotel)
    
    # 添加txt文件中但不在CSV中的公司
    existing_hotels = set(df['酒店名称'].unique())
    # 更新需要处理的公司列表
    companies_to_process.update([company for company in txt_companies if company not in existing_hotels])
    
    # 清空日志文件
    log_file_path = os.path.join(当前目录, '经理填补日志.txt')
    with open(log_file_path, 'w', encoding='utf-8') as log_file:
        log_file.write('')

    updates = []
    for hotel in companies_to_process:
        print(f"\n正在搜索 {hotel} 的总经理信息...")
        
        # 使用谷歌搜索获取总经理信息
        result = search_general_manager(hotel)
        
        try:
            # 将字符串转换为字典
            gm_info = json.loads(result)
            
            if gm_info.get('name') and gm_info['name'].lower() != 'none':
                updates.append({
                    '酒店名称': hotel,
                    '电话号码': "No phone number",
                    '姓名': gm_info['name'],
                    '职位': 'General Manager',
                    '电子邮件': 'no email'
            
                })
                # 电子邮件
                # gm_info.get('email', f"gm@{hotel.lower().replace(' ', '')}.com")
                # 电话号码
                # gm_info.get('phone', 'No phone number')
            # 添加到日志
            with open(log_file_path, 'a', encoding='utf-8') as log_file:
                log_file.write(f"为 {hotel} 找到了总经理信息：{gm_info}\n")
        except:
            print(f"处理 {hotel} 的搜索结果时出错")
        
        # 添加延时以避免被封IP
        time.sleep(1)
    
    # 将新信息添加到DataFrame
    if updates:
        updates_df = pd.DataFrame(updates)
        df = pd.concat([df, updates_df], ignore_index=True)
        
        # 创建保存目录
        output_dir = os.path.join(当前目录, 'csv文件夹21个公司', '更新后的文件')
        os.makedirs(output_dir, exist_ok=True)
        
        # 保存更新后的文件
        output_path = os.path.join(output_dir, 'final_filtered_results_with_gm.csv')
        df.to_csv(output_path, index=False)
        print(f"\n已将更新后的数据保存到: {output_path}")
    else:
        print("\n没有找到需要更新的信息")

if __name__ == "__main__":
    find_missing_gm(r'C:\Users\11923\Documents\GitHub\Learning-database\python_study\爬虫单子\final_filtered_results.csv') 