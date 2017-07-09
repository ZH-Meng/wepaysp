package com.zbsp.wepaysp.api.service.pay;

import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.vo.pay.WeixinRefundDetailsVO;

/**
 * 微信官方交易明细service
 */
public interface WeixinBillService {
    
    /**
     * 保存下载的交易明细.
     * 
     * @param bills 下载成功的交易明细
     */
    public void doTransSaveBill(String[] bills);    
 
    
    /**
     * 创建退款明细
     * @param weixinPayDetails 要退款的支付订单
     * @param creator 操作名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 保存成功返回保存后的退款明细VO
     */
    public WeixinRefundDetailsVO doTransCreateRefundDetails(WeixinPayDetails weixinPayDetails, String creator, String operatorUserOid, String logFunctionOid);

    
}
