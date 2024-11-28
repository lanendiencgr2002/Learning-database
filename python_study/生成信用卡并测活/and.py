import random
import time
import 生成信用卡
import 测活
from DrissionPage import ChromiumPage
page = ChromiumPage()

# page.get('https://namso-gen.com/')
# page.new_tab(url='https://checker.top/')

# tab1是当前焦点页面
tab2=page.get_tab(url='namso-gen.com')
tab1=page.get_tab(url='checker.top')

# 生成信用卡例子 print(生成信用卡.生成信用卡(tab1))
while True:
    信用卡号们=生成信用卡.生成信用卡(tab2)
    测活接收=测活.测活(tab1,信用卡号们)
    if 测活接收:
        print('成功检测出信用卡：',测活接收.count('ive'),'张')
        with open('收集.txt','a',encoding='utf-8') as f:
            f.write(测活接收+'\n')






