import time
from threading import Thread

import pygame
from PySide2.QtUiTools import QUiLoader
from PySide2.QtWidgets import QApplication
def 多线程运行(func):
    def wrapper(*args, **kwargs):
        Thread(target=func, args=args, kwargs=kwargs).start()
    return wrapper
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
class Stats:
    def __init__(self):
        self.ui = QUiLoader().load('./ui/untitled.ui')
        self.ui.pushButton.clicked.connect(self.pushButton)
        self.ui.pushButton_2.clicked.connect(self.pushButton_2)

    def pushButton(self):
        获取倒计秒数=int(self.ui.lineEdit.text())
        多线程运行(self.到点铃声)(获取倒计秒数)

    def pushButton_2(self):
        获取倒计秒数 = int(self.ui.lineEdit_2.text())
        多线程运行(self.到点铃声)(获取倒计秒数)

    def 到点铃声(self, 秒数):
        for _ in range(秒数):
            time.sleep(1)
            秒数-=1
        播放mp3("aa.mp3")




# currentText()
app = QApplication([])
stats = Stats()
stats.ui.show()
app.exec_()
