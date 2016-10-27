package com.zbsp.wepaysp.service.partner.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.common.config.SysNestedRoleCode;
import com.zbsp.wepaysp.common.config.SysSequenceCode;
import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.security.DigestHelper;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.manage.SysAuthority;
import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.service.BaseService;
import com.zbsp.wepaysp.service.manage.SysLogService;
import com.zbsp.wepaysp.service.partner.PartnerService;
import com.zbsp.wepaysp.vo.partner.PartnerVO;

public class PartnerServiceImpl
    extends BaseService
    implements PartnerService {

    private SysLogService sysLogService;

    @SuppressWarnings("unchecked")
    @Override
    public PartnerVO doJoinTransQueryPartnerByOid(String partnerOid) {
        Validator.checkArgument(StringUtils.isBlank(partnerOid), "服务商Oid不能为空");
        PartnerVO partnerVO = new PartnerVO();
        Partner partner = commonDAO.findObject(Partner.class, partnerOid);

        if (partner != null) {
            BeanCopierUtil.copyProperties(partner, partnerVO);
            // 查找用户
            String sqlStr = "from SysUser s where s.partner.iwoid = :IWOID";
            Map<String, Object> sqlMap = new HashMap<String, Object>();
            sqlMap.put("IWOID", partner.getIwoid());
            List<SysUser> userList = (List<SysUser>) commonDAO.findObjectList(sqlStr, sqlMap, false);
            if (userList != null && !userList.isEmpty()) {
                partnerVO.setLoginId(userList.get(0).getUserId());
                // partnerVO.setLoginPwd(userList.get(0).getLoginPwd());
            }
            // 查找上级
            if (partner.getParentPartner() != null) {
                partnerVO.setParentPartnerOid(partner.getParentPartner().getIwoid());
            }
        }
        return partnerVO;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PartnerVO> doJoinTransQueryPartnerList(Map<String, Object> paramMap, int startIndex, int maxResult) {
        List<PartnerVO> resultList = new ArrayList<PartnerVO>();

        /* 在当前用户所属服务商的下级服务商集合中模糊查询 */
        String loginId = MapUtils.getString(paramMap, "loginId");
        String contactor = MapUtils.getString(paramMap, "contactor");
        String state = MapUtils.getString(paramMap, "state");
        String company = MapUtils.getString(paramMap, "company");

        String parentPartnerOid = MapUtils.getString(paramMap, "parentPartnerOid");
        String currentUserOid = MapUtils.getString(paramMap, "currentUserOid");

        StringBuffer sql = new StringBuffer("select distinct(p) from Partner p, SysUser u where u.partner=p");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(loginId)) {
            sql.append(" and u.userId like :LOGINID");
            sqlMap.put("LOGINID", "%" + loginId + "%");
        }
        if (StringUtils.isNotBlank(contactor)) {
            sql.append(" and p.contactor like :CONTACTOR");
            sqlMap.put("CONTACTOR", "%" + contactor + "%");
        }
        if (StringUtils.isNotBlank(state)) {
            sql.append(" and p.state = :STATE");
            sqlMap.put("STATE", state);
        }
        if (StringUtils.isNotBlank(company)) {
            sql.append(" and p.company like :COMPANY");
            sqlMap.put("COMPANY", "%" + company + "%");
        }

        sql.append(" and p.parentPartner.iwoid = :PARENTPARTNEROID");
        // 如果没有限制父级服务商oid，需要将当前用户的服务商oid作为限制
        if (StringUtils.isBlank(parentPartnerOid)) {
            SysUser user = commonDAO.findObject(SysUser.class, currentUserOid);
            // 查找父服务商
            if (user != null && user.getPartner() != null) {
                parentPartnerOid = user.getPartner().getIwoid();
            }
        }
        sqlMap.put("PARENTPARTNEROID", parentPartnerOid);

        sql.append(" order by p.createTime desc");
        List<Partner> partnerList = (List<Partner>) commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);

        if (partnerList != null && !partnerList.isEmpty()) {
            for (Partner partner : partnerList) {
                PartnerVO vo = new PartnerVO();
                BeanCopierUtil.copyProperties(partner, vo);

                String sqlStr = "from SysUser s where s.partner.iwoid = :IWOID";
                sqlMap.clear();
                sqlMap.put("IWOID", partner.getIwoid());
                List<SysUser> userList = (List<SysUser>) commonDAO.findObjectList(sqlStr, sqlMap, false);
                if (userList != null && !userList.isEmpty()) {
                    vo.setLoginId(userList.get(0).getUserId());
                }
                vo.setParentCompany(partner.getParentPartner().getCompany());
                resultList.add(vo);
            }
        }

        return resultList;
    }

    @Override
    public int doJoinTransQueryPartnerCount(Map<String, Object> paramMap) {
        String loginId = MapUtils.getString(paramMap, "loginId");
        String contactor = MapUtils.getString(paramMap, "contactor");
        String state = MapUtils.getString(paramMap, "state");
        String company = MapUtils.getString(paramMap, "company");

        String parentPartnerOid = MapUtils.getString(paramMap, "parentPartnerOid");
        String currentUserOid = MapUtils.getString(paramMap, "currentUserOid");

        StringBuffer sql = new StringBuffer("select count(distinct p.iwoid) from Partner p, SysUser u where u.partner=p");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(loginId)) {
            sql.append(" and u.userId like :LOGINID");
            sqlMap.put("LOGINID", "%" + loginId + "%");
        }
        if (StringUtils.isNotBlank(contactor)) {
            sql.append(" and p.contactor like :CONTACTOR");
            sqlMap.put("CONTACTOR", "%" + contactor + "%");
        }
        if (StringUtils.isNotBlank(state)) {
            sql.append(" and p.state = :STATE");
            sqlMap.put("STATE", state);
        }
        if (StringUtils.isNotBlank(company)) {
            sql.append(" and p.company like :COMPANY");
            sqlMap.put("COMPANY", "%" + company + "%");
        }

        sql.append(" and p.parentPartner.iwoid = :PARENTPARTNEROID");
        if (StringUtils.isBlank(parentPartnerOid)) {// 当前用户的服务商oid
            SysUser user = commonDAO.findObject(SysUser.class, currentUserOid);
            // 查找父服务商
            if (user != null && user.getPartner() != null) {
                parentPartnerOid = user.getPartner().getIwoid();
            }
        }
        sqlMap.put("PARENTPARTNEROID", parentPartnerOid);
        return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
    }

    @Override
    public PartnerVO doTransAddPartner(PartnerVO partnerVO, String creator, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException {
        Validator.checkArgument(partnerVO == null, "服务商对象不能为空");
        Validator.checkArgument(StringUtils.isBlank(creator), "创建人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getLoginId()), "登录名不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getLoginPwd()), "登录密码不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getContactor()), "联系人不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getMoblieNumber()), "手机号不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getTelephone()), "固定电话不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getCompany()), "公司不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getAddress()), "地址不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getState()), "状态不能为空");
        Validator.checkArgument(partnerVO.getFeeRate() == null, "分润比率不能为空");
        Validator.checkArgument(partnerVO.getBalance() == null, "余额不能为空");
        Validator.checkArgument(partnerVO.getContractBegin() == null, "期限开始日期不能为空");
        Validator.checkArgument(partnerVO.getContractEnd() == null, "期限截止日期不能为空");

        String sql = "select count(u.iwoid) from SysUser u where u.userId = :USERID and u.state <> :CANCELSTATE ";

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("USERID", partnerVO.getLoginId());
        paramMap.put("CANCELSTATE", SysUser.State.canceled.getValue());

        int idResult = commonDAO.queryObjectCount(sql, paramMap, false);

        if (idResult > 0) {
            throw new AlreadyExistsException("登录名重复！");
        }

        // 创建服务商
        Partner partner = new Partner();
        BeanCopierUtil.copyProperties(partnerVO, partner);
        partner.setIwoid(Generator.generateIwoid());
        
        // 获取 服务商ID 下一个序列值
        sql = "select nextval('" + SysSequenceCode.PARTNER + "') as sequence_value";
        paramMap.clear();
        // paramMap.put("SEQUENCE_NAME", SysSequenceCode.PARTNER);
        Object seqObj = commonDAO.findObject(sql, paramMap, true);
        if (seqObj == null) {
            throw new IllegalArgumentException("服务商Id对应序列记录不存在");
        }
        String partnerId = Generator.generateSequenceNum((Integer) seqObj);
        partner.setPartnerId(partnerId);
        
        // 查找父服务商
        Partner parentPartner = null;
        SysUser user = commonDAO.findObject(SysUser.class, operatorUserOid);
        if (user != null && user.getPartner() != null) {
            // parentPartner = commonDAO.findObject(Partner.class, user.getPartner().getIwoid());
            parentPartner = user.getPartner();
        }
        if (parentPartner != null) {
            partner.setParentPartner(parentPartner);
            partner.setLevel(parentPartner.getLevel() + 1);
        } else {
            partner.setLevel(1);// 顶级服务商
        }
        partner.setCreator(creator);
        commonDAO.save(partner, false);

        // 创建服务商用户
        SysUser newUser = new SysUser();
        newUser.setIwoid(Generator.generateIwoid());
        newUser.setState(SysUser.State.normal.getValue());
        newUser.setUserId(partnerVO.getLoginId());
        newUser.setUserName(partnerVO.getContactor());
        newUser.setLoginPwd(DigestHelper.md5Hex(DigestHelper.sha512HexUnicode(partnerVO.getLoginPwd())));
        newUser.setLineTel(StringUtils.isNotBlank(partnerVO.getMoblieNumber()) ? partnerVO.getMoblieNumber() : partnerVO.getTelephone());
        newUser.setEmail(partnerVO.getEmail());
        newUser.setBuildType(SysUser.BuildType.create.getValue());
        newUser.setLastLoginTime(null);
        if (partner.getLevel() == 1) {
            newUser.setDataPermisionType(SysUser.DataPermisionType.partner1.getValue());
        } else if (partner.getLevel() == 2) {
            newUser.setDataPermisionType(SysUser.DataPermisionType.partner2.getValue());
        } else if (partner.getLevel() == 3) {
            newUser.setDataPermisionType(SysUser.DataPermisionType.partner3.getValue());
        }
        newUser.setUserLevel(SysUser.UserLevel.partner.getValue());
        newUser.setPartner(partner);
        newUser.setCreator(creator);
        commonDAO.save(newUser, false);

        // 设置默认角色
        String roleCode = "";
        if (partner.getLevel() == 1) {
            roleCode = SysNestedRoleCode.PARTNER1;
        } else if (partner.getLevel() == 2) {
            roleCode = SysNestedRoleCode.PARTNER2;
        } else if (partner.getLevel() == 3) {
            roleCode = SysNestedRoleCode.PARTNER3;
        }

        sql = "from SysRole r where r.roleId=:ROLEID and r.state <> :CANCELSTATE";
        paramMap.clear();
        paramMap.put("ROLEID", roleCode);
        paramMap.put("CANCELSTATE", SysUser.State.canceled.getValue());
        SysRole role = commonDAO.findObject(sql, paramMap, false);

        if (role == null) {
            throw new NotExistsException("未找到角色信息");
        } else {
            // 创建角色用户关系
            SysAuthority sysAuthority = new SysAuthority();
            sysAuthority.setIwoid(Generator.generateIwoid());
            sysAuthority.setSysRole(role);
            sysAuthority.setSysUser(newUser);

            commonDAO.save(sysAuthority, false);

            if (role.getUseState().intValue() != SysRole.UseState.used.getValue()) {
                role.setUseState(SysRole.UseState.used.getValue());
                commonDAO.update(role);
            }
        }

        BeanCopierUtil.copyProperties(partner, partnerVO);

        Date processTime = new Date();

        // 增加服务商日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "创建服务商[服务商ID=" + partner.getPartnerId() + ", 公司=" + partner.getCompany() + ", 联系人=" + partner.getContactor() + "]", processTime, processTime, null, partner.toString(), SysLog.State.success.getValue(), partner.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());
        // 添加用户日志logFunctionOid 存 服务商添加按钮oid
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "创建用户[用户ID=" + newUser.getUserId() + ", 用户名称=" + newUser.getUserName() + "]", processTime, processTime, null, newUser.toString(), SysLog.State.success.getValue(), newUser.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());

        return partnerVO;
    }

    @Override
    public PartnerVO doTransUpdatePartner(PartnerVO partnerVO, String modifier, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException {
        Validator.checkArgument(partnerVO == null, "服务商对象不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getIwoid()), "服务商Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(modifier), "修改人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        // 登录名、登录密码不允许修改
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getContactor()), "联系人不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getMoblieNumber()), "手机号不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getTelephone()), "固定电话不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getCompany()), "公司不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getAddress()), "地址不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerVO.getState()), "状态不能为空");
        Validator.checkArgument(partnerVO.getFeeRate() == null, "分润比率不能为空");
        Validator.checkArgument(partnerVO.getBalance() == null, "余额不能为空");
        Validator.checkArgument(partnerVO.getContractBegin() == null, "期限开始日期不能为空");
        Validator.checkArgument(partnerVO.getContractEnd() == null, "期限截止日期不能为空");

        Date processBeginTime = new Date();
        // 查找服务商
        Partner partner = commonDAO.findObject(Partner.class, partnerVO.getIwoid());
        if (partner == null) {
            throw new NotExistsException("未找到要修改的服务商对象");
        } else if (Partner.State.frozen.getValue().equals(partner.getState())) {// 冻结
            throw new IllegalStateException("非法修改：服务商冻结状态不允许修改！");
        }

        String partnerStr = partner.toString();

        partner.setContactor(partnerVO.getContactor());
        partner.setCompany(partnerVO.getCompany());
        partner.setMoblieNumber(partnerVO.getMoblieNumber());
        partner.setAddress(partnerVO.getAddress());
        partner.setTelephone(partnerVO.getTelephone());
        partner.setBalance(partnerVO.getBalance());
        partner.setContractBegin(partnerVO.getContractBegin());
        partner.setContractEnd(partnerVO.getContractEnd());
        partner.setFeeRate(partnerVO.getFeeRate());
        partner.setState(partnerVO.getState());
        partner.setCopyright(partnerVO.getCopyright());
        partner.setCopyrightUrl(partnerVO.getCopyrightUrl());
        partner.setRemark(partnerVO.getRemark());
        partner.setEmail(partnerVO.getEmail());
        partner.setModifier(modifier);
        commonDAO.update(partner);

        String newPartnerStr = partner.toString();
        Date processEndTime = new Date();
        // 记录日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "修改服务商[服务商联系人：" + partner.getContactor() + ",服务商公司：" + partner.getCompany() + "]", processBeginTime, processEndTime, partnerStr, newPartnerStr, SysLog.State.success.getValue(), partner.getIwoid(), logFunctionOid, SysLog.ActionType.modify.getValue());

        BeanCopierUtil.copyProperties(partner, partnerVO);
        return partnerVO;
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

}
