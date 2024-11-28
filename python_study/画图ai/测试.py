import 提示词nextchatapi
import 请求画图api
import 播放mp3
提示= "随机生成一种很浪漫的场景，场景随机，人就是例子那样，例子：一个在樱花树下弹吉他的女孩,不要动漫的，要瘦的，漂亮的，穿传统衣服，清纯，惊艳的韩国女生"
#
# prompt= "一个在樱花树下弹吉他的女孩,不要动漫的，要瘦的，漂亮的，清纯，惊艳的韩国女生"
# (young woman:1.3), long black hair, delicate features, fair skin, innocent expression, enchanting smile, slender figure, white flowy dress, barefoot

# prompt='伪人，眼睛一个大一个小，长得像人但不是人，他们伪装为人'
if __name__ == "__main__":
    # 请求画图api.请求画图api(prompt,自动打开图片=False,自动下载图片=False)
    for i in range(10):
        prompt = 提示词nextchatapi.问gpt问题(提示)
        # print(prompt)
        请求画图api.请求画图api(prompt,自动打开图片=False)
    播放mp3.播放mp3('aa.mp3')