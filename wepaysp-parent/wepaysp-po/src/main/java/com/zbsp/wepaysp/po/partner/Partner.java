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
    
    private String appId;
    private String mchId;
    private String partnerKey;
    private String keyPath;
    private String keyPassword;
    private String appSecret;
    private String isvPartnerId;
    
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
    
    /**
     * 
     *  服务商级别 
     */
    public static enum Level {
        /** 1顶级服务商 */     LEVEL_TOP(1),
        /** 2级服务商 */        LEVEL_2(2),
        /** 3三级服务商*/      LEVEL_3(3);

        private int value;

        public int getValue() {
            return value;
        }

        private Level(int value) {
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
    
    @Column(name = "APP_ID", length = 32)
    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name = "MCH_ID", length = 32)
    public String getMchId() {
        return this.mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    @Column(name = "PARTNER_KEY", length = 32)
    public String getPartnerKey() {
        return this.partnerKey;
    }

    public void setPartnerKey(String partnerKey) {
        this.partnerKey = partnerKey;
    }

    @Column(name = "KEY_PATH", length = 128)
    public String getKeyPath() {
        return this.keyPath;
    }

    public void setKeyPath(String keyPath) {
        this.keyPath = keyPath;
    }

    @Column(name = "KEY_PASSWORD", length = 32)
    public String getKeyPassword() {
        return this.keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    @Column(name = "APP_SECRET", length = 32)
    public String getAppSecret() {
        return this.appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
    
    @Column(name = "isv_partner_id", length = 16)
    public String getIsvPartnerId() {
        return this.isvPartnerId;
    }

    public void setIsvPartnerId(String isvPartnerId) {
        this.isvPartnerId = isvPartnerId;
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
        builder.append("Partner [");
        if (iwoid != null)
            builder.append("iwoid=").append(iwoid).append(", ");
        if (level != null)
            builder.append("level=").append(level).append(", ");
        if (parentPartner != null)
            builder.append("parentPartnerOid=").append(parentPartner.getIwoid()).append(", ");
        if (partnerId != null)
            builder.append("partnerId=").append(partnerId).append(", ");
        if (contractBegin != null)
            builder.append("contractBegin=").append(contractBegin).append(", ");
        if (contractEnd != null)
            builder.append("contractEnd=").append(contractEnd).append(", ");
        if (feeRate != null)
            builder.append("feeRate=").append(feeRate).append(", ");
        if (balance != null)
            builder.append("balance=").append(balance).append(", ");
        if (state != null)
            builder.append("state=").append(state).append(", ");
        if (contactor != null)
            builder.append("contactor=").append(contactor).append(", ");
        if (moblieNumber != null)
            builder.append("moblieNumber=").append(moblieNumber).append(", ");
        if (email != null)
            builder.append("email=").append(email).append(", ");
        if (company != null)
            builder.append("company=").append(company).append(", ");
        if (address != null)
            builder.append("address=").append(address).append(", ");
        if (telephone != null)
            builder.append("telephone=").append(telephone).append(", ");
        if (logo != null)
            builder.append("logo=").append(logo).append(", ");
        if (copyright != null)
            builder.append("copyright=").append(copyright).append(", ");
        if (copyrightUrl != null)
            builder.append("copyrightUrl=").append(copyrightUrl).append(", ");
        if (techSupportCompany != null)
            builder.append("techSupportCompany=").append(techSupportCompany).append(", ");
        if (techSupportUrl != null)
            builder.append("techSupportUrl=").append(techSupportUrl).append(", ");
        if (techSupportPerson != null)
            builder.append("techSupportPerson=").append(techSupportPerson).append(", ");
        if (techSupportPhone != null)
            builder.append("techSupportPhone=").append(techSupportPhone).append(", ");
        if (bankAccountName != null)
            builder.append("bankAccountName=").append(bankAccountName).append(", ");
        if (bankAccountType != null)
            builder.append("bankAccountType=").append(bankAccountType).append(", ");
        if (bankName != null)
            builder.append("bankName=").append(bankName).append(", ");
        if (bankAddr != null)
            builder.append("bankAddr=").append(bankAddr).append(", ");
        if (bankBranch != null)
            builder.append("bankBranch=").append(bankBranch).append(", ");
        if (bankCard != null)
            builder.append("bankCard=").append(bankCard).append(", ");
        if (creator != null)
            builder.append("creator=").append(creator).append(", ");
        if (createTime != null)
            builder.append("createTime=").append(createTime).append(", ");
        if (modifier != null)
            builder.append("modifier=").append(modifier).append(", ");
        if (modifyTime != null)
            builder.append("modifyTime=").append(modifyTime).append(", ");
        if (remark != null)
            builder.append("remark=").append(remark).append(", ");
        if (appId != null)
            builder.append("appId=").append(appId).append(", ");
        if (mchId != null)
            builder.append("mchId=").append(mchId).append(", ");
        if (partnerKey != null)
            builder.append("partnerKey=").append(partnerKey).append(", ");
        if (keyPath != null)
            builder.append("keyPath=").append(keyPath).append(", ");
        if (keyPassword != null)
            builder.append("keyPassword=").append(keyPassword).append(", ");
        if (appSecret != null)
            builder.append("appSecret=").append(appSecret).append(", ");
        if (isvPartnerId != null)
            builder.append("isvPartnerId=").append(isvPartnerId);
        builder.append("]");
        return builder.toString();
    }
    
}
