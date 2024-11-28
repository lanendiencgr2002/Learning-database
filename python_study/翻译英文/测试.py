import sys
from PyQt5.QtWidgets import QApplication, QMainWindow, QLabel
from PyQt5.QtCore import Qt, QPoint, pyqtSignal, QObject
from PyQt5.QtGui import QImage, QPixmap, QCursor
import keyboard
from PIL import ImageGrab
import time
import win32clipboard

class SignalHandler(QObject):
    show_translation_signal = pyqtSignal(str, QPoint)

class TranslationWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        # 设置窗口无边框和透明背景
        self.setWindowFlags(Qt.FramelessWindowHint | Qt.WindowStaysOnTopHint | Qt.Tool)
        self.setAttribute(Qt.WA_TranslucentBackground)
        
        # 创建标签用于显示翻译结果
        self.label = QLabel(self)
        self.label.setStyleSheet("""
            QLabel {
                background-color: rgba(0, 0, 0, 80);
                color: white;
                padding: 10px;
                border-radius: 5px;
                font-size: 14px;
            }
        """)
        self.setCentralWidget(self.label)
        
        # 创建信号处理器
        self.signal_handler = SignalHandler()
        self.signal_handler.show_translation_signal.connect(self.show_translation)
        
        # 隐藏窗口
        self.hide()

    def show_translation(self, text, pos):
        print(f"显示翻译结果: {text}")
        self.label.setText(text)
        self.label.adjustSize()
        self.resize(self.label.size())
        # 将窗口移动到鼠标位置下方
        self.move(pos.x(), pos.y() + 20)
        self.show()

    def mousePressEvent(self, event):
        # 点击窗口时关闭
        self.hide()

def mock_translate_api(image):
    # 模拟翻译API调用
    print("调用翻译API")
    time.sleep(0.5)  # 模拟网络延迟
    return "这是翻译结果示例\nThis is a translation example"

def handle_hotkey():
    print("快捷键被触发")
    try:
        # 获取剪贴板图片
        image = ImageGrab.grabclipboard()
        print(f"剪贴板内容类型: {type(image)}")
        
        if image:
            print("成功获取到剪贴板图片")
            # 获取当前鼠标位置
            cursor_pos = QCursor.pos()
            # 调用翻译API
            translation = mock_translate_api(image)
            # 发送信号显示翻译结果
            window.signal_handler.show_translation_signal.emit(translation, cursor_pos)
        else:
            print("剪贴板中没有图片")
            window.signal_handler.show_translation_signal.emit("请先复制图片到剪贴板", QCursor.pos())
    except Exception as e:
        print(f"发生错误: {str(e)}")
        window.signal_handler.show_translation_signal.emit(f"发生错误: {str(e)}", QCursor.pos())

def handle_quit():
    # 完全退出程序
    app.quit()

if __name__ == '__main__':
    print("程序启动")
    app = QApplication(sys.argv)
    window = TranslationWindow()
    
    # 注册快捷键 (数字5 用于显示翻译，ESC用于退出程序)
    print("注册快捷键: 5")
    keyboard.add_hotkey('5', handle_hotkey)
    keyboard.add_hotkey('esc', handle_quit)  # ESC键完全退出程序
    print("快捷键注册完成")
    5
    sys.exit(app.exec_())
