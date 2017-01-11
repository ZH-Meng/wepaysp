package com.zbsp.wepaysp.mobile.model.vo;

/** H5支付入口VO */
public class MobileH5PayIndexVO {
	
	private String partnerOid;
	private String dealerOid;
	private String storeOid;
	private String dealerEmployeeOid;

	private String payClient;// 支付客户端（扫码识别）
	private String payUrl;// 跳转支付地址（订单地址）

	public String getPartnerOid() {
		return partnerOid;
	}

	public void setPartnerOid(String partnerOid) {
		this.partnerOid = partnerOid;
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

	public String getDealerEmployeeOid() {
		return dealerEmployeeOid;
	}

	public void setDealerEmployeeOid(String dealerEmployeeOid) {
		this.dealerEmployeeOid = dealerEmployeeOid;
	}

	public String getPayClient() {
		return payClient;
	}

	public void setPayClient(String payClient) {
		this.payClient = payClient;
	}

	public String getPayUrl() {
		return payUrl;
	}

	public void setPayUrl(String payUrl) {
		this.payUrl = payUrl;
	}

	@Override
	public String toString() {
		return "MobileH5PayIndexVO [partnerOid=" + partnerOid + ", dealerOid=" + dealerOid + ", storeOid=" + storeOid
				+ ", dealerEmployeeOid=" + dealerEmployeeOid + ", payClient=" + payClient + ", payUrl=" + payUrl + "]";
	}
	
}
