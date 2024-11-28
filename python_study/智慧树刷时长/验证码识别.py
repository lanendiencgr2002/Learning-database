from DrissionPage import ChromiumPage, ChromiumOptions
import drissionpage_utils
import re
import time
from concurrent.futures import ThreadPoolExecutor
from concurrent.futures import wait
import ddddocr
import cv2
import numpy as np

def 初始化dp():
    co = ChromiumOptions().set_local_port(9222)
    page = ChromiumPage(addr_or_opts=co)
    return page
page=初始化dp()

# 初始化检测器
det = ddddocr.DdddOcr(det=True)  # 设置det=True来启用目标检测功能


# <div id="playButton" class="pauseButton"><div class="bigPlayButton pointer" style="display: none;"></div></div>

# <div id="playButton" class="playButton"><div class="bigPlayButton pointer" style=""></div></div>

# <div class="volumeBox volumeNone" style="display: block;"><div class="volumeIcon"></div><div class="volumeBarWrap"><div class="volumeBarWrapBg"><div class="volumeBar"><div class="volumeBall" style="bottom: 0px;"><div class="volumeNumber">0%</div></div><div class="passVolume" style="height: 0px;"></div></div></div></div></div>

# <div class="volumeBox" style="display: block;"><div class="volumeIcon"></div><div class="volumeBarWrap"><div class="volumeBarWrapBg"><div class="volumeBar"><div class="volumeBall" style="bottom: 44px;"><div class="volumeNumber">0%</div></div><div class="passVolume" style="height: 44px;"></div></div></div></div></div>

def 测试播放按钮():
    pass
def 验证码图片():
    # 读取原始图片
    with open('智慧树刷时长/yzm1.png', 'rb') as f:
        image = f.read()
    
    # 目标检测
    result = det.detection(image)
    
    # 将二进制图片数据转换为OpenCV格式
    nparr = np.frombuffer(image, np.uint8)
    img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
    
    # 在检测到的位置画红框
    for box in result:
        x1, y1, x2, y2 = map(int, box)
        cv2.rectangle(img, (x1, y1), (x2, y2), (0, 0, 255), 2)  # BGR格式，(0,0,255)为红色
    
    # 显示图片而不是保存
    cv2.imshow('验证码检测结果', img)
    cv2.waitKey(0)  # 等待按键
    cv2.destroyAllWindows()  # 关闭窗口
    
    print(result)
    


if __name__ == '__main__':
    验证码图片()

