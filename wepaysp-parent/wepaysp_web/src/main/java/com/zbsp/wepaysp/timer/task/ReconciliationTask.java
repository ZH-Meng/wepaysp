package com.zbsp.wepaysp.timer.task;

import java.util.Date;
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
import com.zbsp.wepaysp.api.service.reconciliation.ReconciliationDetailsService;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.po.partner.Partner;

/**
 * 对账
 * 
 * @author zhaozh
 */
@Component
public class ReconciliationTask extends TimerBasicTask {
    
    private static String LOG_PREFIX = "[定时任务] - [每日对账] - ";

    @Autowired
    private ReconciliationDetailsService reconciliationDetailsService;
    
    @Value("${reconciliationDate}")
    private String initDate ;

    @Override
    public void doJob() {
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[开始]"));
        
        Date reconciliationDate ; 
        if (StringUtils.isBlank(initDate)) {
        	reconciliationDate = new DateTime().minusDays(1).toDate();
        }else{
        	reconciliationDate = DateUtil.getDate(initDate, "yyyyMMdd");
        }
        logger.info(StringHelper.combinedString(LOG_PREFIX, reconciliationDate.toString()," 对账"));
        reconciliationDetailsService.doTransReconciliation(reconciliationDate);
      
       
        
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[结束]"));
    }
    
	
    public static void main(String[] args) {

        ApplicationContext context = new FileSystemXmlApplicationContext("config/applicationContext.xml");

        ReconciliationTask processor = (ReconciliationTask) context
            .getBean("reconciliationTask");
        processor.doJob();
    }
}
