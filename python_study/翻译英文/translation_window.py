from PyQt5.QtWidgets import QMainWindow, QLabel
from PyQt5.QtCore import Qt, QPoint
from config import WINDOW_STYLE

class TranslationWindow(QMainWindow):
    def __init__(self, text, pos, manager):
        super().__init__()
        self.manager = manager
        self.dragging = False
        self.offset = QPoint()
        self.init_ui(text, pos)

    def init_ui(self, text, pos):
        self.setWindowFlags(Qt.FramelessWindowHint | Qt.WindowStaysOnTopHint)
        self.setAttribute(Qt.WA_TranslucentBackground)
        
        self.label = QLabel(self)
        self.label.setStyleSheet(WINDOW_STYLE)
        self.label.setText(text)
        self.label.adjustSize()
        self.setCentralWidget(self.label)
        
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
        try:
            if self in self.manager.windows:
                self.manager.windows.remove(self)
            print("关闭一个翻译窗口")
        except Exception as e:
            print(f"关闭窗口时出错: {e}")
        event.accept() 