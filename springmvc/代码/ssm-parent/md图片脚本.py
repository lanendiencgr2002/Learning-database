import os
import json
import sys

def 更新markdown图片配置(markdown文件路径):
    # 确保当前文件是.md结尾
    if not markdown文件路径.endswith('.md'):
        print("当前文件不是Markdown文件")
        return

    markdown文件名 = os.path.splitext(os.path.basename(markdown文件路径))[0]
    资源文件夹 = f"{markdown文件名}.assets"
    
    # 确保资源文件夹存在
    完整资源路径 = os.path.join(os.path.dirname(markdown文件路径), 资源文件夹)
    os.makedirs(完整资源路径, exist_ok=True)
    
    # 更新全局MarkdownImage配置
    设置文件路径 = os.path.expanduser("~\\AppData\\Roaming\\Cursor\\User\\settings.json")
    
    try:
        if os.path.exists(设置文件路径):
            with open(设置文件路径, 'r', encoding='utf-8') as 文件:
                设置内容 = 文件.read()
                # print(f"全局settings.json 内容:\n{设置内容}")  # 打印文件内容以进行调试
                设置 = json.loads(设置内容)
                # 更新Markdown-image的设置
                设置['markdown-image.local.path'] = 资源文件夹
                # os.makedirs(os.path.dirname(设置文件路径), exist_ok=True)
                with open(设置文件路径, 'w', encoding='utf-8') as 文件:
                    json.dump(设置, 文件, indent=4, ensure_ascii=False)
                print(f"已更新全局Markdown-image设置：路径设为 {资源文件夹}")
        else:
            print(f"全局settings.json 文件不存在")
            # print(f"全局settings.json 文件不存在，将创建新文件")
            # 设置 = {}
    except json.JSONDecodeError as e:
        print(f"警告：{设置文件路径} 文件格式错误。错误信息：{str(e)}")
        # print("将创建新的设置。")
        # 设置 = {}
    
    

if __name__ == "__main__":
    if len(sys.argv) > 1:
        更新markdown图片配置(sys.argv[1])
    else:
        print("请提供Markdown文件路径作为参数")