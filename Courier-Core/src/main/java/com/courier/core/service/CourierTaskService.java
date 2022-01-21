package com.courier.core.service;

import java.util.List;

/**
 * @author Anthony
 * @create 2022/1/16
 * @desc
 */
public interface CourierTaskService {

    void initTask(CourierTaskInstance courierTaskInstance);

    int turnOnTask(CourierTaskInstance courierTaskInstance);

    int markTaskFail(CourierTaskInstance courierTaskInstance);

    int markTaskSuccess(CourierTaskInstance courierTaskInstance);

    void markTaskFallbackFail(CourierTaskInstance courierTaskInstance);

    List<CourierTaskInstance> listUnfinishedTasks();

    CourierTaskInstance getTaskById(Long id, Long shardKey);

    void submitTaskInstance(CourierTaskInstance courierTaskInstance);
}
