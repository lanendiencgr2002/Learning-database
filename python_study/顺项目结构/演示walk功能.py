import os

def 演示walk功能(目标路径):
    print(f"正在遍历文件夹：{目标路径}\n")
    
    for 根路径, 子目录列表, 文件列表 in os.walk(目标路径):
        print(f"当前位置: {根路径}")
        print(f"├── 子目录: {子目录列表}")
        print(f"└── 文件: {文件列表}")
        print("-" * 50)

# 测试代码
测试路径 = r'''
C:\Users\11923\Documents\GitHub\Learning-database\python_study\顺项目结构
'''.strip()  # 使用当前目录作为测试
演示walk功能(测试路径)