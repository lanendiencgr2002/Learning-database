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
        self.size = random.randint(2, 4)
        self.speed = random.uniform(0.5, 2)
        self.acceleration = random.uniform(0.001, 0.005)
        self.angle = random.uniform(0, 360)
        self.rotation_speed = random.uniform(-1, 1)

    def fall(self):
        self.speed += self.acceleration
        self.y += self.speed
        self.x += math.sin(math.radians(self.angle)) * 0.5
        self.angle += self.rotation_speed

        if self.y > Height:
            self.reset()

    def reset(self):
        self.y = random.randint(-50, 0)
        self.x = random.randint(0, Width)
        self.speed = random.uniform(0.5, 2)

    def draw(self, screen):
        for i in range(6):
            angle = math.radians(self.angle + i * 60)
            end_x = self.x + self.size * math.cos(angle)
            end_y = self.y + self.size * math.sin(angle)
            pygame.draw.line(screen, (255, 255, 255), (int(self.x), int(self.y)),
                             (int(end_x), int(end_y)), 1)

            branch_length = self.size * 0.7
            for j in range(2):
                branch_angle = angle + math.radians(30 * (j * 2 - 1))
                branch_end_x = self.x + branch_length * math.cos(branch_angle)
                branch_end_y = self.y + branch_length * math.sin(branch_angle)
                pygame.draw.line(screen, (255, 255, 255), (int(self.x), int(self.y)),
                                 (int(branch_end_x), int(branch_end_y)), 1)

class Lightning:
    def __init__(self):
        self.start = (random.randint(0, Width), 0)
        self.points = self.generate_lightning_points()
        self.color = (255, 255, 200)
        self.lifetime = random.randint(3, 8)
        self.width = random.randint(2, 4)

    def generate_lightning_points(self):
        points = [self.start]
        current_y = 0
        while current_y < Height:
            new_x = points[-1][0] + random.randint(-50, 50)
            new_y = current_y + random.randint(30, 100)
            points.append((new_x, new_y))
            current_y = new_y
        return points

    def draw(self, screen):
        pygame.draw.lines(screen, self.color, False, self.points, self.width)

def main():
    screen = init_pygame()
    clock = pygame.time.Clock()
    running = True
    snowflakes = [Snowflake() for _ in range(300)]
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
            if random.random() < 0.005:  # 0.5% 的概率产生新的闪电
                lightning = Lightning()
                lightning_timer = lightning.lifetime

        pygame.display.update()
        clock.tick(200)  # 设置为200帧

    pygame.quit()

if __name__ == "__main__":
    main()
