from DrissionPage import ChromiumPage, ChromiumOptions
co = ChromiumOptions().set_local_port(9222)
page = ChromiumPage(addr_or_opts=co)
page.timeout=3
url='''
https://www.bilibili.com/video/BV1gW421P7RD?spm_id_from=333.788.player.switch&vd_source=06302e8849387af19d8893d86d26072d&p=110
'''
url=url.strip()
page.get(url)
从xx开始到结束=[1,130]

if __name__ == '__main__':
    获取视频列表 = page.ele('.video-pod__list multip list')
    获取每个时间 = 获取视频列表.eles('@class:duration')
    总秒数 = 0
    指定范围秒数 = 0
    
    for 索引, 时间元素 in enumerate(获取每个时间, start=1):
        时间 = 时间元素.text
        分钟, 秒数 = map(int, 时间.split(':'))
        当前视频秒数 = 分钟 * 60 + 秒数
        总秒数 += 当前视频秒数
        
        if 从xx开始到结束[0] <= 索引 <= 从xx开始到结束[1]:
            指定范围秒数 += 当前视频秒数

    总分钟 = 总秒数 / 60
    指定范围分钟 = 指定范围秒数 / 60
    
    print(f"总时间: {总分钟:.2f} 分钟 ({总分钟/60:.2f} 小时)")
    print(f"时间数量: {len(获取每个时间)}")
    print(f"第{从xx开始到结束[0]}到第{从xx开始到结束[1]}个视频的总时长: {指定范围分钟:.2f} 分钟 ({指定范围分钟/60:.2f} 小时)")
    print(f"指定范围占总时长的比例: {指定范围秒数/总秒数:.2%}")
