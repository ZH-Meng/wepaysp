package com.zbsp.wepaysp.mobile.model.userlogin.v1_0;

import com.zbsp.wepaysp.mobile.common.Signature;
import com.zbsp.wepaysp.mobile.model.base.MobileRequest;

/**
 * 用户登录请求
 */
public class UserLoginRequest extends MobileRequest {
	
	private String userId;
	private String passwd;

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
        return "UserLoginRequest [userId=" + userId + ", passwd=" + passwd + ", " + super.toString() + " ]";
    }

    @Override
    public UserLoginRequest build(String key) {
	    try {
            setSignature(Signature.getSign(this, key));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
	}

}
