/*
 * SysUserServiceImpl.java
 * 创建者：杨帆
 * 创建日期：2015年6月8日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.api.service.manage.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.mobile.result.CommonResult;
import com.zbsp.wepaysp.common.mobile.result.LoginResult;
import com.zbsp.wepaysp.common.security.DigestHelper;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.mo.userlogin.v1_0.UserLoginResponse;
import com.zbsp.wepaysp.po.manage.SysAuthority;
import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.po.manage.SysUserLoginToken;
import com.zbsp.wepaysp.po.partner.DealerEmployee;
import com.zbsp.wepaysp.po.partner.Store;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.manage.SysLogService;
import com.zbsp.wepaysp.api.service.manage.SysUserService;
import com.zbsp.wepaysp.vo.manage.SysUserVO;

/**
 * @author 杨帆
 */
public class SysUserServiceImpl extends BaseService implements SysUserService {

    private SysLogService sysLogService;

    @Override
    public Map<String, Object> doTransUserLogin(String userId, String password, String ip) throws IllegalStateException, IllegalAccessException,
            AlreadyExistsException {
        Validator.checkArgument(StringUtils.isBlank(userId), "用户标识不能为空");
        Validator.checkArgument(StringUtils.isBlank(password), "登录密码不能为空");
        Validator.checkArgument(StringUtils.isBlank(ip), "登录ip不能为空");

        String sql = "select u from SysUser u left join fetch u.partner left join fetch u.dealer left join fetch u.partnerEmployee left join fetch u.dealerEmployee where u.userId = :USERID "
            + "and u.loginPwd = :LOGINPWD and u.state <> :DELETESTATE";

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("USERID", userId);
        paramMap.put("LOGINPWD", DigestHelper.md5Hex(password));
        paramMap.put("DELETESTATE", SysUser.State.canceled.getValue());

        SysUser sysUser = commonDAO.findObject(sql, paramMap, false);

        if (sysUser == null) {
            throw new IllegalAccessException("用户名或密码不正确！");
        } else if (sysUser.getState() == SysUser.State.frozen.getValue()) {
            throw new IllegalStateException("该用户已冻结，不允许登录！");
        }
        if (sysUser.getDealerEmployee() != null && StringUtils.isNotBlank(sysUser.getDealerEmployee().getIwoid())) {
        	sysUser.getDealerEmployee().getStore();
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();

        DateTime now = DateTime.now();

        sql = "update SysUser t set t.lastLoginTime = :LASTLOGINTIME, t.lastLoginIp=:LASTLOGINIP where t.iwoid = :IWOID";

        paramMap.clear();
       	paramMap.put("LASTLOGINIP", ip);
        paramMap.put("LASTLOGINTIME", new Date());
        paramMap.put("IWOID", sysUser.getIwoid());

        commonDAO.executeBatch(sql, paramMap, false);

        resultMap.put("sysUser", sysUser);

        sql = "select t from SysUserLoginToken t where t.sysUserOid = :USEROID";

        paramMap.clear();
        paramMap.put("USEROID", sysUser.getIwoid());

        SysUserLoginToken loginToken = commonDAO.findObject(sql, paramMap, false);

        String token = Generator.generateIwoid();
        Date expireTime = now.plusMinutes(10).toDate();

        if (loginToken == null) {
            loginToken = new SysUserLoginToken();

            loginToken.setSysUserOid(sysUser.getIwoid());
            loginToken.setIwoid(Generator.generateIwoid());
            loginToken.setToken(token);
            loginToken.setExpireTime(expireTime);

            commonDAO.save(loginToken, false);

        } else {
            /*if (now.isBefore(new DateTime(loginToken.getExpireTime()))) {
                throw new AlreadyExistsException("该账号已登录，不允许重复登录！");
            }*/

            loginToken.setToken(token);
            loginToken.setExpireTime(expireTime);

            commonDAO.update(loginToken);
        }

        resultMap.put("loginToken", loginToken);

        sysLogService.doTransSaveSysLog(SysLog.LogType.userLogin.getValue(), sysUser.getIwoid(), "用户登录", now.toDate(),
            now.toDate(), null, null, SysLog.State.success.getValue(), null, null, null);

        return resultMap;
    }

    @Override
    public void doTransModifyUserPwd(String userOid, String oldPwd, String newPwd) throws IllegalAccessException {
        Validator.checkArgument(StringUtils.isBlank(userOid), "用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(oldPwd), "旧密码不能为空");
        Validator.checkArgument(StringUtils.isBlank(newPwd), "新密码不能为空");

        SysUser sysUser = commonDAO.findObject(SysUser.class, userOid);

        if (sysUser == null) {
            throw new IllegalArgumentException("未找到要修改的用户信息");
        }

        if (!DigestHelper.md5Hex(oldPwd).equals(sysUser.getLoginPwd())) {
            throw new IllegalArgumentException("旧密码输入错误，请重试！");
        }

        sysUser.setLoginPwd(DigestHelper.md5Hex(newPwd));

        commonDAO.update(sysUser);

        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), userOid, "修改密码", new Date(), new Date(),
            "旧密码=" + DigestHelper.md5Hex(oldPwd), "新密码=" + sysUser.getLoginPwd(), SysLog.State.success.getValue(), userOid, null,
            SysLog.ActionType.modify.getValue());
    }

    @Override
    public void doTransUserLogout(String userOid) {
        Validator.checkArgument(StringUtils.isBlank(userOid), "用户Oid不能为空");

        /*String sql = "select t from SysUserLoginToken t where t.sysUserOid = :USEROID";

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("USEROID", userOid);

        SysUserLoginToken loginToken = (SysUserLoginToken) commonDAO.findObject(sql, paramMap, false);

        if (loginToken != null) {
            loginToken.setExpireTime(new Date());
            commonDAO.update(loginToken);
        }*/

        sysLogService.doTransSaveSysLog(SysLog.LogType.userLogout.getValue(), userOid, "用户退出登录", new Date(), new Date(),
            null, null, SysLog.State.success.getValue(), null, null, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SysUserVO> doJoinTransQuerySysUserList(Map<String, Object> paramMap, int startIndex, int maxResult) {
        String userId = MapUtils.getString(paramMap, "userId");
        String userName = MapUtils.getString(paramMap, "userName");
        String roleOid = MapUtils.getString(paramMap, "roleOid");
        Integer state = MapUtils.getInteger(paramMap, "state");
        Integer buildType = MapUtils.getInteger(paramMap, "buildType");
        
        StringBuffer sql = new StringBuffer("select u from SysUser u where 1=1");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(userId)) {
            sql.append(" and u.userId like :USERID");
            sqlMap.put("USERID", "%" + userId + "%");
        }

        if (StringUtils.isNotBlank(userName)) {
            sql.append(" and u.userName like :USERNAME");
            sqlMap.put("USERNAME", "%" + userName + "%");
        }

        if (StringUtils.isNotBlank(roleOid)) {
            sql.append(" and u.iwoid in (select sa.sysUser.iwoid from SysAuthority sa where sa.sysRole.iwoid = :ROLEOID)");
            sqlMap.put("ROLEOID", roleOid);
        }

        if (state != null) {
            sql.append(" and u.state = :STATE");
            sqlMap.put("STATE", state.intValue());
        }
        
        if (buildType != null) {
	        sql.append(" and u.buildType = :BUILDTYPE");
	        sqlMap.put("BUILDTYPE", buildType);
        }
        
        sql.append(" order by u.modifyTime desc");

        List<SysUserVO> resultList = new ArrayList<SysUserVO>();
        List<SysUser> userList = (List<SysUser>) commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex,
            maxResult);
        if (userList != null && !userList.isEmpty()) {
            for (SysUser sysUser : userList) {
                SysUserVO vo = new SysUserVO();
                
                BeanCopierUtil.copyProperties(sysUser, vo);

                // 用户角色列表
                String sqlStr = "select s.sysRole from SysAuthority s where s.sysUser.iwoid = :IWOID)";
                sqlMap.clear();
                sqlMap.put("IWOID", sysUser.getIwoid());
                List<SysRole> userRoleList = (List<SysRole>) commonDAO.findObjectList(sqlStr, sqlMap, false);
                vo.setUserRoleList(userRoleList);

                resultList.add(vo);
            }
        }

        return resultList;
    }
    
    @Override
    public int doJoinTransQuerySysUserCount(Map<String, Object> paramMap) {
        String userId = MapUtils.getString(paramMap, "userId");
        String userName = MapUtils.getString(paramMap, "userName");
        String roleOid = MapUtils.getString(paramMap, "roleOid");
        Integer state = MapUtils.getInteger(paramMap, "state");
        Integer buildType = MapUtils.getInteger(paramMap, "buildType");
        
        StringBuffer sql = new StringBuffer("select count(u.iwoid) from SysUser u where 1=1");

        Map<String, Object> sqlMap = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(userId)) {
            sql.append(" and u.userId like :USERID");
            sqlMap.put("USERID", "%" + userId + "%");
        }

        if (StringUtils.isNotBlank(userName)) {
            sql.append(" and u.userName like :USERNAME");
            sqlMap.put("USERNAME", "%" + userName + "%");
        }

        if (StringUtils.isNotBlank(roleOid)) {
            sql.append(" and u.iwoid in (select sa.sysUser.iwoid from SysAuthority sa where sa.sysRole.iwoid = :ROLEOID)");
            sqlMap.put("ROLEOID", roleOid);
        }

        if (state != null) {
            sql.append(" and u.state = :STATE");
            sqlMap.put("STATE", state.intValue());
        }
        
        if (buildType != null) {
	        sql.append(" and u.buildType = :BUILDTYPE");
	        sqlMap.put("BUILDTYPE", buildType);
        }
        
        return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<SysUserVO> doTransExportSysUserList(Map<String, Object> paramMap, String operatorUserOid, String logFunctionOid) {
        List<SysUserVO> resultList = new ArrayList<SysUserVO>();
        StringBuilder queryConditionStr = new StringBuilder("查询条件：");

        String userId = MapUtils.getString(paramMap, "userId");
        String userName = MapUtils.getString(paramMap, "userName");
        String roleOid = MapUtils.getString(paramMap, "roleOid");
        Integer state = MapUtils.getInteger(paramMap, "state");
        Integer buildType = MapUtils.getInteger(paramMap, "buildType");

        StringBuffer sql = new StringBuffer("select u from SysUser u where 1=1");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(userId)) {
            sql.append(" and u.userId like :USERID");
            sqlMap.put("USERID", "%" + userId + "%");
            queryConditionStr.append("[登录名：").append(userId).append("]");
        }
        
        if (StringUtils.isNotBlank(userName)) {
            sql.append(" and u.userName like :USERNAME");
            sqlMap.put("USERNAME", "%" + userName + "%");
            queryConditionStr.append("[真实姓名：").append(userName).append("]");
        }
        
        queryConditionStr.append("[角色：");
        if (StringUtils.isNotBlank(roleOid)) {
            sql.append(" and u.iwoid in (select sa.sysUser.iwoid from SysAuthority sa where sa.sysRole.iwoid = :ROLEOID)");
            sqlMap.put("ROLEOID", roleOid);
            SysRole sysRole = commonDAO.findObject(SysRole.class, roleOid);
            queryConditionStr.append(sysRole.getRoleName()).append("]");
        } else {
            queryConditionStr.append("全部").append("]");
        }
        
        queryConditionStr.append("[状态：");
        if (state != null) {
            sql.append(" and u.state =:STATE");
            sqlMap.put("STATE", state.intValue());
            

            if (SysRole.State.normal.getValue() == state) {
                queryConditionStr.append("正常");
            } else if (SysRole.State.frozen.getValue() == state) {
                queryConditionStr.append("冻结");
            } else if (SysRole.State.canceled.getValue() == state) {
                queryConditionStr.append("注销");
            }

            queryConditionStr.append("]");
        } else {
            queryConditionStr.append("全部").append("]");
        }
        
        if (buildType != null) {
	        sql.append(" and u.buildType = :BUILDTYPE");
	        sqlMap.put("BUILDTYPE", buildType);
        }
        
        sql.append(" order by u.modifyTime desc");
        
        Date processBeginTime = new Date();

        List<SysUser> userList = (List<SysUser>) commonDAO.findObjectList(sql.toString(), sqlMap, false);
        if (userList != null && !userList.isEmpty()) {
            for (SysUser sysUser : userList) {
                SysUserVO vo = new SysUserVO();
                
                BeanCopierUtil.copyProperties(sysUser, vo);
                
                // 用户角色列表
                String sqlStr = "select s.sysRole from SysAuthority s where s.sysUser.iwoid= :IWOID)";
                sqlMap.clear();
                sqlMap.put("IWOID", sysUser.getIwoid());
                List<SysRole> userRoleList = (List<SysRole>) commonDAO.findObjectList(sqlStr, sqlMap, false);
                vo.setUserRoleList(userRoleList);

                resultList.add(vo);
            }
        }
        
        Date processEndTime = new Date();

        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, queryConditionStr.toString(),
            processBeginTime, processEndTime, null, null, SysLog.State.success.getValue(), null, logFunctionOid,
            SysLog.ActionType.export.getValue());

        return resultList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SysUserVO doJoinTransQueryUserByOid(String userOid) {
        Validator.checkArgument(StringUtils.isBlank(userOid), "用户Oid不能为空");

        SysUser sysUser = commonDAO.findObject(SysUser.class, userOid);
        SysUserVO sysUserVO = null;

        if (sysUser != null) {
        	sysUserVO = new SysUserVO();
        	BeanCopierUtil.copyProperties(sysUser, sysUserVO);
        	
            // 用户角色列表
            String sql = "select s.sysRole from SysAuthority s where s.sysUser.iwoid= :IWOID";

            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("IWOID", userOid);

            List<SysRole> userRoleList = (List<SysRole>) commonDAO.findObjectList(sql, paramMap, false);
            sysUserVO.setUserRoleList(userRoleList);
        }

        return sysUserVO;
    }

    @Override
    public void doTransResetUserPwd(String userOid, String newPwd, String operatorOid, String operatorName,
            String logFunctionOid) {
        Validator.checkArgument(StringUtils.isBlank(userOid), "用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(newPwd), "重置后的新密码不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorOid), "执行重置操作的用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorName), "执行重置操作的用户名称 不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志功能项Oid不能为空");

        SysUser user = commonDAO.findObject(SysUser.class, userOid);

        if (user == null) {
            throw new IllegalArgumentException("未找到要重置的用户信息");
        }

        String oldPwdStr = user.getLoginPwd();

        Date currentTime = new Date();
        user.setLoginPwd(DigestHelper.md5Hex(newPwd));
        user.setModifier(operatorName);
        user.setModifyTime(currentTime);
        commonDAO.update(user);
        
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorOid, "用户ID=" + user.getUserId(),
            currentTime, new Date(), oldPwdStr, user.getLoginPwd(), SysLog.State.success.getValue(), userOid,
            logFunctionOid, SysLog.ActionType.resetPwd.getValue());
    }

    @Override
    public SysUserVO doTransAddSysUser(SysUser sysUser, List<String> roleOidList, String creator, String operatorUserOid, String logFunctionOid) 
            throws AlreadyExistsException {
        Validator.checkArgument(sysUser == null, "系统管理用户对象不能为空");
        Validator.checkArgument(roleOidList == null || roleOidList.isEmpty(), "用户角色信息不能为空");
        Validator.checkArgument(StringUtils.isBlank(creator), "创建人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        Validator.checkArgument(!Validator.contains(SysUser.DataPermisionType.class, sysUser.getDataPermisionType()) , "数据权限范围取值不正确");
        
        String sql = "select count(u.iwoid) from SysUser u where u.userId = :USERID and u.state <> :CANCELSTATE ";

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("USERID", sysUser.getUserId());
        paramMap.put("CANCELSTATE", SysUser.State.canceled.getValue());

        int idResult = commonDAO.queryObjectCount(sql, paramMap, false);

        if (idResult > 0) {
            throw new AlreadyExistsException("登录名不允许重复！");
        }

        sysUser.setBuildType(SysUser.BuildType.create.getValue());
        sysUser.setCreator(creator);
        sysUser.setIwoid(Generator.generateIwoid());
        sysUser.setLastLoginTime(null);
        sysUser.setLoginPwd(DigestHelper.md5Hex(sysUser.getLoginPwd()));
        sysUser.setModifier(creator);
        sysUser.setModifyTime(new Date());
        
        commonDAO.save(sysUser, false);

        /******** 处理用户角色赋权 ********/
        List<SysRole> sysRoleList = new ArrayList<SysRole>();

        for (String sysRoleOid : roleOidList) {
            SysRole sysRole = commonDAO.findObject(SysRole.class, sysRoleOid);

            if (sysRole == null) {
                throw new IllegalArgumentException("未找到角色信息");
            } else {
                SysAuthority sysAuthority = new SysAuthority();
                sysAuthority.setIwoid(Generator.generateIwoid());
                sysAuthority.setSysRole(sysRole);
                sysAuthority.setSysUser(sysUser);

                commonDAO.save(sysAuthority, false);

                sysRole.setUseState(SysRole.UseState.used.getValue());
                commonDAO.update(sysRole);

                sysRoleList.add(sysRole);
            }
        }

        SysUserVO sysUserVo = new SysUserVO();

        BeanCopierUtil.copyProperties(sysUser, sysUserVo);
        sysUserVo.setUserRoleList(sysRoleList);

        Date processTime = new Date();

        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid,
            "创建用户[用户ID=" + sysUser.getUserId() + ", 用户名称=" + sysUser.getUserName() + "]", processTime, processTime,
            null, sysUser.toString(), SysLog.State.success.getValue(), sysUser.getIwoid(), logFunctionOid,
            SysLog.ActionType.create.getValue());

        return sysUserVo;
    }

    @Override
    public SysUserVO doTransUpdateSysUser(SysUser sysUser, List<String> roleOidList, String modifier, String operatorUserOid, 
            String logFunctionOid) throws AlreadyExistsException {
        Validator.checkArgument(sysUser == null, "系统管理用户对象不能为空");
        Validator.checkArgument(roleOidList == null || roleOidList.isEmpty(), "用户角色信息不能为空");
        Validator.checkArgument(StringUtils.isBlank(modifier), "修改人不能为空");
        Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        
        String sql = "select u from SysUser u where u.iwoid = :USEROID and u.state <> :STATE";
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("USEROID", sysUser.getIwoid());
        paramMap.put("STATE", SysUser.State.canceled.getValue());

        SysUser oldSysUser = commonDAO.findObject(sql, paramMap, false);

        if (oldSysUser == null) {
            throw new IllegalArgumentException("未找到要修改的系统管理用户对象");
        }

        sql = "select count(u.iwoid) from SysUser u where u.userId = :USERID and u.state <> :CANCELSTATE and u.iwoid <> :USEROID";

        paramMap.clear();
        paramMap.put("USERID", sysUser.getUserId());
        paramMap.put("CANCELSTATE", SysUser.State.canceled.getValue());
        paramMap.put("USEROID", sysUser.getIwoid());

        int idResult = commonDAO.queryObjectCount(sql, paramMap, false);

        if (idResult > 0) {
            throw new AlreadyExistsException("登录名不允许重复！");
        }

        String oldSysUserStr = oldSysUser.toString();

        oldSysUser.setAge(sysUser.getAge());
        oldSysUser.setDepartment(sysUser.getDepartment());
        oldSysUser.setEmail(sysUser.getEmail());
        oldSysUser.setGender(sysUser.getGender());
        oldSysUser.setLineTel(sysUser.getLineTel());
        oldSysUser.setModifier(modifier);
        oldSysUser.setPosition(sysUser.getPosition());
        oldSysUser.setState(sysUser.getState());
        oldSysUser.setRemark(sysUser.getRemark());
        //如果用户没用登陆过本系统，则可以修改userId
        if (oldSysUser.getLastLoginTime() == null) {
            oldSysUser.setUserId(sysUser.getUserId());
        }

        oldSysUser.setUserName(sysUser.getUserName());

        oldSysUser.setDataPermisionType(sysUser.getDataPermisionType());
        commonDAO.update(oldSysUser);

        String newSysUserStr = oldSysUser.toString();

        sql = "delete from SysAuthority sa where sa.sysUser.iwoid = :USEROID";

        paramMap.clear();
        paramMap.put("USEROID", oldSysUser.getIwoid());

        commonDAO.executeBatch(sql, paramMap, false);

        /******** 处理用户角色新赋权 ********/
        List<SysRole> newSysRoleList = new ArrayList<SysRole>();

        for (String sysRoleOid : roleOidList) {
            SysRole sysRole = commonDAO.findObject(SysRole.class, sysRoleOid);

            if (sysRole == null) {
                throw new IllegalArgumentException("未找到角色信息");
            } else {
                SysAuthority sysAuthority = new SysAuthority();
                sysAuthority.setIwoid(Generator.generateIwoid());
                sysAuthority.setSysRole(sysRole);
                sysAuthority.setSysUser(oldSysUser);

                commonDAO.save(sysAuthority, false);

                sysRole.setUseState(SysRole.UseState.used.getValue());
                commonDAO.update(sysRole);

                newSysRoleList.add(sysRole);
            }
        }

        SysUserVO sysUserVo = new SysUserVO();

        BeanCopierUtil.copyProperties(oldSysUser, sysUserVo);
        sysUserVo.setUserRoleList(newSysRoleList);

        Date processTime = new Date();

        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid,
            "修改用户[用户ID=" + oldSysUser.getUserId() + ", 用户名称=" + oldSysUser.getUserName() + "]", processTime,
            processTime, oldSysUserStr, newSysUserStr, SysLog.State.success.getValue(), sysUser.getIwoid(),
            logFunctionOid, SysLog.ActionType.modify.getValue());

        return sysUserVo;
    }

    @Override
    public SysUser doJoinTransIsUserFunction(String userId, String password, String functionOid) throws IllegalStateException, 
            IllegalAccessException, NotExistsException {
        Validator.checkArgument(StringUtils.isBlank(userId), "用户标识不能为空");
        Validator.checkArgument(StringUtils.isBlank(password), "登录密码不能为空");
        Validator.checkArgument(StringUtils.isBlank(functionOid), "功能项不能为空");

        String sql = "select u from SysUser u where u.userId = :USERID and u.loginPwd = :LOGINPWD and u.state <> :DELETESTATE";

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("USERID", userId);
        paramMap.put("LOGINPWD", DigestHelper.md5Hex(password));
        paramMap.put("DELETESTATE", SysUser.State.canceled.getValue());

        SysUser sysUser = commonDAO.findObject(sql, paramMap, false);

        SysUser result = null;

        if (sysUser == null || sysUser.getState() == SysUser.State.canceled.getValue()) {
            throw new IllegalAccessException("用户名或密码错误");
        } else if (sysUser.getState() == SysUser.State.frozen.getValue()) {
            throw new IllegalStateException("用户：" + sysUser.getUserId() + "已冻结，暂时不允许审核！");
        }

        sql = "select count(p.iwoid) from SysPermission p where p.sysFunction.iwoid = :FUNCTIONOID and p.sysRole.iwoid in "
            + "(select a.sysRole.iwoid from SysAuthority a where a.sysUser.iwoid = :USEROID)";

        paramMap.clear();
        paramMap.put("FUNCTIONOID", functionOid);
        paramMap.put("USEROID", sysUser.getIwoid());

        int permissionResult = commonDAO.queryObjectCount(sql, paramMap, false);

        if (permissionResult > 0) {
            result = sysUser;
        } else {
            throw new NotExistsException("用户：" + sysUser.getUserId() + "不具备审核权限！");
        }

        return result;
    }

    @Override
    public boolean doTransRefreshLoginToken(String sysUserOid, String loginToken) {
        String sql = "select t from SysUserLoginToken t where t.sysUserOid = :USEROID and t.token = :LOGINTOKEN";

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("USEROID", sysUserOid);
        paramMap.put("LOGINTOKEN", loginToken);

        SysUserLoginToken sysUserLoginToken = (SysUserLoginToken) commonDAO.findObject(sql, paramMap, false);
        
        boolean result = false;
        
        if (sysUserLoginToken != null) {
            sysUserLoginToken.setExpireTime(DateTime.now().plusMinutes(10).toDate());
            commonDAO.update(sysUserLoginToken);
            result = true;
        }
        
        return result;
    }
    
	@Override
	public UserLoginResponse doTransUserLogin4Client(String loginId, String password) throws IllegalArgumentException {
		Validator.checkArgument(StringUtils.isBlank(loginId), "loginId不能为空");
        Validator.checkArgument(StringUtils.isBlank(password), "登录密码不能为空");
        
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("LOGINPWD", DigestHelper.md5Hex(password));
        paramMap.put("DELETESTATE", SysUser.State.canceled.getValue());
        paramMap.put("SHOPMANAGER", SysUser.UserLevel.shopManager.getValue());
        paramMap.put("CASHIER", SysUser.UserLevel.cashier.getValue());
        
        SysUser sysUser = null;
        if (loginId.matches("\\d{10}")){// 按收银员ID查询
            String sql = "select u from SysUser u left join fetch u.dealerEmployee where u.dealerEmployee.dealerEmployeeId = :DEALEREMPLOYEEID and u.loginPwd = :LOGINPWD"
                + " and u.state <> :DELETESTATE and u.userLevel in (:SHOPMANAGER, :CASHIER)";
            paramMap.put("DEALEREMPLOYEEID", loginId);
            sysUser = commonDAO.findObject(sql, paramMap, false);
        }
        if (sysUser == null) {
            String sql = "select u from SysUser u left join fetch u.dealerEmployee where u.userId = :USERID and u.loginPwd = :LOGINPWD"
                + " and u.state <> :DELETESTATE and u.userLevel in (:SHOPMANAGER, :CASHIER)";
            paramMap.remove("DEALEREMPLOYEEID");
            paramMap.put("USERID", loginId);
            sysUser = commonDAO.findObject(sql, paramMap, false);
        }
        
        DealerEmployee dealerE = null;
        
        UserLoginResponse response = null;
        String responseId = Generator.generateIwoid();
        if (sysUser == null) {
            logger.warn("loginid( {} )或密码不正确！", loginId);
            response = new UserLoginResponse(LoginResult.LOGIN_ID_PASSWD_FAIL.getCode(), LoginResult.LOGIN_ID_PASSWD_FAIL.getDesc(), responseId);
        } else if (sysUser.getState() == SysUser.State.frozen.getValue()) {
            logger.warn("该用户( {} )已冻结，不允许登录！", sysUser.getUserId());
            response = new UserLoginResponse(LoginResult.USER_FROZEN.getCode(), LoginResult.USER_FROZEN.getDesc(), responseId);
        } else {
            dealerE = sysUser.getDealerEmployee(); 
            if (dealerE == null || StringUtils.isBlank(dealerE.getIwoid())) {
                logger.warn("收银员，login：( {} )不存在！", loginId);
                response = new UserLoginResponse(CommonResult.DATA_NOT_EXIST.getCode(), "收银员信息" + CommonResult.DATA_NOT_EXIST.getDesc(), responseId);
            } else {
                Store store = sysUser.getDealerEmployee().getStore();
                
                response = new UserLoginResponse(CommonResult.SUCCESS.getCode(), CommonResult.SUCCESS.getDesc(), responseId);
                
                response.setDealerCompany(dealerE.getDealer() == null ? "" : dealerE.getDealer().getCompany());
                response.setStoreName(store == null ? "" : store.getStoreName());
                response.setDealerEmployeeName(dealerE.getEmployeeName());
                response.setDealerEmployeeId(dealerE.getDealerEmployeeId());
                response.setDealerEmployeeOid(dealerE.getIwoid());
            }
        }
        
        return response;
	}

    public SysLogService getSysLogService() {
        return sysLogService;
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

}
