import re
import os
from PySide2.QtWidgets import QApplication, QMainWindow, QFileDialog, QListWidget, QPushButton, QVBoxLayout, QHBoxLayout, QWidget, QMessageBox
from PySide2.QtCore import Qt

class Markdown标题处理器(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Markdown 标题处理器")
        self.setGeometry(100, 100, 800, 600)
        self.文件路径 = None
        self.标题列表 = []
        self.初始化界面()

    def 初始化界面(self):
        中心部件 = QWidget()
        self.setCentralWidget(中心部件)
        布局 = QVBoxLayout()

        self.文件按钮 = QPushButton("选择 Markdown 文件")
        self.文件按钮.clicked.connect(self.选择文件)
        self.标题列表控件 = QListWidget()
        self.标题列表控件.setSelectionMode(QListWidget.MultiSelection)

        按钮布局 = QHBoxLayout()
        self.升级按钮 = QPushButton("升级选中标题")
        self.降级按钮 = QPushButton("降级选中标题")
        self.升级按钮.clicked.connect(lambda: self.处理标题('升级'))
        self.降级按钮.clicked.connect(lambda: self.处理标题('降级'))

        布局.addWidget(self.文件按钮)
        布局.addWidget(self.标题列表控件)
        按钮布局.addWidget(self.升级按钮)
        按钮布局.addWidget(self.降级按钮)
        布局.addLayout(按钮布局)
        中心部件.setLayout(布局)

    def 选择文件(self):
        self.文件路径, _ = QFileDialog.getOpenFileName(self, "选择 Markdown 文件", "", "Markdown Files (*.md)")
        if self.文件路径:
            self.标题列表 = self.提取标题(self.文件路径)
            self.更新标题列表显示()

    def 提取标题(self, 文件路径):
        with open(文件路径, 'r', encoding='utf-8') as 文件:
            行列表 = 文件.readlines()
        标题 = []
        代码块内 = False
        for 行号, 行 in enumerate(行列表, 1):
            if 行.strip().startswith('```'):
                代码块内 = not 代码块内
                continue
            if not 代码块内:
                匹配 = re.match(r'^(#{1,4})\s(.+)$', 行.strip())
                if 匹配:
                    级别 = len(匹配.group(1))
                    文本 = 匹配.group(2)
                    标题.append((行号, 级别, 文本))
        return 标题

    def 更新标题列表显示(self):
        self.标题列表控件.clear()
        for 行号, 级别, 文本 in self.标题列表:
            self.标题列表控件.addItem(f"(行 {行号}) {'#'*级别} {文本}")

    def 处理标题(self, 操作):
        if not self.文件路径:
            QMessageBox.warning(self, "警告", "请先选择一个 Markdown 文件")
            return
        选中项目 = self.标题列表控件.selectedItems()
        if not 选中项目:
            QMessageBox.warning(self, "警告", "请选择要处理的标题")
            return
        选中索引 = [self.标题列表控件.row(项目) for 项目 in 选中项目]
        选中标题 = [self.标题列表[i] for i in 选中索引]
        self.修改标题(self.文件路径, 选中标题, 操作)
        self.标题列表 = self.提取标题(self.文件路径)
        self.更新标题列表显示()
        QMessageBox.information(self, "成功", "处理完成")

    def 修改标题(self, 文件路径, 选中标题, 操作):
        with open(文件路径, 'r', encoding='utf-8') as 文件:
            行列表 = 文件.readlines()
        for 行号, 级别, _ in 选中标题:
            if 操作 == '升级' and 级别 > 1:
                行列表[行号-1] = 行列表[行号-1][1:]
            elif 操作 == '降级' and 级别 < 4:
                行列表[行号-1] = '#' + 行列表[行号-1]
        with open(文件路径, 'w', encoding='utf-8') as 文件:
            文件.writelines(行列表)

if __name__ == "__main__":
    应用 = QApplication([])
    窗口 = Markdown标题处理器()
    窗口.show()
    应用.exec_()
