import html
import os
import re
import requests
from bs4 import BeautifulSoup
def 获取网页源代码(url):
    try:
        response = requests.get(url, timeout=5)
        if response.status_code == 200:
            # Use the apparent encoding detected by requests to decode the content
            response.encoding = response.apparent_encoding
            return response.text
        else: raise requests.RequestException(f"Error: {response.status_code}")
    except requests.RequestException as e: raise e
def 获取所有js的链接():
    # 获取 script src = 这个链接
    所有js链接=[]
    for i in soup.find_all(name="script"):
        #获取src链接
        try:js链接=i.attrs['src']
        except:continue
        #还原特殊字符
        还原特殊字符后的js链接 = html.escape(js链接)
        # 如果链接里没js 则continue
        if "js" not in js链接:continue
        # 添加到可修改链接
        try:所有js链接.append(还原特殊字符后的js链接)
        except:pass
    return 所有js链接
def 获取所有css的链接():
    # 获取 link href = 这里的css链接
    所有css链接=[]
    for i in soup.find_all(name="link"):
        #获取href链接
        try:css链接=i.attrs['href']
        except:continue
        #还原特殊字符
        还原特殊字符=html.escape(css链接)
        # 如果链接里没css 则contine
        if "css" not in css链接: continue
        # 添加到可修改链接
        try:所有css链接.append(还原特殊字符)
        except:pass
    return 所有css链接
def 获取js的源代码(所有js链接):
    global url
    获取js的源代码=[]
    可修改的js链接=[]
    for i in 所有js链接:
        j=i
        try:
            if(i[:2]=="//"):j=f"https:{i}"
            elif(i[0]=='/'):j=f"{url}{i}"
            获取js的源代码.append(获取网页源代码(j))
            可修改的js链接.append(i)
        except Exception as e:print(f'获取js的源代码失败:{i} {e}')
    return 获取js的源代码,可修改的js链接
def 获取css的源代码(所有css链接):
    global url
    获取css的源代码=[]
    可修改的css链接=[]
    for i in 所有css链接:
        j=i
        try:
            if (i[:2] == "//"):j = f"https:{i}"
            elif (i[0] == f'/'):j = f"{url}{i}"
            获取css的源代码.append(获取网页源代码(j))
            可修改的css链接.append(i)
        except Exception as e:print(f'获取css的源代码失败:{i} {e}')
    return 获取css的源代码,可修改的css链接

def 获取所有图像的链接():
    所有图像链接 = []
    for img in soup.find_all('img'):
        src = img.get('src')
        if src and src.endswith('.svg'):  # 可以根据需要调整条件
            所有图像链接.append(src)
    return 所有图像链接

def 获取并保存图像(所有图像链接):
    可修改的图片链接=[]
    本地所有图片链接=[]
    if not os.path.exists("static/img"):  # 创建保存图像的目录
        os.makedirs("static/img")
    for i, 图像链接 in enumerate(所有图像链接, start=1):
        try:
            if 图像链接.startswith('//'):
                图像链接 = 'https:' + 图像链接
            elif 图像链接.startswith('/'):
                图像链接 = url + 图像链接
            图像响应 = requests.get(图像链接)
            if 图像响应.status_code == 200:
                图像路径 = f'static/img/img{i}.svg'
                with open(图像路径, 'wb') as f:
                    f.write(图像响应.content)
                本地所有图片链接.append(图像路径)
                可修改的图片链接.append(图像链接)
                print(f'已保存: {图像路径}')
            else:
                print(f'图像下载失败: {图像链接}')
        except Exception as e:
            print('获取当前链接发生错误:',+{e})
            continue
    return 本地所有图片链接,可修改的图片链接


def 写成文件(所有js的源代码,所有css的源代码,网页源代码):
    # 没static建一个
    if not os.path.exists("static"):os.makedirs("static")
    for i,j in enumerate(所有js的源代码,start=1):
        with open(f'static/js{i}.js','w',encoding='utf-8') as f:
            f.write(j)
        print(f'已写入static/js{i}.js')
    for i, j in enumerate(所有css的源代码, start=1):
        with open(f'static/css{i}.css','w',encoding='utf-8') as f:
            f.write(j)
        print(f'已写入static/css{i}.css')
    with open('index.html','w',encoding='utf-8') as f:
        f.write(网页源代码)
def 处理createBadge(js的源代码):
    replaced_function = re.sub(r"(function createBadge\(\).*?return.*?})", r"function createBadge() {return};",
                               js的源代码, flags=re.DOTALL)
    return replaced_function
def 处理所有createBadge(所有js的源代码):
    for i in 所有js的源代码:
        索引=所有js的源代码.index(i)
        所有js的源代码[索引]=处理createBadge(i)
    return 所有js的源代码
# 替换源代码中的可以获取源码的js和css链接
def 替换源代码中的可以获取源码的js和css链接(网页源代码,可修改的js链接,可修改的css链接,可修改的图像链接):
    for i in 可修改的js链接:
        网页源代码=网页源代码.replace(i,f'static/js{可修改的js链接.index(i)+1}.js')
    for i in 可修改的css链接:
        网页源代码=网页源代码.replace(i,f'static/css{可修改的css链接.index(i)+1}.css')
    for i in 可修改的图像链接:
        网页源代码 = 网页源代码.replace(i, f'static/img/img{可修改的图像链接.index(i) + 1}.svg')
    return 网页源代码


# url = 'https://lanens-exceptional-site-cc8a8f326478c6b.webflow.io/'  # 替换为您想获取源代码的网页的URL
# 网页源代码=获取网页源代码(url)
# 网页源代码=urll.geturl()

def 开始生成资源():
    global soup,网页源代码
    soup = BeautifulSoup(网页源代码, 'lxml')

    所有js链接: list = 获取所有js的链接()
    所有css链接: list = 获取所有css的链接()
    所有图像链接: list = 获取所有图像的链接()  # 新增
    所有js的源代码, 可修改的js链接 = 获取js的源代码(所有js链接)
    所有css的源代码, 可修改的css链接 = 获取css的源代码(所有css链接)
    所有js的源代码 = 处理所有createBadge(所有js的源代码)
    本地所有图像链接,可修改的图像链接=获取并保存图像(所有图像链接)  # 新增

    网页源代码 = 替换源代码中的可以获取源码的js和css链接(网页源代码, 可修改的js链接, 可修改的css链接,可修改的图像链接)
    写成文件(所有js的源代码, 所有css的源代码, 网页源代码)  # 原有代码
    print("结束了")

if __name__=="__main__":
    global url,网页源代码
    # url=input("请输入url地址:")
    url = 'https://lanens-exceptional-site-cc8a8f326478c6b.webflow.io/'  # 替换为您想获取源代码的网页的URL
    网页源代码 = 获取网页源代码(url)
    开始生成资源()
