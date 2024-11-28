import pandas as pd
import os
from collections import Counter
import re
import json
from collections import OrderedDict

def analyze_email_pattern(email):
    """分析邮件用户名的格式"""
    if pd.isna(email) or email == 'No email':
        return None
    try:
        # 分离用户名和域名
        username = email.split('@')[0]
        
        # 检查分隔符
        if '.' in username:
            parts = username.split('.')
            if len(parts) == 2:
                # 检查大小写模式
                if parts[0].islower() and parts[1].islower():
                    return 'firstname.lastname'
                elif parts[0].isupper() and parts[1].isupper():
                    return 'FIRSTNAME.LASTNAME'
        elif '_' in username:
            return 'firstname_lastname'
        else:
            # 没有分隔符的情况
            return 'firstnamelastname'
    except:
        return None
    
    return None

def analyze_hotel_emails(csv_path):
    df = pd.read_csv(csv_path)
    
    if '电子邮件' not in df.columns:
        return None
    
    valid_emails = df['电子邮件'].dropna() 
    valid_emails = valid_emails[valid_emails != 'No email']
    
    if len(valid_emails) == 0:
        return None
    
    # 创建域名和对应的邮件格式映射
    domain_patterns = {}
    for email in valid_emails:
        if '@' in str(email):
            domain = email.split('@')[1]
            pattern = analyze_email_pattern(email)
            if pattern:
                if domain not in domain_patterns:
                    domain_patterns[domain] = []
                domain_patterns[domain].append(pattern)
    
    # 统计每个域名最常见的邮件格式
    domain_common_patterns = {}
    for domain, patterns in domain_patterns.items():
        pattern_counts = Counter(patterns)
        most_common_pattern = pattern_counts.most_common(1)[0] if pattern_counts else None
        domain_common_patterns[domain] = most_common_pattern
    
    domains = [email.split('@')[1] for email in valid_emails if '@' in str(email)]
    domain_counts = Counter(domains)
    
    patterns = [analyze_email_pattern(email) for email in valid_emails]
    pattern_counts = Counter([p for p in patterns if p is not None])
    
    return {
        'total_emails': len(valid_emails),
        'domains': domain_counts,
        'patterns': pattern_counts,
        'domain_patterns': domain_common_patterns
    }

def export_company_email_patterns_to_json(csv_path, output_path=None):
    """
    分析单个公司的邮件模式并输出为JSON格式，按数量排序
    """
    analysis = analyze_hotel_emails(csv_path)
    if not analysis:
        return None
    
    # 构建JSON结构，使用OrderedDict确保排序
    result = {
        "company_name": os.path.basename(csv_path).replace('result_', '').replace('_1.csv', ''),
        "total_emails": analysis['total_emails'],
        "domains": {},
        "email_patterns": {}
    }
    
    # 对域名进行排序处理
    sorted_domains = analysis['domains'].most_common()
    for domain, count in sorted_domains:
        result["domains"][domain] = {
            "count": count,
            "percentage": round((count / analysis['total_emails']) * 100, 1),
            "main_pattern": analysis['domain_patterns'].get(domain)[0] if domain in analysis['domain_patterns'] else None,
            "pattern_count": analysis['domain_patterns'].get(domain)[1] if domain in analysis['domain_patterns'] else 0
        }
    
    # 对邮件格式进行排序处理
    sorted_patterns = analysis['patterns'].most_common()
    for pattern, count in sorted_patterns:
        result["email_patterns"][pattern] = {
            "count": count,
            "percentage": round((count / analysis['total_emails']) * 100, 1)
        }
    
    # 添加排序信息
    result["sorted_domains"] = [domain for domain, _ in sorted_domains]
    result["sorted_patterns"] = [pattern for pattern, _ in sorted_patterns]
    
    if output_path:
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(result, f, ensure_ascii=False, indent=4)
    
    return result

def main(csv文件目录):
    '''
    输入：csv文件目录
    输出：对邮件的分析结果 result_analyze_email.json
    '''
    csv_files = [f for f in os.listdir(csv文件目录) if f.endswith('.csv') and f.startswith('result_')]
    
    # 存储所有公司的分析结果
    all_results = {}
    
    for csv_file in csv_files:
        csv_path = os.path.join(csv文件目录, csv_file)
        result = export_company_email_patterns_to_json(csv_path)
        if result:
            company_name = result["company_name"]
            all_results[company_name] = result
            print(f"\n已分析 {company_name}")
    
    # 将所有结果保存到一个JSON文件
    当前目录=os.path.dirname(os.path.abspath(__file__))
    output_json_path = os.path.join(当前目录, "result_analyze_email.json")
    with open(output_json_path, 'w', encoding='utf-8') as f:
        json.dump(all_results, f, ensure_ascii=False, indent=4)
    print(f"\n所有分析结果已保存到: {output_json_path}")
    
    # ... 原有的打印分析结果的代码保持不变 ...
    results = {}
    
    for csv_file in csv_files:
        hotel_name = csv_file.replace('result_', '').replace('_1.csv', '')
        analysis = analyze_hotel_emails(os.path.join(csv文件目录, csv_file))
        if analysis:
            results[hotel_name] = analysis
    
    print("\n=== 邮件分析结果 ===\n")
    
    for hotel, data in results.items():
        print(f"\n酒店: {hotel}")
        print(f"总邮件数: {data['total_emails']}")
        
        print("\n域名分布及其主要邮件格式:")
        for domain, count in data['domains'].most_common():
            percentage = (count / data['total_emails']) * 100
            domain_pattern = data['domain_patterns'].get(domain)
            pattern_info = f", 主要格式: {domain_pattern[0]} ({domain_pattern[1]}次)" if domain_pattern else ""
            print(f"  {domain}: {count} ({percentage:.1f}%){pattern_info}")
        
        print("\n邮件格式:")
        for pattern, count in data['patterns'].most_common():
            percentage = (count / data['total_emails']) * 100
            print(f"  {pattern}: {count} ({percentage:.1f}%)")
        
        print("\n" + "-"*50)

if __name__ == "__main__":
    csv目录=r'''
C:\Users\11923\Documents\GitHub\Learning-database\python_study\爬虫单子\csv文件夹21个公司
'''
    csv目录=csv目录.strip()
    main(csv目录) 