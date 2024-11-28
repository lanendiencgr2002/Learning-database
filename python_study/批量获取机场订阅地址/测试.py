from DrissionPage import ChromiumPage, ChromiumOptions

from 批量获取机场订阅地址.批量获取 import 检查链接是否存在或不可用

co = ChromiumOptions().set_local_port(9222)
page = ChromiumPage(addr_or_opts=co)
page.timeout = 1

订阅地址的父divs = page.eles('.text-truncate')
链接个数=len(订阅地址的父divs)//2

for 索引, 订阅地址的父div in enumerate(订阅地址的父divs):
    if 索引 % 2 == 1:  # 只处理奇数索引
        订阅地址 = 订阅地址的父div.ele('tag:a')
        try:
            链接 = 订阅地址.property('href')
            print(链接)
            
            if not 检查链接是否存在或不可用(链接):
                with open('机场订阅地址.txt', 'a', encoding='utf-8') as f:
                    f.write(链接 + '\n')
            else:
                print(f"链接 {链接} 已存在或被标记为不可用，跳过")
            
        except:
            print(f"索引 {索引}: 无法获取链接")

for 索引 in range(1, 链接个数+1):
    过期时间 = page.ele(f'xpath://html/body/table/tbody/tr[{索引*2}]/td[4]')
    print(过期时间.text)
