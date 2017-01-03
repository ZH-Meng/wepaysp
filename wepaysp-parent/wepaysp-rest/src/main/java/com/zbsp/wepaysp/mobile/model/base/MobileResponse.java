package com.zbsp.wepaysp.mobile.model.base;

/**
 * 通讯响应对象
 */
public abstract class MobileResponse {

    /** 响应唯一码 */
    protected String responseId = "";
    
    /** 操作结果 */
    protected int result;
    
    /** 响应消息 */
    protected String message = "";
    
    /** 签名 */
    protected String signature = "";

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
    
    protected abstract MobileResponse build(String key);

}
