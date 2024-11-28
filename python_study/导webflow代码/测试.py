# a='''
# /https/34263054423230556a6d256d76542e4bfb8d12a4803d0b305746a422383fb5/styles/js/easyui-component.js?vpn-7&amp;v=20180227
# '''
# b='''
# <script type="text/javascript" src="/https/34263054423230556a6d256d76542e4bfb8d12a4803d0b305746a422383fb5/styles/js/ntss.js?vpn-7&amp;v=20180126"></script>
# '''
# print(b)
# from bs4 import BeautifulSoup
# soup=BeautifulSoup(b,'lxml')
# qwq=soup.find_all(name="script")
# for i in qwq:print(i['src'])

import html

# 示例字符串
example_string = "Some example text with special characters like & and <"
# 转义特殊字符
escaped_string = html.escape(example_string)
print(escaped_string)  # 输出转义后的字符串
# 反转义特殊字符
unescaped_string = html.unescape(escaped_string)
print(unescaped_string)  # 输出原始字符串
