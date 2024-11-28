# 先执行 然后再开始算时间
def unicode_convert(input_data):
    # 将unicode转换成str
    if isinstance(input_data, dict):
        return {unicode_convert(key): unicode_convert(value) for key, value in input_data.iteritems()}
    elif isinstance(input_data, list):
        return [unicode_convert(element) for element in input_data]
    elif isinstance(input_data, str):
        return input_data
    else:
        return input_data


def ExecuteCommandsFile(path):
    # 4 5 6
    # 会先执行4秒的 然后睡4秒 然后执行5 然后睡1秒 然后执行6
    # 如果命令行传入了参数,则使用命令行参数,否则提示用户输入,此变量表示操作记录文件的路径
    # 第二个不是:,也就代表路径是相对路径
    path = unicode_convert(path)

    if path[2] != ":":
        # 将其解析为从本文件开始的路径
        path = os.path.join(os.path.dirname(__file__), path)

    # 打开文件
    with open(path) as f:
        # 将记录的命令写入命令列表
        command_read = json.loads(f.read())
    command_read = unicode_convert(command_read)
    # 创建鼠标和键盘的执行器,用于模拟键盘和鼠标的操作
    mouse = pynput.mouse.Controller()
    keyboard = pynput.keyboard.Controller()
    # 鼠标的两个按钮
    buttons = {
        "Button.left": pynput.mouse.Button.left,
        "Button.right": pynput.mouse.Button.right
    }
    # 开始后已经经过的时间
    sTime = 0
    # 执行每一条记录
    for command in command_read:
        # 如果是点击记录
        # print(command[0],command[1],command[2])
        # 如果是单击
        if command[0] == "click":
            # 将鼠标移动到记录中的位置
            # mouse.position = (command[1][0], command[1][1])
            # 等待一下
            time.sleep(0.1)
            # 点击
            mouse.click(buttons[command[1][2]])
        # 如果是双击
        elif command[0] == "double-click":
            # 将鼠标移动到记录中的位置
            mouse.position = (command[1][0], command[1][1])
            # 等待一下
            time.sleep(0.1)
            # 双击
            mouse.click(buttons[command[1][2]], 2)
        # 如果是按键按下
        elif command[0] == "press":
            # 如果是特殊按键,会记录成Key.xxx,这里判断是不是特殊按键
            if command[1][0][:3] == "Key":
                # 按下按键
                keyboard.press(eval(command[1][0], {}, {
                    "Key": pynput.keyboard.Key
                }))
            else:
                # 如果是普通按键,直接按下
                if "<255>" == command[1][0]:
                    continue
                # print(command[1][0])
                # print(command[1][0].split("'")[1])
                # keyboard.press(command[1][0].split("'")[1])
                keyboard.press(command[1][0])
        # 如果是按键释放
        elif command[0] == "release":
            # 如果是特殊按键
            if command[1][0][:3] == "Key":
                # 按下按键
                keyboard.release(eval(command[1][0], {}, {
                    "Key": pynput.keyboard.Key
                }))
            else:
                # 普通按键直接按下
                if "<255>" == command[1][0]:
                    continue
                # print(command[1][0])
                # print(command[1][0].split("'")[1])
                # keyboard.release(command[1][0].split("'")[1])
                keyboard.release(command[1][0])
        # command[2]代表此操作距离开始操作所经过的时间,用它减去已经经过的时间就是距离下一次操作的时间
        time.sleep(command[2] - sTime)
        print('已等待：',command[2] - sTime)
        # 更新时间
        sTime = command[2]