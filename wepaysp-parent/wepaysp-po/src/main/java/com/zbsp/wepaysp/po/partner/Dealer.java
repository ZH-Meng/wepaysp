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
    private Integer partnerLevel;
    private String partner1Oid;
    private String partner2Oid;
    private String partner3Oid;
    private PartnerEmployee partnerEmployee;
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
    private String subAppid;
    private String subMchId;
    private String qrCodePath;
    private String alipayUserId;
    private String creator;
    private Timestamp createTime;
    private String modifier;
    private Timestamp modifyTime;
    private String remark;
    
    private String alipayAuthCodePath;

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

    @Column(name = "PARTNER_LEVEL")
    public Integer getPartnerLevel() {
        return this.partnerLevel;
    }

    public void setPartnerLevel(Integer partnerLevel) {
        this.partnerLevel = partnerLevel;
    }
    
    @Column(name = "PARTNER1_OID", length = 32)
    public String getPartner1Oid() {
        return this.partner1Oid;
    }

    public void setPartner1Oid(String partner1Oid) {
        this.partner1Oid = partner1Oid;
    }
    
    @Column(name = "PARTNER2_OID", length = 32)
    public String getPartner2Oid() {
        return this.partner2Oid;
    }

    public void setPartner2Oid(String partner2Oid) {
        this.partner2Oid = partner2Oid;
    }

    @Column(name = "PARTNER3_OID", length = 32)
    public String getPartner3Oid() {
        return this.partner3Oid;
    }

    public void setPartner3Oid(String partner3Oid) {
        this.partner3Oid = partner3Oid;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTNER_EMPLOYEE_OID")
    public PartnerEmployee getPartnerEmployee() {
        return this.partnerEmployee;
    }

    public void setPartnerEmployee(PartnerEmployee partnerEmployee) {
        this.partnerEmployee = partnerEmployee;
    }
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_oid")
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


    @Column(name = "SUB_APPID", length = 32)
    public String getSubAppid() {
        return this.subAppid;
    }

    public void setSubAppid(String subAppid) {
        this.subAppid = subAppid;
    }

    @Column(name = "SUB_MCH_ID", length = 32)
    public String getSubMchId() {
        return this.subMchId;
    }

    public void setSubMchId(String subMchId) {
        this.subMchId = subMchId;
    }
    
    @Column(name = "qr_code_path", length = 256)
    public String getQrCodePath() {
        return qrCodePath;
    }
    
    public void setQrCodePath(String qrCodePath) {
        this.qrCodePath = qrCodePath;
    }
    
    @Column(name = "alipay_user_id", length = 16)
    public String getAlipayUserId() {
        return this.alipayUserId;
    }

    public void setAlipayUserId(String alipayUserId) {
        this.alipayUserId = alipayUserId;
    }
    
    @Column(name = "alipay_auth_code_path", length = 256)
    public String getAlipayAuthCodePath() {
        return alipayAuthCodePath;
    }
    
    public void setAlipayAuthCodePath(String alipayAuthCodePath) {
        this.alipayAuthCodePath = alipayAuthCodePath;
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
        StringBuilder builder = new StringBuilder();
        builder.append("Dealer [");
        if (iwoid != null)
            builder.append("iwoid=").append(iwoid).append(", ");
        if (partner != null)
            builder.append("partnerOid=").append(partner.getIwoid()).append(", ");
        if (dealerId != null)
            builder.append("dealerId=").append(dealerId).append(", ");
        if (partnerLevel != null)
            builder.append("partnerLevel=").append(partnerLevel).append(", ");
        if (partner1Oid != null)
            builder.append("partner1Oid=").append(partner1Oid).append(", ");
        if (partner2Oid != null)
            builder.append("partner2Oid=").append(partner2Oid).append(", ");
        if (partner3Oid != null)
            builder.append("partner3Oid=").append(partner3Oid).append(", ");
        if (partnerEmployee != null)
            builder.append("partnerEmployeeOid=").append(partnerEmployee.getIwoid()).append(", ");
        if (merchant != null)
            builder.append("merchantOid=").append(merchant.getIwoid()).append(", ");
        if (contactor != null)
            builder.append("contactor=").append(contactor).append(", ");
        if (company != null)
            builder.append("company=").append(company).append(", ");
        if (address != null)
            builder.append("address=").append(address).append(", ");
        if (telephone != null)
            builder.append("telephone=").append(telephone).append(", ");
        if (moblieNumber != null)
            builder.append("moblieNumber=").append(moblieNumber).append(", ");
        if (qqNumber != null)
            builder.append("qqNumber=").append(qqNumber).append(", ");
        if (email != null)
            builder.append("email=").append(email).append(", ");
        if (state != null)
            builder.append("state=").append(state).append(", ");
        if (techSupportPerson != null)
            builder.append("techSupportPerson=").append(techSupportPerson).append(", ");
        if (techSupportPhone != null)
            builder.append("techSupportPhone=").append(techSupportPhone).append(", ");
        if (subAppid != null)
            builder.append("subAppid=").append(subAppid).append(", ");
        if (subMchId != null)
            builder.append("subMchId=").append(subMchId).append(", ");
        if (qrCodePath != null)
            builder.append("qrCodePath=").append(qrCodePath).append(", ");
        if (alipayUserId != null)
            builder.append("alipayUserId=").append(alipayUserId).append(", ");
        if (alipayAuthCodePath != null)
            builder.append("alipayAuthCodePath=").append(alipayAuthCodePath).append(", ");
        if (creator != null)
            builder.append("creator=").append(creator).append(", ");
        if (createTime != null)
            builder.append("createTime=").append(createTime).append(", ");
        if (modifier != null)
            builder.append("modifier=").append(modifier).append(", ");
        if (modifyTime != null)
            builder.append("modifyTime=").append(modifyTime).append(", ");
        if (remark != null)
            builder.append("remark=").append(remark);
        builder.append("]");
        return builder.toString();
    }

}
