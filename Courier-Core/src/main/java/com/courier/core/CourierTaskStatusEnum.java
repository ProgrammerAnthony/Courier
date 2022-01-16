package com.courier.core;

/**
 * @author Anthony
 * @create 2022/1/16
 * @desc
 */
public enum CourierTaskStatusEnum {
    INIT(1, "init status"),
    START(2, "init status"),
    SUCCESS(3, "success status"),
    FAIL(4, "fail status");

    public int code;
    public String desc;

    CourierTaskStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public int getCode() {
        return code;
    }
}

