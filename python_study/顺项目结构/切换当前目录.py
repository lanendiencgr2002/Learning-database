import os

# 获取当前脚本的绝对路径
current_path = os.path.abspath(__file__)
# 获取脚本所在目录
script_dir = os.path.dirname(current_path)
# 切换到脚本所在目录
os.chdir(script_dir)
print('当前目录切换成功',script_dir)