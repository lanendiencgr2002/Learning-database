import pyautogui    
import pydirectinput  
import time
class pyautogui操作类:
    """
    pyautogui操作类 - 封装了pyautogui和pydirectinput的常用操作
    """
    def __init__(self, 报错=True, 停顿时间=0):
        """
        初始化pyautogui操作类
        Args:
            报错: 是否禁用pyautogui的失败保护机制,默认True
            停顿时间: pyautogui操作间的停顿时间,默认0
        """
        if 报错:pyautogui.FAILSAFE = False
        if 停顿时间 != 0:pyautogui.PAUSE = 停顿时间

    def 获取显示器的分辨率(self):
        """获取显示器分辨率
        Returns:
            Size: 包含宽高的元组
        """
        return pyautogui.size()

    def 鼠标移动(self,x,y,时间=.3):
        """移动鼠标到指定坐标
        Args:
            x: x坐标
            y: y坐标 
            时间: 移动持续时间,默认0.3秒
        """
        pyautogui.moveTo(x, y, duration=时间)

    def 获取当前鼠标位置(self):
        """获取当前鼠标位置
        Returns:
            Point: 包含x,y坐标的元组
        """
        return pyautogui.position()

    def 鼠标按下(self):
        """按下鼠标左键"""
        pyautogui.mouseDown()

    def 鼠标释放(self):
        """释放鼠标左键"""
        pyautogui.mouseUp()

    def 鼠标拖动(self,x,y,时间延时=.3):
        """拖动鼠标到指定坐标
        Args:
            x: 目标x坐标
            y: 目标y坐标
            时间延时: 拖动持续时间,默认0.3秒
        """
        pyautogui.dragTo(x,y,duration=时间延时)

    def 鼠标滚动(self,鼠标滚动单位:int):
        """滚动鼠标滚轮
        Args:
            鼠标滚动单位: 滚动的单位数,正数向上滚动,负数向下滚动
        """
        pyautogui.scroll(鼠标滚动单位)

    def 鼠标单击(self,x,y,哪个键='左键'):
        """在指定位置单击鼠标
        Args:
            x: x坐标
            y: y坐标
            哪个键: 使用的鼠标按键,可选'左键','右键','中间',默认'左键'
        """
        if 哪个键=='左键':pyautogui.click(x,y,button='left')
        if 哪个键 == '右键': pyautogui.click(x, y, button='right')
        if 哪个键 == '中间': pyautogui.click(x, y, button='middle')

    def 鼠标双击(self,x,y,哪个键='左键'):
        """在指定位置双击鼠标
        Args:
            x: x坐标
            y: y坐标
            哪个键: 使用的鼠标按键,可选'左键','右键','中间',默认'左键'
        """
        if 哪个键=='左键':pyautogui.doubleClick(x,y)
        if 哪个键 == '右键': pyautogui.rightClick(x,y)
        if 哪个键 == '中间': pyautogui.middleClick(x,y)

    def 鼠标按方向拖动(self,左右,上下,时间延时=.3):
        """按相对方向拖动鼠标
        Args:
            左右: 水平移动距离,正数向右,负数向左
            上下: 垂直移动距离,正数向下,负数向上
            时间延时: 拖动持续时间,默认0.3秒
        """
        pyautogui.dragRel(左右,上下,duration=时间延时)

    def 按下键(self,键:str):
        """按下指定键
        Args:
            键: 键名
        """
        pyautogui.keyDown(键)

    def 释放键(self,键:str):
        """释放指定键
        Args:
            键: 键名
        """
        pyautogui.keyUp(键)

    def 按下并释放键(self,键:str):
        """按下并释放指定键
        Args:
            键: 键名
        """
        pyautogui.press(键)

    def 键盘文本输入(self,键盘英文文本:str,时间延时=.01):
        """输入英文文本
        Args:
            键盘英文文本: 要输入的英文文本
            时间延时: 每个字符间的延时,默认0.01秒
        """
        pyautogui.typewrite(键盘英文文本, 时间延时)

    def 键盘列表输入(self,按键列表=['T','i','s','left','left','h',]):
        """按顺序输入按键列表
        Args:
            按键列表: 要按顺序输入的按键列表
        """
        pyautogui.typewrite(按键列表)

    def 快捷键按下(self,*args:str):
        """按下组合键
        Args:
            *args: 要组合的按键列表
        """
        pyautogui.hotkey(args)

    def 获取截图pil对象(self):
        """获取屏幕截图
        Returns:
            Image: PIL图像对象
        """
        return pyautogui.screenshot()

    def 保存截图(self,left=999999, top=999999, right=999999, bottom=999999):
        """保存屏幕截图
        Args:
            left: 左边界坐标
            top: 上边界坐标
            right: 右边界坐标
            bottom: 下边界坐标
            不传参数则截取全屏
        """
        if left==999999 or top==999999 or right==999999 or bottom==999999:
            pyautogui.screenshot().save('全屏截图.png')
        else:pyautogui.screenshot(region=(left, top, right - left, bottom - top)).save('区域截图.png')

    def 返回某点像素颜色(self,img,x,y):
        """获取图像指定点的像素颜色
        Args:
            img: PIL图像对象
            x: x坐标
            y: y坐标
        Returns:
            tuple: RGB颜色值元组
        """
        return img.getpixel((x,y))

    def 获取找图坐标(self,图片路劲):
        """在屏幕上查找指定图片
        Args:
            图片路劲: 要查找的图片路径
        Returns:
            Point: 找到的图片中心坐标,未找到返回None
        """
        try:
            找的图片的位置= pyautogui.locateCenterOnScreen(图片路劲,confidence=0.66)
            return 找的图片的位置
        except:return None

    def 直接按下键(self,键:str):
        """使用pydirectinput直接按下按键
        Args:
            键: 按键名
        """
        pydirectinput.keyDown(键)

    def 直接释放键(self,键:str):
        """使用pydirectinput直接释放按键
        Args:
            键: 按键名
        """
        pydirectinput.keyUp(键)

    def 直接按下并释放键(self,键:str):
        """使用pydirectinput直接按下并释放按键
        Args:
            键: 按键名
        """
        pydirectinput.keyDown(键)
        pydirectinput.keyUp(键)

def 测试():
    操作类=pyautogui操作类()
    操作类.直接按下键('w')
    time.sleep(1)
    操作类.直接释放键('w')
if __name__ == '__main__':
    测试()