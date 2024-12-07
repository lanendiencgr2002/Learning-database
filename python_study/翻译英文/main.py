import sys
import time
import threading
import win32clipboard
from collections import OrderedDict
from PyQt5.QtWidgets import QApplication, QMainWindow, QSystemTrayIcon, QMenu, QMessageBox
from PyQt5.QtCore import Qt, QPoint, pyqtSignal, QObject
from PyQt5.QtGui import QCursor, QIcon

from config import *
from translation_window import TranslationWindow
from 接口3 import DeepLTranslator

class SignalHandler(QObject):
    show_translation_signal = pyqtSignal(str, QPoint)

class TranslationManager(QMainWindow):
    def __init__(self):
        super().__init__()
        self.init_ui()
        self.init_signals()
        self.init_translator()
        self.init_cache()
        self.start_listeners()

    def init_ui(self):
        self.setWindowFlags(Qt.Tool)
        self.setAttribute(Qt.WA_QuitOnClose, False)
        self.create_tray_icon()
        self.hide()

    def init_signals(self):
        self.signal_handler = SignalHandler()
        self.signal_handler.show_translation_signal.connect(self.show_translation)

    def init_translator(self):
        self.translator = DeepLTranslator()
        self.translation_lock = threading.Lock()
        self.running = True
        self.windows = []

    def init_cache(self):
        self.translation_cache = OrderedDict()
        self.is_first_run = True
        self.last_text = ""

    def create_tray_icon(self):
        self.tray_icon = QSystemTrayIcon(self)
        self.tray_icon.setIcon(QIcon(ICON_PATH))
        
        tray_menu = QMenu()
        close_windows_action = tray_menu.addAction("关闭所有翻译窗口")
        close_windows_action.triggered.connect(self.close_all_windows)
        tray_menu.addSeparator()
        exit_action = tray_menu.addAction("退出程序")
        exit_action.triggered.connect(self.close_application)
        
        self.tray_icon.setContextMenu(tray_menu)
        self.tray_icon.show()

    def start_listeners(self):
        self.clipboard_thread = threading.Thread(target=self.clipboard_listener, daemon=True)
        self.clipboard_thread.start()

    def is_translated(self, text):
        if not text or len(text.strip()) == 0:
            return False
        
        english_chars = len([c for c in text if c.isalpha() and ord(c) < 128])
        chinese_chars = len([c for c in text if '\u4e00' <= c <= '\u9fff'])
        total_valid_chars = english_chars + chinese_chars
        
        if total_valid_chars == 0:
            return False
        
        english_ratio = english_chars / total_valid_chars
        return english_ratio >= ENGLISH_THRESHOLD

    def get_clipboard_text(self):
        try:
            win32clipboard.OpenClipboard()
            try:
                return win32clipboard.GetClipboardData(win32clipboard.CF_UNICODETEXT)
            except:
                return ""
            finally:
                win32clipboard.CloseClipboard()
        except Exception as e:
            print(ERROR_MESSAGES["clipboard_error"].format(e))
            return ""

    def clipboard_listener(self):
        while self.running:
            try:
                if self.is_first_run:
                    self.last_text = self.get_clipboard_text()
                    self.is_first_run = False
                    time.sleep(CLIPBOARD_CHECK_INTERVAL)
                    continue
                
                current_text = self.get_clipboard_text()
                
                if current_text and current_text != self.last_text and self.is_translated(current_text):
                    if self.translation_lock.acquire(blocking=False):
                        try:
                            print(f"检测到新的英文内容: {current_text[:50]}...")
                            cursor_pos = QCursor.pos()
                            translation = self.get_translation(current_text)
                            self.signal_handler.show_translation_signal.emit(translation, cursor_pos)
                            self.last_text = current_text
                        finally:
                            self.translation_lock.release()
                    else:
                        print("上一个翻译正在进行中，跳过当前翻译")
            except Exception as e:
                print(f"剪贴板监听错误: {e}")
            time.sleep(CLIPBOARD_CHECK_INTERVAL)

    def get_translation(self, text):
        # 检查缓存
        if text in self.translation_cache:
            return self.translation_cache[text]
        
        try:
            translation = self.translator.translate(text)
            result = f"原文: {text}\n译文: {translation}"
            
            # 更新缓存
            self.translation_cache[text] = result
            if len(self.translation_cache) > MAX_CACHE_SIZE:
                self.translation_cache.popitem(last=False)
                
            return result
        except Exception as e:
            return ERROR_MESSAGES["translation_error"].format(e)

    def show_translation(self, text, pos):
        print(f"显示翻译结果: {text[:50]}...")
        new_window = TranslationWindow(text, pos, self)
        new_window.show()
        self.windows.append(new_window)

    def close_application(self):
        reply = QMessageBox.question(
            self, 
            '确认退出', 
            '确定要退出翻译程序吗？',
            QMessageBox.Yes | QMessageBox.No,
            QMessageBox.No
        )
        
        if reply == QMessageBox.Yes:
            self.running = False
            self.close_all_windows()
            QApplication.instance().quit()

    def closeEvent(self, event):
        event.ignore()
        self.hide()
        self.close_all_windows()

    def close_all_windows(self):
        for window in self.windows[:]:
            window.close()
        self.windows.clear()

if __name__ == '__main__':
    print("程序启动")
    app = QApplication(sys.argv)
    app.setQuitOnLastWindowClosed(False)
    manager = TranslationManager()
    sys.exit(app.exec_())
