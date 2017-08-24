package com.zbsp.wepaysp.timer.task;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tencent.WXPay;
import com.tencent.protocol.close_order_protocol.CloseOrderReqData;
import com.tencent.protocol.pay_query_protocol.ScanPayQueryReqData;
import com.zbsp.wepaysp.api.listener.DefaultCloseOrderBusinessResultListener;
import com.zbsp.wepaysp.api.listener.DefaultOrderQueryBusinessResultListener;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.main.pay.WeixinPayDetailsMainService;
import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.SysEnums.PayType;
import com.zbsp.wepaysp.common.constant.SysEnums.TradeStatus;
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
        // 查出前intervalTime毫秒的交易处理中和待关闭、用户支付中的记录
        List<WeixinPayDetails> tradingList = weixinPayDetailsService.doJoinTransQueryWeixinPayDetailsByState(
            new int[] { TradeStatus.TRADEING.getValue(), TradeStatus.TRADE_TO_BE_CLOSED.getValue(), TradeStatus.TRADE_PAYING.getValue() }, intervalTime);
        
        // 开始时间限制时间
        Date minDate = new Date(new Date().getTime() - maxQueryIntervaltime);
        
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 需要处理的记录数：" + ((tradingList != null && !tradingList.isEmpty()) ? tradingList.size() : 0) +  " ]"));
        if (tradingList != null && !tradingList.isEmpty()) {
            int closeSuccTimes = 0;
            int querySuccTimes = 0;
            int closeErrTimes = 0;
            int queryErrTimes = 0; 
            boolean closeFlag = false; 
    		
    		Map<String, Object> partnerMap = null;
    		String certLocalPath = null;
    		String certPassword = null;
    		String keyPartner = null;
            for (WeixinPayDetails payDetail : tradingList) {
            	if (StringUtils.isBlank(payDetail.getPartner1Oid())) {
            		logger.error("订单信息缺失：partner1Oid为空！");
            	}
            	
            	// 从内存中获取服务商配置信息
            	partnerMap = SysConfig.partnerConfigMap.get(payDetail.getPartner1Oid());
            	if (partnerMap != null && !partnerMap.isEmpty()) {
            		certLocalPath = MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH);
            		certPassword = MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_PASSWORD);
            		keyPartner = MapUtils.getString(partnerMap, SysEnvKey.WX_KEY);
        		} else {
        			throw new RuntimeException("系统数据异常，服务商配置信息不存在");
        		}
            	
                if (payDetail.getTradeStatus() != null && payDetail.getTradeStatus() == TradeStatus.TRADE_TO_BE_CLOSED.getValue()) {
                    closeFlag = true;
                } else {
                    if (PayType.WEIXIN_MICROPAY.getValue().equals(payDetail.getPayType())) {
                        // 刷卡支付设置了过期时间 2分钟
                        if (TimeUtil.timeAfter(new Date(), TimeUtil.plusSeconds(payDetail.getTransBeginTime(), SysEnvKey.WX_MICROPAY_EXPIRE_SECS))) 
                            closeFlag = true; 
                    } else {
                        if (TimeUtil.timeBefore(payDetail.getTransBeginTime(), minDate)) // 交易时间是否在1小时之前
                            closeFlag = true;
                    }
                }
                
                if (closeFlag) {
                    if (PayType.WEIXIN_MICROPAY.getValue().equals(payDetail.getPayType())) {
                        // 刷卡支付不支持关闭订单，刷卡支付设置了过期时间，直接关闭系统订单。
                        weixinPayDetailsService.doTransUpdatePayDetailState(payDetail.getOutTradeNo(), TradeStatus.TRADE_CLOSED, "自主关闭-支付超时");
                        logger.info(LOG_PREFIX + "刷卡支付在用户输入密码超过过期时间后自主关闭，outTradeNo={}", payDetail.getOutTradeNo());
                    } else if (PayType.WEIXIN_JSAPI.getValue().equals(payDetail.getPayType())) {
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
                                "系统支付订单(ID=" + payDetail.getOutTradeNo() + "）关闭错误", "，异常信息：" + e.getMessage()), e);
                            closeErrTimes++;
                        }
                        logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 调用关闭订单API结束 ]", " - [系统支付订单ID=" + payDetail.getOutTradeNo() +" ]"));
                    }
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
                            "系统支付订单(ID=" + payDetail.getOutTradeNo() + "）查询错误", "，异常信息：" + e.getMessage()), e);
                        queryErrTimes++;
                    }
                    logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 调用查询订单API结束 ]", " - [系统支付订单ID=" + payDetail.getOutTradeNo() +" ]"));
                }
                
                closeFlag = false;
            }
            
            logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 调用查询订单API成功次数：" + querySuccTimes + ", 失败次数：" + queryErrTimes + " ]",
                " - [ 调用关闭订单API成功次数：" + closeSuccTimes +  ", 失败次数：" + closeErrTimes + " ]"));
        }
        
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[结束]"));
    }
}
