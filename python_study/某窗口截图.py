import pyautogui
import numpy as np
import cv2
from typing import Optional, Tuple, Union
import time
    
class ScreenshotUtils:
    """屏幕截图工具类"""
    @staticmethod
    def capture(region: Optional[Tuple[int, int, int, int]] = None, 
                fmt: str = 'RGB') -> np.ndarray:
        """
        截取屏幕指定区域的图像
        
        参数:
            region: 截图区域 (left, top, width, height)，默认为None表示全屏
            fmt: 返回图像的格式，支持 'RGB' 或 'BGR'
            
        返回:
            numpy数组格式的图像数据
        """
        # 获取截图
        if region is None:
            screenshot = pyautogui.screenshot()
        else:
            left, top, width, height = region
            screenshot = pyautogui.screenshot(region=region)
            
        # 转换为numpy数组
        img_array = np.array(screenshot)
        
        # 根据指定格式转换颜色空间
        if fmt == 'BGR':
            return cv2.cvtColor(img_array, cv2.COLOR_RGB2BGR)
        elif fmt == 'RGB':
            return img_array
        else:
            raise ValueError('不支持的图像格式，只能使用 RGB 或 BGR')

    @staticmethod
    def capture_window(window_title: str,
                      region: Optional[Tuple[int, int, int, int]] = None,
                      fmt: str = 'RGB') -> np.ndarray:
        """
        截取指定窗口的图像
        
        参数:
            window_title: 窗口标题
            region: 窗口内的截图区域 (left, top, width, height)，默认为None表示整个窗口
            fmt: 返回图像的格式，支持 'RGB' 或 'BGR'
            
        返回:
            numpy数组格式的图像数据
        """
        try:
            # 获取窗口位置
            window = pyautogui.getWindowsWithTitle(window_title)[0]
            
            # 激活窗口
            window.activate()
            
            # 计算截图区域
            if region is None:
                screenshot_region = (window.left, window.top, 
                                  window.width, window.height)
            else:
                left, top, width, height = region
                screenshot_region = (window.left + left, window.top + top,
                                  width, height)
            
            # 获取截图
            return ScreenshotUtils.capture(region=screenshot_region, fmt=fmt)
            
        except IndexError:
            raise ValueError(f'未找到标题为 "{window_title}" 的窗口')

def 测试截图微信并展示():
    img = 测试截图微信()
    # 添加图像有效性检查
    if img is not None and img.size > 0:
        print(f"图像尺寸: {img.shape}")
        cv2.imshow('微信截图', img)
        cv2.waitKey(0)
        cv2.destroyAllWindows()
    else:
        print("获取到的图像无效")

def 测试截图微信():
    try:
        截图工具 = ScreenshotUtils()
        # 首先验证是否能找到窗口
        windows = pyautogui.getWindowsWithTitle('微信')
        print(f"找到的窗口数量: {len(windows)}")
        if windows:
            window = windows[0]
            print(f"窗口信息: {window}")
            print(f"窗口位置: 左={window.left}, 上={window.top}, 宽={window.width}, 高={window.height}")
            
            # 确保窗口尺寸有效
            if window.width <= 0 or window.height <= 0:
                print("警告：窗口尺寸无效")
                return None
                
            # 添加延迟以确保窗口激活
            time.sleep(0.5)
            result = 截图工具.capture_window('微信')
            print(f"截图结果形状: {result.shape}")
            return result
        else:
            print("未找到微信窗口")
            return None
    except Exception as e:
        print(f"发生错误: {str(e)}")
        return None

if __name__ == '__main__':
    测试截图微信并展示()  