# 窗口样式配置
WINDOW_STYLE = """
    QLabel {
        background-color: rgba(40, 40, 40, 200);  
        color: #FFFFFF;
        padding: 15px;
        border-radius: 10px;
        font-size: 16px;
        border: 1px solid #666666;
        font-family: "Microsoft YaHei";
    }
"""

# 程序配置
CLIPBOARD_CHECK_INTERVAL = 0.25  # 剪贴板检查间隔（秒）
ENGLISH_THRESHOLD = 0.5  # 英文字符占比阈值
MAX_CACHE_SIZE = 100  # 翻译缓存最大条数

# 图标和资源
ICON_PATH = "icon.png"  # 托盘图标路径

# 错误消息
ERROR_MESSAGES = {
    "clipboard_error": "获取剪贴板内容失败: {}",
    "translation_error": "翻译失败: {}",
    "window_close_error": "关闭窗口时出错: {}"
} 