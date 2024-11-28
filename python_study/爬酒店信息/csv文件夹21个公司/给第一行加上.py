import os
import csv

def add_header_to_csv():
    # 获取脚本所在目录的绝对路径
    script_dir = os.path.dirname(os.path.abspath(__file__))
    # 切换到脚本所在目录
    os.chdir(script_dir)
    
    # 定义要添加的标题行
    header = ['酒店名称', '电话号码', '姓名', '职位', '电子邮件']
    
    # 获取当前目录下所有的CSV文件
    csv_files = [f for f in os.listdir('.') if f.endswith('.csv')]
    
    if not csv_files:
        print("当前目录下没有找到CSV文件！")
        print("当前目录：", os.getcwd())
        return
        
    for csv_file in csv_files:
        # 读取原始文件内容
        with open(csv_file, 'r', encoding='utf-8') as file:
            rows = list(csv.reader(file))
        
        # 写入新文件（包含标题行）
        with open(csv_file, 'w', encoding='utf-8', newline='') as file:
            writer = csv.writer(file)
            writer.writerow(header)
            writer.writerows(rows)
        
        print(f'已为 {csv_file} 添加标题行')

if __name__ == '__main__':
    add_header_to_csv()