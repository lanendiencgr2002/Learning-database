import matplotlib.pyplot as plt
import numpy as np
class 简单渲染器:
    """简单的matplotlib渲染器演示"""
    def __init__(self):
        # 创建图形和坐标轴对象 figsize=(8, 6)表示窗口大小
        self.图形, self.坐标轴 = plt.subplots(figsize=(8, 6))
        # 设置图形标题
        self.坐标轴.set_title('钓鱼模拟器演示')
        # 设置坐标轴范围
        self.坐标轴.set_xlim(0, 100)
        self.坐标轴.set_ylim(0, 100)
    def 绘制钓鱼状态(self, 进度条位置=50, 目标区域=(40, 60)):
        """绘制钓鱼状态
        参数:
            进度条位置: 当前进度条在哪里（0-100之间的数字）
            目标区域: 目标区域的范围，格式是(起始位置, 结束位置)
        """
        # 清除之前的绘图
        self.坐标轴.clear()
        # 绘制目标区域（绿色区域）alpha表示透明度
        self.坐标轴.axvspan(目标区域[0], 目标区域[1], color='green', alpha=0.3)
        # 绘制进度条（红色竖线）linewidth表示线条宽度
        self.坐标轴.axvline(x=进度条位置, color='red', linewidth=2) 
        # 更新显示 0.01秒暂停一次
        plt.pause(0.01)
def 演示程序():
    """渲染器演示程序"""
    # 创建渲染器实例
    渲染器 = 简单渲染器()
    # 模拟钓鱼进度条移动
    for 位置 in np.linspace(0, 100, 200):  # 从0到100，总共取200个点
        渲染器.绘制钓鱼状态(进度条位置=位置)
    plt.show()
if __name__ == '__main__':
    演示程序()