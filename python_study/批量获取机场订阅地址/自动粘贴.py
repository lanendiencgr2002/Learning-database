import re

import keyboard
import pyperclip
import time

def 监听热键():
    keyboard.add_hotkey('ctrl+v', 检测是否按下Ctrl_V)

def 检测是否按下Ctrl_V():
    global 是否按下Ctrl_V
    是否按下Ctrl_V = True
    print("检测到按下Ctrl+V")

# 读取文件内容
with open('机场订阅地址.txt', 'r', encoding='utf-8') as file:
    content = file.read()

# 提取不以#开头的链接,包括带日期的行
pattern = r'^(?!#).*?(https?://\S+|vmess://\S+|hysteria2://\S+|ss://\S+|vless://\S+)'
links = re.findall(pattern, content, re.MULTILINE)

def 处理链接(links):
    global 是否按下Ctrl_V
    for index, link in enumerate(links, 1):
        pyperclip.copy(link)
        print(f"已复制第 {index} 个链接: {link}")
        print("请粘贴链接 (Ctrl+V)...")
        
        是否按下Ctrl_V = False
        while not 是否按下Ctrl_V:
            time.sleep(0.1)
        
        if index < len(links):
            pyperclip.copy(links[index])
            print(f"已准备第 {index+1} 个链接: {links[index]}")
        
        time.sleep(0.5)

def main():
    global 是否按下Ctrl_V
    是否按下Ctrl_V = False
    
    监听热键()
    处理链接(links)
    print("所有链接已处理完毕")

if __name__ == "__main__":
    main()