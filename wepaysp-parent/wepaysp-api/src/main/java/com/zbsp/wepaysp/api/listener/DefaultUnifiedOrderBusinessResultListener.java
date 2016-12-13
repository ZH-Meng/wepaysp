package com.zbsp.wepaysp.api.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tencent.business.UnifiedOrderBusiness;
import com.tencent.protocol.unified_order_protocol.UnifiedOrderResData;
import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.api.util.WeixinPackConverter;

/**
 * 孟郑宏
 */
public class DefaultUnifiedOrderBusinessResultListener implements UnifiedOrderBusiness.ResultListener {
    
    // 日志对象
    protected final Logger logger = LogManager.getLogger(getClass());
    public static final String ON_FAIL_BY_RETURN_CODE_ERROR = "on_fail_by_return_code_error";
    public static final String ON_FAIL_BY_RETURN_CODE_FAIL = "on_fail_by_return_code_fail";
    public static final String ON_FAIL_BY_SIGN_INVALID = "on_fail_by_sign_invalid";

    public static final String ON_FAIL = "on_fail";
    public static final String ON_SUCCESS = "on_success";

    private String result = "";
    private String prepayId = "";// 微信生成的预支付回话标识，用于后续接口调用中使用，该值有效期为2小时

    private WeixinPayDetailsService weixinPayDetailsService;

    public DefaultUnifiedOrderBusinessResultListener(WeixinPayDetailsService weixinPayDetailsService) {
        this.weixinPayDetailsService = weixinPayDetailsService;
    }

    @Override
    /**
     * 遇到这个问题一般是程序没按照API规范去正确地传递参数导致，请仔细阅读API文档里面的字段说明
     */
    public void onFailByReturnCodeError(UnifiedOrderResData unifiedOrderResData) {
        logger.warn("微信统一下单失败：没按照API规范去正确地传递参数，系统订单ID=" + unifiedOrderResData.getOut_trade_no());
        result = ON_FAIL_BY_RETURN_CODE_ERROR;
    }

    /**
     * 同上，遇到这个问题一般是程序没按照API规范去正确地传递参数导致，请仔细阅读API文档里面的字段说明
     */
    @Override
    public void onFailByReturnCodeFail(UnifiedOrderResData unifiedOrderResData) {
        logger.warn("微信统一下单失败：通讯失败，系统订单ID=" + unifiedOrderResData.getOut_trade_no());
        updateOrderResult(unifiedOrderResData);
        result = ON_FAIL_BY_RETURN_CODE_FAIL;
    }

    /**
     * 统一下单请求API返回的数据签名验证失败，有可能数据被篡改了。遇到这种错误建议商户直接告警，做好安全措施
     */
    @Override
    public void onFailBySignInvalid(UnifiedOrderResData unifiedOrderResData) {
        logger.warn("微信统一下单失败：数据签名验证失败，系统订单ID=" + unifiedOrderResData.getOut_trade_no());
        updateOrderResult(unifiedOrderResData);
        result = ON_FAIL_BY_SIGN_INVALID;
    }

    /**
     * 统一下单失败，其他原因导致，这种情况建议把log记录好
     */
    @Override
    public void onFail(UnifiedOrderResData unifiedOrderResData) {
        logger.warn("微信统一下单失败，系统订单ID=" + unifiedOrderResData.getOut_trade_no() +"，错误码：" + unifiedOrderResData.getErr_code() +", 错误描述：" + unifiedOrderResData.getErr_code_des());
        updateOrderResult(unifiedOrderResData);
        result = ON_FAIL;
    }

    /**
     * 恭喜，统一下单成功
     */
    @Override
    public void onSuccess(UnifiedOrderResData unifiedOrderResData) {
        logger.info("微信统一下单成功，预支付标识："+ unifiedOrderResData.getPrepay_id());
        updateOrderResult(unifiedOrderResData);
        result = ON_SUCCESS;
        prepayId = unifiedOrderResData.getPrepay_id();
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId;
    }

    public WeixinPayDetailsService getWeixinPayDetailsService() {
        return weixinPayDetailsService;
    }

    public void setWeixinPayDetailsService(WeixinPayDetailsService weixinPayDetailsService) {
        this.weixinPayDetailsService = weixinPayDetailsService;
    }

    /**
     * 调用service更新交易明细
     * 
     * @param unifiedOrderResData
     */
    private void updateOrderResult(UnifiedOrderResData unifiedOrderResData) {
        weixinPayDetailsService.doTransUpdateOrderResult(unifiedOrderResData.getReturn_code(), unifiedOrderResData.getResult_code(), WeixinPackConverter.unifiedOrderRes2WeixinPayDetailsVO(unifiedOrderResData));
    }

}
