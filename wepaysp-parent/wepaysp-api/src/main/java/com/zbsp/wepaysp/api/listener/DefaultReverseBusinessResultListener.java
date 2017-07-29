package com.zbsp.wepaysp.api.listener;

import com.tencent.business.ReverseBusiness;
import com.tencent.protocol.reverse_protocol.ReverseResData;
import com.zbsp.wepaysp.api.service.main.pay.WeixinRefundDetailsMainService;
import com.zbsp.wepaysp.api.util.WeixinPackConverter;
import com.zbsp.wepaysp.vo.pay.WeixinRefundDetailsVO;

public class DefaultReverseBusinessResultListener implements ReverseBusiness.ResultListener {

    public static final String ON_FAIL_BY_RETURN_CODE_ERROR = "on_fail_by_return_code_error";
    public static final String ON_FAIL_BY_RETURN_CODE_FAIL = "on_fail_by_return_code_fail";
    public static final String ON_FAIL_BY_SIGN_INVALID = "on_fail_by_sign_invalid";
    public static final String ON_REVERSE_FAIL = "on_refund_fail";
    public static final String ON_REVERSE_SUCCESS = "on_refund_success";

    private String result = "";

    private WeixinRefundDetailsMainService weixinRefundDetailsMainService;

    public DefaultReverseBusinessResultListener(WeixinRefundDetailsMainService weixinRefundDetailsMainService) {
        this.weixinRefundDetailsMainService = weixinRefundDetailsMainService;
    }
    
    @Override
    public void onFailByReturnCodeError(ReverseResData reverseResData) {
        result = ON_FAIL_BY_RETURN_CODE_ERROR;
    }

    @Override
    public void onFailByReturnCodeFail(ReverseResData reverseResData) {
        result = ON_FAIL_BY_RETURN_CODE_FAIL;
    }

    @Override
    public void onFailByReverseSignInvalid(ReverseResData reverseResData) {
        result = ON_FAIL_BY_SIGN_INVALID;
    }

    @Override
    public void onFail(ReverseResData reverseResData, String outTradeNo) {
        result = ON_REVERSE_FAIL;
        WeixinRefundDetailsVO refundVO = WeixinPackConverter.reverseRes2WeixinRefundDetailsVO(reverseResData);
        refundVO.setOutTradeNo(outTradeNo);
        weixinRefundDetailsMainService.reverseResult(refundVO);
    }

    @Override
    public void onSuccess(ReverseResData reverseResData, String outTradeNo) {
        result = ON_REVERSE_SUCCESS;
        WeixinRefundDetailsVO refundVO = WeixinPackConverter.reverseRes2WeixinRefundDetailsVO(reverseResData);
        refundVO.setOutTradeNo(outTradeNo);
        weixinRefundDetailsMainService.reverseResult(refundVO);
    }
    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }
    
}
