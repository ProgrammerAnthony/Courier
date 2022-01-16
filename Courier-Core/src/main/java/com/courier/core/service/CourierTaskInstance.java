package com.courier.core.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Anthony
 * @create 2022/1/16
 * @desc
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourierTaskInstance {

    private Long id;
    /**
     * user define task id
     */
    private String taskId;
    /**
     * full method sign name
     */
    private String methodSignName;
    /**
     * method name
     */
    private String methodName;
    /**
     * parameter class path
     */
    private String parameterTypes;
    /**
     * parameter in Json
     */
    private String taskParameter;
    /**
     * task status
     */
    private int taskStatus;
    /**
     * default 60s interval
     */
    private int executeIntervalSec;
    /**
     * default 60s delay
     */
    private int delayTime;
    /**
     * execute times
     */
    private int executeTimes;
    /**
     * execute time
     */
    private Long executeTime;
    /**
     * error message when process
     */
    private String errorMsg;
    /**
     * execute mode
     */
    private Integer executeMode;
    /**
     * thread mode
     */
    private Integer threadMode;
    /**
     * alarm expression
     */
    private String alertExpression;
    /**
     * Alarm action bean name is required to implement CourierFrameworkAlerter method and add to Spring
     */
    private String alertActionBeanName;
    /**
     * fallback class
     */
    private String fallbackClassName;
    /**
     * fallback error message
     */
    private String fallbackErrorMsg;
    /**
     * shard key
     */
    private Long shardKey;

    private Date gmtCreate;

    private Date gmtModified;
}
