package com.zbsp.wepaysp.vo.partner;

import java.io.Serializable;

/**
 * 商户员工（收银员）VO
 * 
 * @author 孟郑宏
 */
public class DealerEmployeeVO
    implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6954293319075245829L;
	private String iwoid;
    private String storeId;
    private String storeName;
    private String dealerEmployeeId;
    private String employeeName;
    private String moblieNumber;
    private String state;
    private String refundPassword;
    private String remark;
    private String loginId;
    private String loginPwd;
    private String storeOid;
    private String oldRefundPassword;
    private String dealerOid;
    
    public String getIwoid() {
        return iwoid;
    }
    
    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }
    
	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getDealerEmployeeId() {
		return dealerEmployeeId;
	}

	public void setDealerEmployeeId(String dealerEmployeeId) {
		this.dealerEmployeeId = dealerEmployeeId;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getMoblieNumber() {
		return moblieNumber;
	}

	public void setMoblieNumber(String moblieNumber) {
		this.moblieNumber = moblieNumber;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getRefundPassword() {
		return refundPassword;
	}

	public void setRefundPassword(String refundPassword) {
		this.refundPassword = refundPassword;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getLoginPwd() {
		return loginPwd;
	}

	public void setLoginPwd(String loginPwd) {
		this.loginPwd = loginPwd;
	}

	public String getStoreOid() {
		return storeOid;
	}

	public void setStoreOid(String storeOid) {
		this.storeOid = storeOid;
	}

	public String getOldRefundPassword() {
		return oldRefundPassword;
	}

	public void setOldRefundPassword(String oldRefundPassword) {
		this.oldRefundPassword = oldRefundPassword;
	}
    
    public String getDealerOid() {
        return dealerOid;
    }
    
    public void setDealerOid(String dealerOid) {
        this.dealerOid = dealerOid;
    }
	
}
