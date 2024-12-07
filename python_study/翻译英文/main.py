import sys
from PyQt5.QtWidgets import QApplication, QMainWindow, QLabel
from PyQt5.QtCore import Qt, QPoint, pyqtSignal, QObject
from PyQt5.QtGui import QCursor
import time
import win32clipboard
import requests
import json
import threading
import re
from 接口3 import DeepLTranslator

# 常量定义
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

TRANSLATION_API_URL = "http://localhost:5000/chat"
CLIPBOARD_CHECK_INTERVAL = 0.5  # 剪贴板检查间隔（秒）

class SignalHandler(QObject):
    show_translation_signal = pyqtSignal(str, QPoint)

class TranslationWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.init_ui()
        self.init_signals()
        self.start_listeners()
        self.translator = DeepLTranslator()  # 使用DeepL翻译器
        
    def init_ui(self):
        """初始化UI组件"""
        self.setWindowFlags(Qt.FramelessWindowHint | Qt.WindowStaysOnTopHint | Qt.Tool)
        self.setAttribute(Qt.WA_TranslucentBackground)
        
        self.label = QLabel(self)
        self.label.setStyleSheet(WINDOW_STYLE)
        self.setCentralWidget(self.label)
        
        # 初始化拖动相关变量
        self.dragging = False
        self.offset = QPoint()
        
    def init_signals(self):
        """初始化信号处理"""
        self.signal_handler = SignalHandler()
        self.signal_handler.show_translation_signal.connect(self.show_translation)
        
    def start_listeners(self):
        """启动监听线程"""
        self.last_text = ""
        self.clipboard_thread = threading.Thread(target=self.clipboard_listener, daemon=True)
        self.clipboard_thread.start()
        
    def is_translated(self, text, english_threshold=0.5):
        """
        检查文本是否需要翻译（英译中）
        
        Args:
            text: 输入文本
            english_threshold: 英文字符占比阈值，默认0.5（50%）
        
        Returns:
            bool: True表示需要翻译（英文占主导），False表示不需要翻译（中文占主导）
        """
        if not text or len(text.strip()) == 0:
            return False
        
        # 统计英文字母
        english_chars = len([c for c in text if c.isalpha() and ord(c) < 128])
        
        # 统计中文字符
        chinese_chars = len([c for c in text if '\u4e00' <= c <= '\u9fff'])
        
        # 计算总有效字符数
        total_valid_chars = english_chars + chinese_chars
        
        if total_valid_chars == 0:
            return False
        
        # 计算英文字符占比
        english_ratio = english_chars / total_valid_chars
        
        # 返回是否需要翻译（英文占比高于阈值时需要翻译）
        return english_ratio >= english_threshold

    def get_clipboard_text(self):
        """安全地获取剪贴板内容"""
        try:
            win32clipboard.OpenClipboard()
            try:
                return win32clipboard.GetClipboardData(win32clipboard.CF_UNICODETEXT)
            except:
                return ""
            finally:
                win32clipboard.CloseClipboard()
        except Exception as e:
            print(f"获取剪贴板内容失败: {e}")
            return ""
    
    def clipboard_listener(self):
        """剪贴板监听线程"""
        while True:
            try:
                text = self.get_clipboard_text()
                if text and text != self.last_text and self.is_translated(text):
                    print(f"检测到新的英文内容: {text[:50]}...")
                    cursor_pos = QCursor.pos()
                    self.last_text = text
                    translation = self.translate_text(text)
                    self.signal_handler.show_translation_signal.emit(translation, cursor_pos)
            except Exception as e:
                print(f"剪贴板监听错误: {e}")
            time.sleep(CLIPBOARD_CHECK_INTERVAL)
    
    def translate_text(self, text):
        """翻译文本"""
        try:
            translation = self.translator.translate(text)
            return f"原文: {text}\n译文: {translation}"
        except Exception as e:
            return f"翻译失败: {str(e)}"
    
    def show_translation(self, text, pos):
        """显示翻译结果"""
        print(f"显示翻译结果: {text[:50]}...")
        self.label.setText(text)
        self.label.adjustSize()
        self.resize(self.label.size())
        self.move(pos.x(), pos.y() + 20)
        self.show()
    
    def mousePressEvent(self, event):
        """鼠标按下事件"""
        if event.button() == Qt.LeftButton:
            self.dragging = True
            self.offset = event.pos()
    
    def mouseDoubleClickEvent(self, event):
        """鼠标双击事件"""
        if event.button() == Qt.LeftButton:
            self.hide()
    
    def mouseMoveEvent(self, event):
        """鼠标移动事件"""
        if self.dragging:
            self.move(event.globalPos() - self.offset)
    
    def mouseReleaseEvent(self, event):
        """鼠标释放事件"""
        if event.button() == Qt.LeftButton:
            self.dragging = False

if __name__ == '__main__':
    print("程序启动")
    app = QApplication(sys.argv)
    window = TranslationWindow()
    sys.exit(app.exec_())
