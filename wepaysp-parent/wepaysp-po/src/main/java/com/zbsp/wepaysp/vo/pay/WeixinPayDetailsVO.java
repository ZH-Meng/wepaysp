package com.zbsp.wepaysp.vo.pay;

import java.io.Serializable;
import java.util.Date;

/**
 * 微信交易（支付）明细VO
 * 
 * @author 孟郑宏
 */
public class WeixinPayDetailsVO
    implements Serializable {

    private static final long serialVersionUID = 8467137862008977835L;
    private String iwoid;
    private String partnerOid;
    private String partnerName;
    private String partnerEmployeeName;
    private String dealerName;
    private String storeName;
    private String dealerEmployeeName;
    private String payType;
    private String outTradeNo;
    private Integer totalFee;
    private String resultCode;
    private Date transBeginTime;

    private Date beginTime;
    private Date endTime;

    private String partner2Oid;
    private String partner3Oid;

    private String partnerEmployeeId;
    private String dealerId;
    private String storeId;
    private String dealerEmployeeId;
    private String partnerId;

    private String dealerEmployeeOid;
    
    private String dealerOid;
    private String storeOid;
    private String notifyUrl;// 接收微信支付异步通知回调地址，通知url必须为直接可访问的url，不能携带参数。

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
    private String feeType;
    private String spbillCreateIp;
    private String goodsTag;
    private String limitPay;
    private String authCode;
    private String openid;
    private String subOpenid;
    private String isSubscribe;
    private String tradeType;
    private String bankType;
    private String cashFeeType;
    private Long cashFee;
    private Integer settlementTotalFee;
    private Integer couponFee;
    private String transactionId;
    private Date timeEnd;

    private String returnCode;
    private String returnMsg;
    private String errCode;
    private String errCodeDes;
    private String timeStart;
    private String timeExpire;
    

    public String getIwoid() {
        return iwoid;
    }

    public void setIwoid(String iwoid) {
        this.iwoid = iwoid;
    }

    public String getPartnerOid() {
        return partnerOid;
    }

    public void setPartnerOid(String partnerOid) {
        this.partnerOid = partnerOid;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getPartnerEmployeeName() {
        return partnerEmployeeName;
    }

    public void setPartnerEmployeeName(String partnerEmployeeName) {
        this.partnerEmployeeName = partnerEmployeeName;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getDealerEmployeeName() {
        return dealerEmployeeName;
    }

    public void setDealerEmployeeName(String dealerEmployeeName) {
        this.dealerEmployeeName = dealerEmployeeName;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public Date getTransBeginTime() {
        return transBeginTime;
    }

    public void setTransBeginTime(Date transBeginTime) {
        this.transBeginTime = transBeginTime;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getPartner2Oid() {
        return partner2Oid;
    }

    public void setPartner2Oid(String partner2Oid) {
        this.partner2Oid = partner2Oid;
    }

    public String getPartner3Oid() {
        return partner3Oid;
    }

    public void setPartner3Oid(String partner3Oid) {
        this.partner3Oid = partner3Oid;
    }

    public String getPartnerEmployeeId() {
        return partnerEmployeeId;
    }

    public void setPartnerEmployeeId(String partnerEmployeeId) {
        this.partnerEmployeeId = partnerEmployeeId;
    }

    public String getDealerId() {
        return dealerId;
    }

    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getDealerEmployeeId() {
        return dealerEmployeeId;
    }

    public void setDealerEmployeeId(String dealerEmployeeId) {
        this.dealerEmployeeId = dealerEmployeeId;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }
    
    public String getDealerEmployeeOid() {
        return dealerEmployeeOid;
    }
    
    public void setDealerEmployeeOid(String dealerEmployeeOid) {
        this.dealerEmployeeOid = dealerEmployeeOid;
    }

    public String getDealerOid() {
        return dealerOid;
    }

    public void setDealerOid(String dealerOid) {
        this.dealerOid = dealerOid;
    }

    public String getStoreOid() {
        return storeOid;
    }

    public void setStoreOid(String storeOid) {
        this.storeOid = storeOid;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSubAppid() {
        return subAppid;
    }

    public void setSubAppid(String subAppid) {
        this.subAppid = subAppid;
    }

    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public String getSubMchId() {
        return subMchId;
    }

    public void setSubMchId(String subMchId) {
        this.subMchId = subMchId;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getFeeType() {
        return feeType;
    }

    public void setFeeType(String feeType) {
        this.feeType = feeType;
    }

    public String getSpbillCreateIp() {
        return spbillCreateIp;
    }

    public void setSpbillCreateIp(String spbillCreateIp) {
        this.spbillCreateIp = spbillCreateIp;
    }

    public String getGoodsTag() {
        return goodsTag;
    }

    public void setGoodsTag(String goodsTag) {
        this.goodsTag = goodsTag;
    }

    public String getLimitPay() {
        return limitPay;
    }

    public void setLimitPay(String limitPay) {
        this.limitPay = limitPay;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getSubOpenid() {
        return subOpenid;
    }

    public void setSubOpenid(String subOpenid) {
        this.subOpenid = subOpenid;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public String getErrCodeDes() {
        return errCodeDes;
    }

    public void setErrCodeDes(String errCodeDes) {
        this.errCodeDes = errCodeDes;
    }

    public String getIsSubscribe() {
        return isSubscribe;
    }

    public void setIsSubscribe(String isSubscribe) {
        this.isSubscribe = isSubscribe;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getCashFeeType() {
        return cashFeeType;
    }

    public void setCashFeeType(String cashFeeType) {
        this.cashFeeType = cashFeeType;
    }

    public Long getCashFee() {
        return cashFee;
    }

    public void setCashFee(Long cashFee) {
        this.cashFee = cashFee;
    }

    public Integer getSettlementTotalFee() {
        return settlementTotalFee;
    }

    public void setSettlementTotalFee(Integer settlementTotalFee) {
        this.settlementTotalFee = settlementTotalFee;
    }

    public Integer getCouponFee() {
        return couponFee;
    }

    public void setCouponFee(Integer couponFee) {
        this.couponFee = couponFee;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
    }
    
    public String getTimeStart() {
        return timeStart;
    }
    
    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }
    
    public String getTimeExpire() {
        return timeExpire;
    }

    public void setTimeExpire(String timeExpire) {
        this.timeExpire = timeExpire;
    }

}
