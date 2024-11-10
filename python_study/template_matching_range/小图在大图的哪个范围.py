import cv2
import numpy as np
import os
def 查找图片位置(大图路径, 小图路径):
    """
    在大图中查找小图的位置范围
    返回: [x_min%, y_min%, x_max%, y_max%] 表示小图在大图中的位置范围（百分比）
    """
    try:
        # 读取图片
        大图 = cv2.imread(大图路径)
        小图 = cv2.imread(小图路径)
        
        if 大图 is None or 小图 is None:
            raise FileNotFoundError("无法读取图片文件")
            
        # 获取图片尺寸
        大图高度, 大图宽度 = 大图.shape[:2]
        小图高度, 小图宽度 = 小图.shape[:2]
        
        print(f"大图尺寸: {大图.shape}")
        print(f"小图尺寸: {小图.shape}")
        
        # 使用模板匹配
        result = cv2.matchTemplate(大图, 小图, cv2.TM_CCOEFF_NORMED)
        
        # 获取匹配位置
        min_val, max_val, min_loc, max_loc = cv2.minMaxLoc(result)
        
        # 获取匹配位置的坐标
        x, y = max_loc
        
        # 计算范围（百分比）
        x_min = (x / 大图宽度) * 100
        y_min = (y / 大图高度) * 100
        x_max = ((x + 小图宽度) / 大图宽度) * 100
        y_max = ((y + 小图高度) / 大图高度) * 100
        
        范围 = [x_min, y_min, x_max, y_max]
        print(f"找到小图位置范围（百分比）: {范围}")
        
        # 在大图上标记找到的位置（用于可视化）
        结果图 = 大图.copy()
        cv2.rectangle(结果图, (x, y), (x + 小图宽度, y + 小图高度), (0, 255, 0), 2)
        
        return 范围, 结果图
        
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return None, None

def 测试():
    当前目录 = os.path.dirname(os.path.abspath(__file__))
    try:
        范围, 结果图 = 查找图片位置(
            大图路径=os.path.join(当前目录, 'biteall.png'),
            小图路径=os.path.join(当前目录, 'bitesmal.png')
        )
        
        if 范围 is not None:
            print(f"小图在大图中的位置范围（百分比）:")
            print(f"左上角: ({范围[0]:.2f}%, {范围[1]:.2f}%)")
            print(f"右下角: ({范围[2]:.2f}%, {范围[3]:.2f}%)")
            # 将图缩小为一半再展示
            结果图 = cv2.resize(结果图, None, fx=0.5, fy=0.5)
            # 显示结果
            cv2.imshow('找到的位置', 结果图)
            cv2.waitKey(0)
            cv2.destroyAllWindows()
            
            # 保存结果图片
            cv2.imwrite('结果.png', 结果图)
            
    except Exception as e:
        print(f"测试时发生错误: {str(e)}")

if __name__ == '__main__':
    测试()