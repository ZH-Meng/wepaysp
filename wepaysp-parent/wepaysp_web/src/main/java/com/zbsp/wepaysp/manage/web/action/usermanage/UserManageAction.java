/*
 * UserManageAction.java
 * 创建者：马宗旺
 * 创建日期：2015年6月13日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.action.usermanage;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;
import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.security.DigestHelper;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.manage.web.util.ExcelUtil;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.api.service.manage.SysRoleService;
import com.zbsp.wepaysp.api.service.manage.SysUserService;
import com.zbsp.wepaysp.vo.manage.SysUserVO;

/**
 * @author mazw
 */
public class UserManageAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = 1542152641844307005L;

    private Map<String, Object> session;

    private List<SysUserVO> userVoList;

    private List<SysRole> roleList;

    private SysUser sysUser;

    private String roleOid;

    private SysUserVO sysUserVo;

    private String provinceIwoid;

    private String downFileName;

    private SysUserService sysUserService;
    private SysRoleService sysRoleService;

    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        try {
            paramMap.put("buildType", SysUser.BuildType.create.getValue());

            userVoList = sysUserService.doJoinTransQuerySysUserList(paramMap, start, size);
            rowCount = sysUserService.doJoinTransQuerySysUserCount(paramMap);

        } catch (Exception e) {
            logger.error("用户权限管理查询列表错误：" + e.getMessage());
            setAlertMessage("用户权限管理查询列表错误：" + e.getMessage());
        }

        return "userList";
    }

    public String list() {
        initPageData(100);
        return goCurrent();
    }

    public String goToCreateUser() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        List<Integer> roleStateList = new ArrayList<Integer>();
        roleStateList.add(SysRole.State.normal.getValue());
        roleStateList.add(SysRole.State.frozen.getValue());
        paramMap.put("stateList", roleStateList);
        paramMap.put("buildType", SysUser.BuildType.create.getValue());

        try {
            roleList = sysRoleService.doJoinTransQuerySysRoleList(paramMap, 0, -1);
        } catch (Exception e) {
            logger.error("用户权限管理跳转创建用户错误：" + e.getMessage());
            setAlertMessage("用户权限管理跳转创建用户错误：" + e.getMessage());
        }

        return "createUser";
    }

    public String createUser() {
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<String> roleOidList = new ArrayList<String>();
        roleOidList.add(roleOid);

        sysUser.setLoginPwd(DigestHelper.sha512HexUnicode(sysUser.getLoginPwd()));

        try {
            sysUserVo = sysUserService.doTransAddSysUser(sysUser, roleOidList, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));

            setAlertMessage("新增用户：" + sysUserVo.getUserId() + "成功！");
            sysUser = null;
        } catch (AlreadyExistsException e) {
            logger.warn("用户权限管理创建用户失败" + e.getMessage());
            setAlertMessage("用户权限管理创建用户失败！");
        } catch (Exception e) {
            logger.error("用户权限管理创建用户错误：" + e.getMessage());
            setAlertMessage("用户权限管理创建用户错误！");
        }

        return goToCreateUser();
    }

    public String goToUpdateUser() {
        Map<String, Object> processData = new HashMap<String, Object>();
        processData.put("userOid", sysUser.getIwoid());

        try {
            sysUserVo = sysUserService.doJoinTransQueryUserByOid(sysUser.getIwoid());

            Map<String, Object> paramMap = new HashMap<String, Object>();
            List<Integer> roleStateList = new ArrayList<Integer>();
            roleStateList.add(SysRole.State.normal.getValue());
            roleStateList.add(SysRole.State.frozen.getValue());
            paramMap.put("stateList", roleStateList);
            paramMap.put("buildType", SysUser.BuildType.create.getValue());

            roleList = sysRoleService.doJoinTransQuerySysRoleList(paramMap, 0, -1);

            roleOid = sysUserVo.getUserRoleList().get(0).getIwoid();
        } catch (Exception e) {
            logger.error("用户权限管理跳转修改用户错误：" + e.getMessage());
            setAlertMessage("用户权限管理跳转修改用户错误！");
        }

        return "updateUser";
    }

    public String updateUser() {
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        sysUser = new SysUser();
        BeanCopierUtil.copyProperties(sysUserVo, sysUser);

        List<String> roleOidList = new ArrayList<String>();
        roleOidList.add(roleOid);

        try {
            sysUserVo = sysUserService.doTransUpdateSysUser(sysUser, roleOidList, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));

            setAlertMessage("修改用户：" + sysUserVo.getUserId() + "成功！");
            return list();
        } catch (AlreadyExistsException e) {
            logger.warn("用户权限管理修改用户失败：" + e.getMessage());
            setAlertMessage("用户权限管理修改用户失败！");
        } catch (Exception e) {
            logger.error("用户权限管理修改用户错误：" + e.getMessage());
            setAlertMessage("用户权限管理修改用户错误！");
        }

        return goToUpdateUser();
    }

    public String exportUser() {
        return SUCCESS;
    }

    public InputStream getDownFile() {
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        InputStream inputStream = null;
        Map<String, Object> paramMap = new HashMap<String, Object>();

        try {
            paramMap.put("buildType", SysUser.BuildType.create.getValue());

            userVoList = sysUserService.doTransExportSysUserList(paramMap, manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));

            List<SysUserVO> sysUserVOList = new ArrayList<SysUserVO>();
            for (SysUserVO sysUser : userVoList) {

                if (null != sysUser.getGender()) {
                    if (0 == sysUser.getGender()) {
                        sysUser.setGenderName("男");
                    } else if (1 == sysUser.getGender()) {
                        sysUser.setGenderName("女");
                    }
                }

                if (sysUser.getState() == 0) {
                    sysUser.setStateName("正常");
                } else if (sysUser.getState() == 1) {
                    sysUser.setStateName("冻结");
                } else if (sysUser.getState() == 2) {
                    sysUser.setStateName("注销");
                }

                sysUser.setRoleName(sysUser.getUserRoleList().get(0).getRoleName());

                sysUserVOList.add(sysUser);
            }

            String path = UserManageAction.class.getResource("userListExport.xlsx").getPath();
            ExcelUtil excelUtil = new ExcelUtil();
            inputStream = excelUtil.writeData(sysUserVOList, path, null, false);

            String fileName = "用户权限管理-" + DateUtil.getDate(new Date(), "yyyyMMdd") + ".xlsx";
            downFileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("用户权限管理导出列表错误：" + e.getMessage());
        }
        return inputStream;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public Map<String, Object> getSession() {
        return session;
    }

    public List<SysUserVO> getUserVoList() {
        return userVoList;
    }

    public void setUserVoList(List<SysUserVO> userVoList) {
        this.userVoList = userVoList;
    }

    public List<SysRole> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<SysRole> roleList) {
        this.roleList = roleList;
    }

    public SysUser getSysUser() {
        return sysUser;
    }

    public void setSysUser(SysUser sysUser) {
        this.sysUser = sysUser;
    }

    public String getRoleOid() {
        return roleOid;
    }

    public void setRoleOid(String roleOid) {
        this.roleOid = roleOid;
    }

    public SysUserVO getSysUserVo() {
        return sysUserVo;
    }

    public void setSysUserVo(SysUserVO sysUserVo) {
        this.sysUserVo = sysUserVo;
    }

    public String getProvinceIwoid() {
        return provinceIwoid;
    }

    public void setProvinceIwoid(String provinceIwoid) {
        this.provinceIwoid = provinceIwoid;
    }

    public String getDownFileName() {
        return downFileName;
    }

    public void setDownFileName(String downFileName) {
        this.downFileName = downFileName;
    }

    public void setSysUserService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    public void setSysRoleService(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

}
