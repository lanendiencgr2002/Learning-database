import time

import pyautogui
import pyautogui
import pydirectinput


class pyautogui操作类:
    def __init__(self, 报错=True, 停顿时间=0):
        if 报错:pyautogui.FAILSAFE = False
        if 停顿时间 != 0:pyautogui.PAUSE = 停顿时间
    def 获取显示器的分辨率(self):return pyautogui.size()
    def 鼠标移动(self,x,y,时间=.3):pyautogui.moveTo(x, y, duration=时间)
    def 获取当前鼠标位置(self):return pyautogui.position()
    def 鼠标按下(self):pyautogui.mouseDown()
    def 鼠标释放(self):pyautogui.mouseUp()
    def 鼠标拖动(self,x,y,时间延时=.3):pyautogui.dragTo(x,y,duration=时间延时)
    def 鼠标滚动(self,鼠标滚动单位:int):pyautogui.scroll(鼠标滚动单位) # 取决单位方向操作系统
    def 鼠标单击(self,x,y,哪个键='左键'):
        if 哪个键=='左键':pyautogui.click(x,y,button='left')
        if 哪个键 == '右键': pyautogui.click(x, y, button='right')
        if 哪个键 == '中间': pyautogui.click(x, y, button='middle')
    def 鼠标双击(self,x,y,哪个键='左键'):
        if 哪个键=='左键':pyautogui.doubleClick(x,y)
        if 哪个键 == '右键': pyautogui.rightClick(x,y)
        if 哪个键 == '中间': pyautogui.middleClick(x,y)
    def 鼠标按方向拖动(self,左右,上下,时间延时=.3):pyautogui.dragRel(左右,上下,duration=时间延时)
    def 按下键(self,键:str):pyautogui.keyDown(键)
    def 释放键(self,键:str):pyautogui.keyUp(键)
    def 按下并释放键(self,键:str):pyautogui.press(键)
    def 键盘文本输入(self,键盘英文文本:str,时间延时=.01):pyautogui.typewrite(键盘英文文本, 时间延时)
    def 键盘列表输入(self,按键列表=['T','i','s','left','left','h',]):pyautogui.typewrite(按键列表)
    def 快捷键按下(self,*args:str):pyautogui.hotkey(args)
    def 获取截图pil对象(self):return pyautogui.screenshot()
    def 保存截图(self,left=999999, top=999999, right=999999, bottom=999999): # 传左上右下 传元组时可以用*打散开来
        if left==999999 or top==999999 or right==999999 or bottom==999999:
            pyautogui.screenshot().save('全屏截图.png')
        else:pyautogui.screenshot(region=(left, top, right - left, bottom - top)).save('区域截图.png')
    def 返回某点像素颜色(self,img,x,y):return img.getpixel((x,y))
    def 获取找图坐标(self,图片路劲):
        try:
            找的图片的位置= pyautogui.locateCenterOnScreen(图片路劲,confidence=0.66)
            return 找的图片的位置
        except:return None
    def 直接按下键(self,键:str):pydirectinput.keyDown(键)
    def 直接释放键(self,键:str):pydirectinput.keyUp(键)
    def 直接按下并释放键(self,键:str):
        pydirectinput.keyDown(键)
        time.sleep(0.05)
        pydirectinput.keyUp(键)
        time.sleep(0.05)
pyautogui操作类=pyautogui操作类()
def 点击开始游戏():
    try:
        坐标=pyautogui操作类.获取找图坐标('opengame.png')
        pyautogui操作类.鼠标单击(坐标[0],坐标[1])
        print('点击开始游戏 成功')
    except:print('点击开始游戏 失败')
def 点击排位模式():
    try:
        坐标 = pyautogui操作类.获取找图坐标('img.png')
        pyautogui操作类.鼠标单击(坐标[0], 坐标[1])
        print('点击排位模式 成功')
    except:
        print('点击排位模式 失败')
def 点击门返回():
    try:
        坐标 = pyautogui操作类.获取找图坐标('img.png')
        pyautogui操作类.鼠标单击(坐标[0], 坐标[1])
        print('点击门返回 成功')
    except:
        print('点击门返回 失败')
def 点击匹配():
    try:
        坐标 = pyautogui操作类.获取找图坐标('img_2.png')
        pyautogui操作类.鼠标单击(坐标[0], 坐标[1])
        print('点击匹配 成功')
    except:
        print('点击匹配 失败')
def 点击求生者():
    try:
        坐标 = pyautogui操作类.获取找图坐标('img_3.png')
        pyautogui操作类.鼠标单击(坐标[0], 坐标[1])
        print('点击求生者 成功')
    except:
        print('点击求生者 失败')
def 点击开始案件还原():
    try:
        坐标 = pyautogui操作类.获取找图坐标('img_4.png')
        pyautogui操作类.鼠标单击(坐标[0], 坐标[1])
        print('点击开始案件还原 成功')
    except:
        print('点击开始案件还原 失败')
def 点击监管者():
    try:
        坐标 = pyautogui操作类.获取找图坐标('img_5.png')
        pyautogui操作类.鼠标单击(坐标[0], 坐标[1])
        print('点击监管者 成功')
    except:
        print('点击监管者 失败')
def 点击准备按键还原():
    try:
        坐标 = pyautogui操作类.获取找图坐标('img_6.png')
        pyautogui操作类.鼠标单击(坐标[0], 坐标[1])
        print('点击准备按键还原 成功')
    except:
        print('点击准备按键还原 失败')
def 点击准备开始():
    try:
        坐标 = pyautogui操作类.获取找图坐标('img_7.png')
        pyautogui操作类.鼠标单击(坐标[0], 坐标[1])
        print('点击准备 成功')
    except:
        print('点击准备 失败')
def 一直按wasd():
    try:
        while True:
            pydirectinput.keyDown('w')
            pydirectinput.keyDown('a')
            pydirectinput.keyUp('w')
            pydirectinput.keyDown('s')
            pydirectinput.keyUp('a')
            pydirectinput.keyDown('d')
            pydirectinput.keyUp('s')
            pydirectinput.keyUp('d')

            time.sleep(0.001)

    except KeyboardInterrupt:
        # 当用户按下 Ctrl+C 组合键时，终止循环
        print("停止按键模拟")

# 点击准备开始()a
# 点击准备按键还原()
time.sleep(3)
print('开始')
一直按wasd()

