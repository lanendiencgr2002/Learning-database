import pandas as pd
import os

'''
只需要配置当前director和manager关键词
'''
keywords_priority = {
    'pr': 1,
    'media': 1,
    'public relations': 1,
    'communication': 1,
    'communications': 1,
    'market': 2,
    'marketing': 2
}

def categorize_position(title):
    """对职位进行分类"""
    title = str(title).lower()
    去掉括号=title.replace('(', '').replace(')', '')
    # 岗位分割
    岗位分割 = 去掉括号.split(' ')
    
    # 总经理类
    if 'manager' in 岗位分割 and 'general' in 岗位分割:
        return 'GM', 0  # 返回类别和优先级
    
    # Director 和 Manager 类
    for keyword, priority in keywords_priority.items():
        # 关键词分割  'public relations'  'market'
        keyword_split=keyword.split(' ')
        director_ok=True
        manager_ok=True
        if 'director' in title:
            for word in keyword_split:
                if word in 岗位分割:pass
                else:director_ok=False
        else:director_ok=False
        if 'manager' in title:
            for word in keyword_split:
                if word not in 岗位分割:
                    manager_ok=False
        else:manager_ok=False
        if director_ok:
            return keyword, priority
        elif manager_ok:
            return keyword, priority + 10  # Manager 优先级低于 Director
                
    return None, 999  # 无匹配时返回最低优先级

def process_csv_files(csv_folder):
    """处理所有CSV文件并整合结果"""
    hotel_results = {}  # 用于存储每个酒店的职位信息
    
    # 遍历CSV文件夹中的所有文件
    for filename in os.listdir(csv_folder):
        if filename.endswith('.csv') and not filename.startswith('final_'):
            file_path = os.path.join(csv_folder, filename)
            
            # 读取CSV文件
            df = pd.read_csv(file_path)
            
            # 将NaN值替换为空字符串
            df['职位'] = df['职位'].fillna('')
            
            # 对每个职位进行评估 用于遍历 DataFrame 的每一行
            for _, row in df.iterrows():
                # 酒店名称
                hotel_name = row['酒店名称']
                # 职位类别 函数传入职位名称，返回职位类别
                position_category = categorize_position(row['职位'])
                
                # 存没有职位 没记录过的酒店
                if position_category: # 如果存在职位类别
                    category, priority = position_category  # 解包返回的类别和优先级
                    position_data = row.to_dict()
                    position_data['priority'] = priority  # 添加优先级
                    
                    # 初始化
                    if hotel_name not in hotel_results:
                        hotel_results[hotel_name] = { 
                            'GM': None, # 总经理
                            'Directors': [], # 总监
                            'Managers': {}  # 用于存储不同类型的Manager
                        }
                    # 总经理
                    if category == 'GM': # 如果职位类别是总经理
                        if not hotel_results[hotel_name]['GM']: # 如果酒店的总经理为空
                            hotel_results[hotel_name]['GM'] = position_data # 将当前行转换为字典并赋值给总经理
                    
                    # 总监
                    elif category in keywords_priority: 
                        if "director" in row['职位'].lower():
                            # 如果总监列表中没有这个职位
                            if not any(d['职位'] == row['职位'] for d in hotel_results[hotel_name]['Directors']):
                                hotel_results[hotel_name]['Directors'].append(position_data)
                            # 删除manager对应的
                            hotel_results[hotel_name]['Managers'].pop(category, None)
                        
                    # 替补manager
                        if "manager" in row['职位'].lower():
                            # 如果总监列表中没有这个职位
                            if not any(d['职位'] == row['职位'] for d in hotel_results[hotel_name]['Directors']):
                                hotel_results[hotel_name]['Managers'][category] = position_data
    # 整理结果为最终格式
    final_results = []
    for hotel_name, positions in hotel_results.items():
        # 添加GM记录（如果有）
        if positions['GM']:
            final_results.append(positions['GM'])
        
        # 创建一个集合来跟踪已经处理过的职位领域
        processed_areas = set()
        
        # 首先添加所有Director记录
        for director in positions['Directors']:
            final_results.append(director)
            # 从职位名称中提取领域
            title = director['职位'].lower()
            for keyword in ['market', 'pr', 'media', 'public relations', 'communication', 'sales']:
                if keyword in title:
                    processed_areas.add(keyword)
        
        # 对于没有Director的领域，使用对应的Manager
        sorted_managers = sorted(
            positions['Managers'].values(),
            key=lambda x: x['priority']
        )
        
        for manager in sorted_managers:
            title = manager['职位'].lower()
            # 检查这个Manager的领域是否已经有Director
            area_covered = False
            for keyword in ['market', 'pr', 'media', 'public relations', 'communication', 'sales']:
                if keyword in title and keyword not in processed_areas:
                    area_covered = True
                    processed_areas.add(keyword)
                    manager_data = manager.copy()
                    manager_data.pop('priority', None)
                    final_results.append(manager_data)
                    break
    
    # 创建最终的DataFrame
    if final_results:
        final_df = pd.DataFrame(final_results)
        # 确保只保留需要的列
        columns_to_keep = ['酒店名称', '电话号码', '姓名', '职位', '电子邮件']
        final_df = final_df[columns_to_keep]
        
        # 获取当前运行脚本的目录
        当前目录=os.path.dirname(os.path.abspath(__file__))
        output_path = os.path.join(当前目录, 'final_filtered_results.csv')
        final_df.to_csv(output_path, index=False)
        print(f"已将最终筛选结果保存到: {output_path}")
        return final_df
    else:
        print("没有找到符合条件的数据")
        return None

def 如果文件夹不存在则创建文件夹(文件夹路径):
    if not os.path.exists(文件夹路径):
        os.makedirs(文件夹路径)

def 测试当前文件使用(csv文件目录):
    results = process_csv_files(csv文件目录)
    if results is not None:
        print("\n处理完成的数据预览:")
        # 打印前5行
        print(results.head()) 

if __name__ == "__main__":
    测试当前文件使用(r'C:\Users\11923\Documents\GitHub\Learning-database\python_study\爬虫单子\生成邮件后的csv文件目录')

