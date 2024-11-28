import keyboard
import pyperclip
import openai


def 问gpt问题(问题,需求="无",模型="gpt-3.5-turbo"):
    openai.api_key = "sk-ATXhpDsohKCPBxdGfLsHXFUf0Q8zDhNxUNjwQOvLWwOLoJMP"
    openai.api_base = 'https://api.chatanywhere.tech'
    response = openai.ChatCompletion.create(
        model=模型,
        messages=[
           # {"role": "system", "content": "这是c++代码，将这些代码格式化,有必要要加上using namespace std;,不用回复我下面是格式化后的代码，但是要加上代码块"},
           #  {"role": "system",
           #   "content": "将这些代码格式化,返回我加上java代码块的结果"},
           #  {"role": "system",
           #      "content": "将这些文本代码格式化"},
           #  {"role": "system",
           #   "content": "用清华源pip这个"},
            # 需求
             {"role": "system",
              "content": f"{需求}"},
            {"role": "user", "content":问题},
        ]
    )
    return response["choices"][0]["message"]["content"]
def 将文本放进剪贴板(文本):
    pyperclip.copy(文本)
def 从剪贴板上获取最近文本():
    return pyperclip.paste()
def 开始监听按键并执行函数(快捷键,函数名,参数元组=()):
    keyboard.add_hotkey(快捷键, 函数名, args=参数元组)
    keyboard.wait("ctrl+esc")
def 将剪贴板的字格式化(需求,模型):
    代码 = 从剪贴板上获取最近文本()
    格式化后的代码 = 问gpt问题(代码,需求,模型)
    将文本放进剪贴板(格式化后的代码)
    print('问gpt结束返回文本：',格式化后的代码)

if __name__=='__main__':
    pass

    # 代码=从剪贴板上获取最近文本()
    #
    # 格式化后的代码=问gpt问题(代码)
    # 将文本放进剪贴板(格式化后的代码)
    # print(问gpt问题(代码))
