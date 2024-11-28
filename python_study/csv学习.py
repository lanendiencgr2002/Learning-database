import pandas as pd
import os

# 检查文件是否存在，如果不存在则创建示例数据
if not os.path.exists('user.csv'):
    # 创建示例数据
    data = {
        'name': ['张三', '李四', '王五', '赵六'],
        'age': [25, 30, 28, 35],
        'city': ['北京', '上海', '北京', '广州']
    }
    # 创建并保存CSV文件
    pd.DataFrame(data).to_csv('user.csv', index=False, encoding='utf-8')

# 1. 读取CSV文件
df = pd.read_csv('user.csv')
print(df)
"""
输出:
   name  age city
0  张三   25  北京
1  李四   30  上海
2  王五   28  北京
3  赵六   35  广州
"""

# 2. 获取某一列数据
ages = df['age']
print(ages)
"""
输出:
0    25
1    30
2    28
3    35
Name: age, dtype: int64
"""

# 3. 条件筛选 - 找出年龄大于30的
# df[df['age'] > 30] 等价于 df[df['age'].apply(lambda x: x > 30)]
older_than_30 = df[df['age'] > 30]
print(older_than_30)
"""
输出:
   name  age city
3  赵六   35  广州
"""

# 4. 统计某列的值出现次数
city_counts = df['city'].value_counts()
print(city_counts)
"""
输出:
北京    2
上海    1
广州    1
Name: city, dtype: int64
"""

# 5. 简单的数据分组统计
# 比如计算每个城市的平均年龄
avg_age_by_city = df.groupby('city')['age'].mean()
print(avg_age_by_city)
"""
输出:
city
北京    26.5
上海    30.0
广州    35.0
Name: age, dtype: float64
"""