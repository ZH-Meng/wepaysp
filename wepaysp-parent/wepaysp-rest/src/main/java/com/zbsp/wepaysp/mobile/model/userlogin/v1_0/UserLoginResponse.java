package com.zbsp.wepaysp.mobile.model.userlogin.v1_0;

public class UserLoginResponse {

    /** 请求唯一码 */
    private String requestId = "";
    /** 操作结果 */
    private int result;
    /** 响应消息 */
    private String message = "";
    /** 内容体签名 */
    private String signature = "";
	
	/** 商户公司名称 */
	private String dealerCompany = "";
	/** 门店名称 */
	private String storeName = "";
	/** 收银员姓名 */
	private String dealerEmployeeName = "";
	/** 收银员ID */
	private String dealerEmployeeId = "";
	/** 收银员Oid */
	private String dealerEmployeeOid = "";
	/** 会话id */
	private String sessionId = "";
	
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

	public String getDealerCompany() {
		return dealerCompany;
	}

	public void setDealerCompany(String dealerCompany) {
		this.dealerCompany = dealerCompany;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getDealerEmployeeName() {
		return dealerEmployeeName;
	}

	public void setDealerEmployeeName(String dealerEmployeeName) {
		this.dealerEmployeeName = dealerEmployeeName;
	}

	public String getDealerEmployeeId() {
		return dealerEmployeeId;
	}

	public void setDealerEmployeeId(String dealerEmployeeId) {
		this.dealerEmployeeId = dealerEmployeeId;
	}

	public String getDealerEmployeeOid() {
		return dealerEmployeeOid;
	}

	public void setDealerEmployeeOid(String dealerEmployeeOid) {
		this.dealerEmployeeOid = dealerEmployeeOid;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	@Override
	public String toString() {
		return "UserLoginResponseBody [requestId=" + requestId + ", result=" + result + ", message=" + message
				+ ", signature=" + signature + ", dealerCompany=" + dealerCompany + ", storeName=" + storeName
				+ ", dealerEmployeeName=" + dealerEmployeeName + ", dealerEmployeeId=" + dealerEmployeeId
				+ ", dealerEmployeeOid=" + dealerEmployeeOid + ", sessionId=" + sessionId + "]";
	}

}
