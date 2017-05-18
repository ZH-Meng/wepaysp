package com.zbsp.wepaysp.api.service.weixin.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.tencent.protocol.appid.sns_userinfo_protocol.GetUserinfoResData;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.manage.SysLogService;
import com.zbsp.wepaysp.api.service.weixin.PayNoticeBindWeixinService;
import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.exception.BindNoUniqueException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.po.partner.Dealer;
import com.zbsp.wepaysp.po.partner.DealerEmployee;
import com.zbsp.wepaysp.po.partner.Store;
import com.zbsp.wepaysp.po.weixin.PayNoticeBindWeixin;
import com.zbsp.wepaysp.po.weixin.PayNoticeBindWeixin.State;
import com.zbsp.wepaysp.vo.weixin.PayNoticeBindWeixinVO;

public class PayNoticeBindWeixinServiceImpl
    extends BaseService
    implements PayNoticeBindWeixinService {

    private SysLogService sysLogService;

    @Override
    public List<PayNoticeBindWeixinVO> doJoinTransQueryPayNoticeBindWeixinList(Map<String, Object> paramMap) {
        Validator.checkArgument(paramMap == null, "paramMap不能为空！");
        List<PayNoticeBindWeixinVO> resultList = new ArrayList<PayNoticeBindWeixinVO>();
        String type = MapUtils.getString(paramMap, "type");
        Validator.checkArgument(StringUtils.isBlank(type), "type不能为空！");
        String storeOid = MapUtils.getString(paramMap, "storeOid");
        String dealerEmployeeOid = MapUtils.getString(paramMap, "dealerEmployeeOid");
        String state = MapUtils.getString(paramMap, "state");
        if (type.equals(PayNoticeBindWeixin.Type.store.getValue())) {
            Validator.checkArgument(StringUtils.isBlank(storeOid), "storeOid不能为空！");
        } else if (type.equals(PayNoticeBindWeixin.Type.dealerEmployee.getValue())) {
            Validator.checkArgument(StringUtils.isBlank(dealerEmployeeOid), "dealerEmployeeOid不能为空！");
        } else {
            throw new IllegalArgumentException("参数type只能是1或者2");
        }

        StringBuffer jpql = new StringBuffer("from PayNoticeBindWeixin p where 1=1 ");
        Map<String, Object> jpqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(type)) {
            jpql.append(" and p.type = :TYPE");
            jpqlMap.put("TYPE", type);
        }
        if (StringUtils.isNotBlank(storeOid)) {
            jpql.append(" and p.store.iwoid = :STOREOID");
            jpqlMap.put("STOREOID", storeOid);
        }
        if (StringUtils.isNotBlank(dealerEmployeeOid)) {
            jpql.append(" and p.payDealerEmployee.iwoid = :PAYDEALEREMPLOYEEOID");
            jpqlMap.put("PAYDEALEREMPLOYEEOID", dealerEmployeeOid);
        }
        if (StringUtils.isNotBlank(state)) {
            jpql.append(" and p.state = :STATE");
            jpqlMap.put("STATE", state);
        }

        jpql.append(" order by p.modifyTime desc");
        
        @SuppressWarnings("unchecked")
        List<PayNoticeBindWeixin> payNoticeBindWeixinList = (List<PayNoticeBindWeixin>) commonDAO.findObjectList(jpql.toString(), jpqlMap, false);
        if (payNoticeBindWeixinList != null && !payNoticeBindWeixinList.isEmpty()) {
            for (PayNoticeBindWeixin wx : payNoticeBindWeixinList) {
                PayNoticeBindWeixinVO vo = new PayNoticeBindWeixinVO();
                BeanCopierUtil.copyProperties(wx, vo);
                
                if (wx.getPayDealerEmployee() != null) {
                    vo.setPayDealerEmployeeName(wx.getPayDealerEmployee().getEmployeeName());
                    vo.setPayDealerEmployeeId(wx.getPayDealerEmployee().getDealerEmployeeId());
                    vo.setPayDealerEmployeeOid(wx.getPayDealerEmployee().getIwoid());
                }
                if (wx.getStore() != null) {
                    vo.setStoreOid(wx.getStore().getIwoid());
                    vo.setStoreId(wx.getStore().getStoreId());
                    vo.setStoreName(wx.getStore().getStoreName());
                }
                
                if (wx.getBindDealerEmployee() != null) {
                    vo.setBindDealerEmployeeName(wx.getBindDealerEmployee().getEmployeeName());
                    vo.setBindDealerEmployeeId(wx.getBindDealerEmployee().getDealerEmployeeId());
                    vo.setBindDealerEmployeeOid(wx.getBindDealerEmployee().getIwoid());
                }
                resultList.add(vo);
            }
        }
        return resultList;
    }

    @Override
    public void doTransUpdatePayNoticeBindWeixinList(List<PayNoticeBindWeixinVO> bindVOList, String modifier, String operatorUserOid, String logFunctionOid) {
        Validator.checkArgument(bindVOList == null || bindVOList.isEmpty(), "bindVOList不能为空！");
        int count = 0;
        logger.info("正在批量更新绑定记录条数：" + bindVOList.size());
        for (PayNoticeBindWeixinVO vo : bindVOList) {
            PayNoticeBindWeixin wx = commonDAO.findObject(PayNoticeBindWeixin.class, vo.getIwoid());
            if (wx == null) {
                logger.error("微信支付通知绑定信息不存在，oid=" + vo.getIwoid());
                continue;
            }
            String oldWxStr = wx.toString();
            Date processBeginTime = new Date();
            if (StringUtils.isNotBlank(vo.getBindDealerEmployeeOid())) {
            	DealerEmployee bindDealerEmployee = new DealerEmployee();
            	bindDealerEmployee.setIwoid(vo.getBindDealerEmployeeOid());
            	wx.setBindDealerEmployee(bindDealerEmployee);
            } else {
            	wx.setBindDealerEmployee(null);
            }
            wx.setState(vo.getState());
            wx.setModifier(modifier);
            commonDAO.update(wx);
            sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "微信支付通知绑定信息[绑定收银员姓名oid：" + vo.getBindDealerEmployeeOid() + "，状态：" + vo.getState() + "]", processBeginTime, processBeginTime, oldWxStr, wx.toString(), SysLog.State.success.getValue(), wx.getIwoid(), logFunctionOid, SysLog.ActionType.modify.getValue());
            count++;
        }
        
        logger.info("批量更新成功绑定记录条数：" + count);
    }

    @Override
    public void doTransDeletePayNoticeBindWeixin(String payNoticeBindWeixinOid) {
        Validator.checkArgument(StringUtils.isBlank(payNoticeBindWeixinOid), "payNoticeBindWeixinOid不能为空！");
        
        StringBuffer jpql = new StringBuffer("delete from PayNoticeBindWeixin p where p.iwoid=:IWOID");
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        jpqlMap.put("IWOID", payNoticeBindWeixinOid);
        int delteCount =commonDAO.executeBatch(jpql.toString(), jpqlMap, false);
        if (delteCount > 0) {
        	logger.info("删除微信支付通知绑定信息，oid=" + payNoticeBindWeixinOid);
        }
    }

    @Override
    public PayNoticeBindWeixinVO doTransAddPayNoticeBindWeixin(String bindType, String toRelateOid, GetUserinfoResData userinfoResData) {
        Validator.checkArgument(StringUtils.isBlank(bindType), "bindType不能为空！");
        Validator.checkArgument(userinfoResData == null, "userinfoResData不能为空！");
        Validator.checkArgument(StringUtils.isBlank(userinfoResData.getOpenid()), "userinfoResData.openid不能为空！");
        Validator.checkArgument(StringUtils.isBlank(toRelateOid), "支付通知绑定toRelateOid不能为空！");
        
        // 校验是否绑定过
        StringBuffer jpql = new StringBuffer("from PayNoticeBindWeixin p where p.openid=:OPENID ");
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        jpqlMap.put("OPENID", userinfoResData.getOpenid());
        
        List<?> bindList = null;
        
        PayNoticeBindWeixin po = new PayNoticeBindWeixin();
        String logTemp = "";
        if (PayNoticeBindWeixin.Type.dealer.getValue().equals(bindType)) {// 微信绑定商户
            jpql.append(" and p.type=:TYPE");
            jpqlMap.put("TYPE", bindType);
            bindList = commonDAO.findObjectList(jpql.toString(), jpqlMap, false);
            if (bindList != null && !bindList.isEmpty()) {// 只可能存在一个
                PayNoticeBindWeixin bindwx = (PayNoticeBindWeixin) bindList.get(0);
                if (toRelateOid.equals(bindwx.getBindDealer().getIwoid())) {
                    throw new AlreadyExistsException("已绑定过此商户，忽略绑定");
                } else {
                    // 不能重复绑定
                    throw new BindNoUniqueException("绑定失败，已绑定过其他商户");
                }
            }
            
            // 绑定商户
            Dealer d = commonDAO.findObject(Dealer.class, toRelateOid);
            if (d == null) {
                throw new NotExistsException("微信绑定商户，收银员不存在，dealerEmployeeOid=" + toRelateOid);
            }
            po.setBindDealer(d);;
            logTemp = "，关联商户：" + toRelateOid;
        } else if (PayNoticeBindWeixin.Type.store.getValue().equals(bindType) // 微信绑定绑定门店级支付码 
            || PayNoticeBindWeixin.Type.dealerEmployee.getValue().equals(bindType)) {// 微信绑定绑定收银员级支付码
            jpql.append(" and p.type in (:TYPE1,:TYPE2)");
            jpqlMap.put("TYPE1", PayNoticeBindWeixin.Type.store.getValue());
            jpqlMap.put("TYPE2", PayNoticeBindWeixin.Type.dealerEmployee.getValue());
            
            bindList = commonDAO.findObjectList(jpql.toString(), jpqlMap, false);
            if (bindList != null && !bindList.isEmpty()) {// 只可能存在一个
                PayNoticeBindWeixin bindwx = (PayNoticeBindWeixin) bindList.get(0);
                if (PayNoticeBindWeixin.Type.store.getValue().equals(bindwx.getType()) && toRelateOid.equals(bindwx.getStore().getIwoid())) {
                    throw new AlreadyExistsException("已绑定过此门店级支付通知，忽略绑定");
                } else if (PayNoticeBindWeixin.Type.dealerEmployee.getValue().equals(bindwx.getType()) && toRelateOid.equals(bindwx.getPayDealerEmployee().getIwoid())) {
                    throw new AlreadyExistsException("已绑定过此收银员级支付通知，忽略绑定");
                } else {
                    // 支付码（门店级、收银员级）只能绑定一个微信，不能重复绑定。但可以绑定商户
                    throw new BindNoUniqueException("绑定失败，已绑定过其他支付码");
                }
            }
            
            if (PayNoticeBindWeixin.Type.dealerEmployee.getValue().equals(bindType)) {
                // 绑定收银员
                DealerEmployee de = commonDAO.findObject(DealerEmployee.class, toRelateOid);
                if (de == null) {
                    throw new NotExistsException("微信支付通知绑定，收银员不存在，dealerEmployeeOid=" + toRelateOid);
                }
                po.setPayDealerEmployee(de);
                logTemp = "，关联收银员：" + toRelateOid;
            } else if (PayNoticeBindWeixin.Type.store.getValue().equals(bindType)) {
                // 绑定门店
                Store store = commonDAO.findObject(Store.class, toRelateOid);
                if (store == null) {
                    throw new NotExistsException("微信支付通知绑定，门店不存在，storeOid=" + toRelateOid);
                }
                po.setStore(store);
                logTemp = "，关联门店：" + toRelateOid;
            }
        } else {
            throw new IllegalArgumentException("参数type只能是1、2、3");
        }
        
        po.setIwoid(Generator.generateIwoid());
        po.setState(PayNoticeBindWeixin.State.open.getValue());// 默认开启
        po.setOpenid(userinfoResData.getOpenid());
        po.setNickname(userinfoResData.getNickname());
        po.setSex(StringUtils.isBlank(userinfoResData.getSex()) ? 0 : Integer.parseInt(userinfoResData.getSex()));
        po.setType(bindType);
        po.setCreator(userinfoResData.getOpenid());
        // TODO 考虑增加字段 来维护昵称与微信尽量保持一致
        commonDAO.save(po, false);
        
        // 记录日志
        Date logTime = new Date();
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, "创建支付通知绑定信息[绑定类别=" + po.getType() +"，openid=" + po.getOpenid() + ", 昵称=" + po.getNickname() + ", 性别=" + po.getSex() + ", 状态=" + po.getState() + logTemp+ "]", 
            logTime, logTime, null, po.toString(), SysLog.State.success.getValue(), po.getIwoid(), null, SysLog.ActionType.create.getValue());
        
        PayNoticeBindWeixinVO vo = new PayNoticeBindWeixinVO();
        BeanCopierUtil.copyProperties(po, vo);
        
        if (PayNoticeBindWeixin.Type.dealer.getValue().equals(bindType)) {
            vo.setDealerId(po.getBindDealer().getDealerId());
            vo.setDealerName(po.getBindDealer().getCompany());
            vo.setDealerOid(po.getBindDealer().getIwoid());
        } else if (PayNoticeBindWeixin.Type.dealerEmployee.getValue().equals(bindType)) {
            Store sto = po.getPayDealerEmployee().getStore();
            vo.setPayDealerEmployeeName(po.getPayDealerEmployee().getEmployeeName());
            vo.setPayDealerEmployeeId(po.getPayDealerEmployee().getDealerEmployeeId());
            vo.setPayDealerEmployeeOid(po.getPayDealerEmployee().getIwoid());
            vo.setStoreOid(sto.getIwoid());
            vo.setStoreId(sto.getStoreId());
            vo.setStoreName(sto.getStoreName());
        } else {
            vo.setStoreOid(po.getStore().getIwoid());
            vo.setStoreId(po.getStore().getStoreId());
            vo.setStoreName(po.getStore().getStoreName());
        }
        
        return vo;
    }
    
    @Override
    public SysUser doJoinTransQueryBindUser(String openid) {
        Validator.checkArgument(StringUtils.isBlank(openid), "openid不能为空！");
        String jpql = "from PayNoticeBindWeixin p where p.openid=:OPENID";
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        jpqlMap.put("OPENID", openid);
        PayNoticeBindWeixin bindInfo = commonDAO.findObject(jpql, jpqlMap, false);
        if (bindInfo == null) {
            return null;
        } else {
            jpqlMap.clear();
            if (bindInfo.getBindDealer() != null) {
                jpql = "from SysUser u left Join fetch u.dealer where u.dealer=:DEALER";
                jpqlMap.put("DEALER", bindInfo.getBindDealer() );
                return commonDAO.findObject(jpql, jpqlMap, false);
            } else if (bindInfo.getBindDealerEmployee() != null) {
                jpql = "from SysUser u left Join fetch u.dealerEmployee where u.dealerEmployee=:DEALEREMPLOYEE";
                jpqlMap.put("DEALEREMPLOYEE", bindInfo.getBindDealerEmployee());
                return commonDAO.findObject(jpql, jpqlMap, false);
            } else {
                return null;
            }
        }
    }
    
    @Override
    public Map<String, Object> doJoinTransQueryBindInfo(String openid) {
        Validator.checkArgument(StringUtils.isBlank(openid), "openid不能为空！");
        String jpql = "from PayNoticeBindWeixin p where p.openid=:OPENID";
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        jpqlMap.put("OPENID", openid);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        PayNoticeBindWeixin bindInfo = commonDAO.findObject(jpql, jpqlMap, false);
        if (bindInfo != null) {
            resultMap.put("bindwx", bindInfo);
            jpqlMap.clear();
            SysUser user = null;
            if (bindInfo.getBindDealer() != null) {
                jpql = "from SysUser u left Join fetch u.dealer where u.dealer=:DEALER";
                jpqlMap.put("DEALER", bindInfo.getBindDealer() );
                user = commonDAO.findObject(jpql, jpqlMap, false);
            } else if (bindInfo.getBindDealerEmployee() != null) {
                jpql = "from SysUser u left Join fetch u.dealerEmployee where u.dealerEmployee=:DEALEREMPLOYEE";
                jpqlMap.put("DEALEREMPLOYEE", bindInfo.getBindDealerEmployee());
                user = commonDAO.findObject(jpql, jpqlMap, false);
            }
            resultMap.put("user", user);
        }
        return resultMap;
    }
    
    @Override
    public void doTransUpdateBindWeixinState(String bindOid, State state) {
        Validator.checkArgument(StringUtils.isBlank(bindOid), "bindOid不能为空！");
        Validator.checkArgument(state == null, "state不能为空！");
        PayNoticeBindWeixin bindwx = commonDAO.findObject(PayNoticeBindWeixin.class, bindOid);
        if (bindwx == null) {
            throw new NotExistsException("PayNoticeBindWeixin不存在，oid=" + bindOid);
        }
        bindwx.setState(state.getValue());
        commonDAO.update(bindwx);
    }
    
	public void setSysLogService(SysLogService sysLogService) {
		this.sysLogService = sysLogService;
	}


}
