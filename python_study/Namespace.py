import argparse
import time
'''
跟类差不多，只不过更简洁
'''


def 基础用法示例():
    print("\n=== 1. Namespace基础用法 ===")
    # 创建Namespace对象
    my_vars = argparse.Namespace()
    
    # 添加一些基本属性
    my_vars.name = "小明"
    my_vars.age = 18
    my_vars.hobbies = ["游戏", "钓鱼"]
    
    # 打印属性
    print(f"姓名: {my_vars.name}")
    print(f"年龄: {my_vars.age}")
    print(f"爱好: {my_vars.hobbies}")
    
    # 修改属性
    my_vars.age = 20
    print(f"修改后的年龄: {my_vars.age}")

def 游戏窗口示例():
    print("\n=== 2. 模拟游戏窗口信息 ===")
    # 创建游戏相关的Namespace
    game_vars = argparse.Namespace()
    
    # 设置游戏窗口信息
    game_vars.window_rect = (100, 100, 1920, 1080)  # (x, y, width, height)
    game_vars.window_title = "测试游戏"
    game_vars.is_running = True
    
    # 访问和使用这些信息
    print(f"窗口位置: 左上角({game_vars.window_rect[0]}, {game_vars.window_rect[1]})")
    print(f"窗口大小: {game_vars.window_rect[2]}x{game_vars.window_rect[3]}")
    print(f"游戏标题: {game_vars.window_title}")
    
    # 动态添加新属性
    game_vars.fps = 60
    print(f"游戏帧率: {game_vars.fps}")

def 比较不同方式():
    print("\n=== 3. 不同方式的比较 ===")
    
    # 使用字典
    print("方式1 - 使用字典:")
    dict_vars = {
        'score': 100,
        'level': 1
    }
    print(f"分数: {dict_vars['score']}")
    
    # 使用Namespace
    print("\n方式2 - 使用Namespace:")
    ns_vars = argparse.Namespace(score=100, level=1)
    print(f"分数: {ns_vars.score}")
    
    # 使用类
    print("\n方式3 - 使用类:")
    class GameVars:
        def __init__(self):
            self.score = 100
            self.level = 1
    
    class_vars = GameVars()
    print(f"分数: {class_vars.score}")

def 高级用法():
    print("\n=== 4. 高级用法 ===")
    
    # 创建带有初始值的Namespace
    game_state = argparse.Namespace(
        start_time=time.time(),
        player_name="玩家1",
        scores=[],
        settings={
            "difficulty": "普通",
            "volume": 80
        }
    )
    
    # 检查属性是否存在
    print(f"是否存在player_name属性: {'player_name' in vars(game_state)}")
    
    # 获取所有属性
    print("\n所有属性:")
    for name, value in vars(game_state).items():
        print(f"{name}: {value}")
    
    # 动态删除属性
    if 'scores' in vars(game_state):
        delattr(game_state, 'scores')
        print("\n删除scores属性后的属性列表:")
        print(vars(game_state))

def main():
    print("=== Namespace学习示例 ===")
    
    while True:
        print("\n请选择要运行的示例:")
        print("1. 基础用法示例")
        print("2. 游戏窗口示例")
        print("3. 比较不同方式")
        print("4. 高级用法")
        print("0. 退出")
        
        choice = input("请输入数字(0-4): ")
        
        if choice == '1':
            基础用法示例()
        elif choice == '2':
            游戏窗口示例()
        elif choice == '3':
            比较不同方式()
        elif choice == '4':
            高级用法()
        elif choice == '0':
            print("程序结束")
            break
        else:
            print("无效的选择，请重试")
        
        input("\n按Enter继续...")

if __name__ == "__main__":
    main()