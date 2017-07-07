package com.zbsp.wepaysp.timer.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zbsp.alipay.trade.config.Constants;
import com.zbsp.alipay.trade.model.builder.AlipayOpenAuthTokenAppRequestBuilder;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.alipay.AlipayAppAuthDetailsService;
import com.zbsp.wepaysp.api.util.AliPayUtil;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.po.alipay.AlipayAppAuthDetails;
import com.zbsp.wepaysp.vo.alipay.AlipayAppAuthDetailsVO;

/**
 * 定时刷新支付宝应用授权过期的app_auth_token
 * 
 * @author 孟郑宏
 */
@Component
public class RefreshAppAuthTokenTask
    extends TimerBasicTask {
    private static String LOG_PREFIX = "[定时任务] - [刷新支付宝应用授权过期的app_auth_token] - ";
    
    @Autowired
    private AlipayAppAuthDetailsService alipayAppAuthDetailsService;
    
    @Override
    public void doJob() {
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[开始]"));
        
        // 查出已经过期（服务连续停止运行时）和剩余有效时间<=1天的应用授权明细        
        List<AlipayAppAuthDetails> authDetailsList = alipayAppAuthDetailsService.doJoinTransQueryExpiredAppAuthDetails(SysConfig.appId4Face2FacePay);
        
        if (authDetailsList != null && !authDetailsList.isEmpty()) {
            for (AlipayAppAuthDetails authDetails : authDetailsList) {
                try {
                    logger.info(LOG_PREFIX + "商户{}授权应用AppAuthToken{}即将过期过期，准备刷新", authDetails.getDealer().getCompany(), authDetails.getAppAuthToken());
                    AlipayAppAuthDetailsVO appAuthDetailsVO = AliPayUtil
                        .getOrRefreshAppAuthToken(new AlipayOpenAuthTokenAppRequestBuilder().setGrantType(Constants.GRANT_TYPE_REFRESH_TOKEN).setRefreshToken(authDetails.getAppRefreshToken()));
                    if (appAuthDetailsVO == null) {
                        logger.error(LOG_PREFIX + AlarmLogPrefix.invokeAliPayAPIErr.getValue() + "刷新商户{}授权应用AppAuthToken{}  - 失败", authDetails.getDealer().getCompany(), authDetails.getAppAuthToken());
                    } else {
                        authDetails.setAuthAppId(appAuthDetailsVO.getAuthAppId());
                        authDetails.setAppAuthToken(appAuthDetailsVO.getAppAuthToken());
                        authDetails.setAppRefreshToken(appAuthDetailsVO.getAppRefreshToken());
                        authDetails.setExpiresIn(appAuthDetailsVO.getExpiresIn());
                        authDetails.setReExpiresIn(appAuthDetailsVO.getReExpiresIn());
                        authDetails.setAuthEnd(appAuthDetailsVO.getAuthEnd());
                        authDetails.setAuthStart(appAuthDetailsVO.getAuthStart());
                        authDetails.setAuthMethods(appAuthDetailsVO.getAuthMethods());
                        alipayAppAuthDetailsService.doTransUpdateAppAuthDetail(authDetails);
                    }
                } catch (Exception e) {
                    logger.error(LOG_PREFIX + "刷新商户授权应用异常：" + e.getMessage(), e);
                }
            }
        }
        
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[结束]"));
    }

}
