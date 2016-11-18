package com.zbsp.wepaysp.api.listener;

import com.tencent.business.RefundBusiness;
import com.tencent.protocol.refund_protocol.RefundResData;

/**
 * User: rizenguo
 * Date: 2014/12/2
 * Time: 18:32
 */
public class DefaultRefundBusinessResultListener implements RefundBusiness.ResultListener {

    public static final String ON_FAIL_BY_RETURN_CODE_ERROR = "on_fail_by_return_code_error";
    public static final String ON_FAIL_BY_RETURN_CODE_FAIL = "on_fail_by_return_code_fail";
    public static final String ON_FAIL_BY_SIGN_INVALID = "on_fail_by_sign_invalid";
    public static final String ON_REFUND_FAIL = "on_refund_fail";
    public static final String ON_REFUND_SUCCESS = "on_refund_success";

    private String result = "";

    @Override
    public void onFailByReturnCodeError(RefundResData refundResData) {
        result = ON_FAIL_BY_RETURN_CODE_ERROR;
    }

    @Override
    public void onFailByReturnCodeFail(RefundResData refundResData) {
        result = ON_FAIL_BY_RETURN_CODE_FAIL;
    }

    @Override
    public void onFailBySignInvalid(RefundResData refundResData) {
        result = ON_FAIL_BY_SIGN_INVALID;
    }

    @Override
    public void onRefundFail(RefundResData refundResData) {
        result = ON_REFUND_FAIL;
    }

    @Override
    public void onRefundSuccess(RefundResData refundResData) {
        result = ON_REFUND_SUCCESS;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
