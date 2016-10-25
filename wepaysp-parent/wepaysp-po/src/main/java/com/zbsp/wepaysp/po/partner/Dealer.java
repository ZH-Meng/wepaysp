package com.zbsp.wepaysp.po.partner;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "dealer_t")
public class Dealer
    implements java.io.Serializable {
    
    private static final long serialVersionUID = 8142967504803140023L;
    private String iwoid;
    private Partner partner;
    private String dealerId;
    private Partner partner2;
    private Partner partner3;
    private Merchant merchant;
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
    private Integer feeRate;
    private String subAppid;
    private String subMchId;
    private String creator;
    private Timestamp createTime;
    private String modifier;
    private Timestamp modifyTime;
    private String remark;

    public Dealer() {
    }

    @Id
    @Column(name = "IWOID", unique = true, nullable = false, length = 32)
    public String getIwoid() {
        return this.iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTNER_OID")
    public Partner getPartner() {
        return this.partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    @Column(name = "DEALER_ID", length = 32)
    public String getDealerId() {
        return this.dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTNER2_OID")
    public Partner getPartner2() {
        return this.partner2;
    }

    public void setPartner2(Partner partner2) {
        this.partner2 = partner2;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTNER3_OID")
    public Partner getPartner3() {
        return this.partner3;
    }

    public void setPartner3(Partner partner3) {
        this.partner3 = partner3;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEALER_OID")
    public Merchant getMerchant() {
        return this.merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }

    @Column(name = "CONTACTOR", nullable = false, length = 64)
    public String getContactor() {
        return this.contactor;
    }

    public void setContactor(String contactor) {
        this.contactor = contactor;
    }

    @Column(name = "COMPANY", nullable = false, length = 256)
    public String getCompany() {
        return this.company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Column(name = "ADDRESS", length = 256)
    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Column(name = "TELEPHONE", length = 32)
    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Column(name = "MOBLIE_NUMBER", length = 32)
    public String getMoblieNumber() {
        return this.moblieNumber;
    }

    public void setMoblieNumber(String moblieNumber) {
        this.moblieNumber = moblieNumber;
    }

    @Column(name = "QQ_NUMBER", length = 16)
    public String getQqNumber() {
        return this.qqNumber;
    }

    public void setQqNumber(String qqNumber) {
        this.qqNumber = qqNumber;
    }

    @Column(name = "EMAIL", length = 100)
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "STATE", nullable = false, length = 1)
    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
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

    @Column(name = "FEE_RATE")
    public Integer getFeeRate() {
        return this.feeRate;
    }

    public void setFeeRate(Integer feeRate) {
        this.feeRate = feeRate;
    }

    @Column(name = "SUB_APPID", length = 32)
    public String getSubAppid() {
        return this.subAppid;
    }

    public void setSubAppid(String subAppid) {
        this.subAppid = subAppid;
    }

    @Column(name = "SUB_MCH_ID", nullable = false, length = 32)
    public String getSubMchId() {
        return this.subMchId;
    }

    public void setSubMchId(String subMchId) {
        this.subMchId = subMchId;
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

}
