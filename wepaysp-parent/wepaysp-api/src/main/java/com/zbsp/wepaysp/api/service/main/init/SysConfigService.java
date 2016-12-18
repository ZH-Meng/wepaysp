package com.zbsp.wepaysp.api.service.main.init;

import java.util.Map;

/**
 * 系统配置初始化服务
 * 
 * @author 孟郑宏
 */
public interface SysConfigService {

    /**
     *  根据顶级服务商Oid查找服务商配置信息<br>
     *  获取服务商配置先从 SysConfig.partnerConfigMap 或 SysConfig.partnerConfigMap2<br>
     *  如果不存在再从数据库获取
     *  
     * @param partnerOid 顶级服务商Oid
     * @return Map 包含key ：SysEnvKey.WX_APPID、SysEnvKey.WX_SECRET、SysEnvKey.WX_CERT_LOCAL_PATH、SysEnvKey.CERT_PASSWORD、SysEnvKey.WX_KEY
     */
    public Map<String, Object> getPartnerCofigInfoByPartnerOid(String partnerOid);

    /**
     *  根据公众号查找服务商配置信息<br>
     *  获取服务商配置先从 SysConfig.partnerConfigMap 或 SysConfig.partnerConfigMap2<br>
     *  如果不存在再从数据库获取
     *  
     * @param appid 公众号
     @return Map 包含key ：SysEnvKey.WX_APPID、SysEnvKey.WX_SECRET、SysEnvKey.WX_CERT_LOCAL_PATH、SysEnvKey.CERT_PASSWORD、SysEnvKey.WX_KEY
     */
	public Map<String, Object> getPartnerCofigInfoByAppid(String appid);

}
