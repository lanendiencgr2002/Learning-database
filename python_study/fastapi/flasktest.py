import time

from flask import Flask, render_template, request, abort, jsonify, Response, session

app = Flask(__name__,static_folder='static')
app.config['JSON_AS_ASCII'] = False
# 使用文件系统存储 session
app.config['SESSION_TYPE'] = 'filesystem'

@app.route('/', methods=['GET'])
def hello():
    print('开始处理请求')
    time.sleep(3)
    print('等待结束成功')
    return {}

@app.route('/e', methods=['GET'])
def helo():
    print('开始处理请求e')
    time.sleep(3)
    print('等待结束成功e')
    return {}

# 启用会话功能
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
