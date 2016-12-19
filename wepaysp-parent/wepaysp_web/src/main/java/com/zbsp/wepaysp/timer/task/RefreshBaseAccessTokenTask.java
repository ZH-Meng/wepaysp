package com.zbsp.wepaysp.timer.task;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.util.WeixinUtil;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.common.util.TimeUtil;

/**
 * 刷新基本支持的AccessToken作业
 * 
 * @author 孟郑宏
 */
@Component
public class RefreshBaseAccessTokenTask
    extends TimerBasicTask {
    private static String LOG_PREFIX = "[定时任务] - [刷新基本支持的AccessToken] - ";
    
    private boolean REFRESHF_RUN = false;
    
    @Override
    public void doJob() {
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[开始]"));
        
        // 遍历所有服务商
        for (Entry<String, Map<String, Object>> entry : SysConfig.partnerConfigMap.entrySet()) {
        	String partner1Oid = entry.getKey();
        	// 服务商配置
        	Map<String, Object> partnerMap= entry.getValue();
			String accessToken = MapUtils.getString(partnerMap, SysEnvKey.WX_BASE_ACCESS_TOKEN);
			Long expireTime = MapUtils.getLong(partnerMap, SysEnvKey.WX_BASE_EXPIRE_TIME);
			String appid = MapUtils.getString(partnerMap, SysEnvKey.WX_APP_ID);
            
        	// 检查和设置开关
        	if (StringUtils.isBlank(accessToken)) {// 首次启动
        		REFRESHF_RUN = true;
        	} else if (expireTime == null || TimeUtil.plusSeconds(120).getTime() >= expireTime.longValue()) {// access_token过期 提前两分钟刷新
        		REFRESHF_RUN = true;
        	} else {
        		REFRESHF_RUN = false;
        		logger.info("服务商（"+ appid +"）access_token有效，不刷新");
        	}
        	if (REFRESHF_RUN) {
        		// 刷新
        		new WeixinUtil().refreshBaseAccessToken(partner1Oid);
        		
        		REFRESHF_RUN = false;
        	}
        }
        
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[结束]"));
    }

}
