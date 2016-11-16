package com.zbsp.wepaysp.manage.web.listener;

import org.apache.commons.lang3.StringUtils;

import com.tencent.business.ScanPayBusiness;
import com.tencent.protocol.pay_protocol.ScanPayResData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryResData;
import com.tencent.protocol.reverse_protocol.ReverseResData;
import com.zbsp.wepaysp.common.constant.EnumDefine.ResultCode;
import com.zbsp.wepaysp.common.constant.EnumDefine.ReturnCode;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;


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
    
    /**
     * 调用service更新交易明细
     * @param scanPayResData
     */
    private void updatePayResult(ScanPayResData scanPayResData) {
        if (ReturnCode.SUCCESS.toString().equals(scanPayResData.getReturn_code())) {
            weixinPayDetailsService.doTransUpdatePayResult(scanPayResData.getResult_code(), scanPayResData.getErr_code(), convert2WeixinPayDetailsVO(scanPayResData));
        }
    }
    
    private WeixinPayDetailsVO convert2WeixinPayDetailsVO(ScanPayResData scanPayResData) {
        WeixinPayDetailsVO  vo = new WeixinPayDetailsVO();
        //协议层
        vo.setReturnCode(scanPayResData.getReturn_code());
        vo.setReturnMsg(scanPayResData.getReturn_msg());
        
        //协议返回的具体数据（以下字段在return_code 为SUCCESS 的时候有返回）
        if (ReturnCode.SUCCESS.toString().equals(scanPayResData.getReturn_code())) {
            vo.setAppid(scanPayResData.getAppid());
            vo.setMchId(scanPayResData.getMch_id());
            vo.setNonceStr(scanPayResData.getNonce_str());
            vo.setSign(scanPayResData.getSign());
            vo.setResultCode(scanPayResData.getResult_code());
            vo.setErrCode(scanPayResData.getErr_code());
            vo.setErrCodeDes(scanPayResData.getErr_code_des());
            
            //业务返回的具体数据（以下字段在return_code 和result_code 都为SUCCESS 的时候有返回）
            if (ResultCode.SUCCESS.toString().equals(scanPayResData.getResult_code())) {
                vo.setDeviceInfo(scanPayResData.getDevice_info());
                vo.setOpenid(scanPayResData.getOpenid());
                vo.setIsSubscribe(scanPayResData.getIs_subscribe());
                vo.setTradeType(scanPayResData.getTrade_type());
                vo.setBankType(scanPayResData.getBank_type());
                vo.setTotalFee(StringUtils.isNotBlank(scanPayResData.getTotal_fee()) ? Integer.parseInt(scanPayResData.getTotal_fee()) : null);
                vo.setCouponFee(StringUtils.isNotBlank(scanPayResData.getCoupon_fee()) ? Integer.parseInt(scanPayResData.getCoupon_fee()) : null);
                vo.setFeeType(scanPayResData.getFee_type());
                vo.setTransactionId(scanPayResData.getTransaction_id());
                vo.setOutTradeNo(scanPayResData.getOut_trade_no());
                vo.setAttach(scanPayResData.getAttach());
                vo.setTimeEnd(StringUtils.isNotBlank(scanPayResData.getTime_end()) ? DateUtil.getDate(scanPayResData.getTime_end(), "yyyyMMddHHmmss") : null);
            }
        }
        
        // TODO 兼容统一下单
        return vo;
    }
    
}
