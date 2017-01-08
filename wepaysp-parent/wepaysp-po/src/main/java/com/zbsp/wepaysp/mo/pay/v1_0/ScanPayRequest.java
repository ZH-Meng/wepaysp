package com.zbsp.wepaysp.mo.pay.v1_0;

import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mo.base.MobileRequest;

/**
 * 智慧扫码支付请求
 */
public class ScanPayRequest extends MobileRequest {

	/** 收银员Oid */
	private String dealerEmployeeOid;

	/** 支付金额，单位为分 */
	private long payMoney;

	/** 扫描枪扫码支付授权码 */
	private String authCode;

	public String getDealerEmployeeOid() {
		return dealerEmployeeOid;
	}

	public void setDealerEmployeeOid(String dealerEmployeeOid) {
		this.dealerEmployeeOid = dealerEmployeeOid;
	}

	public long getPayMoney() {
		return payMoney;
	}

	public void setPayMoney(long payMoney) {
		this.payMoney = payMoney;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	@Override
	public String toString() {
		return "ScanPayRequest [dealerEmployeeOid=" + dealerEmployeeOid + ", payMoney=" + payMoney + ", authCode=" + authCode + ", " + super.toString() + " ]";
	}

	@Override
	public ScanPayRequest build(String key) {
		try {
			setSignature(Signature.getSign(this, key));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return this;
	}

}
