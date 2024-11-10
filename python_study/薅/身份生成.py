import requests
import json
import random

国家列表={
        0: "random",
        1: "chinese-china",
        2: "english-united-states",
        3: "japanese-japan",
        4: "korean-korea",
        5: "french-france",
        6: "german-germany",
         7: "spanish-spain",
        8: "italian-italy",
        9: "russian-russia"
    }
性别列表={
        0: "random",
        1: "male",
        2: "female"
    }
def 获取国家(选择=0):
    return 国家列表[选择]
def 获取性别(选择=0):
    return 性别列表[选择]
def 随机获取国家():
    return random.choice(list(国家列表.values()))

def 随机获取性别():
    return random.choice(list(性别列表.values()))

def 生成虚拟身份():
    国家 = 随机获取国家()
    性别 = 随机获取性别()

    接口地址 = f"https://api.namefake.com/{国家}/{性别}/"

    try:
        响应 = requests.get(接口地址)
        响应.raise_for_status()
        数据 = json.loads(响应.text)

        翻译字典 = {
            "name": "姓名",
            "address": "地址",
            "latitude": "纬度",
            "longitude": "经度",
            "maiden_name": "婚前姓氏",
            "birth_data": "出生日期",
            "phone_h": "家庭电话",
            "phone_w": "工作电话",
            "email_u": "电子邮箱用户名",
            "email_d": "电子邮箱域名",
            "username": "用户名",
            "password": "密码",
            "domain": "域名",
            "useragent": "用户代理",
            "ipv4": "IPv4地址",
            "macaddress": "MAC地址",
            "plasticcard": "信用卡号",
            "cardexpir": "卡片过期日期",
            "bonus": "奖金",
            "company": "公司",
            "color": "颜色",
            "uuid": "UUID",
            "height": "身高",
            "weight": "体重",
            "blood": "血型",
            "eye": "眼睛颜色",
            "hair": "头发",
            "pict": "头像",
            "url": "URL",
            "sport": "运动",
            "ipv4_url": "IPv4 URL",
            "email_url": "电子邮箱 URL",
            "domain_url": "域名 URL"
        }

        with open("身份生成信息.txt", "w", encoding="utf-8") as 文件:
            文件.write("生成的虚拟身份信息:\n")
            for 键, 值 in 数据.items():
                中文键 = 翻译字典.get(键, 键)
                文件.write(f"{中文键}: {值}\n")

        print("身份信息已保存到 '身份生成信息.txt' 文件中。")

    except requests.exceptions.RequestException as 错误:
        print(f"请求失败: {错误}")
    except json.JSONDecodeError:
        print("JSON解析失败")
    except IOError as 文件错误:
        print(f"文件写入错误: {文件错误}")

if __name__ == "__main__":
    生成虚拟身份()
