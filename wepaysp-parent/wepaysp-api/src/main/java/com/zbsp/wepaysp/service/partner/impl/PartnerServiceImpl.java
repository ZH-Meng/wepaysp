package com.zbsp.wepaysp.service.partner.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.security.DigestHelper;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.po.manage.SysUser.userLevel;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.service.BaseService;
import com.zbsp.wepaysp.service.manage.SysLogService;
import com.zbsp.wepaysp.service.partner.PartnerService;
import com.zbsp.wepaysp.vo.partner.PartnerVO;

public class PartnerServiceImpl
    extends BaseService
    implements PartnerService {

    private SysLogService sysLogService;

    @Override
    public List<PartnerVO> doJoinTransQueryPartnerList(Map<String, Object> paramMap, int startIndex, int maxResult) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int doJoinTransQueryPartnerCount(Map<String, Object> paramMap) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public PartnerVO doTransAddPartner(PartnerVO partnerVO, String creator, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException {
        Validator.checkArgument(partnerVO == null, "代理商对象不能为空");
        Validator.checkArgument(StringUtils.isBlank(creator), "创建人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getLoginId()), "日志记录项Oid不能为空");

        String sql = "select count(u.iwoid) from SysUser u where u.userId = :USERID and u.state <> :CANCELSTATE ";

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("USERID", partnerVO.getLoginId());
        paramMap.put("CANCELSTATE", SysUser.State.canceled.getValue());

        int idResult = commonDAO.queryObjectCount(sql, paramMap, false);

        if (idResult > 0) {
            throw new AlreadyExistsException("创建子代理商失败：登录名不允许重复！");
        }

        SysUser sysUser = new SysUser();
        Partner partner = new Partner();

        // 查找父代理商
        Partner parentPartner = commonDAO.findObject(Partner.class, operatorUserOid);
        if (parentPartner != null) {
            partner.setParentPartner(parentPartner);
        }

        // 保存代理商
        BeanCopierUtil.copyProperties(partnerVO, partner);
        partner.setIwoid(Generator.generateIwoid());
        // FIXME
        partner.setState("1");// 使用中
        commonDAO.save(partner, false);

        // 保存用户
        sysUser.setBuildType(SysUser.BuildType.create.getValue());
        sysUser.setCreator(creator);
        sysUser.setIwoid(Generator.generateIwoid());
        sysUser.setLastLoginTime(null);
        sysUser.setLoginPwd(DigestHelper.md5Hex(partnerVO.getLoginPwd()));
        sysUser.setModifier(creator);
        sysUser.setModifyTime(new Date());
        sysUser.setUserLevel(userLevel.partner.getValue());
        commonDAO.save(sysUser, false);

        BeanCopierUtil.copyProperties(partner, partnerVO);

        Date processTime = new Date();

        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "创建用户[用户ID=" + sysUser.getUserId() + ", 用户名称=" + sysUser.getUserName() + "]", processTime, processTime, null, sysUser.toString(), SysLog.State.success.getValue(), sysUser.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());

        return partnerVO;
    }

    @Override
    public PartnerVO doTransUpdatePartner(PartnerVO partner, String modifier, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException {
        // TODO Auto-generated method stub
        return null;
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

}
