package com.courier.core.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Anthony
 * @create 2022/1/19
 * @desc
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourierConsistencyConfig {
    /**
     * core pool size for schedule task
     */
    public Integer threadCorePoolSize;
    /**
     * max pool size for schedule task
     */
    public Integer threadMaxPoolSize;
    /**
     * queue size for schedule task
     */
    public Integer threadPoolQueueSize;
    /**
     * keep alive time
     */
    public Long threadPoolKeepAliveTime;
    /**
     * alternatives:[SECONDS,MINUTES,HOURS,DAYS,NANOSECONDS,MICROSECONDS,MILLISECONDS] for keep alive time
     */
    public String threadPoolKeepAliveTimeUnit;
    /**
     * fallback trigger when execution times is bigger than this threshold
     */
    public Integer failCountThreshold;

    /**
     * time range class full name is required here ,  which must implement {@link com.courier.core.custom.TaskTimeRangeQuery}
     */
    private String taskScheduleTimeRangeClassName = "";

}
