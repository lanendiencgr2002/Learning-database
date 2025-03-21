from enum import Enum
from dataclasses import dataclass, field
from datetime import datetime
from typing import Any

class 枚举类(Enum):
    成功=200
    失败=400
    服务器错误=500

# 如果没有注解像这样子:str 那他就是类变量，如果有的话，它是实例变量
# 实例变量要用实例去访问，也可以用构造方法给他赋初始值 类变量就可以直接用类来调用
@dataclass
class APIResponse:
    status: 枚举类
    message: str
    data: Any
    timestamp: datetime = field(default_factory=datetime.now)

def 测试枚举类dataclass():
    response = APIResponse(status=枚举类.成功, message="Success", data="Hello, World!")
    print(response.status.name)
    print(response.timestamp)

if __name__ == '__main__':
    测试枚举类dataclass()


