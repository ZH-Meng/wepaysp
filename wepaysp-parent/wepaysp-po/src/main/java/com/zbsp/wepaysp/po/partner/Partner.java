package com.zbsp.wepaysp.po.partner;

import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "partner_t")
public class Partner
    implements java.io.Serializable {

    private static final long serialVersionUID = -8385188525011820369L;
    private String iwoid;
    private Integer level;
    private Partner parentPartner;
    private String partnerId;
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
    private String logo;
    private String copyright;
    private String copyrightUrl;
    private String techSupportCompany;
    private String techSupportUrl;
    private String techSupportPerson;
    private String techSupportPhone;
    private String bankAccountName;
    private String bankAccountType;
    private String bankName;
    private String bankAddr;
    private String bankBranch;
    private String bankCard;
    private String creator;
    private Timestamp createTime;
    private String modifier;
    private Timestamp modifyTime;
    private String remark;

    public static enum State {
        /** 状态:未使用 */        nouse("1"),
        /** 状态:使用 */        using("2"),
        /** 状态:冻结 */        frozen("3");

        private String value;

        public String getValue() {
            return value;
        }

        private State(String value) {
            this.value = value;
        }
    }
    
    public Partner() {
    }

    @Id
    @Column(name = "IWOID", unique = true, nullable = false, length = 32)
    public String getIwoid() {
        return this.iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    @Column(name = "LEVEL", nullable = false)
    public Integer getLevel() {
        return this.level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_PARTNER_OID")
    public Partner getParentPartner() {
        return this.parentPartner;
    }

    public void setParentPartner(Partner parentPartner) {
        this.parentPartner = parentPartner;
    }
    
    @Column(name = "PARTNER_ID", length = 32)
    public String getPartnerId() {
        return partnerId;
    }
    
    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "CONTRACT_BEGIN", nullable = false, length = 0)
    public Date getContractBegin() {
        return this.contractBegin;
    }

    public void setContractBegin(Date contractBegin) {
        this.contractBegin = contractBegin;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "CONTRACT_END", nullable = false, length = 0)
    public Date getContractEnd() {
        return this.contractEnd;
    }

    public void setContractEnd(Date contractEnd) {
        this.contractEnd = contractEnd;
    }

    @Column(name = "FEE_RATE")
    public Integer getFeeRate() {
        return this.feeRate;
    }

    public void setFeeRate(Integer feeRate) {
        this.feeRate = feeRate;
    }

    @Column(name = "BALANCE", nullable = false)
    public Long getBalance() {
        return this.balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    @Column(name = "STATE", nullable = false, length = 1)
    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name = "CONTACTOR", nullable = false, length = 32)
    public String getContactor() {
        return this.contactor;
    }

    public void setContactor(String contactor) {
        this.contactor = contactor;
    }

    @Column(name = "MOBLIE_NUMBER", nullable = false, length = 32)
    public String getMoblieNumber() {
        return this.moblieNumber;
    }

    public void setMoblieNumber(String moblieNumber) {
        this.moblieNumber = moblieNumber;
    }

    @Column(name = "EMAIL", length = 100)
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "COMPANY", nullable = false, length = 256)
    public String getCompany() {
        return this.company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Column(name = "ADDRESS", nullable = false, length = 256)
    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "TELEPHONE", nullable = false, length = 32)
    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Column(name = "LOGO", length = 256)
    public String getLogo() {
        return this.logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Column(name = "COPYRIGHT", length = 256)
    public String getCopyright() {
        return this.copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    @Column(name = "COPYRIGHT_URL", length = 256)
    public String getCopyrightUrl() {
        return this.copyrightUrl;
    }

    public void setCopyrightUrl(String copyrightUrl) {
        this.copyrightUrl = copyrightUrl;
    }

    @Column(name = "TECH_SUPPORT_COMPANY", length = 256)
    public String getTechSupportCompany() {
        return this.techSupportCompany;
    }

    public void setTechSupportCompany(String techSupportCompany) {
        this.techSupportCompany = techSupportCompany;
    }

    @Column(name = "TECH_SUPPORT_URL", length = 256)
    public String getTechSupportUrl() {
        return this.techSupportUrl;
    }

    public void setTechSupportUrl(String techSupportUrl) {
        this.techSupportUrl = techSupportUrl;
    }

    @Column(name = "TECH_SUPPORT_PERSON", length = 32)
    public String getTechSupportPerson() {
        return this.techSupportPerson;
    }

    public void setTechSupportPerson(String techSupportPerson) {
        this.techSupportPerson = techSupportPerson;
    }

    @Column(name = "TECH_SUPPORT_PHONE", length = 32)
    public String getTechSupportPhone() {
        return this.techSupportPhone;
    }

    public void setTechSupportPhone(String techSupportPhone) {
        this.techSupportPhone = techSupportPhone;
    }

    @Column(name = "BANK_ACCOUNT_NAME", length = 32)
    public String getBankAccountName() {
        return this.bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    @Column(name = "BANK_ACCOUNT_TYPE", length = 1)
    public String getBankAccountType() {
        return this.bankAccountType;
    }

    public void setBankAccountType(String bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    @Column(name = "BANK_NAME", length = 32)
    public String getBankName() {
        return this.bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Column(name = "BANK_ADDR", length = 32)
    public String getBankAddr() {
        return this.bankAddr;
    }

    public void setBankAddr(String bankAddr) {
        this.bankAddr = bankAddr;
    }

    @Column(name = "BANK_BRANCH", length = 32)
    public String getBankBranch() {
        return this.bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    @Column(name = "BANK_CARD", length = 32)
    public String getBankCard() {
        return this.bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    @Column(name = "CREATOR", nullable = false, length = 32)
    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(name = "CREATE_TIME", nullable = false, length = 0)
    public Timestamp getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Column(name = "MODIFIER", length = 32)
    public String getModifier() {
        return this.modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Column(name = "MODIFY_TIME", length = 0)
    public Timestamp getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(Timestamp modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Column(name = "REMARK", length = 256)
    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Partner [iwoid=" + iwoid + ", level=" + level + ", parentPartner=" + parentPartner + ", contractBegin=" + contractBegin + ", contractEnd=" + contractEnd + ", feeRate=" + feeRate + ", balance=" + balance + ", state=" + state + ", contactor=" + contactor + ", moblieNumber=" + moblieNumber + ", email=" + email + ", company=" + company + ", address=" + address + ", telephone=" + telephone + "]";
    }

}
