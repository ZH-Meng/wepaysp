package com.zbsp.wepaysp.api.service.main.pay;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import com.zbsp.alipay.trade.config.Configs;
import com.zbsp.alipay.trade.model.result.AlipayF2FPayResult;
import com.zbsp.alipay.trade.model.result.AlipayF2FQueryResult;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.pay.AliPayDetailsService;
import com.zbsp.wepaysp.api.util.AliPayPackConverter;
import com.zbsp.wepaysp.api.util.AliPayUtil;
import com.zbsp.wepaysp.common.constant.SysEnums;
import com.zbsp.wepaysp.common.constant.AliPayEnums.AliPayResult;
import com.zbsp.wepaysp.common.constant.AliPayEnums.AsynNotifyHandleResult;
import com.zbsp.wepaysp.common.constant.AliPayEnums.GateWayResponse;
import com.zbsp.wepaysp.common.constant.AliPayEnums.TradeState4AliPay;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.SysEnums.TradeStatus;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.exception.ConvertPackException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.vo.alipay.AlipayWapPayNotifyVO;
import com.zbsp.wepaysp.vo.pay.AliPayDetailsVO;

public class AliPayDetailsMainServiceImpl
    extends BaseService
    implements AliPayDetailsMainService {

    private AliPayDetailsService aliPayDetailsService;
    
    public void setAliPayDetailsService(AliPayDetailsService aliPayDetailsService) {
        this.aliPayDetailsService = aliPayDetailsService;
    }
    
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
            payResult = AliPayUtil.tradeF2FPay(payDetailsVO);
            
            // 打印应答
            logger.info(logPrefix + "调用当面付接口-条码支付结果 - outTradeNo={}, tradeStatus={}, reponse : {})", outTradeNo, payResult.getTradeStatus(), 
                payResult.getResponse() == null ? null : JSONUtil.toJSONString(payResult.getResponse(), true));
            
            flag = true;
        } catch (ConvertPackException e) {
            logger.error(logPrefix + AlarmLogPrefix.sendAliPayRequestException + "支付明细转换支付请求包构造器(ouTradeNo={}) - 异常 : {}", outTradeNo, e.getMessage());
        } catch (Exception e) {
            logger.error(logPrefix + AlarmLogPrefix.invokeAliPayAPIErr.getValue() + "调用当面付接口-条码支付请求(ouTradeNo={}) - 异常 : {}", outTradeNo, e.getMessage(), e);
        } finally {
            logger.info(logPrefix + "调用当面付接口-条码支付请求 - 结束");
            if (!flag) {
                return resultMap;
            }
        }
        
        AliPayDetailsVO payResultVO = null;
        boolean updateFlag = false;
        Integer updateTradeStatus = null;
        logger.info(logPrefix + "处理支付宝当面条码支付结果 - 开始");
        try {
            // 同步返回SDK已验签 支付中，轮询使用官方推荐3ms * 10 次
            // FIXME 支付异常，撤销异常，交易直接关闭 
            // 支付处理中，撤销结果未知，再查询结果
            String updateRemark = "";
            switch (payResult.getTradeStatus()) {
                case SUCCESS:
                    logger.info(logPrefix + "- SDK支付成功, ouTradeNo={}, 准备更新支付结果", outTradeNo);
                    updateFlag = true;
                    break;
                case FAILED:
                    if (payResult.getResponse() == null) {// 支付异常->撤销成功/失败
                        logger.warn(logPrefix + "SDK支付异常，撤销成功/失败, ouTradeNo={}, 支付结果信息为空，支付状态置为待关闭)", outTradeNo);
                        
                        aliPayDetailsService.doTransUpdatePayDetailState(outTradeNo, TradeStatus.TRADE_TO_BE_CLOSED.getValue(), "SDK支付异常，撤销成功/失败，更新状态为待关闭；");
                    } else {
                        String code = payResult.getResponse().getCode();
                        if (StringUtils.equals(code, GateWayResponse.UNKNOW.getCode())) {// 支付异常->撤销成功/失败 
                            logger.warn(logPrefix + "SDK支付异常，撤销成功/失败, ouTradeNo={}, 准备更新支付结果信息，支付状态置为待关闭", outTradeNo);
                            
                            aliPayDetailsService.doTransUpdatePayDetailState(outTradeNo, TradeStatus.TRADE_TO_BE_CLOSED.getValue(), "SDK支付未知，撤销成功/失败，更新状态为待关闭；");
                        } else if (StringUtils.equals(code, GateWayResponse.ORDER_SUCCESS_PAY_INPROCESS.getCode())) {// 支付处理中->查询超时->撤销成功/失败
                            logger.info(logPrefix + "SDK支付查询超时，撤销结果未知, ouTradeNo={},  再次查询", outTradeNo);
                            updateTradeStatus = queryTradeStatusAfterCancelUnknown(payDetailsVO);
                            if (updateTradeStatus == TradeStatus.MANUAL_HANDLING.getValue()) {
                                logger.error(logPrefix + "SDK支付状态异常, ouTradeNo={}, 支付状态置为人工处理", outTradeNo);
                                updateRemark = "SDK支付状态，更新状态为人工处理中；";
                            }
                            updateFlag = true;
                        } else {// 支付失败
                            logger.warn(logPrefix + "SDK支付明确失败, ouTradeNo={},  准备更新支付结果", outTradeNo);
                            updateFlag = true;
                        }
                    }
                    break;
                case UNKNOWN: 
                    if (payResult.getResponse() == null) {// 支付异常->撤销异常
                        logger.warn(logPrefix + "SDK支付异常，撤销异常, ouTradeNo={}, 支付结果信息为空，支付状态置为待关闭", outTradeNo);
                        aliPayDetailsService.doTransUpdatePayDetailState(outTradeNo, TradeStatus.TRADE_TO_BE_CLOSED.getValue(), "SDK支付异常，撤销成功/失败，更新状态为待关闭；");
                    } else {// 支付处理中->查询超时->撤销异常
                        logger.info(logPrefix + "SDK支付查询超时，撤销结果未知, ouTradeNo={},  再次查询", outTradeNo);
                        updateTradeStatus = queryTradeStatusAfterCancelUnknown(payDetailsVO);
                        if (updateTradeStatus == TradeStatus.MANUAL_HANDLING.getValue()) {
                            logger.error(logPrefix + "SDK支付状态异常, ouTradeNo={}, 支付状态置为人工处理", outTradeNo);
                            updateRemark = "SDK支付状态，更新状态为人工处理中；";
                        }
                        updateFlag = true;
                    }
                    break;
                default:
                    logger.error(logPrefix + "调用当面付接口-条码支付结果 - 不支持的交易状态，交易返回异常(ouTradeNo={})", outTradeNo);
                    break;
            }
            
            if (updateFlag) {
                // 回置订单号，支付失败时结果中outTradeNo为空
                payResult.getResponse().setOutTradeNo(outTradeNo);
                logger.info(logPrefix + "支付响应转换支付明细 - 开始");
                
                payResultVO = AliPayPackConverter.alipayTradePayResponse2AliPayDetailsVO(payResult.getResponse());
                
                logger.info(logPrefix + "支付响应转换支付明细 - 成功");
                
                // 更新支付结果
                logger.info(logPrefix + "更新支付结果 - 开始");
                if (StringUtils.isNotBlank(updateRemark)) {
                    payResultVO.setRemark(updateRemark);
                }
                payResultVO =aliPayDetailsService.doTransUpdateFace2FacePayResult(payResultVO.getCode(), payResultVO.getSubCode(), payResultVO, updateTradeStatus);
                logger.info(logPrefix + "更新支付结果 - 结束");
                
                if (payResultVO.getTradeStatus().intValue() == TradeStatus.TRADE_SUCCESS.getValue()) {
                    resultMap.put("resultCode", AliPayResult.SUCCESS.getCode());
                    resultMap.put("resultDesc", AliPayResult.SUCCESS.getDesc());
                    logger.info("支付宝条码支付成功，outTradeNo : {}, tradeNo : {}", payResultVO.getOutTradeNo(), payResultVO.getTradeNo());
                } else {
                    if (payResultVO.getTradeStatus().intValue() == TradeStatus.MANUAL_HANDLING.getValue()) {
                        logger.warn("支付宝条码支付结果需要人工处理，outTradeNo : {}，错误码 : {}，错误描述 : {}", payResultVO.getOutTradeNo(), payResultVO.getSubCode(), payResultVO.getSubMsg());
                    } else {
                        logger.warn("支付宝条码支付失败，outTradeNo : {}，错误码 : {}，错误描述 : {}", payResultVO.getOutTradeNo(), payResultVO.getSubCode(), payResultVO.getSubMsg());
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
            logger.error(logPrefix + AlarmLogPrefix.handleAliPayResultException.getValue() + "处理支付宝当面条码支付结果(ouTradeNo={}) - 异常 : {}", outTradeNo, e.getMessage(), e);
        } finally {
            logger.info(logPrefix + "处理支付宝当面条码支付结果 - 结束");
        }
        
        //FIXME 支付失败不返回支付结果或者支付明细信息
        return resultMap;
    }

    
    /**
     * 在撤销结果未知或异常后再次调用支付宝支付查询结果查询交易状态，返回状态用于直接更新系统支付订单状态
     * 
     * @param payDetailsVO
     * @return 支付订单状态
     */
    private Integer queryTradeStatusAfterCancelUnknown(AliPayDetailsVO payDetailsVO) {
        logger.info("撤销后调用支付宝支付查询接口 - 开始");
        try {
            AlipayF2FQueryResult queryTradeResult = AliPayUtil.tradeQuery(payDetailsVO);
            logger.info("撤销后调用支付宝支付查询接口 - 结果 - 订单支付结果 : {}, outTradeNo : {}", queryTradeResult.getTradeStatus(), payDetailsVO.getOutTradeNo());
            // 由于已知调用过撤销。直接查看是否交易关闭
            if (queryTradeResult.getResponse() != null) {
                String queryTradeStatus = queryTradeResult.getResponse().getTradeStatus();
                logger.info("撤销后调用支付宝支付查询接口 - 结果 - 交易状态 : {}, outTradeNo : {}", queryTradeStatus, payDetailsVO.getOutTradeNo());
                if (StringUtils.equalsIgnoreCase(TradeState4AliPay.TRADE_CLOSED.toString(), queryTradeStatus)) {
                    return TradeStatus.TRADE_CLOSED.getValue();
                }
            }
            return TradeStatus.MANUAL_HANDLING.getValue();
        } catch (Exception e) {
            logger.info("调用支付宝支付查询接口 - 异常 : {}", e.getMessage(), e);
            return TradeStatus.MANUAL_HANDLING.getValue();
        } finally {
            logger.info("调用支付宝支付查询接口 - 结束");
        }
    }
    
    @Override
    public Map<String, Object> wapPayCreateOrder(AliPayDetailsVO payDetailsVO) {
        String logPrefix = "手机网站支付 - ";
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
            resultMap.put("aliPayDetailsVO", payDetailsVO);
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
        
        // -------------创建成功后调用手机网站支付接口--------------- //
        logger.info(logPrefix + "调用手机网站支付接口 - 开始");
        
        flag = false;
        AlipayTradeWapPayResponse wapPayResp = null;
        try {
            // 调用手机网站支付
            wapPayResp = AliPayUtil.tradeWapPay(payDetailsVO);
            // 打印应答
            logger.info(logPrefix + "调用手机网站支付接口 - 支付结果 - outTradeNo={}, reponse : {})", outTradeNo,
                wapPayResp == null ? null : JSONUtil.toJSONString(wapPayResp, true));
            
            // FIXME code subCode
            if (wapPayResp != null && StringUtils.isNotBlank(wapPayResp.getBody())) {
                resultMap.put("wapPayRespBody", wapPayResp.getBody());
                flag = true;
            }
        } catch (ConvertPackException e) {
            logger.error(logPrefix + AlarmLogPrefix.sendAliPayRequestException + "支付明细转换支付请求包构造器(ouTradeNo={}) - 异常 : {}", outTradeNo, e.getMessage());
        } catch (Exception e) {
            logger.error(logPrefix + AlarmLogPrefix.invokeAliPayAPIErr.getValue() + "调用手机网站支付接口(ouTradeNo={}) - 异常 : {}", outTradeNo, e.getMessage(), e);
        } finally {
            logger.info(logPrefix + "调用手机网站支付接口 -结束");
        }
        
        if (flag) {
            resultMap.put("resultCode", AliPayResult.SUCCESS.getCode());// 下单成功
            resultMap.put("resultDesc", AliPayResult.SUCCESS.getDesc());
        }
        
        return resultMap;
    }

    @Override
    public Map<String, Object> h5ReturnQueryPayResult(String outTradeNo, String tradeNo, String totalAmount, String sellerId) {
        String logPrefix = "手机网站支付完成后前台回跳处理 - ";
        logger.info(logPrefix + "开始");
        // 校验参数
        Validator.checkArgument(StringUtils.isBlank(outTradeNo), "outTradeNo为空");
        Validator.checkArgument(StringUtils.isBlank(tradeNo), "tradeNo为空");
        // （暂只处理outTradeNo和tradeNo）
        
        Map<String, Object>  resultMap = new HashMap<String, Object>();
        
        // 通过系统订单号查找交易明细
        logger.info(logPrefix + "查询交易明细");
        AliPayDetailsVO payDetailsVO = aliPayDetailsService.doJoinTransQueryAliPayDetailsVOByNum(outTradeNo, tradeNo);
        if (payDetailsVO == null) {
            throw new NotExistsException("支付宝支付明细不存在，outTradeNo=" + outTradeNo + "，tradeNo=" + tradeNo);
        }
        
        
        int tradeStatus = payDetailsVO.getTradeStatus();
        logger.info(logPrefix + "检查交易状态 - 状态 : {}", tradeStatus);
        
        // 根据notify_id是否存在判定是否处理过异步同通知，仅仅作为判断，当前交易状态决定是否继续处理
        String updateRemark = "";
        if (StringUtils.isNotBlank(payDetailsVO.getNotifyId())) {
            logger.warn(logPrefix + "检查交易状态 - 系统已经处理过支付宝发来的支付结果异步通知");
        } else {
            logger.info(logPrefix + "检查交易状态 - 系统还未收到支付宝发来的支付结果异步通知");
        }
        
        // 检查交易状态
        if (SysEnums.TradeStatus.TRADEING.getValue() == tradeStatus) {
            logger.info(logPrefix + "检查交易状态 - 状态处理中，调用查询接口");
            updateRemark = "当前状态处理中，主动查询交易，"; 
            
            // 交易状态处理中，查询支付宝
            AlipayF2FQueryResult queryTradeResult = AliPayUtil.tradeQuery(payDetailsVO);
            logger.info(logPrefix + "调用查询接口 - 交易状态 : {}", queryTradeResult.getTradeStatus());

            // 前台回跳说明支付完成，正常不会出现支付中状态，所以直接将查询结果交易状态更新并返回
            if (com.zbsp.alipay.trade.model.TradeStatus.FAILED.equals(queryTradeResult.getTradeStatus())) {
                // 支付失败，直接更新状态
                logger.warn(logPrefix + "查询交易结果为支付失败 - 准备更新交易状态为支付失败");
                
                // 可能支付宝的交易状态为交易关闭，本系统存交易失败也无碍
                aliPayDetailsService.doTransUpdatePayDetailState(outTradeNo, TradeStatus.TRADE_FAIL.getValue(), updateRemark += "更新状态为交易失败；");
                tradeStatus = TradeStatus.TRADE_FAIL.getValue();
            } else if (com.zbsp.alipay.trade.model.TradeStatus.SUCCESS.equals(queryTradeResult.getTradeStatus())) {
                logger.info(logPrefix + "查询交易结果为支付成功 - 准备更新查询交易信息到支付明细中");
                // 转换VO
                payDetailsVO = AliPayPackConverter.alipayTradeQueryResponse2AliPayDetailsVO(queryTradeResult.getResponse());

                payDetailsVO = aliPayDetailsService.doTransUpdateQueryTradeSuccessResult(payDetailsVO);
                payDetailsVO.setRemark(updateRemark + "更新状态为交易成功；");
                tradeStatus = payDetailsVO.getTradeStatus();
            } else {// 查询发生异常，交易状态未知
                logger.warn(logPrefix + "查询异常，交易未知，更新状态为人工处理中");
                aliPayDetailsService.doTransUpdatePayDetailState(outTradeNo, TradeStatus.MANUAL_HANDLING.getValue(), updateRemark + "更新状态为人工处理中；");
                tradeStatus = TradeStatus.MANUAL_HANDLING.getValue();
            }
        }
        
        resultMap.put("tradeStatus", tradeStatus);
        resultMap.put("aliPayDetailsVO", payDetailsVO);
        
        logger.info(logPrefix + "结束");
        return resultMap;
    }

    @Override
    public Map<String, Object> handleAsynNotify(Map<String, String> paramMap) {
        Validator.checkArgument(paramMap == null, "paramMap为空");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        String logPrefix = "处理支付宝支付结果异步通知请求 - ";
        
        // 通过验签（验证通知中的sign参数）来确保支付通知是由支付宝发送的
        logger.info(logPrefix + "验签 - 开始");
        boolean flag = false;
        String result  = AsynNotifyHandleResult.FAILURE.toString();
        try {
            String sign = paramMap.get("sign");
            logger.info(logPrefix + "验签 -异步通知请求参数sign：{}", sign);
            if (!AlipaySignature.rsaCheckV1(paramMap, Configs.getAlipayPublicKey(), "utf-8", Configs.getSignType())) {
                logger.error(logPrefix + "验签 - 签名错误");
            } else {
                logger.info(logPrefix + "验签 - 签名正确");
                flag = true;
            }
        } catch (AlipayApiException e) {
            logger.error(logPrefix + "验签 - 异常 : {}", e.getMessage(), e);
        } catch (Exception e) {
            logger.error(logPrefix + "验签 - 异常 : {}", e.getMessage(), e);
        } finally {
            logger.info(logPrefix + "验签 - 结束");
            if (!flag) {
                resultMap.put("result", result);
                return resultMap;
            }
        }
        
        flag = false;
        // 转换参数到VO，方便操作
        AlipayWapPayNotifyVO notifyVO = new AlipayWapPayNotifyVO();
        try {
            BeanUtils.populate(notifyVO, paramMap);
            flag = true;
        } catch (IllegalAccessException e) {
            logger.error(logPrefix + "请求参数转换对象异常", e);
        } catch (InvocationTargetException e) {
            logger.error(logPrefix + "请求参数转换对象异常", e);
        } finally {
            if (!flag) {
                resultMap.put("result", result);
                return resultMap;
            }
        }

        flag = false;
        logger.info(logPrefix + "检查通知内容 - 开始");
        
        // 检查通知内容，包括通知中的app_id, out_trade_no, total_amount、seller_id是否与请求中的一致，不一致表明本次通知是异常通知，忽略
        try {
            String outTradeNo = notifyVO.getOut_trade_no();
            String appId = notifyVO.getApp_id();
            String totalAmountStr = notifyVO.getTotal_amount();
            Validator.checkArgument(StringUtils.isBlank(outTradeNo), "outTradeNo为空");
            Validator.checkArgument(StringUtils.isBlank(appId), "appId为空");
            Validator.checkArgument(StringUtils.isBlank(totalAmountStr), "totalAmount为空");
            Validator.checkArgument(!NumberUtils.isCreatable(totalAmountStr) || !Pattern.matches(SysEnvKey.REGEX_￥_POSITIVE_FLOAT_2BIT, totalAmountStr), "totalAmount(" + totalAmountStr + ")格式不正确");
            Validator.checkArgument(StringUtils.isBlank(notifyVO.getTrade_status()), "trade_status为空");
            
            
            AliPayDetailsVO payDetailsVO = aliPayDetailsService.doJoinTransQueryAliPayDetailsVOByNum(outTradeNo, notifyVO.getTrade_no());
            if (payDetailsVO == null) {
                logger.error(AlarmLogPrefix.handleAliPayResultErr + logPrefix + "检查通知内容 - 告警 -异步通知订单（outTradeNo={}）不存在", outTradeNo);
            } else {
                // 比较关键通知参数
                flag = true;
                // 比较app_id
                if (!StringUtils.equals(appId, payDetailsVO.getAppId())) {
                    logger.warn(logPrefix + "检查通知内容 - 失败 - app_id不一致，通知app_id={}, 支付明细app_id={}", appId, payDetailsVO.getAppId());
                    flag = false;
                }
                // 比较total_amount
                BigDecimal yuan = new BigDecimal(totalAmountStr);
                int totalAmountNotify = yuan.multiply(new BigDecimal(100)).intValue();// 元转化为分
                if (totalAmountNotify != payDetailsVO.getTotalAmount()) {
                    logger.warn(logPrefix + "检查通知内容 - 失败 - total_amount不一致，通知total_amount={}, 支付明细total_amount={}", totalAmountNotify, payDetailsVO.getTotalAmount());
                    flag = false;
                }
                
                if (!StringUtils.equals(notifyVO.getSeller_id(), payDetailsVO.getSellerId())) {
                    logger.warn(logPrefix + "检查通知内容 - 失败 - seller_id不一致，通知seller_id={}, 支付明细seller_id={}", notifyVO.getSeller_id(), payDetailsVO.getSellerId());
                    flag = false;
                }
                
                if (flag) {// 关键参数检验通过
                    logger.info(logPrefix + "检查通知内容 - 异步通知关键参数检验通过");
                    
                    // 根据trade_status进行后续业务处理
                    String tradeStatusNotify = notifyVO.getTrade_status();
                    String notifyId = notifyVO.getNotify_id();
                    Validator.checkArgument(StringUtils.isBlank(notifyId), "notifyId为空");
                    
                    logger.info(logPrefix + "异步通知：notify_id：{}, outTradeNo : {}, trade_status：{}", notifyId, outTradeNo, tradeStatusNotify);
                    
                    result = AsynNotifyHandleResult.SUCCESS.toString();
                    
                    String updateRemark = "";
                    // notify_id比较
                    if (payDetailsVO.getNotifyId() == null) {
                        logger.info(logPrefix + "检查notify_id - 首次处理异步通知");
                        updateRemark = "首次处理异步通知，";
                        
                        if (TradeStatus.TRADEING.getValue() != payDetailsVO.getTradeStatus()) {// 前台回跳在前已通过查询接口更新了交易结果
                            logger.info(logPrefix + "({})当前交易状态为{} - 异步通知之前已被处理，更新通知信息(不含状态)", outTradeNo, payDetailsVO.getTradeStatus());
                            aliPayDetailsService.doTransUpdateNotifyResult(notifyVO, null, updateRemark + "交易已被处理，只更新通知信息(不含状态)；");
                        } else {// 当前交易处理中
                            if (TradeState4AliPay.TRADE_CLOSED.toString().equals(tradeStatusNotify)) {
                                if (payDetailsVO.getRefundFee() == null || payDetailsVO.getRefundFee() > 0) {// 交易关闭
                                    logger.info(logPrefix + "异步通知交易已取消，准备更新交易状态为关闭");
                                    aliPayDetailsService.doTransUpdateNotifyResult(notifyVO, TradeStatus.TRADE_CLOSED, updateRemark + "通知交易已取消，更新交易状态为关闭；");
                                } else {// 交易已全额退款，理论首次通知走此分支
                                    logger.info(logPrefix + "异步通知交易已全额退款，交易关闭，准备更新交易状态为关闭");
                                    aliPayDetailsService.doTransUpdateNotifyResult(notifyVO, TradeStatus.TRADE_CLOSED, updateRemark + "通知交易关闭(已全额退款)，更新交易状态为关闭；");
                                }
                            } else if (TradeState4AliPay.TRADE_FINISHED.toString().equals(tradeStatusNotify)) {// 交易不支持退款时，才会首次通知结束
                                logger.info(logPrefix + "异步通知交易结束，准备更新交易状态为成功");
                                aliPayDetailsService.doTransUpdateNotifyResult(notifyVO, TradeStatus.TRADE_SUCCESS, updateRemark + "通知交易结束，更新交易状态为成功；");
                            } else if (TradeState4AliPay.TRADE_SUCCESS.toString().equals(tradeStatusNotify)) {// 交易成功
                                logger.info(logPrefix + "异步通知交易成功，准备更新交易状态为成功");
                                aliPayDetailsService.doTransUpdateNotifyResult(notifyVO, TradeStatus.TRADE_SUCCESS, updateRemark + "通知交易成功，更新交易状态为成功；");
                            } else {// WAIT_BUYER_PAY，不会通知此状态
                                logger.warn(logPrefix + "异步通知交易({})，忽略处理", tradeStatusNotify);
                                result =AsynNotifyHandleResult.FAILURE.toString();
                            }
                        }
                    } else if (StringUtils.equals(notifyId, payDetailsVO.getNotifyId())) {
                        logger.warn(logPrefix + "检查notify_id - 已被处理过，忽略");
                        result =AsynNotifyHandleResult.FAILURE.toString();
                    } else if (!StringUtils.equals(notifyId, payDetailsVO.getNotifyId())) {// 二次通知，可能会是交易已经成功并且已经超过可退款期限
                        logger.info(logPrefix + "检查notify_id - 二次通知，当前notify_id : {}，新的notify_id : {}", payDetailsVO.getNotifyId(), notifyId);
                        logger.info(logPrefix + "检查notify_id - 二次通知，当前状态 : {}，通知交易状态: {}", payDetailsVO.getTradeStatus(), tradeStatusNotify);
                        updateRemark = "异步通知第二次，";
                        if (TradeState4AliPay.TRADE_FINISHED.toString().equals(tradeStatusNotify)) {
                            logger.info(logPrefix + "二次通知交易TRADE_FINISHED，准备更新交易状态为成功");
                            aliPayDetailsService.doTransUpdateNotifyResult(notifyVO, TradeStatus.TRADE_SUCCESS, updateRemark + "通知交易TRADE_FINISHED，更新交易状态为成功；");//FIXME 考虑增加交易状态类别
                        } else {// 理论不会出现，暂不处理
                            logger.error(AlarmLogPrefix.handleAliPayResultErr + "异步二次通知(notifyId=)，通知(状态=)未处理", notifyId, tradeStatusNotify);
                        }
                    }
                } else {
                    logger.warn(logPrefix + "检查通知内容 - 异步通知关键参数检验未通过，忽略");
                }
            }
        } catch (Exception e) {
            logger.error(logPrefix + "检查通知内容 - 异常 : {}", e.getMessage(), e);
        } finally {
            logger.info(logPrefix + "检查通知内容 - 结束");
        }
        
        // 返回
        resultMap.put("result", result);
        return resultMap;
    }

	@Override
	public Map<String, Object> scanPayCreateOrder(AliPayDetailsVO payDetailsVO) {
        String logPrefix = "扫码支付 - ";
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
            resultMap.put("aliPayDetailsVO", payDetailsVO);
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
        
        // -------------创建成功后调用扫码预下单接口--------------- //
        logger.info(logPrefix + "调用扫码预下单接口 - 开始");
        
        flag = false;
        AlipayTradePrecreateResponse precreateResp = null;
        try {
            // 调用手机网站支付
        	precreateResp = AliPayUtil.tradePrecreate(payDetailsVO);
            // 打印应答
            logger.info(logPrefix + "调用扫码预下单接口 - 支付结果 - outTradeNo={}, reponse : {})", outTradeNo,
            		precreateResp == null ? null : JSONUtil.toJSONString(precreateResp, true));
            if (precreateResp != null && StringUtils.isNotBlank(precreateResp.getQrCode())) {
                resultMap.put("qrCode", precreateResp.getQrCode());
                flag = true;
            }
        } catch (ConvertPackException e) {
            logger.error(logPrefix + AlarmLogPrefix.sendAliPayRequestException + "支付明细转换预下单请求包构造器(ouTradeNo={}) - 异常 : {}", outTradeNo, e.getMessage());
        } catch (Exception e) {
            logger.error(logPrefix + AlarmLogPrefix.invokeAliPayAPIErr.getValue() + "调用预下单接口(ouTradeNo={}) - 异常 : {}", outTradeNo, e.getMessage(), e);
        } finally {
            logger.info(logPrefix + "调用扫码预下单接口 -结束");
        }
        if (flag) {
            resultMap.put("resultCode", AliPayResult.SUCCESS.getCode());// 下单成功
            resultMap.put("resultDesc", AliPayResult.SUCCESS.getDesc());
        }
        return resultMap;
	}

}
