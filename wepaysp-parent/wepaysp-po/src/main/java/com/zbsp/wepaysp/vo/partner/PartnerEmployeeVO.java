package com.zbsp.wepaysp.vo.partner;

import java.io.Serializable;

/**
 * 代理商员工（业务员）VO
 * 
 * @author 孟郑宏
 */
public class PartnerEmployeeVO
    implements Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 773568814552443180L;
    private String iwoid;
    private String partnerEmployeeId;
    private String employeeName;
    private String moblieNumber;
    private String state;
    private String remark;
    private String loginId;
    private String loginPwd;
    private Integer feeRate;
    private String partnerOid;
    
    public String getIwoid() {
        return iwoid;
    }
    
    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }
    
    public String getPartnerEmployeeId() {
        return partnerEmployeeId;
    }
    
    public void setPartnerEmployeeId(String partnerEmployeeId) {
        this.partnerEmployeeId = partnerEmployeeId;
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
	
    public Integer getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(Integer feeRate) {
        this.feeRate = feeRate;
    }

    public String getPartnerOid() {
        return partnerOid;
    }
    
    public void setPartnerOid(String partnerOid) {
        this.partnerOid = partnerOid;
    }
    
}
