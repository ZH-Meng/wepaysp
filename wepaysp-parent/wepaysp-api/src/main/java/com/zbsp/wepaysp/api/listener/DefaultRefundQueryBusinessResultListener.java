package com.zbsp.wepaysp.api.listener;

import com.tencent.business.RefundQueryBusiness;
import com.tencent.protocol.refund_query_protocol.RefundQueryResData;


/**
 * User: rizenguo
 * Date: 2014/12/2
 * Time: 19:08
 */
public class DefaultRefundQueryBusinessResultListener implements RefundQueryBusiness.ResultListener {


    public static final String ON_FAIL_BY_RETURN_CODE_ERROR = "on_fail_by_return_code_error";
    public static final String ON_FAIL_BY_RETURN_CODE_FAIL = "on_fail_by_return_code_fail";
    public static final String ON_FAIL_BY_SIGN_INVALID = "on_fail_by_sign_invalid";
    public static final String ON_REFUND_QUERY_FAIL = "on_refund_query_fail";
    public static final String ON_REFUND_QUERY_SUCCESS = "on_refund_query_success";

    private String result = "";

    @Override
    public void onFailByReturnCodeError(RefundQueryResData refundQueryResData) {
        result = ON_FAIL_BY_RETURN_CODE_ERROR;
    }

    @Override
    public void onFailByReturnCodeFail(RefundQueryResData refundQueryResData) {
        result = ON_FAIL_BY_RETURN_CODE_FAIL;
    }

    @Override
    public void onFailBySignInvalid(RefundQueryResData refundQueryResData) {
        result = ON_FAIL_BY_SIGN_INVALID;
    }

    @Override
    public void onRefundQueryFail(RefundQueryResData refundQueryResData) {
        result = ON_REFUND_QUERY_FAIL;
    }

    @Override
    public void onRefundQuerySuccess(RefundQueryResData refundQueryResData) {
        result = ON_REFUND_QUERY_SUCCESS;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
