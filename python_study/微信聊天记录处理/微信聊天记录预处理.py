import os
def 处理整个微信聊天记录():
    # 读取微信聊天记录
    with open('安嘻嘻.txt', 'r', encoding='utf-8') as file:
        content = file.read()
    # 预处理聊天记录
    processed_lines = []
    是否保留当前行 = False
    for line in content.split('\n'):
        # 跳过空行
        if not line.strip():
            continue
        # 如果行包含时间戳（以年份开头）
        if line.startswith('2024'):
            # 检查是否是安嘻嘻的消息
            if '安嘻嘻' in line:
                是否保留当前行 = True
        else:
            if 是否保留当前行:
                processed_lines.append(line)
                是否保留当前行 = False
    # 将处理后的内容写入新文件
    with open('安嘻嘻的消息.txt', 'w', encoding='utf-8') as file:
        file.write('\n'.join(processed_lines))
def 将一个大的文本txt拆分(多少行一个文件=230):
    with open('安嘻嘻的消息.txt', 'r', encoding='utf-8') as file:
        content = file.read()
    lines = content.split('\n')
    # 如果不存在文件夹，则创建文件夹
    if not os.path.exists('拆分后的文件'):
        os.makedirs('拆分后的文件')
    for i in range(0, len(lines), 多少行一个文件):
        with open(f'拆分后的文件/安嘻嘻的消息_{i//多少行一个文件}.txt', 'w', encoding='utf-8') as file:
            file.write('\n'.join(lines[i:i+多少行一个文件]))

if __name__ == '__main__':
    # 处理整个微信聊天记录()
    将一个大的文本txt拆分()
