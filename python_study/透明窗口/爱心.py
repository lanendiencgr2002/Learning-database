import pygame
import win32api
import win32con
import win32gui
import random
import math

Width = 1920
Height = 1080
fuchsia = (255, 0, 128)  # 透明颜色

def init_pygame():
    pygame.init()
    # 创建窗口
    screen = pygame.display.set_mode((Width, Height), pygame.NOFRAME)
    # 设置窗口标题
    pygame.display.set_caption('Falling Hearts')
    
    # 创建一个透明窗口
    hwnd = pygame.display.get_wm_info()["window"]
    win32gui.SetWindowLong(hwnd, win32con.GWL_EXSTYLE,
                          win32gui.GetWindowLong(hwnd, win32con.GWL_EXSTYLE) | win32con.WS_EX_LAYERED)
    # 设置窗口透明度
    win32gui.SetLayeredWindowAttributes(hwnd, win32api.RGB(*fuchsia), 0, win32con.LWA_COLORKEY)
    # 设置窗口置顶
    win32gui.SetWindowPos(hwnd, win32con.HWND_TOPMOST, 0, 0, 0, 0,
                         win32con.SWP_NOMOVE | win32con.SWP_NOSIZE)
    
    return screen

class Heart:
    def __init__(self):
        self.size = random.randint(2,5)
        self.x = random.randint(0, Width)
        self.y = random.randint(-50, 0)
        self.speed = random.uniform(1, 3)
        self.color = (255, 0, 0)  # 红色爱心
        self.angle = random.uniform(0, 360)
        self.swing_speed = random.uniform(-2, 2)

    def fall(self):
        self.y += self.speed
        self.x += math.sin(math.radians(self.angle)) * 0.5
        self.angle += self.swing_speed

        if self.y > Height:
            self.reset()

    def reset(self):
        self.y = random.randint(-50, 0)
        self.x = random.randint(0, Width)
        self.speed = random.uniform(1, 3)

    def draw(self, screen):
        # 绘制实心爱心
        points = []
        for t in range(0, 360, 5):
            t_rad = math.radians(t)
            x = self.size * 16 * math.sin(t_rad) ** 3
            y = self.size * -(13 * math.cos(t_rad) - 5 * math.cos(2*t_rad) - 2 * math.cos(3*t_rad) - math.cos(4*t_rad))
            points.append((self.x + x, self.y + y))
        
        if len(points) > 2:
            pygame.draw.polygon(screen, self.color, points)

def main():
    screen = init_pygame()
    clock = pygame.time.Clock()
    running = True
    hearts = [Heart() for _ in range(50)]  # 减少数量以避免过于密集

    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            elif event.type == pygame.KEYDOWN:
                if event.key == pygame.K_ESCAPE:
                    running = False

        screen.fill(fuchsia)  # 填充透明背景

        # 绘制和移动爱心
        for heart in hearts:
            heart.fall()
            heart.draw(screen)

        pygame.display.update()
        clock.tick(60)  # 降低帧率以获得更好的性能

    pygame.quit()

if __name__ == "__main__":
    main()