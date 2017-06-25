package com.zbsp.wepaysp.api.service.main.pay;

import java.util.Map;

import com.zbsp.wepaysp.vo.pay.AliPayDetailsVO;

/***
 * 支付宝交易（支付）明细主服务（无事务）
 * 
 * @author 孟郑宏
 */
public interface AliPayDetailsMainService {

    /**
     * 面对面条码支付
     * 
     * <pre>
     * 生成支付宝支付明细；
     * 调用支付宝当面付支付接口；
     * 同步更新支付结果；
     * 组装支付结果响应返回；
     * </pre>
     * 
     * @param payDetailsVO 当面付-条码支付参数
     *            
     * @return 
     */
    public Map<String, Object> face2FaceBarPay(AliPayDetailsVO payDetailsVO);

    /**
     * 手机网站支付下单
     * @param payDetailsVO 下单参数
     * @return
     */
    public Map<String, Object> wapPayCreateOrder(AliPayDetailsVO payDetailsVO);

    /**
     * 手机网站支付前台回跳查询交易结果
     * @param outTradeNo 系统订单ID
     * @param tradeNo 支付宝交易ID
     * @param totalAmount 订单总金额
     * @param sellerId 收款帐号
     * @return Map Key:Value
     *  <pre>
     *      tradeStatus：交易状态
     *      aliPayDetailsVO：交易信息（包含商户、门店、金额、订单号、交易时间等）
     *  </pre>
     */
    public Map<String, Object> h5ReturnQueryPayResult(String outTradeNo, String tradeNo, String totalAmount, String sellerId);

    /**
     * 处理支付结果异步通知
     * @param paramMap 通知请求参数
     * @return 
     * @return Map Key:Value
     *  <pre>
     *      result：success表示处理成功，可直接响应给支付宝；其他均标识处理失败
     *  </pre>
     */
    public Map<String, Object> handleAsynNotify(Map<String, String> paramMap);
    
    /**
     * 扫码支付下单
     * @param payDetailsVO 下单参数
     * @return
     */
	public Map<String, Object> scanPayCreateOrder(AliPayDetailsVO payDetailsVO);

}
