package com.zbsp.wepaysp.api.listener;


import com.tencent.business.ScanPayBusiness;
import com.tencent.protocol.pay_protocol.ScanPayResData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryResData;
import com.tencent.protocol.reverse_protocol.ReverseResData;
import com.zbsp.wepaysp.common.constant.EnumDefine.ReturnCode;
import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.api.util.WeixinPackConverter;


/**
 * User: rizenguo
 * Date: 2014/12/2
 * Time: 10:41
 *
 */
public class DefaultScanPayBusinessResultListener implements ScanPayBusiness.ResultListener{

    public static final String ON_FAIL_BY_RETURN_CODE_ERROR = "on_fail_by_return_code_error";
    public static final String ON_FAIL_BY_RETURN_CODE_FAIL = "on_fail_by_return_code_fail";
    public static final String ON_FAIL_BY_SIGN_INVALID = "on_fail_by_sign_invalid";

    public static final String ON_FAIL_BY_QUERY_SIGN_INVALID = "on_fail_by_query_sign_invalid";
    public static final String ON_FAIL_BY_REVERSE_SIGN_INVALID = "on_fail_by_query_service_sign_invalid";

    public static final String ON_FAIL_BY_AUTH_CODE_EXPIRE = "on_fail_by_auth_code_expire";
    public static final String ON_FAIL_BY_AUTH_CODE_INVALID = "on_fail_by_auth_code_invalid";
    public static final String ON_FAIL_BY_MONEY_NOT_ENOUGH = "on_fail_by_money_not_enough";



    public static final String ON_FAIL = "on_fail";
    public static final String ON_SUCCESS = "on_success";

    private String result = "";
    private String transcationID = "";
    
    private WeixinPayDetailsService weixinPayDetailsService;
    public DefaultScanPayBusinessResultListener(WeixinPayDetailsService weixinPayDetailsService) {
    	this.weixinPayDetailsService = weixinPayDetailsService;
	}

    @Override
    /**
     * 遇到这个问题一般是程序没按照API规范去正确地传递参数导致，请仔细阅读API文档里面的字段说明
     */
    public void onFailByReturnCodeError(ScanPayResData scanPayResData) {
        result = ON_FAIL_BY_RETURN_CODE_ERROR;
    }

    @Override
    /**
     * 同上，遇到这个问题一般是程序没按照API规范去正确地传递参数导致，请仔细阅读API文档里面的字段说明
     */
    public void onFailByReturnCodeFail(ScanPayResData scanPayResData) {
        result = ON_FAIL_BY_RETURN_CODE_FAIL;
    }

    @Override
    /**
     * 支付请求API返回的数据签名验证失败，有可能数据被篡改了。遇到这种错误建议商户直接告警，做好安全措施
     */
    public void onFailBySignInvalid(ScanPayResData scanPayResData) {
        result = ON_FAIL_BY_SIGN_INVALID;
    }

    @Override
    public void onFailByQuerySignInvalid(ScanPayQueryResData scanPayQueryResData) {
        result = ON_FAIL_BY_QUERY_SIGN_INVALID;
    }

    @Override
    public void onFailByReverseSignInvalid(ReverseResData reverseResData) {
        result = ON_FAIL_BY_REVERSE_SIGN_INVALID;
    }

    @Override
    /**
     * 用户用来支付的二维码已经过期，提示收银员重新扫一下用户微信“刷卡”里面的二维码"
     */
    public void onFailByAuthCodeExpire(ScanPayResData scanPayResData) {
        updatePayResult(scanPayResData);
        result = ON_FAIL_BY_AUTH_CODE_EXPIRE;
    }

    @Override
    /**
     * 授权码无效，提示用户刷新一维码/二维码，之后重新扫码支付
     */
    public void onFailByAuthCodeInvalid(ScanPayResData scanPayResData) {
        updatePayResult(scanPayResData);
        result = ON_FAIL_BY_AUTH_CODE_INVALID;
    }

    @Override
    /**
     * 支付失败，其他原因导致，这种情况建议把log记录好
     */
    public void onFail(ScanPayResData scanPayResData) {
        updatePayResult(scanPayResData);
        result = ON_FAIL;
    }

    @Override
    /**
     * 用户余额不足，换其他卡支付或是用现金支付
     */
    public void onFailByMoneyNotEnough(ScanPayResData scanPayResData) {
        updatePayResult(scanPayResData);
        result = ON_FAIL_BY_MONEY_NOT_ENOUGH;
    }

    @Override
    /**
     * 恭喜，支付成功，请返回成功结果
     */
    public void onSuccess(ScanPayResData scanPayResData, String transID) {
        updatePayResult(scanPayResData);
        result = ON_SUCCESS;
        transcationID = transID;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTranscationID() {
        return transcationID;
    }

    public void setTranscationID(String transcationID) {
        this.transcationID = transcationID;
    }
    
	public WeixinPayDetailsService getWeixinPayDetailsService() {
		return weixinPayDetailsService;
	}

	public void setWeixinPayDetailsService(WeixinPayDetailsService weixinPayDetailsService) {
		this.weixinPayDetailsService = weixinPayDetailsService;
	}
	
    /**
     * 调用service更新交易明细
     * @param scanPayResData
     */
    private void updatePayResult(ScanPayResData scanPayResData) {
        if (ReturnCode.SUCCESS.toString().equals(scanPayResData.getReturn_code())) {
            weixinPayDetailsService.doTransUpdatePayResult(scanPayResData.getResult_code(), scanPayResData.getErr_code(), WeixinPackConverter.scanPayRes2WeixinPayDetailsVO(scanPayResData));
        }
    }
    
}
