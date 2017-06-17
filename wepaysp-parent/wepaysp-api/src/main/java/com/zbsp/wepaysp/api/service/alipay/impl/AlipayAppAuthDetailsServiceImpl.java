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
        Validator.checkArgument(StringUtils.isBlank(appAuthDetailsVO.getDealerOid()), "appAuthDetailsVO.dealerOid为空");
        //Validator.checkArgument(StringUtils.isBlank(appAuthDetailsVO.getAuthUserId()), "appAuthDetailsVO.authUserId为空");
        
        // 根据appId查找应用
        logger.info("查找应用({}) - 开始", appAuthDetailsVO.getAppId());
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from AlipayApp a where a.appId=:APPID";
        jpqlMap.put("APPID", appAuthDetailsVO.getAppId());

        AlipayApp app = commonDAO.findObject(jpql, jpqlMap, false);
        if (app == null) {
            throw new NotExistsException("AlipayApp不存在（appId=" + appAuthDetailsVO.getAppId() + "）");
        }
        logger.info("查找应用({}) - 结束", appAuthDetailsVO.getAppId());
        
        /*// 根据authUserId查找商户
        logger.info("查找商户(支付宝PID={}) - 开始", appAuthDetailsVO.getAuthUserId());
        jpqlMap.clear();
        jpql = "from Dealer d where d.alipayUserId=:ALIPAYUSERID";
        jpqlMap.put("ALIPAYUSERID", appAuthDetailsVO.getAuthUserId());

        Dealer dealer = commonDAO.findObject(jpql, jpqlMap, false);
        if (dealer == null) {
            throw new NotExistsException("dealer不存在（alipayUserId=" + appAuthDetailsVO.getAuthUserId() + "）");
        }
        logger.info("查找商户(支付宝PID={}) - 结束", appAuthDetailsVO.getAuthUserId());*/
        
        // 查找商户
        logger.info("查找商户(oid={}) - 开始", appAuthDetailsVO.getDealerOid());
        Dealer dealer = commonDAO.findObject(Dealer.class, appAuthDetailsVO.getDealerOid());
        if (dealer == null) {
            throw new NotExistsException("dealer不存在（oid=" + appAuthDetailsVO.getDealerOid() + "）");
        }
        logger.info("查找商户(oid={}) - 结束", appAuthDetailsVO.getDealerOid());
        // 更新商户支付宝PID（暂无实用）
        Date logDate = new Date();
        if (StringUtils.isNotBlank(appAuthDetailsVO.getAuthUserId())) {
            if (StringUtils.isNotBlank(dealer.getAlipayUserId()) && !StringUtils. equals(dealer.getAlipayUserId(), appAuthDetailsVO.getAuthUserId())) {
                logger.info("商户(oid={}) - 支付宝授权更换支付宝账户，原有PID：{}, 新的PID：{}", dealer.getAlipayUserId(), appAuthDetailsVO.getAuthUserId());
            }
            String oldDealerStr = dealer.toString();
            dealer.setAlipayUserId(appAuthDetailsVO.getAuthUserId());
            commonDAO.update(dealer);
            // 记录修改日志
            sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, "修改商户信息[alipayUserId=" + dealer.getAlipayUserId() + "]", 
                logDate, logDate, oldDealerStr, dealer.toString(), SysLog.State.success.getValue(), dealer.getIwoid(), null, SysLog.ActionType.modify.getValue());
        } else {
            logger.warn("商户(oid={}) - 授权处理，authUserId为空", appAuthDetailsVO.getDealerOid());
        }
        
        // 将旧的授权状态置为无效
        jpqlMap.clear();
        jpql = "from AlipayAppAuthDetails a where a.dealer=:DEALER and a.alipayApp=:ALIPAYAPP and a.status=:STATUS";
        jpqlMap.put("DEALER", dealer);
        jpqlMap.put("ALIPAYAPP", app);
        jpqlMap.put("STATUS", AlipayAppAuthDetails.AppAuthStatus.VALID.toString());
        AlipayAppAuthDetails oldAuths = commonDAO.findObject(jpql, jpqlMap, false);
        if (oldAuths != null) {
            String oldAuthStr = oldAuths.toString();
            logger.info("商户({})曾授权过应用({})，现将令牌({})状态置为invalid", dealer.getDealerId(), app.getAppId(), oldAuths.getAppAuthToken());
            oldAuths.setStatus(AlipayAppAuthDetails.AppAuthStatus.INVALID.toString());
            commonDAO.update(oldAuths);            
            // 记录修改日志
            sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, "修改商户授权支付宝应用明细[status=" + oldAuths.getStatus() + "]", 
                logDate, logDate, oldAuthStr, oldAuths.toString(), SysLog.State.success.getValue(), oldAuths.getIwoid(), null, SysLog.ActionType.modify.getValue());
        }
        
        // 记录新的授权记录，关联商户、蚂蚁平台应用
        logger.info("保存授权令牌(支付宝PID={}，应用{}，令牌{}) - 开始", appAuthDetailsVO.getAuthUserId(), appAuthDetailsVO.getAppId(), appAuthDetailsVO.getAppAuthToken());
        AlipayAppAuthDetails appAuthDetails = new AlipayAppAuthDetails();
        appAuthDetails.setIwoid(Generator.generateIwoid());
        appAuthDetails.setDealer(dealer);
        appAuthDetails.setAlipayApp(app);
        appAuthDetails.setAppId(appAuthDetailsVO.getAppId());
        appAuthDetails.setAppAuthToken(appAuthDetailsVO.getAppAuthToken());
        appAuthDetails.setAppRefreshToken(appAuthDetailsVO.getAppRefreshToken());
        appAuthDetails.setExpiresIn(appAuthDetailsVO.getExpiresIn());
        appAuthDetails.setReExpiresIn(appAuthDetailsVO.getReExpiresIn());
        appAuthDetails.setAuthStart(appAuthDetailsVO.getAuthStart());
        appAuthDetails.setAuthEnd(appAuthDetailsVO.getAuthEnd());
        appAuthDetails.setAuthMethods(appAuthDetailsVO.getAuthMethods());
        appAuthDetails.setStatus(AlipayAppAuthDetails.AppAuthStatus.VALID.toString());
        appAuthDetails.setAuthAppId(appAuthDetailsVO.getAuthAppId());
        commonDAO.save(appAuthDetails, false);
        logger.info("保存授权令牌(支付宝PID={}，应用{}，令牌{}) - 结束", appAuthDetailsVO.getAuthUserId(), appAuthDetailsVO.getAppId(), appAuthDetailsVO.getAppAuthToken());
        
        // 记录日志
        logger.info("记录日志 - 新增商户授权应用明细 - 开始");
        Date processTime = new Date();
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, 
            "新增商户授权应用明细[appId=" + appAuthDetails.getAppId() + "，商户=" + appAuthDetails.getDealer().getDealerId() + "，商户支付宝PID=" + appAuthDetails.getDealer().getAlipayUserId() 
            + "，授权令牌：" + appAuthDetails.getAppAuthToken() + ", 刷新令牌=" + appAuthDetails.getAppRefreshToken() + "]", 
            processTime, processTime, null, appAuthDetails.toString(), SysLog.State.success.getValue(), appAuthDetails.getIwoid(), null, SysLog.ActionType.create.getValue());
        
        logger.info("记录日志 - 新增商户授权应用明细 - 结束");
        
        BeanCopierUtil.copyProperties(appAuthDetails, appAuthDetailsVO);
        appAuthDetailsVO.setDealerName(dealer.getCompany());
        appAuthDetailsVO.setAppName(app.getAppName());
        
        return appAuthDetailsVO;
    }

    @Override
    public AlipayAppAuthDetailsVO doJoinTranQueryAppAuthDetailByDealer(String dealerOid, String appId) {
        // 检查参数
        Validator.checkArgument(StringUtils.isBlank(dealerOid), "dealerOid为空");
        Validator.checkArgument(StringUtils.isBlank(appId), "appId为空");
        
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from AlipayAppAuthDetails a where a.dealer.iwoid=:DEALEROID and a.alipayApp.appId=:APPID and a.status=:STATUS";
        jpqlMap.put("DEALEROID", dealerOid);
        jpqlMap.put("APPID", appId);
        jpqlMap.put("STATUS", AlipayAppAuthDetails.AppAuthStatus.VALID.toString());
        AlipayAppAuthDetails appAuthDetails = commonDAO.findObject(jpql, jpqlMap, false);
        AlipayAppAuthDetailsVO appAuthDetailsVO = null;
        if (appAuthDetails != null) {
        	appAuthDetailsVO = new AlipayAppAuthDetailsVO();
        	BeanCopierUtil.copyProperties(appAuthDetails, appAuthDetailsVO);
        }
        return appAuthDetailsVO;
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

}
