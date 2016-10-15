/*
 * SecurityCodeImageAction.java
 * 创建者：王金旭
 * 创建日期：2014年6月5日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.action.common;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

import com.zbsp.wepaysp.manage.web.action.BaseAction;
import com.zbsp.wepaysp.manage.web.util.SecurityCode;
import com.zbsp.wepaysp.manage.web.util.SecurityImage;

public class SecurityCodeImageAction extends BaseAction implements SessionAware {

    private static final long serialVersionUID = -5486320659442007613L;

    private Map<String, Object> session;
    private ByteArrayInputStream imageStream;

    private String timestamp;

    public String execute() {
        String securityCode = SecurityCode.getSecurityCode();
        imageStream = SecurityImage.getImageAsInputStream(securityCode);

        session.put("securityCode", securityCode);
        return SUCCESS;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public ByteArrayInputStream getImageStream() {
        return imageStream;
    }

    public void setImageStream(ByteArrayInputStream imageStream) {
        this.imageStream = imageStream;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
