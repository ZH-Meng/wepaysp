package com.zbsp.wepaysp.api.service.main.edu;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.edu.AlipayEduBillService;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.AliPayEnums.AsynNotifyHandleResult;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.edu.AlipayEduBill;
import com.zbsp.wepaysp.vo.alipay.AlipayWapPayNotifyVO;

public class AlipayEduBillMainServiceImpl
    implements AlipayEduBillMainService {

    protected final Logger logger = LogManager.getLogger(getClass());
    
    private AlipayEduBillService alipayEduBillService;
    
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

        flag = false;
        logger.info(logPrefix + "检查通知内容 - 开始");
        
        // 检查通知内容，包括通知中的app_id, out_trade_no, total_amount、seller_id是否与请求中的一致，不一致表明本次通知是异常通知，忽略
        try {
            String outTradeNo = notifyVO.getOut_trade_no();// k12平台的outTradeNo
            String appId = notifyVO.getApp_id();
            String totalAmountStr = notifyVO.getTotal_amount();
            Validator.checkArgument(StringUtils.isBlank(outTradeNo), "outTradeNo为空");
            Validator.checkArgument(StringUtils.isBlank(appId), "appId为空");
            Validator.checkArgument(StringUtils.isBlank(totalAmountStr), "totalAmount为空");
            Validator.checkArgument(!NumberUtils.isCreatable(totalAmountStr) || !Pattern.matches(SysEnvKey.REGEX_￥_POSITIVE_FLOAT_2BIT, totalAmountStr), "totalAmount(" + totalAmountStr + ")格式不正确");
            Validator.checkArgument(StringUtils.isBlank(notifyVO.getTrade_status()), "trade_status为空");
            
            // 查找通知，验证是否重复通知
            
            // 记录通知
            
            // 处理通知
            
            //AlipayEduBill bill = alipayEduBillService.doJoinTransQueryAliPayEduBillByK12OrderNo(outTradeNo);
        } catch (Exception e) {
            logger.error(logPrefix + "检查通知内容 - 异常 : {}", e.getMessage(), e);
        } finally {
            logger.info(logPrefix + "检查通知内容 - 结束");
        }
        
        // 返回
        resultMap.put("result", result);
        return resultMap;
    }
    
    public void setAlipayEduBillService(AlipayEduBillService alipayEduBillService) {
        this.alipayEduBillService = alipayEduBillService;
    }
    
}
