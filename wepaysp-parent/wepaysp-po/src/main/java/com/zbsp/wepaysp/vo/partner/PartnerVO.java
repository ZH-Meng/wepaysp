package com.zbsp.wepaysp.vo.partner;

import java.io.Serializable;
import java.util.Date;

public class PartnerVO
    implements Serializable {

    private static final long serialVersionUID = -2467954569564498116L;

    private String iwoid;
    private Integer level;
    private String parentPartnerOid;
    private Date contractBegin;
    private Date contractEnd;
    private Integer feeRate;
    private Long balance;
    private String state;
    private String contactor;
    private String moblieNumber;
    private String email;
    private String company;
    private String address;
    private String telephone;
    private String copyright;
    private String copyrightUrl;
    private String techSupportCompany;
    private String techSupportUrl;
    private String techSupportPerson;
    private String techSupportPhone;
    private String remark;

    private String loginId;
    private String loginPwd;
    private String parentCompany;
    private String partneId;

    public String getIwoid() {
        return iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getParentPartnerOid() {
        return parentPartnerOid;
    }

    public void setParentPartnerOid(String parentPartnerOid) {
        this.parentPartnerOid = parentPartnerOid;
    }

    public Date getContractBegin() {
        return contractBegin;
    }

    public void setContractBegin(Date contractBegin) {
        this.contractBegin = contractBegin;
    }

    public Date getContractEnd() {
        return contractEnd;
    }

    public void setContractEnd(Date contractEnd) {
        this.contractEnd = contractEnd;
    }

    public Integer getFeeRate() {
        return feeRate;
    }

    public void setFeeRate(Integer feeRate) {
        this.feeRate = feeRate;
    }

    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getContactor() {
        return contactor;
    }

    public void setContactor(String contactor) {
        this.contactor = contactor;
    }

    public String getMoblieNumber() {
        return moblieNumber;
    }

    public void setMoblieNumber(String moblieNumber) {
        this.moblieNumber = moblieNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getCopyrightUrl() {
        return copyrightUrl;
    }

    public void setCopyrightUrl(String copyrightUrl) {
        this.copyrightUrl = copyrightUrl;
    }

    public String getTechSupportCompany() {
        return techSupportCompany;
    }

    public void setTechSupportCompany(String techSupportCompany) {
        this.techSupportCompany = techSupportCompany;
    }

    public String getTechSupportUrl() {
        return techSupportUrl;
    }

    public void setTechSupportUrl(String techSupportUrl) {
        this.techSupportUrl = techSupportUrl;
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

	public String getParentCompany() {
		return parentCompany;
	}

	public void setParentCompany(String parentCompany) {
		this.parentCompany = parentCompany;
	}

    public String getPartneId() {
        return partneId;
    }
    
    public void setPartneId(String partneId) {
        this.partneId = partneId;
    }

}
