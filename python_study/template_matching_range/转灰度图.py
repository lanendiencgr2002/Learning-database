import cv2
import os
# 设置输入图片名称（请确保图片存在于当前目录）
input_image = "bitesmal.png"  # 根据实际图片名称修改

def 测试():
    # 获取当前目录
    current_dir = os.path.dirname(os.path.abspath(__file__))
    
    # 构建完整的输入输出路径
    input_path = os.path.join(current_dir, input_image)
    output_path = os.path.join(current_dir, "gray_" + input_image)
    
    # 转换图片
    if convert_to_gray(input_path, output_path):
        print("转换成功！")
    else:
        print("转换失败！")

def convert_to_gray(input_path, output_path):
    """
    将彩色图片转换为灰度图
    :param input_path: 输入图片路径
    :param output_path: 输出图片路径
    """
    # 读取图片
    img = cv2.imread(input_path)
    
    # 检查图片是否成功读取
    if img is None:
        print(f"无法读取图片: {input_path}")
        return False
    
    # 转换为灰度图
    gray_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    
    # 保存灰度图
    cv2.imwrite(output_path, gray_img)
    print(f"灰度图已保存至: {output_path}")
    return True


if __name__ == "__main__":
    测试()