package com.zbsp.wepaysp.mobile.model.base;

/**
 * 通讯响应对象
 */
public abstract class MobileResponse {

    /** 请求唯一码 */
    protected String requestId = "";
    
    /** 操作结果 */
    protected int result;
    
    /** 响应消息 */
    protected String message = "";
    
    /** 签名 */
    protected String signature = "";

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

    @Override
    public String toString() {
        return "MobileResponse [requestId=" + requestId + ", result=" + result + ", message=" + message + ", signature=" + signature + "]";
    }
    
    protected abstract MobileResponse build(String key);

}
