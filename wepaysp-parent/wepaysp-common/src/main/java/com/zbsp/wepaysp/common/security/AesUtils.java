package com.zbsp.wepaysp.common.security;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.zbsp.wepaysp.common.security.exception.AesDecryptException;
import com.zbsp.wepaysp.common.security.exception.AesEncryptException;
import com.zbsp.wepaysp.common.security.exception.AesKeyException;

public final class AesUtils {

    // 密钥算法-AES
    public static final String KEY_ALGORITHM = "AES";

    // 密码信息-算法:AES、模式:ECB、填充方式:PKCS5Padding
    public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    // 密钥长度-(java默认只能处理128位以内的长度,如果需要处理大于128位可以使用JCE解除密钥长度限制)
    public static final int KEY_SIZE = 128;

    private AesUtils() {
    }

    /**
     * 生成密钥
     */
    public static byte[] initKey() throws AesKeyException {
        try {
            // 实例化
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);
            // 初始化密钥长度
            keyGenerator.init(KEY_SIZE);
            // 生成秘密密钥
            SecretKey secretKey = keyGenerator.generateKey();
            // 获得密钥的二进制编码形式
            return secretKey.getEncoded();
        } catch (Exception e) {
            throw new AesKeyException(e.getMessage(), e);
        }
    }

    /**
     * 加密
     */
    public static byte[] encrypt(byte[] key, byte[] data) throws AesEncryptException {
        try {
            // 还原密钥
            SecretKey secretKey = toKey(key);
            // 实例化
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            // 初始化,设置为加密模式
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // 执行操作
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new AesEncryptException(e.getMessage(), e);
        }
    }

    /**
     * 解密
     */
    public static byte[] decrypt(byte[] key, byte[] data) throws AesDecryptException {
        try {
            // 还原密钥
            SecretKey secretKey = toKey(key);
            // 实例化
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            // 初始化,设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            // 执行操作
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new AesDecryptException(e.getMessage(), e);
        }
    }

    /**
     * 转换密钥
     */
    private static SecretKey toKey(byte[] key) {
        // 实例化AES密钥
        return new SecretKeySpec(key, KEY_ALGORITHM);
    }
    
}
