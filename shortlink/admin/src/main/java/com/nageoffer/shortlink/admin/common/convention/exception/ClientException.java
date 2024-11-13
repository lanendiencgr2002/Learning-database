

package com.nageoffer.shortlink.admin.common.convention.exception;

import com.nageoffer.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.nageoffer.shortlink.admin.common.convention.errorcode.IErrorCode;

/**
 * 客户端异常类
 * 用于处理所有与客户端相关的异常情况，例如：
 * - 参数验证失败
 * - 请求格式错误
 * - 客户端权限不足
 * 等业务场景
 */
public class ClientException extends AbstractException {

    /**
     * 仅使用错误码构造异常
     * @param errorCode 错误码
     */
    public ClientException(IErrorCode errorCode) {
        this(null, null, errorCode);
    }

    /**
     * 使用自定义消息构造异常
     * @param message 自定义错误消息
     */
    public ClientException(String message) {
        this(message, null, BaseErrorCode.CLIENT_ERROR);
    }

    /**
     * 使用自定义消息和错误码构造异常
     * @param message 自定义错误消息
     * @param errorCode 错误码
     */
    public ClientException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    /**
     * 完整的构造函数
     * @param message 自定义错误消息
     * @param throwable 原始异常
     * @param errorCode 错误码
     */
    public ClientException(String message, Throwable throwable, IErrorCode errorCode) {
        super(message, throwable, errorCode);
    }

    /**
     * 重写toString方法，提供更清晰的异常信息输出
     * @return 格式化的异常信息字符串
     */
    @Override
    public String toString() {
        return "ClientException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}
