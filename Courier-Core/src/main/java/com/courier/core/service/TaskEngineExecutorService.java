package com.courier.core.service;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Anthony
 * @create 2022/1/19
 * @desc
 */
@Slf4j
public class TaskEngineExecutorService implements TaskEngineExecutor {

    @Autowired
    private CourierTaskService courierTaskService;

    @Autowired
    private TaskScheduleManager taskScheduleManager;

    @Autowired
    private ThreadPoolExecutor alertNoticePool;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeTaskInstance(CourierTaskInstance instance) {
        try {
            int result = courierTaskService.turnOnTask(instance);
            if (result <= 0) {
                log.warn("task started，exit. task:{}", JSONUtil.toJsonStr(instance));
                return;
            }
            instance = courierTaskService.getTaskById(instance.getId(), 0L);
            taskScheduleManager.executeTask(instance);
            int success = courierTaskService.markTaskSuccess(instance);
            log.info("task succeed and result is:{}", success);
        }catch (Exception e){
            log.error("task instance :{} failed with error:",JSONUtil.toJsonStr(instance),e);
            instance.setErrorMsg(getErrorMsg(e));
            instance.setExecuteTime(getNextExecutionTime(instance));
            int failResult = courierTaskService.markTaskFail(instance);
            log.info("task failed with result:{},next execute time:{}",failResult>0,instance.getExecuteTime());
            fallbackTaskInstance(instance);
        }
    }

    private Long getNextExecutionTime(CourierTaskInstance instance) {
        return null;
    }

    private String getErrorMsg(Exception e) {
        return null;
    }

    @Override
    public void fallbackTaskInstance(CourierTaskInstance instance) {
        //todo
    }
}
