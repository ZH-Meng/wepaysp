package com.zbsp.wepaysp.api.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tencent.business.OrderQueryBusiness;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryResData;
import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.api.util.WeixinPackConverter;

/**
 * 孟郑宏
 */
public class DefaultOrderQueryBusinessResultListener implements OrderQueryBusiness.ResultListener {
    
    // 日志对象
    protected final Logger logger = LogManager.getLogger(getClass());
    public static final String ON_FAIL_BY_RETURN_CODE_ERROR = "on_fail_by_return_code_error";
    public static final String ON_FAIL_BY_RETURN_CODE_FAIL = "on_fail_by_return_code_fail";
    public static final String ON_FAIL_BY_SIGN_INVALID = "on_fail_by_sign_invalid";

    public static final String ON_ORDER_QUERY_FAIL = "on_order_query_fail";
    public static final String ON_ORDER_QUERY_SUCCESS = "on_order_query_success";

    private String result = "";

    private WeixinPayDetailsService weixinPayDetailsService;

    public DefaultOrderQueryBusinessResultListener(WeixinPayDetailsService weixinPayDetailsService) {
        this.weixinPayDetailsService = weixinPayDetailsService;
    }

    @Override
    /**
     * 遇到这个问题一般是程序没按照API规范去正确地传递参数导致，请仔细阅读API文档里面的字段说明
     */
    public void onFailByReturnCodeError(ScanPayQueryResData orderQueryResData) {
        logger.warn("微信订单查询失败：没按照API规范去正确地传递参数");
        result = ON_FAIL_BY_RETURN_CODE_ERROR;
    }

    @Override
    /**
     * 同上，遇到这个问题一般是程序没按照API规范去正确地传递参数导致，请仔细阅读API文档里面的字段说明
     */
    public void onFailByReturnCodeFail(ScanPayQueryResData orderQueryResData) {
        logger.warn("微信订单查询失败：微信下单通讯失败");
        result = ON_FAIL_BY_RETURN_CODE_FAIL;
    }

    @Override
    /**
     * 订单查询请求API返回的数据签名验证失败，有可能数据被篡改了。遇到这种错误建议商户直接告警，做好安全措施
     */
    public void onFailBySignInvalid(ScanPayQueryResData orderQueryResData) {
        logger.warn("微信订单查询失败：数据签名验证失败");        
        result = ON_FAIL_BY_SIGN_INVALID;
    }

    @Override
    /**
     * 订单查询失败，其他原因导致，这种情况建议把log记录好
     */
    public void onOrderQueryFail(ScanPayQueryResData orderQueryResData) {
        logger.warn("微信订单查询失败，错误码：" + orderQueryResData.getErr_code() +", 错误描述：" + orderQueryResData.getErr_code_des());        
        result = ON_ORDER_QUERY_FAIL;
    }

    @Override
    /**
     * 恭喜，订单查询成功，请返回成功结果
     */
    public void onOrderQuerySuccess(ScanPayQueryResData orderQueryResData) {
        logger.info("微信订单查询成功");
        updateOrderQueryResult(orderQueryResData);
        result = ON_ORDER_QUERY_SUCCESS;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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
     * @param orderQueryResData
     */
    private void updateOrderQueryResult(ScanPayQueryResData orderQueryResData) {
        weixinPayDetailsService.doTransUpdateOrderQueryResult(WeixinPackConverter.orderQueryRes2WeixinPayDetailsVO(orderQueryResData));
    }

}
