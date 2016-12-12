package com.zbsp.wepaysp.api.service.main.pay;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.tencent.WXPay;
import com.tencent.common.Signature;
import com.tencent.common.Util;
import com.tencent.protocol.unified_order_protocol.JSPayReqData;
import com.tencent.protocol.unified_order_protocol.WxPayNotifyData;
import com.tencent.protocol.unified_order_protocol.WxPayNotifyResultData;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;
import com.zbsp.wepaysp.common.constant.EnumDefine.DevParam;
import com.zbsp.wepaysp.common.constant.EnumDefine.ReturnCode;
import com.zbsp.wepaysp.common.constant.EnumDefine.WxPayResult;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.api.util.WeixinPackConverter;
import com.zbsp.wepaysp.api.listener.DefaultOrderQueryBusinessResultListener;
import com.zbsp.wepaysp.api.listener.DefaultScanPayBusinessResultListener;
import com.zbsp.wepaysp.api.listener.DefaultUnifiedOrderBusinessResultListener;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails.PayType;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails.TradeStatus;
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
            String transactionId = null;
            try {
                DefaultScanPayBusinessResultListener scanPayListener = new DefaultScanPayBusinessResultListener(weixinPayDetailsService);// 刷卡支付监听器

                // 组包、调用刷卡API
                WXPay.doScanPayBusiness(WeixinPackConverter.weixinPayDetailsVO2ScanPayReq(weixinPayDetailsVO), scanPayListener, weixinPayDetailsVO.getCertLocalPath(), weixinPayDetailsVO.getCertPassword(), weixinPayDetailsVO.getKeyPartner());
                
                String listenerResult = scanPayListener.getResult();
                transactionId = scanPayListener.getTranscationID();// 正常情况业务结果为成功时返回

                if (StringUtils.equalsIgnoreCase(listenerResult, DefaultScanPayBusinessResultListener.ON_FAIL_BY_RETURN_CODE_ERROR)) {
                    logger.warn("系统订单ID" + outTradeNo + "微信刷卡支付失败，没按照API规范去正确地传递参数");
                } else {
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
                }
                resultMap.put("transactionId", transactionId);
            } catch (Exception e) {
                logger.error("支付失败，" + e.getMessage());
                logger.error(e.getMessage(), e);
            }
        } else if (StringUtils.equals(PayType.JSAPI.getValue(), payType)) {// 公众号支付
            logger.info("开始微信公众号下单！");
            String prepayId = null;
            try {
                DefaultUnifiedOrderBusinessResultListener orderListener = new DefaultUnifiedOrderBusinessResultListener(weixinPayDetailsService);// 公众号下单监听器
                
                // 组包、调用下单API
                WXPay.doUnifiedOrderBusiness(WeixinPackConverter.weixinPayDetailsVO2UnifiedOrderReq(weixinPayDetailsVO), orderListener, weixinPayDetailsVO.getCertLocalPath(), weixinPayDetailsVO.getCertPassword(), weixinPayDetailsVO.getKeyPartner());
                
                String listenerResult = orderListener.getResult();
                if (StringUtils.equalsIgnoreCase(listenerResult, DefaultScanPayBusinessResultListener.ON_SUCCESS)) {
                    prepayId = orderListener.getPrepayId();// 预支付标识
                    if (StringUtils.isBlank(orderListener.getPrepayId())) {
                        logger.warn("公众号下单结果prepayId缺失");
                        resDesc = "公众号下单结果prepayId缺失";
                    } else {
                        resCode = WxPayResult.SUCCESS.getCode();
                        resDesc = WxPayResult.SUCCESS.getDesc();
                        // 组装支付请求包
                        JSPayReqData jsPayReqData = new JSPayReqData(weixinPayDetailsVO.getAppid(), prepayId, weixinPayDetailsVO.getKeyPartner());
                        resultMap.put("jsPayReqData", jsPayReqData);
                    }
                }
                resultMap.put("prepayId", prepayId);
            } catch (Exception e) {
                logger.error("公众号下单失败，" + e.getMessage());
                logger.error(e.getMessage(), e);
            }
        }
        
        resultMap.put("resultCode", resCode);
        resultMap.put("resultDesc", resDesc);
        resultMap.put("outTradeNo", outTradeNo);
        resultMap.put("wexinPayDetailsVO", weixinPayDetailsVO);
        
        return resultMap;
    }
    
    public void setWeixinPayDetailsService(WeixinPayDetailsService weixinPayDetailsService) {
        this.weixinPayDetailsService = weixinPayDetailsService;
    }

    @Override
    public String handleWxPayNotify(String respXmlString) {
        Validator.checkArgument(StringUtils.isBlank(respXmlString), "支付结果通知字串不能为空");
        logger.info("公众号支付结果通知处理API开始处理.");
        WxPayNotifyResultData result=null;
        String appid = null;
        WxPayNotifyData wxNotify = (WxPayNotifyData) Util.getObjectFromXML(respXmlString, WxPayNotifyData.class);
        if (wxNotify == null || StringUtils.isBlank(wxNotify.getAppid())) {
            logger.error("公众号支付结果通知，解析参数格式失败，结果内容：" + respXmlString);
            result=new WxPayNotifyResultData("FAIL");
            result.setReturn_msg("解析参数格式失败");
        } else {
            appid = wxNotify.getAppid();
            // 从内存中查找key 
            String key = DevParam.KEY.getValue();// FIXME
            //检查xml是否有效        
            boolean flag = false;
            try {
                flag = Signature.checkIsSignValidFromResponseString(respXmlString, key);
            } catch (Exception e) {
                logger.debug(e.getMessage(), e);
                result=new WxPayNotifyResultData("FAIL");
                result.setReturn_msg("失败");
            }
            if (flag) {
                String returnCode = wxNotify.getReturn_code();
                String resultCode = wxNotify.getResult_code();
                try {
	                if (StringUtils.equalsIgnoreCase(ReturnCode.FAIL.toString(), returnCode)) {
	                	// TODO 关闭订单
	                } else {
                		weixinPayDetailsService.doTransUpdatePayResult(returnCode, resultCode, WeixinPackConverter.payNotify2weixinPayDetailsVO(wxNotify));
	                }
	                result = new WxPayNotifyResultData("SUCCESS");
	                result.setReturn_msg("成功");
                } catch (Exception e) {
                	logger.error("微信支付结果通知错误，结果内容：" + respXmlString + "，处理异常信息：" + e.getMessage());
                	result = new WxPayNotifyResultData("FAIL");
                	result.setReturn_msg("失败");
                }
               // TODO 发送支付结果公众号信息（支付成功/失败）
                
            } else {
                logger.error("公众号支付结果通知，签名失败，结果内容：" + respXmlString);
                result = new WxPayNotifyResultData("FAIL");
                result.setReturn_msg("签名失败");
            }
        }
        XStream xStreamForResponsetData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));

        //将要提交给微信支付结果通知处理结果对象转换成XML格式数据
        String resultXML = xStreamForResponsetData.toXML(result);
        logger.info("处理结束，返回结果：" + resultXML);
        return resultXML;
    }

    @Override
    public Map<String, Object> checkPayResult(String payResult, String weixinPayDetailOid) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        WeixinPayDetailsVO payDetailVO =  weixinPayDetailsService.doJoinTransQueryWeixinPayDetailsByOid(weixinPayDetailOid);
        Integer tradeStatus = payDetailVO.getTradeStatus();
        
        if (tradeStatus.intValue() == TradeStatus.TRADEING.getValue()) {// 处理中，代表系统没有收到微信支付结果通知
            if ("cancel".equalsIgnoreCase(payResult)) {// 用户取消支付
                logger.info("订单状态处理中，用户取消支付，现主动发起关闭订单请求");
                // TODO 关闭订单
                
            } else {
                logger.info("订单状态处理中，系统暂未收到微信支付结果通知，现主动发起订单查询请求");
                // 主动查询微信支付结果
                try {
                    DefaultOrderQueryBusinessResultListener orderQueryListener = new DefaultOrderQueryBusinessResultListener(this);// 订单查询监听器
                    
                    // FIXME 从内存中获取 --------------------临时数据
                    String certLocalPath = DevParam.CERT_LOCAL_PATH.getValue();
                    String certPassword = DevParam.CERT_PASSWORD.getValue(); 
                    String keyPartner = DevParam.KEY.getValue();
                    payDetailVO.setCertLocalPath(certLocalPath);
                    payDetailVO.setCertPassword(certPassword);
                    payDetailVO.setKeyPartner(keyPartner);
                    
                    // 订单查询
                    WXPay.doOrderQueryBusiness(WeixinPackConverter.weixinPayDetailsVO2OrderQueryReq(payDetailVO), orderQueryListener, payDetailVO.getCertLocalPath(), payDetailVO.getCertPassword(), payDetailVO.getKeyPartner());
                    
                    payDetailVO = weixinPayDetailsService.doJoinTransQueryWeixinPayDetailsByOid(weixinPayDetailOid);
                    tradeStatus = payDetailVO.getTradeStatus();// 当前订单状态
                } catch (Exception e) {
                    logger.error("订单（系统订单ID：" + payDetailVO.getOutTradeNo() + "）查询失败，" + e.getMessage());
                    logger.error(e.getMessage(), e);
                }
            }
        }

        if ("ok".equalsIgnoreCase(payResult)) {
            if (payDetailVO.getTradeStatus().intValue() != TradeStatus.TRADE_SUCCESS.getValue()) {
                logger.warn("微信H5支付结果为ok，系统支付交易状态为：" + payDetailVO.getTradeStatus().intValue() + "系统订单ID：" + payDetailVO.getOutTradeNo());
            }
        } else if ("cancel".equalsIgnoreCase(payResult)) {// 用户取消支付
            //weixinPayDetailsService.doTransCancelPay(weixinPayDetailOid);
            if (payDetailVO.getTradeStatus().intValue() != TradeStatus.TRADE_CLOSED.getValue()) {
                logger.warn("微信H5支付结果为cancel，系统支付交易状态为：" + payDetailVO.getTradeStatus().intValue() + "系统订单ID：" + payDetailVO.getOutTradeNo());
            }
        } else if ("error".equalsIgnoreCase(payResult)) {// 用户取消支付
            if (payDetailVO.getTradeStatus().intValue() != TradeStatus.TRADE_FAIL.getValue()) {
                logger.warn("微信H5支付结果为error，系统支付交易状态为：" + payDetailVO.getTradeStatus().intValue() + "系统订单ID：" + payDetailVO.getOutTradeNo());
            }
        }
        
        resultMap.put("tradeStatus", tradeStatus);
        resultMap.put("weixinPayDetailsVO", payDetailVO);        
        return resultMap;
    }

    @Override
    public void handleOrderQueryResult(WeixinPayDetailsVO queryResultVO) {
        // TODO Auto-generated method stub
        // TODO 如果订单不存在，关闭订单
        // 查询成功
        //weixinPayDetailsService.doTransUpdateOrderQueryResult(queryResultVO);
    }
    
}
