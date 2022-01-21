package com.courier.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.CompletionService;

/**
 * @author Anthony
 * @create 2022/1/19
 * @desc task manager for schedule type task
 */
@Slf4j
@Component
public class TaskScheduleManager {
    @Autowired
    CourierTaskService courierTaskService;
    @Autowired
    private CompletionService<CourierTaskInstance> consistencyTaskPool;

    @Resource
    private TaskEngineExecutor taskEngineExecutor;

    public void executeTask(CourierTaskInstance instance) {

    }
}
