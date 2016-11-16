package com.zbsp.wepaysp.manage.web.listener;

import com.tencent.business.DownloadBillBusiness;
import com.tencent.protocol.downloadbill_protocol.DownloadBillResData;

/**
 * User: rizenguo
 * Date: 2014/12/3
 * Time: 10:42
 */
public class DefaultDownloadBillBusinessResultListener implements DownloadBillBusiness.ResultListener {

    public static final String ON_FAIL_BY_RETURN_CODE_ERROR = "on_fail_by_return_code_error";
    public static final String ON_FAIL_BY_RETURN_CODE_FAIL = "on_fail_by_return_code_fail";
    public static final String ON_DOWNLOAD_BILL_FAIL = "on_download_bill_fail";
    public static final String ON_DOWNLOAD_BILL_SUCCESS = "on_download_bill_success";

    private String result = "";

    @Override
    public void onFailByReturnCodeError(DownloadBillResData downloadBillResData) {
        result = ON_FAIL_BY_RETURN_CODE_ERROR;
    }

    @Override
    public void onFailByReturnCodeFail(DownloadBillResData downloadBillResData) {
        result = ON_FAIL_BY_RETURN_CODE_FAIL;
    }

    @Override
    public void onDownloadBillFail(String response) {
        result = ON_DOWNLOAD_BILL_FAIL;
    }

    @Override
    public void onDownloadBillSuccess(String response) {
        result = ON_DOWNLOAD_BILL_SUCCESS;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

}
