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
@Table(name = "merchant_t")
public class Merchant
    implements java.io.Serializable {

    private static final long serialVersionUID = 2185740448720204906L;
    private String iwoid;
    private Partner partner;
    private String contactor;
    private String moblieNumber;
    private String email;
    private String merchantId;
    private String company;
    private String address;
    private String shortName;
    private String businessSubjects;
    private String serviceTel;
    private String businessLicense;
    private String businessLicenseImg;
    private Date businessBegin;
    private Date businessEnd;
    private String businessScope;
    private String businessSite;
    private String special;
    private String extraLicenses;
    private String state;
    private String organizationCode;
    private Date organizationBegin;
    private Date organizationEnd;
    private String organizationCertImg;
    private String certificateHolderType;
    private String certificateType;
    private String certificateCode;
    private String certificateName;
    private String certificateImg1;
    private String certificateImg2;
    private Date certificateBegin;
    private Date certificateEnd;
    private String bankAccountType;
    private String bankAccountName;
    private String bankName;
    private String bankAddr;
    private String bankBranch;
    private String bankCard;
    private String codeLogo;
    private String codeUrl;
    private Timestamp applyTime;
    private Timestamp applyPassTime;
    private Timestamp signTime;
    private Timestamp configTime;
    private String creator;
    private Timestamp createTime;
    private String modifier;
    private Timestamp modifyTime;
    private String remark;

    public Merchant() {
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

    @Column(name = "EMAIL", nullable = false, length = 100)
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "MERCHANT_ID", nullable = false, length = 32)
    public String getMerchantId() {
        return this.merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
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

    @Column(name = "SHORT_NAME", nullable = false, length = 32)
    public String getShortName() {
        return this.shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @Column(name = "BUSINESS_SUBJECTS", nullable = false, length = 256)
    public String getBusinessSubjects() {
        return this.businessSubjects;
    }

    public void setBusinessSubjects(String businessSubjects) {
        this.businessSubjects = businessSubjects;
    }

    @Column(name = "SERVICE_TEL", nullable = false, length = 32)
    public String getServiceTel() {
        return this.serviceTel;
    }

    public void setServiceTel(String serviceTel) {
        this.serviceTel = serviceTel;
    }

    @Column(name = "BUSINESS_LICENSE", length = 32)
    public String getBusinessLicense() {
        return this.businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    @Column(name = "BUSINESS_LICENSE_IMG", length = 256)
    public String getBusinessLicenseImg() {
        return this.businessLicenseImg;
    }

    public void setBusinessLicenseImg(String businessLicenseImg) {
        this.businessLicenseImg = businessLicenseImg;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "BUSINESS_BEGIN", length = 0)
    public Date getBusinessBegin() {
        return this.businessBegin;
    }

    public void setBusinessBegin(Date businessBegin) {
        this.businessBegin = businessBegin;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "BUSINESS_END", length = 0)
    public Date getBusinessEnd() {
        return this.businessEnd;
    }

    public void setBusinessEnd(Date businessEnd) {
        this.businessEnd = businessEnd;
    }

    @Column(name = "BUSINESS_SCOPE", nullable = false, length = 256)
    public String getBusinessScope() {
        return this.businessScope;
    }

    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    @Column(name = "BUSINESS_SITE", length = 256)
    public String getBusinessSite() {
        return this.businessSite;
    }

    public void setBusinessSite(String businessSite) {
        this.businessSite = businessSite;
    }

    @Column(name = "SPECIAL", length = 256)
    public String getSpecial() {
        return this.special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    @Column(name = "EXTRA_LICENSES", length = 256)
    public String getExtraLicenses() {
        return this.extraLicenses;
    }

    public void setExtraLicenses(String extraLicenses) {
        this.extraLicenses = extraLicenses;
    }

    @Column(name = "STATE", nullable = false, length = 1)
    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Column(name = "ORGANIZATION_CODE", length = 32)
    public String getOrganizationCode() {
        return this.organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "ORGANIZATION_BEGIN", length = 0)
    public Date getOrganizationBegin() {
        return this.organizationBegin;
    }

    public void setOrganizationBegin(Date organizationBegin) {
        this.organizationBegin = organizationBegin;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "ORGANIZATION_END", length = 0)
    public Date getOrganizationEnd() {
        return this.organizationEnd;
    }

    public void setOrganizationEnd(Date organizationEnd) {
        this.organizationEnd = organizationEnd;
    }

    @Column(name = "ORGANIZATION_CERT_IMG", length = 256)
    public String getOrganizationCertImg() {
        return this.organizationCertImg;
    }

    public void setOrganizationCertImg(String organizationCertImg) {
        this.organizationCertImg = organizationCertImg;
    }

    @Column(name = "CERTIFICATE_HOLDER_TYPE", length = 1)
    public String getCertificateHolderType() {
        return this.certificateHolderType;
    }

    public void setCertificateHolderType(String certificateHolderType) {
        this.certificateHolderType = certificateHolderType;
    }

    @Column(name = "CERTIFICATE_TYPE", length = 256)
    public String getCertificateType() {
        return this.certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    @Column(name = "CERTIFICATE_CODE", length = 32)
    public String getCertificateCode() {
        return this.certificateCode;
    }

    public void setCertificateCode(String certificateCode) {
        this.certificateCode = certificateCode;
    }

    @Column(name = "CERTIFICATE_NAME", length = 32)
    public String getCertificateName() {
        return this.certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    @Column(name = "CERTIFICATE_IMG1", length = 256)
    public String getCertificateImg1() {
        return this.certificateImg1;
    }

    public void setCertificateImg1(String certificateImg1) {
        this.certificateImg1 = certificateImg1;
    }

    @Column(name = "CERTIFICATE_IMG2", length = 256)
    public String getCertificateImg2() {
        return this.certificateImg2;
    }

    public void setCertificateImg2(String certificateImg2) {
        this.certificateImg2 = certificateImg2;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "CERTIFICATE_BEGIN", length = 0)
    public Date getCertificateBegin() {
        return this.certificateBegin;
    }

    public void setCertificateBegin(Date certificateBegin) {
        this.certificateBegin = certificateBegin;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "CERTIFICATE_END", length = 0)
    public Date getCertificateEnd() {
        return this.certificateEnd;
    }

    public void setCertificateEnd(Date certificateEnd) {
        this.certificateEnd = certificateEnd;
    }

    @Column(name = "BANK_ACCOUNT_TYPE", length = 1)
    public String getBankAccountType() {
        return this.bankAccountType;
    }

    public void setBankAccountType(String bankAccountType) {
        this.bankAccountType = bankAccountType;
    }

    @Column(name = "BANK_ACCOUNT_NAME", length = 32)
    public String getBankAccountName() {
        return this.bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
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

    @Column(name = "CODE_LOGO", length = 256)
    public String getCodeLogo() {
        return this.codeLogo;
    }

    public void setCodeLogo(String codeLogo) {
        this.codeLogo = codeLogo;
    }

    @Column(name = "CODE_URL", length = 256)
    public String getCodeUrl() {
        return this.codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

    @Column(name = "APPLY_TIME", length = 0)
    public Timestamp getApplyTime() {
        return this.applyTime;
    }

    public void setApplyTime(Timestamp applyTime) {
        this.applyTime = applyTime;
    }

    @Column(name = "APPLY_PASS_TIME", length = 0)
    public Timestamp getApplyPassTime() {
        return this.applyPassTime;
    }

    public void setApplyPassTime(Timestamp applyPassTime) {
        this.applyPassTime = applyPassTime;
    }

    @Column(name = "SIGN_TIME", length = 0)
    public Timestamp getSignTime() {
        return this.signTime;
    }

    public void setSignTime(Timestamp signTime) {
        this.signTime = signTime;
    }

    @Column(name = "CONFIG_TIME", length = 0)
    public Timestamp getConfigTime() {
        return this.configTime;
    }

    public void setConfigTime(Timestamp configTime) {
        this.configTime = configTime;
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
