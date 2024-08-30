# -*- coding: utf-8 -*-  # 设置编码格式为 utf-8，确保兼容性
"""
Created on Wed Apr  8 12:14:29 2020  # 文件创建日期和时间

@author: pang  # 作者信息
"""

import cv2  # 导入 OpenCV 库，用于图像处理
import numpy as np  # 导入 NumPy 库，用于数组操作
import win32gui, win32ui, win32con, win32api  # 导入 win32 系列库，用于 Windows API 调用


def grab_screen(region=None):
    """
    截取屏幕的函数，可以选择截取特定区域或整个屏幕。

    Parameters:
    region (tuple): (left, top, right, bottom) 定义截取区域的元组。如果为 None，则截取整个屏幕。

    Returns:
    numpy.ndarray: 返回截取的屏幕图像。
    """

    hwin = win32gui.GetDesktopWindow()  # 获取桌面窗口的句柄

    if region:  # 如果指定了截取区域
        left, top, x2, y2 = region  # 解包区域参数
        width = x2 - left + 1  # 计算截取区域的宽度
        height = y2 - top + 1  # 计算截取区域的高度
    else:  # 如果未指定截取区域，则截取整个屏幕
        width = win32api.GetSystemMetrics(win32con.SM_CXVIRTUALSCREEN)  # 获取屏幕宽度
        height = win32api.GetSystemMetrics(win32con.SM_CYVIRTUALSCREEN)  # 获取屏幕高度
        left = win32api.GetSystemMetrics(win32con.SM_XVIRTUALSCREEN)  # 获取屏幕左上角 X 坐标
        top = win32api.GetSystemMetrics(win32con.SM_YVIRTUALSCREEN)  # 获取屏幕左上角 Y 坐标

    hwindc = win32gui.GetWindowDC(hwin)  # 获取窗口的设备上下文（DC）
    srcdc = win32ui.CreateDCFromHandle(hwindc)  # 从句柄创建设备上下文
    memdc = srcdc.CreateCompatibleDC()  # 创建兼容的内存设备上下文
    bmp = win32ui.CreateBitmap()  # 创建位图对象
    bmp.CreateCompatibleBitmap(srcdc, width, height)  # 根据窗口 DC 创建兼容的位图
    memdc.SelectObject(bmp)  # 将位图选择到内存 DC 中
    memdc.BitBlt((0, 0), (width, height), srcdc, (left, top), win32con.SRCCOPY)  # 从源 DC 复制位图到内存 DC

    signedIntsArray = bmp.GetBitmapBits(True)  # 获取位图的位数据
    img = np.fromstring(signedIntsArray, dtype='uint8')  # 将位数据转换成 NumPy 数组
    img.shape = (height, width, 4)  # 重塑数组形状为 (height, width, 4)

    srcdc.DeleteDC()  # 删除源 DC
    memdc.DeleteDC()  # 删除内存 DC
    win32gui.ReleaseDC(hwin, hwindc)  # 释放窗口的 DC
    win32gui.DeleteObject(bmp.GetHandle())  # 删除位图对象

    return img  # 返回截取的图像
