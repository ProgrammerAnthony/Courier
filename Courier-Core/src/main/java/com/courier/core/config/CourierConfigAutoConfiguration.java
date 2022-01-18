package com.courier.core.config;

import com.courier.core.custom.TaskTimeRangeQuery;
import com.courier.core.exception.CourierException;
import com.courier.core.utils.ReflectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

import static com.courier.core.utils.DefaultValueUtils.getOrDefault;

/**
 * @author Anthony
 * @create 2022/1/19
 * @desc spring boot Auto Configuration
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {
        CourierConsistencyConfigProperties.class,
        CourierFallbackConfigProperties.class
})
public class CourierConfigAutoConfiguration {
    @Autowired
    CourierConsistencyConfigProperties courierConsistencyConfigProperties;

    @Autowired
    CourierFallbackConfigProperties courierFallbackConfigProperties;

    @Bean
    public CourierConsistencyConfig buildCourierConsistencyConfig() {
        doConfigCheck(courierConsistencyConfigProperties);

        return CourierConsistencyConfig
                .builder()
                .threadCorePoolSize(getOrDefault(courierConsistencyConfigProperties.getThreadCorePoolSize(), 5))
                .threadMaxPoolSize(getOrDefault(courierConsistencyConfigProperties.getThreadMaxPoolSize(), 5))
                .threadPoolQueueSize(getOrDefault(courierConsistencyConfigProperties.getThreadPoolQueueSize(), 100))
                .threadPoolKeepAliveTime(getOrDefault(courierConsistencyConfigProperties.getThreadPoolKeepAliveTime(), 60L))
                .threadPoolKeepAliveTimeUnit(getOrDefault(courierConsistencyConfigProperties.getThreadPoolKeepAliveTimeUnit(), "SECONDS"))
                .taskScheduleTimeRangeClassName(getOrDefault(courierConsistencyConfigProperties.getTaskScheduleTimeRangeClassName(), ""))
                .failCountThreshold(getOrDefault(courierFallbackConfigProperties.getFailCountThreshold(), 2))
                .build();

    }


    private void doConfigCheck(CourierConsistencyConfigProperties courierConsistencyConfigProperties) {
        TimeUnit timeUnit = null;
        if (!StringUtils.isEmpty(courierConsistencyConfigProperties.getThreadPoolKeepAliveTimeUnit())) {
            try {
                timeUnit = TimeUnit.valueOf(courierConsistencyConfigProperties.getThreadPoolKeepAliveTimeUnit());
            } catch (IllegalArgumentException e) {
                log.error("check threadPoolKeepAliveTimeUnit error", e);
                String errMsg = "threadPoolKeepAliveTimeUnit config error！alternative: [SECONDS,MINUTES,HOURS,DAYS,NANOSECONDS,MICROSECONDS,MILLISECONDS]";
                throw new CourierException(errMsg);
            }
        }

        if (!StringUtils.isEmpty(courierConsistencyConfigProperties.getTaskScheduleTimeRangeClassName())) {
            // exists check
            Class<?> taskScheduleTimeRangeClass = ReflectUtils.checkClassByName(courierConsistencyConfigProperties.getTaskScheduleTimeRangeClassName());
            if (ObjectUtils.isEmpty(taskScheduleTimeRangeClass)) {
                String errMsg = String.format("could not find class %s ，check the class path", courierConsistencyConfigProperties.getTaskScheduleTimeRangeClassName());
                throw new CourierException(errMsg);
            }
            //implements check
            boolean result = ReflectUtils.isRealizeTargetInterface(taskScheduleTimeRangeClass,
                    TaskTimeRangeQuery.class.getName());
            if (!result) {
                String errMsg = String.format("class %s ，not implement TaskTimeRangeQuery", courierConsistencyConfigProperties.getTaskScheduleTimeRangeClassName());
                throw new CourierException(errMsg);
            }
        }


    }


}
