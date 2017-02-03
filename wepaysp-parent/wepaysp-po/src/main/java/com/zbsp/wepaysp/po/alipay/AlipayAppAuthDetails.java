package com.zbsp.wepaysp.po.alipay;

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

import com.zbsp.wepaysp.po.partner.Dealer;

@Entity
@Table(name = "alipay_app_auth_details_t")
public class AlipayAppAuthDetails
    implements java.io.Serializable {

    private static final long serialVersionUID = -260302848174622982L;

    private String iwoid;
    private Dealer dealer;
    private AlipayApp alipayApp;
    private String appId;
    private String appAuthToken;
    private String appRefreshToken;
    private Integer expiresIn;
    private Integer reExpiresIn;
    private String authAppId;
    private String authMethods;
    private Date authStart;
    private Date authEnd;
    private String status;
    private String creator;
    private Timestamp createTime;
    private String modifier;
    private Timestamp modifyTime;
    private String remark;

    public static enum AppAuthStatus {
        /**有效状态*/
        VALID,
        /**无效状态*/
        INVALID;
    }
    
    public AlipayAppAuthDetails() {
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
    @JoinColumn(name = "DEALER_OID")
    public Dealer getDealer() {
        return this.dealer;
    }

    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ALIPAY_APP_OID")
    public AlipayApp getAlipayApp() {
        return this.alipayApp;
    }

    public void setAlipayApp(AlipayApp alipayApp) {
        this.alipayApp = alipayApp;
    }

    @Column(name = "APP_ID", length = 16)
    public String getAppId() {
        return this.appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Column(name = "APP_AUTH_TOKEN", length = 40)
    public String getAppAuthToken() {
        return this.appAuthToken;
    }

    public void setAppAuthToken(String appAuthToken) {
        this.appAuthToken = appAuthToken;
    }

    @Column(name = "APP_REFRESH_TOKEN", length = 40)
    public String getAppRefreshToken() {
        return this.appRefreshToken;
    }

    public void setAppRefreshToken(String appRefreshToken) {
        this.appRefreshToken = appRefreshToken;
    }

    @Column(name = "EXPIRES_IN")
    public Integer getExpiresIn() {
        return this.expiresIn;
    }

    public void setExpiresIn(Integer expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Column(name = "RE_EXPIRES_IN")
    public Integer getReExpiresIn() {
        return this.reExpiresIn;
    }

    public void setReExpiresIn(Integer reExpiresIn) {
        this.reExpiresIn = reExpiresIn;
    }
    
    @Column(name = "auth_app_id", length = 16)
    public String getAuthAppId() {
        return this.authAppId;
    }

    public void setAuthAppId(String authAppId) {
        this.authAppId = authAppId;
    }

    @Column(name = "AUTH_METHODS", length = 1028)
    public String getAuthMethods() {
        return this.authMethods;
    }

    public void setAuthMethods(String authMethods) {
        this.authMethods = authMethods;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "AUTH_START", length = 0)
    public Date getAuthStart() {
        return this.authStart;
    }

    public void setAuthStart(Date authStart) {
        this.authStart = authStart;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "AUTH_END", length = 0)
    public Date getAuthEnd() {
        return this.authEnd;
    }

    public void setAuthEnd(Date authEnd) {
        this.authEnd = authEnd;
    }

    @Column(name = "STATUS", length = 10)
    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        builder.append("AlipayAppAuthDetails [");
        if (iwoid != null)
            builder.append("iwoid=").append(iwoid).append(", ");
        if (dealer != null)
            builder.append("dealerOid=").append(dealer.getIwoid()).append(", ");
        if (alipayApp != null)
            builder.append("alipayAppOid=").append(alipayApp.getIwoid()).append(", ");
        if (appId != null)
            builder.append("appId=").append(appId).append(", ");
        if (appAuthToken != null)
            builder.append("appAuthToken=").append(appAuthToken).append(", ");
        if (appRefreshToken != null)
            builder.append("appRefreshToken=").append(appRefreshToken).append(", ");
        if (expiresIn != null)
            builder.append("expiresIn=").append(expiresIn).append(", ");
        if (reExpiresIn != null)
            builder.append("reExpiresIn=").append(reExpiresIn).append(", ");
        if (authAppId != null)
            builder.append("authAppId=").append(authAppId).append(", ");
        if (authMethods != null)
            builder.append("authMethods=").append(authMethods).append(", ");
        if (authStart != null)
            builder.append("authStart=").append(authStart).append(", ");
        if (authEnd != null)
            builder.append("authEnd=").append(authEnd).append(", ");
        if (status != null)
            builder.append("status=").append(status).append(", ");
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
