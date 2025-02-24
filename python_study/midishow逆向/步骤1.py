import requests
import os

# 获取当前脚本的绝对路径
current_path = os.path.abspath(__file__)
# 获取脚本所在目录
script_dir = os.path.dirname(current_path)
# 切换到脚本所在目录
os.chdir(script_dir)
print('当前目录切换成功',script_dir)



# download-file?t=


def download_midi(midi_id):
    url = f'https://www.midishow.com/midi/download?id={midi_id}'
    
    headers = {
        'Accept': '*/*',
        'Cookie': 'PHPSESSID=h26cv358eqihou5ra3m73v2bmj',
        'Referer': f'https://www.midishow.com/midi/{midi_id}.html',
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/132.0.0.0 Safari/537.36'
    }
    
    try:
        response = requests.get(url, headers=headers)
        
        # 检查响应状态码
        if response.status_code == 200:
            # 将响应内容写入文件
            filename = f'response_{midi_id}.txt'
            try:
                with open(filename, 'w', encoding='utf-8') as f:
                    f.write(response.text)
                print(f'成功将响应内容写入文件: {filename}')
            except Exception as e:
                print(f'写入文件失败: {str(e)}')
            return True
        else:
            print(f'下载失败,状态码: {response.status_code}')
            return False
            
    except Exception as e:
        print(f'下载出错: {str(e)}')
        return False

# 使用示例
if __name__ == '__main__':
    midi_id = '159745'  # 要下载的MIDI文件ID
    download_midi(midi_id)
