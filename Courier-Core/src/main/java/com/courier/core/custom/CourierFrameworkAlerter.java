package com.courier.core.custom;

import com.courier.core.service.CourierTaskInstance;

/**
 * @author Anthony
 * @create 2022/1/19
 * @desc implement this class to send alert when fallback fails
 */
public interface CourierFrameworkAlerter {
    void sendAlertNotice(CourierTaskInstance courierTaskInstance);
}
