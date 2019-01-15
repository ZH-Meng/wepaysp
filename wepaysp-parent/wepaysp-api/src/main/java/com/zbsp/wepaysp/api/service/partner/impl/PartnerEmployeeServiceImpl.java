package com.zbsp.wepaysp.api.service.partner.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.common.config.SysNestedRoleCode;
import com.zbsp.wepaysp.common.config.SysSequenceCode;
import com.zbsp.wepaysp.common.config.SysSequenceMultiple;
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
import com.zbsp.wepaysp.po.partner.PartnerEmployee;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.manage.SysLogService;
import com.zbsp.wepaysp.api.service.partner.PartnerEmployeeService;
import com.zbsp.wepaysp.vo.partner.PartnerEmployeeVO;


public class PartnerEmployeeServiceImpl
    extends BaseService
    implements PartnerEmployeeService {

    private SysLogService sysLogService;
    
	@Override
    public PartnerEmployeeVO doJoinTransQueryPartnerEmployeeByOid(String partnerEmployeeOid) {
    	Validator.checkArgument(StringUtils.isBlank(partnerEmployeeOid), "代理商员工Oid不能为空！");
        PartnerEmployeeVO partnerEmployeeVO = new PartnerEmployeeVO();
        PartnerEmployee partnerEmployee = commonDAO.findObject(PartnerEmployee.class, partnerEmployeeOid);
        if (partnerEmployee != null) {
            BeanCopierUtil.copyProperties(partnerEmployee, partnerEmployeeVO);
            // 查找用户
            String sqlStr = "from SysUser s where s.partnerEmployee.iwoid = :IWOID";
            Map<String, Object> sqlMap = new HashMap<String, Object>();
            sqlMap.put("IWOID", partnerEmployee.getIwoid());
            List<SysUser> userList = commonDAO.findObjectList(sqlStr, sqlMap, false);
            if (userList != null && !userList.isEmpty()) {
            	partnerEmployeeVO.setLoginId(userList.get(0).getUserId());
            }
        }
        return partnerEmployeeVO;
    }

	@Override
    public List<PartnerEmployeeVO> doJoinTransQueryPartnerEmployeeList(Map<String, Object> paramMap, int startIndex, int maxResult) {
    	List<PartnerEmployeeVO> resultList = new ArrayList<PartnerEmployeeVO>();
    	   
    	/* 所属某代理商下的所有代理商查询 */
    	// String state = MapUtils.getString(paramMap, "state");
        String employeeName = StringUtils.trim(MapUtils.getString(paramMap, "employeeName"));
        String moblieNumber = StringUtils.trim(MapUtils.getString(paramMap, "moblieNumber"));
        String partnerOid = MapUtils.getString(paramMap, "partnerOid");
        String partnerEmployeeOid = MapUtils.getString(paramMap, "partnerEmployeeOid");
        Validator.checkArgument(StringUtils.isBlank(partnerOid) && StringUtils.isBlank(partnerEmployeeOid), "代理商Oid或代理商员工Oid至少一个不能为空！");
        
        StringBuffer sql = new StringBuffer("select distinct(pe) from PartnerEmployee pe, Partner p where pe.partner=p");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(employeeName)) {
            sql.append(" and pe.employeeName like :EMPLOYEENAME");
            sqlMap.put("EMPLOYEENAME", "%" + employeeName + "%");
        }
        if (StringUtils.isNotBlank(moblieNumber)) {
            sql.append(" and pe.moblieNumber like :MOBLIENUMBER");
            sqlMap.put("MOBLIENUMBER", "%" + moblieNumber + "%");
        }

        if (StringUtils.isNotBlank(partnerEmployeeOid)) {
            PartnerEmployee pe = commonDAO.findObject(PartnerEmployee.class, partnerEmployeeOid);
            partnerOid = pe.getPartner().getIwoid();
        }
        
        sql.append(" and pe.partner.iwoid = :PARTNEROID");
        sqlMap.put("PARTNEROID", partnerOid);
        sql.append(" order by pe.partnerEmployeeId desc");
        List<PartnerEmployee> partnerEmployeeList = commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);

        if (partnerEmployeeList != null && !partnerEmployeeList.isEmpty()) {
            for (PartnerEmployee partnerEmployee : partnerEmployeeList) {
                PartnerEmployeeVO vo = new PartnerEmployeeVO();
                BeanCopierUtil.copyProperties(partnerEmployee, vo);
                
                String sqlStr = "from SysUser s where s.partnerEmployee.iwoid = :IWOID";
                sqlMap.clear();
                sqlMap.put("IWOID", partnerEmployee.getIwoid());
                List<SysUser> userList = commonDAO.findObjectList(sqlStr, sqlMap, false);
                if (userList != null && !userList.isEmpty()) {
                    vo.setLoginId(userList.get(0).getUserId());
                }
                resultList.add(vo);
            }
        }

        return resultList;
    }

    @Override
    public int doJoinTransQueryPartnerEmployeeCount(Map<String, Object> paramMap) {
        // String state = MapUtils.getString(paramMap, "state");
        String employeeName = StringUtils.trim(MapUtils.getString(paramMap, "employeeName"));
        String moblieNumber = StringUtils.trim(MapUtils.getString(paramMap, "moblieNumber"));
        String partnerOid = MapUtils.getString(paramMap, "partnerOid");
        Validator.checkArgument(StringUtils.isBlank(partnerOid), "代理商Oid不能为空！");

        StringBuffer sql = new StringBuffer("select count(distinct pe.iwoid) from PartnerEmployee pe, Partner p where pe.partner=p");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(employeeName)) {
            sql.append(" and pe.employeeName like :EMPLOYEENAME");
            sqlMap.put("EMPLOYEENAME", "%" + employeeName + "%");
        }
        if (StringUtils.isNotBlank(moblieNumber)) {
            sql.append(" and pe.moblieNumber like :MOBLIENUMBER");
            sqlMap.put("MOBLIENUMBER", "%" + moblieNumber + "%");
        }

        sql.append(" and pe.partner.iwoid = :PARTNEROID");
        sqlMap.put("PARTNEROID", partnerOid);
        return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
    }

    @Override
    public PartnerEmployeeVO doTransAddPartnerEmployee(PartnerEmployeeVO partnerEmployeeVO, String creator, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException, IllegalAccessException {
    	Validator.checkArgument(partnerEmployeeVO == null, "业务员对象不能为空");
        Validator.checkArgument(StringUtils.isBlank(creator), "创建人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerEmployeeVO.getLoginId()), "登录名不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerEmployeeVO.getLoginPwd()), "登录密码不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerEmployeeVO.getEmployeeName()), "业务员姓名不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerEmployeeVO.getMoblieNumber()), "业务员手机号不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerEmployeeVO.getState()), "业务员状态不能为空");
        Validator.checkArgument(partnerEmployeeVO.getFeeRate() == null, "分润比率不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerEmployeeVO.getPartnerOid()), "代理商Oid不能为空");
        
        String sql = "select count(u.iwoid) from SysUser u where u.userId = :USERID and u.state <> :CANCELSTATE ";

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("USERID", StringUtils.trim(partnerEmployeeVO.getLoginId()));
        paramMap.put("CANCELSTATE", SysUser.State.canceled.getValue());

        int idResult = commonDAO.queryObjectCount(sql, paramMap, false);
        if (idResult > 0) {
            throw new AlreadyExistsException("登录名重复！");
        }

        // 创建代理商员工（业务员）
        PartnerEmployee partnerEmployee = new PartnerEmployee();
        partnerEmployee.setIwoid(Generator.generateIwoid());
        
        // 查找所属代理商
        Partner partner = commonDAO.findObject(Partner.class, partnerEmployeeVO.getPartnerOid());
        if (partner == null) {
            throw new NotExistsException("未找到代理商！");
        }
        partnerEmployee.setPartner(partner);
        
        // 获取 代理商员工ID下一个序列值
        sql = "select nextval('" + SysSequenceCode.PARTNER_EMPLOYEE + "') as sequence_value";
        paramMap.clear();
        Object seqObj = commonDAO.findObject(sql, paramMap, true);
        if (seqObj == null) {
            throw new IllegalArgumentException("代理商员工Id对应序列记录不存在");
        }
        String partnerEmployeeId = Generator.generateSequenceNum((Integer) seqObj, SysSequenceMultiple.PARTNER_EMPLOYEE);
        partnerEmployee.setPartnerEmployeeId(partnerEmployeeId);
        partnerEmployee.setMoblieNumber(partnerEmployeeVO.getMoblieNumber());
        partnerEmployee.setEmployeeName(partnerEmployeeVO.getEmployeeName());
        partnerEmployee.setState(partnerEmployeeVO.getState());
        partnerEmployee.setRemark(partnerEmployeeVO.getRemark());
        partnerEmployee.setCreator(creator);
        partnerEmployee.setFeeRate(partnerEmployeeVO.getFeeRate());
        commonDAO.save(partnerEmployee, false);
        
        // 创建用户
        SysUser newUser = new SysUser();
        newUser.setIwoid(Generator.generateIwoid());
        newUser.setState(SysUser.State.normal.getValue());
        newUser.setUserId(StringUtils.trim(partnerEmployeeVO.getLoginId()));
        newUser.setUserName(partnerEmployee.getEmployeeName());
        newUser.setLoginPwd(DigestHelper.md5Hex(DigestHelper.sha512HexUnicode(StringUtils.trim(partnerEmployeeVO.getLoginPwd()))));
        newUser.setLineTel(partnerEmployee.getMoblieNumber());
        newUser.setBuildType(SysUser.BuildType.create.getValue());
        newUser.setLastLoginTime(null);
        newUser.setDataPermisionType(SysUser.DataPermisionType.none.getValue());
        newUser.setUserLevel(SysUser.UserLevel.salesman.getValue());
        newUser.setPartnerEmployee(partnerEmployee);
        newUser.setCreator(creator);
        commonDAO.save(newUser, false);

        // 设置默认角色
        String roleCode = SysNestedRoleCode.SALESMAN;

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


        BeanCopierUtil.copyProperties(partnerEmployee, partnerEmployeeVO);

        Date processTime = new Date();

        // 增加业务员日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "创建业务员[业务员ID=" + partnerEmployee.getPartnerEmployeeId() + ", 业务员姓名=" + partnerEmployee.getEmployeeName() + "，业务员手机号：" + partnerEmployee.getMoblieNumber() + ", 代理商=" + partnerEmployee.getPartner().getCompany() + "]", processTime, processTime, null, partnerEmployee.toString(), SysLog.State.success.getValue(), partnerEmployee.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());
        // 添加用户日志logFunctionOid 存 业务员添加按钮oid
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "创建用户[用户ID=" + newUser.getUserId() + ", 用户名称=" + newUser.getUserName() + "]", processTime, processTime, null, newUser.toString(), SysLog.State.success.getValue(), newUser.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());

        return partnerEmployeeVO;
    }

    @Override
    public PartnerEmployeeVO doTransUpdatePartnerEmployee(PartnerEmployeeVO partnerEmployeeVO, String modifier, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException {
    	Validator.checkArgument(partnerEmployeeVO == null, "业务员对象不能为空");
    	Validator.checkArgument(StringUtils.isBlank(partnerEmployeeVO.getIwoid()), "业务员Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(modifier), "修改人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerEmployeeVO.getEmployeeName()), "业务员姓名不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerEmployeeVO.getMoblieNumber()), "业务员手机号不能为空");
        Validator.checkArgument(partnerEmployeeVO.getFeeRate() == null, "分润比率不能为空");
        
        Date processBeginTime = new Date();
        // 查找商户
        PartnerEmployee partnerEmployee = commonDAO.findObject(PartnerEmployee.class, partnerEmployeeVO.getIwoid());
        if (partnerEmployee == null) {
            throw new NotExistsException("未找到要修改的业务员对象");
        }

        String partnerEmployeeStr = partnerEmployee.toString();
        partnerEmployee.setMoblieNumber(partnerEmployeeVO.getMoblieNumber());
        partnerEmployee.setEmployeeName(partnerEmployeeVO.getEmployeeName());
        partnerEmployee.setState(partnerEmployeeVO.getState());
        partnerEmployee.setRemark(partnerEmployeeVO.getRemark());
        partnerEmployee.setFeeRate(partnerEmployeeVO.getFeeRate());
        partnerEmployee.setModifier(modifier);
        commonDAO.update(partnerEmployee);

        String newPartnerEmployeeStr = partnerEmployee.toString();
        Date processEndTime = new Date();
        // 记录日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "修改业务员[业务员姓名：" + partnerEmployee.getEmployeeName() + "，业务员手机号：" + partnerEmployee.getMoblieNumber() + "]", processBeginTime, processEndTime, partnerEmployeeStr, newPartnerEmployeeStr, SysLog.State.success.getValue(), partnerEmployee.getIwoid(), logFunctionOid, SysLog.ActionType.modify.getValue());

        BeanCopierUtil.copyProperties(partnerEmployee, partnerEmployeeVO);
        return partnerEmployeeVO;
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

}
