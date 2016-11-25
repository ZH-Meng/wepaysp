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

    /**
     * 处理微信支付结果通知
     * 
     * @param respXmlString 结果通知XML 字符串
     * @return 系统处理结果 XML 字串
     */
    public String handleWxPayNotify(String respXmlString);

    /**
     * 检查微信公众号支付结果
     * 
     * @param jsPayResult 检查的结果，jsPayResult = ok 才会与db中结果检查
     * @param weixinPayDetailOid 支付明细Oid
     * @return 
     */
    public Map<String, Object> checkPayResult(String jsPayResult, String weixinPayDetailOid);

}
