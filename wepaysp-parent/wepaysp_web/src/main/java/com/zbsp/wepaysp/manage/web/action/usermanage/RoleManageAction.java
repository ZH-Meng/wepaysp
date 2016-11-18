/*
 * RoleManageAction.java
 * 创建者：杨帆
 * 创建日期：2015年6月11日
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
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.manage.web.util.ExcelUtil;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.api.service.manage.SysRoleService;
import com.zbsp.wepaysp.vo.manage.SysRoleVO;

/**
 * 角色管理Action
 * 
 * @author 杨帆
 */
public class RoleManageAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = 7071039875122268932L;

    private List<SysRole> sysRoleList;

    private Map<String, Object> session;

    private SysRole sysRole;

    private String downFileName;

    private SysRoleService sysRoleService;

    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("buildType", SysUser.BuildType.create.getValue());
        try {
            sysRoleList = sysRoleService.doJoinTransQuerySysRoleList(paramMap, start, size);
            rowCount = sysRoleService.doJoinTransQuerySysRoleCount(paramMap);

        } catch (Exception e) {
            logger.error("角色管理查询列表错误：" + e.getMessage());
            setAlertMessage("角色管理查询列表错误：" + e.getMessage());
        }
        return "roleManage";
    }

    public String list() {
        initPageData(100);
        return goCurrent();
    }

    public String goToCreateRole() {
        return "createRole";
    }

    public String goToUpdateRole() {
        try {
            sysRole = sysRoleService.doJoinTransQuerySysRoleByOid(sysRole.getIwoid());

        } catch (Exception e) {
            logger.error("角色管理修改角色加载详情处理错误：" + e.getMessage());
            setAlertMessage("角色管理修改角色加载详情处理错误：" + e.getMessage());
        }

        return "updateRole";
    }

    public String createRole() {
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            sysRoleService.doTransAddRole(sysRole, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
            setAlertMessage("新增角色：" + sysRole.getRoleName() + "成功！");
            sysRole = null;
        } catch (AlreadyExistsException e) {
            logger.warn("角色管理创建角色失败", e.getMessage());
            setAlertMessage("角色管理创建角色失败：" + e.getMessage());
        } catch (Exception e) {
            logger.error("角色管理创建角色错误：" + e.getMessage());
            setAlertMessage("角色管理创建角色错误：" + e.getMessage());
        }
        return "createRole";
    }

    public String updateRole() {
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            sysRoleService.doTransUpdateRole(sysRole, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
            setAlertMessage("修改角色：" + sysRole.getRoleName() + "成功！");

            return list();
        } catch (AlreadyExistsException e) {
            logger.error("角色管理修改角色失败：" + e.getMessage());
            setAlertMessage("角色管理修改角色失败：" + e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("角色管理修改角色失败：" + e.getMessage());
            setAlertMessage("角色管理修改角色失败：" + e.getMessage());
        } catch (Exception e) {
            logger.error("角色管理修改角色错误：" + e.getMessage());
            setAlertMessage("角色管理修改角色错误：" + e.getMessage());
        }

        return "updateRole";
    }

    public String exportFile() {
        return SUCCESS;
    }

    public InputStream getDownFile() {
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        InputStream inputStream = null;
        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("buildType", SysUser.BuildType.create.getValue());

        try {
            sysRoleList = sysRoleService.doTransExportSysRoleList(paramMap, manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));

            List<SysRoleVO> sysRoleVoList = new ArrayList<SysRoleVO>();

            for (SysRole sysRole : sysRoleList) {
                SysRoleVO sysRoleVo = new SysRoleVO();
                sysRoleVo.setIwoid(sysRole.getIwoid());
                sysRoleVo.setRoleId(sysRole.getRoleId());

                if (sysRole.getRoleLevel() == 0) {
                    sysRoleVo.setRoleLevelName("应用级别");
                } else if (sysRole.getRoleLevel() == 1) {
                    sysRoleVo.setRoleLevelName("管理级别");
                }
                sysRoleVo.setDescription(sysRole.getDescription());
                sysRoleVo.setRoleName(sysRole.getRoleName());
                sysRoleVo.setModifier(sysRole.getModifier());
                sysRoleVo.setModifyTime(sysRole.getModifyTime());

                if (sysRole.getState() == 0) {
                    sysRoleVo.setStateName("正常");
                } else if (sysRole.getState() == 1) {
                    sysRoleVo.setStateName("冻结");
                } else if (sysRole.getState() == 2) {
                    sysRoleVo.setStateName("注销");
                }
                sysRoleVo.setRemark(sysRole.getRemark());
                sysRoleVo.setRoleIndex(sysRole.getRoleIndex());
                sysRoleVoList.add(sysRoleVo);
            }

            String path = RoleManageAction.class.getResource("roleListExport.xlsx").getPath();
            ExcelUtil excelUtil = new ExcelUtil();
            inputStream = excelUtil.writeData(sysRoleVoList, path, null, false);

            String fileName = "角色管理-" + DateUtil.getDate(new Date(), "yyyyMMdd") + ".xlsx";
            downFileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("角色管理导出列表错误：" + e.getMessage());
        }

        return inputStream;
    }

    public List<SysRole> getSysRoleList() {
        return sysRoleList;
    }

    public void setSysRoleList(List<SysRole> sysRoleList) {
        this.sysRoleList = sysRoleList;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public SysRole getSysRole() {
        return sysRole;
    }

    public void setSysRole(SysRole sysRole) {
        this.sysRole = sysRole;
    }

    public String getDownFileName() {
        return downFileName;
    }

    public void setDownFileName(String downFileName) {
        this.downFileName = downFileName;
    }

    public void setSysRoleService(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

}
