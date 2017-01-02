package com.zbsp.wepaysp.mobile.model.userlogin.v1_0;

public class UserLoginRequest {
	
	private String version = "";
    private String requestId = "";
    private String deviceId = "";
    private String sessionid = "";
    private String signature = "";
	
	private String userId;
	private String passwd;
	
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	@Override
	public String toString() {
		return "UserLoginRequestBody [version=" + version + ", requestId=" + requestId + ", deviceId=" + deviceId
				+ ", sessionid=" + sessionid + ", signature=" + signature + ", userId=" + userId + ", passwd=" + passwd
				+ "]";
	}

}
