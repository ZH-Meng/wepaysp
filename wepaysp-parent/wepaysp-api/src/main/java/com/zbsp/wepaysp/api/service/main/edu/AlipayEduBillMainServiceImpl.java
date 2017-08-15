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
            if (!AlipaySignature.rsaCheckV1(paramMap, alipayPublicKey, MapUtils.getString(paramMap, "charset"), "")) {
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
    
    public static void main(String[] args) {
        
        try {
            boolean b = AlipaySignature.rsaCheckContent("app_id=2016112803443590&auth_app_id=2017053107391618&buyer_id=2088202477752996&buyer_logon_id=men***@163.com&buyer_pay_amount=0.04&charset=GBK&fund_bill_list=[{\"amount\":\"0.04\",\"fundChannel\":\"ALIPAYACCOUNT\"}]&gmt_create=2017-08-15 15:14:40&gmt_payment=2017-08-15 15:14:41&invoice_amount=0.04&notify_id=db0f6f2fad963e0c9c5780d2f7141b1nn2&notify_time=2017-08-15 15:14:41&notify_type=trade_status_sync&out_trade_no=59929e85c71c884ff01e8fa1&passback_params=b3JkZXJObz01OTkyOWU4NWM3MWM4ODRmZjAxZThmYTE=&point_amount=0.00&receipt_amount=0.04&seller_email=775907359@qq.com&seller_id=2088521354078238&subject=2017���＾����5&total_amount=0.04&trade_no=2017081521001004990219233031&trade_status=TRADE_SUCCESS&version=1.0,s",
                "Uk+6SmV5GHjf5fz7/t0SddkuSpmGqCBLY+O4e7vQJGGyxZvzKS1F1CY8XlCmC6991zR/mpsr6TN3DViliLHC15Jvy9yaofmXD9hTsrLxrzYqPpCwY/LUhnkKZ66XztIDtO/TTVQm7ks1PLMb2UvlY69EPhTUFo7J0qKVZTmEM4=", 
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgub5iPOR+EJfZ85xAQZvE8cRrDTNRnZNzoNK6jSpZnSql4Dl+Sb/wPQMSsOP/IvIm1qqpK1NQinLmrNKqJBT6a2rfLNqzZLLQIcMf2l/zIJWE7tR9OWEcDApe5f7UXZJx0GaK/wKJheBlo/+5bM7P+aGylE+/f9dL5FVyFeN/eX9f1yr/J6rvCNRq1vCWcZ/Sq6moGioqYckx2swHHeuarwh3QcUzKTt62zaFDlAyKJUV5co5BSMCbeJzCrFyAMr9kpuzkvlUL4cJ4l+UUiHYIkTgtZLGyOGwS9oUL7PU09sKl2dBS+a3nx+KJ05Yejl7Fn5q/wKfqBLU9YKn1comwIDAQAB", "UTF-8");
            System.out.println(b);
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
