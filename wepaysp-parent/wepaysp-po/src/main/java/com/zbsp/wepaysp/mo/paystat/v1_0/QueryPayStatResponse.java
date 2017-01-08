package com.zbsp.wepaysp.mo.paystat.v1_0;

import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mo.base.MobileResponse;

/**
 * 统计结算查询响应
 */
public class QueryPayStatResponse extends MobileResponse {
	
	/** 支付结算 集合 List<PayStatData> 的 JSON串*/
	private String payStatListJSON;
	
    public QueryPayStatResponse() {}

	public QueryPayStatResponse(int result, String message, String responseId) {
		super(result, message, responseId);
	}

    public String getPayStatListJSON() {
		return payStatListJSON;
	}

	public void setPayStatListJSON(String payStatListJSON) {
		this.payStatListJSON = payStatListJSON;
	}

	@Override
	public String toString() {
		return "QueryPayDetailResponse [payStatListJSON=" + payStatListJSON + ", " + super.toString() + " ]";
	}

	@Override
    public QueryPayStatResponse build(String key) {
        try {
            setSignature(Signature.getSign(this, key));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

}
