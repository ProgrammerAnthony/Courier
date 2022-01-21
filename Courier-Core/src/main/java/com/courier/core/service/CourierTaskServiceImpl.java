package com.courier.core.service;

import com.courier.core.ExecutionEnum;
import com.courier.core.ThreadEnum;
import com.courier.core.mapper.TaskStoreMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.concurrent.CompletionService;

/**
 * @author Anthony
 * @create 2022/1/18
 * @desc
 */
@Slf4j
@Service
public class CourierTaskServiceImpl implements CourierTaskService{
    @Autowired
    private TaskStoreMapper taskStoreMapper;

    @Autowired
    private TaskEngineExecutor taskEngineExecutor;

    @Autowired
    private CompletionService<CourierTaskInstance> consistencyTaskPool;

    /**
     * init task and execute it only if  {@link ExecutionEnum.EXECUTE_NOW} mode
     * @param courierTaskInstance
     */
    @Override
    public void initTask(CourierTaskInstance courierTaskInstance) {
        Long result = taskStoreMapper.initTask(courierTaskInstance);
        log.info("[Courier] init task result [{}]", result > 0);

        if (ExecutionEnum.EXECUTE_NOW.getCode()!=courierTaskInstance.getExecuteMode()) {
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
        return 0;
    }

    @Override
    public void markTaskFail(CourierTaskInstance courierTaskInstance) {

    }

    @Override
    public void markTaskSuccess(CourierTaskInstance courierTaskInstance) {

    }

    @Override
    public void markTaskFallbackFail(CourierTaskInstance courierTaskInstance) {

    }

    @Override
    public List<CourierTaskInstance> listUnfinishedTasks() {
        return null;
    }

    @Override
    public CourierTaskInstance getTaskById(Long id, Long shardKey) {
        return null;
    }

    @Override
    public void submitTaskInstance(CourierTaskInstance courierTaskInstance) {
        if (ThreadEnum.SYNC.getCode()==courierTaskInstance.getThreadMode()) {
            taskEngineExecutor.executeTaskInstance(courierTaskInstance);
        } else if (ThreadEnum.ASYNC.getCode()==courierTaskInstance.getThreadMode()) {
            consistencyTaskPool.submit(() -> {
                taskEngineExecutor.executeTaskInstance(courierTaskInstance);
                return courierTaskInstance;
            });
        }
    }
}
