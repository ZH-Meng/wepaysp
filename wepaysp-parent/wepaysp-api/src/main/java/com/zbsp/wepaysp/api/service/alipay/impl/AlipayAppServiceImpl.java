package com.zbsp.wepaysp.api.service.alipay.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.alipay.AlipayAppService;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.alipay.AlipayApp;


public class AlipayAppServiceImpl
    extends BaseService
    implements AlipayAppService {

    @SuppressWarnings("unchecked")
    @Override
    public List<AlipayApp> doJoinTransQueryAppByPartnerOid(String partnerOid) {
        Validator.checkArgument(StringUtils.isBlank(partnerOid), "partnerOid为空");
        
        Map<String, Object> jpqlMap = new HashMap<String,Object>();
        String jpql = "from AlipayApp a where a.partner.iwoid = :PARTNEROID";
        jpqlMap.put("PARTNEROID", partnerOid);
        
        return (List<AlipayApp>) commonDAO.findObjectList(jpql, jpqlMap, false);
    }

}
