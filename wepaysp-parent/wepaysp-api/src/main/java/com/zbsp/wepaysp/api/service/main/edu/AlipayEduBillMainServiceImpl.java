package com.zbsp.wepaysp.api.service.main.edu;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.codec.Base64;
import com.alipay.api.response.AlipayEcoEduKtBillingModifyResponse;
import com.alipay.api.response.AlipayEcoEduKtBillingQueryResponse;
import com.zbsp.alipay.trade.config.Constants;
import com.zbsp.wepaysp.api.service.edu.AlipayEduBillService;
import com.zbsp.wepaysp.api.service.edu.AlipayEduNotifyService;
import com.zbsp.wepaysp.api.util.AliPayEduUtil;
import com.zbsp.wepaysp.common.constant.AliPayEnums.AsynNotifyHandleResult;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
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
            //String signType = MapUtils.getString(SysConfig.alipayAppMap.get(SysConfig.appId4Edu), SysEnvKey.ALIPAY_APP_SIGN_TYPE);
            //String alipayPublicKey = MapUtils.getString(SysConfig.alipayAppMap.get(SysConfig.appId4Edu), SysEnvKey.ALIPAY_PUBLIC_KEY);
            
            String alipayPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
            if (!AlipaySignature.rsaCheckV1(paramMap, alipayPublicKey, MapUtils.getString(paramMap, "charset"), "RSA")) {
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
            
            // 异步通知信息都是K12的，不必校验app_id
            // 检查重要参数app_id、totalAmount、seller_id
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
        } catch (Exception e) {
            logger.error(logPrefix + "记录通知内容并处理账单 - 异常 : {}", e.getMessage(), e);
        } finally {
            logger.info(logPrefix + "记录通知内容并处理账单 - 结束");
        }
        
        // 返回
        resultMap.put("result", result);
        return resultMap;
    }

	@Override
    public Map<String, Object> closeEduBill(String totalBillOid, String billOid) {
        Validator.checkArgument(StringUtils.isBlank(totalBillOid) && StringUtils.isBlank(billOid), "totalBillOid与billOid不能都为空");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", "success");
        resultMap.put("msg", "操作成功");
        
        List<AlipayEduBill> toCloseBillList = new ArrayList<>();
        if (StringUtils.isNotBlank(totalBillOid)) {
            toCloseBillList = alipayEduBillService.doJoinTransQueryAlipayEduBillByStatus(totalBillOid, OrderStatus.NOT_PAY);// FIXME 待发送的账单是否需要关闭
        } else {
            toCloseBillList.add(alipayEduBillService.doJoinTransQueryBillByOid(billOid));
        }

        int failCount = 0;
        List<AlipayEduBill> closeSuccessBillList = new ArrayList<>();
        AlipayEcoEduKtBillingModifyResponse closeResponse = null;
        for (AlipayEduBill bill : toCloseBillList) {
            try {
                closeResponse = AliPayEduUtil.billModify(bill, 2);
                logger.info("outTradeNo:{}, 账单关闭结果:{}", bill.getOutTradeNo(), JSONUtil.toJSONString(closeResponse, true));
                if (closeResponse == null || !Constants.SUCCESS.equals(closeResponse.getCode())) {// 交易或者结束
                    logger.warn("outTradeNo:{},账单关闭失败！", bill.getOutTradeNo());
                    failCount++;
                } else {
                    bill.setOrderStatus(OrderStatus.ISV_CLOSED.name());
                    closeSuccessBillList.add(bill);
                }
            } catch (AlipayApiException e) {
                logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeAliPayAPIErr.getValue(), e.getMessage()));
            }
        }

        if (failCount > 0) {
            if (closeSuccessBillList.size() == 0) {
                resultMap.put("code", "fail");
                resultMap.put("msg", "操作失败");
            } else {
                resultMap.put("code", "notAllSuccess");
                resultMap.put("msg", failCount + "个账单关闭失败");
            }
        }

        // 批量更新（状态关闭）
        if (closeSuccessBillList.size() > 0)
            alipayEduBillService.doTransUpdateBillList(closeSuccessBillList);
        return resultMap;
    }
    
    public void setAlipayEduBillService(AlipayEduBillService alipayEduBillService) {
        this.alipayEduBillService = alipayEduBillService;
    }

    public void setAlipayEduNotifyService(AlipayEduNotifyService alipayEduNotifyService) {
        this.alipayEduNotifyService = alipayEduNotifyService;
    }
    
    public static void main(String[] args) {
        String sign = "Uk+6SmV5GHjf5fz7/t0SddkuSpmGqCBLY+O4e7vQJGGyxZvzKS1F1CY8XlCmC6991zR/mpsr6TN3DViliLHC15Jvy9yaofmXD9hTsrLxrzYqPpCwY/LUhnkKZ66XztIDtO/TTVQm7ks1PLMb2UvlY69EPhTUFo7J0qKVZTmEM4=";
        String alipayPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB";
        try {
        	System.out.println(Base64.decodeBase64(sign.getBytes()).length);
            boolean b = AlipaySignature.rsaCheckContent("app_id=2016112803443590&auth_app_id=2017053107391618&buyer_id=2088202477752996&buyer_logon_id=men***@163.com&buyer_pay_amount=0.04&charset=GBK&fund_bill_list=[{\"amount\":\"0.04\",\"fundChannel\":\"ALIPAYACCOUNT\"}]&gmt_create=2017-08-15 15:14:40&gmt_payment=2017-08-15 15:14:41&invoice_amount=0.04&notify_id=db0f6f2fad963e0c9c5780d2f7141b1nn2&notify_time=2017-08-15 15:14:41&notify_type=trade_status_sync&out_trade_no=59929e85c71c884ff01e8fa1&passback_params=b3JkZXJObz01OTkyOWU4NWM3MWM4ODRmZjAxZThmYTE=&point_amount=0.00&receipt_amount=0.04&seller_email=775907359@qq.com&seller_id=2088521354078238&subject=2017���＾����5&total_amount=0.04&trade_no=2017081521001004990219233031&trade_status=TRADE_SUCCESS&version=1.0",
                //"eUk+6SmV5GHjf5fz7/t0SddkuSpmGqCBLY+O4e7vQJGGyxZvzKS1F1CY8XlCmC6991zR/mpsr6TN3DViliLHC15Jvy9yaofmXD9hTsrLxrzYqPpCwY/LUhnkKZ66XztIDtO/TTVQm7ks1PLMb2UvlY69EPhTUFo7J0qKVZTmEM4=",
                //"VXXGSaee11FbOl8V89nUCuCLz2UCxEPJyo3khbM7uL3aKk1xG9H8481LY/DgFiWjEonGOvjQ1uz5MGIIrQVG6Xkopx66PoCAj1cmZQ63mwcXl9bNb/YYRgwuaGv+wI0rgv4idw8OOsrA9XffaEQXMRtSJmC9B6Trm4r41JSlIOLIM7QVqtIYO2FZOU9dBrcX8mQXXh1O/rZwa0Y+Mm8xCBQfuZRm6GgwVZDMR650fyeU5q+aUbl0Zol55C+WVd3NR08iFCofd/nrktUo5EGgfK6tbTyRthEMcPdAKmeC/MFuY5Nh0djo1rynOvjaA/wSH9BseoeyPnUx3Qel5m7XAA==",
            		sign,
                //"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCuk5ohQonX2RaUKkpgECJ6/FHBs4j7mhPR1jIc0PPKy+ypNTn54bqKUs36f01iAS6elgJi15kFnmimG/I10ezoe7YIkTUdOeh1+B/IlZVX637wwr8PyUYRn9Jcbq6KGTBoK7EakDtMcTrqkSZ1NN1tLaxUDF4bMnll9273/njeXQIDAQAB",
            		alipayPublicKey,
            		"utf-8");
            System.out.println(b);
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        AlipayEduBill bill = new AlipayEduBill();
        bill.setIsvPartnerId("2088422728707658");
        bill.setSchoolPid("2088521354078238");
        bill.setK12OrderNo("59929e85c71c884ff01e8fa1");
        try {
        	AliPayEduUtil.eduClient = new DefaultAlipayClient(
        			"https://openapi.alipay.com/gateway.do", 
        			"2017081308171308", 
        			"MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCQWklPkDnUyzExlt1TrQn5txha8UtJzFdmJnnje4q3mJ01Y10HqUqfFXjw3BJs2r1PKSwAVgq2JelWRVqVL6BifNjvyrvLKsN505aRJTfDVtQYKCOQ9HHVmO9CqJ6tpwHbsPElT5pWbTSmqbOaWTTSO4nas1oLL1mYoq76zduX5kE3RqgGydlf8GAjwgqBvg85OIo4hozge3XIZmrlpsD0qLqp42Kc73l/y1b3+2YCtHG2oYN2YdenLRjdePOvploDnmExrnuFSktTCjf2hS/RXybKRK/PTXTmQ6XUc6J5Y9z/fjeCAmr5qVOa6soanGb0Pr9iFJb/Fsic6Z5aDehrAgMBAAECggEAMmDQJQxYooMnb/8Ozx0c1iGoRlTfyame9u+GqnV7PAfvFarP1NSoTkzYIHctlv/gAbiEurlMk7U3cfxSWK5+cuDPdSBz2bva7LGDYrRyeaHb83JOt1AYSktNdR4UAta+N0XRYwwc+Pyz21Zub/eiD5znx4PAcRbdVf+wwacGdERI+3JVn7M0hlPIrVjMrhYKzGukawahq7U60wExUvoctad1qQHprnfbP30swm+21YmpHkyHLEqHpwyEwzr7FdNkD6MVMOW9SGF+ToW5pnCnjahjtX4g0pzXZgoogCQUtCWR8CkTP2orRbavEypZ0MMMkM/n4mkazMsK+L0yscRIAQKBgQDPi8ghZyq6igOtONWCoZNWDN7qoOuVARWMZfqJ1E6zUApPLNfKrSlF7vycEwlENDXtAlRAhYH/UpGBkcoIrMipAVDvdQ0XFB95cMU/zEDo0B6XA//IoVVjMAWdKPd+lDqaTqwlpTUyJjhBpgWssajA2q8/hki6RJ/PKS/lTpoV6wKBgQCyDa85gVjfVVbRK459O48AEBKEkMQa23h2ltkSW83k0ImFAK0vtKjQAKP4l8zeUqcBnZWjufdZs2LHYeR7nLdBw6IB2sXyHkPoACGZNEUEItS87WDb45X/l5oCdzsUIh0asOBbuqAnAikdTsdruWU3vbrrX1J5iiLlteUct2JXgQKBgB+xoOY48vV5jgYt360LL4y8em4qFM+0Uq4WBKR3n20SlABzQKDawKrFTB4pagjYUEPZTzRrLubmnQHEWa3pdNsaMFng7hbQI95Psk+DyYXEAmfdXHou6PM0qibN0r2ptSlLSk6VZAI8g+rCh6tPlg2dV5XD0+deA2mHSKEj17PxAoGALUEfJR2EChd1nmpUp+3IVbrACiOFRFI91t5WQ71DooGCQmS+n95p/Zv8EQX8ExGs0tNLojZ08L6QP57Y5LYbOcLQXWpOX1Bj8AaAg6DuXbPAoFgxAxJgIHtWsIO/Z0mYXh1QmN/hLqj07DcGppGTcUxuwr4Sh0OdxQarOdY+T4ECgYB/zjvvU407pJ17Z0MmugP1QJ5zL61K3bE5n32DgUJbp68NJSlBGMO/XIQpcFtqwQM0YwMNLLHLw5p2huGE+ukONrcQAdYn7AFU/ULYGtU46e/gCSA0EF8qJ1QrkgJO8tCQ/smgxmbYVeS8bPV4JfJgPey8VHZ+9ZDQH6/msz6mLQ==", "json", "utf-8", 
        			"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgub5iPOR+EJfZ85xAQZvE8cRrDTNRnZNzoNK6jSpZnSql4Dl+Sb/wPQMSsOP/IvIm1qqpK1NQinLmrNKqJBT6a2rfLNqzZLLQIcMf2l/zIJWE7tR9OWEcDApe5f7UXZJx0GaK/wKJheBlo/+5bM7P+aGylE+/f9dL5FVyFeN/eX9f1yr/J6rvCNRq1vCWcZ/Sq6moGioqYckx2swHHeuarwh3QcUzKTt62zaFDlAyKJUV5co5BSMCbeJzCrFyAMr9kpuzkvlUL4cJ4l+UUiHYIkTgtZLGyOGwS9oUL7PU09sKl2dBS+a3nx+KJ05Yejl7Fn5q/wKfqBLU9YKn1comwIDAQAB", 
        			"RSA2");
			AlipayEcoEduKtBillingQueryResponse response = AliPayEduUtil.billQuery(bill);
			System.out.println(JSONUtil.toJSONString(response, true));
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
    }
    
}
