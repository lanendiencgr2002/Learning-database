import sys
from PyQt5.QtWidgets import QApplication, QMainWindow, QLabel  # 导入Qt界面组件
from PyQt5.QtCore import Qt, QPoint, pyqtSignal, QObject  # 导入Qt核心功能
from PyQt5.QtGui import QCursor  # 导入光标相关功能
import time
import win32clipboard  # 用于访问Windows剪贴板
import threading
from 接口3 import DeepLTranslator  # 导入DeepL翻译API封装

# 窗口样式定义
WINDOW_STYLE = """
    QLabel {
        background-color: rgba(40, 40, 40, 200);  
        color: #FFFFFF;
        padding: 15px;
        border-radius: 10px;
        font-size: 16px;
        border: 1px solid #666666;
        font-family: "Microsoft YaHei";
    }
"""

CLIPBOARD_CHECK_INTERVAL = 0.25  # 剪贴板检查间隔（秒）

class SignalHandler(QObject):
    show_translation_signal = pyqtSignal(str, QPoint)  # 定义显示翻译的信号

class TranslationWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.init_ui()  # 初始化UI
        self.init_signals()  # 初始化信号
        self.translator = DeepLTranslator()  # 初始化翻译器
        self.is_first_run = True  # 标记是否首次运行
        self.last_text = ""  # 记录上次翻译的文本
        self.start_listeners()  # 启动监听线程
        
    def init_ui(self):
        self.setWindowFlags(Qt.FramelessWindowHint | Qt.WindowStaysOnTopHint | Qt.Tool)  # 设置无边框、置顶窗口
        self.setAttribute(Qt.WA_TranslucentBackground)  # 设置透明背景
        
        self.label = QLabel(self)  # 创建标签用于显示翻译结果
        self.label.setStyleSheet(WINDOW_STYLE)  # 应用预定义样式
        self.setCentralWidget(self.label)
        
        self.dragging = False  # 初始化拖动状态
        self.offset = QPoint()  # 初始化拖动偏移量
        
    def init_signals(self):
        self.signal_handler = SignalHandler()  # 创建信号处理器
        self.signal_handler.show_translation_signal.connect(self.show_translation)  # 连接信号到显示函数
        
    def start_listeners(self):
        self.last_text = ""
        self.clipboard_thread = threading.Thread(target=self.clipboard_listener, daemon=True)  # 创建剪贴板监听线程
        self.clipboard_thread.start()  # 启动监听线程
        
    def is_translated(self, text, english_threshold=0.5):
        if not text or len(text.strip()) == 0:
            return False
        
        english_chars = len([c for c in text if c.isalpha() and ord(c) < 128])  # 统计英文字符数
        chinese_chars = len([c for c in text if '\u4e00' <= c <= '\u9fff'])  # 统计中文字符数
        total_valid_chars = english_chars + chinese_chars  # 计算总有效字符数
        
        if total_valid_chars == 0:
            return False
        
        english_ratio = english_chars / total_valid_chars  # 计算英文字符占比
        return english_ratio >= english_threshold  # 根据阈值判断是否需要翻译

    def get_clipboard_text(self):
        try:
            win32clipboard.OpenClipboard()  # 打开剪贴板
            try:
                return win32clipboard.GetClipboardData(win32clipboard.CF_UNICODETEXT)  # 获取剪贴板文本
            except:
                return ""
            finally:
                win32clipboard.CloseClipboard()  # 确保关闭剪贴板
        except Exception as e:
            print(f"获取剪贴板内容失败: {e}")
            return ""
    
    def clipboard_listener(self):
        while True:
            try:
                if self.is_first_run:  # 首次运行只记录当前剪贴板内容
                    self.last_text = self.get_clipboard_text()
                    self.is_first_run = False
                    time.sleep(CLIPBOARD_CHECK_INTERVAL)
                    continue
                
                current_text = self.get_clipboard_text()  # 获取当前剪贴板内容
                if current_text and current_text != self.last_text and self.is_translated(current_text):  # 检查是否需要翻译
                    print(f"检测到新的英文内容: {current_text[:50]}...")
                    cursor_pos = QCursor.pos()  # 获取当前鼠标位置
                    translation = self.translate_text(current_text)  # 翻译文本
                    self.signal_handler.show_translation_signal.emit(translation, cursor_pos)  # 发送显示信号
                self.last_text = current_text
            except Exception as e:
                print(f"剪贴板监听错误: {e}")
            time.sleep(CLIPBOARD_CHECK_INTERVAL)
    
    def translate_text(self, text):
        try:
            translation = self.translator.translate(text)  # 调用翻译API
            return f"原文: {text}\n译文: {translation}"  # 格式化显示内容
        except Exception as e:
            return f"翻译失败: {str(e)}"
    
    def show_translation(self, text, pos):
        print(f"显示翻译结果: {text[:50]}...")
        # 先重置label大小
        self.label.setFixedSize(1, 1)  # 重置为最小尺寸
        self.label.setText(text)  # 设置翻译文本
        self.label.adjustSize()  # 调整标签大小
        # 取消固定大小限制
        self.label.setFixedSize(self.label.sizeHint())
        self.resize(self.label.size())  # 调整窗口大小
        self.move(pos.x(), pos.y() + 20)  # 移动窗口到鼠标位置下方
        self.show()  # 显示窗口
    
    def mousePressEvent(self, event):
        if event.button() == Qt.LeftButton:  # 左键按下开始拖动
            self.dragging = True
            self.offset = event.pos()
    
    def mouseDoubleClickEvent(self, event):
        if event.button() == Qt.LeftButton:  # 左键双击隐藏窗口
            self.hide()
    
    def mouseMoveEvent(self, event):
        if self.dragging:  # 拖动窗口
            self.move(event.globalPos() - self.offset)
    
    def mouseReleaseEvent(self, event):
        if event.button() == Qt.LeftButton:  # 释放左键结束拖动
            self.dragging = False

if __name__ == '__main__':
    print("程序启动")
    app = QApplication(sys.argv)  # 创建Qt应用程序实例
    window = TranslationWindow()  # 创建翻译窗口实例
    sys.exit(app.exec_())  # 运行应用程序主循环
