# -*- coding: utf-8 -*-
"""
Created on Wed Apr  8 10:37:50 2020

@author: pang
"""

import ctypes  # 导入ctypes模块，用于调用C语言的动态链接库
import time  # 导入time模块，用于实现延时操作

# 获取SendInput函数，该函数用于模拟键盘输入
SendInput = ctypes.windll.user32.SendInput

# 定义一些键盘按键的扫描码
W = 0x11
A = 0x1E
S = 0x1F
D = 0x20

M = 0x32
J = 0x24
K = 0x25
LSHIFT = 0x2A
R = 0x13  # 用R代替识破
V = 0x2F

Q = 0x10
I = 0x17
O = 0x18
P = 0x19
C = 0x2E
F = 0x21

up = 0xC8
down = 0xD0
left = 0xCB
right = 0xCD

esc = 0x01

# 定义C语言结构体，用于模拟键盘输入
PUL = ctypes.POINTER(ctypes.c_ulong)


class KeyBdInput(ctypes.Structure):
    _fields_ = [("wVk", ctypes.c_ushort),  # 虚拟键码
                ("wScan", ctypes.c_ushort),  # 硬件扫描码
                ("dwFlags", ctypes.c_ulong),  # 动作标志
                ("time", ctypes.c_ulong),  # 时间戳
                ("dwExtraInfo", PUL)]  # 额外信息


class HardwareInput(ctypes.Structure):
    _fields_ = [("uMsg", ctypes.c_ulong),  # 消息
                ("wParamL", ctypes.c_short),  # WPARAM低位
                ("wParamH", ctypes.c_ushort)]  # WPARAM高位


class MouseInput(ctypes.Structure):
    _fields_ = [("dx", ctypes.c_long),  # X轴移动量
                ("dy", ctypes.c_long),  # Y轴移动量
                ("mouseData", ctypes.c_ulong),  # 鼠标数据
                ("dwFlags", ctypes.c_ulong),  # 动作标志
                ("time", ctypes.c_ulong),  # 时间戳
                ("dwExtraInfo", PUL)]  # 额外信息


class Input_I(ctypes.Union):
    _fields_ = [("ki", KeyBdInput),  # 键盘输入
                ("mi", MouseInput),  # 鼠标输入
                ("hi", HardwareInput)]  # 硬件输入


class Input(ctypes.Structure):
    _fields_ = [("type", ctypes.c_ulong),  # 输入类型
                ("ii", Input_I)]  # 输入数据


# 定义模拟按键按下的函数
def PressKey(hexKeyCode):
    extra = ctypes.c_ulong(0)  # 额外信息设为0
    ii_ = Input_I()  # 创建Input_I实例
    ii_.ki = KeyBdInput(0, hexKeyCode, 0x0008, 0, ctypes.pointer(extra))  # 设置键盘输入数据
    x = Input(ctypes.c_ulong(1), ii_)  # 创建Input实例
    ctypes.windll.user32.SendInput(1, ctypes.pointer(x), ctypes.sizeof(x))  # 发送输入


# 定义模拟按键释放的函数
def ReleaseKey(hexKeyCode):
    extra = ctypes.c_ulong(0)  # 额外信息设为0
    ii_ = Input_I()  # 创建Input_I实例
    ii_.ki = KeyBdInput(0, hexKeyCode, 0x0008 | 0x0002, 0, ctypes.pointer(extra))  # 设置键盘输入数据
    x = Input(ctypes.c_ulong(1), ii_)  # 创建Input实例
    ctypes.windll.user32.SendInput(1, ctypes.pointer(x), ctypes.sizeof(x))  # 发送输入


# 定义各种动作函数，调用PressKey和ReleaseKey实现
def defense():
    PressKey(M)
    time.sleep(0.05)
    ReleaseKey(M)


def attack():
    PressKey(J)
    time.sleep(0.05)
    ReleaseKey(J)


def go_forward():
    PressKey(W)
    time.sleep(0.4)
    ReleaseKey(W)


def go_back():
    PressKey(S)
    time.sleep(0.4)
    ReleaseKey(S)


def go_left():
    PressKey(A)
    time.sleep(0.4)
    ReleaseKey(A)


def go_right():
    PressKey(D)
    time.sleep(0.4)
    ReleaseKey(D)


def jump():
    PressKey(K)
    time.sleep(0.1)
    ReleaseKey(K)


def dodge():
    PressKey(R)
    time.sleep(0.1)
    ReleaseKey(R)


def lock_vision():
    PressKey(V)
    time.sleep(0.3)
    ReleaseKey(V)
    time.sleep(0.1)


def go_forward_QL(t):
    PressKey(W)
    time.sleep(t)
    ReleaseKey(W)


def turn_left(t):
    PressKey(left)
    time.sleep(t)
    ReleaseKey(left)


def turn_up(t):
    PressKey(up)
    time.sleep(t)
    ReleaseKey(up)


def turn_right(t):
    PressKey(right)
    time.sleep(t)
    ReleaseKey(right)


def F_go():
    PressKey(F)
    time.sleep(0.5)
    ReleaseKey(F)


def forward_jump(t):
    PressKey(W)
    time.sleep(t)
    PressKey(K)
    ReleaseKey(W)
    ReleaseKey(K)


def press_esc():
    PressKey(esc)
    time.sleep(0.3)
    ReleaseKey(esc)


def dead():
    PressKey(M)
    time.sleep(0.5)
    ReleaseKey(M)


if __name__ == '__main__':
    time.sleep(5)  # 延迟5秒，以便有时间切换到目标窗口
    time1 = time.time()  # 记录当前时间
    while (True):
        if abs(time.time() - time1) > 5:  # 如果已经过去5秒，跳出循环
            break
        else:
            PressKey(M)
            time.sleep(0.1)
            ReleaseKey(M)
            time.sleep(0.2)

    PressKey(W)
    time.sleep(0.4)
    ReleaseKey(W)
    time.sleep(1)

    PressKey(J)
    time.sleep(0.1)
    ReleaseKey(J)
    time.sleep(1)
