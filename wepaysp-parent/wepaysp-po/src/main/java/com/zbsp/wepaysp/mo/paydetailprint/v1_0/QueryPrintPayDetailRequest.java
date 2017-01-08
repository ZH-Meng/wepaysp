package com.zbsp.wepaysp.mo.paydetailprint.v1_0;

import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mo.base.MobileRequest;

/**
 * 打印支付明细查询请求
 */
public class QueryPrintPayDetailRequest extends MobileRequest {

	/** 系统订单号 */
	private String outTradeNo;
	
	/** 支付方式 */
	private int payType;

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

	@Override
	public String toString() {
		return "QueryPrintPayDetailRequest [outTradeNo=" + outTradeNo + ", payType=" + payType + ", " + super.toString() + " ]";
	}

	@Override
	public QueryPrintPayDetailRequest build(String key) {
		try {
			setSignature(Signature.getSign(this, key));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return this;
	}

}
