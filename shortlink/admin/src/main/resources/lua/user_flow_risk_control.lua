--[[
 * 用户流量风险控制 Lua 脚本
 * 
 * 功能描述：
 * 1. 实现基于 Redis 的分布式限流功能
 * 2. 使用固定时间窗口算法进行访问频率控制
 * 3. 保证计数器递增和过期时间设置的原子性
 * 
 * 工作原理：
 * 1. 使用 Redis 的 INCR 命令实现原子计数
 * 2. 在第一次访问时设置过期时间，创建时间窗口
 * 3. 返回当前时间窗口内的访问次数
 * 
 * 参数说明：
 * - KEYS[1]: 用户标识符（用户名或其他唯一标识）
 * - ARGV[1]: 时间窗口大小（单位：秒）
 * 
 * 返回值：
 * - 返回当前时间窗口内的访问次数
 * 
 * 使用场景：
 * - 防止用户短时间内发送过多请求
 * - 作为系统防护的第一道防线
 * - 保护系统免受 DDoS 攻击
--]]

-- 获取并转换输入参数
local username = KEYS[1]
-- 将时间窗口参数转换为数字类型，确保后续操作的正确性
local timeWindow = tonumber(ARGV[1])

-- 构建 Redis 键名
-- 使用统一前缀便于管理和监控
-- 将用户标识作为后缀确保每个用户的计数器相互独立
local KEY_PREFIX = "short-link:user-flow-risk-control:"
local accessKey = KEY_PREFIX .. username

-- 原子递增访问计数器
-- 使用 INCR 命令确保在并发情况下计数的准确性
-- 返回递增后的当前计数值
local currentAccessCount = redis.call("INCR", accessKey)

-- 设置过期时间
-- 仅在首次访问（计数器为1）时设置过期时间
-- 这样可以确保时间窗口的准确性，避免重复设置导致窗口延长
if currentAccessCount == 1 then
    redis.call("EXPIRE", accessKey, timeWindow)
end

-- 返回当前访问次数
-- 调用方可以根据返回值判断是否超过限制
return currentAccessCount

