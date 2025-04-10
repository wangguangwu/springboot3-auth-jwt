package com.wangguangwu.springboot3_auth_jwt.model;

import lombok.Data;

/**
 * API 响应封装类
 *
 * @author wangguangwu
 */
@Data
public class ApiResponse<T> {

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 成功响应
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return API 响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "SUCCESS", data);
    }

    /**
     * 错误响应
     *
     * @param code    状态码
     * @param message 错误信息
     * @param <T>     数据类型
     * @return API 响应
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return error(code, message, null);
    }

    /**
     * 错误响应
     *
     * @param code    错误码
     * @param message 错误消息
     * @param data    错误数据
     * @param <T>     数据类型
     * @return API 响应
     */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }
}
