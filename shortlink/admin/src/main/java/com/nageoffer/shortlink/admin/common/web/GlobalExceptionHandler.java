

package com.nageoffer.shortlink.admin.common.web;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.nageoffer.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import com.nageoffer.shortlink.admin.common.convention.exception.AbstractException;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
import java.util.Optional;

/**
 * 全局异常处理器
 * 
 * 设计目的：
 * 1. 统一处理系统中的各类异常，确保响应格式一致性
 * 2. 避免异常堆栈信息泄露到前端
 * 3. 将异常转换为友好的错误提示
 * 4. 集中化的日志记录，便于问题追踪
 * 
 * 处理的异常类型：
 * 1. 参数验证异常（MethodArgumentNotValidException）
 * 2. 业务异常（AbstractException及其子类）
 * 3. 未捕获的系统异常（Throwable）
 */
@Component("globalExceptionHandlerByAdmin")
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数验证失败异常
     * 
     * 处理逻辑：
     * 1. 获取第一个验证失败的字段错误信息
     * 2. 记录请求方法、URL和错误信息
     * 3. 返回统一格式的错误响应
     * 
     * @param request HTTP请求对象，用于获取请求信息
     * @param ex 参数验证异常对象
     * @return 统一格式的错误响应
     */
    @SneakyThrows
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result validExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException ex) {
        // 获取绑定结果中的第一个字段错误
        BindingResult bindingResult = ex.getBindingResult();
        FieldError firstFieldError = CollectionUtil.getFirst(bindingResult.getFieldErrors());
        // 使用Optional优雅处理空值情况
        String exceptionStr = Optional.ofNullable(firstFieldError)
                .map(FieldError::getDefaultMessage)
                .orElse(StrUtil.EMPTY);
        // 记录错误日志，包含请求方法、URL和错误信息
        log.error("[{}] {} [ex] {}", request.getMethod(), getUrl(request), exceptionStr);
        return Results.failure(BaseErrorCode.CLIENT_ERROR.code(), exceptionStr);
    }

    /**
     * 处理业务异常
     * 
     * 处理逻辑：
     * 1. 区分是否有根异常，决定日志记录方式
     * 2. 记录详细的请求信息和异常堆栈
     * 3. 转换为统一的错误响应
     * 
     * @param request HTTP请求对象
     * @param ex 业务异常对象
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(value = {AbstractException.class})
    public Result abstractException(HttpServletRequest request, AbstractException ex) {
        if (ex.getCause() != null) {
            // 如果有根异常，记录完整堆栈信息
            log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), ex.toString(), ex.getCause());
            return Results.failure(ex);
        }
        // 否则只记录异常信息
        log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), ex.toString());
        return Results.failure(ex);
    }

    /**
     * 处理未预期的系统异常
     * 
     * 处理逻辑：
     * 1. 记录详细的错误日志
     * 2. 特殊处理聚合模式下的异常
     * 3. 返回统一的系统错误响应
     * 
     * @param request HTTP请求对象
     * @param throwable 未知异常对象
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(value = Throwable.class)
    public Result defaultErrorHandler(HttpServletRequest request, Throwable throwable) {
        log.error("[{}] {} ", request.getMethod(), getUrl(request), throwable);
        // 处理聚合模式下的特殊异常
        if (Objects.equals(throwable.getClass().getSuperclass().getSimpleName(), AbstractException.class.getSimpleName())) {
            String errorCode = ReflectUtil.getFieldValue(throwable, "errorCode").toString();
            String errorMessage = ReflectUtil.getFieldValue(throwable, "errorMessage").toString();
            return Results.failure(errorCode, errorMessage);
        }
        return Results.failure();
    }

    /**
     * 获取完整的请求URL（包含查询参数）
     * 
     * @param request HTTP请求对象
     * @return 完整的请求URL字符串
     */
    private String getUrl(HttpServletRequest request) {
        if (StringUtils.isEmpty(request.getQueryString())) {
            return request.getRequestURL().toString();
        }
        return request.getRequestURL().toString() + "?" + request.getQueryString();
    }
}
