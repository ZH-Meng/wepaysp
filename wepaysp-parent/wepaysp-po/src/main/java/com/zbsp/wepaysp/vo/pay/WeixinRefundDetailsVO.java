package com.zbsp.wepaysp.vo.pay;

import java.io.Serializable;
import java.util.Date;

public class WeixinRefundDetailsVO
    implements Serializable {

    private static final long serialVersionUID = 8467137862008977835L;
    private String iwoid;
    private String partnerOid;
    private String partnerName;
    private String partnerEmployeeName;
    private String dealerName;
    private String storeName;
    private String dealerEmployeeName;
    private String outTradeNo;
    private Integer totalFee;
    private Integer refundFee;
    private String resultCode;
    private Date transBeginTime;

    private Date beginTime;
    private Date endTime;
    private String refundEmployeeName;// 退款人
    private String partner2Oid;
    private String partner3Oid;

    private String partnerEmployeeId;
    private String dealerId;
    private String storeId;
    private String dealerEmployeeId;

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

    public Integer getRefundFee() {
        return refundFee;
    }

    public void setRefundFee(Integer refundFee) {
        this.refundFee = refundFee;
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

	public String getRefundEmployeeName() {
		return refundEmployeeName;
	}

	public void setRefundEmployeeName(String refundEmployeeName) {
		this.refundEmployeeName = refundEmployeeName;
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

}
