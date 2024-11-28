from flask import Flask, render_template, request
import 测试 as tt
app = Flask(__name__)

@app.route('/', methods=['GET', 'POST'])
def index():
    if request.method == 'GET':
        # 如果是GET请求，只需渲染页面，不需要传递额外的参数
        return render_template('test1.html')
    elif request.method == 'POST':
        # 从表单获取文本内容
        text = request.form.get('content')
        tt.发送信息(text)
        response=tt.获取最后的回答()
        print(text)
        # 将变量传递给HTML模板
        # 注意：确保您的HTML模板内有逻辑来显示这个变量
        return render_template('test1.html', a=response)  # 将用户输入作为a变量传递给模板

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
