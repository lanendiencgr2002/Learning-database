a = ''
with open('收集.txt', 'r', encoding='utf-8') as f:
    a = f.read()
    print(a)  # 如果您仍然想打印文件内容

print(a.count('ive'))
