package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import com.nageoffer.shortlink.admin.common.convention.exception.ServiceException;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.dao.entity.GroupDO;
import com.nageoffer.shortlink.admin.dao.mapper.GroupMapper;
import com.nageoffer.shortlink.admin.remote.ShortLinkActualRemoteService;
import com.nageoffer.shortlink.admin.remote.dto.req.ShortLinkRecycleBinPageReqDTO;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkPageRespDTO;
import com.nageoffer.shortlink.admin.service.RecycleBinService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * URL 回收站服务实现类
 * 
 * 负责处理短链接回收站相关的业务逻辑
 * 主要功能包括分页查询用户回收站中的短链接
 */
@Service(value = "recycleBinServiceImplByAdmin")
@RequiredArgsConstructor
public class RecycleBinServiceImpl implements RecycleBinService {

    // 远程短链接服务，用于调用实际的短链接操作
    private final ShortLinkActualRemoteService shortLinkActualRemoteService;

    // 用户分组数据访问对象，用于查询用户分组信息
    private final GroupMapper groupMapper;

    /**
     * 分页查询回收站中的短链接
     * 
     * @param requestParam 分页查询请求参数
     * @return 分页的短链接响应数据
     * @throws ServiceException 当用户没有分组信息时抛出异常
     */
    @Override
    public Result<Page<ShortLinkPageRespDTO>> pageRecycleBinShortLink(ShortLinkRecycleBinPageReqDTO requestParam) {
        // 构建查询条件：查询当前用户未删除的分组
        LambdaQueryWrapper<GroupDO> queryWrapper = Wrappers.lambdaQuery(GroupDO.class)
                .eq(GroupDO::getUsername, UserContext.getUsername()) // 匹配当前登录用户
                .eq(GroupDO::getDelFlag, 0); // 仅查询未删除的分组

        // 执行查询，获取用户的分组列表
        List<GroupDO> groupDOList = groupMapper.selectList(queryWrapper);

        // 检查用户是否有分组，如果没有分组则抛出异常
        if (CollUtil.isEmpty(groupDOList)) {
            throw new ServiceException("用户无分组信息");
        }

        // 将用户的分组ID列表设置到请求参数中，用于后续的远程查询
        requestParam.setGidList(groupDOList.stream().map(GroupDO::getGid).toList());

        // 调用远程服务，分页查询回收站中的短链接
        return shortLinkActualRemoteService.pageRecycleBinShortLink(
            requestParam.getGidList(), 
            requestParam.getCurrent(), 
            requestParam.getSize()
        );
    }
}
