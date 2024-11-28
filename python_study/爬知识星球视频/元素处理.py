import time

import pyautogui
from DrissionPage import ChromiumPage, ChromiumOptions
import 找模板且点击

co = ChromiumOptions().set_local_port(9222)
page = ChromiumPage(addr_or_opts=co)
page.timeout=3

def 获取视频列表():
    芋道项目视频=page.eles('text:芋道项目视频')
    return 芋道项目视频

def 获取视频元素():
    视频元素=page.ele('tag:video')
    return 视频元素


def 拉下所有视频列表():
    视频列表=获取视频列表()
    for 芋 in 视频列表:
        父=芋.parent()
        拉下标了=父.ele('.icon hide',timeout=1)
        if 拉下标了:
            拉下标了.click()
        print(芋.text)
    print('都拉下了')


def 获取元素中点(元素):
    x,y=元素.rect.midpoint
    return (int(x),int(y))
def 元素单击(元素):
    元素.click()
def 元素双击(元素):
    元素.click.multi(2)
def 放大or缩小视频(元素,暂停中=True):
    # 在暂停的情况下，不然不行
    if 暂停中:
        元素单击(元素)
        元素双击(元素)
    else:
        元素单击(元素)
        time.sleep(1)
        元素单击(元素)
        元素双击(元素)
    time.sleep(1)
def pyautogui点击(x,y):
    pyautogui.click(x,y)
if __name__=='__main__':
    pass
    # 视频列表=获取视频列表()
    # 拉下所有视频列表()
    视频元素=获取视频元素()
    # 找模板且点击.找模板且点击('quan2_ping2.png')
    放大or缩小视频(视频元素,暂停中=False)
    # 元素单击(视频元素)
    

   

