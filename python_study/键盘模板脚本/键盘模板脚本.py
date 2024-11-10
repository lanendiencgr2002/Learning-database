import cv2, numpy as np, pyautogui, keyboard

def 处理按键(按键):
    屏幕截图 = cv2.cvtColor(np.array(pyautogui.screenshot()), cv2.COLOR_RGB2GRAY)
    按键动作 = {
        '1': lambda: 查找并点击("a.png", 屏幕截图),
        '2': lambda: 查找并点击("b.png", 屏幕截图),
        '3': lambda: 查找并点击("c.png", 屏幕截图),
        '4': lambda: 查找并点击("d.png", 屏幕截图),
        'space': lambda: 查找并点击("certain.png", 屏幕截图),
        'f': lambda: 查找并点击("f.png", 屏幕截图),
        's': lambda: 查找并点击("s.png", 屏幕截图) or 查找并点击("s1.png", 屏幕截图),
        'left': lambda: 拖动鼠标('右'),
        'right': lambda: 拖动鼠标('左'),
        'up': lambda: 拖动鼠标('上'),
        'down': lambda: 拖动鼠标('下')
    }
    动作 = 按键动作.get(按键.name)
    if 动作:  # 如果找到了对应操作，那么就执行
        if 动作(): pass
        else: print(f"没找到{按键.name}对应的模板")
    else: print(f"没找到{按键.name}对应的操作")

def 查找并点击(模板路径, 屏幕截图, 阈值=0.915):
    模板 = cv2.imread(模板路径, 0)
    结果 = cv2.matchTemplate(屏幕截图, 模板, cv2.TM_CCOEFF_NORMED)
    _, 最大值, _, 最大位置 = cv2.minMaxLoc(结果)
    if 最大值 >= 阈值:
        高, 宽 = 模板.shape
        中心点 = (最大位置[0] + 宽 // 2, 最大位置[1] + 高 // 2)
        pyautogui.click(中心点)
        return True
    return False

def 拖动鼠标(方向, 持续时间=0.2):
    方向设置 = {
        '左': (1846, 526, -450, 0),
        '右': (1366, 513, 450, 0),
        '下': (1605, 538, 0, -250),
        '上': (1605, 538, 0, 250)
    }
    起点x, 起点y, 位移x, 位移y = 方向设置.get(方向, (0, 0, 0, 0))  # 没找到就返回(0, 0, 0, 0)
    pyautogui.mouseDown(起点x, 起点y)
    pyautogui.moveTo(起点x + 位移x, 起点y + 位移y, 持续时间)
    pyautogui.mouseUp()
    return True

pyautogui.PAUSE, pyautogui.FAILSAFE = 0.1, True
keyboard.on_press(处理按键)
keyboard.wait('p')