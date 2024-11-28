import time

from DrissionPage import ChromiumPage


def 测活(tab,输入内容="123"):
    # 输入
    输入=tab.ele('xpath:/html/body/div[1]/div/div[2]/div[1]/div/div[1]/div[1]/div/div/div/label/div/div[1]/div/textarea')
    输入.clear()
    输入.input(输入内容)

    # 点击开始
    点击按钮=tab.ele('xpath:/html/body/div[1]/div/div[2]/div[1]/div/div[1]/div[2]/div/button')
    点击按钮.click()

    # 检查加载
    while (True):
        是否加载 = tab.ele('.q-spinner text-primary', timeout=1)
        # print('正在检查是否加载...')
        time.sleep(1)
        if 是否加载 == None:
            break
    # print('加载完成')

    # 检查好的
    live显示 = tab.ele('xpath:/html/body/div[1]/div/div[2]/div[1]/div/div[2]/div/div/div[1]/div/div/div[2]/p')
    # print(live显示.text)
    return live显示.text

if __name__=="__main__":
    page = ChromiumPage()

    # 访问url
    # page.get('https://checker.top/')

    # 输入
    # 输入=page.ele('xpath:/html/body/div[1]/div/div[2]/div[1]/div/div[1]/div[1]/div/div/div/label/div/div[1]/div/textarea')
    # 输入.clear()
    # 输入.input('12')

    # 检查加载
    while(True):
        是否加载=page.ele('.q-spinner text-primary',timeout=1)
        print('正在检查是否加载...')
        time.sleep(1)
        if 是否加载==None:
            break
    print('加载完成')

    # 检查好的
    # 信用卡卡号 | 过期月份 | 过期年份 | cvs（密码）  用户名是随便的
    #  邮编输入IPAPI 197中获取到的zip编号 https://ip-api.com/docs/api:json#test
    live显示=page.ele('xpath:/html/body/div[1]/div/div[2]/div[1]/div/div[2]/div/div/div[1]/div/div/div[2]/p')
    print(live显示.text)
