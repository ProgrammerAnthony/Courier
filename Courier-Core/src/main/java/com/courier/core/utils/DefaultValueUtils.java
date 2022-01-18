package com.courier.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;



/**
 * default value utils
 */
@Slf4j
public class DefaultValueUtils {


    public static String getOrDefault(String value, String defaultValue) {
        if (StringUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }


    public static Integer getOrDefault(Integer value, Integer defaultValue) {
        if (ObjectUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }


    public static Long getOrDefault(Long value, Long defaultValue) {
        if (ObjectUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }

    public static Boolean getOrDefault(Boolean value, Boolean defaultValue) {
        if (ObjectUtils.isEmpty(value)) {
            return defaultValue;
        }
        return value;
    }


}
