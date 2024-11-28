import time

def 通用等待(检查函数, 错误信息, 超时=10):
    剩余时间 = 超时
    while True:
        try:
            if 检查函数():
                break
            time.sleep(1)
            剩余时间 -= 1
            if 剩余时间 < 0:
                raise Exception(f"{错误信息}，超时{超时}秒，退出循环。")
        except Exception as e:
            if 剩余时间 < 0:
                raise e
            time.sleep(1)
            剩余时间 -= 1

def 等待元素加载完成(元素, 条件:str, 超时=10):
    """等待页面元素加载完成"""
    def 检查元素存在():
        元素.ele(条件)
        return True
    
    通用等待(
        检查元素存在,
        f"等待元素加载完成：{条件}",
        超时
    )

def 等待跳转到指定页面(元素, 目标url列表, 超时=10):
    """等待页面跳转到目标URL"""
    def 检查页面URL():
        if 元素.url in 目标url列表:
            return True
        return False
    
    通用等待(
        检查页面URL,
        f"等待跳转目标页面：{元素.url}",
        超时
    )

def 打开指定页面并等待跳转到指定页面(元素, 目标url):
    打开页面(元素, 目标url)
    等待跳转到指定页面(元素, [目标url])

def 打开页面(元素, 目标url):
    元素.get(目标url)

def 找一个元素(元素, 条件:str):
    try:
        元素=元素.ele(条件)
        return 元素
    except:
        return None

def 找多个元素(元素, 条件:str):
    return 元素.eles(条件)

def 获取元素文本(元素):
    return 元素.text.strip()

def 获取元素地址(元素):
    return 元素.link

def 找一个元素的属性(元素, 条件, 属性):
    try:
        if 条件==None:
            return 元素.attr(属性)
        else:
            return 找一个元素(元素,条件).attr(属性)
    except:
        return None

def 找一个元素的文本(元素,条件:str):
    return 找一个元素的属性(元素,条件,'text')

def 创建多个标签页对象(元素,标签页数量=5):
    return [元素.new_tab() for _ in range(标签页数量)]

def 开始监听数据包(元素):
    元素.listen.start()

def 获取数据包(元素):
    '''
    获取数据包,返回一个可迭代对象
    '''
    return 元素.listen.steps()

def 结束监听数据包(元素):
    元素.listen.stop()

