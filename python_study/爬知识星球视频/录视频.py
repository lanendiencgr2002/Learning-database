import pyautogui
import time  # 添加time模块导入


def 快捷键按下(*args: str):
    pyautogui.hotkey(*args)

def 开始录视频():
    快捷键按下('ctrl', 'f1')

def 停止录视频():
    快捷键按下('ctrl', 'f2')

if __name__ == '__main__':
    开始录视频()
    time.sleep(5)
    停止录视频()
