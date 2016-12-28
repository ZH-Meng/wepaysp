package com.zbsp.wepaysp.mobile.model.base;

import java.io.Serializable;

/**
 * 通讯响应头对象
 */
public class ResponseHead implements Serializable {

    private static final long serialVersionUID = 3371625835010014835L;
    
    // 请求唯一码
    private String requestId;
    // 操作结果
    private int result;
    // 响应消息
    private String message;
    // 内容体签名
    private String signature = "null";
    
    public ResponseHead() {
    }

    public ResponseHead(String requestId, int result, String message) {
        this.requestId = requestId;
        this.result = result;
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
