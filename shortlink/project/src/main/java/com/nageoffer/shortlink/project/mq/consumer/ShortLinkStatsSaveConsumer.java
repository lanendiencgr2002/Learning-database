package com.nageoffer.shortlink.project.mq.consumer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Week;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nageoffer.shortlink.project.common.convention.exception.ServiceException;
import com.nageoffer.shortlink.project.dao.entity.LinkAccessLogsDO;
import com.nageoffer.shortlink.project.dao.entity.LinkAccessStatsDO;
import com.nageoffer.shortlink.project.dao.entity.LinkBrowserStatsDO;
import com.nageoffer.shortlink.project.dao.entity.LinkDeviceStatsDO;
import com.nageoffer.shortlink.project.dao.entity.LinkLocaleStatsDO;
import com.nageoffer.shortlink.project.dao.entity.LinkNetworkStatsDO;
import com.nageoffer.shortlink.project.dao.entity.LinkOsStatsDO;
import com.nageoffer.shortlink.project.dao.entity.LinkStatsTodayDO;
import com.nageoffer.shortlink.project.dao.entity.ShortLinkGotoDO;
import com.nageoffer.shortlink.project.dao.mapper.LinkAccessLogsMapper;
import com.nageoffer.shortlink.project.dao.mapper.LinkAccessStatsMapper;
import com.nageoffer.shortlink.project.dao.mapper.LinkBrowserStatsMapper;
import com.nageoffer.shortlink.project.dao.mapper.LinkDeviceStatsMapper;
import com.nageoffer.shortlink.project.dao.mapper.LinkLocaleStatsMapper;
import com.nageoffer.shortlink.project.dao.mapper.LinkNetworkStatsMapper;
import com.nageoffer.shortlink.project.dao.mapper.LinkOsStatsMapper;
import com.nageoffer.shortlink.project.dao.mapper.LinkStatsTodayMapper;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkGotoMapper;
import com.nageoffer.shortlink.project.dao.mapper.ShortLinkMapper;
import com.nageoffer.shortlink.project.dto.biz.ShortLinkStatsRecordDTO;
import com.nageoffer.shortlink.project.mq.idempotent.MessageQueueIdempotentHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.nageoffer.shortlink.project.common.constant.RedisKeyConstant.LOCK_GID_UPDATE_KEY;
import static com.nageoffer.shortlink.project.common.constant.ShortLinkConstant.AMAP_REMOTE_URL;



/**
 * 短链接监控状态保存消费者
 * 该类负责消费Redis Stream中的短链接访问统计数据，并将统计数据持久化到数据库中
 * 包括访问量、访客数、IP数、地理位置、操作系统、浏览器等多维度统计信息
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShortLinkStatsSaveConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    // 注入所需的数据访问层组件
    private final ShortLinkMapper shortLinkMapper;
    private final ShortLinkGotoMapper shortLinkGotoMapper;
    private final RedissonClient redissonClient;
    private final LinkAccessStatsMapper linkAccessStatsMapper;
    private final LinkLocaleStatsMapper linkLocaleStatsMapper;
    private final LinkOsStatsMapper linkOsStatsMapper;
    private final LinkBrowserStatsMapper linkBrowserStatsMapper;
    private final LinkAccessLogsMapper linkAccessLogsMapper;
    private final LinkDeviceStatsMapper linkDeviceStatsMapper;
    private final LinkNetworkStatsMapper linkNetworkStatsMapper;
    private final LinkStatsTodayMapper linkStatsTodayMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;

    // 高德地图API密钥，用于IP地址解析
    @Value("${short-link.stats.locale.amap-key}")
    private String statsLocaleAmapKey;

    /**
     * 消费Redis Stream消息的方法
     * @param message Redis Stream中的消息记录
     */
    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        String stream = message.getStream();
        RecordId id = message.getId();
        
        // 消息幂等性检查
        if (messageQueueIdempotentHandler.isMessageBeingConsumed(id.toString())) {
            if (messageQueueIdempotentHandler.isAccomplish(id.toString())) {
                return; // 消息已处理完成，直接返回
            }
            throw new ServiceException("消息未完成流程，需要消息队列重试");
        }

        try {
            // 解析消息内容  消息是一个KEY VALUE 结构
            Map<String, String> producerMap = message.getValue();
            ShortLinkStatsRecordDTO statsRecord = JSON.parseObject(producerMap.get("statsRecord"), ShortLinkStatsRecordDTO.class);
            
            // 执行统计数据保存
            actualSaveShortLinkStats(statsRecord);
            
            // 处理完成后删除Redis Stream中的消息
            stringRedisTemplate.opsForStream().delete(Objects.requireNonNull(stream), id.getValue());
            
        } catch (Throwable ex) {
            // 发生异常时清除消息处理标记并抛出异常
            messageQueueIdempotentHandler.delMessageProcessed(id.toString());
            log.error("记录短链接监控消费异常", ex);
            throw ex;
        }
        
        // 标记消息处理完成
        messageQueueIdempotentHandler.setAccomplish(id.toString());
    }

    /**
     * ⭐ 短链接统计数据保存核心方法
     * 这里是被消息队列调用的方法，所以需要使用读锁，一个用户请求这个短链接，就会把当前请求放到消息。队列中处理
     * == 功能概述 ==
     * 全面记录短链接访问的多维度统计信息，包括：
     * - 访问量统计
     * - 地理位置分析
     * - 操作系统分布
     * - 浏览器类型
     * - 设备类型
     * - 网络环境
     * 
     * == 处理流程 ==
     * 1. 获取短链接分组ID
     * 2. 解析时间维度信息
     * 3. 多维度统计数据记录
     * 4. 地理位置解析
     * 5. 访问日志记录
     * 
     * 💡 并发安全：使用读锁确保数据一致性
     * 
     * @param statsRecord 统计数据记录传输对象
     */
    public void actualSaveShortLinkStats(ShortLinkStatsRecordDTO statsRecord) {
        String fullShortUrl = statsRecord.getFullShortUrl();
        // 获取读写锁，确保并发安全
        // 使用读锁的原因：
        // 这是一个统计信息记录的过程
        // 需要读取短链接的基本信息（如 GID）
        // 使用读锁可以：
        // 允许多个统计线程并发读取
        // 防止在统计过程中发生修改操作
        // 保证读取数据的一致性
        RReadWriteLock readWriteLock = redissonClient.getReadWriteLock(String.format(LOCK_GID_UPDATE_KEY, fullShortUrl));
        RLock rLock = readWriteLock.readLock();
        rLock.lock();
        try {
            // 查询短链接GID
            LambdaQueryWrapper<ShortLinkGotoDO> queryWrapper = Wrappers.lambdaQuery(ShortLinkGotoDO.class)
                    .eq(ShortLinkGotoDO::getFullShortUrl, fullShortUrl);
            ShortLinkGotoDO shortLinkGotoDO = shortLinkGotoMapper.selectOne(queryWrapper);
            String gid = shortLinkGotoDO.getGid();
            
            // 获取当前时间信息
            Date currentDate = statsRecord.getCurrentDate();
            int hour = DateUtil.hour(currentDate, true);
            Week week = DateUtil.dayOfWeekEnum(currentDate);
            int weekValue = week.getIso8601Value();

            // 保存基础访问统计信息
            LinkAccessStatsDO linkAccessStatsDO = LinkAccessStatsDO.builder()
                    .pv(1)
                    .uv(statsRecord.getUvFirstFlag() ? 1 : 0)
                    .uip(statsRecord.getUipFirstFlag() ? 1 : 0)
                    .hour(hour)
                    .weekday(weekValue)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkAccessStatsMapper.shortLinkStats(linkAccessStatsDO);

            // 调用高德地图API解析IP地址获取地理位置信息
            Map<String, Object> localeParamMap = new HashMap<>();
            localeParamMap.put("key", statsLocaleAmapKey);
            localeParamMap.put("ip", statsRecord.getRemoteAddr());
            String localeResultStr = HttpUtil.get(AMAP_REMOTE_URL, localeParamMap);
            JSONObject localeResultObj = JSON.parseObject(localeResultStr);
            String infoCode = localeResultObj.getString("infocode");
            String actualProvince = "未知";
            String actualCity = "未知";
            
            // 解析并保存地理位置统计信息
            if (StrUtil.isNotBlank(infoCode) && StrUtil.equals(infoCode, "10000")) {
                String province = localeResultObj.getString("province");
                boolean unknownFlag = StrUtil.equals(province, "[]");
                LinkLocaleStatsDO linkLocaleStatsDO = LinkLocaleStatsDO.builder()
                        .province(actualProvince = unknownFlag ? actualProvince : province)
                        .city(actualCity = unknownFlag ? actualCity : localeResultObj.getString("city"))
                        .adcode(unknownFlag ? "未知" : localeResultObj.getString("adcode"))
                        .cnt(1)
                        .fullShortUrl(fullShortUrl)
                        .country("中国")
                        .date(currentDate)
                        .build();
                linkLocaleStatsMapper.shortLinkLocaleState(linkLocaleStatsDO);
            }

            // 保存操作系统统计
            LinkOsStatsDO linkOsStatsDO = LinkOsStatsDO.builder()
                    .os(statsRecord.getOs())
                    .cnt(1)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkOsStatsMapper.shortLinkOsState(linkOsStatsDO);

            // 保存浏览器统计
            LinkBrowserStatsDO linkBrowserStatsDO = LinkBrowserStatsDO.builder()
                    .browser(statsRecord.getBrowser())
                    .cnt(1)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkBrowserStatsMapper.shortLinkBrowserState(linkBrowserStatsDO);

            // 保存设备统计
            LinkDeviceStatsDO linkDeviceStatsDO = LinkDeviceStatsDO.builder()
                    .device(statsRecord.getDevice())
                    .cnt(1)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkDeviceStatsMapper.shortLinkDeviceState(linkDeviceStatsDO);

            // 保存网络环境统计
            LinkNetworkStatsDO linkNetworkStatsDO = LinkNetworkStatsDO.builder()
                    .network(statsRecord.getNetwork())
                    .cnt(1)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkNetworkStatsMapper.shortLinkNetworkState(linkNetworkStatsDO);

            // 保存访问日志
            LinkAccessLogsDO linkAccessLogsDO = LinkAccessLogsDO.builder()
                    .user(statsRecord.getUv())
                    .ip(statsRecord.getRemoteAddr())
                    .browser(statsRecord.getBrowser())
                    .os(statsRecord.getOs())
                    .network(statsRecord.getNetwork())
                    .device(statsRecord.getDevice())
                    .locale(StrUtil.join("-", "中国", actualProvince, actualCity))
                    .fullShortUrl(fullShortUrl)
                    .build();
            linkAccessLogsMapper.insert(linkAccessLogsDO);

            // 更新短链接统计数据
            shortLinkMapper.incrementStats(gid, fullShortUrl, 1, statsRecord.getUvFirstFlag() ? 1 : 0, statsRecord.getUipFirstFlag() ? 1 : 0);

            // 更新今日统计数据
            LinkStatsTodayDO linkStatsTodayDO = LinkStatsTodayDO.builder()
                    .todayPv(1)
                    .todayUv(statsRecord.getUvFirstFlag() ? 1 : 0)
                    .todayUip(statsRecord.getUipFirstFlag() ? 1 : 0)
                    .fullShortUrl(fullShortUrl)
                    .date(currentDate)
                    .build();
            linkStatsTodayMapper.shortLinkTodayState(linkStatsTodayDO);
        } finally {
            // 释放锁
            rLock.unlock();
        }
    }
}
