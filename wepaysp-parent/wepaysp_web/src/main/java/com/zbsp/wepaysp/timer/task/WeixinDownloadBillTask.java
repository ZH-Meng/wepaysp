package com.zbsp.wepaysp.timer.task;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.tencent.WXPay;
import com.tencent.protocol.downloadbill_protocol.DownloadBillReqData;
import com.tencent.service.DownloadBillService;
import com.zbsp.wepaysp.api.listener.DefaultDownloadBillBusinessResultListener;
import com.zbsp.wepaysp.api.service.main.init.SysConfigService;
import com.zbsp.wepaysp.api.service.partner.PartnerService;
import com.zbsp.wepaysp.api.service.pay.WeixinBillService;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.po.partner.Partner;

/**
 * 微信官方交易明细下载
 * 
 * @author zhaozh
 */
@Component
public class WeixinDownloadBillTask extends TimerBasicTask {
    
    private static String LOG_PREFIX = "[定时任务] - [微信官方交易明细下载] - ";

    @Autowired
    private WeixinBillService weixinBillService;
    
    @Autowired 
    private PartnerService partnerService;
    
    @Autowired 
    private SysConfigService sysConfigService;

    @Value("${initBillDate}")
    private String billDate;

    @Override
    public void doJob() {
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[开始]"));
        
        String initDate = billDate; 
        //获取顶级渠道商，分渠道商下载对账单
        List<Partner> topPartnerList = partnerService.doJoinTransQueryTopPartner(null, null);
        
        
        for(int i=96; i>1 ;i--){
        	initDate = new DateTime().minusDays(i).toString("yyyyMMdd");
        
        
//        if (StringUtils.isBlank(initDate)) {
//            initDate = new DateTime().minusDays(1).toString("yyyyMMdd");
//        }
        

        
        for(Partner partner:topPartnerList){
            // 从内存中获取服务商配置信息
            Map<String, Object> partnerMap = sysConfigService.getPartnerCofigInfoByPartnerOid(partner.getIwoid());
            
    		if (partnerMap != null && !partnerMap.isEmpty()) {
    			    			
    	        DownloadBillReqData scanPayReqData = new DownloadBillReqData(
    	                null,
    	                initDate,
    	                DownloadBillService.BILL_TYPE_ALL,
    	                // 加密key
    	                MapUtils.getString(partnerMap, SysEnvKey.WX_KEY),
    	                //appId
    	                MapUtils.getString(partnerMap, SysEnvKey.WX_APP_ID),
    	                //mchId
    	                MapUtils.getString(partnerMap, SysEnvKey.WX_MCH_ID),
    	                //subMchId
    	                "",
    	                "",
    	                "GZIP"
    	        );
    	        //第二步：创建一个用来处理被扫支付业务逻辑各种结果分支的监听器resultListener
    	        DefaultDownloadBillBusinessResultListener resultListener = new DefaultDownloadBillBusinessResultListener(weixinBillService);
    	        //第三步：执行业务逻辑
    	        try {
    	        	logger.info(StringHelper.combinedString(LOG_PREFIX, "发送下载对账单请求"));    	        	
					WXPay.doDownloadBillBusiness(scanPayReqData, resultListener,MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH),MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_PASSWORD),MapUtils.getString(partnerMap, SysEnvKey.WX_KEY));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
 
    		} else {
    			throw new RuntimeException("系统数据异常，服务商配置信息不存在");
    		}
        }
        
        }
       
        
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[结束]"));
    }
    
	public void setSysConfigService(SysConfigService sysConfigService) {
		this.sysConfigService = sysConfigService;
	}
	
    public static void main(String[] args) {

        ApplicationContext context = new FileSystemXmlApplicationContext("config/applicationContext.xml");

        WeixinDownloadBillTask processor = (WeixinDownloadBillTask) context
            .getBean("weixinDownloadBillTask");
        processor.doJob();
    }
}
