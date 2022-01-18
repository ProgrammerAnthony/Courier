package com.courier.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Anthony
 * @create 2022/1/19
 * @desc
 */
public class TaskEngineExecutorService implements TaskEngineExecutor{

    @Autowired
    private CourierTaskService courierTaskService;

    @Autowired
    private TaskScheduleManager taskScheduleManager;

    @Autowired
    private ThreadPoolExecutor alertNoticePool;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeTaskInstance(CourierTaskInstance instance) {
        //todo
    }

    @Override
    public void fallbackTaskInstance(CourierTaskInstance instance) {
        //todo
    }
}
