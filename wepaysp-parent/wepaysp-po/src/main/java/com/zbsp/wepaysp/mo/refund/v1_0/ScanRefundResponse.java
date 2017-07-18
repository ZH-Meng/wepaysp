package com.zbsp.wepaysp.mo.refund.v1_0;

import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mo.base.MobileResponse;

/**
 * 扫码退款响应
 */
public class ScanRefundResponse extends MobileResponse {
	
    public ScanRefundResponse() {}

	public ScanRefundResponse(int result, String message, String responseId) {
		super(result, message, responseId);
	}

	@Override
	public String toString() {
		return "ScanPayResponse [" + super.toString() + " ]";
	}

	@Override
    public ScanRefundResponse build(String key) {
        try {
            setSignature(Signature.getSign(this, key));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

}
