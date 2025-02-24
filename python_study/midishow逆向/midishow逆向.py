# 1. curl --location --request POST 'https://www.midishow.com/midi/download?id=159745' \
# --header 'Cookie: _csrf=53de4cd708b4ebdc2016110115e0e51114ee6b23428853c6ca504b25d4a7a6d4a%3A2%3A%7Bi%3A0%3Bs%3A5%3A%22_csrf%22%3Bi%3A1%3Bs%3A32%3A%22qaEOkUpkSCCtVpuxjuInKZgchLPfKUmh%22%3B%7D' \
# --data-urlencode '_csrf=0C0KH_nyaUVemdUmw5zAUR7HPogfmLt8Vv7WUjx_ZNKhTE9QkqcZLg3allKV7LUpdLJ35lTC3B8-soY0dyoJug=='

# 指定一个 id
# 返回有 t

# 2. curl --location --request GET 'https://www.midishow.com/midi/download-file?t=WzE1OTc0NSw2OTcyMjY1XQ%3D%3D' \
# --header 'Cookie: PHPSESSID=adg70dtfiqpvn512sifk0p97h4' \

# 下载的名字跟 t 有关

import requests

# Cookie 配置
CSRF_COOKIE = '_identity=1d82c1d4eee37b8804beaa35dea49022c7236ae10340519ef83036d631fc6abaa%3A2%3A%7Bi%3A0%3Bs%3A9%3A%22_identity%22%3Bi%3A1%3Bs%3A51%3A%22%5B866860%2C%22JSGqKWKF6tjcAgLBBFSipWXof_-P66Xq%22%2C2592000%5D%22%3B%7D; expires=Sun, 16-Mar-2025 17:36:00 GMT; Max-Age=2592000; path=/; HttpOnly'
PHPSESSID_COOKIE = 'PHPSESSID=adg70dtfiqpvn512sifk0p97h4'
CSRF_TOKEN = '0C0KH_nyaUVemdUmw5zAUR7HPogfmLt8Vv7WUjx_ZNKhTE9QkqcZLg3allKV7LUpdLJ35lTC3B8-soY0dyoJug=='

def get_download_token(midi_id):
    """
    第一步: 获取下载token
    """
    url = f'https://www.midishow.com/midi/download?id={midi_id}'
    headers = {
        'Cookie': CSRF_COOKIE
    }
    data = {
        '_csrf': CSRF_TOKEN
    }
    
    response = requests.post(url, headers=headers, data=data)
    if response.status_code != 200:
        print("获取下载token失败")
        return None
    
    # 添加调试信息
    print("Response Status Code:", response.status_code)
    print("Response Headers:", response.headers)
    print("Response Content:", response.text)
    
    try:
        t = response.json().get('t')
        if not t:
            print("未能获取到token")
            return None
        return t
    except requests.exceptions.JSONDecodeError as e:
        print("JSON解析错误:", e)
        print("服务器返回内容:", response.text)
        return None

def download_midi_file(t, midi_id):
    """
    第二步: 下载midi文件
    """
    url = f'https://www.midishow.com/midi/download-file?t={t}'
    headers = {
        'Cookie': PHPSESSID_COOKIE
    }
    
    response = requests.get(url, headers=headers)
    if response.status_code != 200:
        print("下载midi文件失败")
        return False
    
    # 保存文件
    filename = f'midi_{midi_id}.mid'
    with open(filename, 'wb') as f:
        f.write(response.content)
    print(f"文件已保存为: {filename}")
    return True

def download_midi(midi_id):
    """
    完整的下载流程
    """
    # 第一步：获取token
    t = get_download_token(midi_id)
    if not t:
        return False
    
    # 第二步：下载文件
    # return download_midi_file(t, midi_id)

if __name__ == '__main__':
    # 使用示例
    midi_id = 159745
    download_midi(midi_id)


