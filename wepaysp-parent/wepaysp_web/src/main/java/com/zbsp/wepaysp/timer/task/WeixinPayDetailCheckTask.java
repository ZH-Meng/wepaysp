package com.zbsp.wepaysp.timer.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;

/**
 * 微信支付交易明细状态检查作业，状态处理中的需要处理
 * 
 * @author mengzh
 */
@Component
public class WeixinPayDetailCheckTask {
    // 日志对象
    protected Logger logger = LogManager.getLogger(getClass());
    
    @Autowired
    private WeixinPayDetailsService weixinPayDetailsService;
    
	public void stateCheckJob() {
		logger.info("开始处理微信支付交易明细状态处理中的记录");
		System.out.println(weixinPayDetailsService);
	}
}
