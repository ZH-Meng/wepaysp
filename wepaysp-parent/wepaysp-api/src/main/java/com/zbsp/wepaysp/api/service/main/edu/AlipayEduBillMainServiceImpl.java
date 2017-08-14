package com.zbsp.wepaysp.api.service.main.edu;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.response.AlipayEcoEduKtBillingModifyResponse;
import com.zbsp.alipay.trade.config.Constants;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.edu.AlipayEduBillService;
import com.zbsp.wepaysp.api.service.edu.AlipayEduNotifyService;
import com.zbsp.wepaysp.api.util.AliPayEduUtil;
import com.zbsp.wepaysp.common.constant.AliPayEnums.AsynNotifyHandleResult;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.edu.AlipayEduBill;
import com.zbsp.wepaysp.po.edu.AlipayEduNotify;
import com.zbsp.wepaysp.po.edu.AlipayEduBill.OrderStatus;
import com.zbsp.wepaysp.vo.alipay.AlipayWapPayNotifyVO;

public class AlipayEduBillMainServiceImpl
    implements AlipayEduBillMainService {

    protected final Logger logger = LogManager.getLogger(getClass());
    
    private AlipayEduBillService alipayEduBillService;
    private AlipayEduNotifyService alipayEduNotifyService;
    
    public Map<String, Object> handleAlipayPayEduBillNotify(Map<String, String> paramMap) {
        Validator.checkArgument(paramMap == null, "paramMap为空");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        String logPrefix = "处理支付宝教育缴费账单异步通知请求 - ";
        
        // 通过验签（验证通知中的sign参数）来确保支付通知是由支付宝发送的
        logger.info(logPrefix + "验签 - 开始");
        boolean flag = false;
        String result  = AsynNotifyHandleResult.FAILURE.toString();
        try {
            String signType = MapUtils.getString(SysConfig.alipayAppMap.get(SysConfig.appId4Edu), SysEnvKey.ALIPAY_APP_SIGN_TYPE);
            String alipayPublicKey = MapUtils.getString(SysConfig.alipayAppMap.get(SysConfig.appId4Edu), SysEnvKey.ALIPAY_PUBLIC_KEY);
            if (!AlipaySignature.rsaCheckV1(paramMap, alipayPublicKey, "utf-8", signType)) {
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

        logger.info(logPrefix + "记录通知内容并处理账单 - 开始");
        try {
            // 记录通知
            AlipayEduNotify eduNotify = alipayEduNotifyService.doTransSaveEduNotify(notifyVO);
            
            // 检查重要参数app_id、totalAmount、seller_id
            if (!StringUtils.equals(notifyVO.getApp_id(), eduNotify.getAlipayEduBill().getAppId())) {
                logger.warn("检查通知内容 - 失败 - app_id不一致，通知app_id={}, 账单明细app_id={}", notifyVO.getApp_id(), eduNotify.getAlipayEduBill().getAppId());
            } else {
                result  = AsynNotifyHandleResult.SUCCESS.toString();
                // TODO 通过线程池任务来异步处理，账单同步超时处理
                
                // 根据通知处理账单
                AlipayEduBill bill = alipayEduBillService.doTransUpdateBillByAlipayEduNotify(eduNotify);
                
                if (OrderStatus.PAY_SUCCESS.name().equalsIgnoreCase(bill.getOrderStatus())) {
                    // 同步缴费成功进行销帐
                    AlipayEcoEduKtBillingModifyResponse response = AliPayEduUtil.billModify(bill, 1);
                    
                    logger.info("调用同步账单状态（缴费成功）接口，k12OrderNo：{}, 响应：{}", bill.getK12OrderNo(), JSONUtil.toJSONString(response, true));
                    if (response == null || !Constants.SUCCESS.equals(response.getCode())) {// 交易或者结束
                        logger.error(StringHelper.combinedString("同步账单状态（缴费成功）失败", AlarmLogPrefix.invokeAliPayAPIErr.getValue()));
                    } else {
                        // 同步成功
                        bill.setOrderStatus(OrderStatus.BILLING_SUCCESS.name());
                        alipayEduBillService.doTransUpdateAlipayEduBill(bill);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(logPrefix + "记录通知内容并处理账单 - 异常 : {}", e.getMessage(), e);
        } finally {
            logger.info(logPrefix + "记录通知内容并处理账单 - 结束");
        }
        
        // 返回
        resultMap.put("result", result);
        return resultMap;
    }
    
    public void setAlipayEduBillService(AlipayEduBillService alipayEduBillService) {
        this.alipayEduBillService = alipayEduBillService;
    }

    public void setAlipayEduNotifyService(AlipayEduNotifyService alipayEduNotifyService) {
        this.alipayEduNotifyService = alipayEduNotifyService;
    }
    
}
