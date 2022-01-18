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
@ConfigurationProperties(prefix = "courier.consistency.action")
public class CourierFallbackConfigProperties {
    /**
     * trigger fallback
     */
    public Integer failCountThreshold = 0;
}
