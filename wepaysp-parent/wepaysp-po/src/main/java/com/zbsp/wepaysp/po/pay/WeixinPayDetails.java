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
@Table(name = "weixin_pay_details_t")
public class WeixinPayDetails
    implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2544758054505060094L;
    private String iwoid;
    private Dealer dealer;
    private DealerEmployee dealerEmployee;
    private Store store;
    private Partner partner;
    private PartnerEmployee partnerEmployee;
    private String payType;
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
    private String body;
    private String detail;
    private String attach;
    private String outTradeNo;
    private Integer totalFee;
    private String feeType;
    private String spbillCreateIp;
    private String goodsTag;
    private String limitPay;
    private String authCode;
    private String returnCode;
    private String returnMsg;
    private String resultCode;
    private String errCode;
    private String errCodeDes;
    private String openid;
    private String isSubscribe;
    private String subOpenid;
    private String subIsSubscribe;
    private String tradeType;
    private String bankType;
    private String cashFeeType;
    private Long cashFee;
    private Integer settlementTotalFee;
    private Integer couponFee;
    private String transactionId;
    private Date timeEnd;
    private Date transBeginTime;
    private Date transEndTime;
    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    private String remark;

    /**
     * 交易类型 
     */
    public static enum PayType {
        /** 1：刷卡支付 */        MICROPAY("1"),
        /** 2：公众号支付 */     JSAPI("2"),
        /** 3：扫码支付 */        NATIVE("3"),
        /** 4：微信买单 */        WEIXINPAY("4");
        private String value;

        public String getValue() {
            return value;
        }

        private PayType(String value) {
            this.value = value;
        }
    }
    
    public WeixinPayDetails() {
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

    @Column(name = "PAY_TYPE", length = 32)
    public String getPayType() {
        return this.payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
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

    @Column(name = "MCH_ID", length = 32)
    public String getMchId() {
        return this.mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    @Column(name = "SUB_MCH_ID", length = 32)
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

    @Column(name = "BODY", length = 128)
    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Column(name = "DETAIL", length = 8192)
    public String getDetail() {
        return this.detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Column(name = "ATTACH", length = 128)
    public String getAttach() {
        return this.attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    @Column(name = "OUT_TRADE_NO", length = 32)
    public String getOutTradeNo() {
        return this.outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    @Column(name = "TOTAL_FEE")
    public Integer getTotalFee() {
        return this.totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    @Column(name = "FEE_TYPE", length = 16)
    public String getFeeType() {
        return this.feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    @Column(name = "SPBILL_CREATE_IP", length = 16)
    public String getSpbillCreateIp() {
        return this.spbillCreateIp;
    }

    public void setSpbillCreateIp(String spbillCreateIp) {
        this.spbillCreateIp = spbillCreateIp;
    }

    @Column(name = "GOODS_TAG", length = 32)
    public String getGoodsTag() {
        return this.goodsTag;
    }

    public void setGoodsTag(String goodsTag) {
        this.goodsTag = goodsTag;
    }

    @Column(name = "LIMIT_PAY", length = 32)
    public String getLimitPay() {
        return this.limitPay;
    }

    public void setLimitPay(String limitPay) {
        this.limitPay = limitPay;
    }

    @Column(name = "AUTH_CODE", length = 128)
    public String getAuthCode() {
        return this.authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
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

    @Column(name = "OPENID", length = 128)
    public String getOpenid() {
        return this.openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Column(name = "IS_SUBSCRIBE", length = 1)
    public String getIsSubscribe() {
        return this.isSubscribe;
    }

    public void setIsSubscribe(String isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    @Column(name = "SUB_OPENID", length = 128)
    public String getSubOpenid() {
        return this.subOpenid;
    }

    public void setSubOpenid(String subOpenid) {
        this.subOpenid = subOpenid;
    }

    @Column(name = "SUB_IS_SUBSCRIBE", length = 1)
    public String getSubIsSubscribe() {
        return this.subIsSubscribe;
    }

    public void setSubIsSubscribe(String subIsSubscribe) {
        this.subIsSubscribe = subIsSubscribe;
    }

    @Column(name = "TRADE_TYPE", length = 16)
    public String getTradeType() {
        return this.tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    @Column(name = "BANK_TYPE", length = 16)
    public String getBankType() {
        return this.bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    @Column(name = "CASH_FEE_TYPE", length = 16)
    public String getCashFeeType() {
        return this.cashFeeType;
    }

    public void setCashFeeType(String cashFeeType) {
        this.cashFeeType = cashFeeType;
    }

    @Column(name = "CASH_FEE")
    public Long getCashFee() {
        return this.cashFee;
    }

    public void setCashFee(Long cashFee) {
        this.cashFee = cashFee;
    }

    @Column(name = "SETTLEMENT_TOTAL_FEE")
    public Integer getSettlementTotalFee() {
        return this.settlementTotalFee;
    }

    public void setSettlementTotalFee(Integer settlementTotalFee) {
        this.settlementTotalFee = settlementTotalFee;
    }

    @Column(name = "COUPON_FEE")
    public Integer getCouponFee() {
        return this.couponFee;
    }

    public void setCouponFee(Integer couponFee) {
        this.couponFee = couponFee;
    }

    @Column(name = "TRANSACTION_ID", length = 32)
    public String getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Column(name = "TIME_END", length = 0)
    public Date getTimeEnd() {
        return this.timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
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

    @Column(name = "CREATOR", length = 32)
    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(name = "CREATE_TIME", length = 0)
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

    @Override
    public String toString() {
        return "WeixinPayDetails [iwoid=" + iwoid + ", dealer=" + dealer + ", dealerEmployee=" + dealerEmployee + ", store=" + store + ", partner=" + partner + ", partnerEmployee=" + partnerEmployee + ", payType=" + payType + ", deviceInfo=" + deviceInfo + ", nonceStr=" + nonceStr + ", sign=" + sign + ", body=" + body + ", detail=" + detail + ", attach=" + attach + ", outTradeNo=" + outTradeNo + ", totalFee=" + totalFee + ", feeType=" + feeType + ", spbillCreateIp=" + spbillCreateIp + ", goodsTag=" + goodsTag + ", tradeType=" + tradeType + ", bankType=" + bankType + ", cashFeeType=" + cashFeeType + ", cashFee=" + cashFee + ", transBeginTime=" + transBeginTime + "]";
    }

}
