package com.nageoffer.shortlink.project.common.web;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.nageoffer.shortlink.project.common.convention.errorcode.BaseErrorCode;
import com.nageoffer.shortlink.project.common.convention.exception.AbstractException;
import com.nageoffer.shortlink.project.common.convention.result.Result;
import com.nageoffer.shortlink.project.common.convention.result.Results;
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

import java.util.Optional;

/**
 * 全局异常处理器
 * 
 * 该类负责统一处理系统中的各类异常，主要功能：
 * 1. 将异常转换为统一的响应格式
 * 2. 根据异常类型提供不同的处理策略
 * 3. 记录异常日志，便于问题排查
 */
@Component
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     * 
     * 处理流程：
     * 1. 获取第一个验证失败的字段错误信息
     * 2. 记录请求信息和错误详情
     * 3. 返回统一格式的错误响应
     * 
     * @param request HTTP请求对象
     * @param ex 参数验证异常
     * @return 统一格式的错误响应
     */
    @SneakyThrows
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result validExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException ex) {
        // 获取验证结果
        BindingResult bindingResult = ex.getBindingResult();
        // 提取第一个字段错误（通常最重要）
        FieldError firstFieldError = CollectionUtil.getFirst(bindingResult.getFieldErrors());
        // 使用Optional安全处理可能的空值
        String exceptionStr = Optional.ofNullable(firstFieldError)
                .map(FieldError::getDefaultMessage)
                .orElse(StrUtil.EMPTY);
        // 记录错误日志，包含请求方法、URL和错误信息
        log.error("[{}] {} [ex] {}", request.getMethod(), getUrl(request), exceptionStr);
        // 返回客户端错误响应
        return Results.failure(BaseErrorCode.CLIENT_ERROR.code(), exceptionStr);
    }

    /**
     * 处理业务异常
     * 
     * 特点：
     * 1. 处理继承自AbstractException的所有异常
     * 2. 区分是否有原因异常，采用不同的日志记录方式
     * 
     * @param request HTTP请求对象
     * @param ex 业务异常
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(value = {AbstractException.class})
    public Result abstractException(HttpServletRequest request, AbstractException ex) {
        if (ex.getCause() != null) {
            // 如果有原因异常，记录完整的异常栈信息
            log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), ex.toString(), ex.getCause());
            return Results.failure(ex);
        }
        // 仅记录异常信息
        log.error("[{}] {} [ex] {}", request.getMethod(), request.getRequestURL().toString(), ex.toString());
        return Results.failure(ex);
    }

    /**
     * 处理未预期的异常
     * 
     * 作为最后的防线，捕获所有未被其他处理器处理的异常
     * 
     * @param request HTTP请求对象
     * @param throwable 任意异常
     * @return 统一格式的错误响应
     */
    @ExceptionHandler(value = Throwable.class)
    public Result defaultErrorHandler(HttpServletRequest request, Throwable throwable) {
        // 记录详细的错误日志
        log.error("[{}] {} ", request.getMethod(), getUrl(request), throwable);
        // 返回通用错误响应
        return Results.failure();
    }

    /**
     * 获取完整的请求URL（包含查询参数）
     * 
     * @param request HTTP请求对象
     * @return 完整的URL字符串
     */
    private String getUrl(HttpServletRequest request) {
        // 根据是否有查询参数，构建不同格式的URL
        if (StringUtils.isEmpty(request.getQueryString())) {
            return request.getRequestURL().toString();
        }
        return request.getRequestURL().toString() + "?" + request.getQueryString();
    }
}
