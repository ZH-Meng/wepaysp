package com.zbsp.wepaysp.api.service.main.pay;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.tencent.WXPay;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.api.service.pay.WeixinRefundDetailsService;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;
import com.zbsp.wepaysp.vo.pay.WeixinRefundDetailsVO;

public class WeixinRefundDetailsMainServiceImpl 
	extends BaseService 
	implements WeixinRefundDetailsMainService {
	
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

	public void setWeixinPayDetailsService(WeixinPayDetailsService weixinPayDetailsService) {
		this.weixinPayDetailsService = weixinPayDetailsService;
	}

	public void setWeixinRefundDetailsService(WeixinRefundDetailsService weixinRefundDetailsService) {
		this.weixinRefundDetailsService = weixinRefundDetailsService;
	}

}
