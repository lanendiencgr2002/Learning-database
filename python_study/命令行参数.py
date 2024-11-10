# argparse 是 Python 的标准库，用于处理命令行参数
# 它可以让我们轻松地编写用户友好的命令行界面
import argparse  

class StudentInfo:
    def __init__(self):
        # ArgumentParser 是一个解析器，用于解析命令行参数
        # description 参数用于描述这个程序的用途
        self.parser = argparse.ArgumentParser(description='处理学生信息')
        
        # add_argument() 方法用于添加命令行参数
        # --name 这样的参数称为"可选参数"，运行时可以选择是否传入
        # default: 设置参数的默认值，如果用户没有传入该参数，就使用默认值
        # help: 参数的说明文字，当用户使用 -h 或 --help 时会显示
        self.parser.add_argument('--name', default='张三', help='学生姓名')
        
        # type=int 表示这个参数的值会被转换成整数
        # 如果用户输入的不是数字，程序会报错
        self.parser.add_argument('--age', default=18, type=int, help='学生年龄')
        
        # type=float 表示这个参数的值会被转换成浮点数
        self.parser.add_argument('--score', default=80, type=float, help='考试分数')
        
        # parse_args() 方法会解析命令行参数
        # 解析后的参数会存储在 self.args 中
        self.args = self.parser.parse_args()
    
    def show_info(self):
        """显示学生信息"""
        print(f"\n学生信息:")
        # 通过 self.args.参数名 的方式访问参数值
        print(f"姓名: {self.args.name}")
        print(f"年龄: {self.args.age}岁")
        print(f"分数: {self.args.score}分")

# 当直接运行这个文件时，__name__ 的值为 '__main__'
# 这样可以确保这段代码只在直接运行时执行，而不是在被导入时执行
if __name__ == '__main__':
    student = StudentInfo()
    student.show_info()

# 运行方式示例:
# 1. 使用默认值运行：
#    python 命令行参数.py
# 
# 2. 传入部分参数：
#    python 命令行参数.py --name 李四 --age 20
#
# 3. 查看帮助信息：
#    python 命令行参数.py --help
#
# 4. 传入所有参数：
#    python 命令行参数.py --name 李四 --age 20 --score 95.5