package com.zbsp.wepaysp.common.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EnumUtil {
    
    /**
     * 通过getValue方法的返回值等于value获取枚举，需要枚举类有getValue方法
     * @param enumClass 枚举类
     * @param value
     * @return 如果存在返回枚举示例，如果不存在则返回null
     */
    public static <E extends Enum<E>> E getEnumByGetValueMethod(final Class<E> enumClass, final Serializable value) {
        if (value == null) {
            return null;
        }
        for (final E e : enumClass.getEnumConstants()) {
            try {
                Method method = e.getClass().getDeclaredMethod("getValue");
                if (value.equals(method.invoke(e))) {
                    return e;
                }
            } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e1) {
                e1.printStackTrace();
            }
        }
        return null;
    }
}
