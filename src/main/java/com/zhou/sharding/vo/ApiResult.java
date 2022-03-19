package com.zhou.sharding.vo;

import com.alibaba.fastjson.JSON;
import com.zhou.sharding.enums.GlobalExceptionEnum;
import lombok.Data;

/**
 * @author wenyu zhou
 */
@Data
public class ApiResult<T> {

    private int code;
    private String msg;
    private T data;

    public ApiResult(){}

    public ApiResult(GlobalExceptionEnum exceptionEnum){
        this.code = exceptionEnum.getCode();
        this.msg = exceptionEnum.getMsg();
    }

    public ApiResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ApiResult<T> success(String message) {
        return new ApiResult<>(GlobalExceptionEnum.SUCCESS.getCode(), message);
    }

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(GlobalExceptionEnum.SUCCESS.getCode(), GlobalExceptionEnum.SUCCESS.getMsg(), data);
    }

    public static <T> ApiResult<T> success() {
        return success(GlobalExceptionEnum.SUCCESS.getMsg());
    }

    public static <T> ApiResult<T> error(String message) {
        return new ApiResult<>(GlobalExceptionEnum.FAIL.getCode(), message);
    }

    public static <T> ApiResult<T> error() {
        return error(GlobalExceptionEnum.FAIL.getMsg());
    }

    public static <T> ApiResult<T> paramError(String message) {
        return new ApiResult<>(GlobalExceptionEnum.PARAMETER_ERROR.getCode(), message);
    }

    public boolean checkFail() {
        return this.code != GlobalExceptionEnum.SUCCESS.getCode();
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
