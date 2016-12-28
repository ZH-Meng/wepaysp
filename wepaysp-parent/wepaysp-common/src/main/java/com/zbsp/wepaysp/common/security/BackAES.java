package com.zbsp.wepaysp.common.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class BackAES {

    /**
     * 加密解决算法
     */
    private static String ivParameter = "1234567890123456";// 默认偏移

    private static String WAYS = "AES";
    private static String MODE = "";
    private static boolean isPwd = false;
    private static String ModeCode = "PKCS5Padding";

    private static int pwdLenght = 16;
    private static String val = "0";

    public static String selectMod(int type) {
        // ECB("ECB", "0"), CBC("CBC", "1"), CFB("CFB", "2"), OFB("OFB", "3");
        switch (type) {
            case 0:
                isPwd = false;
                MODE = WAYS + "/" + AESType.ECB.key() + "/" + ModeCode;
                break;
            case 1:
                isPwd = true;
                MODE = WAYS + "/" + AESType.CBC.key() + "/" + ModeCode;
                break;
            case 2:
                isPwd = true;
                MODE = WAYS + "/" + AESType.CFB.key() + "/" + ModeCode;
                break;
            case 3:
                isPwd = true;
                MODE = WAYS + "/" + AESType.OFB.key() + "/" + ModeCode;
                break;
        }
        return MODE;
    }

    // 加密
    public static String encrypt(String sSrc, String sKey, int type)
        throws Exception {

        sKey = toMakekey(sKey, pwdLenght, val);
        Cipher cipher = Cipher.getInstance(selectMod(type));
        byte[] raw = sKey.getBytes("ASCII");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, WAYS);

        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度
        if (isPwd == false) {// ECB 不用密码
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        } else {
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        }

        byte[] encrypted = cipher.doFinal(sSrc.getBytes("UTF-8"));
        return Base64.encodeBase64String(encrypted);// 此处使用BASE64做转码。
    }

    // 解密
    public static String decrypt(String sSrc, String sKey, int type)
        throws Exception {

        sKey = toMakekey(sKey, pwdLenght, val);
        byte[] raw = sKey.getBytes("ASCII");
        SecretKeySpec skeySpec = new SecretKeySpec(raw, WAYS);
        Cipher cipher = Cipher.getInstance(selectMod(type));
        IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
        if (isPwd == false) {// ECB 不用密码
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        }
        byte[] encrypted1 = Base64.decodeBase64(sSrc.getBytes());// 先用base64解密
        byte[] original = cipher.doFinal(encrypted1);
        String originalString = new String(original, "UTF-8");
        return originalString;
    }

    // key
    public static String toMakekey(String str, int strLength, String val) {
        int strLen = str.length();
        if (strLen < strLength) {
            while (strLen < strLength) {
                StringBuffer buffer = new StringBuffer();
                buffer.append(str).append(val);
                str = buffer.toString();
                strLen = str.length();
            }
        }
        return str;
    }
}
