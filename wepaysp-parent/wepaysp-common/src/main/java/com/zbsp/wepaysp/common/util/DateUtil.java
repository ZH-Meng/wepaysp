package com.zbsp.wepaysp.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.StringUtils;

/**
 * 日期工具类
 * 
 * @author 刘钊
 */
public class DateUtil {

    /**
     * 获取当前时刻为周几
     * 
     * @param date
     *            时间对象
     * @return int
     */
    public static int getWeek(Date date) {
        if (date == null) {
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.DAY_OF_WEEK);
        if (week == 1) {
            return 7;
        } else {
            return week - 1;
        }
    }

    /**
     * 日期相加
     * 
     * @param date
     *            日期
     * @param field
     *            相加模式，具体值为Calendar类的相关常量
     * @param amount
     *            相加数量，如果为负数则为相减
     * @return Date
     */
    public static Date addDate(Date date, int field, int amount) {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        gc.add(field, amount);
        return gc.getTime();
    }

    /**
     * 根据表达式格式化日期
     * 
     * @param date
     *            日期对象
     * @param pattern
     *            表达式
     * @return String
     */
    public static String getDate(Date date, String pattern) {
        SimpleDateFormat df = null;
        String returnValue = "";
        if (date != null) {
            df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }

    /**
     * 根据把String类型日期转换成Date类型
     * 
     * @param strDate
     *            String日期
     * @param pattern
     *            表达式
     * @return Date
     */
    public static Date getDate(String strDate, String pattern) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        } else {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            try {
                return df.parse(strDate);
            } catch (ParseException e) {
                return null;
            }
        }
    }
    /**
     * @param c
     *            y 年 M 月 d 天 H小时 m 分钟 s秒
     * @param number
     *            向前 向后 数量
     * @param zf
     *            正1代表向后后 -1 代表向 前
     * @return
     */
    public static Date getTime(Date date,char c, int number, int zf) {
        number = number * zf;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);// 设置当前时间
        switch (c) {
            case 'y':
                calendar.add(Calendar.YEAR, number);
                break;
            case 'M':
                calendar.add(Calendar.MONTH, number);
                break;
            case 'd':
                calendar.add(Calendar.DAY_OF_YEAR, number);
                break;
            case 'H':
                calendar.add(Calendar.HOUR, number);
                break;
            case 'm':
                calendar.add(Calendar.MINUTE, number);
                break;
            case 's':
                calendar.add(Calendar.SECOND, number);
                break;
            default:
                break;
        }
        return calendar.getTime();
    }
    
    /**
     * 获取前一天日期
     * 
     * @return Date
     */
    public static Date getYesterday() {
		Calendar calendar = Calendar.getInstance(); 
		calendar.add(Calendar.DAY_OF_MONTH, -1); // 设置为前一天
		return calendar.getTime();
    }
    /**
	 * 获取指定日期的一天的的最后时刻23:59:59
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFinallyDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat returnFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

		String temp = format.format(date);
		temp += " 23:59:59";

		try {
			return returnFormat.parse(temp);
		} catch (ParseException e) {
			return null;
		}
	}
	
}
