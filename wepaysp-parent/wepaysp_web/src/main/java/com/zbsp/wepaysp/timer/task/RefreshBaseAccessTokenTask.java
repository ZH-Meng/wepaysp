package com.zbsp.wepaysp.timer.task;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.tencent.WXPay;
import com.tencent.protocol.base_access_token_protocol.GetBaseAccessTokenReqData;
import com.tencent.protocol.base_access_token_protocol.GetBaseAccessTokenResData;
import com.zbsp.wepaysp.api.util.WeixinUtil;
import com.zbsp.wepaysp.common.constant.EnumDefine;
import com.zbsp.wepaysp.common.constant.EnumDefine.AlarmLogPrefix;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.StringHelper;

@Component
public class RefreshBaseAccessTokenTask
    extends TimerBasicTask {
    private static String LOG_PREFIX = "[定时任务] - [刷新基本支持的AccessToken] - ";
    private static boolean REFRESHF_RUN = false;
    public static String accessToken;
    public static long expireTime;
    private static int refreshErrorCount = 3;
    
    @Override
    public void doJob() {
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[开始]"));
        // 检查和设置开关
        if (StringUtils.isBlank(accessToken)) {// 首次启动
            REFRESHF_RUN = true;
        } else if (new Date().getTime() >= (expireTime - 60 * 60 * 2)) {// access_token过期 提前两分钟刷新
            REFRESHF_RUN = true;
        } else {
            REFRESHF_RUN = false;
            logger.info("当前access_token有效，不刷新");
        }
        
        if (REFRESHF_RUN) {
            logger.info("开始调用刷新access_token接口");
            // FIXME 静态开发数据
            String appid= EnumDefine.DevParam.APPID.getValue();
            String secret= EnumDefine.DevParam.APPSECRET.getValue();
            String certLocalPath = EnumDefine.DevParam.CERT_LOCAL_PATH.getValue();
            String certPassword = EnumDefine.DevParam.CERT_PASSWORD.getValue();
            
            boolean result = false;
            for (int i = 1; i <= refreshErrorCount; i++) {
                if (getAccessToken(appid, secret, certLocalPath, certPassword)) {
                    logger.info("获取/刷新Access_token成功");
                    result = true;
                    break;
                } else {
                    logger.error("获取/刷新Access_token失败第" + i + "次");
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (!result) {
                logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxJSAPIErr.getValue(),
                    "获取/刷新Access_token失败"));
            }
        }
        logger.info(StringHelper.combinedString(LOG_PREFIX, "[结束]"));
        
        //FIXME WeixinUtil.sendTemplateMsg(null, null, null, null, accessToken);
    }
    
    private boolean getAccessToken(String appid, String secret, String certLocalPath, String certPassword) {
        try {
            String jsonResult = WXPay.requestGetBaseAccessTokenService(new GetBaseAccessTokenReqData(appid, secret), certLocalPath, certPassword);
            // 转化JSON结果
            GetBaseAccessTokenResData accessTokenResult = JSONUtil.parseObject(jsonResult, GetBaseAccessTokenResData.class);
            // 校验获取access_token
            if (checkAccessTokenResult(accessTokenResult)) {
                accessToken = accessTokenResult.getAccess_token();
                logger.info("access_token：" + accessToken + "，expires_in：" + accessTokenResult.getExpires_in());
                // 设置过期时间
                expireTime = new Date().getTime() + accessTokenResult.getExpires_in() * 1000;
                return true;
            } else {
                logger.error("获取/刷新Access_token失败，错误码：" + accessTokenResult.getErrcode() + "，错误描述：" + accessTokenResult.getErrmsg());
                return false;
            }
        } catch (Exception e) {
            logger.error(StringHelper.combinedString(AlarmLogPrefix.invokeWxJSAPIErr.getValue(),
                "获取/刷新Access_token失败", "，异常信息：" + e.getMessage()));
            logger.error(e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 校验http get 获取access_token的结果
     * 
     * @param accessTokenResultVO
     * @return
     */
    private boolean checkAccessTokenResult(GetBaseAccessTokenResData accessTokenResultVO) {
        boolean result = false;
        if (accessTokenResultVO == null) {
            logger.warn("accessTokenResultVO为空");
        } else {
            logger.debug(accessTokenResultVO.toString());
        }
        if (StringUtils.isNotBlank(accessTokenResultVO.getAccess_token())) {
            result = true;
        } else if (StringUtils.isNotBlank(accessTokenResultVO.getErrcode())) {
            result = true;
        } else {
            logger.warn("get or refresh access_token result invalid");
        }
        return result;
    }

}
