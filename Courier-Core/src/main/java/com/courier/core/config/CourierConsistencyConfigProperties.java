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
@ConfigurationProperties(prefix = "courier.consistency.parallel.pool")
public class CourierConsistencyConfigProperties {
    /**
     * core pool size for schedule task
     */
    public Integer threadCorePoolSize = 5;
    /**
     * max pool size for schedule task
     */
    public Integer threadMaxPoolSize = 5;
    /**
     * queue size for schedule task
     */
    public Integer threadPoolQueueSize = 100;
    /**
     * keep alive time
     */
    public Long threadPoolKeepAliveTime = 60L;
    /**
     * alternatives:[SECONDS,MINUTES,HOURS,DAYS,NANOSECONDS,MICROSECONDS,MILLISECONDS] for keep alive time
     */
    public String threadPoolKeepAliveTimeUnit = "SECONDS";

    /**
     * time range class full name is required here ,  which must implement {@link com.courier.core.custom.TaskTimeRangeQuery}
     */
    private String taskScheduleTimeRangeClassName = "";

}
