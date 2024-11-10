import matplotlib.pyplot as plt  # 导入绘图库
import numpy as np  # 导入数值计算库
from matplotlib.animation import FFMpegWriter  # 导入视频写入工具

class SimpleAnimation:
    def __init__(self):
        # 创建图形和坐标轴
        self.fig, self.ax = plt.subplots()
        # 设置坐标轴范围
        self.ax.set_xlim(0, 10)
        self.ax.set_ylim(0, 10)
        # 创建一个点（小球）
        self.point, = self.ax.plot([5], [5], 'ro')  # 'ro'表示红色圆点
        
    def update_position(self, frame):
        """更新小球位置"""
        # 计算新的位置（让小球做圆周运动）
        x = 5 + 3 * np.cos(frame / 10)  # 圆心(5,5)，半径3
        y = 5 + 3 * np.sin(frame / 10)
        
        # 更新点的位置
        self.point.set_data([x], [y])
        
    def save_animation(self, filename='animation.mp4', frames=100):
        # 创建视频写入器，设置帧率为30fps
        writer = FFMpegWriter(fps=30)
        # 保存动画
        with writer.saving(self.fig, filename, dpi=100):
            print("开始生成动画...")
            # 循环生成每一帧
            for frame in range(frames):
                # 更新小球位置
                self.update_position(frame)
                # 抓取当前帧
                writer.grab_frame()
                # 显示进度
                if frame % 10 == 0:
                    print(f"进度: {frame}/{frames}")
        print(f"动画已保存为: {filename}")

# 运行示例
if __name__ == '__main__':
    # 创建动画对象
    anim = SimpleAnimation()
    # 生成并保存动画
    anim.save_animation()

"""
使用说明：
1. 需要先安装 FFmpeg：
   - Windows: 下载FFmpeg并添加到系统环境变量
   - Mac: brew install ffmpeg
   - Linux: sudo apt-get install ffmpeg

2. 需要的Python包：
   pip install matplotlib numpy

3. 代码运行后会生成一个MP4文件，显示一个做圆周运动的红色小球

代码结构解释：
- SimpleAnimation类：处理动画的主要类
  - __init__：初始化图形和小球
  - update_position：更新小球位置
  - save_animation：保存动画为视频文件

FFMpegWriter的主要参数：
- fps：视频的帧率
- dpi：输出视频的分辨率
"""