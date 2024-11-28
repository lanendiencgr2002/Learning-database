import sys
from PyQt5.QtWidgets import (QApplication, QMainWindow, QWidget, QVBoxLayout, 
                           QHBoxLayout, QLabel, QPushButton, QTextEdit)
from PyQt5.QtCore import Qt, QTimer, QRect
from PyQt5.QtGui import QPainter, QPen, QColor, QKeySequence
from PyQt5.QtWidgets import QShortcut
import win32api
import win32con
import uiautomation as auto
import ctypes

class TransparentOverlay(QWidget):
    def __init__(self):
        super().__init__()
        self.setWindowFlags(Qt.FramelessWindowHint | Qt.WindowStaysOnTopHint | Qt.Tool | Qt.WindowTransparentForInput)
        self.setAttribute(Qt.WA_TranslucentBackground)
        self.setAttribute(Qt.WA_TransparentForMouseEvents)
        self.setStyleSheet("background:transparent;")
        
        screen = QApplication.primaryScreen().geometry()
        self.setGeometry(screen)
        
        self.current_rect = None
        self.tracking = False
        self.manual_selected = False

    def paintEvent(self, event):
        if self.current_rect:
            painter = QPainter(self)
            painter.setPen(QPen(QColor(255, 0, 0), 2, Qt.SolidLine))
            painter.drawRect(self.current_rect)

    def update_rect(self, left, top, right, bottom):
        self.current_rect = QRect(left, top, right - left, bottom - top)
        self.update()

class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.initUI()
        self.overlay = TransparentOverlay()
        self.overlay.setParent(None)
        self.tracking_timer = QTimer()
        self.tracking_timer.timeout.connect(self.track_element)
        self.current_element = None
        self.manual_selected = False
        
        # 添加快捷键
        self.capture_shortcut = QShortcut(QKeySequence("9"), self)
        self.capture_shortcut.activated.connect(self.capture_element)
        
        self.track_shortcut = QShortcut(QKeySequence("8"), self)
        self.track_shortcut.activated.connect(self.toggle_tracking)
        
        # 添加鼠标滚轮事件监听
        self.wheelTimer = QTimer()
        self.wheelTimer.timeout.connect(self.check_wheel)
        self.wheelTimer.start(50)  # 每50ms检查一次
        self.last_wheel_value = win32api.GetKeyState(0x88)

    def check_wheel(self):
        try:
            if self.current_element and self.tracking_timer.isActive():
                wheel_value = win32api.GetKeyState(0x88)  # 0x88 是鼠标滚轮的虚拟键码
                if wheel_value != self.last_wheel_value:
                    if wheel_value < self.last_wheel_value:  # 向上滚动
                        parent = self.current_element.GetParentControl()
                        if parent:
                            self.manual_selected = True
                            self.current_element = parent
                            self.update_element_info()
                            print("选择父元素:", parent.ControlTypeName)
                    self.last_wheel_value = wheel_value
        except Exception as e:
            print(f"滚轮检查错误: {e}")

    def initUI(self):
        self.setWindowTitle('桌面元素选择器')
        self.setGeometry(100, 100, 600, 400)

        central_widget = QWidget()
        self.setCentralWidget(central_widget)
        layout = QVBoxLayout(central_widget)

        # 控制按钮
        btn_layout = QHBoxLayout()
        self.track_btn = QPushButton('开始追踪 (8)', self)
        self.track_btn.clicked.connect(self.toggle_tracking)
        btn_layout.addWidget(self.track_btn)

        self.capture_btn = QPushButton('捕获元素 (9)', self)
        self.capture_btn.clicked.connect(self.capture_element)
        btn_layout.addWidget(self.capture_btn)
        layout.addLayout(btn_layout)

        # 提示信息
        help_label = QLabel('使用说明：\n'
                          '1. 按8开始/停止追踪\n'
                          '2. 按9捕获当前元素\n'
                          '3. 鼠标滚轮向上：选择父元素\n'
                          '4. 自动选择最内层元素')
        layout.addWidget(help_label)

        # 元素信息显示
        self.info_display = QTextEdit()
        self.info_display.setReadOnly(True)
        layout.addWidget(self.info_display)

        # 坐标信息显示
        self.coords_label = QLabel()
        layout.addWidget(self.coords_label)

    def toggle_tracking(self):
        if not self.tracking_timer.isActive():
            self.track_btn.setText('停止追踪 (8)')
            self.overlay.show()
            self.manual_selected = False
            self.tracking_timer.start(100)
        else:
            self.track_btn.setText('开始追踪 (8)')
            self.overlay.hide()
            self.tracking_timer.stop()

    def track_element(self):
        try:
            if not self.manual_selected:
                x, y = win32api.GetCursorPos()
                element = self.get_deepest_element(x, y)
                if element:
                    self.current_element = element
                    self.update_element_info()
        except Exception as e:
            print(f"追踪错误: {e}")

    def get_deepest_element(self, x, y):
        element = auto.ControlFromPoint(x, y)
        deepest = element
        
        while True:
            children = element.GetChildren()
            found_child = False
            
            for child in children:
                if self.point_in_element(x, y, child):
                    element = child
                    deepest = child
                    found_child = True
                    break
                    
            if not found_child:
                return deepest

    def update_element_info(self):
        if self.current_element:
            rect = self.current_element.BoundingRectangle
            self.overlay.update_rect(rect.left, rect.top, rect.right, rect.bottom)
            
            # 更新坐标标签
            self.coords_label.setText(
                f'位置: ({rect.left}, {rect.top}, {rect.right}, {rect.bottom}) '
                f'[{rect.right-rect.left}x{rect.bottom-rect.top}]'
            )

    def point_in_element(self, x, y, element):
        rect = element.BoundingRectangle
        return rect.left <= x < rect.right and rect.top <= y < rect.bottom

    def capture_element(self):
        if self.current_element:
            info = (
                f"元素信息:\n"
                f"名称: {self.current_element.Name}\n"
                f"类名: {self.current_element.ClassName}\n"
                f"自动化ID: {self.current_element.AutomationId}\n"
                f"控件类型: {self.current_element.ControlTypeName}\n"
                f"位置: {self.current_element.BoundingRectangle}\n"
            )
            self.info_display.setText(info)

if __name__ == '__main__':
    app = QApplication(sys.argv)
    main_window = MainWindow()
    main_window.show()
    sys.exit(app.exec_())