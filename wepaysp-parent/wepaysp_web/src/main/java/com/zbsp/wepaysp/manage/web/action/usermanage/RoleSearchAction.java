package com.zbsp.wepaysp.manage.web.action.usermanage;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.manage.web.util.DateUtil;
import com.zbsp.wepaysp.manage.web.util.ExcelUtil;
import com.zbsp.wepaysp.manage.web.vo.TreeNode;
import com.zbsp.wepaysp.po.manage.SysFunction;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.service.manage.SysPermissionService;
import com.zbsp.wepaysp.service.manage.SysRoleService;
import com.zbsp.wepaysp.vo.manage.SysRoleVO;

public class RoleSearchAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = -5782558420007711150L;

    private List<SysRole> sysRoleList;
    private List<String> roleNameList;
    private Map<String, Object> session;

    private String roleOid;
    private String functionStr;

    private String roleName;
    private String roleNames;
    private Integer roleLevel;
    private Integer state;

    private String conditionRoleName;
    private String conditionRoleLevel;
    private String conditionRoleStatus;

    private String backRoleOid;

    private String downFileName;

    private SysRoleService sysRoleService;
    private SysPermissionService sysPermissionService;

    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("roleName", roleName);
        paramMap.put("roleLevel", roleLevel);
        paramMap.put("state", state);

        if (StringUtils.isNotBlank(roleName)) {
            paramMap.put("roleName", roleName);
        }

        if (roleLevel != null) {
            paramMap.put("roleLevel", roleLevel);
        }

        if (state != null) {
            paramMap.put("state", state);
        }

        paramMap.put("buildType", SysUser.BuildType.create.getValue());

        try {
            sysRoleList = sysRoleService.doJoinTransQuerySysRoleList(paramMap, start, size);
            rowCount = sysRoleService.doJoinTransQuerySysRoleCount(paramMap);

            paramMap.clear();
            paramMap.put("buildType", SysUser.BuildType.create.getValue());
            roleNameList = sysRoleService.doJoinTransQueryUniqueRoleNameList(paramMap);

        } catch (Exception e) {
            logger.error("角色权限查询列表错误：" + e.getMessage());
            setAlertMessage("角色权限查询列表错误：" + e.getMessage());
        }

        return "roleSearch";
    }

    public String exportFile() {
        return SUCCESS;
    }

    public InputStream getDownFile() {
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        InputStream inputStream = null;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("roleName", roleName);
        paramMap.put("roleLevel", roleLevel);
        paramMap.put("state", state);

        if (roleLevel != null) {
            if (SysRole.Level.manage.getValue() == roleLevel) {
                conditionRoleLevel = "管理级别";
            } else if (SysRole.Level.normal.getValue() == roleLevel) {
                conditionRoleLevel = "应用级别";
            }
        } else {
            conditionRoleLevel = "全部";
        }

        if (state != null) {
            if (SysRole.State.normal.getValue() == state) {
                conditionRoleStatus = "正常";
            } else if (SysRole.State.frozen.getValue() == state) {
                conditionRoleStatus = "冻结";
            } else if (SysRole.State.canceled.getValue() == state) {
                conditionRoleStatus = "注销";
            }
        } else {
            conditionRoleStatus = "全部";
        }

        if (StringUtils.isNotBlank(roleName)) {
            conditionRoleName = roleName;
        } else {
            conditionRoleName = "全部";
        }

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

            StringBuffer queryParam = new StringBuffer();

            queryParam.append(" 角色名称：").append(conditionRoleName);
            queryParam.append(" 角色级别：").append(conditionRoleLevel);
            queryParam.append(" 状态：").append(conditionRoleStatus);

            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("param", queryParam);

            String path = RoleSearchAction.class.getResource("roleSearchListExport.xlsx").getPath();
            ExcelUtil excelUtil = new ExcelUtil();
            inputStream = excelUtil.writeData(sysRoleVoList, path, dataMap, false);

            String fileName = "角色权限查询-" + DateUtil.getDate(new Date(), "yyyyMMdd") + ".xlsx";

            downFileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("角色权限查询导出列表错误：" + e.getMessage());
        }

        return inputStream;
    }

    public String checkRoleFunction() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("roleOid", roleOid);

        try {
            List<SysFunction> sysFunctions = sysPermissionService.doJoinTransQueryRoleFunctionList(paramMap);
            SysRole sysRole = sysRoleService.doJoinTransQuerySysRoleByOid(roleOid);

            roleName = sysRole.getRoleName();

            List<TreeNode> treeNodeList = new ArrayList<TreeNode>();

            if (sysFunctions != null && !sysFunctions.isEmpty()) {
                for (SysFunction sysFunction : sysFunctions) {
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
        } catch (Exception e) {
            logger.error("角色权限查询查看角色功能错误：" + e.getMessage());
            setAlertMessage("角色权限查询查看角色功能错误：" + e.getMessage());
        }

        return "checkRoleFunction";
    }

    public String list() {
        initPageData(100);
        return goCurrent();
    }

    public List<SysRole> getSysRoleList() {
        return sysRoleList;
    }

    public void setSysRoleList(List<SysRole> sysRoleList) {
        this.sysRoleList = sysRoleList;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getRoleLevel() {
        return roleLevel;
    }

    public void setRoleLevel(Integer roleLevel) {
        this.roleLevel = roleLevel;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public List<String> getRoleNameList() {
        return roleNameList;
    }

    public void setRoleNameList(List<String> roleNameList) {
        this.roleNameList = roleNameList;
    }

    public String getRoleOid() {
        return roleOid;
    }

    public void setRoleOid(String roleOid) {
        this.roleOid = roleOid;
    }

    public String getFunctionStr() {
        return functionStr;
    }

    public void setFunctionStr(String functionStr) {
        this.functionStr = functionStr;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public String getConditionRoleName() {
        return conditionRoleName;
    }

    public void setConditionRoleName(String conditionRoleName) {
        this.conditionRoleName = conditionRoleName;
    }

    public String getConditionRoleLevel() {
        return conditionRoleLevel;
    }

    public void setConditionRoleLevel(String conditionRoleLevel) {
        this.conditionRoleLevel = conditionRoleLevel;
    }

    public String getConditionRoleStatus() {
        return conditionRoleStatus;
    }

    public void setConditionRoleStatus(String conditionRoleStatus) {
        this.conditionRoleStatus = conditionRoleStatus;
    }

    public String getBackRoleOid() {
        return backRoleOid;
    }

    public void setBackRoleOid(String backRoleOid) {
        this.backRoleOid = backRoleOid;
    }

    public String getDownFileName() {
        return downFileName;
    }

    public void setDownFileName(String downFileName) {
        this.downFileName = downFileName;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    public void setSysRoleService(SysRoleService sysRoleService) {
        this.sysRoleService = sysRoleService;
    }

    public void setSysPermissionService(SysPermissionService sysPermissionService) {
        this.sysPermissionService = sysPermissionService;
    }

}
