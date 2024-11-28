import pygame
import win32api
import win32con
import win32gui
import random
import time

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


screen = init_pygame()
running = True
clock = pygame.time.Clock()
while running:
    screen.fill(fuchsia)
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            running = False
    pygame.display.update()
    clock.tick(60)
pygame.quit()