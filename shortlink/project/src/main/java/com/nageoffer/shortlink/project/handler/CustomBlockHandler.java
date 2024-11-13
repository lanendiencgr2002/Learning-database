package com.nageoffer.shortlink.project.handler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.dto.req.ShortLinkCreateReqDTO;
import com.nageoffer.shortlink.project.dto.resp.ShortLinkCreateRespDTO;

/**
 * 自定义流量控制处理器
 * 
 * 该类用于处理系统触发流量控制时的降级逻辑
 * 主要功能：
 * 1. 处理短链接创建接口的流量控制
 * 2. 当触发流控时返回友好的提示信息
 */
public class CustomBlockHandler {

    /**
     * 短链接创建接口的流控处理方法
     * 
     * @param requestParam 短链接创建请求参数
     * @param exception Sentinel触发的流控异常
     * @return 包含错误信息的统一响应对象
     * 
     * 说明：
     * - 当短链接创建接口触发流控时，会调用此方法
     * - 返回特定错误码(B100000)和友好提示信息
     * - 该方法必须是static的，因为是通过Sentinel框架反射调用
     */
    public static Result<ShortLinkCreateRespDTO> createShortLinkBlockHandlerMethod(ShortLinkCreateReqDTO requestParam, BlockException exception) {
        return new Result<ShortLinkCreateRespDTO>()
                .setCode("B100000")    // 设置业务错误码
                .setMessage("当前访问网站人数过多，请稍后再试..."); // 设置用户友好的提示信息
    }
}
