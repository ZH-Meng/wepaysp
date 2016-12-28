package com.zbsp.wepaysp.common.security;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.binary.StringUtils;

import com.zbsp.wepaysp.common.security.exception.AesDecryptException;
import com.zbsp.wepaysp.common.security.exception.AesEncryptException;
import com.zbsp.wepaysp.common.security.exception.AesKeyException;


public final class AesHelper {

    private AesHelper() {
    }

    /**
     * 生成密钥
     */
    public static String initKeyHex() throws AesKeyException {

        return Hex.encodeHexString(AesUtils.initKey());
    }

    /**
     * 加密
     */
    public static String encryptBase64ByKeyHex(final String key, final String data) throws AesEncryptException {
        try {
            byte[] keyBytes = Hex.decodeHex(key.toCharArray());
            byte[] dataBytes = StringUtils.getBytesUtf8(data);
            return Base64.encodeBase64URLSafeString(AesUtils.encrypt(keyBytes, dataBytes));
        } catch (DecoderException e) {
            throw new AesEncryptException(e.getMessage(), e);
        }
    }

    /**
     * 解密
     */
    public static String decryptBase64ByKeyHex(final String key, final String data) throws AesDecryptException {
        try {
            byte[] keyBytes = Hex.decodeHex(key.toCharArray());
            byte[] dataBytes = Base64.decodeBase64(data);
            return StringUtils.newStringUtf8(AesUtils.decrypt(keyBytes, dataBytes));
        } catch (DecoderException e) {
            throw new AesDecryptException(e.getMessage(), e);
        }
    }

    /**
     * 生成密钥
     */
    public static String initKeyBase64() throws AesKeyException {
        return Base64.encodeBase64URLSafeString(AesUtils.initKey());
    }

    /**
     * 加密
     */
    public static String encryptBase64ByKeyBase64(final String key, final String data) throws AesEncryptException {
        byte[] keyBytes = Base64.decodeBase64(StringUtils.getBytesUtf8(key));
        byte[] dataBytes = StringUtils.getBytesUtf8(data);
        return Base64.encodeBase64URLSafeString(AesUtils.encrypt(keyBytes, dataBytes));
    }

    /**
     * 解密
     */
    public static String decryptBase64ByKeyBase64(final String key, final String data) throws AesDecryptException {
        byte[] keyBytes = Base64.decodeBase64(StringUtils.getBytesUtf8(key));
        byte[] dataBytes = Base64.decodeBase64(data);
        return StringUtils.newStringUtf8(AesUtils.decrypt(keyBytes, dataBytes));
    }

    /**
     * 加密
     */
    public static String encryptBase64(final String key, final String data, int type) throws AesEncryptException {
        try {
            return BackAES.encrypt(data, key, type);
        } catch (Exception e) {
            throw new AesEncryptException(e.getMessage(), e);
        }
    }

    /**
     * 解密
     */
    public static String decryptBase64(final String key, final String data, int type)
        throws AesDecryptException {

        try {
            return BackAES.decrypt(data, key, type);
        } catch (Exception e) {
            throw new AesDecryptException(e.getMessage(), e);
        }
    }
}
