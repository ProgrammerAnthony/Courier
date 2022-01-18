package com.courier.core.service;

import java.util.List;

/**
 * @author Anthony
 * @create 2022/1/16
 * @desc
 */
public interface CourierTaskService {
    void initTask(CourierTaskInstance courierTaskInstance);

    void markTaskFail(CourierTaskInstance courierTaskInstance);

    void markTaskSuccess(CourierTaskInstance courierTaskInstance);

    void markTaskFallbackFail(CourierTaskInstance courierTaskInstance);

    List<CourierTaskInstance> listUnfinishedTasks();

    CourierTaskInstance getTaskByIdAndShardId(Long id, Long shardKey);

    void submitTaskInstance(CourierTaskInstance courierTaskInstance);
}
