package com.courier.core.custom;

import java.util.Calendar;
import java.util.Date;

import static com.courier.core.utils.DateUtils.getDateByDayNum;

/**
 * task query range ,
 * remember to implement this class when you want to customize your own time range query
 */
public interface TaskTimeRangeQuery {


    Date getStartTime();


    Date getEndTime();


    Long limitTaskCount();


    /**
     * default for{@link #getStartTime()}
     *
     * @return
     */
    static Date getStartTimeByStatic() {
        return getDateByDayNum(new Date(), Calendar.HOUR, -1);
    }


    /**
     * default for{@link #getEndTime()} ()}
     *
     * @return
     */
    static Date getEndTimeByStatic() {
        return new Date();
    }

    /**
     * default for{@link #limitTaskCount()} ()}
     *
     * @return
     */
    static Long limitTaskCountByStatic() {
        return 1000L;
    }
}