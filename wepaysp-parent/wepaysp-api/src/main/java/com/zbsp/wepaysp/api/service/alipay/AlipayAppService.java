package com.zbsp.wepaysp.api.service.alipay;

import java.util.List;

import com.zbsp.wepaysp.po.alipay.AlipayApp;

/**
 * 支付宝应用管理服务
 */
public interface AlipayAppService {
    
    /**
     * 根据服务商Oid查找支付宝应用
     * 
     * @param partnerOid 服务商Oid
     * @return 
     */
    public List<AlipayApp> doJoinTransQueryAppByPartnerOid(String partnerOid);
}
