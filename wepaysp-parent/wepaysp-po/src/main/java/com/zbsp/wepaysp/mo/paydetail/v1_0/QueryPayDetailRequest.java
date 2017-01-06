package com.zbsp.wepaysp.mo.paydetail.v1_0;

import com.zbsp.wepaysp.common.security.Signature;
import com.zbsp.wepaysp.mo.base.MobileRequest;

/**
 * 支付明细查询请求 
 */
public class QueryPayDetailRequest extends MobileRequest {
	
	/**收银员Oid*/
	private String dealerEmployeeOid;
	
	/**查询方式：1：收款查询，2：账单查询*/
	private int queryType;
	
	/** 商户订单号 */
	private String outTradeNo;
	
	/**支付订单号 */
	private String transactionId;
	
	/** 查询开始时间 */
	private String beginTime;
	
	/** 查询截止时间 */
	private String endTime;
	
	/**支付方式*/
	private String payType;
	
	/**订单状态*/
	private String tradeStatus;
	
	/** 页码 */
	private int pageNum;
	
	/** 每页行数 */
	private String pageSize;
	
	public static enum QueryType {
		/**收款查询*/
		collection(1),
		/**账单查询*/
		bill(2);
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

	public String getOutTradeNo() {
		return outTradeNo;
	}

	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
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

    public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	
    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    @Override
	public String toString() {
		return "QueryPayDetailRequest [dealerEmployeeOid=" + dealerEmployeeOid + ", queryType=" + queryType
				+ ", outTradeNo=" + outTradeNo + ", transactionId=" + transactionId + ", beginTime=" + beginTime + ", endTime=" 
				+ endTime + ", payType=" + payType + ", tradeStatus=" + tradeStatus + ", pageNum=" + pageNum + ", pageSize=" + pageSize + ", " + super.toString() +" ]";
	}

	@Override
    public QueryPayDetailRequest build(String key) {
	    try {
            setSignature(Signature.getSign(this, key));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
	}
	
}
