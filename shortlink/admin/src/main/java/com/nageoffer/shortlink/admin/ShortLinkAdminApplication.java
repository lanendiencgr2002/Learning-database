/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nageoffer.shortlink.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/** @JSONField(name="id")
 * 来自 Fastjson2 库
 * 标记在类的属性上，当对象转换为 JSON 时，userId 字段将被表示为 "id"。 会生成json然后{id:xxx}
 * 当从 JSON 解析对象时，JSON 中的 "id" 键将被映射到 userId 字段。   json{id:xxx} 会解析为userId:xxx
 * 
 */

/** JSON.parseObject()
 * 来自 Fastjson2 库
 * 将 JSON 字符串转换为指定类型的对象
 * 例子：
 * UserInfoDTO userInfoDTO = JSON.parseObject(userInfoJsonStr.toString(), UserInfoDTO.class);
 * 将 JSON 字符串 userInfoJsonStr 转换为 UserInfoDTO 对象
 */

/** ThreadLocal线程局部变量
 * 实现用户上下文管理
 * 在多线程环境中，每个线程都需要有自己的独立数据副本，以避免线程间的数据干扰
 * 
 * ThreadLocal 的基本原理：
 * 它内部维护了一个 ThreadLocalMap，用线程作为 key，存储的值作为 value。
 * 每次访问时，它会根据当前线程找到对应的值
 * 
 * TransmittableThreadLocal 的特点： 也是线程安全的Threadlocal
 * 这是阿里巴巴开发的 ThreadLocal 增强版。
 * 它解决了在使用线程池时，父线程的上下文无法传递到子线程的问题。
 * 
 * 在以下文件中演示：
 * admin\src\main\java\com\nageoffer\shortlink\admin\common\biz\user\UserContext.java
 * admin\src\main\java\com\nageoffer\shortlink\admin\filter\UserTransmitFilter.java
 * 设置过滤器为组件
 * admin\src\main\java\com\nageoffer\shortlink\admin\config\UserConfiguration.java
 */

/** 继承BaseDO
 * BaseDO 是数据库持久层对象基础属性，比如创建时间、更新时间、等等
 * 然后就不用多搞属性代码了
 * 
 * 在以下文件中演示：
 * admin\src\main\java\com\nageoffer\shortlink\admin\dao\entity\GroupDO.java
 */

/** @Builder 短链接分组新增
 * 这个注解可以链式创建对象
 * 例子：
 * GroupDO groupDO = GroupDO.builder()
 *                             .gid(gid)
 *                             .sortOrder(0)
 *                             .username(username)
 *                             .name(groupName)
 *                             .build();
 * 
 * 在以下文件中演示：
 * admin\src\main\java\com\nageoffer\shortlink\admin\service\impl\GroupServiceImpl.java\save
 * admin\src\main\java\com\nageoffer\shortlink\admin\dao\entity\GroupDO.java
 */

/** 用户登录
 * 
 * 在以下文件中演示：
 * admin\src\main\java\com\nageoffer\shortlink\admin\service\impl\UserServiceImpl.java\login
 */

/** 分片键 
 * 
 * 用于将数据库(表)水平拆分的数据库字段。
 * 分库分表中的分片键(Sharding Key)是一个关键决策,它直接影响了分库分表的性能和可扩展性。以下是一些选择分片键的关键因素:
 * 1. 访问频率: 选择分片键应考虑数据的访问频率。将"等"访问的数据放在同一个分片上,可以提高查询性能和降低跨分片查询的开销。
 * 2. 数据均匀性: 分片键应该使数据的均匀分布在各个分片上,避免出现热点数据集中在某个分片上的情况。
 * 3. 数据不可变: 一旦选择了分片键,它应该是不可变的,不能随着业务的变化而频繁修改。
 * 
 * 用户名和用户ID选哪个作为分片键？ 如果没有分片键会查所有表
 * 用户名。用户名可以登录。 
 * 
 * 在以下文件中演示：
 * admin\src\main\resources\shardingsphere-config-dev.yaml
 * admin\src\main\resources\application.yaml
 */

/** 用户分库分表
 * 为什么要分库分表
 * 1. 数据量庞大。
 * 2. 查询性能缓慢，之前可能是20ms，后续随着数据量的增长，查询时间呈指数增长。
 * 3. 数据库连接不够。
 * 
 * 什么情况下分表
 * 数据量过大或者数据表对应的磁盘文件过大，一般几千万都不用分
 * 
 * 什么情况下分库
 * 连接不够用。MySQLServer假设支持4000个数据库连接。一个服务连接池最大10个，假设有40个节点。已经占用了400个数据库连接。
 * 类似于这种服务，有10个，那你这个MvSQLServer连接就不够了
 * 
 * 什么情况下又分库又分表
 * 高并发写入或查询场景。 
 * 数据库量巨大场景
 * 
 * mycat：最近不咋维护了，而且是用的代理，是不支持jdbc层面的
 */

/** 如果恶意请求全部使用未注册用户名发起注册
 * 无法防住，需要做限流
 */

/** 如何解决海量用户注册同一用户名问题
 * 根据用户名获取分布式锁，如果获取不到，则表示该用户名已经被注册。
 * 用redisson实现分布式锁
 * 
 * 在以下文件中演示：
 * admin\src\main\java\com\nageoffer\shortlink\admin\common\constant\RedisCacheConstant.java
 * admin\src\main\java\com\nageoffer\shortlink\admin\service\impl\UserServiceImpl.java\register
 */

/** 用户创建时间等自动填充 
 * 
 * 在以下文件中演示：
 * admin\src\main\java\com\nageoffer\shortlink\admin\config\MyMetaObjectHandler.java
 * admin\src\main\java\com\nageoffer\shortlink\admin\common\database\BaseDO.java
 */

/** 如何防止用户名重复
 * 通过布隆过滤器把所有用户名进行加载。这样该功能就能完全隔离数据库。
 * 数据库层面添加唯一索引。
 */

/** 布隆过滤器 解决缓存穿透问题
 * 
 * 在以下文件中演示：
 * admin\src\main\java\com\nageoffer\shortlink\admin\config\RBloomFilterConfiguration.java
 * admin\src\main\java\com\nageoffer\shortlink\admin\service\impl\UserServiceImpl.java
 */ 

/** 用户敏感信息接口返回脱敏
 * 弄一个PhoneDesensitizationSerializer.class 类
 * 加上这个注解@JsonSerialize(using = PhoneDesensitizationSerializer.class) private String phone;
 * 
 * 在以下文件中演示：
 * {@link com.nageoffer.shortlink.admin.dto.resp.UserRespDTO}
 * {@link com.nageoffer.shortlink.admin.common.serialize.PhoneDesensitizationSerializer}
 */

/** 构造器注入@RequiredArgsConstructor
 * @RequiredArgsConstructor在类上，是Lombok提供的一个注解 用于依赖注入
 * 然后在类中 private final xxx;来注入
 */

/**
 * 短链接后管应用
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients("com.nageoffer.shortlink.admin.remote")
@MapperScan("com.nageoffer.shortlink.admin.dao.mapper")
public class ShortLinkAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShortLinkAdminApplication.class, args);
    }
}
