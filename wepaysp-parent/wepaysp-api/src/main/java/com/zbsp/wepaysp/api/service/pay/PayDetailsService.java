package com.zbsp.wepaysp.api.service.pay;

import java.util.Map;

import com.zbsp.wepaysp.mo.paydetail.v1_0.QueryPayDetailResponse;
import com.zbsp.wepaysp.mo.paydetailprint.v1_0.QueryPrintPayDetailResponse;

/***
 * 支付明细service
 */
public interface PayDetailsService {
	
    /**
     * 根据收银员Oid，Map参数查询支付明细并组装QueryPayDetailResponse响应
     * 
     * @param dealerEmployeeOid 收银员Oid
     * @param paramMap  查询参数Map中key的取值如下：
     * <pre>
     *      dealerEmployeeOid:         String类型，收银员Oid，根据此参数精确查询
     *      transactionId:                   String类型，业务员Id，根据此参数模糊查询
     *      dealerId:                          String类型，商家ID，根据此参数模糊查询
     *      dealerEmployeeId:            String类型，收银员ID，根据此参数模糊查询
     *      storeId:                            String类型，门店ID，根据此参数模糊查询
     *      beginTime:                        Date类型，交易开始时间，根据此参数模糊查询
     *      endTime:                          Date类型，交易截止时间，根据此参数模糊查询
     *      payType:                          支付类型，根据此参数模糊查询
     * </pre>
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @param pageNum 页码
     * @return QueryPayDetailResponse
     */
    public QueryPayDetailResponse doJoinTransQueryPayDetails(String dealerEmployeeOid, Map<String, Object> paramMap, int startIndex, int maxResult) throws IllegalArgumentException;

	
    /**
     * 根据商户订单号查找支付成功明细
     * 
     * @param outTradeNo 商户订单号
     * @param payType 支付类型
     * @return QueryPrintPayDetailResponse
     */
	public QueryPrintPayDetailResponse doJoinTransQueryPaySuccessDetail(String outTradeNo, int payType);
}
