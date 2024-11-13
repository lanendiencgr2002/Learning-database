

package com.nageoffer.shortlink.admin.common.convention.result;

import com.nageoffer.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.nageoffer.shortlink.admin.common.convention.exception.AbstractException;

import java.util.Optional;

/**
 * Results 工具类：统一响应结果构造器
 * 
 * 设计目的：
 * 1. 提供统一的接口响应格式，确保API返回数据的一致性
 * 2. 简化响应对象的创建过程，避免重复代码
 * 3. 支持多种响应场景（成功/失败/异常）的统一处理
 * 
 * 使用说明：
 * - 成功场景：使用 success() 或 success(T data)
 * - 失败场景：使用 failure() 相关重载方法
 * - 异常场景：通过 failure(AbstractException) 处理自定义异常
 */
public final class Results {

    /**
     * 构造无数据的成功响应
     * 适用场景：操作成功但无需返回数据的接口（如：删除、更新操作）
     */
    public static Result<Void> success() {
        return new Result<Void>()
                .setCode(Result.SUCCESS_CODE);
    }

    /**
     * 构造带数据的成功响应
     * 适用场景：需要返回业务数据的查询类接口
     * 
     * @param data 业务数据，支持任意类型
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>()
                .setCode(Result.SUCCESS_CODE)
                .setData(data);
    }

    /**
     * 构造默认的失败响应
     * 适用场景：
     * 1. 系统发生未预期的异常
     * 2. 不需要特定错误信息时的通用错误响应
     */
    public static Result<Void> failure() {
        return new Result<Void>()
                .setCode(BaseErrorCode.SERVICE_ERROR.code())
                .setMessage(BaseErrorCode.SERVICE_ERROR.message());
    }

    /**
     * 构造基于自定义异常的失败响应
     * 处理逻辑：
     * 1. 优先使用异常中的错误码和消息
     * 2. 如果异常中的错误信息为空，则使用默认的服务错误信息
     * 3. 通过 Optional 优雅处理空值情况
     * 
     * @param abstractException 业务异常对象，包含错误码和错误信息
     */
    public static Result<Void> failure(AbstractException abstractException) {
        String errorCode = Optional.ofNullable(abstractException.getErrorCode())
                .orElse(BaseErrorCode.SERVICE_ERROR.code());
        String errorMessage = Optional.ofNullable(abstractException.getErrorMessage())
                .orElse(BaseErrorCode.SERVICE_ERROR.message());
        return new Result<Void>()
                .setCode(errorCode)
                .setMessage(errorMessage);
    }

    /**
     * 构造自定义错误码和消息的失败响应
     * 适用场景：需要返回特定错误信息的场合
     * 
     * @param errorCode 错误码
     * @param errorMessage 错误消息
     */
    public static Result<Void> failure(String errorCode, String errorMessage) {
        return new Result<Void>()
                .setCode(errorCode)
                .setMessage(errorMessage);
    }
}
