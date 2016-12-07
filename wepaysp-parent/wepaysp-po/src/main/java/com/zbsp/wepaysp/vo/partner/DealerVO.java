package com.zbsp.wepaysp.vo.partner;

import java.io.Serializable;

/**
 * 商户VO
 * 
 * @author 孟郑宏
 */
public class DealerVO
    implements Serializable {

    private static final long serialVersionUID = 2712799168814035691L;

    private String iwoid;
    private String partnerOid;
    private String partnerCompany;
    private String dealerId;
    private String contactor;
    private String company;
    private String address;
    private String telephone;
    private String moblieNumber;
    private String qqNumber;
    private String email;
    private String state;
    private String techSupportPerson;
    private String techSupportPhone;
    private String subAppid;
    private String subMchId;
    private String remark;

    private String loginId;
    private String loginPwd;
    private String coreDataFlag;// on;off 顶级服务商可以编辑商户核心数据，其他用户不可以
    private String partnerEmployeeOid;
    private String partnerEmployeeName;

    private String qrCodePath;
    
    public String getIwoid() {
        return iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    public String getPartnerOid() {
        return partnerOid;
    }

    public void setPartnerOid(String partnerOid) {
        this.partnerOid = partnerOid;
    }

    public String getPartnerCompany() {
        return partnerCompany;
    }

    public void setPartnerCompany(String partnerCompany) {
        this.partnerCompany = partnerCompany;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getContactor() {
        return contactor;
    }

    public void setContactor(String contactor) {
        this.contactor = contactor;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMoblieNumber() {
        return moblieNumber;
    }

    public void setMoblieNumber(String moblieNumber) {
        this.moblieNumber = moblieNumber;
    }

    public String getQqNumber() {
        return qqNumber;
    }

    public void setQqNumber(String qqNumber) {
        this.qqNumber = qqNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTechSupportPerson() {
        return techSupportPerson;
    }

    public void setTechSupportPerson(String techSupportPerson) {
        this.techSupportPerson = techSupportPerson;
    }

    public String getTechSupportPhone() {
        return techSupportPhone;
    }

    public void setTechSupportPhone(String techSupportPhone) {
        this.techSupportPhone = techSupportPhone;
    }

    public String getSubAppid() {
        return subAppid;
    }

    public void setSubAppid(String subAppid) {
        this.subAppid = subAppid;
    }

    public String getSubMchId() {
        return subMchId;
    }

    public void setSubMchId(String subMchId) {
        this.subMchId = subMchId;
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

    public String getCoreDataFlag() {
        return coreDataFlag;
    }

    public void setCoreDataFlag(String coreDataFlag) {
        this.coreDataFlag = coreDataFlag;
    }

    public String getPartnerEmployeeOid() {
        return partnerEmployeeOid;
    }

    public void setPartnerEmployeeOid(String partnerEmployeeOid) {
        this.partnerEmployeeOid = partnerEmployeeOid;
    }

    public String getPartnerEmployeeName() {
        return partnerEmployeeName;
    }

    public void setPartnerEmployeeName(String partnerEmployeeName) {
        this.partnerEmployeeName = partnerEmployeeName;
    }

    public String getQrCodePath() {
        return qrCodePath;
    }
    
    public void setQrCodePath(String qrCodePath) {
        this.qrCodePath = qrCodePath;
    }

}
