from itertools import cycle
from typing import List, Dict, Tuple

class LoadBalancer:
    """负载均衡器，用于管理和分配API请求"""
    
    def __init__(self, apis: List[Dict]):
        """初始化负载均衡器
        
        参数:
            apis (List[Dict]): API配置的列表，包含每个API的详细信息。
        """
        self.apis = list(enumerate(apis))
        self.current = cycle(self.apis)
    
    def get_next_api(self) -> Tuple[int, Dict]:
        """获取下一个可用的API配置
        
        返回:
            Tuple[int, Dict]: 包含API索引和其配置的元组，索引用于标识API，配置包含API的详细信息。
        """
        return next(self.current) 