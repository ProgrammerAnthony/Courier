package com.courier.core;

/**
 * @author Anthony
 * @create 2022/1/16
 * @desc
 */
public enum ExecutionEnum {
    EXECUTE_NOW(1, "execute right now"),
    EXECUTE_SCHEDULE(2, "execute later");

    public int code;
    public String desc;

    ExecutionEnum(int code, String desc) {
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
