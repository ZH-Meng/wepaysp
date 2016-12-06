package com.zbsp.wepaysp.api.util;

import org.apache.commons.lang.StringUtils;

import com.tencent.protocol.pay_protocol.ScanPayReqData;
import com.tencent.protocol.pay_protocol.ScanPayResData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryResData;
import com.tencent.protocol.unified_order_protocol.UnifiedOrderReqData;
import com.tencent.protocol.unified_order_protocol.UnifiedOrderResData;
import com.tencent.protocol.unified_order_protocol.WxPayNotifyData;
import com.zbsp.wepaysp.common.constant.EnumDefine.ResultCode;
import com.zbsp.wepaysp.common.constant.EnumDefine.ReturnCode;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;

/**
 * 微信支付HTTP请求/响应包与系统VO转换器
 * 
 * @author 孟郑宏
 */
public class WeixinPackConverter {

    
    /**
     * 微信支付明细VO转换成刷卡支付请求包
     * 
     * @param weixinPayDetailsVO 微信支付明细VO
     * @return 刷卡支付请求包
     */
    public static ScanPayReqData weixinPayDetailsVO2ScanPayReq(WeixinPayDetailsVO weixinPayDetailsVO) {
        ScanPayReqData scanPayReq = new ScanPayReqData(
            weixinPayDetailsVO.getAuthCode(), 
            weixinPayDetailsVO.getBody(), 
            weixinPayDetailsVO.getAttach(), 
            weixinPayDetailsVO.getOutTradeNo(), 
            weixinPayDetailsVO.getTotalFee(), 
            weixinPayDetailsVO.getDeviceInfo(), 
            weixinPayDetailsVO.getSpbillCreateIp(),
            weixinPayDetailsVO.getTimeStart(), 
            weixinPayDetailsVO.getTimeExpire(), 
            weixinPayDetailsVO.getGoodsTag(),
            weixinPayDetailsVO.getKeyPartner(),
            weixinPayDetailsVO.getAppid(),
            weixinPayDetailsVO.getMchId(),
            weixinPayDetailsVO.getSubMchId()
            );
        return scanPayReq;
    }
    
    /**
     * 刷卡支付响应包转换成微信支付明细VO
     * 
     * @param scanPayResData 刷卡支付响应包
     * @return 微信支付明细VO
     */
    public static WeixinPayDetailsVO scanPayRes2WeixinPayDetailsVO(ScanPayResData scanPayResData) {
        WeixinPayDetailsVO  vo = new WeixinPayDetailsVO();
        //协议层
        vo.setReturnCode(scanPayResData.getReturn_code());
        vo.setReturnMsg(scanPayResData.getReturn_msg());
        vo.setOutTradeNo(scanPayResData.getOut_trade_no());
        
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
                vo.setAttach(scanPayResData.getAttach());
                vo.setTimeEnd(StringUtils.isNotBlank(scanPayResData.getTime_end()) ? DateUtil.getDate(scanPayResData.getTime_end(), "yyyyMMddHHmmss") : null);
            }
        }
        // FIXME 待补全，只组装有用的数据
        
        return vo;
    }

    /**
     * 统一下单结果包转化为微信支付明细VO
     * @param unifiedOrderResData 统一下单结果包
     * @return 微信支付明细VO（只包含了下单结果）
     */
    public static WeixinPayDetailsVO unifiedOrderRes2WeixinPayDetailsVO(UnifiedOrderResData unifiedOrderResData) {
        WeixinPayDetailsVO  vo = new WeixinPayDetailsVO();
        //协议层
        vo.setReturnCode(unifiedOrderResData.getReturn_code());
        vo.setReturnMsg(unifiedOrderResData.getReturn_msg());
        vo.setOutTradeNo(unifiedOrderResData.getOut_trade_no());
        
        //协议返回的具体数据（以下字段在return_code 为SUCCESS 的时候有返回）
        if (ReturnCode.SUCCESS.toString().equals(unifiedOrderResData.getReturn_code())) {
            vo.setAppid(unifiedOrderResData.getAppid());
            vo.setMchId(unifiedOrderResData.getMch_id());
            vo.setSubMchId(unifiedOrderResData.getSub_mch_id());
            vo.setSubAppid(unifiedOrderResData.getSub_appid());
            vo.setDeviceInfo(unifiedOrderResData.getDevice_info());
            vo.setNonceStr(unifiedOrderResData.getNonce_str());
            vo.setSign(unifiedOrderResData.getSign());
            vo.setResultCode(unifiedOrderResData.getResult_code());
            vo.setErrCode(unifiedOrderResData.getErr_code());
            vo.setErrCodeDes(unifiedOrderResData.getErr_code_des());
            
            //业务返回的具体数据（以下字段在return_code 和result_code 都为SUCCESS 的时候有返回）
            if (ResultCode.SUCCESS.toString().equals(unifiedOrderResData.getResult_code())) {
                vo.setTradeType(unifiedOrderResData.getTrade_type());
                vo.setPrepayId(unifiedOrderResData.getPrepay_id());
                vo.setCodeUrl(unifiedOrderResData.getCode_url());
            }
        }
        return vo;
    }

    /**
     * 微信支付明细VO转换成统一下单请求包
     * 
     * @param weixinPayDetailsVO 微信支付明细VO
     * @return 统一下单请求包
     */
    public static UnifiedOrderReqData weixinPayDetailsVO2UnifiedOrderReq(WeixinPayDetailsVO weixinPayDetailsVO) {
        UnifiedOrderReqData unifiedOrderReq = new UnifiedOrderReqData(
            weixinPayDetailsVO.getAppid(), 
            weixinPayDetailsVO.getMchId(), 
            weixinPayDetailsVO.getSubAppid(), 
            weixinPayDetailsVO.getSubMchId(), 
            weixinPayDetailsVO.getDeviceInfo(), 
            null, null, null, // nonce_str、sign、sign_type
            weixinPayDetailsVO.getBody(), 
            weixinPayDetailsVO.getDetail(), 
            weixinPayDetailsVO.getAttach(), 
            weixinPayDetailsVO.getOutTradeNo(), 
            weixinPayDetailsVO.getFeeType(), 
            weixinPayDetailsVO.getTotalFee(), 
            weixinPayDetailsVO.getSpbillCreateIp(), 
            weixinPayDetailsVO.getTimeStart(), 
            weixinPayDetailsVO.getTimeExpire(), 
            weixinPayDetailsVO.getGoodsTag(), 
            weixinPayDetailsVO.getNotifyUrl(), 
            weixinPayDetailsVO.getTradeType(), 
            null, //ProductId
            weixinPayDetailsVO.getLimitPay(), 
            weixinPayDetailsVO.getOpenid(), 
            weixinPayDetailsVO.getSubOpenid(), 
            weixinPayDetailsVO.getKeyPartner());
           
        return unifiedOrderReq;
    }

    public static WeixinPayDetailsVO payNotify2weixinPayDetailsVO(WxPayNotifyData wxNotify) {
        WeixinPayDetailsVO  vo = new WeixinPayDetailsVO();
        //协议层
        vo.setReturnCode(wxNotify.getReturn_code());
        vo.setReturnMsg(wxNotify.getReturn_msg());
        vo.setOutTradeNo(wxNotify.getOut_trade_no());
        
        //协议返回的具体数据（以下字段在return_code 为SUCCESS 的时候有返回）
        if (ReturnCode.SUCCESS.toString().equals(wxNotify.getReturn_code())) {
            vo.setAppid(wxNotify.getAppid());
            vo.setMchId(wxNotify.getMch_id());
            vo.setNonceStr(wxNotify.getNonce_str());
            vo.setSign(wxNotify.getSign());
            vo.setResultCode(wxNotify.getResult_code());
            vo.setErrCode(wxNotify.getErr_code());
            vo.setErrCodeDes(wxNotify.getErr_code_des());
            
            //业务返回的具体数据（以下字段在return_code 和result_code 都为SUCCESS 的时候有返回）
            vo.setDeviceInfo(wxNotify.getDevice_info());
            vo.setOpenid(wxNotify.getOpenid());
            vo.setIsSubscribe(wxNotify.getIs_subscribe());
            vo.setTradeType(wxNotify.getTrade_type());
            vo.setBankType(wxNotify.getBank_type());
            vo.setTotalFee(StringUtils.isNotBlank(wxNotify.getTotal_fee()) ? Integer.parseInt(wxNotify.getTotal_fee()) : null);
            vo.setCouponFee(StringUtils.isNotBlank(wxNotify.getCoupon_fee()) ? Integer.parseInt(wxNotify.getCoupon_fee()) : null);
            vo.setFeeType(wxNotify.getFee_type());
            vo.setTransactionId(wxNotify.getTransaction_id());
            vo.setAttach(wxNotify.getAttach());
            vo.setTimeEnd(StringUtils.isNotBlank(wxNotify.getTime_end()) ? DateUtil.getDate(wxNotify.getTime_end(), "yyyyMMddHHmmss") : null);
        }
        // FIXME 待补全，只组装有用的数据
        return vo;
    }

    /**
     * 订单查询结果包转换为WeixinPayDetailsVO
     * @param orderQueryResData 订单查询结果包 
     * @return WeixinPayDetailsVO
     */
    public static WeixinPayDetailsVO orderQueryRes2WeixinPayDetailsVO(ScanPayQueryResData orderQueryResData) {
        WeixinPayDetailsVO  vo = new WeixinPayDetailsVO();
        //协议层
        vo.setReturnCode(orderQueryResData.getReturn_code());
        vo.setReturnMsg(orderQueryResData.getReturn_msg());
        vo.setOutTradeNo(orderQueryResData.getOut_trade_no());
        
        //协议返回的具体数据（以下字段在return_code 为SUCCESS 的时候有返回）
        if (ReturnCode.SUCCESS.toString().equals(orderQueryResData.getReturn_code())) {
            vo.setAppid(orderQueryResData.getAppid());
            vo.setMchId(orderQueryResData.getMch_id());
            vo.setNonceStr(orderQueryResData.getNonce_str());
            vo.setSign(orderQueryResData.getSign());
            vo.setResultCode(orderQueryResData.getResult_code());
            vo.setErrCode(orderQueryResData.getErr_code());
            vo.setErrCodeDes(orderQueryResData.getErr_code_des());
            
            //业务返回的具体数据（以下字段在return_code 和result_code 都为SUCCESS 的时候有返回）
            if (ResultCode.SUCCESS.toString().equals(orderQueryResData.getResult_code())) {
                vo.setDeviceInfo(orderQueryResData.getDevice_info());
                vo.setOpenid(orderQueryResData.getOpenid());
                vo.setIsSubscribe(orderQueryResData.getIs_subscribe());
                vo.setTradeState(orderQueryResData.getTrade_state());
                vo.setTradeType(orderQueryResData.getTrade_type());
                vo.setBankType(orderQueryResData.getBank_type());
                vo.setTotalFee(StringUtils.isNotBlank(orderQueryResData.getTotal_fee()) ? Integer.parseInt(orderQueryResData.getTotal_fee()) : null);
                vo.setCouponFee(StringUtils.isNotBlank(orderQueryResData.getCoupon_fee()) ? Integer.parseInt(orderQueryResData.getCoupon_fee()) : null);
                vo.setFeeType(orderQueryResData.getFee_type());
                vo.setTransactionId(orderQueryResData.getTransaction_id());
                vo.setAttach(orderQueryResData.getAttach());
                vo.setTimeEnd(StringUtils.isNotBlank(orderQueryResData.getTime_end()) ? DateUtil.getDate(orderQueryResData.getTime_end(), "yyyyMMddHHmmss") : null);
            }
        }
        return vo;
    }

    /**
     * 支付明细VO转换为订单查询请求包
     * @param payDetailVO
     * @return 订单查询请求包 ScanPayQueryReqData
     */
    public static ScanPayQueryReqData weixinPayDetailsVO2OrderQueryReq(WeixinPayDetailsVO payDetailVO) {
        ScanPayQueryReqData reqData = new ScanPayQueryReqData(
            payDetailVO.getTransactionId(), 
            payDetailVO.getOutTradeNo(), 
            payDetailVO.getKeyPartner(), 
            payDetailVO.getAppid(), 
            payDetailVO.getMchId(), 
            payDetailVO.getSubMchId()); 
        return reqData;
    }
}
