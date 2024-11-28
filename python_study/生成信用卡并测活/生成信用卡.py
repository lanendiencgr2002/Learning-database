import random
import time

from DrissionPage import ChromiumPage

def 生成信用卡(tab):
    # 切换Advance选项
    tab.ele('xpath:/html/body/main/section[1]/div/div[3]/div[1]/div[2]/nav/a[2]').click()
    # 神秘数字 5425503300xxxxxx  515462001608
    numbers = ["411197", "454642", "481501","5425503300xxxxxx","515462001608"]
    chosen_number = random.choice(numbers)
    bin = tab.ele(
        'xpath:/html/body/main/section[1]/div/div[3]/div[3]/div/div/div[1]/div/form/div[1]/div[1]/div[1]/input')
    bin.clear()
    bin.input(chosen_number)
    # 点击生成
    tab.ele('xpath:/html/body/main/section[1]/div/div[3]/div[3]/div/div/div[1]/div/form/div[5]/button').click()
    time.sleep(.3)
    tab.ele('xpath:/html/body/main/section[1]/div/div[3]/div[3]/div/div/div[1]/div/form/div[5]/button').click()
    # 获取卡号们
    卡号们 = tab.ele('xpath:/html/body/main/section[1]/div/div[3]/div[3]/div/div/div[2]/div/div[1]/div[2]/pre')
    return 卡号们.text

if __name__=="__main__":
    page = ChromiumPage()
    # 访问url
    page.get('https://namso-gen.com/')
    # 切换Advance选项
    page.ele('xpath:/html/body/main/section[1]/div/div[3]/div[1]/div[2]/nav/a[2]').click()
    # 神秘数字
    numbers = ["411197", "454642", "481501"]
    chosen_number = random.choice(numbers)
    bin=page.ele('xpath:/html/body/main/section[1]/div/div[3]/div[3]/div/div/div[1]/div/form/div[1]/div[1]/div[1]/input')
    bin.clear()
    bin.input(chosen_number)
    # 点击生成
    page.ele('xpath:/html/body/main/section[1]/div/div[3]/div[3]/div/div/div[1]/div/form/div[5]/button').click()
    time.sleep(.3)
    page.ele('xpath:/html/body/main/section[1]/div/div[3]/div[3]/div/div/div[1]/div/form/div[5]/button').click()
    # 获取卡号们
    卡号们=page.ele('xpath:/html/body/main/section[1]/div/div[3]/div[3]/div/div/div[2]/div/div[1]/div[2]/pre')
    卡号们=卡号们.text.split('\n')
    print(卡号们)


