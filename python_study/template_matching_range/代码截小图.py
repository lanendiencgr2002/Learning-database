import cv2
import os
import 当前目录切换当前目录

def 截取并保存图片(输入图片路径='is_bited.png', 
                输出图片路径='截取结果.png',
                x=None, y=None, w=None, h=None,
                范围百分比=None):
    """
    从原图截取指定区域并保存，支持具体坐标或百分比范围
    参数:
        输入图片路径: 原图路径
        输出图片路径: 保存路径
        x, y: 具体坐标模式时的截取起点坐标
        w, h: 具体坐标模式时的截取区域的宽度和高度
        范围百分比: 百分比模式时的范围 [x_min%, y_min%, x_max%, y_max%]
                  例如 [10, 20, 90, 80] 表示在图片10%-90%宽度和20%-80%高度范围内
    注意: 必须选择使用具体坐标或范围百分比其中一种方式
    """
    try:
        # 读取原图
        原图 = cv2.imread(输入图片路径)
        if 原图 is None:
            raise FileNotFoundError(f"无法读取图片: {输入图片路径}")
            
        图片高度, 图片宽度 = 原图.shape[:2]
        print(f"原图尺寸: {原图.shape}")
        
        # 检查参数是否正确
        if 范围百分比 is not None and (x is not None or y is not None or w is not None or h is not None):
            raise ValueError("不能同时指定具体坐标和范围百分比")
        
        if 范围百分比 is None and (x is None or y is None or w is None or h is None):
            raise ValueError("必须指定具体坐标或范围百分比其中一种")
        
        # 使用范围百分比
        if 范围百分比 is not None:
            x_min_pct, y_min_pct, x_max_pct, y_max_pct = 范围百分比
            
            # 验证百分比范围
            if not all(0 <= p <= 100 for p in 范围百分比):
                raise ValueError("百分比范围必须在0-100之间")
            if x_min_pct >= x_max_pct or y_min_pct >= y_max_pct:
                raise ValueError("最大百分比必须大于最小百分比")
            
            # 转换百分比为实际像素位置
            x = int(图片宽度 * x_min_pct / 100)
            y = int(图片高度 * y_min_pct / 100)
            w = int(图片宽度 * (x_max_pct - x_min_pct) / 100)
            h = int(图片高度 * (y_max_pct - y_min_pct) / 100)
            
            print(f"百分比范围 {范围百分比} 转换为像素位置: x={x}, y={y}, w={w}, h={h}")
        
        # 检查并调整截取范围
        if x >= 图片宽度 or y >= 图片高度:
            raise ValueError(f"截取起点 ({x}, {y}) 超出图片范围 ({图片宽度}, {图片高度})")
        
        # 确保截取范围不超出图片边界
        实际w = min(w, 图片宽度 - x)
        实际h = min(h, 图片高度 - y)
        
        # 截取指定区域
        截取结果 = 原图[y:y+实际h, x:x+实际w]
        print(f"截取区域尺寸: {截取结果.shape}")
        
        # 保存截取结果
        cv2.imwrite(输出图片路径, 截取结果)
        print(f"截取结果已保存到: {输出图片路径}")
        
        return 截取结果
        
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return None

def 测试():
    # 测试两种方式
    测试用例 = [
        # 具体坐标方式
        {  # 1568, 951
            "x": 1568, "y": 951, "w": 74, "h": 74,
            "范围百分比": None,
            "说明": "具体坐标方式"
        },
        # 范围百分比方式
        {
            "x": None, "y": None, "w": None, "h": None,
            "范围百分比": [50, 50, 60, 60],
            "说明": "范围百分比方式"
        }
    ]
    
    for i, 参数 in enumerate(测试用例):
        print(f"\n测试 {i+1}: {参数['说明']}")
        输出文件 = f'result_{i+1}.png'
        当前目录 = os.path.dirname(os.path.abspath(__file__))
        # 创建一个新的字典，只包含函数需要的参数
        函数参数 = {
            "输入图片路径": os.path.join(当前目录, 'biteall.png'),
            "输出图片路径": os.path.join(当前目录, 输出文件)    ,
            "x": 参数["x"],
            "y": 参数["y"],
            "w": 参数["w"],
            "h": 参数["h"],
            "范围百分比": 参数["范围百分比"]
        }
        
        结果 = 截取并保存图片(**函数参数)
        
        if 结果 is not None:
            cv2.imshow(f'截取结果_{i+1}', 结果)
            cv2.waitKey(1000)
    
    cv2.destroyAllWindows()

if __name__ == '__main__':
    测试()