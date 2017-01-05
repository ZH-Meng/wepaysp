package com.zbsp.wepaysp.mo.userlogin.v1_0;

import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mo.base.MobileRequest;

/**
 * 用户登陆请求
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
        return "UserLoginRequest [userId=" + userId + ", " + super.toString() + " ]";
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
