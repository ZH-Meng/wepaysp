/*
 * TimeUtil.java
 * 创建者：杨帆
 * 创建日期：2016年5月2日
 *
 * 版权所有(C) 2015-2018。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.common.util;

import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;

/**
 * 时间工具类.
 * 
 * @author 杨帆
 */
public final class TimeUtil {

    /**
     * 比较时间，是否第一个时间在第二个时间之后.
     * 
     * @param first 第一个时间
     * @param second 第二个时间
     * @return 比较结果
     */
    public static boolean timeAfter(Date first, Date second) {
        return new DateTime(first).isAfter(new DateTime(second));
    }
    
    /**
     * 比较时间，是否第一个时间在第二个时间之前.
     * 
     * @param first 第一个时间
     * @param second 第二个时间
     * @return 比较结果
     */
    public static boolean timeBefore(Date first, Date second) {
        return new DateTime(first).isBefore(new DateTime(second));
    }
    
    /**
     * 比较时间，是否第一个时间与第二个时间相等.
     * 
     * @param first 第一个时间
     * @param second 第二个时间
     * @return 比较结果
     */
    public static boolean timeEqual(Date first, Date second) {
        return new DateTime(first).isEqual(new DateTime(second));
    }
    
    /**
     * 比较时间先后.
     * 如果第一个时间在第二个时间后返回1，如果第一个时间在第二个时间前返回-1，
     * 如果两时间相等返回0.
     * 
     * @param first 第一个时间
     * @param second 第二个时间
     * @return 比较结果
     */
    public static Integer timeCompare(Date first, Date second) {
        if (timeAfter(first, second)) return 1;
        if (timeBefore(first, second)) return -1;
        if (timeEqual(first, second)) return 0;
        
        return null;
    }
    
    /**
     * 为指定时间增加秒数.
     * 
     * @param dateTime 指定时间
     * @param seconds 要增加的秒数
     * @return 变动后的时间
     */
    public static Date plusSeconds(Date dateTime, int seconds) {
        return new DateTime(dateTime).plusSeconds(seconds).toDate();
    }
    
    /**
     * 为当前时间增加秒数.
     * 
     * @param seconds 要增加的秒数.
     * @return 变动后的时间
     */
    public static Date plusSeconds(int seconds) {
        return DateTime.now().plusSeconds(seconds).toDate();
    }
    
    /**
     * 
     * @param date
     * @param timeUnit
     * @return
     */
    public static Date getLastTimeUnit(Date date, int timeUnit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        /* 取得上一个时间单位,即中间表中的前一小时，或前一天，前一月 */
        if (timeUnit == Calendar.HOUR_OF_DAY) {
            calendar.add(timeUnit, -1);
        } else if (timeUnit == Calendar.DAY_OF_MONTH) {
            calendar.add(timeUnit, -1);
        } else if (timeUnit == Calendar.MONTH) {
            calendar.add(timeUnit, -1);
        }
        return calendar.getTime();
    }
}
