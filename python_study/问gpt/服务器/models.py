from pydantic import BaseModel
from typing import Optional

class ChatRequest(BaseModel):
    """
    聊天请求模型类
    
    Attributes:
        问题 (str): 用户输入的问题文本
        接口 (Optional[str]): 可选的接口参数，默认为None
    """
    问题: str
    接口: Optional[str] = None

class TestRequest(BaseModel):
    """
    测试请求模型类
    
    Attributes:
        问题 (Optional[str]): 可选的问题文本，默认为'你好'
    """
    问题: Optional[str] = '你好'