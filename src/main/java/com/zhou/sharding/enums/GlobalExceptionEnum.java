package com.zhou.sharding.enums;

import lombok.Getter;

/**
 * @author wenyu zhou
 * @version 2022-02-26
 */
public enum GlobalExceptionEnum {
    /**
     * 全局异常码
     */
    SUCCESS(200,"成功"),
    FAIL(500,"失败"),
    PARAMETER_ERROR(501,"参数有误");

    @Getter
    private final Integer code;

    @Getter
    private final String msg;

    GlobalExceptionEnum(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
