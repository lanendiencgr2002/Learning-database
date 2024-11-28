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

def draw(screen, enemyPos):
    screen.fill(fuchsia)  # 填充透明背景
    if len(enemyPos) > 0:
        off_x = -30
        off_y = -50
        rectWidth = 280
        rectHeight = 120
        for pos in enemyPos:
            pygame.draw.rect(screen, col, pygame.Rect(pos[0] + off_x, pos[1] + off_y, rectWidth, rectHeight))
    pygame.display.update()

def main():
    screen = init_pygame()
    clock = pygame.time.Clock()
    running = True
    enemyPos = []

    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            elif event.type == pygame.MOUSEBUTTONDOWN:
                if event.button == 1:  # 左键点击
                    enemyPos.append(event.pos)
                elif event.button == 3:  # 右键点击
                    enemyPos = []  # 清空所有矩形

        draw(screen, enemyPos)
        clock.tick(60)

    pygame.quit()

if __name__ == "__main__":
    main()
