import pyautogui    
import pydirectinput  
import time
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
        pydirectinput.keyUp(键)

def 测试():
    操作类=pyautogui操作类()
    操作类.直接按下键('w')
    time.sleep(1)
    操作类.直接释放键('w')
if __name__ == '__main__':
    测试()