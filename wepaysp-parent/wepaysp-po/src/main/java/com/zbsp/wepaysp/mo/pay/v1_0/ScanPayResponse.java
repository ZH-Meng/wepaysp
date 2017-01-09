package com.zbsp.wepaysp.mo.pay.v1_0;

import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mo.base.MobileResponse;

/**
 * 智慧扫码支付响应
 */
public class ScanPayResponse extends MobileResponse {
	
	/**商户订单号*/
	private String outTradeNo;
	
	/**支付方式*/
	private int payType;
	
	/**订单状态*/
	private int tradeStatus;
	
	/**实收金额，单位：分*/
	private long collectionMoney;
	
	/**交易时间 yyyy/MM/dd HH:mm:ss*/
	private String transTime;
	
    public ScanPayResponse() {}

	public ScanPayResponse(int result, String message, String responseId) {
		super(result, message, responseId);
	}
	
    public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public int getPayType() {
		return payType;
	}

	public void setPayType(int payType) {
		this.payType = payType;
	}

	public int getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(int tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public long getCollectionMoney() {
		return collectionMoney;
	}

	public void setCollectionMoney(long collectionMoney) {
		this.collectionMoney = collectionMoney;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}

	@Override
	public String toString() {
		return "ScanPayResponse [outTradeNo=" + outTradeNo + ", payType=" + payType + ", tradeStatus=" + tradeStatus
				+ ", collectionMoney=" + collectionMoney + ", transTime=" + transTime + ", " + super.toString() + " ]";
	}

	@Override
    public ScanPayResponse build(String key) {
        try {
            setSignature(Signature.getSign(this, key));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

}
