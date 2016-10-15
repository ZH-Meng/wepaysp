/*
 * Validator.java
 * 创建者：杨帆
 * 创建日期：2016年4月6日
 *
 * 版权所有(C) 2015-2018。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.common.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 校验工具类.
 * 
 * @author 杨帆
 */
public final class Validator {
    
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String LOG_PREFIX = "[Validator]-";
    
    /**
     * 检查字符串是否符合正则表达式.
     * 
     * @param regex 正则表达式
     * @param checkString 要检查的字符串
     * @return 验证通过返回true，否则false
     */
    public static boolean checkByPattern(String regex, String checkString) {
        if (regex == null) {
            return false;
        }
        Pattern ptt = Pattern.compile(regex);
        Matcher mch = ptt.matcher(checkString);
        return mch.matches();
    }
    
    /**
     * 判断一个值是否在枚举中定义.
     * 比较时使用value的equals方法.
     * 
     * @param <T> 枚举必须定义一个getValue()的无参方法，并返回一个已实现java.io.Serializable的值
     * @param enumType 枚举类型
     * @param value 要判断的值
     * @return 存在返回true，不存在返回false
     */
    public static <T extends Enum<?>> boolean contains(Class<T> enumType, Serializable value) {
        T[] constantArr = enumType.getEnumConstants();
        for (T constant : constantArr) {
            try {
                Method method = constant.getClass().getDeclaredMethod("getValue");

                if (method.invoke(constant).equals(value)) {
                    return true;
                }
            } catch (SecurityException e) {
                LOGGER.error(LOG_PREFIX + "[枚举定义验证错误]", e);
            } catch (NoSuchMethodException e) {
                LOGGER.error(LOG_PREFIX + "[枚举定义验证错误]", e);
            } catch (IllegalArgumentException e) {
                LOGGER.error(LOG_PREFIX + "[枚举定义验证错误]", e);
            } catch (IllegalAccessException e) {
                LOGGER.error(LOG_PREFIX + "[枚举定义验证错误]", e);
            } catch (InvocationTargetException e) {
                LOGGER.error(LOG_PREFIX + "[枚举定义验证错误]", e);
            }
        }

        return false;
    }
    
    /**
     * 检查表达式是否满足
     * 
     * @param expression 表达式是否满足
     * @param errorMessage 满足时返回的提示
     */
    public static void checkArgument(boolean expression, String errorMessage) {
        if (expression) {
            throw new IllegalArgumentException(errorMessage);
        } else {
            return;
        }
    }
}
