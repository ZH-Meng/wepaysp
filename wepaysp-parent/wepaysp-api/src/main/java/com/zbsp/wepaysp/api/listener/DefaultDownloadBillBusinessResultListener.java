package com.zbsp.wepaysp.api.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tencent.business.DownloadBillBusiness;
import com.tencent.protocol.downloadbill_protocol.DownloadBillResData;
import com.zbsp.wepaysp.api.service.pay.WeixinBillService;

/**
 * User: rizenguo
 * Date: 2014/12/3
 * Time: 10:42
 */
public class DefaultDownloadBillBusinessResultListener implements DownloadBillBusiness.ResultListener {
	
	protected Logger logger = LogManager.getLogger(getClass());
	
    public static final String ON_FAIL_BY_RETURN_CODE_ERROR = "on_fail_by_return_code_error";
    public static final String ON_FAIL_BY_RETURN_CODE_FAIL = "on_fail_by_return_code_fail";
    public static final String ON_DOWNLOAD_BILL_FAIL = "on_download_bill_fail";
    public static final String ON_DOWNLOAD_BILL_SUCCESS = "on_download_bill_success";
    
    private WeixinBillService weixinBillService = null;
    
    public DefaultDownloadBillBusinessResultListener(WeixinBillService weixinBillService) {
        this.weixinBillService = weixinBillService;
    }

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
		
		response = response.replace("`", "");

		String[] bills = response.split("\\n");
        //下载对账单成功后保存明细、更新原始交易信息的退款字段并生成退款记录
		weixinBillService.doTransSaveBill(bills);

	}

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

	public void setWeixinBillService(WeixinBillService weixinBillService) {
		this.weixinBillService = weixinBillService;
	}

}
