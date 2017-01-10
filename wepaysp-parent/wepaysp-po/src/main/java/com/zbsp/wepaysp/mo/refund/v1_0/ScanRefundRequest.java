package com.zbsp.wepaysp.mo.refund.v1_0;

import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mo.base.MobileRequest;

/**
 * 扫码退款请求
 */
public class ScanRefundRequest extends MobileRequest {

	/** 商户订单号 */
	private String outTradeNo;

	/** 退款金额，单位为分 */
	private String refundMoney;

    public String getOutTradeNo() {
        return outTradeNo;
    }
    
    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }
    
    public String getRefundMoney() {
        return refundMoney;
    }
    
    public void setRefundMoney(String refundMoney) {
        this.refundMoney = refundMoney;
    }

    @Override
	public String toString() {
		return "ScanPayRequest [outTradeNo=" + outTradeNo + ", refundMoney=" + refundMoney + ", " + super.toString() + " ]";
	}

	@Override
	public ScanRefundRequest build(String key) {
		try {
			setSignature(Signature.getSign(this, key));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return this;
	}

}
