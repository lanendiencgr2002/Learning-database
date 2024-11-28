from DrissionPage import ChromiumOptions, ChromiumPage
import time
import os

def 初始化dp():
    co = ChromiumOptions().set_local_port(9222)
    co.set_timeouts(base=15)
    page = ChromiumPage(addr_or_opts=co)
    return page
page=初始化dp()

def 搜索更多酒店():
    搜索更多酒店元素=page.ele('text:搜索更多酒店')
    if 搜索更多酒店元素:
        print('搜索更多酒店元素存在')
        page.ele('text:搜索更多酒店').click()
    else:
        print('搜索更多酒店元素不存在')

def 获取一个酒店列表():
    酒店列表元素=page.ele('.name font-bold')
    if 酒店列表元素:
        print('酒店列表元素存在')
        print(酒店列表元素.text)
    else:
        print('酒店列表元素不存在')

def 获取所有酒店列表():
    酒店列表元素=page.eles('.name font-bold')
    for i in 酒店列表元素:
        print(i.text)
        写文件(i.text)

def 写文件(内容):
    当前目录=os.path.dirname(os.path.abspath(__file__))

    if not os.path.exists(os.path.join(当前目录,'酒店列表.txt')):
        with open(os.path.join(当前目录,'酒店列表.txt'), 'w', encoding='utf-8') as file:
            file.write('')

    with open(os.path.join(当前目录,'酒店列表.txt'), 'a', encoding='utf-8') as file:
        file.write(内容+'\n')

def 处理error(tab):
    try:
        元素=tab.ele('text:Error',timeout=.3)
        if 元素:
            return True
        else:
            return False
    except:
        return False

def needmorecredits(tab):
    try:
        条件='text:need more credits'
        元素=tab.ele(条件,timeout=2)
        if 元素:
            return True
        else:
            return False
    except:
        return False

if __name__ == '__main__':
    # 获取所有酒店列表()
    print(needmorecredits(page))