package com.courier.core.service;

import cn.hutool.extra.spring.SpringUtil;
import com.courier.core.CourierTaskStatusEnum;
import com.courier.core.ExecutionEnum;
import com.courier.core.ThreadEnum;
import com.courier.core.config.CourierConsistencyConfig;
import com.courier.core.custom.TaskTimeRangeQuery;
import com.courier.core.exception.CourierException;
import com.courier.core.mapper.TaskStoreMapper;
import com.courier.core.utils.ReflectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;

/**
 * @author Anthony
 * @create 2022/1/18
 * @desc
 */
@Slf4j
@Service
public class CourierTaskServiceImpl implements CourierTaskService {
    @Autowired
    private TaskStoreMapper taskStoreMapper;

    @Autowired
    private TaskEngineExecutor taskEngineExecutor;

    @Autowired
    private CompletionService<CourierTaskInstance> consistencyTaskPool;

    @Autowired
    private CourierConsistencyConfig courierConsistencyConfig;

    /**
     * init task and execute it only if  {@link ExecutionEnum.EXECUTE_NOW} mode
     *
     * @param courierTaskInstance
     */
    @Override
    public void initTask(CourierTaskInstance courierTaskInstance) {
        Long result = taskStoreMapper.initTask(courierTaskInstance);
        log.info("[Courier Consistency] init task result [{}]", result > 0);

        if (ExecutionEnum.EXECUTE_NOW.getCode() != courierTaskInstance.getExecuteMode()) {
            return;
        }

        // if transaction synchronization is active for the current thread,
        boolean synchronizationActive = TransactionSynchronizationManager.isSynchronizationActive();
        if (synchronizationActive) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronizationAdapter() {
                        @Override
                        public void afterCommit() {
                            submitTaskInstance(courierTaskInstance);
                        }
                    }
            );
        } else {
            submitTaskInstance(courierTaskInstance);
        }
    }

    @Override
    public int turnOnTask(CourierTaskInstance courierTaskInstance) {
        courierTaskInstance.setExecuteTime(System.currentTimeMillis());
        courierTaskInstance.setTaskStatus(CourierTaskStatusEnum.START.getCode());
        return taskStoreMapper.turnOnTask(courierTaskInstance);
    }

    @Override
    public int markTaskFail(CourierTaskInstance courierTaskInstance) {
        return taskStoreMapper.markFallbackFail(courierTaskInstance);
    }

    @Override
    public int markTaskSuccess(CourierTaskInstance courierTaskInstance) {
        return taskStoreMapper.markSuccess(courierTaskInstance);
    }

    @Override
    public void markTaskFallbackFail(CourierTaskInstance courierTaskInstance) {

    }


    @Override
    public List<CourierTaskInstance> listUnfinishedTasks() {
        Date startTime, endTime;
        Long limitTaskCount;
        try {
            // 获取TaskTimeLineQuery实现类
            if (!StringUtils.isEmpty(courierConsistencyConfig.getTaskScheduleTimeRangeClassName())) {
                // 获取Spring容器中所有对于TaskTimeRangeQuery接口的实现类
                Map<String, TaskTimeRangeQuery> beansOfTypeMap = SpringUtil.getBeansOfType(TaskTimeRangeQuery.class);
                TaskTimeRangeQuery taskTimeRangeQuery = getTaskTimeLineQuery(beansOfTypeMap);
                startTime = taskTimeRangeQuery.getStartTime();
                endTime = taskTimeRangeQuery.getEndTime();
                limitTaskCount = taskTimeRangeQuery.limitTaskCount();
                return taskStoreMapper.listByUnFinishTask(startTime.getTime(), endTime.getTime(), limitTaskCount);
            } else {
                startTime = TaskTimeRangeQuery.getStartTimeByStatic();
                endTime = TaskTimeRangeQuery.getEndTimeByStatic();
                limitTaskCount = TaskTimeRangeQuery.limitTaskCountByStatic();
            }
        } catch (Exception e) {
            log.error("[一致性任务框架] 调用业务服务实现具体的告警通知类时，发生异常", e);
            throw new CourierException(e);
        }
        return taskStoreMapper.listByUnFinishTask(startTime.getTime(), endTime.getTime(), limitTaskCount);
    }

    private TaskTimeRangeQuery getTaskTimeLineQuery(Map<String, TaskTimeRangeQuery> beansOfTypeMap) {

        if (beansOfTypeMap.size() == 1) {
            String[] beanNamesForType = SpringUtil.getBeanNamesForType(TaskTimeRangeQuery.class);
            return (TaskTimeRangeQuery) SpringUtil.getBean(beanNamesForType[0]);
        }

        Class<?> clazz = ReflectUtils.getClassByName(courierConsistencyConfig.getTaskScheduleTimeRangeClassName());
        return (TaskTimeRangeQuery) SpringUtil.getBean(clazz);
    }

    @Override
    public CourierTaskInstance getTaskById(Long id, Long shardKey) {
       return taskStoreMapper.getTaskByIdAndShardKey(id, shardKey);
    }

    @Override
    public void submitTaskInstance(CourierTaskInstance courierTaskInstance) {
        if (ThreadEnum.SYNC.getCode() == courierTaskInstance.getThreadMode()) {
            taskEngineExecutor.executeTaskInstance(courierTaskInstance);
        } else if (ThreadEnum.ASYNC.getCode() == courierTaskInstance.getThreadMode()) {
            consistencyTaskPool.submit(() -> {
                taskEngineExecutor.executeTaskInstance(courierTaskInstance);
                return courierTaskInstance;
            });
        }
    }
}
