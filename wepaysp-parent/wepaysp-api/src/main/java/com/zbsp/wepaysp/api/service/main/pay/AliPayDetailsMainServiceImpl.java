package com.zbsp.wepaysp.api.service.main.pay;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.zbsp.alipay.trade.model.builder.AlipayTradePayRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.zbsp.alipay.trade.model.result.AlipayF2FPayResult;
import com.zbsp.alipay.trade.model.result.AlipayF2FQueryResult;
import com.zbsp.alipay.trade.service.AlipayTradeService;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.pay.AliPayDetailsService;
import com.zbsp.wepaysp.api.util.AliPayPackConverter;
import com.zbsp.wepaysp.api.util.AliPayUtil;
import com.zbsp.wepaysp.common.constant.AliPayEnums.AliPayResult;
import com.zbsp.wepaysp.common.constant.AliPayEnums.GateWayResponse;
import com.zbsp.wepaysp.common.constant.AliPayEnums.TradeState4AliPay;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.SysEnums.TradeStatus;
import com.zbsp.wepaysp.common.exception.ConvertPackException;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.vo.pay.AliPayDetailsVO;

public class AliPayDetailsMainServiceImpl
    extends BaseService
    implements AliPayDetailsMainService {

    private AliPayDetailsService aliPayDetailsService;
    
    @Override
    public Map<String, Object> face2FaceBarPay(AliPayDetailsVO payDetailsVO) {
        String logPrefix = "当面付-条码支付 - ";
        // 生成保存支付明细；
        logger.info(logPrefix + "生成支付宝支付明细 - 开始");
        
        String resCode = AliPayResult.ERROR.getCode();
        String resDesc = AliPayResult.ERROR.getDesc();
        
        // 声明返回Map
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("resultCode", resCode);
        resultMap.put("resultDesc", resDesc);
        
        boolean flag = false;
        try {
            payDetailsVO = aliPayDetailsService.doTransCreatePayDetails(payDetailsVO);
            flag = true;
            logger.info(logPrefix + "生成支付宝支付明细 - 成功，系统订单号：{}", payDetailsVO.getOutTradeNo());
        } catch (Exception e) {
            logger.error(logPrefix + "生成支付宝支付明细 - 失败：{}", e.getMessage(), e);
        } finally {
            logger.info(logPrefix + "生成支付宝支付明细 - 结束");
            if (!flag) {
                return resultMap;
            }
        }
        String outTradeNo = payDetailsVO.getOutTradeNo();
        
        // -------------创建成功后调用当面付接口--------------- //
        logger.info(logPrefix + "调用当面付接口-条码支付请求 - 开始");
        flag = false;
       
        AlipayF2FPayResult payResult = null;
        try {
            logger.info(logPrefix + "支付明细转换支付请求包构造器 - 开始");
            // 支付请求构造器
            AlipayTradePayRequestBuilder builder = AliPayPackConverter.aliPayDetailsVO2AlipayTradePayRequestBuilder(payDetailsVO);
            
            logger.info(logPrefix + "支付明细转换支付请求包构造器 - 成功");
            
            // 当面付2.0服务
            AlipayTradeService service = AliPayUtil.getDefaultAlipayTradeService(); 
            // 调用tradePay方法获取当面付应答
            payResult = service.tradePay(builder);
            // 打印应答
            logger.info(logPrefix + "调用当面付接口-条码支付结果 - outTradeNo={}, tradeStatus={}, reponse : {})", outTradeNo, payResult.getTradeStatus(), 
                payResult.getResponse() == null ? null : JSONUtil.toJSONString(payResult.getResponse(), true));
            
            flag = true;
        } catch (ConvertPackException e) {
            logger.error(logPrefix + AlarmLogPrefix.sendAliPayRequestException + "支付明细转换支付请求包构造器(ouTradeNo={}) - 异常 : {}", outTradeNo, e.getMessage());
        } catch (Exception e) {
            logger.error(logPrefix + AlarmLogPrefix.invokeAliPayAPIErr.getValue() + "调用当面付接口-条码支付请求(ouTradeNo={}) - 异常 : {}", outTradeNo, e.getMessage(), e);
        } finally {
            logger.info("调用当面付接口-条码支付请求 - 结束");
            if (!flag) {
                return resultMap;
            }
        }
        
        AliPayDetailsVO payResultVO = null;
        boolean updateFlag = false;
        Integer updateTradeStatus = null;
        logger.info(logPrefix + "处理支付宝当面条码支付结果 - 开始");
        try {
            // 同步返回暂不验签 FIXME 支付中轮询修改
            // FIXME 支付处理中，撤销结果未知，再查询结果
            switch (payResult.getTradeStatus()) {
                case SUCCESS:
                    logger.info(logPrefix + "- SDK支付成功(ouTradeNo={}), 准备更新支付结果", outTradeNo);
                    updateFlag = true;
                    break;
                case FAILED:
                    if (payResult.getResponse() == null) {// 支付异常->撤销成功/失败
                        logger.warn(logPrefix + "SDK支付异常，撤销成功/失败(ouTradeNo={}, 支付结果信息为空，支付状态置为待关闭)", outTradeNo);
                        
                        aliPayDetailsService.doTransUpdatePayDetailState(outTradeNo, TradeStatus.TRADE_TO_BE_CLOSED.getValue());
                    } else {
                        String code = payResult.getResponse().getCode();
                        if (StringUtils.equals(code, GateWayResponse.UNKNOW.getCode())) {// 支付异常->撤销成功/失败 
                            logger.warn(logPrefix + "SDK支付异常，撤销成功/失败(ouTradeNo={}), 准备更新支付结果信息，支付状态置为待关闭", outTradeNo);
                            
                            aliPayDetailsService.doTransUpdatePayDetailState(outTradeNo, TradeStatus.TRADE_TO_BE_CLOSED.getValue());
                        } else if (StringUtils.equals(code, GateWayResponse.ORDER_SUCCESS_PAY_INPROCESS.getCode())) {// 支付处理中->查询超时->撤销成功/失败
                            logger.info(logPrefix + "SDK支付查询超时，撤销结果未知(ouTradeNo={}), 再次查询", outTradeNo);
                            updateTradeStatus = queryTradeStatus(payDetailsVO);
                            if (updateTradeStatus == TradeStatus.MANUAL_HANDLING.getValue()) {
                                logger.error(logPrefix + "SDK支付状态异常(ouTradeNo={}), 支付状态置为人工处理", outTradeNo);
                            }
                            
                            updateFlag = true;
                        } else {// 支付失败
                            logger.warn(logPrefix + "SDK支付明确失败(ouTradeNo={}), 准备更新支付结果", outTradeNo);
                            updateFlag = true;
                        }
                    }
                    break;
                case UNKNOWN: 
                    if (payResult.getResponse() == null) {// 支付异常->撤销异常
                        logger.warn(logPrefix + "SDK支付异常，撤销异常(ouTradeNo={}), 支付结果信息为空，支付状态置为待关闭", outTradeNo);
                        aliPayDetailsService.doTransUpdatePayDetailState(outTradeNo, TradeStatus.TRADE_TO_BE_CLOSED.getValue());
                    } else {// 支付处理中->查询超时->撤销异常
                        logger.info(logPrefix + "SDK支付查询超时，撤销异常(ouTradeNo={}), 再次查询", outTradeNo);
                        updateTradeStatus = queryTradeStatus(payDetailsVO);
                        if (updateTradeStatus == TradeStatus.MANUAL_HANDLING.getValue()) {
                            logger.error(logPrefix + "SDK支付状态异常(ouTradeNo={}), 支付状态置为人工处理", outTradeNo);
                        }
                        updateFlag = true;
                    }
                    break;
                default:
                    logger.error(logPrefix + "调用当面付接口-条码支付结果 - 不支持的交易状态，交易返回异常(ouTradeNo={})", outTradeNo);
                    break;
            }
            
            if (updateFlag) {
                logger.info(logPrefix + "支付响应转换支付明细 - 开始");
                
                payResultVO = AliPayPackConverter.alipayTradePayResponse2AliPayDetailsVO(payResult.getResponse());
                
                logger.info(logPrefix + "支付响应转换支付明细 - 成功");
                
                // 更新支付结果
                logger.info(logPrefix + "更新支付结果 - 开始");
                payResultVO =aliPayDetailsService.doTransUpdateFace2FacePayResult(payResultVO.getCode(), payResultVO.getSubCode(), payResultVO, updateTradeStatus);
                logger.info(logPrefix + "更新支付结果 - 结束");
                
                if (payResultVO.getTradeStatus().intValue() == TradeStatus.TRADE_SUCCESS.getValue()) {
                    resultMap.put("resultCode", AliPayResult.SUCCESS.getCode());
                    resultMap.put("resultDesc", AliPayResult.SUCCESS.getDesc());
                    logger.info("系统订单ID=" + payResultVO.getOutTradeNo() + "支付宝条码支付成功，支付宝订单ID=" + payResultVO.getTradeNo());
                } else {
                    if (payResultVO.getTradeStatus().intValue() == TradeStatus.MANUAL_HANDLING.getValue()) {
                        logger.warn("系统订单ID : {}，支付宝条码支付结果需要人工处理，错误码 : {}，错误描述 : {}", payResultVO.getOutTradeNo(), payResultVO.getSubCode(), payResultVO.getSubMsg());
                    } else {
                        logger.warn("系统订单ID : {}，支付宝条码支付失败，错误码 : {}，错误描述 : {}", payResultVO.getOutTradeNo(), payResultVO.getSubCode(), payResultVO.getSubMsg());
                    }
                    resultMap.put("resultCode", payResultVO.getSubCode());
                    resultMap.put("resultDesc", payResultVO.getSubMsg());
                }
                resultMap.put("aliPayDetailsVO", payResultVO);
                return resultMap;
            }
        } catch (ConvertPackException e) {
            logger.error(logPrefix + AlarmLogPrefix.handleAliPayResultException + "支付响应转换支付明细(ouTradeNo={}) - 异常 : {}", outTradeNo, e.getMessage());
        } catch (Exception e) {
            logger.error(logPrefix + AlarmLogPrefix.handleAliPayResultErr.getValue() + "处理支付宝当面条码支付结果(ouTradeNo={}) - 失败 : {}", outTradeNo, e.getMessage(), e);
        } finally {
            logger.info(logPrefix + "处理支付宝当面条码支付结果 - 结束");
        }
        
        //FIXME 支付失败不返回支付结果或者支付明细信息
        return resultMap;
    }

    private Integer queryTradeStatus(AliPayDetailsVO payDetailsVO) {
        AlipayTradeQueryRequestBuilder queryBuilder = new AlipayTradeQueryRequestBuilder()
            .setAppAuthToken(payDetailsVO.getAppAuthToken())
            .setOutTradeNo(payDetailsVO.getOutTradeNo());
        
        AlipayF2FQueryResult queryTradeResult = AliPayUtil.getDefaultAlipayTradeService().queryTradeResult(queryBuilder);
        if (queryTradeResult.isTradeSuccess()) {
            if (queryTradeResult.getResponse() != null) {
                String queryTradeStatus = queryTradeResult.getResponse().getTradeStatus();
                logger.info("SDK支付查询超时，撤销结果未知(ouTradeNo={}), 再次查询结果交易状态 : {}", payDetailsVO.getOutTradeNo(), queryTradeStatus);
                if (StringUtils.equalsIgnoreCase(TradeState4AliPay.TRADE_CLOSED.toString(), queryTradeStatus)) {
                    return TradeStatus.TRADE_CLOSED.getValue();
                }
            }
        }
        return TradeStatus.MANUAL_HANDLING.getValue();
    }
    
    public void setAliPayDetailsService(AliPayDetailsService aliPayDetailsService) {
        this.aliPayDetailsService = aliPayDetailsService;
    }
    
}
