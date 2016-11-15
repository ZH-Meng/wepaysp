package com.zbsp.wepaysp.common.security;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.common.util.Generator;

/**
 * 微信支付签名工具类，同wxpay-sdk中 Signature类区别，对URL是否进行Encoder处理？
 * 
 * @author 孟郑宏
 */
public class SignHelper {
    
    public static void main(String[] args) {
        Map<String, String> testMap = new HashMap<String, String>();
        testMap.put("mch_id", Generator.generateIwoid());
        testMap.put("nonce_str", Generator.generateRandomNumber(32));
        testMap.put("sub_appid", "");
        testMap.put("total_fee", 200 + "");
        testMap.put("notify_url", "http://www.wepaysp.com/pay/appidpay!notify.action");
        testMap.put("sign", Generator.generateIwoid());
        System.out.println(sign4WxPay(testMap, Generator.generateIwoid()));
    }
    
    /**
     * 微信支付签名 <br>
     * 规则：<br>
     * 过滤待签名数据，sign和空值不参加签名<br>
     * 先按照参数名ASCII码从小到大排序（字典序）<br>
     * 用&符号拼接成字符串，其中 URLEncoder <br>
     * 最后拼接上API秘钥，str&key=密钥 <br>
     * md5运算，全部转换为大写
     * 
     * @param signMap
     * @return 微信支付签名
     */
    public static String sign4WxPay(Map<String, String> signMap, String APIKey) {
        ArrayList<String> keyList = new ArrayList<String>(signMap.keySet());
        Collections.sort(keyList);
        StringBuffer sb = new StringBuffer();
        for (String key : keyList) {
            if (!"sign".equals(key) && StringUtils.isNotBlank(signMap.get(key))) {
                sb.append(key);
                sb.append("=");
                if ("notify_url".equals(key)) {
                    try {
                        sb.append(URLEncoder.encode(signMap.get(key), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException("url encoder fail." + e.getMessage());
                    }
                } else {
                    sb.append(signMap.get(key));
                }
                sb.append("&");
            }
        }
        sb.append(sb.length() > 0 ? "" : "&");
        sb.append("key=" + APIKey);
        // System.out.println("Sign no MD5 String：" + sb.toString());
        return  DigestHelper.md5Hex(sb.toString()).toUpperCase();
    }
    
}
