package com.courier.core.service;

/**
 * @author Anthony
 * @create 2022/1/18
 * @desc
 */
public interface TaskEngineExecutor {


    void executeTaskInstance(CourierTaskInstance instance);

    /**
     * execute when {@link #executeTaskInstance(CourierTaskInstance)} fails
     * @param instance
     */
    void fallbackTaskInstance(CourierTaskInstance instance);
}
