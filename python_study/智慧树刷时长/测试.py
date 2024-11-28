from DrissionPage import ChromiumPage, ChromiumOptions
import drissionpage_utils
import re
import time
from concurrent.futures import ThreadPoolExecutor
from concurrent.futures import wait
def 初始化dp():
    # co = ChromiumOptions().set_local_port(9222)
    # page = ChromiumPage(addr_or_opts=co)
    page = ChromiumPage()
    return page
page=初始化dp()

def 点下一个(bar):
    drissionpage_utils.找一个元素(bar,'#nextBtn').click()
def 切换1点5倍数(bar):
    
    drissionpage_utils.找一个元素(bar,'.speedBox').click()
    drissionpage_utils.找一个元素(bar,'.speedTab speedTab15').click()
def 找bar元素激活(元素): 
    元素.run_js("""
            var bar = document.querySelector('.controlsBar');
            if (bar) {
                // 强制显示
                bar.style.display = 'block';
                bar.style.visibility = 'visible';
                bar.style.opacity = '1';
                // 防止被其他脚本修改
                Object.defineProperty(bar.style, 'display', {
                    get: function() { return 'block'; },
                    set: function() { /* 忽略设置 */ }
                });
            }
        """)
def 点播放(元素):
    if 元素.ele('#playButton',timeout=3):
        元素.ele('#playButton').click()
def 点流畅(元素):
    drissionpage_utils.找一个元素(元素,'.definiBox').click()
    drissionpage_utils.找一个元素(元素,'text=流畅').click()
def 获取已播放进度(元素): 
    已播放元素 = drissionpage_utils.找一个元素(元素,'.passTime')
    已播放进度 = 已播放元素.attr('style')
    if 已播放进度:
        提取结果 = re.search(r'\d+(\.\d+)?', 已播放进度)
        return float(提取结果.group())
    else:
        return 100
def 关闭音量(元素):
    if 元素.ele('.volumeBox',timeout=3):
        元素.ele('.volumeBox').click()
def 处理选项(元素):
    while True:
        选项元素 = drissionpage_utils.找一个元素(元素,'.topic-item')
        if 选项元素:
            选项元素.click()
            关闭 = drissionpage_utils.找一个元素(元素,'text=关闭')
            if 关闭:
                关闭.click()
            print('处理选项完成')
            点播放(元素)
        time.sleep(5)  # 避免过于频繁检查
def 切入视频(元素):
    找bar元素激活(元素)
    切换1点5倍数(元素)
    找bar元素激活(元素)
    点流畅(元素)
    找bar元素激活(元素)
    关闭音量(元素)
    找bar元素激活(元素)
    点播放(元素)

    

def 主流程(元素):
    while True:
        切入视频(元素)
        print('切入视频')
        time.sleep(0.5)
        while True:
            已播放进度 = 获取已播放进度(元素)
            找播放按钮防止不播放=元素.ele('.playButton',timeout=.3)
            if 找播放按钮防止不播放:
                找播放按钮防止不播放.click()
            if 已播放进度 >= 100:
                break
            print('播放进度', 已播放进度)
            time.sleep(20)
        print('播放完成')
        找bar元素激活(元素)
        点下一个(元素)
        time.sleep(5)
        print('下一集')

def 多线程执行(元素):
    try:
        with ThreadPoolExecutor(max_workers=2) as executor:
            # 提交两个任务并获取它们的Future对象
            future1 = executor.submit(主流程, 元素)
            future2 = executor.submit(处理选项, 元素)
            
            # 等待任务完成或出现异常
            done, not_done = wait(
                [future1, future2],
                return_when='FIRST_EXCEPTION'  # 当任何任务完成或出现异常时返回
            )
            
            # 检查是否有异常发生
            for future in done:
                try:
                    future.result()  # 如果有异常会在这里抛出
                except Exception as e:
                    print(f"任务执行出错: {e}")
                    raise  # 重新抛出异常
                    
    except Exception as e:
        print(f"程序执行出错: {e}")
    finally:
        print("程序结束")
    
def 获取课程学习页面(page):
    获取到课程学习的页面=page.get_tabs()
    for 单个页面 in 获取到课程学习的页面:
        if '课程学习' in 单个页面.title:
            课程学习页面=单个页面
            return 课程学习页面
    exit()

if __name__ == "__main__":
    page = 初始化dp()

    课程学习页面=获取课程学习页面(page)

    多线程执行(课程学习页面)

    # print(page.title)
    # bar=page.ele('.controlsBar')
    # print(bar)

    # while True:
    #     找bar元素激活(page)
    #     time.sleep(1)
