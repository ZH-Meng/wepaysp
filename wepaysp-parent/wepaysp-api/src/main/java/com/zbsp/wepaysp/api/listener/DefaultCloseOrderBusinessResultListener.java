package com.zbsp.wepaysp.api.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tencent.business.CloseOrderBusiness;
import com.tencent.protocol.close_order_protocol.CloseOrderResData;
import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.api.util.WeixinPackConverter;


/**
 * 孟郑宏
 */
public class DefaultCloseOrderBusinessResultListener implements CloseOrderBusiness.ResultListener {

    // 日志对象
    protected final Logger logger = LogManager.getLogger(getClass());
    public static final String ON_FAIL_BY_RETURN_CODE_ERROR = "on_fail_by_return_code_error";
    public static final String ON_FAIL_BY_RETURN_CODE_FAIL = "on_fail_by_return_code_fail";
    public static final String ON_FAIL_BY_SIGN_INVALID = "on_fail_by_sign_invalid";

    public static final String ON_CLOSE_ORDER_FAIL = "on_close_order_fail";
    public static final String ON_CLOSE_ORDER_SUCCESS = "on_close_order_success";

    private String result = "";

    private WeixinPayDetailsService weixinPayDetailsService;

    public DefaultCloseOrderBusinessResultListener(WeixinPayDetailsService weixinPayDetailsService) {
        this.weixinPayDetailsService = weixinPayDetailsService;
    }

    /**
     * 遇到这个问题一般是程序没按照API规范去正确地传递参数导致，请仔细阅读API文档里面的字段说明
     */
    @Override
    public void onFailByReturnCodeError(CloseOrderResData closeOrderResData) {
        logger.warn("微信关闭订单失败：没按照API规范去正确地传递参数，系统订单ID=" + closeOrderResData.getOut_trade_no());
        result = ON_FAIL_BY_RETURN_CODE_ERROR;
    }

    /**
     * 同上，遇到这个问题一般是程序没按照API规范去正确地传递参数导致，请仔细阅读API文档里面的字段说明
     */
    @Override
    public void onFailByReturnCodeFail(CloseOrderResData closeOrderResData) {
        logger.warn("微信关闭订单失败：通讯失败，系统订单ID=" + closeOrderResData.getOut_trade_no());
        result = ON_FAIL_BY_RETURN_CODE_FAIL;
    }

    /**
     * 关闭订单请求API返回的数据签名验证失败，有可能数据被篡改了。遇到这种错误建议商户直接告警，做好安全措施
     */
    @Override
    public void onFailBySignInvalid(CloseOrderResData closeOrderResData) {
        logger.warn("微信关闭订单失败：数据签名验证失败，系统订单ID=" + closeOrderResData.getOut_trade_no());
        result = ON_FAIL_BY_SIGN_INVALID;
    }

    @Override
    /**
     * 关闭订单失败，其他原因导致，这种情况建议把log记录好
     */
    public void onCloseOrderFail(CloseOrderResData closeOrderResData) {
        logger.warn("微信关闭订单失败，，系统订单ID=" + closeOrderResData.getOut_trade_no() +"，错误码：" + closeOrderResData.getErr_code() +", 错误描述：" + closeOrderResData.getErr_code_des());
        weixinPayDetailsService.doTransUpdateOrderCloseResult("FAIL", WeixinPackConverter.closeOrderRes2WeixinPayDetailsVO(closeOrderResData));
        result = ON_CLOSE_ORDER_FAIL;
    }

    @Override
    /**
     * 恭喜，关闭订单成功
     */
    public void onCloseOrderSuccess(CloseOrderResData closeOrderResData) {
        logger.info("微信关闭订单成功");
        weixinPayDetailsService.doTransUpdateOrderCloseResult("SUCCESS", WeixinPackConverter.closeOrderRes2WeixinPayDetailsVO(closeOrderResData));
        result = ON_CLOSE_ORDER_SUCCESS;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
    

}
