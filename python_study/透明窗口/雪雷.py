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
    screen = pygame.display.set_mode((Width, Height), pygame.NOFRAME)
    pygame.display.set_caption('雪花与雷电')
    hwnd = pygame.display.get_wm_info()["window"]
    win32gui.SetWindowLong(hwnd, win32con.GWL_EXSTYLE,
                           win32gui.GetWindowLong(hwnd, win32con.GWL_EXSTYLE) | win32con.WS_EX_LAYERED)
    win32gui.SetLayeredWindowAttributes(hwnd, win32api.RGB(*fuchsia), 0, win32con.LWA_COLORKEY)
    win32gui.SetWindowPos(hwnd, -1, 0, 0, Width, Height, 3)
    return screen


class Snowflake:
    def __init__(self):
        self.x = random.randint(0, Width)
        self.y = random.randint(-50, 0)
        self.size = random.randint(2, 5)
        self.speed = random.uniform(1, 3)
        self.acceleration = random.uniform(0.01, 0.03)  # 添加加速度

    def fall(self):
        self.speed += self.acceleration  # 速度随时间增加
        self.y += self.speed

        # 当雪花落到屏幕底部时，重置位置和速度
        if self.y > Height:
            self.reset()

    def reset(self):
        self.y = random.randint(-50, 0)
        self.x = random.randint(0, Width)
        self.speed = random.uniform(1, 3)  # 重置速度

    def draw(self, screen):
        pygame.draw.circle(screen, (255, 255, 255), (int(self.x), int(self.y)), self.size)


class Lightning:
    def __init__(self):
        self.start = (random.randint(0, Width), 0)
        self.points = self.generate_lightning_points()
        self.color = (255, 255, 0)
        self.lifetime = random.randint(5, 15)
        self.width = random.randint(2, 5)

    def generate_lightning_points(self):
        points = [self.start]
        current_y = 0
        while current_y < Height:
            new_x = points[-1][0] + random.randint(-100, 100)
            new_y = current_y + random.randint(50, 150)
            points.append((new_x, new_y))
            current_y = new_y
        return points

    def draw(self, screen):
        pygame.draw.lines(screen, self.color, False, self.points, self.width)

def main():
    screen = init_pygame()
    clock = pygame.time.Clock()
    running = True
    snowflakes = [Snowflake() for _ in range(200)]
    lightning = None
    lightning_timer = 0

    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            elif event.type == pygame.KEYDOWN:
                if event.key == pygame.K_ESCAPE:
                    running = False

        screen.fill(fuchsia)  # 填充透明背景

        # 绘制和移动雪花
        for snowflake in snowflakes:
            snowflake.fall()
            snowflake.draw(screen)

        # 处理闪电
        if lightning:
            lightning.draw(screen)
            lightning_timer -= 1
            if lightning_timer <= 0:
                lightning = None
        else:
            if random.random() < 0.01:  # 1% 的概率产生新的闪电
                lightning = Lightning()
                lightning_timer = lightning.lifetime

        pygame.display.update()
        clock.tick(60)

    pygame.quit()

if __name__ == "__main__":
    main()
