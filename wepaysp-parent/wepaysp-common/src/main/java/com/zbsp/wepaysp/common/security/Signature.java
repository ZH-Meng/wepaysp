package com.zbsp.wepaysp.common.security;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zbsp.wepaysp.common.http.common.HttpConfig;
import com.zbsp.wepaysp.common.http.common.HttpConfig.ParamType;
import com.zbsp.wepaysp.common.http.exception.HttpProcessException;
import com.zbsp.wepaysp.common.http.httpclient.HttpClientUtil;
import com.zbsp.wepaysp.common.security.DigestHelper;
import com.zbsp.wepaysp.common.util.JSONUtil;

import net.sf.cglib.beans.BeanMap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
            if (f.get(o) != null && !"".equals(f.get(o)) && !"signature".equals(f.getName())) {
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
            if (entry.getValue() != null && !"".equals(entry.getValue()) && !"signature".equals(entry.getKey())) {
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
        Map map = null;
        if (request instanceof Map) {
        	map = (Map) request;
        } else {
        	map = BeanMap.create(request);
        }
        String sign = (String) map.get("signature");
        
       if (StringUtils.isBlank(sign)) {
           logger.warn("数据签名数据不存在，有可能被第三方篡改!!!");
           return false;
       }

        logger.info("signature is :" + sign);
        
        // 重新计算签名，比较
        String signFromRequest = Signature.getSign(map, key);

        if (!signFromRequest.equals(sign)) {
            // 签名验不过，表示这个API返回的数据有可能已经被篡改了
            logger.warn("数据签名验证不通过，有可能被第三方篡改!!!");
            return false;
        }
        logger.info("数据签名验证通过!");
        return true;
    }
    
    public static void main(String[] args) {
    	// 密钥
    	String key = "e6b835a2d55942eb8623a73d206c305c";
    	
    	// 此处用Map转JSON串做请求参数调试，建议创建使用请求响应对象转JSON串
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("requestId", "100c8a2102cc4ec69061483932575494");
		map.put("userId", "1701100001");
		map.put("passwd", "111111");
		map.put("appType", 3);
		map.put("signature", getSign(map, key));
		
		System.out.println("request :" + JSONUtil.toJSONString(map, true));
		
		HttpConfig httpConfig = HttpConfig.custom(ParamType.String);
        Header[] headers = new Header [] {new BasicHeader("Content-Type", "application/json")};
        String apiUrl = "http://123.207.188.155:8080/user/v1/login";
        String result = null;
		try {
			result = HttpClientUtil.post(httpConfig.url(apiUrl).headers(headers).stringParam(JSONUtil.toJSONString(map, true)));
		} catch (HttpProcessException e) {
			e.printStackTrace();
		}
		
		// 响应
		System.out.println("response :" + result);
		/*map.clear();
		map.put("responseId", "9698b30cb94b4f3194ea34b6361dc86c");
		map.put("result", 0);
		map.put("message", "登陆成功");
		map.put("dealerCompany", "果香四溢水果超市");
		map.put("storeName", "禾声福酒家-广渠门店");
		map.put("dealerEmployeeName", "dealer1_员工3");
		map.put("dealerEmployeeId", "1612270002");
		map.put("dealerEmployeeOid", "9c5d9f7681f548e8a0b8d9619c999449");
		map.put("signature", "2C30947AB30A46EA5E137086B597800A");
		System.out.println(getSign(map, key));
		System.out.println("response :" + JSONUtil.toJSONString(map, true));
		*/
		
		// 验签
		System.out.println(checkIsSignValidFromRequest(map, key));
	}

}
