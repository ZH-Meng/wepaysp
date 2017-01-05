package com.zbsp.wepaysp.mo.base;

import com.zbsp.wepaysp.common.security.Signature;

/**
 * 通讯响应对象
 */
public class MobileResponse {

    /** 响应唯一码 */
    protected String responseId = "";
    
    /** 操作结果 */
    protected int result;
    
    /** 响应消息 */
    protected String message = "";
    
    /** 签名 */
    protected String signature = "";
    
    public MobileResponse() {}
    
    public MobileResponse(int result, String message, String responseId) {
        super();
        this.responseId = responseId;
        this.result = result;
        this.message = message;
    }

    public String getResponseId() {
		return responseId;
	}

	public void setResponseId(String responseId) {
		this.responseId = responseId;
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
        return "MobileResponse [responseId=" + responseId + ", result=" + result + ", message=" + message + ", signature=" + signature + "]";
    }
    
    public MobileResponse build(String key) {
        try {
            setSignature(Signature.getSign(this, key));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

}
