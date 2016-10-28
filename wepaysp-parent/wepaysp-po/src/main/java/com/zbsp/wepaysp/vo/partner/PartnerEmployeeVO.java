package com.zbsp.wepaysp.vo.partner;

import java.io.Serializable;

/**
 * 服务商（代理商）员工VO
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
    private String loginId;
    
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
    
    public String getLoginId() {
        return loginId;
    }
    
    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
    
}
