import cv2, numpy as np, pyautogui, keyboard
def handle_key_press(key):
    screenshot = cv2.cvtColor(np.array(pyautogui.screenshot()), cv2.COLOR_RGB2GRAY)
    key_actions = {
        '1': lambda: find_and_click("a.png", screenshot),
        '2': lambda: find_and_click("b.png", screenshot),
        '3': lambda: find_and_click("c.png", screenshot),
        '4': lambda: find_and_click("d.png", screenshot),
        'space': lambda: find_and_click("certain.png", screenshot),
        'f': lambda: find_and_click("f.png", screenshot),
        's': lambda: find_and_click("s.png", screenshot) or find_and_click("s1.png", screenshot),
        'left': lambda: drag_mouse('right'),
        'right': lambda: drag_mouse('left'),
        'up': lambda: drag_mouse('up'),
        'down': lambda: drag_mouse('down')
    }
    action = key_actions.get(key.name)
    if action: #如果找到了对应操作，那么就操作
        if action():pass # print(f"按下了{key.name}")
        else:print(f"没找到{key.name}对应的模板")
    else:print(f"没找到{key.name}对应的操作")
def find_and_click(template_path, screenshot, threshold=0.915):
    template = cv2.imread(template_path, 0)
    result = cv2.matchTemplate(screenshot, template, cv2.TM_CCOEFF_NORMED)
    _, max_val, _, max_loc = cv2.minMaxLoc(result)
    if max_val >= threshold:
        h, w = template.shape
        center = (max_loc[0] + w // 2, max_loc[1] + h // 2)
        pyautogui.click(center)
        return True
    # print(f"没找到按键，匹配值：{max_val}")
    return False
def drag_mouse(direction, duration=0.2):
    directions = {
        'left': (1846, 526, -450, 0),
        'right': (1366, 513, 450, 0),
        'down': (1605, 538, 0, -250),
        'up': (1605, 538, 0, 250)
    }
    x, y, dx, dy = directions.get(direction, (0, 0, 0, 0)) # 没找到就是(0, 0, 0, 0)
    pyautogui.mouseDown(x, y)
    pyautogui.moveTo(x + dx, y + dy, duration)
    pyautogui.mouseUp()
    return True
pyautogui.PAUSE, pyautogui.FAILSAFE = 0.1, True
keyboard.on_press(handle_key_press)
keyboard.wait('p')