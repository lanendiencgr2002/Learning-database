import tomli
import tomli_w
from datetime import datetime
from pathlib import Path

def read_toml(file_path: str) -> dict:
    """读取TOML文件"""
    try:
        with open(file_path, "rb") as f:
            return tomli.load(f)
    except Exception as e:
        print(f"读取TOML文件出错: {e}")
        return {}
def write_toml(file_path: str, data: dict) -> bool:
    """写入TOML文件"""
    try:
        with open(file_path, "wb") as f:
            tomli_w.dump(data, f)
        return True
    except Exception as e:
        print(f"写入TOML文件出错: {e}")
        return False

def create_default_config() -> dict:
    """创建默认配置"""
    return {
        # 基本设置
        "app": {
            "name": "我的应用",
            "version": "1.0.0",
            "debug": True,
            "launch_date": datetime.now(),
        },
        
        # 数据库设置
        "database": {
            "host": "localhost",
            "port": 5432,
            "name": "myapp_db",
            "user": "admin",
            "password": "secret123"
        },
        
        # 日志设置
        "logging": {
            "level": "INFO",
            "file_path": "logs/app.log",
            "max_size": 1024,
            "backup_count": 3
        },
        
        # 用户设置
        "users": [
            {
                "name": "张三",
                "role": "admin",
                "active": True,
                "login_times": 0
            },
            {
                "name": "李四", 
                "role": "user",
                "active": True,
                "login_times": 0
            }
        ]
    }

def main():
    config_path = "config.toml"
    
    # 如果配置文件不存在,创建默认配置
    if not Path(config_path).exists():
        default_config = create_default_config()
        if write_toml(config_path, default_config):
            print("已创建默认配置文件")
    
    # 读取配置
    config = read_toml(config_path)
    
    # 访问配置示例
    if config:
        # 访问基本设置
        print(f"应用名称: {config['app']['name']}")
        print(f"版本: {config['app']['version']}")
        
        # 访问数据库设置
        db = config['database']
        print(f"数据库连接: postgresql://{db['user']}:xxxxx@{db['host']}:{db['port']}/{db['name']}")
        
        # 访问日志设置
        print(f"日志级别: {config['logging']['level']}")
        
        # 访问用户列表
        print("\n用户列表:")
        for user in config['users']:
            print(f"- {user['name']} ({user['role']})")
        
        # 修改配置示例
        config['users'][0]['login_times'] += 1
        
        # 保存修改
        if write_toml(config_path, config):
            print("\n配置已更新")

if __name__ == "__main__":
    main() 