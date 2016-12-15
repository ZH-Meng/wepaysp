package com.zbsp.wepaysp.api.service.weixin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.manage.SysLogService;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.po.partner.DealerEmployee;
import com.zbsp.wepaysp.po.weixin.PayNoticeBindWeixin;
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
            jpql.append(" and p.storeOid = :STOREOID");
            jpqlMap.put("STOREOID", storeOid);
        }
        if (StringUtils.isNotBlank(dealerEmployeeOid)) {
            jpql.append(" and p.payDealerEmployeeOid = :PAYDEALEREMPLOYEEOID");
            jpqlMap.put("PAYDEALEREMPLOYEEOID", dealerEmployeeOid);
        }
        if (StringUtils.isNotBlank(state)) {
            jpql.append(" and p.state = :STATE");
            jpqlMap.put("STATE", state);
        }

        jpql.append(" order by p.createTime desc");
        
        @SuppressWarnings("unchecked")
        List<PayNoticeBindWeixin> payNoticeBindWeixinList = (List<PayNoticeBindWeixin>) commonDAO.findObjectList(jpql.toString(), jpqlMap, false);
        if (payNoticeBindWeixinList != null && !payNoticeBindWeixinList.isEmpty()) {
            for (PayNoticeBindWeixin wx : payNoticeBindWeixinList) {
                PayNoticeBindWeixinVO vo = new PayNoticeBindWeixinVO();
                BeanCopierUtil.copyProperties(wx, vo);
                if (wx.getBindDealerEmployee() != null) {
                    vo.setBindDealerEmployeeName(wx.getBindDealerEmployee().getEmployeeName());
                    vo.setBindDealerEmployeeId(wx.getBindDealerEmployee().getDealerEmployeeId());
                    vo.setBindDealerEmployeeName(wx.getBindDealerEmployee().getEmployeeName());
                    vo.setBindDealerEmployeeOid(wx.getBindDealerEmployee().getIwoid());
                }
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
            DealerEmployee bindDealerEmployee = new DealerEmployee();
            bindDealerEmployee.setIwoid(vo.getBindDealerEmployeeOid());
            wx.setBindDealerEmployee(bindDealerEmployee);
            wx.setState(vo.getState());
            wx.setModifier(modifier);
            commonDAO.update(wx);
            sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "微信支付通知绑定信息[绑定收银员姓名oid：" + vo.getIwoid() + "，状态：" + vo.getState() + "]", processBeginTime, processBeginTime, oldWxStr, wx.toString(), SysLog.State.success.getValue(), wx.getIwoid(), logFunctionOid, SysLog.ActionType.modify.getValue());
            count++;
        }
        
        logger.info("批量更新成功绑定记录条数：" + count);
    }

    @Override
    public void doTransDeletePayNoticeBindWeixin(String payNoticeBindWeixinOid) {
        Validator.checkArgument(StringUtils.isBlank(payNoticeBindWeixinOid), "type不能为空！");
        
        StringBuffer jpql = new StringBuffer("delete from PayNoticeBindWeixin p where p.iwoid=:IWOID");
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        jpqlMap.put("IWOID", payNoticeBindWeixinOid);
        commonDAO.executeBatch(jpql.toString(), jpqlMap, false);
        logger.info("删除微信支付通知绑定信息，oid=" + payNoticeBindWeixinOid);
    }

}
