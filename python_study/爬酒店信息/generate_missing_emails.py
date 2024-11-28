import pandas as pd
import json
import os
import re

def format_name(name, pattern):
    """
    根据给定的模式格式化姓名
    """
    if pd.isna(name) or not isinstance(name, str):
        return None
    
    # 移除括号内的内容和多余的空格
    name = re.sub(r'\([^)]*\)', '', name).strip()
    # 分割姓名
    parts = name.split()
    if len(parts) < 2:
        return None
        
    firstname = parts[0].lower()
    lastname = parts[-1].lower()
    
    if pattern == 'firstname.lastname':
        return f"{firstname}.{lastname}"
    elif pattern == 'FIRSTNAME.LASTNAME':
        return f"{firstname.upper()}.{lastname.upper()}"
    elif pattern == 'firstname_lastname':
        return f"{firstname}_{lastname}"
    elif pattern == 'firstnamelastname':
        return f"{firstname}{lastname}"
    
    return None

def generate_missing_emails(csv文件目录):
    当前目录 = os.path.dirname(os.path.abspath(__file__))
    
    # 创建新目录用于保存更新后的文件
    输出目录 = os.path.join(当前目录, "生成邮件后的csv文件目录")
    if not os.path.exists(输出目录):
        os.makedirs(输出目录)
    
    # 读取邮件模式分析结果
    with open(os.path.join(当前目录, "result_analyze_email.json"), 'r', encoding='utf-8') as f:
        email_patterns = json.load(f)
    
    # 处理每个CSV文件
    csv_files = [f for f in os.listdir(csv文件目录) if f.endswith('.csv') and f.startswith('result_')]
    
    for csv_file in csv_files:
        # 对每个csv文件进行处理 转为公司名字
        company_name = csv_file.replace('result_', '').replace('_1.csv', '')
        if company_name not in email_patterns:
            continue
            
        # 获取该公司最常用的域名和邮件格式 如果公司没有邮件格式 则跳过
        company_data = email_patterns[company_name]
        if not company_data['sorted_domains'] or not company_data['sorted_patterns']:
            continue
            
        # 获取最常用的域名和邮件格式
        main_domain = company_data['sorted_domains'][0]
        main_pattern = company_data['sorted_patterns'][0]
        
        # 读取CSV文件
        df = pd.read_csv(os.path.join(csv文件目录, csv_file))
        
        # 处理缺失的邮件
        for idx, row in df.iterrows():
            if pd.isna(row['电子邮件']) or row['电子邮件'] == 'No email':
                if '姓名' in df.columns:
                    formatted_name = format_name(row['姓名'], main_pattern)
                    if formatted_name:
                        generated_email = f"{formatted_name}@{main_domain}"
                        df.at[idx, '电子邮件'] = generated_email
                        print(f"为 {row['姓名']} 生成邮件: {generated_email}")
        
        # 保存更新后的CSV文件到新目录
        output_file = os.path.join(输出目录, f"updated_{csv_file}")
        df.to_csv(output_file, index=False, encoding='utf-8')
        print(f"\n已更新文件: {output_file}")

if __name__ == "__main__":
    generate_missing_emails(r'C:\Users\11923\Documents\GitHub\Learning-database\python_study\爬虫单子\csv文件夹21个公司')
