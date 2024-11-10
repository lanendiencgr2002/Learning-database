import winsound
import time

# 音阶频率（近似值）
do = 523  # C
re = 587  # D
mi = 659  # E
fa = 698  # F
sol = 784  # G
la = 880  # A
si = 988  # B

# 播放一个简单的音阶
for note in [do, re, mi, fa, sol, la, si]:
    winsound.Beep(note, 500)  # 每个音符持续0.5秒
    time.sleep(0.1)  # 音符之间稍作停顿