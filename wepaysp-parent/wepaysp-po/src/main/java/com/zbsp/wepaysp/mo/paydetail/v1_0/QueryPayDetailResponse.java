package com.zbsp.wepaysp.mo.paydetail.v1_0;

import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mo.base.MobileResponse;

/**
 * 支付明细查询响应
 */
public class QueryPayDetailResponse extends MobileResponse {
	
	/** 支付明细 集合 List<PayData> 的 JSON串*/
	private String payDetailListJSON;
	
	/** 金额合计 */
	private long totalMoney;
	
	/** 笔数合计 */
	private long totalAmount;
	
    public QueryPayDetailResponse() {}

	public QueryPayDetailResponse(int result, String message, String responseId) {
		super(result, message, responseId);
	}
	
    public String getPayDetailListJSON() {
		return payDetailListJSON;
	}

	public void setPayDetailListJSON(String payDetailListJSON) {
		this.payDetailListJSON = payDetailListJSON;
	}

	public long getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(long totalMoney) {
		this.totalMoney = totalMoney;
	}
	
    public long getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(long totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
	public String toString() {
		return "QueryPayDetailResponse [totalMoney=" + totalMoney + ", totalAmount=" + totalAmount + ", payDetailListJSON=" + payDetailListJSON + ", " + super.toString() + " ]";
	}

	@Override
    public QueryPayDetailResponse build(String key) {
        try {
            setSignature(Signature.getSign(this, key));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }

}
