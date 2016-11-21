package com.zbsp.wepaysp.api.service.main.pay;

import java.util.Map;

import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;

public interface WeixinRefundDetailsMainService {

	
    /**
     * 执行过程：
     * <pre>
     *      根据系统支付订单ID查找支付明细（交易订单）；
     *      创建退款明细；
     *      调用WxPay API（创建撤销支付请求包、invoke API）；
     *      更新退款结果到db；
     *      更新支付明细中退款信息；
     *      响应退款结果；
     * <pre>
     * @param weixinPayDetailsVO 要退款的支付明细对象 
     * @param creator 操作人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 退款返回保存Map集合
     */
    public Map<String, Object> cashierDeskRefund(WeixinPayDetailsVO weixinPayDetailsVO, String creator, String operatorUserOid, String logFunctionOid);

}
