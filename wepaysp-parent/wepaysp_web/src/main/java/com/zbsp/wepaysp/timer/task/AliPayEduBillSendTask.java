package com.zbsp.wepaysp.timer.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayEcoEduKtBillingSendResponse;
import com.zbsp.alipay.trade.config.Constants;
import com.zbsp.wepaysp.api.service.edu.AlipayEduBillService;
import com.zbsp.wepaysp.api.util.AliPayEduUtil;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.po.edu.AlipayEduBill;
import com.zbsp.wepaysp.po.edu.AlipayEduBill.OrderStatus;

/**
 * 支付宝教育缴费账单发送任务<br>
 * <ol>
 * <li>查找状态为INIT的账单明细
 * <li>调用支付宝edu 账单发送接口。
 * 
 * @author mengzh
 */
@Component
public class AliPayEduBillSendTask extends TimerBasicTask {
    
    private static String LOG_PREFIX = "[定时任务] - [支付宝教育缴费账单发送] - ";
    
    @Autowired
    private AlipayEduBillService alipayEduBillService;

    @Override
    public void doJob() {
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[开始]"));
        
        // 查出新建待发送的账单明细
        List<AlipayEduBill> billList = alipayEduBillService.doJoinTransQueryAlipayEduBillByStatus(OrderStatus.INIT);
        
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 需要发送的账单明细数量：" + ((billList != null && !billList.isEmpty()) ? billList.size() : 0) +  " ]"));
        AlipayEcoEduKtBillingSendResponse response = null;
        if (billList != null && !billList.isEmpty()) {
            for (AlipayEduBill bill : billList) {
                try {
                    response = AliPayEduUtil.billSend(bill);
                    logger.info(LOG_PREFIX + "outTradeNo:{}, 账单发送结果:{}", bill.getOutTradeNo(), JSONUtil.toJSONString(response, true));
                    if (response == null || !Constants.SUCCESS.equals(response.getCode())) {// 交易或者结束
                        logger.warn(LOG_PREFIX + "发送失败");
                    } else {
                        logger.info(LOG_PREFIX + "发送成功");
                        bill.setStudentNo(response.getStudentNo());
                        bill.setOrderStatus(OrderStatus.NOT_PAY.name());
                        bill.setK12OrderNo(response.getOrderNo());
                        alipayEduBillService.doTransUpdateAlipayEduBill(bill);
                    }
                    Thread.sleep(1000);
                } catch (AlipayApiException e) { 
                    logger.error(StringHelper.combinedString(LOG_PREFIX, AlarmLogPrefix.invokeAliPayAPIErr.getValue(), e.getMessage()));
                } catch (Exception e) {
                    logger.error(StringHelper.combinedString(LOG_PREFIX, "异常:\n{}"), e.getMessage(), e);
                }
            }
        }
        
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[结束]"));
    }
}
