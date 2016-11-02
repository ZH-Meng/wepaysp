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
    
    /**
     * 是否是本月的最后一天
     * 
     * @param date
     * @return
     */
    public static boolean isMonthEnd(Date date) {
        
        Date date1 = date;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        
        return day == 1;
         
    }

    /**
     * 是否是本月的第一天
     * 
     * @param date
     * @return
     */
    public static boolean isFirstDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH) == 1;
    }

    /**
     * 得到day的起始时间点。
     * 
     * @param date
     * @return
     */
    public static Date getDayStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 得到day的终止时间点.
     * 
     * @param date
     * @return
     */
    public static Date getDayEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    /**
     * 得到month的起始时间点
     * 
     * @param date
     * @return
     */
    public static Date getMonthStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 得到month的终止时间点.
     * 
     * @param date
     * @return
     */
    public static Date getMonthEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    /**
     * 得到hour的起始时间点
     * 
     * @param date
     * @return
     */
    public static Date getHourStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 得到hour的终止时间点.
     * 
     * @param date
     * @return
     */
    public static Date getHourEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        calendar.add(Calendar.MILLISECOND, -1);
        return calendar.getTime();
    }

    /**
     * 得到前一个hour的起始时间点
     * 
     * @param date
     * @return
     */
    public static Date getLastHourStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        return calendar.getTime();
    }

    /**
     * 得到前一个day的起始时间点
     * 
     * @param date
     * @return
     */
    public static Date getLastDayStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        return calendar.getTime();
    }

    /**
     * 得到前一个month的起始时间点
     * 
     * @param date
     * @return
     */
    public static Date getLastMonthStart(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }
    
}
