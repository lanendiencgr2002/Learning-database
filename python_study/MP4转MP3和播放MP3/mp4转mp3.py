import os
import glob
from pydub import AudioSegment
import 当前目录切换当前目录

def 转码父文件夹下的所有视频():
    # 存储所有子文件夹路径
    文件夹列表 = []

    # 获取用户输入的父文件夹路径
    父文件夹路径 = input('请输入要转码的父文件夹路径：')

    # 遍历所有子文件夹
    for 根路径, 目录列表, 文件列表 in os.walk(父文件夹路径):
        文件夹列表.append(根路径)

    # 遍历每个文件夹进行转换
    for 当前文件夹 in 文件夹列表:
        视频目录 = 当前文件夹
        # 支持的视频格式列表
        视频格式 = ('*.mp4', '*.flv')
        转换计数 = 1
        
        # 切换到当前处理的文件夹
        os.chdir(视频目录)
        
        # 遍历所有支持的视频格式
        for 格式 in 视频格式:
            # 处理当前格式的所有视频文件
            for 视频文件 in glob.glob(格式):
                # 生成MP3文件名（保持原文件名，仅改变扩展名）
                音频文件名 = os.path.splitext(os.path.basename(视频文件))[0] + '.mp3'
                # 转换视频为MP3格式
                AudioSegment.from_file(视频文件).export(音频文件名, format='mp3')
                print('已转码', str(转换计数), '个视频！')
                转换计数 += 1
        
        # 删除已转换的MP4文件
        for 待删除文件 in glob.glob(os.path.join(视频目录, '*.mp4')):
            os.remove(待删除文件)

def 转码当前文件夹下的所有视频(是否询问删除=False):
    """只转换当前文件夹下的视频文件（不包含子文件夹）"""
    # 支持的视频格式
    视频格式 = ('*.mp4', '*.flv')
    转换计数 = 1
    
    # 遍历所有支持的视频格式
    for 格式 in 视频格式:
        # 处理当前格式的所有视频文件
        for 视频文件 in glob.glob(格式):
            try:
                # 生成MP3文件名（保持原文件名，仅改变扩展名）
                音频文件名 = os.path.splitext(os.path.basename(视频文件))[0] + '.mp3'
                # 转换视频为MP3格式
                AudioSegment.from_file(视频文件).export(音频文件名, format='mp3')
                print(f'已转码 {视频文件} -> {音频文件名}')
                转换计数 += 1
            except Exception as e:
                print(f'转换 {视频文件} 失败: {str(e)}')
    
    print(f'\n转换完成！共处理 {转换计数-1} 个文件')
    

    if 是否询问删除:    
        # 询问是否删除原视频文件 默认是删除的
        if input('\n是否删除原视频文件？(y/n): ').lower() == 'y':
            for 格式 in 视频格式:
                for 视频文件 in glob.glob(格式):
                    try:
                        os.remove(视频文件)
                        print(f'已删除: {视频文件}')
                    except Exception as e:
                        print(f'删除 {视频文件} 失败: {str(e)}')
    else:
        for 格式 in 视频格式:
            for 视频文件 in glob.glob(格式):
                try:
                    os.remove(视频文件)
                    print(f'已删除: {视频文件}')
                except Exception as e:
                    print(f'删除 {视频文件} 失败: {str(e)}')
if __name__ == "__main__":
    # 转码父文件夹下的所有视频()
    转码当前文件夹下的所有视频()
    

