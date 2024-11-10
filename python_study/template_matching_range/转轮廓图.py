import cv2
import os
# 设置输入图片名称
input_image = "result_1.png"  # 根据实际图片名称修改
    

def 测试():
    # 获取当前目录
    current_dir = os.path.dirname(os.path.abspath(__file__))
    
    
    # 构建完整的输入输出路径
    input_path = os.path.join(current_dir, input_image)
    output_path = os.path.join(current_dir, "edge_" + input_image)
    
    # 转换图片
    if gray_to_contour(input_path, output_path):
        print("边缘检测完成！")
    else:
        print("处理失败！")


def gray_to_contour(input_path, output_path):
    """
    将图片转换为轮廓图（边缘检测）
    :param input_path: 输入图片路径
    :param output_path: 输出轮廓图路径
    """
    # 读取图片
    img = cv2.imread(input_path)
    
    if img is None:
        print(f"无法读取图片: {input_path}")
        return False
    
    # 转换为灰度图
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    
    # 直接使用Canny边缘检测
    edge_output = cv2.Canny(gray, 50, 150)
    
    # 保存轮廓图
    cv2.imwrite(output_path, edge_output)
    print(f"边缘检测图已保存至: {output_path}")
    return True

    
if __name__ == "__main__":
    测试()