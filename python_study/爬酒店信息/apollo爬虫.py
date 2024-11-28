import drissionpage_utils
from DrissionPage import ChromiumPage, ChromiumOptions
import os
import time
import csv
from multiprocessing import Pool
import traceback

当前目录=os.path.dirname(os.path.abspath(__file__))

def 初始化dp():
    co = ChromiumOptions().set_local_port(9222)
    co.set_timeouts(base=5)
    page = ChromiumPage(addr_or_opts=co)
    return page
page=初始化dp()

def 读取公司名():
    with open(os.path.join(当前目录,'酒店英文名.txt'), 'r', encoding='utf-8') as file:
        return [line.strip() for line in file]
公司名列表=读取公司名()
print(len(公司名列表))

def 将公司地址写入文件(公司地址):
    with open(os.path.join(当前目录,'公司地址.txt'), 'a', encoding='utf-8') as file:
        file.write(公司地址+'\n')

def 测试获取所有公司地址():
    tab=page.new_tab()
    companies=page.ele('text:Companies')
    if not companies:return False
    for i in 公司名列表:
        查公司名字(tab,i)
        公司地址=获得公司列表栏(tab)
        将公司地址写入文件(f"{i}:{len(公司地址)}")
        for j in 公司地址:
            将公司地址写入文件(j)
            break

def 查公司名字(tab,公司名字):
    tab.get(f'https://app.apollo.io/#/companies?page=1&&qOrganizationName={公司名字}')

def Upgrade_your_plan(tab):
    try:
        元素=tab.ele('text=Upgrade your plan',timeout=.3)
        if 元素:
            return True
        else:
            return False
    except:
        return False
def 处理error(tab):
    try:
        元素=tab.ele('text:Error',timeout=.3)
        if 元素:
            return True
        else:
            return False
    except:
        return False
        

def 获取名字与职称(元素):
    条件=".zp_dqVxo"
    两个=drissionpage_utils.找多个元素(元素,条件)
    return 两个[0].text,两个[1].text

def 获取公司(元素):
    条件=".zp_vLk0C zp_f7eHi"
    return drissionpage_utils.找一个元素的文本(元素,条件)

def 点关闭freeplan(tab):
    条件=".zp-icon mdi mdi-close zp_mMqLX zp_YicAV zp_EASSb zp_OXiV_"
    try:
        tab.ele(条件,timeout=.3).click()
    except:
        pass

def 获取邮件(元素,tab):
    try:
        不可用=元素.ele("text:Save Contact",timeout=.3)
        if 不可用:
            return 'No email'
    except:pass
    try:
        点击条件="text=Access email"
        元素.ele(点击条件,timeout=.3).click()
        条件=".zp_2QeoE"
        邮件=tab.ele(条件).text
        点击条件3=".zp-button zp_GGHzP zp_Ak05C zp_PLp2D"
        元素.ele(点击条件3,timeout=.3).click()
        return 邮件
    except Exception as e1:
        print('没有邮件1',e1)
        try:
            # 框
            点击条件2=".zp-button zp_GGHzP zp_m0QSL zp_Kbe5T zp_LOyEM zp_PLp2D zp_JDVxz"
            元素.ele(点击条件2,timeout=.3).click()
            条件=".zp_2QeoE"
            邮件=tab.ele(条件).text
            点击条件3=".zp-button zp_GGHzP zp_Ak05C zp_PLp2D"
            元素.ele(点击条件3,timeout=.3).click()
            return 邮件
        except Exception as e1:
            try:
                点击条件3=".zp-button zp_GGHzP zp_Ak05C zp_PLp2D"
                元素.ele(点击条件3,timeout=.3).click()
                print('没有邮件2',e1)
                return 'No email'
            except:
                print('没有邮件3',e1)
                return 'No email'

def 获取公司名2(tab):
    条件='.zp_d9irS EditTarget'
    公司名=tab.ele(条件).text
    return 公司名

def 点employees(tab):
    drissionpage_utils.找一个元素(tab,'text=Employees').click()

def 获取员工列表(tab):
    员工列表框= drissionpage_utils.找多个元素(tab,'.zp_LMloD')
    return 员工列表框

def 点击下一页(tab):
    try:
        下一页=drissionpage_utils.找一个元素(tab,'@@class=zp-button zp_GGHzP zp_PLp2D zp_RY4qw@@aria-label=right-arrow')
        if 下一页.click():
            return True
        else:
            return False
    except:
        return False

def 测试():
    drissionpage_utils.找一个元素(page,'text=Employees').click()
    员工列表框= drissionpage_utils.找多个元素(page,'.zp_LMloD')
    # try:
    #     下一页.click()
    # except:
    #     print('没有下一页')
    print(len(员工列表框))
    # for i in 员工列表框:
    i=员工列表框[1]
    print(获取名字与职称(i))
    print(获取公司名2(page))
    print(获取邮件(i,page))
    time.sleep(1)

def 查公司电话(tab):
    # url=f'https://app.apollo.io/#/companies?page=1&&qOrganizationName={公司名字}'
    # tab.get(url)
    try:
        条件1='text=Phone'
        获取公司电话元素=drissionpage_utils.找一个元素(tab,条件1)
        获取公司电话=获取公司电话元素.parent().text.split('\n')[1]
        print(获取公司电话)
        return 获取公司电话
    except:
        return 'No phone number'

def 获取公司名字和电话(tab):
    公司名字=获取公司名2(tab)
    公司电话=查公司电话(tab)
    return 公司名字,公司电话

def 获取人数(tab):
    条件1='.zp_hWv1I'
    元素=tab.eles(条件1,timeout=.3)[1]

    条件='.zp_7UCkf zp_cThXP zp_MLi4w zp_PTp8r'
    人数=元素.eles(条件,timeout=.3)[2].text

    print(人数)

def 获得公司列表栏(tab):
    try:
        条件1='.zp_hWv1I'
        第一个大框=tab.eles(条件1,timeout=.3)[1]

        条件='.zp_7UCkf zp_cThXP zp_MLi4w zp_PTp8r'
        人数=第一个大框.eles(条件,timeout=.3)[2].text
        print('人数',人数)
        urls=第一个大框.eles('tag:a',timeout=.3)
        urlres=[]
        for i in urls:
            print(i.attr('href'))
            if 'apollo' in i.attr('href'):
                urlres.append(i.attr('href'))
                break
        return urlres
    except:
        return []

def 初始化csv(公司名字):
    # 创建csv文件夹（如果不存在）
    csv目录 = os.path.join(当前目录, 'csv文件夹')
    if not os.path.exists(csv目录):
        os.makedirs(csv目录)
    
    # 首先尝试使用序号1的文件名
    csv文件路径 = os.path.join(csv目录, f'result_{公司名字}_1.csv')
    
    # 如果文件不存在，直接使用序号1的文件名
    if not os.path.exists(csv文件路径):
        with open(csv文件路径, 'w', newline='', encoding='utf-8') as file:
            pass
        return csv文件路径
    
    # 如果文件已存在，则查找最大序号并加1
    序号 = 1
    while os.path.exists(csv文件路径):
        序号 += 1
        csv文件路径 = os.path.join(csv目录, f'result_{公司名字}_{序号}.csv')
    
    # 创建新文件
    with open(csv文件路径, 'w', newline='', encoding='utf-8') as file:
        pass
    return csv文件路径

def 写入csv(酒店名称, 电话号码, 姓名, 职称, 电邮, csv文件路径):
    with open(csv文件路径, 'a', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)
        writer.writerow([酒店名称, 电话号码, 姓名, 职称, 电邮])

def 获取员工列表并爬信息(tab, 公司名字, 公司电话, csv文件路径):
    员工列表 = 获取员工列表(tab)
    
    for i in 员工列表:
        姓名, 职称 = 获取名字与职称(i)

        电邮 = 获取邮件(i, tab)
        # 电邮='No email'
        写入csv(公司名字, 公司电话, 姓名, 职称, 电邮, csv文件路径)
        print(f"已写入: {公司名字} - {姓名} - {职称} - {电邮}")

def 关闭(tab):
    try:
        条件='.zp-icon mdi mdi-close zp_mMqLX zp_YicAV zp_EASSb zp_VbsDe'
        元素=tab.ele(条件,timeout=.3)
        if 元素:
            元素.click()
    except:pass

def pagenotsupportted(tab):
    try:
        条件='text=Page Not Supported'
        元素=tab.ele(条件,timeout=2)
        if 元素:
            return True
        else:
            return False
    except:
        return False

def needmorecredits(tab):
    try:
        条件='text:need more credits'
        元素=tab.ele(条件,timeout=2)
        if 元素:
            return True
        else:
            return False
    except:
        return False

def 爬取单个公司(tab,公司地址):
    try:
        tab.get(公司地址)
        公司名字, 公司电话 = 获取公司名字和电话(tab)
        csv文件路径 = 初始化csv(公司名字)  # 获取新的CSV文件路径
        点employees(tab)
        获取员工列表并爬信息(tab, 公司名字, 公司电话, csv文件路径)
        是否可点击下一页=点击下一页(tab)
        if pagenotsupportted(tab):
            return "page not supported"
        if Upgrade_your_plan(tab):
            return "upgrade your plan"
        if needmorecredits(tab):
            return "need more credits"
        if 处理error(tab):
            return "Error,请求频繁"
            
        点关闭freeplan(tab)
        关闭(tab)
        time.sleep(2)
        while 是否可点击下一页:
            获取员工列表并爬信息(tab, 公司名字, 公司电话, csv文件路径)
            是否可点击下一页=点击下一页(tab)
            if pagenotsupportted(tab):
                return "page not supported"
            if Upgrade_your_plan(tab):
                return "upgrade your plan"
            if needmorecredits(tab):
                return "need more credits"
            if 处理error(tab):
                return "Error,请求频繁,不超过 5 分钟恢复"
        print(f'爬取公司：{公司名字}完成，数据已保存到{csv文件路径}')
        return "ok"
    except Exception as e:
        print(f"爬取公司 {公司地址} 时发生错误: {str(e)}")
        return f"error:{str(e)}"

def 单进程爬取(公司地址):
    try:
        # 为每个进程创建新的浏览器实例
        tab = page.new_tab()
        
        # 爬取公司信息
        爬取成功 = 爬取单个公司(tab, 公司地址)
        
        if 爬取成功=="ok":
            # 记录已完成的公司
            已完成公司文件 = os.path.join(当前目录, '已完成公司.txt')
            with open(已完成公司文件, 'a', encoding='utf-8') as f:
                f.write(f"{公司地址}\n")
        else:
            # 如果错误文件不存在，则创建
            if not os.path.exists(os.path.join(当前目录,'错误信息.txt')):
                with open(os.path.join(当前目录,'错误信息.txt'), 'w', encoding='utf-8') as f:
                    pass
            # 把错误信息写入文件
            with open(os.path.join(当前目录,'错误信息.txt'), 'a', encoding='utf-8') as f:
                f.write(f"{公司地址} 错误信息:{爬取成功}\n")
            tab.close()
            return False
        # 关闭浏览器
        tab.close()
        return 爬取成功
        
    except Exception as e:
        print(f"爬取公司 {公司地址} 时发生错误: {str(e)}")
        print(traceback.format_exc())
        tab.close()
        return False

def 多进程爬取():
    公司地址列表 = 读所有公司地址()
    最大进程数 = 1
    已完成公司文件 = os.path.join(当前目录, '已完成公司.txt')
    
    # 创建已完成公司记录文件（如果不存在）
    if not os.path.exists(已完成公司文件):
        with open(已完成公司文件, 'w', encoding='utf-8'):
            pass
            
    # 读取已完成的公司地址
    已完成公司地址 = set()
    with open(已完成公司文件, 'r', encoding='utf-8') as f:
        已完成公司地址 = set(line.strip() for line in f if line.strip())
    
    # 过滤掉已完成的公司地址
    待爬取公司 = [地址 for 地址 in 公司地址列表 if 地址 not in 已完成公司地址]
    
    print(f"总共有{len(公司地址列表)}个公司地址")
    print(f"已完成{len(已完成公司地址)}个公司")
    print(f"本次将爬取{len(待爬取公司)}个公司")
    
    # 使用进程池持续处理
    with Pool(最大进程数) as pool:
        # 异步提交所有任务
        结果 = []
        for 公司地址 in 待爬取公司:
            结果.append(pool.apply_async(单进程爬取, (公司地址,)))
        
        # 等待所有任务完成并统计结果
        成功数 = 0
        失败数 = 0
        for i, future in enumerate(结果, 1):
            try:
                if future.get():  # 获取进程执行结果
                    成功数 += 1
                else:
                    失败数 += 1
                print(f"\r当前进度: {i}/{len(待爬取公司)}, 成功: {成功数}, 失败: {失败数}", end="")
            except Exception as e:
                失败数 += 1
                print(f"\n处理公司时发生错误: {str(e)}")
    
    print(f"\n\n爬取完成！成功: {成功数}家公司，失败: {失败数}家公司")

def 测试主流程():
    tabs=drissionpage_utils.创建多个标签页对象(page,5)

    tab = page.new_tab()
    tab.get('https://app.apollo.io/#/organizations/5a9cbea1a6da98d99774859f?')
    公司名字, 公司电话 = 获取公司名字和电话(tab)
    csv文件路径 = 初始化csv(公司名字)  # 获取新的CSV文件路径
    点employees(tab)
    获取员工列表并爬信息(tab, 公司名字, 公司电话, csv文件路径)
    是否可点击下一页=点击下一页(tab)
    time.sleep(2)
    while 是否可点击下一页:
        获取员工列表并爬信息(tab, 公司名字, 公司电话, csv文件路径)
        是否可点击下一页=点击下一页(tab)
        time.sleep(2)
    print(f'爬取完成，数据已保存到{csv文件路径}')

def 读所有公司地址():
    公司地址列表=[]
    with open(os.path.join(当前目录,'公司地址.txt'), 'r', encoding='utf-8') as file:
        for line in file:
            公司地址列表.append(line.strip())
    过滤前缀="https://app.apollo.io"
    return [i for i in 公司地址列表 if i.startswith(过滤前缀)]

def 修复邮件爬取问题():
    获取员工列表并爬信息(page,None,None,None)

def 测试pagenotsupportted():
    print(pagenotsupportted(page))


if __name__ == '__main__':
    pass
    多进程爬取()
    # 测试获取所有公司地址()