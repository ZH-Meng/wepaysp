package com.zbsp.wepaysp.api.util;

import org.apache.commons.lang.StringUtils;

import com.tencent.protocol.pay_protocol.ScanPayReqData;
import com.tencent.protocol.pay_protocol.ScanPayResData;
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
        
        //协议返回的具体数据（以下字段在return_code 为SUCCESS 的时候有返回）
        if (ReturnCode.SUCCESS.toString().equals(scanPayResData.getReturn_code())) {
            vo.setAppid(scanPayResData.getAppid());
            vo.setMchId(scanPayResData.getMch_id());
            vo.setNonceStr(scanPayResData.getNonce_str());
            vo.setSign(scanPayResData.getSign());
            vo.setResultCode(scanPayResData.getResult_code());
            vo.setErrCode(scanPayResData.getErr_code());
            vo.setErrCodeDes(scanPayResData.getErr_code_des());
            
            vo.setOutTradeNo(scanPayResData.getOut_trade_no());
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
        
        // TODO 兼容统一下单
        return vo;
    }
}
