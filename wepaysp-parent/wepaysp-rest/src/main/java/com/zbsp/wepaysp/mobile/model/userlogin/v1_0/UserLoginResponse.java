package com.zbsp.wepaysp.mobile.model.userlogin.v1_0;

import com.zbsp.wepaysp.mobile.common.Signature;
import com.zbsp.wepaysp.mobile.model.base.MobileResponse;

/**
 * 用户登录响应
 */
public class UserLoginResponse extends MobileResponse {
	
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
	
	public UserLoginResponse() {}
	
    public UserLoginResponse(int result, String message) {
        super();
        this.result = result;
        this.message = message;
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

    @Override
    public String toString() {
        return "UserLoginResponse [dealerCompany=" + dealerCompany + ", storeName=" + storeName + ", dealerEmployeeName=" + dealerEmployeeName 
            + ", dealerEmployeeId=" + dealerEmployeeId + ", dealerEmployeeOid=" + dealerEmployeeOid + ", " + super.toString() + " ]";
    }
    
    @Override
    public UserLoginResponse build(String key) {
        try {
            setSignature(Signature.getSign(this, key));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

}
