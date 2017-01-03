package com.zbsp.wepaysp.mobile.model.base;

/**
 * 通讯请求对象
 */
public abstract class MobileRequest {
    
    /** 请求唯一码 */
    protected String requestId = "";
    /** 客户端类型 */
    protected int appType;
    /** 签名 */
    protected String signature = "";

    public static enum AppType {
        /** 客户端类型：IOS */
        ios(0),
        /** 客户端类型：Android */
        android(1),
        /** 客户端类型：PC */
        pc(2);

        private int value;

        public int getValue() {
            return value;
        }

        private AppType(int value) {
            this.value = value;
        }
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public int getAppType() {
        return appType;
    }

    public void setAppType(int appType) {
        this.appType = appType;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "MobileRequest [requestId=" + requestId + ", signature=" + signature + ", appType=" + appType + "]";
    }
    
    protected abstract MobileRequest build(String key);

}
