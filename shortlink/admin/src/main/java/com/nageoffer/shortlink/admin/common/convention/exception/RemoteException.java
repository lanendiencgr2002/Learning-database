

package com.nageoffer.shortlink.admin.common.convention.exception;

import com.nageoffer.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.nageoffer.shortlink.admin.common.convention.errorcode.IErrorCode;

/**
 * 远程服务调用异常类
 * 用于处理微服务架构中的远程调用异常，例如：
 * - 服务调用超时
 * - 服务不可用
 * - 网络连接异常
 * - 远程服务返回错误
 */
public class RemoteException extends AbstractException {

    /**
     * 使用自定义消息构造异常，使用默认的远程调用错误码
     * @param message 自定义错误消息
     */
    public RemoteException(String message) {
        this(message, null, BaseErrorCode.REMOTE_ERROR);
    }

    /**
     * 使用自定义消息和错误码构造异常
     * @param message 自定义错误消息
     * @param errorCode 错误码
     */
    public RemoteException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    /**
     * 完整的构造函数
     * @param message 自定义错误消息
     * @param throwable 原始异常
     * @param errorCode 错误码
     */
    public RemoteException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    /**
     * 重写toString方法，提供更清晰的异常信息输出
     * @return 格式化的异常信息字符串
     */
    @Override
    public String toString() {
        return "RemoteException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}