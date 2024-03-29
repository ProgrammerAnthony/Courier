package com.courier.core.aspect;

import cn.hutool.json.JSONUtil;
import com.courier.core.CourierTaskStatusEnum;
import com.courier.core.ExecutionEnum;
import com.courier.core.annotation.CourierTask;
import com.courier.core.service.CourierTaskInstance;
import com.courier.core.service.CourierTaskService;
import com.courier.core.utils.AopTaskInitPrevent;
import com.courier.core.utils.ReflectUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * @author Anthony
 * @create 2022/1/16
 * @desc
 */
@Slf4j
@Aspect
@Component
public class CourierTaskAspect {
    @Autowired
    CourierTaskService courierTaskService;


    @Around("@annotation(courierTask)")
    public Object initCourierTask(ProceedingJoinPoint joinPoint, CourierTask courierTask) throws Throwable {
        log.info("access method:{} is called on {} args {}", joinPoint.getSignature().getName(), joinPoint.getThis(), joinPoint.getArgs());
        if(AopTaskInitPrevent.shouldPrevent()){
            return joinPoint.proceed();
        }

        CourierTaskInstance courierTaskInstance = createCourierTaskInstance(joinPoint, courierTask);
        courierTaskService.initTask(courierTaskInstance);
        return null;
    }

    private CourierTaskInstance createCourierTaskInstance(ProceedingJoinPoint point, CourierTask courierTask) {
        Class<?>[] argsClazz = ReflectUtils.getArgsClass(point.getArgs());
        String fullyQualifiedName = ReflectUtils.getTargetMethodFullyQualifiedName(point, argsClazz);
        String parameterTypes = ReflectUtils.getArgsClassNames(point.getSignature());

        Date date = new Date();

        CourierTaskInstance instance = CourierTaskInstance.builder()
                .taskId(StringUtils.isEmpty(courierTask.id()) ? fullyQualifiedName : courierTask.id())
                .methodName(point.getSignature().getName())
                .parameterTypes(parameterTypes)
                //add full qualified name to this instance
                .methodSignName(fullyQualifiedName)
                //json serialization args
                .taskParameter(JSONUtil.toJsonStr(point.getArgs()))
                //now or later
                .executeMode(courierTask.executeMode().getCode())
                .threadMode(courierTask.threadMode().getCode())
                .executeIntervalSec(courierTask.executeIntervalSec())
                .delayTime(courierTask.delayTime())
                .executeTimes(0)
                .taskStatus(CourierTaskStatusEnum.INIT.getCode())
                .errorMsg("")
                .alertExpression(StringUtils.isEmpty(courierTask.alertExpression()) ? "" : courierTask.alertExpression())
                .alertActionBeanName(StringUtils.isEmpty(courierTask.alarmBeanName()) ? "" : courierTask.alarmBeanName())
                .fallbackClassName(ReflectUtils.getFullyQualifiedClassName(courierTask.fallbackClass()))
                .fallbackErrorMsg("")
                .gmtCreate(date)
                .gmtModified(date)
                .build();

        instance.setExecuteTime(getExecuteTime(instance));

        return instance;
    }

    private Long getExecuteTime(CourierTaskInstance instance) {
        if (ExecutionEnum.EXECUTE_SCHEDULE.getCode() == instance.getExecuteMode()) {
            long delayTimeMillSecond = instance.getDelayTime() * 1000L;
            return System.currentTimeMillis() + delayTimeMillSecond;
        } else {
            return System.currentTimeMillis();
        }
    }
}
