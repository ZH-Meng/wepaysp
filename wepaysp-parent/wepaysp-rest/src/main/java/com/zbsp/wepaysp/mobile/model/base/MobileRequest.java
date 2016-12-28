package com.zbsp.wepaysp.mobile.model.base;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.zbsp.wepaysp.common.security.AesHelper;
import com.zbsp.wepaysp.common.security.DigestHelper;
import com.zbsp.wepaysp.common.security.exception.AesEncryptException;
import com.zbsp.wepaysp.common.util.JSONUtil;

/**
 * 通讯请求对象
 */
public class MobileRequest implements Serializable {

    private static final long serialVersionUID = -3696329463248445308L;
    
    private final Logger logger = LogManager.getLogger();
    private static final String LOG_PREFIX = "[MobileRequest]-";

    private RequestHead head;

    private Object body;

    public RequestHead getHead() {
        return head;
    }

    public void setHead(RequestHead head) {
        this.head = head;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    /**
     * 构造响应头，生成签名
     * 
     * @return
     */
    public MobileRequest buildHead() {
        if (body == null) {
            throw new IllegalArgumentException("响应Body未设置");
        }

        if (head == null) {
            throw new IllegalArgumentException("响应头未设置");
        }

        head.setSignature(DigestHelper.md5Hex("prvnterminalapp" + JSONUtil.toJSONString(this, true)));

        return this;
    }

    /**
     * 构造响应体.
     * 
     * @param aesKey
     * @return
     */
    public MobileRequest buildBody(String aesKey, String deviceId) {
        if (body == null) {
            throw new IllegalArgumentException("响应Body未设置");
        }

        if (head == null) {
            throw new IllegalArgumentException("响应头未设置");
        }

        if (StringUtils.isBlank(head.getSignature())) {
            throw new IllegalArgumentException("响应头签名未设置");
        }

        try {
            body = AesHelper.encryptBase64(DigestHelper.md5Hex(aesKey + StringUtils.right(deviceId, 15)).substring(8, 24),
               JSONUtil.toJSONString(body, true), 0);
        } catch (AesEncryptException e) {
            logger.error(LOG_PREFIX + "[构造通讯请求体错误]", e);
        }

        return this;
    }
}
