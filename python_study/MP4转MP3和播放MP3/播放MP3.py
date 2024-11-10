import pygame
import 当前目录切换当前目录

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


if __name__ == "__main__":
    # 播放mp3("aa.mp3")
    # 寄咯
    播放mp3("./寄咯.mp3")
