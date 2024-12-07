import sys
from PyQt5.QtWidgets import QApplication, QMainWindow, QLabel, QSystemTrayIcon, QMenu, QMessageBox  # 导入Qt界面组件
from PyQt5.QtCore import Qt, QPoint, pyqtSignal, QObject  # 导入Qt核心功能
from PyQt5.QtGui import QCursor, QIcon  # 导入光标相关功能
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
    def __init__(self, text, pos, manager):  # 添加manager参数
        super().__init__()
        self.manager = manager  # 保存manager引用
        self.dragging = False
        self.offset = QPoint()
        self.init_ui(text, pos)

    def init_ui(self, text, pos):
        # 设置窗口属性
        self.setWindowFlags(Qt.FramelessWindowHint | Qt.WindowStaysOnTopHint)
        self.setAttribute(Qt.WA_TranslucentBackground)
        
        # 创建并设置标签
        self.label = QLabel(self)
        self.label.setStyleSheet(WINDOW_STYLE)
        self.label.setText(text)
        self.label.adjustSize()
        self.setCentralWidget(self.label)
        
        # 设置窗口大小和位置
        self.resize(self.label.size())
        self.move(pos.x(), pos.y() + 20)

    def mousePressEvent(self, event):
        if event.button() == Qt.LeftButton:
            self.dragging = True
            self.offset = event.pos()

    def mouseReleaseEvent(self, event):
        if event.button() == Qt.LeftButton:
            self.dragging = False

    def mouseMoveEvent(self, event):
        if self.dragging:
            self.move(event.globalPos() - self.offset)

    def mouseDoubleClickEvent(self, event):
        if event.button() == Qt.LeftButton:
            self.close()

    def closeEvent(self, event):
        # 从manager的windows列表中移除自己
        try:
            if self in self.manager.windows:
                self.manager.windows.remove(self)
            print("关闭一个翻译窗口")
        except Exception as e:
            print(f"关闭窗口时出错: {e}")
        event.accept()

class TranslationManager(QMainWindow):
    def __init__(self):
        super().__init__()
        self.init_signals()
        self.translator = DeepLTranslator()
        self.is_first_run = True
        self.last_text = ""
        self.windows = []
        self.translation_lock = threading.Lock()
        self.running = True
        
        # 设置窗口标志,使其成为一个持久存在的主窗口
        self.setWindowFlags(Qt.Tool)  # 使用 Tool 标志使窗口不显示在任务栏
        self.setAttribute(Qt.WA_QuitOnClose, False)  # 关闭时不退出程序
        
        self.start_listeners()
        self.create_tray_icon()
        self.hide()

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
        chinese_chars = len([c for c in text if '\u4e00' <= c <= '\u9fff'])  # 统计���文字符数
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
        while self.running:  # 修改循环条件
            try:
                if self.is_first_run:  # 首次运行只记录当前剪贴板内容
                    self.last_text = self.get_clipboard_text()
                    self.is_first_run = False
                    time.sleep(CLIPBOARD_CHECK_INTERVAL)
                    continue
                
                current_text = self.get_clipboard_text()  # 获取当前剪贴板内容
                # 使用锁来确保翻译过程的原子性
                if current_text and current_text != self.last_text and self.is_translated(current_text):
                    # 尝试获取锁
                    if self.translation_lock.acquire(blocking=False):  # 非阻塞方式获取锁
                        try:
                            print(f"检测到新的英文内容: {current_text[:50]}...")
                            cursor_pos = QCursor.pos()  # 获取当前鼠标位置
                            translation = self.translate_text(current_text)  # 翻译文本
                            self.signal_handler.show_translation_signal.emit(translation, cursor_pos)  # 发送显示信号
                            self.last_text = current_text
                        finally:
                            self.translation_lock.release()  # 确保锁被释放
                    else:
                        print("上一个翻译正在进行中，跳过当前翻译")
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
        # 创建新窗口时传入manager引用
        new_window = TranslationWindow(text, pos, self)
        new_window.show()
        self.windows.append(new_window)

    def create_tray_icon(self):
        # 创建托盘图标
        self.tray_icon = QSystemTrayIcon(self)
        self.tray_icon.setIcon(QIcon("icon.png"))
        
        # 创建托盘菜单
        tray_menu = QMenu()
        # 添加"关闭所有窗口"选项
        close_windows_action = tray_menu.addAction("关闭所有翻译窗口")
        close_windows_action.triggered.connect(self.close_all_windows)
        # 添加分隔线
        tray_menu.addSeparator()
        # 添加退出选项
        exit_action = tray_menu.addAction("退出程序")
        exit_action.triggered.connect(self.close_application)
        
        self.tray_icon.setContextMenu(tray_menu)
        self.tray_icon.show()

    def close_application(self):
        # 修改退出逻辑
        reply = QMessageBox.question(
            self, 
            '确认退出', 
            '确定要退出翻译程序吗？',
            QMessageBox.Yes | QMessageBox.No,
            QMessageBox.No
        )
        
        if reply == QMessageBox.Yes:
            self.running = False
            for window in self.windows[:]:
                window.close()
            self.windows.clear()
            QApplication.instance().quit()
        else:
            # 如果选择不退出，只关闭所有翻译窗口
            for window in self.windows[:]:
                window.close()
            self.windows.clear()

    def closeEvent(self, event):
        # 点击关闭按钮时只隐藏窗口，不退出程序
        event.ignore()
        self.hide()
        # 关闭所有翻译窗口但保持程序运行
        for window in self.windows[:]:
            window.close()
        self.windows.clear()

    def close_all_windows(self):
        # 新增方法：只关闭所有翻译窗口
        for window in self.windows[:]:
            window.close()
        self.windows.clear()

if __name__ == '__main__':
    print("程序启动")
    app = QApplication(sys.argv)
    manager = TranslationManager()  # 使用新的类名
    sys.exit(app.exec_())
