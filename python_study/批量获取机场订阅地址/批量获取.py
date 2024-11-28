from DrissionPage import ChromiumPage, ChromiumOptions

def 检查链接是否存在或不可用(链接):
    try:
        with open('机场订阅地址.txt', 'r', encoding='utf-8') as f:
            内容 = f.read()
            if 链接 in 内容:
                return True
    except FileNotFoundError:
        pass
    return False

co = ChromiumOptions().set_local_port(9222)
page = ChromiumPage(addr_or_opts=co)
page.timeout = 1


for pagenum in range(1, 2):
    url = f'https://fssp.byws.online/page/{pagenum}'
    page.get(url)
    订阅地址的父divs = page.eles('.text-truncate')
    链接个数 = len(订阅地址的父divs) // 2

    for 索引 in range(链接个数):
        订阅地址的父div = 订阅地址的父divs[索引 * 2 + 1]  # 只处理奇数索引
        订阅地址 = 订阅地址的父div.ele('tag:a')
        try:
            链接 = 订阅地址.property('href')
            过期时间 = page.ele(f'xpath://html/body/table/tbody/tr[{(索引+1)*2}]/td[4]').text
            print(f"过期时间: {过期时间}")
            print(f"链接: {链接}")
            
            if not 检查链接是否存在或不可用(链接):
                with open('机场订阅地址.txt', 'a', encoding='utf-8') as f:
                    f.write(f"{过期时间:<10} {链接}\n")
            else:
                print(f"链接 {链接} 已存在或被标记为不可用，跳过")
            
        except Exception as e:
            print(f"索引 {索引}: 无法获取链接或过期时间, 错误: {str(e)}")

