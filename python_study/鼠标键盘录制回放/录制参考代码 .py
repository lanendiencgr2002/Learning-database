def on_key_press(key):  # 当按键按下时记录
    if key == pynput.keyboard.Key.esc:  # 如果是esc
        global isRunning
        isRunning = False  # 通知监听鼠标的线程
        mouse = pynput.mouse.Controller()  # 获取鼠标的控制器
        mouse.click(pynput.mouse.Button.left)  # 通过模拟点击鼠标以执行鼠标的线程,然后退出监听.
        return False  # 监听函数return False表示退出监听12
    command_list.append((
        "press",  # 操作模式
        (str(key).strip("'"),),  # 具体按下的键,传进来的参数并不是一个字符串,而是一个对象,如果按下的是普通的键,会记录下键对应的字符,否则会使一个"Key.xx"的字符串
        time.time() - startTime  # 操作距离程序开始运行的秒数
    ))


def on_key_release(key):  # 但按键松开时记录
    command_list.append((
        "release",  # 操作模式
        (str(key).strip("'"),),  # 键信息,参见on_key_press中的相同部分
        time.time() - startTime  # 操作距离程序开始运行的秒数
    ))


def on_mouse_click(x, y, button, pressed):
    # 用到旧的坐标以用于判断有没有双击
    global mouse_x_old
    global mouse_y_old
    global mouse_t_old
    if not isRunning:  # 如果已经不在运行了
        return False  # 退出监听
    if not pressed:  # 如果是松开事件
        return True  # 不记录
    if mouse_x_old == x and mouse_y_old == y:
        if time.time() - mouse_t_old > 0.3:  # 如果两次点击时间小于0.3秒就会判断为双击 否则就是单击
            command_list.append((
                "click",  # 操作模式
                (x, y, str(button)),  # 分别是鼠标的坐标和按下的按键
                time.time() - startTime  # 操作距离程序开始运行的秒数
            ))
        else:
            command_list.pop(0)  # 删除前一个
            command_list.append((
                "double-click",  # 操作模式
                (x, y, str(button)),  # 分别是鼠标的坐标和按下的按键
                time.time() - startTime  # 操作距离程序开始运行的秒数
            ))
    else:
        command_list.append((
            "click",  # 操作模式
            (x, y, str(button)),  # 分别是鼠标的坐标和按下的按键
            time.time() - startTime  # 操作距离程序开始运行的秒数
        ))
    mouse_x_old = x
    mouse_y_old = y
    mouse_t_old = time.time()


def start_key_listen():  # 用于开始按键的监听
    # 进行监听
    with pynput.keyboard.Listener(on_press=on_key_press, on_release=on_key_release) as listener:
        listener.join()


def start_mouse_listen():  # 用于开始鼠标的监听
    # 进行监听
    with pynput.mouse.Listener(on_click=on_mouse_click) as listener:
        listener.join()


def toFile(command_list, path):  # 保存为文件,参数分别为操作记录和保存位置
    with open(path, "w") as f:
        f.write(json.dumps(command_list))  # 使用json格式写入