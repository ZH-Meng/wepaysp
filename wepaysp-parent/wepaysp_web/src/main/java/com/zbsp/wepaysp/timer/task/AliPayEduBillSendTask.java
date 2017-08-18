package com.zbsp.wepaysp.timer.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipayEcoEduKtBillingSendResponse;
import com.zbsp.alipay.trade.config.Constants;
import com.zbsp.wepaysp.api.service.edu.AlipayEduBillService;
import com.zbsp.wepaysp.api.service.edu.AlipayEduTotalBillService;
import com.zbsp.wepaysp.api.util.AliPayEduUtil;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.po.edu.AlipayEduBill;
import com.zbsp.wepaysp.po.edu.AlipayEduTotalBill;
import com.zbsp.wepaysp.po.edu.AlipayEduBill.OrderStatus;

/**
 * 支付宝教育缴费账单发送任务<br>
 * 
 * @author mengzh
 */
@Component
public class AliPayEduBillSendTask
    extends TimerBasicTask {

    private static String LOG_PREFIX = "[定时任务] - [支付宝教育缴费账单发送] - ";

    @Value("${eduBillSendValidMins}")
    private int validMins = 60;// 待发送账单的有效分钟数
    @Value("${eduSendIntervalMilliSecs}")
    private int sendIntervalMilliSecs = 200;// 发送账单的间隔毫秒数

    @Autowired
    private AlipayEduTotalBillService alipayEduTotalBillService;
    @Autowired
    private AlipayEduBillService alipayEduBillService;

    @Override
    public void doJob() {
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[开始]"));

        try {
            // ① 查出新建（明细未发送或者未全部发送）的账单集合
            List<AlipayEduTotalBill> watingSendList = alipayEduTotalBillService.doJoinTransQueryTotalBillOfWaitingSend(validMins);
            logger.info(StringHelper.combinedString(LOG_PREFIX, "[ 需要发送的账单数量：{}]"), (watingSendList != null && !watingSendList.isEmpty()) ? watingSendList.size() : 0);

            List<AlipayEduTotalBill> sendSuceessList = new ArrayList<>();// 明细全部发送成功的账单集合

            boolean totalBillSucess = true;
            AlipayEcoEduKtBillingSendResponse response = null;

            // ② 遍历待发送的账单集合
            for (AlipayEduTotalBill totalBill : watingSendList) {

                // ③ 查找待发送账单totalBill的明细集合
                List<AlipayEduBill> billList = alipayEduBillService.doJoinTransQueryAlipayEduBillByStatus(totalBill.getIwoid(), OrderStatus.INIT);
                logger.info(StringHelper.combinedString(LOG_PREFIX, "[ {}需要发送的账单明细数量：{}]"), totalBill.getBillName(), (billList != null && !billList.isEmpty()) ? billList.size() : 0);

                // ④ 遍历并调用缴费账单发送接口，暂只发送一次
                if (billList != null && !billList.isEmpty()) {
                    for (AlipayEduBill bill : billList) {
                        try {
                            response = AliPayEduUtil.billSend(bill);
                            logger.info(LOG_PREFIX + "outTradeNo:{}, 账单发送结果:{}", bill.getOutTradeNo(), JSONUtil.toJSONString(response, true));
                            if (response == null || !Constants.SUCCESS.equals(response.getCode())) {// 交易或者结束
                                logger.warn(LOG_PREFIX + "发送失败");
                                totalBillSucess = false;
                            } else {
                                logger.info(LOG_PREFIX + "发送成功");
                                bill.setStudentNo(response.getStudentNo());
                                bill.setOrderStatus(OrderStatus.NOT_PAY.name());
                                bill.setK12OrderNo(response.getOrderNo());
                                // ⑤ 更新账单明细状态为已发送 待支付
                                alipayEduBillService.doTransUpdateAlipayEduBill(bill);
                            }

                            // 间隔sendIntervalMilliSecs毫秒
                            Thread.sleep(sendIntervalMilliSecs);
                        } catch (AlipayApiException e) {
                            logger.error(StringHelper.combinedString(LOG_PREFIX, AlarmLogPrefix.invokeAliPayAPIErr.getValue(), e.getMessage()));
                            totalBillSucess = false;
                        } catch (Exception e) {
                            logger.error(StringHelper.combinedString(LOG_PREFIX, "异常:\n{}"), e.getMessage(), e);
                            totalBillSucess = false;
                        }
                    }
                }

                // ⑥ 若totalBill账单的明细全部发送成功，设置发送时间和状态（发送成功）
                if (totalBillSucess) {
                    totalBill.setSendTime(new Date());
                    totalBill.setOrderStatus(com.zbsp.wepaysp.po.edu.AlipayEduTotalBill.OrderStatus.SEND_SUCCESS.name());
                    sendSuceessList.add(totalBill);
                    logger.info(StringHelper.combinedString(LOG_PREFIX, "账单（{}）发送成功"), totalBill.getBillName());
				} else
					totalBillSucess = true;
            }
            
            if (!sendSuceessList.isEmpty()) {
                //  批量更新发送成功的账单集合
                alipayEduTotalBillService.doTransUpdateTotalBillList(sendSuceessList);
            }
        } catch (Exception e) {
            logger.error(StringHelper.combinedString(LOG_PREFIX, "异常:\n{}"), e.getMessage(), e);
        }

        logger.info(StringHelper.combinedString(LOG_PREFIX, "[结束]"));
    }

}
