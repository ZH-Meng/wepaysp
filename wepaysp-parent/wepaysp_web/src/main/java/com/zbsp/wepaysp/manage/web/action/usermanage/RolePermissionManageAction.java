/*
 * RolePermissionManageAction.java
 * 创建者：杨帆
 * 创建日期：2015年7月1日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.action.usermanage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.manage.web.action.BaseAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.manage.web.vo.TreeNode;
import com.zbsp.wepaysp.po.manage.SysFunction;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.api.service.manage.SysFunctionService;
import com.zbsp.wepaysp.api.service.manage.SysPermissionService;
import com.zbsp.wepaysp.api.service.manage.SysRoleService;

/**
 * 角色权限管理Action
 * 
 * @author 杨帆
 */
public class RolePermissionManageAction
    extends BaseAction
    implements SessionAware {

    private static final long serialVersionUID = 3562692242978207182L;
    // 角色信息列表
    private List<SysRole> sysRoleList;
    // 全部菜单信息
    private String functionStr;
    // 角色菜单信息
    private String roleFunctionStr;
    // 选中的角色Oid
    private String sysRoleOid;
    // 已选择的菜单ID列表
    private String[] nodeId;
    // 角色名称
    private String sysRoleName;

    private Map<String, Object> session;

    private SysPermissionService sysPermissionService;
    private SysRoleService sysRoleService;
    private SysFunctionService sysFunctionService;

    @Override
    public String execute()
        throws Exception {
        try {
            Map<String, Object> queryRoleMap = new HashMap<String, Object>();
            queryRoleMap.put("state", SysRole.State.normal.getValue());
            queryRoleMap.put("buildType", SysUser.BuildType.create.getValue());

            sysRoleList = sysRoleService.doJoinTransQuerySysRoleList(queryRoleMap, 0, -1);

            if (StringUtils.isNotBlank(sysRoleOid)) {
                SysRole sysRole = sysRoleService.doJoinTransQuerySysRoleByOid(sysRoleOid);

                Map<String, Object> queryFunctionMap = new HashMap<String, Object>();
                queryFunctionMap.put("state", SysFunction.State.normal.getValue());

                if (SysRole.Level.normal.getValue() == sysRole.getRoleLevel()) {
                    queryFunctionMap.put("functionLevel", sysRole.getRoleLevel());
                }

                List<SysFunction> sysFunctionList = sysFunctionService.doJoinTransQuerySysFunctionList(queryFunctionMap);

                Map<String, Object> queryRoleFunctionMap = new HashMap<String, Object>();
                queryRoleFunctionMap.put("roleOid", sysRoleOid);
                queryRoleFunctionMap.put("functionState", SysFunction.State.normal.getValue());

                List<SysFunction> roleFunctionList = sysPermissionService.doJoinTransQueryRoleFunctionList(queryRoleFunctionMap);

                Collections.sort(sysRoleList);

                if (sysRole != null) {
                    sysRoleName = sysRole.getRoleName();
                }

                List<TreeNode> treeNodeList = new ArrayList<TreeNode>();

                if (sysFunctionList != null && !sysFunctionList.isEmpty()) {
                    for (SysFunction sysFunction : sysFunctionList) {
                        TreeNode treeNode = new TreeNode();
                        treeNode.setFatherId(sysFunction.getParentFunctionOid());
                        treeNode.setNodeId(sysFunction.getIwoid());
                        treeNode.setNodeName(sysFunction.getAliasName());
                        treeNode.setNodeOrder(sysFunction.getDisplayOrder());

                        treeNodeList.add(treeNode);
                    }

                    Map<String, List<TreeNode>> treeNodeMap = new HashMap<String, List<TreeNode>>();
                    treeNodeMap.put("userNode", treeNodeList);

                    functionStr = JSONUtil.toJSONString(treeNodeMap, true);
                }

                if (roleFunctionList != null && !roleFunctionList.isEmpty()) {
                    treeNodeList.clear();

                    for (SysFunction sysFunction : roleFunctionList) {
                        TreeNode treeNode = new TreeNode();
                        treeNode.setFatherId(sysFunction.getParentFunctionOid());
                        treeNode.setNodeId(sysFunction.getIwoid());
                        treeNode.setNodeName(sysFunction.getAliasName());
                        treeNode.setNodeOrder(sysFunction.getDisplayOrder());

                        treeNodeList.add(treeNode);
                    }

                    Map<String, List<TreeNode>> treeNodeMap = new HashMap<String, List<TreeNode>>();
                    treeNodeMap.put("userNode", treeNodeList);

                    roleFunctionStr = JSONUtil.toJSONString(treeNodeMap, true);
                }
            }
        } catch (Exception e) {
            logger.error("角色权限管理查询错误：" + e.getMessage());
            setAlertMessage("角色权限管理查询错误：" + e.getMessage());
        }

        return "rolePermissionManage";
    }

    public String assignResource()
        throws Exception {
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            sysPermissionService.doTransAssignRoleFunction(sysRoleOid, Arrays.asList(nodeId), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));

            setAlertMessage("角色：" + sysRoleName + " 授权成功！");
        } catch (IllegalAccessException e) {
            logger.warn("角色权限管理赋权失败：" + e.getMessage());
            setAlertMessage("角色权限管理赋权失败：" + e.getMessage());
        } catch (Exception e) {
            logger.error("角色权限管理赋权错误：" + e.getMessage());
            setAlertMessage("角色权限管理赋权错误：" + e.getMessage());
        }

        return execute();
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public String getSysRoleName() {
        return sysRoleName;
    }

    public void setSysRoleName(String sysRoleName) {
        this.sysRoleName = sysRoleName;
    }

    public String getRoleFunctionStr() {
        return roleFunctionStr;
    }

    public void setRoleFunctionStr(String roleFunctionStr) {
        this.roleFunctionStr = roleFunctionStr;
    }

    public String getSysRoleOid() {
        return sysRoleOid;
    }

    public void setSysRoleOid(String sysRoleOid) {
        this.sysRoleOid = sysRoleOid;
    }

    public String[] getNodeId() {
        return nodeId;
    }

    public void setNodeId(String[] nodeId) {
        this.nodeId = nodeId;
    }

    public List<SysRole> getSysRoleList() {
        return sysRoleList;
    }

    public void setSysRoleList(List<SysRole> sysRoleList) {
        this.sysRoleList = sysRoleList;
    }

    public String getFunctionStr() {
        return functionStr;
    }

    public void setFunctionStr(String functionStr) {
        this.functionStr = functionStr;
    }

    public void setSysPermissionService(SysPermissionService sysPermissionService) {
        this.sysPermissionService = sysPermissionService;
    }

    public void setSysRoleService(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    public void setSysFunctionService(SysFunctionService sysFunctionService) {
        this.sysFunctionService = sysFunctionService;
    }

}
