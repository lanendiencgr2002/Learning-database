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
    pygame.display.set_caption('透明窗体绘图测试')
    hwnd = pygame.display.get_wm_info()["window"]
    win32gui.SetWindowLong(hwnd, win32con.GWL_EXSTYLE, 
                           win32gui.GetWindowLong(hwnd, win32con.GWL_EXSTYLE) | win32con.WS_EX_LAYERED)
    win32gui.SetLayeredWindowAttributes(hwnd, win32api.RGB(*fuchsia), 0, win32con.LWA_COLORKEY)
    win32gui.SetWindowPos(hwnd, -1, 0, 0, Width, Height, 3)
    return screen

class Shape:
    def __init__(self, x, y, color):
        self.x = x
        self.y = y
        self.color = color
        self.speed_x = random.randint(-5, 5)
        self.speed_y = random.randint(-5, 5)

    def move(self):
        self.x += self.speed_x
        self.y += self.speed_y
        if self.x < 0 or self.x > Width:
            self.speed_x *= -1
        if self.y < 0 or self.y > Height:
            self.speed_y *= -1

class Circle(Shape):
    def __init__(self, x, y, radius, color):
        super().__init__(x, y, color)
        self.radius = radius

    def draw(self, screen):
        pygame.draw.circle(screen, self.color, (int(self.x), int(self.y)), self.radius)

class Rectangle(Shape):
    def __init__(self, x, y, width, height, color):
        super().__init__(x, y, color)
        self.width = width
        self.height = height

    def draw(self, screen):
        pygame.draw.rect(screen, self.color, (int(self.x), int(self.y), self.width, self.height))

class Triangle(Shape):
    def __init__(self, x, y, size, color):
        super().__init__(x, y, color)
        self.size = size

    def draw(self, screen):
        points = [
            (int(self.x), int(self.y - self.size)),
            (int(self.x - self.size * math.sqrt(3) / 2), int(self.y + self.size / 2)),
            (int(self.x + self.size * math.sqrt(3) / 2), int(self.y + self.size / 2))
        ]
        pygame.draw.polygon(screen, self.color, points)

def main():
    screen = init_pygame()
    clock = pygame.time.Clock()
    running = True
    shapes = []

    # 创建一些随机的形状
    for _ in range(5):
        shapes.append(Circle(random.randint(0, Width), random.randint(0, Height), 
                             random.randint(20, 50), (random.randint(0, 255), random.randint(0, 255), random.randint(0, 255))))
        shapes.append(Rectangle(random.randint(0, Width), random.randint(0, Height), 
                                random.randint(40, 100), random.randint(40, 100), 
                                (random.randint(0, 255), random.randint(0, 255), random.randint(0, 255))))
        shapes.append(Triangle(random.randint(0, Width), random.randint(0, Height), 
                               random.randint(40, 80), 
                               (random.randint(0, 255), random.randint(0, 255), random.randint(0, 255))))

    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            elif event.type == pygame.KEYDOWN:
                if event.key == pygame.K_ESCAPE:
                    running = False

        screen.fill(fuchsia)  # 填充透明背景

        for shape in shapes:
            shape.move()
            shape.draw(screen)

        pygame.display.update()
        clock.tick(60)

    pygame.quit()

if __name__ == "__main__":
    main()
