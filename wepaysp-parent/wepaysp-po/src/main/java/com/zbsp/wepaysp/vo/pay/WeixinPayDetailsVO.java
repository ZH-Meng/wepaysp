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
    private Date timeEnd;
    
    private Date beginTime;
    private Date endTime;
    private String refundEmployeeName;

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

    public Date getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Date timeEnd) {
        this.timeEnd = timeEnd;
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

	public String getRefundEmployeeName() {
		return refundEmployeeName;
	}

	public void setRefundEmployeeName(String refundEmployeeName) {
		this.refundEmployeeName = refundEmployeeName;
	}
    
}
