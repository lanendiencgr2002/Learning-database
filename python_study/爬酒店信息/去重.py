import pandas as pd
import os

def remove_duplicates(file_name):
    # 获取文件路径
    current_dir = os.path.dirname(os.path.abspath(__file__))
    input_file = os.path.join(current_dir, file_name)
    
    # 读取CSV文件
    df = pd.read_csv(input_file)
    
    # 去除完全重复的行
    df_no_duplicates = df.drop_duplicates()
    
    # 生成输出文件名（在原文件名基础上加上"_no_duplicates"）
    file_name_without_ext = os.path.splitext(file_name)[0]
    output_file = os.path.join(current_dir, f"{file_name_without_ext}_no_duplicates.csv")
    
    # 保存去重后的数据
    df_no_duplicates.to_csv(output_file, index=False)
    
    # 打印去重信息
    duplicates_removed = len(df) - len(df_no_duplicates)
    print(f"原始行数: {len(df)}")
    print(f"去重后行数: {len(df_no_duplicates)}")
    print(f"删除的重复行数: {duplicates_removed}")
    print(f"去重后的文件已保存为: {output_file}")

def 给文件夹下的所有文件去重(文件目录):
    for file in os.listdir(文件目录):
        if file.endswith(".csv"):
            remove_duplicates(file)

if __name__ == "__main__":
    # 处理指定的CSV文件
    file_name = "result_Grand Hyatt Kuala Lumpur_1.csv"
    remove_duplicates(file_name)
