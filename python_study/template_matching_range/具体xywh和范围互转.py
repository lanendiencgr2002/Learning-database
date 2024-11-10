import cv2
import os
def 获取图片尺寸(图片路径):
    """获取图片的宽度和高度"""
    图片 = cv2.imread(图片路径)
    if 图片 is None:
        raise ValueError(f"无法读取图片: {图片路径}")
    return 图片.shape[1], 图片.shape[0]  # 宽度, 高度

def 具体坐标转百分比(x, y, w, h, 图片路径):
    """将具体的xywh坐标转换为百分比范围 [x_min%, y_min%, x_max%, y_max%]"""
    图片宽度, 图片高度 = 获取图片尺寸(图片路径)
    return [
        (x / 图片宽度) * 100,
        (y / 图片高度) * 100,
        ((x + w) / 图片宽度) * 100,
        ((y + h) / 图片高度) * 100
    ]

def 百分比转具体坐标(x_min_pct, y_min_pct, x_max_pct, y_max_pct, 图片路径):
    """将百分比范围转换为具体的xywh坐标 (x, y, w, h)"""
    图片宽度, 图片高度 = 获取图片尺寸(图片路径)
    x = int(图片宽度 * x_min_pct / 100)
    y = int(图片高度 * y_min_pct / 100)
    w = int(图片宽度 * (x_max_pct - x_min_pct) / 100)
    h = int(图片高度 * (y_max_pct - y_min_pct) / 100)
    return (x, y, w, h)

if __name__ == '__main__':
    当前目录 = os.path.dirname(os.path.abspath(__file__))
    图片路径 = os.path.join(当前目录, 'biteall.png')
    具体坐标 = (1595, 955, 74, 74)
    左上右下范围 = [81.66666666666667, 88.05555555555556, 86.04166666666667, 95.0]
    # 测试具体坐标转百分比
    百分比 = 具体坐标转百分比(*具体坐标, 图片路径)
    print(f"具体坐标 {具体坐标} -> 百分比范围 {[round(p, 2) for p in 百分比]}%")
    
    # 测试百分比转回具体坐标
    还原坐标 = 百分比转具体坐标(*百分比, 图片路径)
    print(f"百分比范围 -> 还原坐标 {还原坐标}")

    # 测试范围转具体坐标
    具体坐标 = 百分比转具体坐标(*左上右下范围, 图片路径)
    print(f"范围 {左上右下范围} -> 具体坐标 {具体坐标}")