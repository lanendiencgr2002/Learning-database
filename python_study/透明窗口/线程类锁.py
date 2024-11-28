import pygame
import win32api
import win32con
import win32gui
import random
import time
import tkinter as tk
import threading

Width = 1920
Height = 1080
fuchsia = (255, 0, 128)  # 透明颜色
col = (255, 0, 0)  # 绘制颜色

def init_pygame():
    pygame.init()
    screen = pygame.display.set_mode((Width, Height), pygame.NOFRAME)
    pygame.display.set_caption('透明窗体')
    hwnd = pygame.display.get_wm_info()["window"]
    win32gui.SetWindowLong(hwnd, win32con.GWL_EXSTYLE,
                           win32gui.GetWindowLong(hwnd, win32con.GWL_EXSTYLE) | win32con.WS_EX_LAYERED)
    win32gui.SetLayeredWindowAttributes(hwnd, win32api.RGB(*fuchsia), 0, win32con.LWA_COLORKEY)
    win32gui.SetWindowPos(hwnd, -1, 0, 0, Width, Height, 3)
    return screen

class PygameThread(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)
        self.screen = init_pygame()
        self.running = True
        self.clock = pygame.time.Clock()
        self.rectangles = []
        self.lock = threading.Lock()

    def run(self):
        while self.running:
            self.screen.fill(fuchsia)
            for event in pygame.event.get():
                if event.type == pygame.QUIT:
                    self.running = False

            with self.lock:
                for rect in self.rectangles:
                    pygame.draw.rect(self.screen, col, rect)

            pygame.display.update()
            self.clock.tick(60)

    def stop(self):
        self.running = False

    def add_rect(self):
        with self.lock:
            new_rect = (random.randint(0, Width - 100), random.randint(0, Height - 100), 100, 100)
            self.rectangles.append(new_rect)

    def clear_rects(self):
        with self.lock:
            self.rectangles.clear()

def create_tkinter_window(pygame_thread):
    root = tk.Tk()
    root.title("Pygame 控制")

    def add_rect():
        pygame_thread.add_rect()

    def clear_rects():
        pygame_thread.clear_rects()

    def quit_app():
        pygame_thread.stop()
        root.quit()

    add_button = tk.Button(root, text="添加矩形", command=add_rect)
    add_button.pack()

    clear_button = tk.Button(root, text="清除矩形", command=clear_rects)
    clear_button.pack()

    quit_button = tk.Button(root, text="退出", command=quit_app)
    quit_button.pack()

    root.mainloop()

if __name__ == "__main__":
    pygame_thread = PygameThread()
    pygame_thread.start()
    create_tkinter_window(pygame_thread)
    pygame_thread.join()
    pygame.quit()
