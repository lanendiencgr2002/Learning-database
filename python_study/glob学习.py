import glob
import os

def 创建示例文件():
    """创建测试文件"""
    os.makedirs('测试文件夹/子文件夹', exist_ok=True)
    
    # 创建测试文件
    测试文件 = [
        '测试文件夹/test1.txt',
        '测试文件夹/test2.txt',
        '测试文件夹/pic1.jpg',
        '测试文件夹/video1.mp4',
        '测试文件夹/子文件夹/test3.txt'
    ]
    
    for 文件 in 测试文件:
        with open(文件, 'w') as f:
            f.write('测试文件')

def glob演示():
    """glob模块基本用法演示"""
    # 基本匹配 - 查找所有txt文件
    print('\n1. *.txt:', glob.glob('测试文件夹/*.txt'))
    
    # 递归查找 - 包含子目录的所有txt
    print('\n2. **/*.txt:', glob.glob('测试文件夹/**/*.txt', recursive=True))
    
    # 多类型匹配 - 查找jpg和mp4
    print('\n3. 多类型:', glob.glob('测试文件夹/*.jpg') + glob.glob('测试文件夹/*.mp4'))

if __name__ == '__main__':
    创建示例文件()
    glob演示()
    
    # 清理文件
    if input('\n删除测试文件？(y/n): ').lower() == 'y':
        import shutil
        shutil.rmtree('测试文件夹')