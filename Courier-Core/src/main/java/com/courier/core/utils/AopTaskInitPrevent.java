package com.courier.core.utils;

import java.util.function.Supplier;

/**
 * @author Anthony
 * @create 2022/1/23
 * @desc
 */
public class AopTaskInitPrevent {
    private static final ThreadLocal<Boolean> FLAG = ThreadLocal.withInitial(() -> false);

    public static Boolean shouldPrevent() {
        return FLAG.get();
    }

    public static void setPrevent(Boolean value) {
        FLAG.set(value);
    }
}
