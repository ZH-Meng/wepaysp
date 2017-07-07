package com.zbsp.wepaysp.timer.task;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.zbsp.alipay.trade.model.result.AlipayF2FQueryResult;
import com.zbsp.wepaysp.api.service.pay.AliPayDetailsService;
import com.zbsp.wepaysp.api.util.AliPayPackConverter;
import com.zbsp.wepaysp.api.util.AliPayUtil;
import com.zbsp.wepaysp.common.constant.AliPayEnums.GateWayResponse;
import com.zbsp.wepaysp.common.constant.AliPayEnums.TradeCancelAction;
import com.zbsp.wepaysp.common.constant.AliPayEnums.TradeState4AliPay;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.SysEnums.TradeStatus;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.po.pay.AliPayDetails;

/**
 * 只考虑了当面付，接入其他支付后调整<br>
 * 
 * 支付宝支付交易明细状态检查作业，状态处理中的需要处理<br>
 * 查出当面付达到过期时间仍处理中的交易，查询支付宝平台的交易状态进行同步。
 * <p>如果支付宝未将订单关闭，则主动发起撤销
 * 
 * @author mengzh
 */
@Component
public class AliPayDetailCheckTask extends TimerBasicTask {
    
    private static String LOG_PREFIX = "[定时任务] - [处理交易状态处理中的支付宝支付明细] - ";
    
    /**
     * 由于当面付刷卡支付SDK会立即处理结果不会出现处理中的结果，扫码支付支付成功会发送后来通知，故不需要查询短时间内的交易<br> 
     * 查询30分钟之前的处理中的数据
     */
    private long intervalTime = 60 * 30L;// 秒
    
    @Autowired
    private AliPayDetailsService aliPayDetailsService;

    @Override
    public void doJob() {
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[开始]"));
        
        // 查出30分钟之前的处理中的数据
        List<AliPayDetails> tradingList = aliPayDetailsService.doJoinTransQueryAliPayDetailsByState(new int[] { TradeStatus.TRADEING.getValue() }, intervalTime);
        
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 需要处理的记录数：" + ((tradingList != null && !tradingList.isEmpty()) ? tradingList.size() : 0) +  " ]"));
        AlipayTradeQueryResponse response = null;
        AlipayF2FQueryResult queryResult = null;
        if (tradingList != null && !tradingList.isEmpty()) {
            for (AliPayDetails aliPayDetails : tradingList) {
                try {
                    // 发起交易查询（用于同步支付宝的交易状态）
                    queryResult = AliPayUtil.tradeQuery(aliPayDetails.getOutTradeNo(), aliPayDetails.getTradeNo(), aliPayDetails.getAppAuthToken());
                    response = queryResult.getResponse();
                    if (queryResult.isTradeSuccess()) {// 交易或者结束
                        if (TradeState4AliPay.TRADE_SUCCESS.toString().equalsIgnoreCase(response.getTradeStatus())
                            || TradeState4AliPay.TRADE_FINISHED.toString().equalsIgnoreCase(response.getTradeStatus())) {
                            aliPayDetailsService.doTransUpdateQueryTradeSuccessResult(AliPayPackConverter.alipayTradeQueryResponse2AliPayDetailsVO(response));
                        }
                    } else if (com.zbsp.alipay.trade.model.TradeStatus.FAILED == queryResult.getTradeStatus()) {
                        // 只处理交易不存在和交易支付中的情况
                        if (GateWayResponse.FAIL.getCode().equals(response.getCode()) && "ACQ.TRADE_NOT_EXIST".equals(response.getSubCode())) {
                            // 如果交易不存在，直接关闭订单
                            aliPayDetailsService.doTransUpdatePayDetailState(aliPayDetails.getOutTradeNo(), TradeStatus.TRADE_CLOSED.getValue(), "定时查询支付宝，返回交易不存在");
                        } else if (GateWayResponse.SUCCESS.getCode().equals(response.getCode())) {
                            if (TradeState4AliPay.TRADE_CLOSED.toString().equalsIgnoreCase(response.getTradeStatus())) {
                                aliPayDetailsService.doTransUpdatePayDetailState(aliPayDetails.getOutTradeNo(), TradeStatus.TRADE_CLOSED.getValue(), "定时查询支付宝，返回交易状态为TRADE_CLOSED");
                            } else if (TradeState4AliPay.WAIT_BUYER_PAY.toString().equalsIgnoreCase(response.getTradeStatus())) {// 正常不会进入此分支，刷卡支付或预下单时设定了过期时间
                                if (TimeUtil.timeBefore(new Date(aliPayDetails.getTransBeginTime().getTime()), TimeUtil.getLastTimeUnit(new Date(), Calendar.DAY_OF_MONTH))) {// 24小时之前的交易不能插销
                                    logger.info(StringHelper.combinedString(LOG_PREFIX, "交易待支付时间超过24小时，调用关闭交易接口"));
                                    // 此分支不会进入，为修复之前的数据而写
                                    AlipayTradeCloseResponse closeResponse = AliPayUtil.tradeClose(aliPayDetails.getOutTradeNo(), aliPayDetails.getAppAuthToken());
                                    if (closeResponse == null) {
                                        logger.error(StringHelper.combinedString(LOG_PREFIX, AlarmLogPrefix.invokeAliPayAPIErr.getValue(), "调用关闭交易接口没响应"));
                                    } else if (GateWayResponse.SUCCESS.getCode().equals(closeResponse.getCode())) {
                                        aliPayDetailsService.doTransUpdatePayDetailState(aliPayDetails.getOutTradeNo(), TradeStatus.TRADE_CLOSED.getValue(), "定时任务关闭过期订单");
                                    } else {
                                        logger.warn(StringHelper.combinedString(LOG_PREFIX, "交易过期关闭失败({})，outTradeNo={}"), closeResponse.getSubCode(), aliPayDetails.getOutTradeNo());
                                    }
                                } else {
                                    logger.info(StringHelper.combinedString(LOG_PREFIX, "交易待支付过期，调用撤销交易接口"));
                                    // 交易时间（预下单时间）在30分钟之前的即已过期订单，但是支付宝平台还为关闭的，系统主动发起关闭
                                    AlipayTradeCancelResponse cancelResponse = AliPayUtil.tradeCancel(aliPayDetails.getOutTradeNo(), aliPayDetails.getAppAuthToken());
                                    
                                    // 撤销成功更新交易结果为关闭
                                    if (cancelResponse == null ) {
                                        logger.error(StringHelper.combinedString(LOG_PREFIX, AlarmLogPrefix.invokeAliPayAPIErr.getValue(), "调用撤销交易接口没响应"));
                                    } else if (GateWayResponse.SUCCESS.getCode().equals(cancelResponse.getCode())) {
                                        if (TradeCancelAction.refund.toString().equalsIgnoreCase(cancelResponse.getAction())) {// 30分钟过期的交易订单（用户待支付），查询后立即撤销
                                            aliPayDetailsService.doTransUpdatePayDetailState(aliPayDetails.getOutTradeNo(), TradeStatus.TRADE_REVERSED.getValue(), "定时任务撤销过期订单，发生退款");
                                        } else {
                                            aliPayDetailsService.doTransUpdatePayDetailState(aliPayDetails.getOutTradeNo(), TradeStatus.TRADE_CLOSED.getValue(), "定时任务撤销过期订单，未发生退款");
                                        }
                                    } else {
                                        // 撤销失败
                                        logger.warn(StringHelper.combinedString(LOG_PREFIX, "交易过期撤销失败({})，outTradeNo={}"), cancelResponse.getSubCode(), aliPayDetails.getOutTradeNo());
                                    }
                                }
                            }
                        } else {
                            logger.error(StringHelper.combinedString(LOG_PREFIX, AlarmLogPrefix.handleAliPayResultErr.getValue(), "调用查询交易接口响应未处理"));
                        }
                    }
                } catch (Exception e) {
                    logger.error(StringHelper.combinedString(LOG_PREFIX, "异常:\n{}"), e.getMessage(), e);
                }
            }
        }
        
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[结束]"));
    }
}
