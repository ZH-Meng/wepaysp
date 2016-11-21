package com.zbsp.wepaysp.api.service.main.pay;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.tencent.WXPay;
import com.zbsp.wepaysp.common.constant.EnumDefine.WxPayResult;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.api.util.WeixinPackConverter;
import com.zbsp.wepaysp.api.listener.DefaultScanPayBusinessResultListener;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails.PayType;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;


public class WeixinPayDetailsMainServiceImpl
    extends BaseService
    implements WeixinPayDetailsMainService {

    private WeixinPayDetailsService weixinPayDetailsService;
	
    @Override
    public Map<String, Object> createPayAndInvokeWxPay(WeixinPayDetailsVO weixinPayDetailsVO, String creator, String operatorUserOid, String logFunctionOid) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String resCode = WxPayResult.ERROR.getCode();
        String resDesc = WxPayResult.ERROR.getDesc();
        String outTradeNo = null; 
        String transactionId = null;
        logger.info("创建微信支付明细开始");
        try {
            weixinPayDetailsService.doTransCreatePayDetails(weixinPayDetailsVO, creator, operatorUserOid, logFunctionOid);
            outTradeNo = weixinPayDetailsVO.getOutTradeNo();
            logger.info("微信支付明细保存成功！");
        } catch (Exception e) {
            logger.warn("创建微信支付明细失败");
            throw e;
        }
        
        String payType = weixinPayDetailsVO.getPayType();
        
        if (StringUtils.equals(PayType.MICROPAY.getValue(), payType)) {// 刷卡支付
            logger.info("开始微信刷卡支付！");
            try {
                DefaultScanPayBusinessResultListener scanPayListener = new DefaultScanPayBusinessResultListener(weixinPayDetailsService);// 刷卡支付监听器

                // 组包、调用刷卡API
                WXPay.doScanPayBusiness(WeixinPackConverter.weixinPayDetailsVO2ScanPayReq(weixinPayDetailsVO), scanPayListener, weixinPayDetailsVO.getCertLocalPath(), weixinPayDetailsVO.getCertPassword(), weixinPayDetailsVO.getKeyPartner());
                
                //String listenerResult = scanPayListener.getResult();
                transactionId = scanPayListener.getTranscationID();// 正常情况业务结果为成功时返回

                // 查询支付结果
                Map<String, Object> jpqlMap = new HashMap<String, Object>();
                String jpql = "from WeixinPayDetails w where w.outTradeNo=:OUTTRADENO";
                jpqlMap.put("OUTTRADENO", outTradeNo);
                
                WeixinPayDetails payDetails = commonDAO.findObject(jpql, jpqlMap, false);
                if (payDetails == null) {
                    throw new NotExistsException("系统支付订单不存在！");
                }
                
                String wxResultCode = payDetails.getResultCode();
                resCode = payDetails.getErrCode();
                resDesc = payDetails.getErrCodeDes();
                
                if (StringUtils.equalsIgnoreCase(WxPayResult.SUCCESS.getCode(), wxResultCode)) {// 业务结果：成功
                    resCode = WxPayResult.SUCCESS.getCode();
                    resDesc = WxPayResult.SUCCESS.getDesc();
                    logger.info("系统订单ID=" + outTradeNo + "微信刷卡支付成功，微信支付订单ID=" + payDetails.getTransactionId());
                } else {// 支付失败或错误
                    logger.warn("系统订单ID" + outTradeNo + "微信刷卡支付失败，错误码：" + resCode + "，错误描述：" + resDesc);
                }
            } catch (Exception e) {
                logger.error("支付失败，" + e.getMessage());
                logger.debug(e.getMessage(), e);
            }
        } else if (StringUtils.equals(PayType.JSAPI.getValue(), payType)) {// 公众号支付
            //TODO 提交微信下单并刷卡支付
            
        }

        resultMap.put("resultCode", resCode);
        resultMap.put("resultDesc", resDesc);
        resultMap.put("outTradeNo", outTradeNo);
        resultMap.put("transactionId", transactionId);
        resultMap.put("wexinPayDetailsVO", weixinPayDetailsVO);// FIXME
        
        return resultMap;
    }
    
    public void setWeixinPayDetailsService(WeixinPayDetailsService weixinPayDetailsService) {
        this.weixinPayDetailsService = weixinPayDetailsService;
    }
    
}
