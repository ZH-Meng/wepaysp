package com.zbsp.wepaysp.common.util;

import java.math.BigDecimal;
import java.math.BigInteger;

 
public class ArrayUtil {
	/**
	 * 从数组中取出索引为index的元素，塑形为Integer。若该元素为null，则返回defaultValue。
	 * 说明：此函数不检查是否数组是否越界，塑性是否正确。使用此函数一定要注意这两点。 使用此函数与直接使用语句相比的唯一好处就是减少书写错误。
	 *
	 * @param objects
	 * @param index
	 * @param defaultValue
	 * @return
	 */
	public static Integer getInteger(Object[] objects, int index, int defaultValue) {
		return objects[index] != null ? (Integer) objects[index] : Integer.valueOf(defaultValue);
	}

	/**
	 * 从数组中取出索引为index的元素，塑形为Long。若该元素为null，则返回defaultValue。
	 * 说明：此函数不检查是否数组是否越界，塑性是否正确。使用此函数一定要注意这两点。 使用此函数与直接使用语句相比的唯一好处就是减少书写错误。
	 *
	 * @param objects
	 * @param index
	 * @param defaultValue
	 * @return
	 */
	public static Long getLong(Object[] objects, int index, long defaultValue) {
		return objects[index] != null ? (Long) objects[index] : Long.valueOf(defaultValue);
	}
	
    // 2010.08.21 添加转化成BigDecimal的方法
    public static BigDecimal getBigDecimal(Object[] objects, int index, long defaultValue) {
        return objects[index] != null ? (BigDecimal) objects[index] : new BigDecimal(defaultValue);
    }

    // 2010.08.21 添加转化成BigInteger的方法
    public static BigInteger getBigInteger(Object[] objects, int index, String defaultValue) {
        return objects[index] != null ? (BigInteger) objects[index] : new BigInteger(defaultValue);
    }

}
