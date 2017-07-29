package com.zbsp.wepaysp.po.pay;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.zbsp.wepaysp.po.partner.Dealer;
import com.zbsp.wepaysp.po.partner.DealerEmployee;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.po.partner.PartnerEmployee;
import com.zbsp.wepaysp.po.partner.Store;

@Entity
@Table(name = "weixin_refund_details_t")
public class WeixinRefundDetails
    implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2387694473825090871L;
    private String iwoid;
    private String weixinPayDetailsOid;
    private Dealer dealer;
    private DealerEmployee dealerEmployee;
    private Store store;
    private Partner partner;
    private PartnerEmployee partnerEmployee;
    private Integer partnerLevel;
    private String partner1Oid;
    private String partner2Oid;
    private String partner3Oid;
    private String appid;
    private String subAppid;
    private String mchId;
    private String subMchId;
    private String deviceInfo;
    private String nonceStr;
    private String sign;
    private String outTradeNo;
    private String transactionId;
  	private String refundId;
    private Integer refundType;
    private String outRefundNo;
    private Integer totalFee;
    private Integer refundFee;
    private String refundFeeType;
    private String refundAccount;
    private String returnCode;
    private String returnMsg;
    private String resultCode;
    private String errCode;
    private String errCodeDes;
    private String refundChannel;
    private Integer cashFee;
    private Integer cashRefundFee;
    private String cashFeeType;
    private String couponType;
    private Integer couponRefundFee;
    private Integer couponRefundCount;
    private String couponRefundBatchId;
    private String couponRefundId;
    private Integer couponRefundFeeSingle;
    private Date transBeginTime;
    private Date transEndTime;
    private Date endTime;
    private Integer tradeStatus;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;

    public WeixinRefundDetails() {
    }

    @Id
    @Column(name = "IWOID", unique = true, nullable = false, length = 32)
    public String getIwoid() {
        return this.iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    @Column(name = "WEIXIN_PAY_DETAILS_OID", length = 32)
    public String getWeixinPayDetailsOid() {
        return weixinPayDetailsOid;
    }

    public void setWeixinPayDetailsOid(String weixinPayDetailsOid) {
        this.weixinPayDetailsOid = weixinPayDetailsOid;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEALER_OID", nullable = false)
    public Dealer getDealer() {
        return this.dealer;
    }

    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
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
    @JoinColumn(name = "STORE_OID")
    public Store getStore() {
        return this.store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARTNER_OID", nullable = false)
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

    @Column(name = "APPID", length = 32)
    public String getAppid() {
        return this.appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    @Column(name = "SUB_APPID", length = 32)
    public String getSubAppid() {
        return this.subAppid;
    }

    public void setSubAppid(String subAppid) {
        this.subAppid = subAppid;
    }

    @Column(name = "MCH_ID", nullable = false, length = 32)
    public String getMchId() {
        return this.mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    @Column(name = "SUB_MCH_ID", nullable = false, length = 32)
    public String getSubMchId() {
        return this.subMchId;
    }

    public void setSubMchId(String subMchId) {
        this.subMchId = subMchId;
    }

    @Column(name = "DEVICE_INFO", length = 32)
    public String getDeviceInfo() {
        return this.deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    @Column(name = "NONCE_STR", length = 32)
    public String getNonceStr() {
        return this.nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    @Column(name = "SIGN", length = 32)
    public String getSign() {
        return this.sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Column(name = "OUT_TRADE_NO", nullable = false, length = 32)
    public String getOutTradeNo() {
        return this.outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    @Column(name = "TRANSACTION_ID", length = 32)
    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Column(name = "REFUND_TYPE")
    public Integer getRefundType() {
        return refundType;
    }

    public void setRefundType(Integer refundType) {
        this.refundType = refundType;
    }

    @Column(name = "TOTAL_FEE")
    public Integer getTotalFee() {
        return this.totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    @Column(name = "REFUND_FEE")
    public Integer getRefundFee() {
        return this.refundFee;
    }

    public void setRefundFee(Integer refundFee) {
        this.refundFee = refundFee;
    }

    @Column(name = "REFUND_FEE_TYPE", length = 16)
    public String getRefundFeeType() {
        return this.refundFeeType;
    }

    public void setRefundFeeType(String refundFeeType) {
        this.refundFeeType = refundFeeType;
    }

    @Column(name = "REFUND_ACCOUNT", length = 32)
    public String getRefundAccount() {
        return this.refundAccount;
    }

    public void setRefundAccount(String refundAccount) {
        this.refundAccount = refundAccount;
    }

    @Column(name = "RETURN_CODE", length = 16)
    public String getReturnCode() {
        return this.returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    @Column(name = "RETURN_MSG", length = 128)
    public String getReturnMsg() {
        return this.returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    @Column(name = "RESULT_CODE", length = 16)
    public String getResultCode() {
        return this.resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Column(name = "ERR_CODE", length = 32)
    public String getErrCode() {
        return this.errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    @Column(name = "ERR_CODE_DES", length = 128)
    public String getErrCodeDes() {
        return this.errCodeDes;
    }

    public void setErrCodeDes(String errCodeDes) {
        this.errCodeDes = errCodeDes;
    }
    
    @Column(name = "OUT_REFUND_NO", length = 64)
    public String getOutRefundNo() {
		return outRefundNo;
	}

	public void setOutRefundNo(String outRefundNo) {
		this.outRefundNo = outRefundNo;
	}
	
    @Column(name = "REFUND_ID", length = 64)
    public String getRefundId() {
        return this.refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
    }

    @Column(name = "REFUND_CHANNEL", length = 16)
    public String getRefundChannel() {
        return this.refundChannel;
    }

    public void setRefundChannel(String refundChannel) {
        this.refundChannel = refundChannel;
    }

    @Column(name = "CASH_FEE")
    public Integer getCashFee() {
        return this.cashFee;
    }

    public void setCashFee(Integer cashFee) {
        this.cashFee = cashFee;
    }

    @Column(name = "CASH_REFUND_FEE")
    public Integer getCashRefundFee() {
        return this.cashRefundFee;
    }

    public void setCashRefundFee(Integer cashRefundFee) {
        this.cashRefundFee = cashRefundFee;
    }

    @Column(name = "CASH_FEE_TYPE", length = 16)
    public String getCashFeeType() {
        return this.cashFeeType;
    }

    public void setCashFeeType(String cashFeeType) {
        this.cashFeeType = cashFeeType;
    }

    @Column(name = "COUPON_TYPE", length = 8)
    public String getCouponType() {
        return this.couponType;
    }

    public void setCouponType(String couponType) {
        this.couponType = couponType;
    }

    @Column(name = "COUPON_REFUND_FEE")
    public Integer getCouponRefundFee() {
        return this.couponRefundFee;
    }

    public void setCouponRefundFee(Integer couponRefundFee) {
        this.couponRefundFee = couponRefundFee;
    }

    @Column(name = "COUPON_REFUND_COUNT")
    public Integer getCouponRefundCount() {
        return this.couponRefundCount;
    }

    public void setCouponRefundCount(Integer couponRefundCount) {
        this.couponRefundCount = couponRefundCount;
    }

    @Column(name = "COUPON_REFUND_BATCH_ID", length = 32)
    public String getCouponRefundBatchId() {
        return this.couponRefundBatchId;
    }

    public void setCouponRefundBatchId(String couponRefundBatchId) {
        this.couponRefundBatchId = couponRefundBatchId;
    }

    @Column(name = "COUPON_REFUND_ID", length = 32)
    public String getCouponRefundId() {
        return this.couponRefundId;
    }

    public void setCouponRefundId(String couponRefundId) {
        this.couponRefundId = couponRefundId;
    }

    @Column(name = "COUPON_REFUND_FEE_SINGLE")
    public Integer getCouponRefundFeeSingle() {
        return this.couponRefundFeeSingle;
    }

    public void setCouponRefundFeeSingle(Integer couponRefundFeeSingle) {
        this.couponRefundFeeSingle = couponRefundFeeSingle;
    }

    @Column(name = "TRANS_BEGIN_TIME", length = 0)
    public Date getTransBeginTime() {
        return this.transBeginTime;
    }

    public void setTransBeginTime(Date transBeginTime) {
        this.transBeginTime = transBeginTime;
    }

    @Column(name = "TRANS_END_TIME", length = 0)
    public Date getTransEndTime() {
        return this.transEndTime;
    }

    public void setTransEndTime(Date transEndTime) {
        this.transEndTime = transEndTime;
    }

    @Column(name = "END_TIME", length = 0)
    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Column(name = "trade_status")
    public Integer getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(Integer tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    @Column(name = "CREATOR", nullable = false, length = 32)
    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

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
