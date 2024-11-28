import cv2
import numpy as np
import tkinter as tk
from tkinter import ttk
from PIL import Image, ImageTk


class HSVFilterApp:
    def __init__(self, window, window_title):
        self.window = window
        self.window.title(window_title)

        # 读取图像
        self.img = cv2.imread('img.png')
        if self.img is None:
            print("Error: Image not found.")
            exit()

        # 调整图像大小
        self.img = cv2.resize(self.img, (int(self.img.shape[1] * 0.5), int(self.img.shape[0] * 0.5)))

        # 创建 HSV 值的变量
        self.h_min = tk.IntVar(value=0)
        self.h_max = tk.IntVar(value=180)
        self.s_min = tk.IntVar(value=0)
        self.s_max = tk.IntVar(value=255)
        self.v_min = tk.IntVar(value=0)
        self.v_max = tk.IntVar(value=255)

        # 创建控件
        self.create_widgets()

        # 更新图像
        self.update_image()

        self.window.mainloop()

    def create_widgets(self):
        # 创建标签和输入框
        ttk.Label(self.window, text="H Min:").grid(row=0, column=0)
        ttk.Entry(self.window, textvariable=self.h_min, width=5).grid(row=0, column=1)
        ttk.Button(self.window, text="+", command=lambda: self.adjust_value(self.h_min, 1, 0, 180)).grid(row=0,
                                                                                                         column=2)
        ttk.Button(self.window, text="-", command=lambda: self.adjust_value(self.h_min, -1, 0, 180)).grid(row=0,
                                                                                                          column=3)

        ttk.Label(self.window, text="H Max:").grid(row=1, column=0)
        ttk.Entry(self.window, textvariable=self.h_max, width=5).grid(row=1, column=1)
        ttk.Button(self.window, text="+", command=lambda: self.adjust_value(self.h_max, 1, 0, 180)).grid(row=1,
                                                                                                         column=2)
        ttk.Button(self.window, text="-", command=lambda: self.adjust_value(self.h_max, -1, 0, 180)).grid(row=1,
                                                                                                          column=3)

        ttk.Label(self.window, text="S Min:").grid(row=2, column=0)
        ttk.Entry(self.window, textvariable=self.s_min, width=5).grid(row=2, column=1)
        ttk.Button(self.window, text="+", command=lambda: self.adjust_value(self.s_min, 1, 0, 255)).grid(row=2,
                                                                                                         column=2)
        ttk.Button(self.window, text="-", command=lambda: self.adjust_value(self.s_min, -1, 0, 255)).grid(row=2,
                                                                                                          column=3)

        ttk.Label(self.window, text="S Max:").grid(row=3, column=0)
        ttk.Entry(self.window, textvariable=self.s_max, width=5).grid(row=3, column=1)
        ttk.Button(self.window, text="+", command=lambda: self.adjust_value(self.s_max, 1, 0, 255)).grid(row=3,
                                                                                                         column=2)
        ttk.Button(self.window, text="-", command=lambda: self.adjust_value(self.s_max, -1, 0, 255)).grid(row=3,
                                                                                                          column=3)

        ttk.Label(self.window, text="V Min:").grid(row=4, column=0)
        ttk.Entry(self.window, textvariable=self.v_min, width=5).grid(row=4, column=1)
        ttk.Button(self.window, text="+", command=lambda: self.adjust_value(self.v_min, 1, 0, 255)).grid(row=4,
                                                                                                         column=2)
        ttk.Button(self.window, text="-", command=lambda: self.adjust_value(self.v_min, -1, 0, 255)).grid(row=4,
                                                                                                          column=3)

        ttk.Label(self.window, text="V Max:").grid(row=5, column=0)
        ttk.Entry(self.window, textvariable=self.v_max, width=5).grid(row=5, column=1)
        ttk.Button(self.window, text="+", command=lambda: self.adjust_value(self.v_max, 1, 0, 255)).grid(row=5,
                                                                                                         column=2)
        ttk.Button(self.window, text="-", command=lambda: self.adjust_value(self.v_max, -1, 0, 255)).grid(row=5,
                                                                                                          column=3)

        # 创建图像显示区域
        self.canvas = tk.Canvas(self.window, width=self.img.shape[1], height=self.img.shape[0])
        self.canvas.grid(row=0, column=4, rowspan=6, padx=10, pady=10)

    def adjust_value(self, var, delta, min_val, max_val):
        var.set(max(min(var.get() + delta, max_val), min_val))
        self.update_image()

    def update_image(self):
        # 将 BGR 图像转换为 HSV
        hsv_img = cv2.cvtColor(self.img, cv2.COLOR_BGR2HSV)

        # 定义 HSV 范围
        lower_bound = np.array([self.h_min.get(), self.s_min.get(), self.v_min.get()])
        upper_bound = np.array([self.h_max.get(), self.s_max.get(), self.v_max.get()])

        # 创建掩膜
        mask = cv2.inRange(hsv_img, lower_bound, upper_bound)

        # 应用掩膜
        filtered_img = cv2.bitwise_and(self.img, self.img, mask=mask)

        # 转换颜色空间
        filtered_img_rgb = cv2.cvtColor(filtered_img, cv2.COLOR_BGR2RGB)

        # 将 NumPy 数组转换为 PIL 图像
        pil_img = Image.fromarray(filtered_img_rgb)

        # 将 PIL 图像转换为 Tkinter 图像
        tk_img = ImageTk.PhotoImage(image=pil_img)

        # 更新画布上的图像
        self.canvas.create_image(0, 0, anchor=tk.NW, image=tk_img)
        self.canvas.image = tk_img


# 创建主窗口并运行应用
root = tk.Tk()
app = HSVFilterApp(root, "HSV Filter")
