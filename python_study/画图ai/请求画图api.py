import datetime
import os
import subprocess
import time

import requests
from PIL import Image

def 请求画图api(prompt='一个在樱花树下弹吉他的女孩,不要动漫的，瘦的，漂亮的，韩国女生', 打印图片url等信息=True, 自动下载图片=True, 自动打开图片=True):
    token = get_token(打印图片url等信息)
    if token:
        image_url = req_flux(token, prompt, 打印图片url等信息)
        if image_url:
            if 自动下载图片:
                download_image(image_url, 自动打开图片)
            return image_url
    return None

def get_token(打印图片url等信息):
    url = "https://fluxaiweb.com/flux/getToken"
    try:
        response = requests.get(url)
        response.raise_for_status()
        data = response.json()
        if 打印图片url等信息:
            print("Token response:", data)
        return data.get("data", {}).get("token")
    except requests.exceptions.RequestException as e:
        print(f"Error getting token: {e}")
        return None

def req_flux(token, prompt_value, 打印图片url等信息, aspect_ratio="1:1", output_format="webp", num_outputs=1, output_quality=90):
    url = "https://fluxaiweb.com/flux/generateImage"
    payload = {
        "prompt": prompt_value,
        "aspectRatio": aspect_ratio,
        "outputFormat": output_format,
        "numOutputs": num_outputs,
        "outputQuality": output_quality
    }
    headers = {
        'Content-Type': 'application/json',
        'token': token
    }
    try:
        response = requests.post(url, headers=headers, json=payload)
        response.raise_for_status()
        data = response.json()
        if 打印图片url等信息:
            print('API response:', data)
        image_url = data.get("data", {}).get("image")
        if image_url:
            if 打印图片url等信息:
                print("Image URL:", image_url)
            return image_url
        else:
            print("No image found in response")
            return None
    except requests.exceptions.RequestException as e:
        print(f"Error making request: {e}")
        return None


def download_image(image_url, 自动打开图片):
    # 创建一个名为 "images" 的子目录
    image_dir = "images"
    if not os.path.exists(image_dir):
        os.makedirs(image_dir)

    # 使用时间戳创建唯一的文件名
    timestamp = int(time.time())
    date_time = datetime.datetime.fromtimestamp(timestamp)
    formatted_time = date_time.strftime("%Y%m%d_%H%M%S")

    # 从原始URL获取文件扩展名
    file_extension = os.path.splitext(image_url.split("/")[-1])[1]

    # 构建新的文件名和路径
    local_filename = f"{formatted_time}{file_extension}"
    file_path = os.path.join(image_dir, local_filename)

    try:
        response = requests.get(image_url)
        response.raise_for_status()

        # 将图片保存到子目录中
        with open(file_path, 'wb') as f:
            f.write(response.content)
        print(f"Image successfully downloaded: {file_path}")

        # 获取文件的绝对路径
        absolute_path = os.path.abspath(file_path)
        print(f"Full file path: {absolute_path}")

        if 自动打开图片:
            open_image(absolute_path)
    except requests.exceptions.RequestException as e:
        print(f"Error downloading image: {e}")



def open_image(file_path):
    try:
        img = Image.open(file_path)
        img.show()
    except Exception as e:
        print(f"Error opening image with PIL: {e}")
        try:
            if os.name == 'nt':  # Windows
                os.startfile(file_path)
            elif os.name == 'posix':  # macOS and Linux
                subprocess.call(('xdg-open', file_path))
        except Exception as e:
            print(f"Error opening image with system default: {e}")

if __name__ == "__main__":
    请求画图api()
