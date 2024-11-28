import cv2, numpy as np, pyautogui, keyboard

def 按下热键自动找模板点击(按键):
    全屏截图 = cv2.cvtColor(np.array(pyautogui.screenshot()), cv2.COLOR_RGB2GRAY)
    按键操作 = {
        # '按键':lambda:按下按键后执行的函数
        '1': lambda: 找模板且点击("a.png", 全屏截图),
    }
    操作 = 按键操作.get(按键.name)
    # 处理没找到模板，操作
    if 操作:
        if 操作():pass
        else:print(f"没找到{按键.name}对应的模板")
    else:print(f"没找到{按键.name}对应的操作")
def 找模板且点击(模板路径, 截图=cv2.cvtColor(np.array(pyautogui.screenshot()), cv2.COLOR_RGB2GRAY), 阈值=0.915,延时=0.2):
    模板 = cv2.imread(模板路径, 0)
    匹配结果 = cv2.matchTemplate(截图, 模板, cv2.TM_CCOEFF_NORMED)
    _, 最大值, _, 最大位置 = cv2.minMaxLoc(匹配结果)
    if 最大值 >= 阈值:
        高, 宽 = 模板.shape
        中心 = (最大位置[0] + 宽 // 2, 最大位置[1] + 高 // 2)
        pyautogui.click(中心,duration=0.4)
        return True
    # print(f"没找到按键，匹配值：{max_val}")
    return False
def 拖动鼠标(方向, 持续时间=0.2):
    方向设置 = {
        'left': (1846, 526, -450, 0),
        'right': (1366, 513, 450, 0),
        'down': (1605, 538, 0, -250),
        'up': (1605, 538, 0, 250)
    }
    x, y, dx, dy = 方向设置.get(方向, (0, 0, 0, 0))
    pyautogui.mouseDown(x, y)
    pyautogui.moveTo(x + dx, y + dy, 持续时间)
    pyautogui.mouseUp()
    return True
if __name__ == "__main__":
    pyautogui.PAUSE, pyautogui.FAILSAFE = 0.1, True
    keyboard.on_press(按下热键自动找模板点击)
    keyboard.wait('p')
