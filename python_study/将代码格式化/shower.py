import subprocess
import time
from threading import Thread

import keyboard
import pyautogui
import pygame
import pyperclip
import win32gui
import win32con
from PySide2.QtUiTools import QUiLoader
from PySide2.QtWidgets import QApplication
import 将剪贴板的字格式化 as oprt
def 通过窗口进程id置顶窗口():
    win32gui.SetWindowPos(window_handle, win32con.HWND_TOPMOST, 0, 0, 0, 0,win32con.SWP_NOMOVE | win32con.SWP_NOACTIVATE | win32con.SWP_NOOWNERZORDER | win32con.SWP_SHOWWINDOW | win32con.SWP_NOSIZE)
def 通过窗口进程id取消置顶窗口():
    win32gui.SetWindowPos(window_handle, win32con.HWND_NOTOPMOST, 0, 0, 0, 0,win32con.SWP_SHOWWINDOW | win32con.SWP_NOSIZE | win32con.SWP_NOMOVE)
def 播放mp3(mp3文件路径='e.wav'):
    # 转音频 https://www.aconvert.com/cn/audio/
    pygame.mixer.init()
    sound = pygame.mixer.Sound(mp3文件路径)
    channel = pygame.mixer.Channel(0)  # 获取第 0 通道
    channel.play(sound)
    # 使用 channel.get_busy() 来检查音频是否仍在播放
    while channel.get_busy():
        pass
    sound.stop()
    pygame.mixer.quit()

def 开始操作(self):
    try:
        需求=self.ui.lineEdit_2.text()
        模型=self.ui.comboBox.currentText()
        oprt.将剪贴板的字格式化(需求,模型)
        回答=oprt.从剪贴板上获取最近文本()
        self.ui.lineEdit.setText(回答)
        把内容写入记事本(回答)
        播放mp3('aa.mp3')
    except Exception as e:
        print('发生了错误：',e)

def 输入组合键(*args):
    pyautogui.hotkey(args)
def 快捷键弄出代码块(self):
    输入组合键('ctrl','shift','k')
    输入组合键('down')
    文本=self.ui.comboBox_3.currentText()
    模拟键盘输入文本(文本)
    输入组合键('enter')
    输入组合键('up')
def 模拟键盘输入文本(文本):
    pyautogui.typewrite(文本)
def 监控鼠标左边活动主函数(self):
    from pynput import mouse
    def 监控鼠标左键活动(x, y, 按键消息, 是否按下):
        if 按键消息 == mouse.Button.left:
            if 是否按下:pass
                # 快捷键弄出代码块()
            else:return False  # 通过返回False来通知监听器停止
    listener = mouse.Listener(on_click=监控鼠标左键活动)
    listener.start()
    listener.join()
    快捷键弄出代码块(self)
def 加上代码块(self):
    监控鼠标左边活动主函数(self)
def 切换询问(self):
    现在的询问=self.ui.comboBox_2.currentText()
    self.ui.lineEdit_2.setText(现在的询问)
def 把内容写入记事本(内容, 记事本的路径="record.txt"):
    with open(记事本的路径, "w", encoding='utf-8') as file:
        file.write(内容)
def 打开记事本():
    import os
    if not os.path.exists("record.txt"):
        with open("record.txt", "w") as file:pass
    subprocess.run(["start", "record.txt"], shell=True)

def 检查10秒内是否有新的剪贴板内容():
    老剪贴板内容 = pyperclip.paste()  # 获取初始剪贴板内容
    新剪贴板内容 = None
    for i in range(100):
        新剪贴板内容 = pyperclip.paste()  # 每隔 0.1 秒检查剪贴板内容
        if 新剪贴板内容 != 老剪贴板内容:
            print("检测到新的剪贴板内容：", 新剪贴板内容)
            return True
        time.sleep(0.1)
    return False
def 开始监听按键并执行函数(快捷键, 函数名, 参数=None):  # 参数默认为None
    if 参数 is not None: keyboard.add_hotkey(快捷键, 函数名, args=(参数,))
    else: keyboard.add_hotkey(快捷键, 函数名)  # 如果没有参数，不传递args
    keyboard.wait("ctrl+esc")
def 自动检测剪贴板并操作(self):
    print('开始自动检测剪贴板并操作')
    if(检查10秒内是否有新的剪贴板内容()):
        print('开始执行操作')
        开始操作(self)
    else:print('超过10秒未检测到新的剪贴板内容')
# 不会阻塞 但是没返回值
def 多线程运行(func):
    def wrapper(*args, **kwargs):
        Thread(target=func, args=args, kwargs=kwargs).start()
    return wrapper

def 操作自动检测的(self):
    # 获取当前按钮内容
    按钮文本 = self.ui.pushButton_5.text()
    if 按钮文本=='关闭自动功能':
        keyboard.unhook_all()
        self.ui.pushButton_5.setText('开启自动功能')
    else:
        多线程运行(开始监听按键并执行函数)("shift+c", 自动检测剪贴板并操作,self)
        self.ui.pushButton_5.setText('关闭自动功能')
class Stats:
    def __init__(self):
        self.ui = QUiLoader().load('./ui/untitled.ui')
        self.ui.actiontxtfile.triggered.connect(打开记事本)
        self.ui.pushButton_3.clicked.connect(通过窗口进程id置顶窗口)
        self.ui.pushButton_2.clicked.connect(通过窗口进程id取消置顶窗口)
        self.ui.pushButton.clicked.connect(lambda:开始操作(self))
        self.ui.pushButton_4.clicked.connect(lambda:加上代码块(self))
        self.ui.comboBox_2.currentTextChanged.connect(lambda:切换询问(self))

        多线程运行(开始监听按键并执行函数)("shift+c",自动检测剪贴板并操作,self)
        self.ui.pushButton_5.clicked.connect(lambda: 操作自动检测的(self))


# currentText()
app = QApplication([])
stats = Stats()
window_handle = int(stats.ui.winId())
通过窗口进程id置顶窗口()
stats.ui.show()
app.exec_()
