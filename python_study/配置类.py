import os
from DrissionPage import ChromiumPage, ChromiumOptions
class 配置类:
    @staticmethod
    def 切换到脚本所在目录():
        # 获取当前脚本的绝对路径
        current_path = os.path.abspath(__file__)
        # 获取脚本所在目录
        script_dir = os.path.dirname(current_path)
        # 切换到脚本所在目录
        os.chdir(script_dir)
        print('当前目录切换成功',script_dir)
    
    @staticmethod
    def dp配置():
        co = ChromiumOptions().set_local_port(8077)
        co.set_timeouts(base=5)
        page = ChromiumPage(addr_or_opts=co)
        print(f"浏览器启动端口: {page.address}")
        return page

if __name__ == "__main__":
    配置类.切换到脚本所在目录()
    配置类.dp配置()
