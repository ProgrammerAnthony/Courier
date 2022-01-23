package com.courier.core.service;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSONUtil;
import com.courier.core.config.CourierConsistencyConfig;
import com.courier.core.utils.ReflectUtils;
import com.courier.core.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
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

    @Autowired
    private CourierConsistencyConfig courierConsistencyConfig;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeTaskInstance(CourierTaskInstance instance) {
        try {
            //task start mark
            int result = courierTaskService.turnOnTask(instance);
            if (result <= 0) {
                log.warn("task started，exit. task:{}", JSONUtil.toJsonStr(instance));
                return;
            }
            instance = courierTaskService.getTaskById(instance.getId(), 0L);
            taskScheduleManager.executeTask(instance);
            //task succeed mark
            int success = courierTaskService.markTaskSuccess(instance);
            log.info("task succeed and result is:{}", success);
        } catch (Exception e) {
            log.error("task instance :{} failed with error:", JSONUtil.toJsonStr(instance), e);
            instance.setErrorMsg(getErrorMsg(e));
            instance.setExecuteTime(getNextExecutionTime(instance));
            //task fail mark
            int failResult = courierTaskService.markTaskFail(instance);
            log.info("task failed with result:{},next execute time:{}", failResult > 0, instance.getExecuteTime());
            fallbackTaskInstance(instance);
        }
    }

    private Long getNextExecutionTime(CourierTaskInstance instance) {
        return null;
    }

    private String getErrorMsg(Exception e) {
        if ("".equals(e.getMessage())) {
            return "";
        }
        String errorMsg = e.getMessage();
        if (StringUtils.isEmpty(errorMsg)) {
            if (e instanceof IllegalAccessException) {
                IllegalAccessException illegalAccessException = (IllegalAccessException) e;
                errorMsg = illegalAccessException.getMessage();
            } else if (e instanceof IllegalArgumentException) {
                IllegalArgumentException illegalArgumentException = (IllegalArgumentException) e;
                errorMsg = illegalArgumentException.getMessage();
            } else if (e instanceof InvocationTargetException) {
                InvocationTargetException invocationTargetException = (InvocationTargetException) e;
                errorMsg = invocationTargetException.getTargetException().getMessage();
            }
        }
        return errorMsg.substring(0, Math.min(errorMsg.length(), 200));
    }

    /**
     * fallback when execute failed
     *
     * @param instance
     */
    @Override
    public void fallbackTaskInstance(CourierTaskInstance instance) {
        if (StringUtils.isEmpty(instance.getFallbackClassName())) {
            parseExpressionAndDoAlert(instance);
            return;
        }
        if (instance.getExecuteTimes() <= courierConsistencyConfig.getFailCountThreshold()) {
            return;
        }
        log.info("execute fallback of instanceId: {}}...", instance.getId());
        Class<?> fallbackClass = ReflectUtils.getClassByName(instance.getFallbackClassName());
        if (ObjectUtils.isEmpty(fallbackClass)) {
            return;
        }

        String taskParameterText = instance.getTaskParameter();
        String parameterTypes = instance.getParameterTypes();
        Class<?>[] paramTypes = getParamTypes(parameterTypes);
        Object[] paramValues = ReflectUtils.buildArgs(taskParameterText, paramTypes);
        Object fallbackClassBean =  SpringUtils.getBean(fallbackClass, paramValues);

        // get fallback method
        Method fallbackMethod = ReflectUtil.getMethod(fallbackClass, instance.getMethodName(), paramTypes);
        try {
            //method invoke
            fallbackMethod.invoke(fallbackClassBean, paramValues);
            int successResult = courierTaskService.markTaskSuccess(instance);

            log.info("task fallback succeed with result [{}]", successResult > 0);
        } catch (Exception e) {
            parseExpressionAndDoAlert(instance);
            instance.setFallbackErrorMsg(getErrorMsg(e));
            int failResult = courierTaskService.markTaskFail(instance);
            log.error("task fallback fail with result: [{}] next schedule time [{} - {}]", failResult > 0,
                    instance.getExecuteTime(), getFormatTime(instance.getExecuteTime()), e);
        }
    }

    private Class<?>[] getParamTypes(String parameterTypes) {
        return ReflectUtils.buildTypeClassArray(parameterTypes.split(","));

    }

    private void parseExpressionAndDoAlert(CourierTaskInstance instance) {
        try {
            if (StringUtils.isEmpty(instance.getAlertExpression())) {
                return;
            }

            alertNoticePool.submit(() -> {
                String expr = rewriteExpr(instance.getAlertExpression());
                String exprResult = readExpr(expr, ExpressionUtils.buildDataMap(instance));
                doAlert(exprResult, instance);
            });
        } catch (Exception e) {
            log.error("send alert with error", e);
        }
    }

    private String readExpr(String expr, Object buildDataMap) {
        return null;
    }

    private void doAlert(String exprResult, CourierTaskInstance instance) {

    }

    private String rewriteExpr(String alertExpression) {
        return null;
    }

    private String getFormatTime(long timestamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(timestamp);
    }
}
