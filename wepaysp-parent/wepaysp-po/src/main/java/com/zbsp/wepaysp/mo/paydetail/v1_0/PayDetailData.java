package com.zbsp.wepaysp.mo.paydetail.v1_0;

/**
 * 支付数据
 * */
public class PayDetailData {
	
	/**商户订单号*/
	private String outTradeNo;
	
	/**支付方式*/
	private String payType;
	
	/**订单状态*/
	private String tradeStatus;
	
	/**实收金额，单位：分*/
	private long collectionMoney;
	
	/**退款金额，单位：分*/
	private long refundMoney;
	
	/**交易时间 yyyy/MM/dd HH:mm:ss*/
	private String transTime;
	
	public PayDetailData() {
	}
	
	public PayDetailData(String outTradeNo, String payType, String tradeStatus, long collectionMoney, long refundMoney, String transTime) {
		super();
		this.outTradeNo = outTradeNo;
		this.payType = payType;
		this.tradeStatus = tradeStatus;
		this.collectionMoney = collectionMoney;
		this.refundMoney = refundMoney;
		this.transTime = transTime;
	}

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(String tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public long getCollectionMoney() {
		return collectionMoney;
	}

	public void setCollectionMoney(long collectionMoney) {
		this.collectionMoney = collectionMoney;
	}

	public long getRefundMoney() {
		return refundMoney;
	}

	public void setRefundMoney(long refundMoney) {
		this.refundMoney = refundMoney;
	}

	public String getTransTime() {
		return transTime;
	}

	public void setTransTime(String transTime) {
		this.transTime = transTime;
	}
	
}
