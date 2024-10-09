

# 一、MySQL数据库的安装



## 步骤一：双击mysql8的安装向导

![1703050965495](MySQL8.0_安装和使用文档.assets\1703050965495.png)

## 步骤二：安装和密码配置

![1703051078057](MySQL8.0_安装和使用文档.assets\1703051078057.png)

配置信息：

![1703051193588](MySQL8.0_安装和使用文档.assets\1703051193588.png)

![1703051261547](MySQL8.0_安装和使用文档.assets\1703051261547.png)



# 二、MySQL数据库服务的启动和停止

MySQL软件的服务器端必须先启动，客户端才可以连接和使用使用数据库。

如果接下来天天用，可以设置自动启动。

* 打开Mac系统设置

* 找到mysql服务

  ![1703051722457](MySQL8.0_安装和使用文档.assets\1703051722457.png)

# 三、MySQL数据库环境变量的配置

打开终端，输入mysql，下面情况证明MYSQL环境变量问题！

![1703051465101](MySQL8.0_安装和使用文档.assets\1703051465101.png)

打开终端配置MySQL环境：[mac环境变量文件在登录用户文件夹下/.bash_profile]

vim ~/.bash_profile

按字母 i 键切换至输入状态，添加如下配置：

PATH="/usr/local/mysql/bin:/Library/Frameworks/Python.framework/Versions/3.7/bin:${PATH}"  # mysql默认安装到/usr/local/mysql文件夹，将其bin，配置到path变量

按ESC键退出输入状态，输入:wq回车即可

然后执行 source .bash_profile 使其配置生效。

输入mysql --version，出现MySQL版本信息，则表示配置成功。