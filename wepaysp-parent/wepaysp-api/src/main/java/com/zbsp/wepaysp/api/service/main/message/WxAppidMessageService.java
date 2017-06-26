package com.zbsp.wepaysp.api.service.main.message;

import java.util.Map;

/**
 * 微信公众号消息服务
 */
public interface WxAppidMessageService {

	/**
	 * 发送支付结果通知
	 * @param payDetailsMap 支付信息
	 * */
	public int sendPayResultNotice(Map<String,Object> payDetailsMap) throws Exception;
}
