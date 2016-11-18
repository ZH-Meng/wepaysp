/*
 * UserRestPwdAction.java
 * 创建者：马宗旺
 * 创建日期：2015年6月13日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.action.usermanage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.security.DigestHelper;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.api.service.manage.SysRoleService;
import com.zbsp.wepaysp.api.service.manage.SysUserService;
import com.zbsp.wepaysp.vo.manage.SysUserVO;

/**
 * @author mazw
 */
public class UserRestPwdAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = 1458294617421845346L;

    private Map<String, Object> session;

    private List<SysUserVO> userVoList;

    private List<SysRole> sysRoleAll;

    private String userId;

    private String userName;

    private String roleOid;

    private String state;

    private SysUserVO sysUser;

    private SysUserService sysUserService;
    private SysRoleService sysRoleService;

    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", StringUtils.isBlank(userId) ? null : userId.trim());
        paramMap.put("userName", StringUtils.isBlank(userName) ? null : userName.trim());
        paramMap.put("roleOid", roleOid);
        paramMap.put("state", state);

        try {
            paramMap.put("buildType", SysUser.BuildType.create.getValue());

            userVoList = sysUserService.doJoinTransQuerySysUserList(paramMap, start, size);
            rowCount = sysUserService.doJoinTransQuerySysUserCount(paramMap);

            List<Integer> roleStateList = new ArrayList<Integer>();
            roleStateList.add(SysRole.State.normal.getValue());
            roleStateList.add(SysRole.State.frozen.getValue());

            paramMap.clear();
            paramMap.put("stateList", roleStateList);
            paramMap.put("buildType", SysUser.BuildType.create.getValue());

            sysRoleAll = sysRoleService.doJoinTransQuerySysRoleList(paramMap, 0, -1);
        } catch (Exception e) {
            logger.error("重置用户密码查询列表错误：" + e.getMessage());
            setAlertMessage("重置用户密码查询列表错误！");
        }

        return "userList";
    }

    public String list() {
        initPageData(100);
        return goCurrent();
    }

    public String toRestUserPwd() {
        try {
            sysUser = sysUserService.doJoinTransQueryUserByOid(sysUser.getIwoid());
        } catch (Exception e) {
            logger.error("重置用户密码查询用户信息错误：" + e.getMessage());
            setAlertMessage("重置用户密码查询用户信息错误！");
        }

        return "toRestUserPwd";
    }

    public String restUserPwd() {
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            sysUserService.doTransResetUserPwd(sysUser.getIwoid(), DigestHelper.sha512Hex(sysUser.getLoginPwd()), manageUser.getIwoid(), manageUser.getUserId(), (String) session.get("currentLogFunctionOid"));

            setAlertMessage("重置密码成功！");
        } catch (Exception e) {
            logger.error("重置用户密码错误：" + e.getMessage());
            setAlertMessage("重置用户密码错误！");
        }
        return list();
    }

    public List<SysUserVO> getUserVoList() {
        return userVoList;
    }

    public void setUserVoList(List<SysUserVO> userVoList) {
        this.userVoList = userVoList;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoleOid() {
        return roleOid;
    }

    public void setRoleOid(String roleOid) {
        this.roleOid = roleOid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    public List<SysRole> getSysRoleAll() {
        return sysRoleAll;
    }

    public void setSysRoleAll(List<SysRole> sysRoleAll) {
        this.sysRoleAll = sysRoleAll;
    }

    public SysUserVO getSysUser() {
        return sysUser;
    }

    public void setSysUser(SysUserVO sysUser) {
        this.sysUser = sysUser;
    }

    public void setSysUserService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    public void setSysRoleService(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

}
