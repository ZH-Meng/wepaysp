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
     * @param payResult H5支付结果，如果系统订单为非处理中，直接返回；反之，如果payResult=cancel，直接取消订单（状态为待关闭），payResult =其他，调查询订单接口
     * @param weixinPayDetailOid 支付明细Oid
     * @return Map
     * <pre>         
     *  tradeStatus 交易状态
     *  weixinPayDetailsVO 交易明细
     * <pre>         
     */
    public Map<String, Object> checkPayResult(String payResult, String weixinPayDetailOid);

    /**
     * 处理订单查询结果
     * @param resultCode 业务结果码
     * @param queryResultVO 查询结果封装的weixinPayDetailsVO
     */
    public void handleOrderQueryResult(String resultCode, WeixinPayDetailsVO queryResultVO);

    /**
     * 更新扫码支付结果，如果支付成功，向收银员发送支付成功通知
     * 
     * @param returnCode 业务结果 SUCCESS/FAIL，FAIL时错误信息详见errCode
     * @param resultCode 业务结果码
     * @param payResultVO 封装的支付结果VO
     */
	public void updateScanPayResult(String returnCode, String resultCode, WeixinPayDetailsVO payResultVO);

}
