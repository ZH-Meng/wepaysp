package com.zbsp.wepaysp.po.pay;

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
import com.zbsp.wepaysp.po.partner.DealerEmployee;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.po.partner.PartnerEmployee;
import com.zbsp.wepaysp.po.partner.Store;

@Entity
@Table(name = "alipay_refund_details_t")
public class AlipayRefundDetails
    implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3263285986836072098L;
    private String iwoid;
    private Dealer dealer;
    private AliPayDetails aliPayDetails;
    private Store store;
    private DealerEmployee dealerEmployee;
    private Partner partner;
    private PartnerEmployee partnerEmployee;
    private Integer partnerLevel;
    private String partner1Oid;
    private String partner2Oid;
    private String partner3Oid;
    private String appId;
    private String appAuthToken;
    private Integer refundType;
    private String outRefundNo;
    private Integer refundAmount;
    private String refundReason;
    private String operatorId;
    private String storeId;
    private String terminalId;
    private String alipayStoreId;
    private String code;
    private String msg;
    private String subCode;
    private String subMsg;
    private String sign;
    private String outTradeNo;
    private String tradeNo;
    private String openId;
    private String buyerLogonId;
    private String fundChange;
    private Integer refundFee;
    private Date gmtRefundPay;
    private String storeName;
    private String buyerUserId;
    private Date transBeginTime;
    private Date transEndTime;
    private Integer tradeStatus;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;

    public AlipayRefundDetails() {
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
    @JoinColumn(name = "ALI_PAY_DETAILS_OID")
    public AliPayDetails getAliPayDetails() {
        return this.aliPayDetails;
    }

    public void setAliPayDetails(AliPayDetails aliPayDetails) {
        this.aliPayDetails = aliPayDetails;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STORE_OID")
    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEALER_EMPLOYEE_OID")
    public DealerEmployee getDealerEmployee() {
        return this.dealerEmployee;
    }

    public void setDealerEmployee(DealerEmployee dealerEmployee) {
        this.dealerEmployee = dealerEmployee;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTNER_OID")
    public Partner getPartner() {
        return this.partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTNER_EMPLOYEE_OID")
    public PartnerEmployee getPartnerEmployee() {
        return this.partnerEmployee;
    }

    public void setPartnerEmployee(PartnerEmployee partnerEmployee) {
        this.partnerEmployee = partnerEmployee;
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

    @Column(name = "REFUND_TYPE")
    public Integer getRefundType() {
        return this.refundType;
    }

    public void setRefundType(Integer refundType) {
        this.refundType = refundType;
    }

    @Column(name = "OUT_REFUND_NO", length = 32)
    public String getOutRefundNo() {
        return this.outRefundNo;
    }

    public void setOutRefundNo(String outRefundNo) {
        this.outRefundNo = outRefundNo;
    }

    @Column(name = "REFUND_AMOUNT")
    public Integer getRefundAmount() {
        return this.refundAmount;
    }

    public void setRefundAmount(Integer refundAmount) {
        this.refundAmount = refundAmount;
    }

    @Column(name = "REFUND_REASON", length = 256)
    public String getRefundReason() {
        return this.refundReason;
    }

    public void setRefundReason(String refundReason) {
        this.refundReason = refundReason;
    }

    @Column(name = "OPERATOR_ID", length = 28)
    public String getOperatorId() {
        return this.operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    @Column(name = "STORE_ID", length = 32)
    public String getStoreId() {
        return this.storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    @Column(name = "TERMINAL_ID", length = 32)
    public String getTerminalId() {
        return this.terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    @Column(name = "ALIPAY_STORE_ID", length = 32)
    public String getAlipayStoreId() {
        return this.alipayStoreId;
    }

    public void setAlipayStoreId(String alipayStoreId) {
        this.alipayStoreId = alipayStoreId;
    }

    @Column(name = "CODE", length = 16)
    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Column(name = "MSG", length = 128)
    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Column(name = "SUB_CODE", length = 64)
    public String getSubCode() {
        return this.subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    @Column(name = "SUB_MSG", length = 128)
    public String getSubMsg() {
        return this.subMsg;
    }

    public void setSubMsg(String subMsg) {
        this.subMsg = subMsg;
    }

    @Column(name = "SIGN", length = 256)
    public String getSign() {
        return this.sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Column(name = "OUT_TRADE_NO", length = 64)
    public String getOutTradeNo() {
        return this.outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    @Column(name = "TRADE_NO", length = 64)
    public String getTradeNo() {
        return this.tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    @Column(name = "OPEN_ID", length = 32)
    public String getOpenId() {
        return this.openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    @Column(name = "BUYER_LOGON_ID", length = 100)
    public String getBuyerLogonId() {
        return this.buyerLogonId;
    }

    public void setBuyerLogonId(String buyerLogonId) {
        this.buyerLogonId = buyerLogonId;
    }

    @Column(name = "FUND_CHANGE", length = 1)
    public String getFundChange() {
        return this.fundChange;
    }

    public void setFundChange(String fundChange) {
        this.fundChange = fundChange;
    }

    @Column(name = "REFUND_FEE")
    public Integer getRefundFee() {
        return this.refundFee;
    }

    public void setRefundFee(Integer refundFee) {
        this.refundFee = refundFee;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "GMT_REFUND_PAY", nullable = false, length = 0)
    public Date getGmtRefundPay() {
        return this.gmtRefundPay;
    }

    public void setGmtRefundPay(Date gmtRefundPay) {
        this.gmtRefundPay = gmtRefundPay;
    }

    @Column(name = "STORE_NAME", length = 512)
    public String getStoreName() {
        return this.storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    @Column(name = "BUYER_USER_ID", length = 28)
    public String getBuyerUserId() {
        return this.buyerUserId;
    }

    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "TRANS_BEGIN_TIME", length = 0)
    public Date getTransBeginTime() {
        return this.transBeginTime;
    }

    public void setTransBeginTime(Date transBeginTime) {
        this.transBeginTime = transBeginTime;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "TRANS_END_TIME", length = 0)
    public Date getTransEndTime() {
        return this.transEndTime;
    }

    public void setTransEndTime(Date transEndTime) {
        this.transEndTime = transEndTime;
    }

    @Column(name = "TRADE_STATUS")
    public Integer getTradeStatus() {
        return this.tradeStatus;
    }

    public void setTradeStatus(Integer tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    @Column(name = "CREATOR", nullable = false, length = 64)
    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "CREATE_TIME", nullable = false, length = 0)
    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Column(name = "MODIFIER", length = 32)
    public String getModifier() {
        return this.modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "MODIFY_TIME", length = 0)
    public Date getModifyTime() {
        return this.modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
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
