package com.zbsp.wepaysp.api.service.main.pay;

import java.util.Map;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;

/**
 * 微信交易明细service-不传播事务
 */
public interface WeixinPayDetailsMainService {

    /**
     * 执行过程：
     * <pre>
     *      创建支付明细（交易订单）；
     *      调用WxPay API；
     *      更新支付结果到db；
     *      响应支付结果；
     * <pre>
     * @param weixinPayDetailsVO 要保存的支付明细对象 
     * @param creator 创建人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 保存成功返回保存后的支付明细VO
     * @throws AlreadyExistsException 如果支付明细已存在
     */
    public Map<String, Object> createPayAndInvokeWxPay(WeixinPayDetailsVO weixinPayDetailsVO, String creator, String operatorUserOid, String logFunctionOid);

}
