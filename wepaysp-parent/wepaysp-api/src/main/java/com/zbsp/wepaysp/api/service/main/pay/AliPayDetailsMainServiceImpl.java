package com.zbsp.wepaysp.api.service.main.pay;

import java.util.HashMap;
import java.util.Map;

import com.zbsp.alipay.trade.model.builder.AlipayTradePayRequestBuilder;
import com.zbsp.alipay.trade.model.result.AlipayF2FPayResult;
import com.zbsp.alipay.trade.service.AlipayTradeService;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.pay.AliPayDetailsService;
import com.zbsp.wepaysp.api.util.AliPayPackConverter;
import com.zbsp.wepaysp.api.util.AliPayUtil;
import com.zbsp.wepaysp.common.constant.AliPayEnums.AliPayResult;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.SysEnums.TradeStatus;
import com.zbsp.wepaysp.common.exception.ConvertPackException;
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
            // 当面付2.0服务
            AlipayTradeService service = AliPayUtil.getDefaultAlipayTradeService();
            
            logger.info(logPrefix + "支付明细转换支付请求包构造器 - 开始");
            // 支付请求构造器
            AlipayTradePayRequestBuilder builder = AliPayPackConverter.aliPayDetailsVO2AlipayTradePayRequestBuilder(payDetailsVO);
            
            logger.info(logPrefix + "支付明细转换支付请求包构造器 - 成功");
            
            // 调用tradePay方法获取当面付应答
            payResult = service.tradePay(builder);
            // 同步返回暂不验签 FIXME 支付中轮询修改
            // 未知交易结果处理
            switch (payResult.getTradeStatus()) {
                case SUCCESS:
                    logger.info(logPrefix + "调用当面付接口-条码支付结果 - 支付成功(ouTradeNo={})", outTradeNo);
                    break;
                case FAILED:// 支付失败或者撤销成功、撤销失败
                    logger.error(logPrefix + "调用当面付接口-条码支付结果 - 支付失败(ouTradeNo={})", outTradeNo);
                    break;
                case UNKNOWN: // 撤销结果异常
                    logger.error(logPrefix + "调用当面付接口-条码支付结果 - 系统异常，订单状态未知(ouTradeNo={})", outTradeNo);
                    break;
                default:
                    logger.error(logPrefix + "调用当面付接口-条码支付结果 - 不支持的交易状态，交易返回异常(ouTradeNo={})", outTradeNo);
                    break;
            }
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
        logger.info(logPrefix + "处理支付宝当面条码支付结果 - 开始");
        try {
            logger.info(logPrefix + "支付响应转换支付明细 - 开始");
            
            //FIXME 连接超时，payResult.getResponse()为空
            payResultVO = AliPayPackConverter.alipayTradePayResponse2AliPayDetailsVO(payResult.getResponse());
            
            logger.info(logPrefix + "支付响应转换支付明细 - 成功");
            
            //FIXME 更新支付结果
            payResultVO =aliPayDetailsService.doTransUpdateFace2FacePayResult(payResultVO.getCode(), payResultVO.getSubCode(), payResultVO);
            
            if (payResultVO.getTradeStatus().intValue() == TradeStatus.TRADE_SUCCESS.getValue()) {
                resCode = AliPayResult.SUCCESS.getCode();
                resDesc = AliPayResult.SUCCESS.getDesc();
                logger.info("系统订单ID=" + payResultVO.getOutTradeNo() + "微信刷卡支付成功，支付宝支付订单ID=" + payResultVO.getTradeNo());
            } else {// 支付失败或错误
                logger.warn("系统订单ID" + outTradeNo + "支付宝条码支付失败，错误码：" + resCode + "，错误描述：" + resDesc);
                resCode = payResultVO.getSubCode();
                resDesc = payResultVO.getSubMsg();
            }
            
            resultMap.put("resultCode", resCode);
            resultMap.put("resultDesc", resDesc);
        } catch (ConvertPackException e) {
            logger.error(logPrefix + AlarmLogPrefix.handleAliPayResultException + "支付响应转换支付明细(ouTradeNo={}) - 异常 : {}", outTradeNo, e.getMessage());
        } catch (Exception e) {
            logger.error(logPrefix + AlarmLogPrefix.handleAliPayResultErr.getValue() + "处理支付宝当面条码支付结果(ouTradeNo={}) - 失败 : {}", outTradeNo, e.getMessage(), e);
        } finally {
            logger.info(logPrefix + "处理支付宝当面条码支付结果 - 结束");
        }
        
        //FIXME 是否必须返回数据库中的记录，客户端？
        resultMap.put("aliPayDetailsVO", payResultVO == null ? payDetailsVO : payResultVO);
        
        return resultMap;
    }

    public void setAliPayDetailsService(AliPayDetailsService aliPayDetailsService) {
        this.aliPayDetailsService = aliPayDetailsService;
    }
    
}
