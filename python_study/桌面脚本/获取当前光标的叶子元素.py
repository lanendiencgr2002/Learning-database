import win32api
import uiautomation as auto
import tkinter as tk
import time

def 点在元素内(x, y, element):
    rect = element.BoundingRectangle
    return rect.left <= x < rect.right and rect.top <= y < rect.bottom

def 获取当前光标的最子元素():
    x, y = win32api.GetCursorPos()
    element = auto.ControlFromPoint(x, y)
    while True:
        children = element.GetChildren()
        found_child = False
        for child in children:
            if 点在元素内(x, y, child):
                element = child
                found_child = True
                break
        if not found_child:
            return element

def 获取元素坐标(element):
    rect = element.BoundingRectangle
    return (rect.left, rect.top, rect.right, rect.bottom, 
            rect.right - rect.left, rect.bottom - rect.top)

def 获取当前光标的最子元素的坐标():
    return 获取元素坐标(获取当前光标的最子元素())

def 循环展示当前光标的最子元素():
    while True:
        leaf_element = 获取当前光标的最子元素()
        print("\n获取到的最子元素:", leaf_element)

        左, 上, 右, 下, 宽度, 高度 = 获取当前光标的最子元素的坐标()
        print(f"\n元素坐标:\n左上角: ({左}, {上})\n右下角: ({右}, {下})")
        print(f"宽度: {宽度}\n高度: {高度}")

        绘制透明红色线框(左, 上, 右, 下)
        
        time.sleep(1)  # 暂停1秒

def 绘制透明红色线框(左, 上, 右, 下):
    原始线宽 = 2
    新线宽 = 原始线宽 * 3  # 将线宽增加三倍

    root = tk.Tk()
    root.overrideredirect(True)
    root.attributes('-alpha', 0.3)  # 设置透明度
    root.attributes('-transparentcolor', 'white')  # 将白色设为透明
    root.geometry(f"{右-左+新线宽}x{下-上+新线宽}+{左-新线宽//2}+{上-新线宽//2}")
    
    canvas = tk.Canvas(root, bg='white', highlightthickness=0)
    canvas.pack(fill=tk.BOTH, expand=True)
    canvas.create_rectangle(新线宽//2, 新线宽//2, 右-左+新线宽//2, 下-上+新线宽//2, outline='red', width=新线宽)
    
    root.lift()
    root.attributes('-topmost', True)
    
    root.after(1000, root.destroy)  # 1秒后自动关闭
    root.update()  # 立即更新窗口，而不是等待mainloop

# 使用示例
if __name__ == "__main__":
    循环展示当前光标的最子元素()
