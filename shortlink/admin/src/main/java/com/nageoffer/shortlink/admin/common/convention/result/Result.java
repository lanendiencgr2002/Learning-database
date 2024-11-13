

package com.nageoffer.shortlink.admin.common.convention.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应结果包装类
 * 
 * 这个类用于规范化所有 API 接口的返回格式，实现了序列化接口以支持网络传输。
 * 采用建造者模式（通过 @Accessors(chain = true)）支持链式调用。
 * 
 * 主要功能：
 * 1. 统一封装接口响应数据
 * 2. 提供成功/失败状态判断
 * 3. 支持请求追踪
 * 4. 规范化错误处理
 *
 * @param <T> 响应数据的类型参数，支持泛型以适应不同的数据类型
 */
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 5679018624309023727L;

    /**
     * 成功响应的状态码
     * 说明：
     * 1. 使用字符串"0"而不是数字0，避免在跨语言调用时的类型转换问题
     * 2. 便于与其他系统集成时的兼容性处理
     */
    public static final String SUCCESS_CODE = "0";

    /**
     * 响应状态码
     * 规则说明：
     * - 0: 表示接口调用成功
     * - 非0: 表示接口调用失败，具体错误码由业务定义
     */
    private String code;

    /**
     * 响应消息
     * 使用场景：
     * 1. 成功时可提供成功提示
     * 2. 失败时提供具体的错误信息
     * 3. 可用于前端直接显示给用户
     */
    private String message;

    /**
     * 响应数据
     * 说明：
     * 1. 可以是任意类型的业务数据
     * 2. 成功时携带实际业务数据
     * 3. 失败时可以为空
     */
    private T data;

    /**
     * 请求ID
     * 用途：
     * 1. 用于分布式系统中的请求追踪
     * 2. 有助于问题排查和日志分析
     * 3. 支持分布式链路追踪
     */
    private String requestId;

    /**
     * 判断请求是否成功
     * 实现说明：
     * 1. 通过比较当前响应码与成功码（"0"）判断
     * 2. 使用 equals 而不是 == 进行字符串比较
     * 
     * @return true: 请求成功; false: 请求失败
     */
    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }
}
