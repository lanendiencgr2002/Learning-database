import numpy as np  

def psnr(img1, img2):
    """计算两张灰度图的PSNR psnr是峰值信噪比 归一化后像素差平方和 表示图像的相似度 值为100是完美匹配"""
    mse = np.mean((img1 / 255.0 - img2 / 255.0) ** 2)
    if mse < 1.0e-10:
        return 100
    PIXEL_MAX = 1
    return 20 * np.log10(PIXEL_MAX / np.sqrt(mse))


