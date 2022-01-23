package com.courier.core.service;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.courier.core.exception.CourierException;
import com.courier.core.utils.AopTaskInitPrevent;
import com.courier.core.utils.ReflectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
        String methodSignName = instance.getMethodSignName();
        Class<?> clazz = getTaskMethodClass(methodSignName.split(ReflectUtils.CLAZZ_SEPARATOR)[0]);
        if (ObjectUtils.isEmpty(clazz)) {
            return;
        }
        Object bean = SpringUtil.getBean(clazz);
        if (ObjectUtils.isEmpty(bean)) {
            return;
        }

        String methodName = instance.getMethodName();
        String[] parameterTypes = instance.getParameterTypes().split(ReflectUtils.METHOD_SEPARATOR);
        Class<?>[] parameterClasses = ReflectUtils.buildTypeClassArray(parameterTypes);
        Method targetMethod = getTargetMethod(methodName, parameterClasses, clazz);
        if (ObjectUtils.isEmpty(targetMethod)) {
            return;
        }
        Object[] args = ReflectUtils.buildArgs(instance.getTaskParameter(), parameterClasses);


        try {
            AopTaskInitPrevent.setPrevent(true);
            targetMethod.invoke(bean, args);
            AopTaskInitPrevent.setPrevent(false);
        } catch (InvocationTargetException e) {
            log.error("[Courier Consistency]load target method error", e);
            Throwable target = e.getTargetException();
            throw new CourierException((Exception) target);
        } catch (Exception ex) {
            throw new CourierException(ex);
        }
    }

    private Class<?> getTaskMethodClass(String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
            return clazz;
        } catch (ClassNotFoundException e) {
            log.error("[Courier Consistency]load target class error", e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get Method object
     * parameterTypes –
     *
     * @param methodName              the name of the method
     * @param parameterTypeClassArray the list of parameters
     * @param clazz                   class name
     * @return
     */
    private Method getTargetMethod(String methodName, Class<?>[] parameterTypeClassArray, Class<?> clazz) {
        try {
            return clazz.getMethod(methodName, parameterTypeClassArray);
        } catch (NoSuchMethodException e) {
            log.error("[Courier Consistency]load target method error", e);
            return null;
        }
    }
}
