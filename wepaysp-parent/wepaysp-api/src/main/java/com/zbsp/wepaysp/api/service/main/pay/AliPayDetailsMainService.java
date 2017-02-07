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

}
