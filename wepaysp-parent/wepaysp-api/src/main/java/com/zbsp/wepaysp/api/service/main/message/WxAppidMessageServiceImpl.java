package com.zbsp.wepaysp.api.service.main.message;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tencent.protocol.appid.send_template_msg_protocol.SendTemplateMsgResData;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.main.init.SysConfigService;
import com.zbsp.wepaysp.api.service.weixin.PayNoticeBindWeixinService;
import com.zbsp.wepaysp.api.util.WeixinUtil;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.WxEnums.SendTempMsgErr;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.weixin.PayNoticeBindWeixin;
import com.zbsp.wepaysp.vo.weixin.PayNoticeBindWeixinVO;

public class WxAppidMessageServiceImpl implements WxAppidMessageService {
    private final Logger logger = LogManager.getLogger(getClass());
    
	private SysConfigService sysConfigService;
    private PayNoticeBindWeixinService payNoticeBindWeixinService;
    
	@Override
	public int sendPayResultNotice(Map<String,Object> payDetailsMap) throws Exception {
		String partner1Oid = MapUtils.getString(payDetailsMap, "partner1Oid");
		String dealerOid = MapUtils.getString(payDetailsMap, "dealerOid");
		String storeOid = MapUtils.getString(payDetailsMap, "storeOid");
		String dealerEmployeeOid = MapUtils.getString(payDetailsMap, "dealerEmployeeOid");
		String outTradeNo = MapUtils.getString(payDetailsMap, "outTradeNo");
		Object transTime = MapUtils.getObject(payDetailsMap, "transBeginTime");
    	Integer totalFee = MapUtils.getInteger(payDetailsMap, "totalFee");
    	if (totalFee== null) {
    		totalFee = MapUtils.getInteger(payDetailsMap, "totalAmount");
    	} 
		// 检查发送支付信息完整性
		Validator.checkArgument(StringUtils.isBlank(partner1Oid), "partner1Oid不能为空");
    	Validator.checkArgument(StringUtils.isBlank(outTradeNo), "系统订单号不能为空");
    	Validator.checkArgument(transTime == null, "交易时间不能为空");
    	Validator.checkArgument(totalFee == null, "交易金额不能为空");
    	
    	// 查找需要收到支付通知的人员touser
    	Map<String, Object> queryMap = new HashMap<String, Object>(); 
    	if (StringUtils.isNotBlank(storeOid)) {
    		if (StringUtils.isNotBlank(dealerEmployeeOid)) {// 收银员级别扫码
	    		queryMap.put("type", PayNoticeBindWeixin.Type.dealerEmployee.getValue());
	    		queryMap.put("dealerEmployeeOid", dealerEmployeeOid);
    		} else {// 门店级别扫码
    			queryMap.put("type", PayNoticeBindWeixin.Type.store.getValue());
    			queryMap.put("storeOid", storeOid);
    		}
    	} else {
    		throw new RuntimeException("支付订单数据异常：storeOid不能为空");
    	}
        int count = 0;
        
    	queryMap.put("state", PayNoticeBindWeixin.State.open.getValue());
    	List<PayNoticeBindWeixinVO> toUserList = payNoticeBindWeixinService.doJoinTransQueryPayNoticeBindWeixinList(queryMap);
    	// 查找商户绑定
    	List<PayNoticeBindWeixinVO> dealerBindList = payNoticeBindWeixinService.doJoinTransQueryDealerBind(dealerOid, PayNoticeBindWeixin.State.open.getValue());
        if (dealerBindList != null && !dealerBindList.isEmpty())
            toUserList.addAll(dealerBindList);

        Set<String> sentSet = new HashSet<>(); // 已发送微信用户集
    	if (toUserList != null && !toUserList.isEmpty()) {
    		logger.info("订单（ID=" + outTradeNo + "）需要向（" + toUserList.size() + "人）发送支付成功通知");
    		// 准备发送消息的参数（服务商配置信息）
    		String certLocalPath = null;
    		String certPassword = null;
    		String accessToken = null;
    		Map<String, Object> partnerMap = sysConfigService.getPartnerCofigInfoByPartnerOid(partner1Oid);
    		if (partnerMap != null && !partnerMap.isEmpty()) {
    			certLocalPath = MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH);
    			certPassword = MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_PASSWORD);
        		// 获取Base_acction_token
    			accessToken = WeixinUtil.getBaseAccessToken(partner1Oid);
    		} else {
    			throw new RuntimeException("系统数据异常，服务商配置信息不存在");
    		}
    		
    		for(PayNoticeBindWeixinVO toUser : toUserList) {
                if (toUser != null && StringUtils.isNotBlank(toUser.getOpenid()) && !sentSet.contains(toUser.getOpenid())) {// 不重复发
    				String messageURL = MessageFormat.format(SysConfig.wxPayMessageLinkURL, toUser.getOpenid(), dealerOid, storeOid, dealerEmployeeOid);
					SendTemplateMsgResData sendResult = WeixinUtil.sendPaySuccessNotice(payDetailsMap, toUser.getOpenid(), certLocalPath, certPassword, accessToken, messageURL);
					if (SendTempMsgErr.SUCCESS.getValue().equals(sendResult.getErrcode())) {// 发送成功
						logger.info("订单（ID=" + outTradeNo + "）向（" + toUser.getNickname() + "(openid=" + toUser.getOpenid() + ")）发送支付成功通知成功");
						count ++;
						sentSet.add(toUser.getOpenid());
					} else {
						logger.info("订单（ID=" + outTradeNo + "）向（" + toUser.getNickname() + "(openid=" + toUser.getOpenid() + ")）发送支付成功通知失败");
					}
    			}
    		}
    		logger.info("订单（ID=" + outTradeNo + "）成功向（" + count + "人）发送支付成功通知");
    	} else {
    	    String temp = "";
    	    if (StringUtils.isNotBlank(dealerEmployeeOid)) {
    	        temp = "收银员-" + MapUtils.getString(payDetailsMap, "dealerEmployeeName") + "（ID=" + MapUtils.getString(payDetailsMap, "dealerEmployeeId") + "）"; 
    	    } else {
    	        temp = "门店-" + MapUtils.getString(payDetailsMap, "storeName") + "（ID=" + MapUtils.getString(payDetailsMap, "storeId") + "）";
    	    }
    	    logger.info(temp + "没有启用的支付通知绑定人"+"，订单（ID=" + outTradeNo + "）不需要发送支付成功通知");
    	}
		return count;
	}

	public void setSysConfigService(SysConfigService sysConfigService) {
		this.sysConfigService = sysConfigService;
	}

	public void setPayNoticeBindWeixinService(PayNoticeBindWeixinService payNoticeBindWeixinService) {
		this.payNoticeBindWeixinService = payNoticeBindWeixinService;
	}
	
}
