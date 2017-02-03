package com.zbsp.wepaysp.po.alipay;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zbsp.wepaysp.po.partner.Partner;

@Entity
@Table(name = "alipay_app_t")
public class AlipayApp
    implements java.io.Serializable {
    
    private static final long serialVersionUID = -5680982085041953880L;
    
    private String iwoid;
    private Partner partner;
    private String appId;
    private String appName;
    private String appType;
    private String alipayPublicKey;
    private String signType;
    private String publicKey;
    private String privateKey;
    private Integer maxQueryRetry;
    private Integer queryDuration;
    private Integer maxCancelRetry;
    private Integer cancelDuration;
    private String creator;
    private Timestamp createTime;
    private String modifier;
    private Timestamp modifyTime;
    private String remark;

    /**蚂蚁开放平台应用的类型*/
    public static enum AppType {
        /**普通应用*/
        ORDINARY,
        /**服务窗应用*/
        SERVICE_WINDOW,
        /**沙箱应用*/
        SANDBOXIE;
    }
    
    public AlipayApp() {
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

    @Column(name = "APP_ID", length = 16)
    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
    
    @Column(name = "app_name", length = 32)
    public String getAppName() {
        return this.appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Column(name = "APP_TYPE", length = 16)
    public String getAppType() {
        return this.appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    @Column(name = "ALIPAY_PUBLIC_KEY", length = 512)
    public String getAlipayPublicKey() {
        return this.alipayPublicKey;
    }

    public void setAlipayPublicKey(String alipayPublicKey) {
        this.alipayPublicKey = alipayPublicKey;
    }

    @Column(name = "SIGN_TYPE", length = 8)
    public String getSignType() {
        return this.signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    @Column(name = "PUBLIC_KEY", length = 512)
    public String getPublicKey() {
        return this.publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    @Column(name = "PRIVATE_KEY", length = 2048)
    public String getPrivateKey() {
        return this.privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @Column(name = "MAX_QUERY_RETRY")
    public Integer getMaxQueryRetry() {
        return this.maxQueryRetry;
    }

    public void setMaxQueryRetry(Integer maxQueryRetry) {
        this.maxQueryRetry = maxQueryRetry;
    }

    @Column(name = "QUERY_DURATION")
    public Integer getQueryDuration() {
        return this.queryDuration;
    }

    public void setQueryDuration(Integer queryDuration) {
        this.queryDuration = queryDuration;
    }

    @Column(name = "MAX_CANCEL_RETRY")
    public Integer getMaxCancelRetry() {
        return this.maxCancelRetry;
    }

    public void setMaxCancelRetry(Integer maxCancelRetry) {
        this.maxCancelRetry = maxCancelRetry;
    }

    @Column(name = "CANCEL_DURATION")
    public Integer getCancelDuration() {
        return this.cancelDuration;
    }

    public void setCancelDuration(Integer cancelDuration) {
        this.cancelDuration = cancelDuration;
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
        builder.append("AlipayApp [");
        if (iwoid != null)
            builder.append("iwoid=").append(iwoid).append(", ");
        if (partner != null)
            builder.append("partnerOid=").append(partner.getIwoid()).append(", ");
        if (appId != null)
            builder.append("appId=").append(appId).append(", ");
        if (appName != null)
            builder.append("appName=").append(appName).append(", ");
        if (appType != null)
            builder.append("appType=").append(appType).append(", ");
        if (alipayPublicKey != null)
            builder.append("alipayPublicKey=").append(alipayPublicKey).append(", ");
        if (signType != null)
            builder.append("signType=").append(signType).append(", ");
        if (publicKey != null)
            builder.append("publicKey=").append(publicKey).append(", ");
        if (privateKey != null)
            builder.append("privateKey=").append(privateKey).append(", ");
        if (maxQueryRetry != null)
            builder.append("maxQueryRetry=").append(maxQueryRetry).append(", ");
        if (queryDuration != null)
            builder.append("queryDuration=").append(queryDuration).append(", ");
        if (maxCancelRetry != null)
            builder.append("maxCancelRetry=").append(maxCancelRetry).append(", ");
        if (cancelDuration != null)
            builder.append("cancelDuration=").append(cancelDuration).append(", ");
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
