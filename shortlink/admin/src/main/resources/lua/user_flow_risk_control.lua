-- 设置用户访问频率限制的参数
local username = KEYS[1]        -- 用户名，从 KEYS 数组获取
local timeWindow = tonumber(ARGV[1])  -- 时间窗口（秒），从 ARGV 数组获取并转换为数字

-- 构造 Redis 中存储用户访问次数的键名    ..拼接字符串
local accessKey = "short-link:user-flow-risk-control:" .. username

-- 原子递增访问次数，并获取递增后的值
local currentAccessCount = redis.call("INCR", accessKey)

-- 设置键的过期时间  只有第一次访问时设置过期时间
if currentAccessCount == 1 then
    redis.call("EXPIRE", accessKey, timeWindow)
end

-- 返回当前访问次数
return currentAccessCount
