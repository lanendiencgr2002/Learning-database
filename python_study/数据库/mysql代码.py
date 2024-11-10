import mysql.connector
from typing import Dict, Optional

# 连接信息
连接信息: Dict[str, str] = {
    "主机": "101.42.175.125",
    "端口": "3306",
    "用户名": "root",
    "密码": "root",
    "自动提交": "True",
    "选择数据库": "mdcsystem"
}

数据库连接: Optional[mysql.connector.MySQLConnection] = None

def 判断是否连接到mysql() -> bool:
    global 数据库连接
    try:
        数据库连接 = mysql.connector.connect(
            host=连接信息["主机"],
            port=int(连接信息["端口"]),
            user=连接信息["用户名"],
            password=连接信息["密码"],
            autocommit=连接信息["自动提交"].lower() == "true"
        )
        return True
    except Exception as 异常:
        print(f"连接错误: {异常}")
        return False

def 判断是否成功选择数据库() -> bool:
    if 判断是否连接到mysql():
        数据库选择 = 连接信息["选择数据库"]
        try:
            assert 数据库连接 is not None
            数据库连接.database = 数据库选择
            print("成功连接到数据库")
            return True
        except Exception as 异常:
            print(f"选择数据库错误: {异常}")
            return False
        finally:
            if 数据库连接:
                数据库连接.close()
    return False

def 列出所有数据库():
    global 数据库连接
    if 判断是否连接到mysql():
        try:
            assert 数据库连接 is not None
            游标 = 数据库连接.cursor()
            游标.execute("SHOW DATABASES")
            数据库列表 = [数据库[0] for 数据库 in 游标.fetchall()]
            print("所有数据库列表：")
            for 数据库 in 数据库列表:
                print(f"- {数据库}")
            return 数据库列表
        except Exception as 异常:
            print(f"列出数据库错误: {异常}")
            return []
        finally:
            if 数据库连接:
                数据库连接.close()
    else:
        print("无法连接到MySQL服务器")
        return []

def 列出数据库中所有表(数据库名字: str):
    global 数据库连接
    if 判断是否连接到mysql():
        try:
            assert 数据库连接 is not None
            数据库连接.database = 数据库名字
            游标 = 数据库连接.cursor()
            游标.execute("SHOW TABLES")
            表列表 = [表[0] for 表 in 游标.fetchall()]
            表数量 = len(表列表)
            for 表 in 表列表:
                print(f"- {表}")
            print(f"数据库 '{数据库名字}' 中共有 {表数量} 个表。")
            return 表列表, 表数量
        except mysql.connector.Error as 异常:
            print(f"列出表错误: {异常}")
            return [], 0
        finally:
            if 数据库连接:
                数据库连接.close()
    else:
        print("无法连接到MySQL服务器")
        return [], 0

def 测试():
    if 判断是否连接到mysql():
        print('连接mysql成功')
        print(列出所有数据库())
        列出数据库中所有表('ruoyi-vue-pro')
    print('测试完成')

if __name__ == '__main__':
    测试()
    
        
    # 判断是否成功选择数据库()
