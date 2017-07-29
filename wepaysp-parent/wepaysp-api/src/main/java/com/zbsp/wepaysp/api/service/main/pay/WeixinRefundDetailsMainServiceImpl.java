package com.zbsp.wepaysp.api.service.main.pay;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.tencent.WXPay;
import com.zbsp.wepaysp.api.listener.DefaultReverseBusinessResultListener;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.main.init.SysConfigService;
import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.api.service.pay.WeixinRefundDetailsService;
import com.zbsp.wepaysp.api.util.WeixinPackConverter;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.WxEnums.WxReverseResult;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.po.pay.WeixinRefundDetails;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;
import com.zbsp.wepaysp.vo.pay.WeixinRefundDetailsVO;

public class WeixinRefundDetailsMainServiceImpl 
	extends BaseService 
	implements WeixinRefundDetailsMainService {
	
    private SysConfigService sysConfigService;
	private WeixinPayDetailsService weixinPayDetailsService;
	private WeixinRefundDetailsService weixinRefundDetailsService;

	@Override
	public Map<String, Object> cashierDeskRefund(WeixinPayDetailsVO weixinPayDetailsVO, String creator, String operatorUserOid, String logFunctionOid) {
		// TODO 检查参数
        Validator.checkArgument(weixinPayDetailsVO == null, "支付订单对象不能为空");
        
		String outTradeNo = weixinPayDetailsVO.getOutTradeNo();// 系统支付订单ID
		Validator.checkArgument(StringUtils.isBlank(outTradeNo), "系统支付订单ID不能为空");
		
		// 查找支付明细
		Map<String, Object> jpQLMap = new HashMap<String, Object>();
		String jqQL = "from WeixinPayDetails w where w.outTradeNo=:OUTTRADENO";
		jpQLMap.put("OUTTRADENO", outTradeNo);
		WeixinPayDetails payDetails = commonDAO.findObject(jqQL, jpQLMap, false);
		
		if (payDetails == null) {
            throw new NotExistsException("系统支付订单不存在！");
        }
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 创建退款明细
		WeixinRefundDetailsVO refundDetailsVO = weixinRefundDetailsService.doTransCreateRefundDetails(payDetails, creator, operatorUserOid, logFunctionOid);
		
		// 退款
		//WXPay.doReverseBusiness(reverseReqData, resultListener, certLocalPath, certPassword, keyPartner);
		
		return resultMap;
	}

    @Override
    public Map<String, Object> reverseOrder(String outTradeNo) {
        Validator.checkArgument(StringUtils.isBlank(outTradeNo), "outTradeNo不能为空");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String resCode = WxReverseResult.FAIL.getCode();
        String resDesc = WxReverseResult.FAIL.getDesc();
        
        // 查找支付明细
        WeixinPayDetails payDetails = weixinPayDetailsService.doJoinTransQueryWeixinPayDetailsByNum(outTradeNo, null, null);
        if (payDetails == null) {
            throw new NotExistsException("系统支付订单不存在！");
        }
        
        String certLocalPath = null;
        String certPassword = null;
        String keyPartner = null;
        Map<String, Object> partnerMap = sysConfigService.getPartnerCofigInfoByPartnerOid(payDetails.getPartner1Oid());
        if (partnerMap != null && !partnerMap.isEmpty()) {
            certLocalPath = MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH);
            certPassword = MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_PASSWORD);
            keyPartner = MapUtils.getString(partnerMap, SysEnvKey.WX_KEY);
        } else {
            throw new RuntimeException("系统数据异常，服务商配置信息不存在");
        }
        
        WeixinRefundDetailsVO refundDetailsVO = null;
        try {
            // 创建退款明细
            refundDetailsVO = weixinRefundDetailsService.doTransCreateReverseDetails(payDetails);
            logger.info("微信退款（撤销）明细保存成功！");
        } catch (Exception e) {
            logger.warn("创建微信退款（撤销）明细失败");
            throw e;
        }
        
        try {
            DefaultReverseBusinessResultListener resultListener = new DefaultReverseBusinessResultListener(this);
            WXPay.doReverseBusiness(WeixinPackConverter.weixinRefundDetailsVO2ReverseReq(refundDetailsVO), resultListener, certLocalPath, certPassword, keyPartner);
            
            // 查询撤销结果
            Map<String, Object> jpqlMap = new HashMap<String, Object>();
            String jpql = "from WeixinRefundDetails w where w.outTradeNo=:OUTTRADENO";
            jpqlMap.put("OUTTRADENO", outTradeNo);

            WeixinRefundDetails refundDetails = commonDAO.findObject(jpql, jpqlMap, false);
            String wxResultCode = refundDetails.getResultCode();
            resCode = refundDetails.getErrCode();
            resDesc = refundDetails.getErrCodeDes();

            if (StringUtils.equalsIgnoreCase(WxReverseResult.SUCCESS.getCode(), wxResultCode)) {// 业务结果：成功
                resCode = WxReverseResult.SUCCESS.getCode();
                resDesc = WxReverseResult.SUCCESS.getDesc();
                logger.info("系统订单ID=" + outTradeNo + "撤销成功，微信支付订单ID=" + payDetails.getTransactionId());
            } else {
                logger.warn("系统订单ID" + outTradeNo + "撤销失败，错误码：" + resCode + "，错误描述：" + resDesc);
            }
        } catch (Exception e) {
            logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxPayAPIErr.getValue(), 
                "系统支付订单(ID=" + outTradeNo + "）撤销错误", "，异常信息：" + e.getMessage()));
        }
        resultMap.put("resultCode", resCode);
        resultMap.put("resultDesc", resDesc);
        return resultMap;
    }

    @Override
    public void reverseResult(WeixinRefundDetailsVO refundDetailsVO) {
        weixinRefundDetailsService.doTransUpdateReverseResult(refundDetailsVO);
    }
	
    public void setSysConfigService(SysConfigService sysConfigService) {
        this.sysConfigService = sysConfigService;
    }

    public void setWeixinPayDetailsService(WeixinPayDetailsService weixinPayDetailsService) {
		this.weixinPayDetailsService = weixinPayDetailsService;
	}

	public void setWeixinRefundDetailsService(WeixinRefundDetailsService weixinRefundDetailsService) {
		this.weixinRefundDetailsService = weixinRefundDetailsService;
	}

}
