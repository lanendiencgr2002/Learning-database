import pandas as pd
import json
import os

def process_gm_positions(csv_file_path):
    # 读取CSV文件
    df = pd.read_csv(csv_file_path)
    
    # 标准化空值
    df['电话号码'] = df['电话号码'].fillna('No phone number')
    df['电子邮件'] = df['电子邮件'].fillna('no email')
    df['电话号码'] = df['电话号码'].replace('null', 'No phone number')
    df['电子邮件'] = df['电子邮件'].replace('null', 'no email')
    
    # 创建酒店名称映射字典
    hotel_name_mapping = {name.lower(): name for name in df['酒店名称'].unique()}
    
    # 读取日志文件获取总经理信息
    当前目录 = os.path.dirname(os.path.abspath(__file__))
    log_file_path = os.path.join(当前目录, '经理填补日志.txt')
    
    gm_info_dict = {}
    with open(log_file_path, 'r', encoding='utf-8') as f:
        for line in f:
            if line.strip():
                try:
                    # 改进的解析逻辑
                    if '找到了总经理信息：' in line:
                        hotel_name = line.split('找到了总经理信息：')[0].replace('为 ', '').strip()
                        info_str = line.split('找到了总经理信息：')[1].strip()
                        
                        # 统一处理JSON字符串
                        info_str = info_str.replace("'", '"').replace('None', 'null')
                        info = json.loads(info_str)
                        
                        if info.get('name') and info['name'].lower() != 'none':
                            # 获取电话号码，优先使用phone，如果没有则使用phone_number
                            phone = info.get('phone') or info.get('phone_number')
                            phone = 'No phone number' if not phone or str(phone).lower() == 'none' else phone
                            
                            # 获取邮箱
                            email = info.get('email')
                            email = 'no email' if not email or str(email).lower() == 'none' else email
                            
                            gm_info_dict[hotel_name] = {
                                '酒店名称': hotel_name,
                                '电话号码': phone,
                                '姓名': info['name'],
                                '职位': 'General Manager',
                                '电子邮件': email
                            }
                except Exception as e:
                    print(f"处理行时出错: {line.strip()}")
                    print(f"错误信息: {str(e)}")
                    continue

    # 处理总经理信息时使用映射
    standardized_gm_info = {}
    for hotel, info in gm_info_dict.items():
        # 使用原CSV中的酒店名称格式
        standard_name = hotel_name_mapping.get(hotel.lower(), hotel)
        
        # 标准化电话和邮件格式
        phone = info.get('电话号码', 'No phone number')
        phone = 'No phone number' if phone in ['null', None, 'None', ''] else phone
        
        email = info.get('电子邮件', 'no email')
        email = 'no email' if email in ['null', None, 'None', ''] else email
        
        standardized_gm_info[standard_name] = {
            '酒店名称': standard_name,
            '电话号码': phone,
            '姓名': info['姓名'],
            '职位': 'General Manager',
            '电子邮件': email
        }

    # 创建新的DataFrame来存储结果
    new_rows = []
    processed_hotels = set()
    
    # 首先处理CSV中已有的酒店
    for hotel in df['酒店名称'].unique():
        processed_hotels.add(hotel.lower())
        # 获取该酒店的所有行
        hotel_rows = df[df['酒店名称'] == hotel]
        
        # 检查是否已经有总经理
        has_gm = any(
            'general manager' in str(position).lower().strip() or 
            'gm' == str(position).lower().strip() or
            '总经理' in str(position).strip()
            for position in hotel_rows['职位']
        )
        
        # 只在没有总经理的情况下添加新的总经理信息
        if not has_gm and hotel in standardized_gm_info:
            new_rows.append(standardized_gm_info[hotel])
        
        # 添加该酒店的所有原始行
        new_rows.extend(hotel_rows.to_dict('records'))
    
    # 处理日志中有但CSV中没有的酒店
    for hotel in standardized_gm_info:
        if hotel.lower() not in processed_hotels:
            new_rows.append(standardized_gm_info[hotel])
            print(f"添加了新酒店的总经理信息: {hotel}")

    # 创建新的DataFrame并保存
    new_df = pd.DataFrame(new_rows)
    
    # 打印统计信息
    print(f"\n处理完成！")
    print(f"原CSV文件中的酒店数量: {len(df['酒店名称'].unique())}")
    print(f"总共处理的总经理信息数量: {len(gm_info_dict)}")
    print(f"新增加的酒店数量: {len(gm_info_dict) - len(processed_hotels)}")
    print(f"最终CSV文件中的行数: {len(new_df)}")
    
    # 保存文件
    output_path = os.path.join(当前目录, 'final_filled_with_gm.csv')
    new_df.to_csv(output_path, index=False)
    print(f"已保存更新后的文件到: {output_path}")

if __name__ == "__main__":
    csv_file_path = r'C:\Users\11923\Documents\GitHub\Learning-database\python_study\爬虫单子\final_filtered_results.csv'
    process_gm_positions(csv_file_path) 

