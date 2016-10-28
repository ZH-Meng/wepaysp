package com.zbsp.wepaysp.service.partner.impl;

import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.service.BaseService;
import com.zbsp.wepaysp.service.manage.SysLogService;
import com.zbsp.wepaysp.service.partner.PartnerEmployeeService;
import com.zbsp.wepaysp.vo.partner.PartnerEmployeeVO;


public class PartnerEmployeeServiceImpl
    extends BaseService
    implements PartnerEmployeeService {

    private SysLogService sysLogService;
    
    @Override
    public PartnerEmployeeVO doJoinTransQueryPartnerEmployeeByOid(String partnerEmployeeOid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PartnerEmployeeVO> doJoinTransQueryPartnerEmployeeList(Map<String, Object> paramMap, int startIndex, int maxResult) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int doJoinTransQueryPartnerEmployeeCount(Map<String, Object> paramMap) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public PartnerEmployeeVO doTransAddPartnerEmployee(PartnerEmployeeVO partnerEmployeeVO, String creator, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException, IllegalAccessException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PartnerEmployeeVO doTransUpdatePartnerEmployee(PartnerEmployeeVO partnerEmployeeVO, String modifier, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException {
        // TODO Auto-generated method stub
        return null;
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

}
