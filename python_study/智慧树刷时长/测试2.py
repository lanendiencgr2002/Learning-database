from DrissionPage import ChromiumPage, ChromiumOptions
import pygame
from pathlib import Path
def dp配置():
    co = ChromiumOptions().set_local_port(9222)
    co.set_timeouts(base=5)
    page = ChromiumPage(addr_or_opts=co)
    return page


page=dp配置()


def 测试可播放标志():    
    target=page.ele('.time_ico_half fl')
    父元素=target.parent()
def 测试未播放标志():
    target=page.eles('.time_ico_half fl')
    未播放列表=[]
    已播放完成列表=[]
    for i in target:
        整个大框=i.parent()
        找到播放完成标志=整个大框.ele('.fl time_icofinish',timeout=.3)
        if 找到播放完成标志:
            已播放完成列表.append(整个大框)
        else:
            未播放列表.append(整个大框)
    print('未播放数量:',len(未播放列表))
    print('已播放完成数量:',len(已播放完成列表))

def 播放mp3(mp3文件路径='e.wav'):
    # 转音频 https://www.aconvert.com/cn/audio/
    pygame.mixer.init()
    sound = pygame.mixer.Sound(mp3文件路径)
    channel = pygame.mixer.Channel(0)  # 获取第 0 通道
    channel.play(sound)
    # 使用 channel.get_busy() 来检查音频是否仍在播放
    while channel.get_busy():
        pass
    sound.stop()
    pygame.mixer.quit()

def 处理视频卡住():
    print('视频卡住')

    播放mp3(Path(__file__).parent.joinpath('./寄咯.mp3'))

if __name__ == '__main__':
    print(page.title)
    # 测试可播放标志()
    # 测试未播放标志()
    处理视频卡住()
