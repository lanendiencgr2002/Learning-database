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
    开始时间=time.time()
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
    # 开始时间
    start_time = time.time()

    # 执行每一条记录
    for command in command_read:
        # 计算应该等待的时间     等待时间=记录时间-当前时间
        wait_time = command[2] - (time.time() - start_time)
        if wait_time > 0:
            time.sleep(wait_time)
        print('当前指令时间：',time.time()-开始时间)
        # 执行命令
        if command[0] == "click":
            mouse.position = (command[1][0], command[1][1])
            time.sleep(0.1)
            mouse.click(buttons[command[1][2]])
        elif command[0] == "double-click":
            mouse.position = (command[1][0], command[1][1])
            time.sleep(0.1)
            mouse.click(buttons[command[1][2]], 2)
        elif command[0] == "press":
            if command[1][0][:3] == "Key":
                keyboard.press(eval(command[1][0], {}, {"Key": pynput.keyboard.Key}))
            else:
                if "<255>" != command[1][0]:
                    keyboard.press(command[1][0])
        elif command[0] == "release":
            if command[1][0][:3] == "Key":
                keyboard.release(eval(command[1][0], {}, {"Key": pynput.keyboard.Key}))
            else:
                if "<255>" != command[1][0]:
                    keyboard.release(command[1][0])

import os  # 用于文件操作
import time  # 用于记录每一项操作的时间
import json  # 用于保存导出我们记录的操作
import threading  # 由于键盘和鼠标事件的监听都是阻塞的,所以用两个线程实现
import tkinter  # 绘制操作界面
from tkinter import messagebox
import pynput  # 用于记录用户事件
import ctypes

PROCESS_PER_MONITOR_DPI_AWARE = 2  # 解决由于屏幕分辨率缩放导致的，pynput监听鼠标位置不准的问题
ctypes.windll.shcore.SetProcessDpiAwareness(PROCESS_PER_MONITOR_DPI_AWARE)

command_list = []  # 用来存储用户的操作
command_read = []  # 用来读取录制的操作
isRunning = True  # 是否在运行,用于实现在按esc后退出的功能
startTime = 0  # 开始时间,会在之后main函数中进行初始化
mouse_x_old = 0
mouse_y_old = 0
mouse_t_old = 0

# 插入录制部分代码

# 插入执行部分代码

class TKDemo:
    def __init__(self):
        self.top = tkinter.Tk()
        self.top.title('鼠标宏录制工具')
        self.top.geometry('500x250')

        frame1 = tkinter.Frame(self.top)
        frame1.pack(side='top')
        l1 = tkinter.Label(frame1,
                           text='【1----录制操作】\n注意：按Esc退出录制，暂不支持键盘组合键')
        l1.pack()
        b1 = tkinter.Button(frame1,
                            text='录制',
                            width=15, height=2,
                            command=self.recordOp)
        b1.pack()
        frame2 = tkinter.Frame(self.top)
        frame2.pack(side='bottom')
        l2 = tkinter.Label(frame2,
                           text='【2----执行操作】')
        l2.pack()
        b2 = tkinter.Button(frame2,
                            text='执行',
                            width=15, height=2,
                            command=self.execOp)
        b2.pack()
        l3 = tkinter.Label(frame2,
                           text='请输入执行次数，默认为1次')
        l3.pack()
        self.count = tkinter.StringVar()
        e1 = tkinter.Entry(frame2, textvariable=self.count)
        e1.pack()

        self.top.mainloop()

    def recordOp(self):
        self.top.iconify()  # 窗口隐藏
        global startTime
        startTime = time.time()  # 初始化开始时间
        key_listen_thread = threading.Thread(target=start_key_listen)  # 创建用于监听按键的线程
        mouse_listen_thread = threading.Thread(target=start_mouse_listen)  # 创建用于监听鼠标的线程
        # 运行线程
        key_listen_thread.start()
        mouse_listen_thread.start()
        # 等待线程结束,也就是等待用户按下esc
        key_listen_thread.join()
        mouse_listen_thread.join()
        # 记录成功之后执行下列操作
        toFile(command_list, "./commands.json")  # 保存文件

        global isRunning
        isRunning = True  # 初始化记录状态
        command_list.clear()  # 清空列表
        self.top.deiconify()  # 窗口显现
        print("记录成功！")
        tkinter.messagebox.showinfo('提示', '记录成功！')

    def execOp(self):
        self.top.iconify()  # 窗口隐藏
        path = 'commands.json'
        count = self.count.get()
        if count.isdigit():
            for i in range(int(count)):
                ExecuteCommandsFile(path)
            print("执行成功%d次!" % (int(count)))
            tkinter.messagebox.showinfo('提示', "执行完毕！\n共%d次！" % (int(count)))
        elif len(count) == 0:
            ExecuteCommandsFile(path)
            print("执行成功1次!")
            tkinter.messagebox.showinfo('提示', '执行完毕！\n共1次！')
        else:
            print("执行失败！请键入数字")
            tkinter.messagebox.showerror('提示', '执行失败！\n请键入数字！')
        self.top.deiconify()  # 窗口显现

def main():  # 主函数
    TKDemo()


if __name__ == "__main__":
    main()