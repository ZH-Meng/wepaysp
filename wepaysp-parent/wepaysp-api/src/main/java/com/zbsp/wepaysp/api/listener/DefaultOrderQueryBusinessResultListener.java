package com.zbsp.wepaysp.api.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tencent.business.OrderQueryBusiness;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryResData;
import com.zbsp.wepaysp.api.service.main.pay.WeixinPayDetailsMainService;
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

    private WeixinPayDetailsMainService weixinPayDetailsMainService;

    public DefaultOrderQueryBusinessResultListener(WeixinPayDetailsMainService weixinPayDetailsMainService) {
        this.weixinPayDetailsMainService = weixinPayDetailsMainService;
    }

    /**
     * 遇到这个问题一般是程序没按照API规范去正确地传递参数导致，请仔细阅读API文档里面的字段说明
     */
    @Override
    public void onFailByReturnCodeError(ScanPayQueryResData orderQueryResData) {
        logger.warn("微信订单查询失败：没按照API规范去正确地传递参数，系统订单ID=" + orderQueryResData.getOut_trade_no());
        result = ON_FAIL_BY_RETURN_CODE_ERROR;
    }

    /**
     * 同上，遇到这个问题一般是程序没按照API规范去正确地传递参数导致，请仔细阅读API文档里面的字段说明
     */
    @Override
    public void onFailByReturnCodeFail(ScanPayQueryResData orderQueryResData) {
        logger.warn("微信订单查询失败：通讯失败，系统订单ID=" + orderQueryResData.getOut_trade_no());
        result = ON_FAIL_BY_RETURN_CODE_FAIL;
    }

    /**
     * 订单查询请求API返回的数据签名验证失败，有可能数据被篡改了。遇到这种错误建议商户直接告警，做好安全措施
     */
    @Override
    public void onFailBySignInvalid(ScanPayQueryResData orderQueryResData) {
        logger.warn("微信订单查询失败：数据签名验证失败，系统订单ID=" + orderQueryResData.getOut_trade_no());
        result = ON_FAIL_BY_SIGN_INVALID;
    }

    /**
     * 订单查询失败，其他原因导致，这种情况建议把log记录好<br>
     * 错误原因：<br> 
     *      ORDERNOTEXIST 此交易订单号不存在；<br> 
     *      SYSTEMERROR 系统错误
     */
    @Override
    public void onOrderQueryFail(ScanPayQueryResData orderQueryResData) {
        logger.warn("微信订单查询失败，，系统订单ID=" + orderQueryResData.getOut_trade_no() +"，错误码：" + orderQueryResData.getErr_code() +", 错误描述：" + orderQueryResData.getErr_code_des());
        weixinPayDetailsMainService.handleOrderQueryResult("FAIL", WeixinPackConverter.orderQueryRes2WeixinPayDetailsVO(orderQueryResData));
        result = ON_ORDER_QUERY_FAIL;
    }

    @Override
    /**
     * 恭喜，订单查询成功
     */
    public void onOrderQuerySuccess(ScanPayQueryResData orderQueryResData) {
        logger.info("微信订单查询成功");
        weixinPayDetailsMainService.handleOrderQueryResult("SUCCESS", WeixinPackConverter.orderQueryRes2WeixinPayDetailsVO(orderQueryResData));
        result = ON_ORDER_QUERY_SUCCESS;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
