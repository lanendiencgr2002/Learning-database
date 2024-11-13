

package com.nageoffer.shortlink.admin.common.biz.user;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * 用户信息传输过滤器
 * 
 * 该过滤器的主要职责：
 * 1. 从HTTP请求头中提取用户相关信息
 * 2. 将用户信息临时存储到UserContext中
 * 3. 请求处理完成后清理用户信息
 * 
 * 使用场景：
 * - 用于在微服务架构中传递和管理用户会话信息
 * - 实现用户信息的线程隔离
 */
@RequiredArgsConstructor
public class UserTransmitFilter implements Filter {

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) {
        // ServletRequest 是一个通用的请求接口，提供基本的请求操作
        // HttpServletRequest 是其子接口，专门用于HTTP请求，提供了更多HTTP相关的方法
        // 由于我们需要使用getHeader()等HTTP特有方法，所以需要进行类型转换
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        
        // 使用转换后的httpServletRequest可以访问HTTP特有的方法
        // 例如：getHeader()用于获取HTTP请求头信息
        String username = httpServletRequest.getHeader("username");
        
        // 只有当username存在时才进行用户信息处理
        if (StrUtil.isNotBlank(username)) {
            // 获取其他用户相关信息
            String userId = httpServletRequest.getHeader("userId");
            String realName = httpServletRequest.getHeader("realName");
            
            // 创建用户信息DTO并存储到ThreadLocal中
            UserInfoDTO userInfoDTO = new UserInfoDTO(userId, username, realName);
            UserContext.setUser(userInfoDTO);
        }
        
        try {
            // 继续执行过滤器链中的其他过滤器
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            // 请求处理完成后，清理ThreadLocal中的用户信息，防止内存泄漏
            UserContext.removeUser();
        }
    }
}