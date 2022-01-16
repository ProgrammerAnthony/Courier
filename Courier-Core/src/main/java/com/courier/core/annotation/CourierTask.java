package com.courier.core.annotation;

import com.courier.core.ExecutionEnum;
import com.courier.core.ThreadEnum;

import java.lang.annotation.*;

/**
 * @author Anthony
 * @create 2022/1/16
 * @desc
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface CourierTask {

    String id() default "";

    Class<?> fallbackClass() default void.class;

    String alarmBeanName() default "";

    int executeIntervalSec() default 60;

    String alertExpression() default "executeTimes > 1 && executeTimes < 5";

    int delayTime() default 60;

    ExecutionEnum executeMode() default ExecutionEnum.EXECUTE_NOW;

    ThreadEnum threadMode() default ThreadEnum.ASYNC;
}
