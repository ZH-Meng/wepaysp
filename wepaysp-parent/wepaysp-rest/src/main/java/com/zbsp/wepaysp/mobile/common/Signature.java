package com.zbsp.wepaysp.mobile.common;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zbsp.wepaysp.common.security.DigestHelper;

import net.sf.cglib.beans.BeanMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Rest 通讯签名算法
 */
public class Signature {

    private final static Logger logger = LogManager.getLogger();

    /**
     * 签名算法，过滤 signature
     * @param o 要参与签名的数据对象
     * @param key 密钥
     * @return 签名
     * @throws IllegalAccessException
     */
    public static String getSign(Object o, String key) throws IllegalAccessException {
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<Field> fieldList = new ArrayList<Field>();

        Class<?> clazz = o.getClass();
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
        }

        for (Field f : fieldList) {
            f.setAccessible(true);
            if (f.get(o) != null && f.get(o) != "" && !"signature".equals(f.getName())) {
                list.add(f.getName() + "=" + f.get(o) + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + key;
        logger.info("Sign Before MD5:" + result);
        result = DigestHelper.md5Hex(result).toUpperCase();
        logger.info("Sign Result:" + result);
        return result;
    }

    /**
     * 签名算法，过滤 signature
     * @param map 要参与签名的数据Map
     * @param key 密钥
     * @return 签名
     */
    public static String getSign(Map<String, Object> map, String key) {
        ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() != "" && !"signature".equals(entry.getKey())) {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + key;
        logger.info("Sign Before MD5:" + result);
        result = DigestHelper.md5Hex(result).toUpperCase();
        logger.info("Sign Result:" + result);
        return result;
    }

    /**
     * 检验rest 请求数据里面的签名是否合法，避免数据在传输的过程中被第三方篡改
     * @param request rest 请求包
     * @param key 密钥
     * @return 签名是否合法
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static boolean checkIsSignValidFromRequest(Object request, String key) {
        if (request == null) {
            logger.warn("数据签名数据不存在，有可能被第三方篡改!!!");
            return false;
        }
        Map map = BeanMap.create(request);
        String sign = (String) map.get("signature");
        
       if (StringUtils.isBlank(sign)) {
           logger.warn("数据签名数据不存在，有可能被第三方篡改!!!");
           return false;
       }

        logger.info("signature is :" + sign);
        // 清掉返回数据对象里面的Sign数据（不能把这个数据也加进去进行签名），然后用签名算法进行签名
        map.put("signature", "");
        // 将API返回的数据根据用签名算法进行计算新的签名，用来跟API返回的签名进行比较
        String signFromRequest = Signature.getSign(map, key);

        if (!signFromRequest.equals(sign)) {
            // 签名验不过，表示这个API返回的数据有可能已经被篡改了
            logger.warn("数据签名验证不通过，有可能被第三方篡改!!!");
            return false;
        }
        logger.info("数据签名验证通过!");
        return true;
    }

}
