package com.courier.core;

/**
 * @author Anthony
 * @create 2022/1/16
 * @desc
 */
public enum ThreadEnum {
    SYNC(1, "synchronizing mode"),
    ASYNC(2, "asynchronizing mode");

    public int code;
    public String desc;

    ThreadEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
