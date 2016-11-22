/*
 * Generator.java
 * 创建者：杨帆
 * 创建日期：2016年4月6日
 *
 * 版权所有(C) 2015-2018。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.common.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * 生成器工具类
 * 
 * @author 杨帆
 */
public final class Generator {
    public static void main(String[] args) {
        System.out.println(generateSequenceYYYYMMddNum(1, 10000000000L));
        System.out.println(generateRandomNumber(32));
    }
    
    /**
     * 生成YYMMdd+五位数序列值，例如：2016102700001.
     * 
     * @return 
     */
    public static String generateSequenceYYYYMMddNum(int nextval, long multiple) {
        String seqPrefix = DateUtil.getDate(new Date(), "YYYYMMdd");
        return (new BigDecimal(Long.valueOf(seqPrefix)).multiply(new BigDecimal(multiple)).add(new BigDecimal(nextval))).toString();
    }
    
    /**
     * 生成YYMMdd+五位数序列值，例如：16102700001.
     * 
     * @return 
     */
    public static String generateSequenceNum(int nextval, int multiple) {
        String seqPrefix = DateUtil.getDate(new Date(), "YYYYMMdd").substring(2);
        return (new BigDecimal(Long.valueOf(seqPrefix)).multiply(new BigDecimal(multiple)).add(new BigDecimal(nextval))).toString();
    }
    
    /**
     * 生成iwoid.
     * 
     * @return iwoid
     */
    public static String generateIwoid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
    /**
     * 生成指定长度的随机数字字符串
     * 
     * @param length 指定长度
     * @return 随机数字字符串
     */
    public static String generateRandomNumber(int length) {
        if (length <= 0) {
            return "";
        }
        
        Random random = new Random();
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
    
    /**
     * 生成指定长度的随机字符串
     * 
     * @param length 指定长度
     * @return 随机字符串
     */
    public static String generateRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        
        Random random = new Random();
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        
        return sb.toString();
    }
    
    /**
     * 在指定的字符范围内生成指定长度的随机字符串
     * 
     * @param characterRange 指定的字符范围
     * @param length 指定长度
     * @return 随机字符串
     */
    public static String generateRandomString(String characterRange, int length) {
        if (characterRange == null) {
            return null;
        }
        
        Random random = new Random();
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(characterRange.length());
            sb.append(characterRange.charAt(number));
        }
        
        return sb.toString();
    }
    
    /**
     * 生成指定长度的随机密码（字母全部小写）.
     * 
     * @param length 指定长度
     * @return 随机密码
     */
    public static String generateRandomPwd(int length) {
        String base = "abcdefghijkmnopqrstuvwxyz0123456789";
        
        Random random = new Random();
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
    
    /**
     * 生成符合特定条件的正整数随机数组.
     * 
     * @param min 随机数的起始值
     * @param max 随机数最大值
     * @param amount 数组长度
     * @param isRepeat 是否允许重复，true允许，false不允许
     * @return 随机数组
     */
    public static int[] generateRandomIntArray(int min, int max, int amount, boolean isRepeat) {
        if (min < 0 || max < 0) {
            throw new IllegalArgumentException("随机数的起始值或最大值不能是负数");
        }
        
        if (!isRepeat && (max - min) < (amount - 1)) {
            throw new IllegalArgumentException("该范围内不能产生不重复的随机数组");
        }
        
        if (min > max) {
            return null;
        }
        
        if (amount <= 0) {
            return null;
        }
        
        Random random = new Random();
        int i = 0;
        int temp = 0;
        int[] codeNumbers = new int[amount];
        
        while (i < amount) {
            temp = random.nextInt(max) % (max - min + 1) + min;
            if (!isRepeat) {
                int flag = 0;
                for (int k = 0; k < i; k++) {
                    if (codeNumbers[k] == temp) {
                        flag = 1;
                        break;
                    }
                }
                if (flag == 1) {
                    continue;
                } else {
                    codeNumbers[i] = temp;
                    i++;
                }
            } else {
                codeNumbers[i] = temp;
                i++;
            }
        }
        return codeNumbers;
    }
}
