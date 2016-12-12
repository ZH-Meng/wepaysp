package com.zbsp.wepaysp.timer.task;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tencent.WXPay;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.zbsp.wepaysp.api.listener.DefaultOrderQueryBusinessResultListener;
import com.zbsp.wepaysp.api.service.main.pay.WeixinPayDetailsMainService;
import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.common.constant.EnumDefine;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;

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
        // 查出前intervalTime毫秒的交易处理中的记录
        List<WeixinPayDetails> tradingList = weixinPayDetailsService.doJoinTransQueryWeixinPayDetailsByState(WeixinPayDetails.TradeStatus.TRADEING.getValue(), intervalTime);
        
        // 开始时间限制时间
        Date minDate = new Date(new Date().getTime() - maxQueryIntervaltime);
        
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 需要处理的记录数：" + ((tradingList != null && !tradingList.isEmpty()) ? tradingList.size() : 0) +  " ]"));
        if (tradingList != null && !tradingList.isEmpty()) {
            int closeTimes = 0;
            int queryTimes = 0;
            for (WeixinPayDetails payDetail : tradingList) {
                // 交易时间是否在1小时之前
                if (TimeUtil.timeBefore(payDetail.getTransBeginTime(), minDate)) {
                    //TODO 调用关闭订单API
                    closeTimes ++;
                } else {// 查询订单
                    logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 开始调用查询订单API ]", " - [weixinPayDetailsOid=" + payDetail.getIwoid() +" ]"));
                    String partnerOid = payDetail.getPartner1Oid();
                    // 查找服务商配置信息                    
                    // FIXME 静态开发数据
                    String keyPartner = EnumDefine.DevParam.KEY.getValue();
                    String certLocalPath = EnumDefine.DevParam.CERT_LOCAL_PATH.getValue();
                    String certPassword = EnumDefine.DevParam.CERT_PASSWORD.getValue();
                    // 调用查询订单API
                    try {
                        WXPay.doOrderQueryBusiness(
                            new ScanPayQueryReqData(payDetail.getTransactionId(), payDetail.getOutTradeNo(), keyPartner, payDetail.getAppid(), payDetail.getMchId(), payDetail.getSubMchId()),
                            new DefaultOrderQueryBusinessResultListener(weixinPayDetailsMainService), certLocalPath, certPassword, keyPartner);
                    } catch (Exception e) {
                        logger.error("订单查询错误，weixinPayDetailsOid=" + payDetail.getIwoid() + "，异常：" + e.getMessage());
                        e.printStackTrace();
                    }
                    queryTimes ++;
                    logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 调用查询订单API结束 ]", " - [weixinPayDetailsOid=" + payDetail.getIwoid() +" ]"));
                }
            }
            
            logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 调用查询订单API次数：" + queryTimes +  " ]", " - [ 调用关闭订单API次数：" + closeTimes +  " ]"));
        }
        
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[结束]"));
    }
}
