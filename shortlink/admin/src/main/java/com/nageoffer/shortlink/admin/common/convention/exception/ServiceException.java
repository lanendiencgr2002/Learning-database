

package com.nageoffer.shortlink.admin.common.convention.exception;

import com.nageoffer.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.nageoffer.shortlink.admin.common.convention.errorcode.IErrorCode;

import java.util.Optional;

/**
 * 服务层异常类
 * 用于处理服务层中的业务异常，例如：
 * - 业务逻辑错误
 * - 数据处理异常
 * - 系统内部错误
 */
public class ServiceException extends AbstractException {

    /**
     * 使用自定义消息构造异常，使用默认的服务错误码
     * @param message 自定义错误消息
     */
    public ServiceException(String message) {
        this(message, null, BaseErrorCode.SERVICE_ERROR);
    }

    /**
     * 仅使用错误码构造异常
     * @param errorCode 错误码
     */
    public ServiceException(IErrorCode errorCode) {
        this(null, errorCode);
    }

    /**
     * 使用自定义消息和错误码构造异常
     * @param message 自定义错误消息
     * @param errorCode 错误码
     */
    public ServiceException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    /**
     * 完整的构造函数
     * @param message 自定义错误消息
     * @param throwable 原始异常
     * @param errorCode 错误码
     */
    public ServiceException(String message, Throwable throwable, IErrorCode errorCode) {
        super(Optional.ofNullable(message).orElse(errorCode.message()), throwable, errorCode);
    }

    /**
     * 重写toString方法，提供更清晰的异常信息输出
     * @return 格式化的异常信息字符串
     */
    @Override
    public String toString() {
        return "ServiceException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}

