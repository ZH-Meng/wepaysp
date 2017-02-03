package com.zbsp.wepaysp.api.service.alipay.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.alipay.AlipayAppAuthDetailsService;
import com.zbsp.wepaysp.api.service.manage.SysLogService;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.alipay.AlipayApp;
import com.zbsp.wepaysp.po.alipay.AlipayAppAuthDetails;
import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.po.partner.Dealer;
import com.zbsp.wepaysp.vo.alipay.AlipayAppAuthDetailsVO;


public class AlipayAppAuthDetailsServiceImpl
    extends BaseService
    implements AlipayAppAuthDetailsService {
    
    private SysLogService sysLogService;

    @Override
    public AlipayAppAuthDetailsVO doTransCreateAppAuthDetail(AlipayAppAuthDetailsVO appAuthDetailsVO) {
        // 检查参数
        Validator.checkArgument(appAuthDetailsVO == null, "appAuthDetailsVO为空");
        Validator.checkArgument(StringUtils.isBlank(appAuthDetailsVO.getAppId()), "appAuthDetailsVO.appid为空");
        Validator.checkArgument(StringUtils.isBlank(appAuthDetailsVO.getAuthUserId()), "appAuthDetailsVO.authUserId为空");
        
        // 根据appId查找应用
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from AlipayApp a where a.appId=:APPID";
        jpqlMap.put("APPID", appAuthDetailsVO.getAppId());

        AlipayApp app = commonDAO.findObject(jpql, jpqlMap, false);
        if (app == null) {
            throw new NotExistsException("AlipayApp不存在（appId=" + appAuthDetailsVO.getAppId() + "）");
        }
        
        // 根据authUserId查找商户
        jpqlMap.clear();
        jpql = "from Dealer d where d.alipayUserId=:ALIPAYUSERID";
        jpqlMap.put("ALIPAYUSERID", appAuthDetailsVO.getAuthUserId());

        Dealer dealer = commonDAO.findObject(jpql, jpqlMap, false);
        if (dealer == null) {
            throw new NotExistsException("dealer不存在（alipayUserId=" + appAuthDetailsVO.getAuthUserId() + "）");
        }
        
        // 记录授权记录，关联商户、蚂蚁平台应用
        AlipayAppAuthDetails appAuthDetails = new AlipayAppAuthDetails();
        appAuthDetails.setIwoid(Generator.generateIwoid());
        appAuthDetails.setDealer(dealer);
        appAuthDetails.setAlipayApp(app);
        appAuthDetails.setAppId(appAuthDetailsVO.getAppId());
        appAuthDetails.setAppAuthToken(appAuthDetailsVO.getAppAuthToken());
        appAuthDetails.setAppRefreshToken(appAuthDetailsVO.getAppRefreshToken());
        appAuthDetails.setExpiresIn(appAuthDetailsVO.getExpiresIn());
        appAuthDetails.setReExpiresIn(appAuthDetailsVO.getReExpiresIn());
        //FIXME
        appAuthDetails.setAuthStart(appAuthDetailsVO.getAuthStart());
        appAuthDetails.setAuthEnd(appAuthDetailsVO.getAuthEnd());
        appAuthDetails.setAuthMethods(appAuthDetailsVO.getAuthMethods());
        appAuthDetails.setStatus(AlipayAppAuthDetails.AppAuthStatus.VALID.toString());
        appAuthDetails.setAuthAppId(appAuthDetailsVO.getAuthAppId());
        commonDAO.save(appAuthDetails, false);
        
        // 记录日志
        Date processTime = new Date();
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, 
            "新增商户授权应用明细[appId=" + appAuthDetails.getAppId() + "，商户PID=" + appAuthDetails.getDealer().getCompany() + "，商户PID=" + appAuthDetails.getDealer().getAlipayUserId() 
            + "，授权令牌：" + appAuthDetails.getAppAuthToken() + ", 刷新令牌=" + appAuthDetails.getAppRefreshToken() + "]", 
            processTime, processTime, null, appAuthDetails.toString(), SysLog.State.success.getValue(), appAuthDetails.getIwoid(), null, SysLog.ActionType.create.getValue());
        
        BeanCopierUtil.copyProperties(appAuthDetails, appAuthDetailsVO);
        appAuthDetailsVO.setDealerName(dealer.getCompany());
        appAuthDetailsVO.setAppName(app.getAppName());
        
        return appAuthDetailsVO;
    }
    
    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

}
