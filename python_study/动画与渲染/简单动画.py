import matplotlib.pyplot as plt
import numpy as np
from matplotlib.animation import FFMpegWriter

class 动画类:
    def __init__(self):
        self.图形, self.坐标轴 = plt.subplots() # 创建图形和坐标轴
        self.坐标轴.set_xlim(0, 10) # 设置坐标轴x范围 
        self.坐标轴.set_ylim(0, 10) # 设置坐标轴y范围 
        # [5] 表示坐标是5，5 r(red)o(圆点) markersize=10 表示点的大小
        self.小球, = self.坐标轴.plot([5], [5], 'ro', markersize=10)  # 红色圆点
    def 更新位置(self, 帧数):
        横坐标 = 5 + 3 * np.cos(帧数 / 10)  # 圆心(5,5)，半径3  一圈是2π 6.28
        纵坐标 = 5 + 3 * np.sin(帧数 / 10)
        self.小球.set_data([横坐标], [纵坐标]) # 更新点的位置
    def 保存动画(self, 文件名='动画.mp4', 总帧数=100):
        视频写入器 = FFMpegWriter(fps=30) # 创建视频写入器，设置帧率为30fps
        # dpi=100 表示每英寸100像素  表示图像的清晰度
        with 视频写入器.saving(self.图形, 文件名, dpi=100):
            print("开始生成动画...")
            for 当前帧 in range(总帧数):
                self.更新位置(当前帧)
                # grab_frame() 抓取当前帧 捕获（抓取）当前图形窗口中显示的内容
                视频写入器.grab_frame()
                if 当前帧 % 10 == 0:
                    print(f"进度: {当前帧}/{总帧数}")
        print(f"动画已保存为: {文件名}")
# 运行示例
if __name__ == '__main__':
    我的动画 = 动画类()
    我的动画.保存动画()

"""
使用说明：
1. 需要先安装 FFmpeg：
   - 访问 https://github.com/BtbN/FFmpeg-Builds/releases
   - 下载 ffmpeg-master-latest-win64-gpl.zip
   - 解压后将 bin 文件夹路径添加到系统环境变量

2. 需要的Python包：
   pip install matplotlib numpy

3. 代码说明：
   - 创建一个红色小球
   - 小球会做圆周运动
   - 生成的视频保存为 动画.mp4
""" 