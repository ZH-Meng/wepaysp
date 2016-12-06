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
import com.zbsp.wepaysp.po.partner.Store;
import com.zbsp.wepaysp.po.partner.Dealer;
import com.zbsp.wepaysp.po.partner.DealerEmployee;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.manage.SysLogService;
import com.zbsp.wepaysp.api.service.partner.DealerEmployeeService;
import com.zbsp.wepaysp.vo.partner.DealerEmployeeVO;


public class DealerEmployeeServiceImpl
    extends BaseService
    implements DealerEmployeeService {

    private SysLogService sysLogService;
    
    @SuppressWarnings("unchecked")
	@Override
    public DealerEmployeeVO doJoinTransQueryDealerEmployeeByOid(String dealerEmployeeOid) {
    	Validator.checkArgument(StringUtils.isBlank(dealerEmployeeOid), "商户员工Oid不能为空！");
        DealerEmployeeVO dealerEmployeeVO = new DealerEmployeeVO();
        DealerEmployee dealerEmployee = commonDAO.findObject(DealerEmployee.class, dealerEmployeeOid);
        if (dealerEmployee != null) {
            BeanCopierUtil.copyProperties(dealerEmployee, dealerEmployeeVO);
            dealerEmployeeVO.setStoreOid(dealerEmployee.getStore().getIwoid());
            // 查找用户
            String sqlStr = "from SysUser s where s.dealerEmployee.iwoid = :IWOID";
            Map<String, Object> sqlMap = new HashMap<String, Object>();
            sqlMap.put("IWOID", dealerEmployee.getIwoid());
            List<SysUser> userList = (List<SysUser>) commonDAO.findObjectList(sqlStr, sqlMap, false);
            if (userList != null && !userList.isEmpty()) {
            	dealerEmployeeVO.setLoginId(userList.get(0).getUserId());
            }
        }
        return dealerEmployeeVO;
    }

    @SuppressWarnings("unchecked")
	@Override
    public List<DealerEmployeeVO> doJoinTransQueryDealerEmployeeList(Map<String, Object> paramMap, int startIndex, int maxResult) {
    	List<DealerEmployeeVO> resultList = new ArrayList<DealerEmployeeVO>();
    	   
    	/* 所属某服务商下的所有服务商查询 */
    	// String state = MapUtils.getString(paramMap, "state");
        String employeeName = MapUtils.getString(paramMap, "employeeName");
        String moblieNumber = MapUtils.getString(paramMap, "moblieNumber");
        String dealerOid = MapUtils.getString(paramMap, "dealerOid");
        String dealerEmployeeOid = MapUtils.getString(paramMap, "dealerEmployeeOid");
        
        Validator.checkArgument(StringUtils.isBlank(dealerOid) && StringUtils.isBlank(dealerEmployeeOid), "商户Oid或商户员工Oid至少一个不能为空！");

        StringBuffer sql = new StringBuffer("select distinct(de) from DealerEmployee de, Dealer d where de.dealer=d");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(employeeName)) {
            sql.append(" and de.employeeName like :EMPLOYEENAME");
            sqlMap.put("EMPLOYEENAME", "%" + employeeName + "%");
        }
        if (StringUtils.isNotBlank(moblieNumber)) {
            sql.append(" and de.moblieNumber like :MOBLIENUMBER");
            sqlMap.put("MOBLIENUMBER", "%" + moblieNumber + "%");
        }

        if (StringUtils.isNotBlank(dealerEmployeeOid)) {
            DealerEmployee de = commonDAO.findObject(DealerEmployee.class, dealerEmployeeOid);
            dealerOid = de.getDealer().getIwoid();
        }
        
        sql.append(" and de.dealer.iwoid = :DEALEROID");
        sqlMap.put("DEALEROID", dealerOid);
        sql.append(" order by de.dealerEmployeeId desc");
        List<DealerEmployee> dealerEmployeeList = (List<DealerEmployee>) commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);

        if (dealerEmployeeList != null && !dealerEmployeeList.isEmpty()) {
            for (DealerEmployee dealerEmployee : dealerEmployeeList) {
                DealerEmployeeVO vo = new DealerEmployeeVO();
                BeanCopierUtil.copyProperties(dealerEmployee, vo);
                vo.setStoreId(dealerEmployee.getStore().getStoreId());
                vo.setStoreName(dealerEmployee.getStore().getStoreName());
                String sqlStr = "from SysUser s where s.dealerEmployee.iwoid = :IWOID";
                sqlMap.clear();
                sqlMap.put("IWOID", dealerEmployee.getIwoid());
                List<SysUser> userList = (List<SysUser>) commonDAO.findObjectList(sqlStr, sqlMap, false);
                if (userList != null && !userList.isEmpty()) {
                    vo.setLoginId(userList.get(0).getUserId());
                }
                resultList.add(vo);
            }
        }

        return resultList;
    }

    @Override
    public int doJoinTransQueryDealerEmployeeCount(Map<String, Object> paramMap) {
        // String state = MapUtils.getString(paramMap, "state");
        String employeeName = MapUtils.getString(paramMap, "employeeName");
        String moblieNumber = MapUtils.getString(paramMap, "moblieNumber");
        String dealerOid = MapUtils.getString(paramMap, "dealerOid");
        Validator.checkArgument(StringUtils.isBlank(dealerOid), "商户Oid不能为空！");

        StringBuffer sql = new StringBuffer("select count(distinct de.iwoid) from DealerEmployee de, Dealer d where de.dealer=d");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(employeeName)) {
            sql.append(" and de.employeeName like :EMPLOYEENAME");
            sqlMap.put("EMPLOYEENAME", "%" + employeeName + "%");
        }
        if (StringUtils.isNotBlank(moblieNumber)) {
            sql.append(" and de.moblieNumber like :MOBLIENUMBER");
            sqlMap.put("MOBLIENUMBER", "%" + moblieNumber + "%");
        }

        sql.append(" and de.dealer.iwoid = :DEALEROID");
        sqlMap.put("DEALEROID", dealerOid);
        return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
    }

    @Override
    public DealerEmployeeVO doTransAddDealerEmployee(DealerEmployeeVO dealerEmployeeVO, String creator, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException, IllegalAccessException {
    	Validator.checkArgument(dealerEmployeeVO == null, "商户员工对象不能为空");
        Validator.checkArgument(StringUtils.isBlank(creator), "创建人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerEmployeeVO.getLoginId()), "登录名不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerEmployeeVO.getLoginPwd()), "登录密码不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerEmployeeVO.getEmployeeName()), "商户员工姓名不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerEmployeeVO.getMoblieNumber()), "商户员工手机号不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerEmployeeVO.getState()), "商户员工状态不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerEmployeeVO.getStoreOid()), "门店Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerEmployeeVO.getRefundPassword()), "退款权限密码不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerEmployeeVO.getDealerOid()), "商户Oid不能为空");
        Validator.checkArgument(dealerEmployeeVO.getEmployeeType() == null, "商户员工状态不能为空");
        
        String sql = "select count(u.iwoid) from SysUser u where u.userId = :USERID and u.state <> :CANCELSTATE ";

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("USERID", dealerEmployeeVO.getLoginId());
        paramMap.put("CANCELSTATE", SysUser.State.canceled.getValue());

        int idResult = commonDAO.queryObjectCount(sql, paramMap, false);
        if (idResult > 0) {
            throw new AlreadyExistsException("登录名重复！");
        }

        // 创建商户员工
        DealerEmployee dealerEmployee = new DealerEmployee();
        dealerEmployee.setIwoid(Generator.generateIwoid());
        
        // 查找所属商户      
        Dealer dealer = commonDAO.findObject(Dealer.class, dealerEmployeeVO.getDealerOid());
        if (dealer == null) {
            throw new NotExistsException("未找到商户信息！");
        }
        dealerEmployee.setDealer(dealer);
        
        // 查找门店
        Store store = commonDAO.findObject(Store.class, dealerEmployeeVO.getStoreOid());
        if (store == null) {
            throw new NotExistsException("未找到门店信息");
        }
        dealerEmployee.setStore(store);
        
        String roleCode = SysNestedRoleCode.CASHIER;// 默认角色
        if (DealerEmployee.EmployeeType.storeManager.getValue() == dealerEmployeeVO.getEmployeeType()) {// 店长
            roleCode = SysNestedRoleCode.STORE_MANAGER;
            // 检查是否已有店长
            if (checkIsExistStoreManager(dealerEmployeeVO.getStoreOid())) {
                throw new AlreadyExistsException("当前门店已有店长");
            }
        }

        sql = "from SysRole r where r.roleId=:ROLEID and r.state <> :CANCELSTATE";
        paramMap.clear();
        paramMap.put("ROLEID", roleCode);
        paramMap.put("CANCELSTATE", SysUser.State.canceled.getValue());
        SysRole role = commonDAO.findObject(sql, paramMap, false);

        if (role == null) {
            throw new NotExistsException("未找到角色信息");
        }
        
        // 获取 服务商员工ID下一个序列值
        sql = "select nextval('" + SysSequenceCode.PARTNER_EMPLOYEE + "') as sequence_value";
        paramMap.clear();
        Object seqObj = commonDAO.findObject(sql, paramMap, true);
        if (seqObj == null) {
            throw new IllegalArgumentException("服务商员工Id对应序列记录不存在");
        }
        String dealerEmployeeId = Generator.generateSequenceNum((Integer) seqObj, SysSequenceMultiple.PARTNER_EMPLOYEE);
        dealerEmployee.setDealerEmployeeId(dealerEmployeeId);
        dealerEmployee.setMoblieNumber(dealerEmployeeVO.getMoblieNumber());
        dealerEmployee.setEmployeeName(dealerEmployeeVO.getEmployeeName());
        dealerEmployee.setRefundPassword(DigestHelper.md5Hex(DigestHelper.sha512HexUnicode(dealerEmployeeVO.getRefundPassword())));
        dealerEmployee.setState(dealerEmployeeVO.getState());
        dealerEmployee.setRemark(dealerEmployeeVO.getRemark());
        dealerEmployee.setCreator(creator);
        dealerEmployee.setEmployeeType(dealerEmployeeVO.getEmployeeType());
        commonDAO.save(dealerEmployee, false);
        
        // 创建用户
        SysUser newUser = new SysUser();
        newUser.setIwoid(Generator.generateIwoid());
        newUser.setState(SysUser.State.normal.getValue());
        newUser.setUserId(dealerEmployeeVO.getLoginId());
        newUser.setUserName(dealerEmployeeVO.getEmployeeName());
        newUser.setLoginPwd(DigestHelper.md5Hex(DigestHelper.sha512HexUnicode(dealerEmployeeVO.getLoginPwd())));
        newUser.setLineTel(dealerEmployeeVO.getMoblieNumber());
        newUser.setBuildType(SysUser.BuildType.create.getValue());
        newUser.setLastLoginTime(null);
        newUser.setDataPermisionType(SysUser.DataPermisionType.none.getValue());
        newUser.setUserLevel(dealerEmployeeVO.getEmployeeType().intValue() == DealerEmployee.EmployeeType.storeManager.getValue() ? SysUser.UserLevel.shopManager.getValue() : SysUser.UserLevel.cashier.getValue());
        newUser.setDealerEmployee(dealerEmployee);
        newUser.setCreator(creator);
        commonDAO.save(newUser, false);

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

        BeanCopierUtil.copyProperties(dealerEmployee, dealerEmployeeVO);

        Date processTime = new Date();

        // 增加商户员工日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "创建商户员工[商户员工ID=" + dealerEmployee.getDealerEmployeeId() + ", 商户员工姓名=" + dealerEmployee.getEmployeeName() + "，商户员工手机号：" + dealerEmployee.getMoblieNumber() + ", 商户=" + dealerEmployee.getDealer().getCompany() + "]", processTime, processTime, null, dealerEmployee.toString(), SysLog.State.success.getValue(), dealerEmployee.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());
        // 添加用户日志logFunctionOid 存 商户员工添加按钮oid
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "创建用户[用户ID=" + newUser.getUserId() + ", 用户名称=" + newUser.getUserName() + "]", processTime, processTime, null, newUser.toString(), SysLog.State.success.getValue(), newUser.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());
        return dealerEmployeeVO;
    }

    @Override
    public DealerEmployeeVO doTransUpdateDealerEmployee(DealerEmployeeVO dealerEmployeeVO, String modifier, String operatorUserOid, String logFunctionOid)
        throws AlreadyExistsException {
    	Validator.checkArgument(dealerEmployeeVO == null, "商户员工对象不能为空");
    	 Validator.checkArgument(StringUtils.isBlank(dealerEmployeeVO.getIwoid()), "商户员工Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(modifier), "修改人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerEmployeeVO.getEmployeeName()), "商户员工姓名不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerEmployeeVO.getMoblieNumber()), "商户员工手机号不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerEmployeeVO.getStoreOid()), "门店Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerEmployeeVO.getState()), "商户员工状态不能为空");
        Validator.checkArgument(dealerEmployeeVO.getEmployeeType() == null, "商户员工状态不能为空");
        
        Date processBeginTime = new Date();
        // 查找商户
        DealerEmployee dealerEmployee = commonDAO.findObject(DealerEmployee.class, dealerEmployeeVO.getIwoid());
        if (dealerEmployee == null) {
            throw new NotExistsException("未找到要修改的商户员工对象");
        }
        String dealerEmployeeStr = dealerEmployee.toString();
        
        if (!StringUtils.equals(dealerEmployee.getStore().getIwoid(), dealerEmployeeVO.getStoreOid())) {
            // 查找门店
            Store store = commonDAO.findObject(Store.class, dealerEmployeeVO.getStoreOid());
            if (store == null) {
                throw new NotExistsException("未找到门店信息");
            }
            dealerEmployee.setStore(store);
        }

        dealerEmployee.setMoblieNumber(dealerEmployeeVO.getMoblieNumber());
        dealerEmployee.setEmployeeName(dealerEmployeeVO.getEmployeeName());
        dealerEmployee.setState(dealerEmployeeVO.getState());
        dealerEmployee.setRemark(dealerEmployeeVO.getRemark());
        if (dealerEmployee.getEmployeeType() == null || dealerEmployee.getEmployeeType().intValue() != dealerEmployeeVO.getEmployeeType()) {// 员工类型改变
            Map<String, Object> jpqlMap = new HashMap<String, Object>();
            String newRoleCode = SysNestedRoleCode.CASHIER;
            if (DealerEmployee.EmployeeType.storeManager.getValue() == dealerEmployeeVO.getEmployeeType()) {// 店长
                newRoleCode = SysNestedRoleCode.STORE_MANAGER;
                // 检查是否已有店长
                if (checkIsExistStoreManager(dealerEmployee.getStore().getIwoid())) {
                    throw new AlreadyExistsException("当前门店已有店长");
                }
            }
            dealerEmployee.setEmployeeType(dealerEmployeeVO.getEmployeeType());
            
            // 修改用户角色
            String jpql = "from SysRole r where r.roleId=:ROLEID and r.state <> :CANCELSTATE";
            jpqlMap.clear();
            jpqlMap.put("ROLEID", newRoleCode);
            jpqlMap.put("CANCELSTATE", SysUser.State.canceled.getValue());
            SysRole newRole = commonDAO.findObject(jpql, jpqlMap, false);

            if (newRole == null) {
                throw new NotExistsException("未找到角色信息，RoleCode=" + newRoleCode);
            } else {
                // 原始角色
                jpqlMap.put("ROLEID", newRoleCode == SysNestedRoleCode.CASHIER ? SysNestedRoleCode.STORE_MANAGER : SysNestedRoleCode.CASHIER);
                SysRole oldRole = commonDAO.findObject(jpql, jpqlMap, false);
                if (oldRole == null) {
                    logger.warn("原始角色" + newRoleCode == SysNestedRoleCode.CASHIER ? SysNestedRoleCode.STORE_MANAGER : SysNestedRoleCode.CASHIER + "不存在");
                }
                // 查找关联用户
                jpql = "from SysUser r where r.dealerEmployee.iwoid=:DEALEREMPLOYEEIWOID";
                jpqlMap.clear();
                jpqlMap.put("DEALEREMPLOYEEIWOID", dealerEmployee.getIwoid());
                SysUser sysUser = commonDAO.findObject(jpql, jpqlMap, false);
                if (sysUser == null) {
                    throw new NotExistsException("未找到用户信息，商户员工oid=" + dealerEmployee.getIwoid());
                }
                // 更改用户级别
                sysUser.setUserLevel(dealerEmployeeVO.getEmployeeType().intValue() == DealerEmployee.EmployeeType.storeManager.getValue() ? SysUser.UserLevel.shopManager.getValue() : SysUser.UserLevel.cashier.getValue());
                commonDAO.update(sysUser);
                
                if (oldRole != null && sysUser != null) {
                    // 删除原始角色用户关系
                    jpql = "delete from SysAuthority r where r.sysRole=:ROLE and r.sysUser=:SYSUSER";
                    jpqlMap.clear();
                    jpqlMap.put("ROLE", oldRole);
                    jpqlMap.put("SYSUSER", sysUser);
                    commonDAO.executeBatch(jpql, jpqlMap, false);
                }
                
                // 创建新的角色用户关系
                SysAuthority sysAuthority = new SysAuthority();
                sysAuthority.setIwoid(Generator.generateIwoid());
                sysAuthority.setSysRole(newRole);
                sysAuthority.setSysUser(sysUser);

                commonDAO.save(sysAuthority, false);

                if (newRole.getUseState().intValue() != SysRole.UseState.used.getValue()) {
                    newRole.setUseState(SysRole.UseState.used.getValue());
                    commonDAO.update(newRole);
                }
            }
        }

        dealerEmployee.setModifier(modifier);
        commonDAO.update(dealerEmployee);

        String newDealerEmployeeStr = dealerEmployee.toString();
        Date processEndTime = new Date();
        // 记录日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "修改商户员工[商户员工姓名：" + dealerEmployee.getEmployeeName() + "，商户员工手机号：" + dealerEmployee.getMoblieNumber() + "]", processBeginTime, processEndTime, dealerEmployeeStr, newDealerEmployeeStr, SysLog.State.success.getValue(), dealerEmployee.getIwoid(), logFunctionOid, SysLog.ActionType.modify.getValue());

        BeanCopierUtil.copyProperties(dealerEmployee, dealerEmployeeVO);
        return dealerEmployeeVO;
    }

	@Override
	public DealerEmployeeVO doTransModifyRefundPwd(String dealerEmployeeOid, String oldPwd, String newPwd, String modifier, String operatorUserOid, String logFunctionOid) throws IllegalAccessException {
		Validator.checkArgument(StringUtils.isBlank(dealerEmployeeOid), "商户员工Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(oldPwd), "旧密码不能为空");
        Validator.checkArgument(StringUtils.isBlank(newPwd), "新密码不能为空");
        Validator.checkArgument(StringUtils.isBlank(modifier), "修改人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        oldPwd = DigestHelper.md5Hex(DigestHelper.sha512HexUnicode(oldPwd));
        DealerEmployee dealerEmployee = commonDAO.findObject(DealerEmployee.class, dealerEmployeeOid);

        if (dealerEmployee == null) {
            throw new IllegalArgumentException("未找到要修改的商户员工信息");
        }

        if (!oldPwd.equals(dealerEmployee.getRefundPassword())) {
            throw new IllegalArgumentException("旧退款密码输入错误，请重试！");
        }

        dealerEmployee.setRefundPassword(DigestHelper.md5Hex(DigestHelper.sha512HexUnicode(newPwd)));

        commonDAO.update(dealerEmployee);
        
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "修改商户员工退款密码", new Date(), new Date(),
            "旧密码=" + oldPwd, "新密码=" + dealerEmployee.getRefundPassword(), SysLog.State.success.getValue(), dealerEmployeeOid, logFunctionOid,
            SysLog.ActionType.modify.getValue());
        DealerEmployeeVO dealerEmployeeVO = new DealerEmployeeVO();
        BeanCopierUtil.copyProperties(dealerEmployee, dealerEmployeeVO);
        return dealerEmployeeVO;
	}

	@Override
	public DealerEmployeeVO doTransResetRefundPwd(String dealerEmployeeOid, String newPwd, String operatorOid, String operatorName, String logFunctionOid) {
		Validator.checkArgument(StringUtils.isBlank(dealerEmployeeOid), "商户员工Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(newPwd), "重置后的新密码不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorOid), "执行重置操作的用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorName), "执行重置操作的用户名称 不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志功能项Oid不能为空");

        DealerEmployee dealerEmployee = commonDAO.findObject(DealerEmployee.class, dealerEmployeeOid);

        if (dealerEmployee == null) {
            throw new IllegalArgumentException("未找到要重置的商户员工信息");
        }

        String oldPwdStr = dealerEmployee.getRefundPassword();

        Date currentTime = new Date();
        dealerEmployee.setRefundPassword(DigestHelper.md5Hex(DigestHelper.sha512HexUnicode(newPwd)));
        dealerEmployee.setModifier(operatorName);
        commonDAO.update(dealerEmployee);

        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorOid, "重置商户员工退款密码[商户员工ID=" + dealerEmployee.getDealerEmployeeId() + ", 新密码：" + dealerEmployee.getRemark() +"]",
            currentTime, new Date(), oldPwdStr, dealerEmployee.getRefundPassword(), SysLog.State.success.getValue(), dealerEmployeeOid,
            logFunctionOid, SysLog.ActionType.resetPwd.getValue());
		
        DealerEmployeeVO dealerEmployeeVO = new DealerEmployeeVO();
        BeanCopierUtil.copyProperties(dealerEmployee, dealerEmployeeVO);
        return dealerEmployeeVO;
	}
	
	private boolean checkIsExistStoreManager(String storeOid) {
	    Validator.checkArgument(StringUtils.isBlank(storeOid), "门店Oid不能为空");
	    Map<String, Object> jpqlMap = new HashMap<String, Object>();
	    String jpql = "select count(d.iwoid) from DealerEmployee d where d.employeeType=:EMPLOYEETYPE and d.store.iwoid=:STOREOID";
        jpqlMap.put("EMPLOYEETYPE", DealerEmployee.EmployeeType.storeManager.getValue());
        jpqlMap.put("STOREOID", storeOid);
        int count = commonDAO.queryObjectCount(jpql, jpqlMap, false);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
	}
	
	 public void setSysLogService(SysLogService sysLogService) {
	        this.sysLogService = sysLogService;
	 }

}
