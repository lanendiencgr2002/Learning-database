import pymysql
from pymysql import Connection
conn=None

def 测试连接数据库():
# 使用读取到的连接信息进行连接
    try:
        global conn
        global code
        conn = Connection(
            host='gz-cynosdbmysql-grp-3eej56dt.sql.tencentcdb.com',
            port=23497,
            user='root',
            password='qiyana324qwe!',
            autocommit=True
        )
        print('连接数据库成功')
        return True
    except Exception as e:
        print('连接数据库失败，错误信息：',e)
        return False
    
def 测试选择数据库(dbselect):
    global conn
    if not 测试连接数据库():return
    dbselect = 'wechat_pyq'
    try:
        conn.select_db(dbselect)
        print('选择数据库成功')
        return True
    except Exception as e:
        print('选择数据库失败，错误信息：',e)
        return False
def 测试插入数据():
    global conn
    if not 测试选择数据库('wechat_pyq'):return
    cursor = conn.cursor()  # 创建游标对象
    data = ("额无语字", '内容额','2024', '图片名字',1,2)
    cursor.execute("INSERT INTO wechat_main (user, content, time, imgname, sendbyi,imgnum) VALUES (%s, %s, %s, %s, %s,%s)", data)
    conn.commit()  # 提交更改
    cursor.close()  # 关闭游标
    print('插入数据成功')
if __name__ == '__main__':
    测试插入数据()
    if conn:
        conn.close()  # 关闭数据库连接
