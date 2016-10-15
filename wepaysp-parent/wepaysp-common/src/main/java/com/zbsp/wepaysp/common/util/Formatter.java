/*
 * Formatter.java
 * 创建者：杨帆
 * 创建日期：2016年5月3日
 *
 * 版权所有(C) 2015-2018。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.common.util;

import java.text.DecimalFormat;

/**
 * 格式化器工具类.
 * 
 * @author 杨帆
 */
public class Formatter {

    /**
     * 按照给定的字符格式化数字
     * 
     * @param formatLength 输出的字符串长度
     * @param paddingChar 长度不足时补足的字符(某些字符不可用，推荐使用数字或字母)
     * @param number 要格式化的数字
     * @return 格式化后的字符串
     */
    public static String formatNumber(int formatLength, char paddingChar, double number) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < formatLength; i++) {
            sb.append(paddingChar);
        }

        DecimalFormat format = new DecimalFormat(sb.toString());

        return format.format(number);
    }

    /**
     * 按照指定的格式格式化数字
     * 
     * @param pattern 格式字符串
     * @param number 要格式化的数字
     * @return 格式化后的字符串
     */
    public static String formatNumber(String pattern, double number) {
        DecimalFormat format = new DecimalFormat(pattern);

        return format.format(number);
    }
}
