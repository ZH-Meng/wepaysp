package com.zbsp.wepaysp.mo.paystat.v1_0;

import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mo.base.MobileRequest;

/**
 * 统计结算查询请求 
 */
public class QueryPayStatRequest extends MobileRequest {
	
	/**收银员Oid*/
	private String dealerEmployeeOid;
	
	/**查询类型：1：当班结算；2：分类统计*/
	private int queryType;
	
	/** 查询开始时间 */
	private String beginTime;
	
	/** 查询截止时间 */
	private String endTime;
	
	public static enum QueryType {
		/**当班结算*/
		ON_DUTY_STAT(1),
		/**分类统计*/
		CLASS_STAT(2);
		private int value;
		
		private QueryType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
	
	public String getDealerEmployeeOid() {
		return dealerEmployeeOid;
	}

	public void setDealerEmployeeOid(String dealerEmployeeOid) {
		this.dealerEmployeeOid = dealerEmployeeOid;
	}

	public int getQueryType() {
		return queryType;
	}

	public void setQueryType(int queryType) {
		this.queryType = queryType;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

    @Override
	public String toString() {
		return "QueryPayDetailRequest [dealerEmployeeOid=" + dealerEmployeeOid + ", queryType=" + queryType
				+ ", beginTime=" + beginTime + ", endTime=" + endTime + ", " + super.toString() +" ]"; 
	}

	@Override
    public QueryPayStatRequest build(String key) {
	    try {
            setSignature(Signature.getSign(this, key));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
	}
	
}
