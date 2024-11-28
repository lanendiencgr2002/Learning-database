import time

from DrissionPage import ChromiumPage

page = ChromiumPage()
page.timeout=100
while True:
    page.get('https://xuhe.me/buy/1')
    page.ele('@name=email').input('lanendiencgr@gmail.com')
    page.ele('xpath:/html/body/div[3]/div/div/div/form/div[2]/button').click()
    page.ele('text:立即支付').click()
    取文本=page.ele('xpath:/html/body/div[3]/div/div/div/div[2]/div/div[3]/textarea').text
    print(取文本)
    with open('账号密码.txt', 'a', encoding='utf-8') as file:
        file.write(取文本 + '\n')  # 添加换行符以便每次追加在新行
    time.sleep(5)