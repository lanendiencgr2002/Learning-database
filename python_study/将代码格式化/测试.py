schools = {
    'School A': (20, 5),
    'School B': (15, 10),
    'School C': (25, 3),
    'School D': (10, 8),
    'School E': (3, 8),
    'School F': (13, 8),
}
sorted_schools = sorted(schools.items(), key=lambda x: (x[1][1], -x[1][0]), reverse=True)

# 打印排序后的学校信息
for school, num_players in sorted_schools:
    print(school, num_players)