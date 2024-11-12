import os
# 尝试切换当前目录 如果不行就忽略
try:    
    os.chdir('./顺项目结构')
except Exception as e:
    print('没关系可忽略，切换当前目录失败：', e)
