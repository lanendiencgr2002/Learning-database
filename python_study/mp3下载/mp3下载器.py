import requests
from PySide2.QtGui import QFontDatabase
from PySide2.QtUiTools import QUiLoader
from PySide2.QtWidgets import QApplication, QListWidgetItem

def get网易云接口(歌名或者歌手,第n首=1):
    url=f"https://api.lolimi.cn/API/wydg/api.php?msg={歌名或者歌手}&n={第n首} 88"
    try:
        response = requests.get(url)
        response.raise_for_status()
        data = response.json()
        return data
    except requests.RequestException as e:
        print(f"请求发生错误: {e}")
        return None
def getqq音乐接口(歌名或者歌手,第n首=1):
    url=f"http://ovoa.cc/api/QQmusic.php?msg={歌名或者歌手}&n={第n首}&type= 62"
    try:
        response = requests.get(url)
        response.raise_for_status()
        data = response.json()
        return data
    except requests.RequestException as e:
        print(f"请求发生错误: {e}")
        return None
class Stats:
    def __init__(self):
        self.ui = QUiLoader().load('./ui/界面.ui')
        self.ui.pushButton_2.clicked.connect(self.pushButton_2)
        self.ui.pushButton.clicked.connect(self.pushButton)
        self.所有歌曲数据 = []
    def pushButton(self):
        if self.ui.radioButton.isChecked():
            # 查询多少首
            查询多少首=int(self.ui.lineEdit_2.text())
            # 网易云下载接口
            获取歌名或者歌手名=self.ui.lineEdit.text()
            self.所有歌曲数据=[]
            for i in range(1,查询多少首+1):
                返回结果=get网易云接口(获取歌名或者歌手名,i)
                self.所有歌曲数据.append(返回结果)
            self.ui.listWidget.clear()
            for 歌曲数据 in self.所有歌曲数据:
                歌名 = 歌曲数据['name']
                歌手 = 歌曲数据['author']
                item_text = f"歌名：{歌名:<10} 歌手：{歌手:<10}"
                self.ui.listWidget.addItem(item_text)
            print(self.所有歌曲数据)
        if self.ui.radioButton_2.isChecked():
            # 查询多少首
            查询多少首 = int(self.ui.lineEdit_2.text())
            # 网易云下载接口
            获取歌名或者歌手名 = self.ui.lineEdit.text()
            self.所有歌曲数据=[]
            for i in range(1,查询多少首+1):
                返回结果 = getqq音乐接口(获取歌名或者歌手名,i)
                self.所有歌曲数据.append(返回结果)
            print(self.所有歌曲数据)
            self.ui.listWidget.clear()
            for 歌曲数据 in self.所有歌曲数据:
                歌名 = 歌曲数据['data']['songname']
                歌手 = 歌曲数据['data']['name']
                item_text = f"歌名：{歌名:<10} 歌手：{歌手:<10}"
                self.ui.listWidget.addItem(item_text)
            print(self.所有歌曲数据)
    def pushButton_2(self):
        current_item = self.ui.listWidget.currentItem()
        if current_item:
            row = self.ui.listWidget.row(current_item)
            text = current_item.text()
            try:
                print(self.所有歌曲数据[row]["mp3"])
            except:
                print(self.所有歌曲数据[row]["data"]["src"])
        else:
            print("没有选中任何项")



app = QApplication([])
stats = Stats()
stats.ui.show()
app.exec_()