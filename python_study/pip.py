# import pip
import subprocess
import sys
import argparse

# use python type hints to make code more readable
from typing import List, Optional

'''
前提需要git弄环境变量
# 从 GitHub 安装特定仓库
pip install git+https://github.com/username/repo.git
# 从 GitHub 仓库的特定子目录安装（本例）
pip install git+https://github.com/philferriere/cocoapi.git#subdirectory=PythonAPI
'''

def 安装包(代理地址: Optional[str], 包参数: List[str]) -> None:
    """
    使用pip安装Python包
    参数:
        代理地址: 可选的HTTP代理地址
        包参数: 要安装的包及其参数列表
    """
    # sys.executable 是Python解释器的路径
    # -m 选项用于运行指定模块的main函数
    # subprocess.run 用于运行命令，并检查命令是否成功执行
    # [sys.executable, "-m", "pip", "install", *包参数] 是pip安装命令的完整路径
    if 代理地址 is None:
        subprocess.run(
            [sys.executable, "-m", "pip", "install", *包参数],
            check=True,
        )
    else:
        subprocess.run(
            [sys.executable, "-m", "pip", "install", f"--proxy={代理地址}", *包参数],
            check=True,
        )


def main():
    # 创建命令行参数解析器
    解析器 = argparse.ArgumentParser(description="安装Python依赖包")
    解析器.add_argument("--cuda", default=None, type=str, help="CUDA版本号")
    解析器.add_argument(
        "--proxy",
        default=None,
        type=str,
        help="HTTP代理地址，例如：[http://127.0.0.1:1080]",
    )
    参数 = 解析器.parse_args()

    # 定义需要安装的包列表
    需安装包清单 = f"""
    cython
    scikit-image
    loguru
    matplotlib
    tabulate
    tqdm
    pywin32
    PyAutoGUI
    PyYAML>=5.3.1
    opencv_python
    keyboard
    Pillow
    pymouse
    numpy>=1.21.1
    torch==1.8.2+{"cpu" if 参数.cuda is None else "cu" + 参数.cuda} -f https://download.pytorch.org/whl/lts/1.8/torch_lts.html
    torchvision==0.9.2+{"cpu" if 参数.cuda is None else "cu" + 参数.cuda} --no-deps -f https://download.pytorch.org/whl/lts/1.8/torch_lts.html
    thop --no-deps
    git+https://github.com/philferriere/cocoapi.git#subdirectory=PythonAPI
    """

    # 逐行处理包安装
    for 当前行 in 需安装包清单.split("\n"):
        处理后行 = 当前行.strip()  # 去除行首行尾的空白字符

        if len(处理后行) > 0:  # 跳过空行
            安装包(参数.proxy, 处理后行.split())

    print("\n所有依赖包安装完成！")


if __name__ == "__main__":
    main()
