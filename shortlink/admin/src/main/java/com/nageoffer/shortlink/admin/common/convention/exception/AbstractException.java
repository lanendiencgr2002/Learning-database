

package com.nageoffer.shortlink.admin.common.convention.exception;

import com.nageoffer.shortlink.admin.common.convention.errorcode.IErrorCode;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * 抽象异常基类，统一处理系统中的三类异常：
 * 1. 客户端异常 (ClientException)
 * 2. 服务端异常 (ServiceException)
 * 3. 远程调用异常 (RemoteException)
 * 
 * 该类继承自RuntimeException，实现了统一的异常处理机制
 */
@Getter  // Lombok注解，自动生成所有字段的getter方法
public abstract class AbstractException extends RuntimeException {

    // 错误码，用于标识具体的异常类型
    public final String errorCode;

    // 错误信息，用于描述异常详情
    public final String errorMessage;

    /**
     * 构造函数
     * @param message 异常消息
     * @param throwable 原始异常
     * @param errorCode 错误码接口
     */
    public AbstractException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable);  // 调用父类RuntimeException的构造函数
        this.errorCode = errorCode.code();  // 设置错误码
        // 使用Optional处理消息，如果message为空则使用errorCode中的默认消息
        this.errorMessage = Optional.ofNullable(StringUtils.hasLength(message) ? message : null)
                                .orElse(errorCode.message());
    }
}