def 统一错误返回装饰器(func):
    def wrapper(*args, **kwargs):
        try:
            return func(*args, **kwargs)
        except Exception as e:
            print(f'服务器发生错误：{e}')
            return None
    return wrapper
def 判断返回是否符合要求(value_type="string"):
    """返回值验证装饰器，不符合要求则返回None
    
    Args:
        value_type: 期望的返回值类型
            - "int": 整数
            - "float": 浮点数
            - "bool": 布尔值("true"/"false")
            - "string": 字符串(默认)
            
    Returns:
        function: 装饰器函数
    """
    def decorator(func):
        def wrapper(*args, **kwargs):
            result = func(*args, **kwargs)
            # 处理 None 返回值
            if result is None:
                return None
                
            try:
                # 根据不同类型进行验证
                if value_type == "int":
                    int(result)  # 尝试转换为整数
                elif value_type == "float":
                    float(result)  # 尝试转换为浮点数
                elif value_type == "bool":
                    if result.lower() not in ["true", "false"]:
                        return None
                # string类型不需要特殊验证
                
                return result
            except (ValueError, AttributeError):
                return None
                
        return wrapper
    return decorator
