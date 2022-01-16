package com.courier.core.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Anthony
 * @create 2022/1/16
 * @desc
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
@Import({CourierTaskSelector.class})
public @interface EnableCourierTask {
}
