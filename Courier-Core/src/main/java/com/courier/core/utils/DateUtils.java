package com.courier.core.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * @author Anthony
 * @create 2022/1/19
 * @desc
 */
public class DateUtils {

    /**
     * get num days before date
     * @param startDate
     * @param calendarUnit
     * @param num
     * @return
     */
    public static Date getDateByDayNum(Date startDate, int calendarUnit, int num) {
        if (num == 0) {
            return startDate;
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startDate);
        calendar.add(calendarUnit, num);
        return calendar.getTime();
    }
}
