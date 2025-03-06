from pathlib import Path
import logging
from logging.handlers import RotatingFileHandler

def setup_logger(maxBytes=5,backupCount=5):
    """
    配置并初始化日志系统。

    Args:
        maxBytes (int): 单个日志文件的最大大小(MB)，默认为5MB。
        backupCount (int): 保留的备份日志文件数量，默认为5个。

    Returns:
        logging.Logger: 配置好的日志记录器实例。

    Raises:
        Exception: 当日志系统初始化失败时抛出异常。

    Example:
        >>> logger = setup_logger(maxBytes=10, backupCount=3)
        >>> logger.info("这是一条日志消息")

    Notes:
        - 日志文件存储在当前文件所在目录的logs子目录下
        - 使用UTF-8编码确保正确处理中文
        - 日志文件使用追加模式，保留历史记录
        - 当日志文件大小超过maxBytes时会自动分割
        - 同时输出日志到文件和控制台
    """
    # 在当前文件所在目录下创建 logs 子目录
    log_dir = Path(__file__).parent / "logs"
    log_dir.mkdir(exist_ok=True)  # 如果目录已存在则不报错
    log_file = log_dir / "ai_chat.log"

    try:
        # 配置根日志记录器
        logging.basicConfig(
            level=logging.INFO,  # 设置日志级别为 INFO
            format='%(asctime)s - %(message)s',  # 日志格式：时间戳 - 消息
            handlers=[
                # 文件处理器：将日志写入文件
                RotatingFileHandler(
                    log_file,
                    maxBytes=maxBytes * 1024 * 1024,  # 5MB 后分割
                    backupCount=backupCount,  # 保留 5 个备份文件 如果超过5个，则删除最旧的文件
                    encoding='utf-8',
                    mode='a'
                ),
                # 流处理器：将日志输出到控制台
                logging.StreamHandler()
            ]
        )
        logging.info("日志系统初始化成功")
    except Exception as e:
        # 异常处理：输出错误信息和尝试写入的文件路径，便于调试
        print(f"日志配置错误: {e}")
        print(f"尝试写入的日志文件路径: {log_file}")

    # 返回与当前模块关联的日志记录器
    return logging.getLogger(__name__) 