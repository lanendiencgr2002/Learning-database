import pygame
import win32api
import win32con
import win32gui
import random
import math

Width = 1920
Height = 1080
fuchsia = (255, 0, 128)  # 透明色
frame_rate = 60

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
    def __init__(self, text=""):
        self.size = random.randint(2,5)
        self.x = random.randint(0, Width)
        self.y = random.randint(-50, 0)
        self.base_speed = random.uniform(1, 3) * 60  # 基础速度乘以60,用于帧率独立计算
        self.color = (255, 0, 0)  # 红色爱心
        self.angle = random.uniform(0, 360)
        self.swing_speed = random.uniform(-2, 2) * 60  # 摆动速度也需要调整
        self.text = text
        try:
            # 尝试使用微软雅黑
            self.font = pygame.font.Font("C:/Windows/Fonts/msyh.ttc", int(self.size * 10))
        except:
            try:
                # 如果没有微软雅黑,尝试使用黑体
                self.font = pygame.font.Font("C:/Windows/Fonts/simhei.ttf", int(self.size * 10))
            except:
                # 如果都没有,使用系统默认字体
                self.font = pygame.font.Font(None, int(self.size * 10))

    def fall(self, dt):
        # 使用dt(秒)来计算位移
        self.y += self.base_speed * dt
        self.x += math.sin(math.radians(self.angle)) * 30 * dt  # 30是原来0.5的60倍
        self.angle += self.swing_speed * dt

        if self.y > Height:
            self.reset()

    def reset(self):
        self.y = random.randint(-50, 0)
        self.x = random.randint(0, Width)
        self.base_speed = random.uniform(1, 3) * 60

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
            
            # 渲染文字
            if self.text:
                text_surface = self.font.render(self.text, True, (255, 255, 255))
                text_rect = text_surface.get_rect()
                text_rect.center = (self.x, self.y)
                screen.blit(text_surface, text_rect)

def main():
    screen = init_pygame()
    clock = pygame.time.Clock()
    running = True
    
    # 创建带有不同文字的爱心
    texts = ["dyl love wsq", "wsq love dyl"]
    hearts = [Heart(random.choice(texts)) for _ in range(50)]

    while running:
        dt = clock.tick(frame_rate) / 1000.0  # 将毫秒转换为秒

        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            elif event.type == pygame.KEYDOWN:
                if event.key == pygame.K_ESCAPE:
                    running = False

        screen.fill(fuchsia)  # 填充透明背景

        # 绘制和移动爱心
        for heart in hearts:
            heart.fall(dt)  # 传入dt
            heart.draw(screen)

        pygame.display.update()

    pygame.quit()

if __name__ == "__main__":
    main()