---
theme: true
print: true
github: 'https://github.com/lanendiencgr2002'
---

# 邓依伦

- 联系方式：13631902746
- 邮件：lanendiencgr@gmail.com

>

- [https://github.com/lanendiencgr2002](https://github.com/lanendiencgr2002)

>

- Java后端开发工程师

>

![头像](https://static.todev.cc/resume/avatar.svg)

## 项目经历

### SaaS短链接系统

**项目描述**  
负责开发和维护一个高性能的短链接管理平台，为企业和个人用户提供高效、安全和可靠的链接转换及管理服务。系统支持高并发访问，具备完善的监控分析功能，帮助用户优化链接管理和营销效果。

**技术架构**  
SpringBoot、SpringCloud Alibaba、RocketMQ、ShardingSphere、Redis、MySQL、Sentinel

**主要工作**
- 使用布隆过滤器优化短链接查重逻辑，显著提升判断效率，相比传统分布式锁+数据库查询方案性能提升超过50%
- 基于RocketMQ实现削峰填谷，解决高并发访问下的监控信息存储问题，系统稳定支持10000+ QPS
- 设计并实现基于双重检查锁的缓存更新机制，有效减少缓存失效场景下的数据库访问压力
- 使用Redisson分布式读写锁确保高并发下数据一致性，通过Redis实现消息幂等性处理
- 基于ShardingSphere实现数据分片，并设计路由表支持短链接分页查询功能
- 集成Sentinel实现接口级别限流和降级，保障系统的稳定性和可用性

## 专业技能

- Mysql：熟悉分库分表，sql优化，锁，MVCC等
- Redis：熟悉主从复制，哨兵，集群，布隆过滤器，淘汰策略等
- RocketMQ：熟悉RocketMQ消息中间件，能够解决消息重复消费、顺序消费、消息丢失以及消息积压等常见问题

## 教育经历
### 肇庆学院

|   专业   |       时间        |
| :------: | :---------------: |
| 软件工程 | 2021.09 - 2025.06 |

<p style="
    display: flex;
    justify-content: center;
    padding: 0.5rem 0;
">
  <img src="//github-readme-stats.vercel.app/api?username=Dunqing&show_icons=true&icon_color=CE1D2D&text_color=718096&bg_color=ffffff&hide_title=true" />
</p>
