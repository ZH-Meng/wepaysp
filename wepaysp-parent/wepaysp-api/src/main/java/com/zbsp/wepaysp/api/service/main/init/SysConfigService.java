package com.zbsp.wepaysp.api.service.main.init;

import java.util.Map;

/**
 * 系统配置初始化服务
 * 
 * @author 孟郑宏
 */
public interface SysConfigService {

    /**
     *  根据顶级服务商Oid查找服务商配置信息
     *  
     * @param partnerOid 顶级服务商Oid
     * @return Map 包含key ：SysEnvKey.WX_APPID、SysEnvKey.WX_SECRET、SysEnvKey.WX_CERT_LOCAL_PATH、SysEnvKey.CERT_PASSWORD、SysEnvKey.WX_KEY
     */
    public Map<String, String> getPartnerCofigInfoByPartnerOid(String partnerOid);

    public Map<String, String> getPartnerCofigInfoByOutTradeNo(String outTradeNo);

}
