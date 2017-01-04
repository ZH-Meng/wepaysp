package com.zbsp.wepaysp.mobile.model.paydetail.v1_0;

import java.util.List;

import com.zbsp.wepaysp.mobile.common.Signature;
import com.zbsp.wepaysp.mobile.model.base.MobileResponse;

/**
 * 支付明细查询响应
 */
public class QueryPayDetailResponse extends MobileResponse {
	
	/** 支付明细 集合*/
	private List<PayData> payDetailList;
	
	/** 金额合计 */
	private long totalMoney;
	
	/** 笔数合计 */
	private long totalCount;
	
    public QueryPayDetailResponse() {}

	public QueryPayDetailResponse(int result, String message, String responseId) {
		super(result, message, responseId);
	}

	public List<PayData> getPayDetailList() {
		return payDetailList;
	}

	public void setPayDetailList(List<PayData> payDetailList) {
		this.payDetailList = payDetailList;
	}

	public long getTotalMoney() {
		return totalMoney;
	}

	public void setTotalMoney(long totalMoney) {
		this.totalMoney = totalMoney;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	@Override
	public String toString() {
		return "QueryPayDetailResponse [totalMoney=" + totalMoney + ", totalCount=" + totalCount + "]";
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
