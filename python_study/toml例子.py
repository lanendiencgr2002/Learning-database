import tomli
import pprint

# 读取toml文件
def 读取toml文件(文件路径: str) -> dict:
    with open(文件路径, 'rb') as f:
        return tomli.load(f)

def 写入toml文件(文件路径: str, 数据: dict):
    with open(文件路径, 'wb') as f:
        tomli.dump(数据, f)

# 测试
if __name__ == '__main__':
    文件路径 = 'config.toml'
    数据 = 读取toml文件(文件路径)
    print(数据['name'])
    # pprint.pprint(数据)
    # 写入toml文件(文件路径, 数据)
