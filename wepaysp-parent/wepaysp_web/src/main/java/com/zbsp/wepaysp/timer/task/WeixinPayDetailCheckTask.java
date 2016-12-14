package com.zbsp.wepaysp.timer.task;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tencent.WXPay;
import com.tencent.protocol.close_order_protocol.CloseOrderReqData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.zbsp.wepaysp.api.listener.DefaultCloseOrderBusinessResultListener;
import com.zbsp.wepaysp.api.listener.DefaultOrderQueryBusinessResultListener;
import com.zbsp.wepaysp.api.service.main.pay.WeixinPayDetailsMainService;
import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.common.constant.EnumDefine;
import com.zbsp.wepaysp.common.constant.EnumDefine.AlarmLogPrefix;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails.TradeStatus;

/**
 * 微信支付交易明细状态检查作业，状态处理中的需要处理
 * 
 * @author mengzh
 */
@Component
public class WeixinPayDetailCheckTask extends TimerBasicTask {
    
    private static String LOG_PREFIX = "[定时任务] - [处理交易状态处理中的支付明细] - ";
    
    /**间隔时间限制，默认1小时之前的不在查询，直接关闭订单*/
    @Value("${maxQueryIntervaltime}")
    private long maxQueryIntervaltime = 1000 * 60 * 60;
    
    @Autowired
    private WeixinPayDetailsService weixinPayDetailsService;
    @Autowired
    private WeixinPayDetailsMainService weixinPayDetailsMainService;

    @Override
    public void doJob() {
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[开始]"));
        // 查出前intervalTime毫秒的交易处理中和待关闭的记录
        List<WeixinPayDetails> tradingList = weixinPayDetailsService.doJoinTransQueryWeixinPayDetailsByState(
            new int[] { WeixinPayDetails.TradeStatus.TRADEING.getValue(), WeixinPayDetails.TradeStatus.TRADE_TO_BE_CLOSED.getValue() }, intervalTime);
        
        // 开始时间限制时间
        Date minDate = new Date(new Date().getTime() - maxQueryIntervaltime);
        
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 需要处理的记录数：" + ((tradingList != null && !tradingList.isEmpty()) ? tradingList.size() : 0) +  " ]"));
        if (tradingList != null && !tradingList.isEmpty()) {
            int closeSuccTimes = 0;
            int querySuccTimes = 0;
            int closeErrTimes = 0;
            int queryErrTimes = 0; 
            boolean closeFlag = false; 
            for (WeixinPayDetails payDetail : tradingList) {
                // String partnerOid = payDetail.getPartner1Oid();
                // 查找服务商配置信息                    
                // FIXME 静态开发数据
                String keyPartner = EnumDefine.DevParam.KEY.getValue();
                String certLocalPath = EnumDefine.DevParam.CERT_LOCAL_PATH.getValue();
                String certPassword = EnumDefine.DevParam.CERT_PASSWORD.getValue();
                if (payDetail.getTradeStatus() != null && payDetail.getTradeStatus() == TradeStatus.TRADE_TO_BE_CLOSED.getValue()) {
                    closeFlag = true;
                } else {
                    if (TimeUtil.timeBefore(payDetail.getTransBeginTime(), minDate)) {// 交易时间是否在1小时之前
                        closeFlag = true;
                    }
                }
                
                if (closeFlag) {
                    // 调用关闭订单API
                    logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 开始调用关闭订单API ]", " - [系统支付订单ID=" + payDetail.getOutTradeNo() +" ]"));
                    try {
                        DefaultCloseOrderBusinessResultListener closeOrderListener= new DefaultCloseOrderBusinessResultListener(weixinPayDetailsService);
                        WXPay.doCloseOrderBusiness(
                            new CloseOrderReqData(payDetail.getOutTradeNo(), keyPartner, payDetail.getAppid(), payDetail.getMchId(), payDetail.getSubMchId()), 
                            closeOrderListener, certLocalPath, certPassword, keyPartner);
                        if (!DefaultCloseOrderBusinessResultListener.ON_CLOSE_ORDER_FAIL.equals(closeOrderListener.getResult()) && 
                            !DefaultCloseOrderBusinessResultListener.ON_CLOSE_ORDER_SUCCESS.equals(closeOrderListener.getResult())) {
                            closeErrTimes++;
                            return;
                        }
                        closeSuccTimes++;
                    } catch (Exception e) {
                        logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxPayAPIErr.getValue(), 
                            "系统支付订单(ID=" + payDetail.getOutTradeNo() + "）关闭错误", "，异常信息：" + e.getMessage()));
                        logger.error(e.getMessage(), e);
                        closeErrTimes++;
                    }
                    logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 调用关闭订单API结束 ]", " - [系统支付订单ID=" + payDetail.getOutTradeNo() +" ]"));
                } else {// 查询订单
                    logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 开始调用查询订单API ]", " - [系统支付订单ID=" + payDetail.getOutTradeNo() +" ]"));
                    // 调用查询订单API
                    try {
                        DefaultOrderQueryBusinessResultListener orderQueryListener = new DefaultOrderQueryBusinessResultListener(weixinPayDetailsMainService); 
                        WXPay.doOrderQueryBusiness(
                            new ScanPayQueryReqData(payDetail.getTransactionId(), payDetail.getOutTradeNo(), keyPartner, payDetail.getAppid(), payDetail.getMchId(), payDetail.getSubMchId()),
                            orderQueryListener, certLocalPath, certPassword, keyPartner);
                        if (!DefaultOrderQueryBusinessResultListener.ON_ORDER_QUERY_FAIL.equals(orderQueryListener.getResult()) && 
                            !DefaultOrderQueryBusinessResultListener.ON_ORDER_QUERY_SUCCESS.equals(orderQueryListener.getResult())) {
                            queryErrTimes++;
                            return;
                        }
                        querySuccTimes++;
                    } catch (Exception e) {
                        logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxPayAPIErr.getValue(), 
                            "系统支付订单(ID=" + payDetail.getOutTradeNo() + "）关闭错误", "，异常信息：" + e.getMessage()));
                        logger.error(e.getMessage(), e);
                        e.printStackTrace();
                        queryErrTimes++;
                    }
                    logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 调用查询订单API结束 ]", " - [系统支付订单ID=" + payDetail.getOutTradeNo() +" ]"));
                }
            }
            
            logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 调用查询订单API成功次数：" + querySuccTimes + ", 失败次数：" + queryErrTimes + " ]",
                " - [ 调用关闭订单API成功次数：" + closeSuccTimes +  ", 失败次数：" + closeErrTimes + " ]"));
        }
        
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[结束]"));
    }
}
