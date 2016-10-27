package com.zbsp.wepaysp.manage.web.action.usermanage;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.manage.web.action.PageAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.manage.web.util.ExcelUtil;
import com.zbsp.wepaysp.manage.web.vo.TreeNode;
import com.zbsp.wepaysp.po.manage.SysFunction;
import com.zbsp.wepaysp.po.manage.SysRole;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.service.manage.SysPermissionService;
import com.zbsp.wepaysp.service.manage.SysRoleService;
import com.zbsp.wepaysp.service.manage.SysUserService;
import com.zbsp.wepaysp.vo.manage.SysUserVO;

public class UserSearchAction
    extends PageAction
    implements SessionAware {

    private static final long serialVersionUID = -4860848352710354867L;

    private List<SysUserVO> sysUserVOList;

    private List<SysRole> sysRoleAll;

    private String functionStr;
    private String userOid;

    private String userId;
    private String userName;
    private String roleOid;
    private Integer state;

    private String conditionRoleName;
    private String conditionStateName;

    private String checkUserId = "";

    private Map<String, Object> session;

    private String downFileName;

    private SysUserService sysUserService;
    private SysRoleService sysRoleService;
    private SysPermissionService sysPermissionService;

    @Override
    protected String query(int start, int size) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", userId == null ? userId : userId.trim());
        paramMap.put("userName", userName == null ? userName : userName.trim());
        paramMap.put("roleOid", roleOid);
        paramMap.put("state", state);

        try {
            paramMap.put("buildType", SysUser.BuildType.create.getValue());
            sysUserVOList = sysUserService.doJoinTransQuerySysUserList(paramMap, start, size);
            rowCount = sysUserService.doJoinTransQuerySysUserCount(paramMap);

            List<Integer> roleStateList = new ArrayList<Integer>();
            roleStateList.add(SysRole.State.normal.getValue());
            roleStateList.add(SysRole.State.frozen.getValue());
            paramMap.clear();
            paramMap.put("stateList", roleStateList);
            paramMap.put("buildType", SysUser.BuildType.create.getValue());
            sysRoleAll = sysRoleService.doJoinTransQuerySysRoleList(paramMap, 0, -1);
        } catch (Exception e) {
            logger.error("用户权限查询列表错误：" + e.getMessage());
            setAlertMessage("用户权限查询列表错误！");
        }

        return "userSearch";
    }

    public String exportFile() {
        return SUCCESS;
    }

    public InputStream getDownFile() {
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        InputStream inputStream = null;
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", userId);
        paramMap.put("userName", userName);
        paramMap.put("roleOid", roleOid);
        paramMap.put("state", state);

        try {
            paramMap.put("buildType", SysUser.BuildType.create.getValue());
            sysUserVOList = sysUserService.doTransExportSysUserList(paramMap, manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));

            List<SysUserVO> sysUserVoList = new ArrayList<SysUserVO>();
            for (SysUserVO sysUser : sysUserVOList) {

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

                sysUserVoList.add(sysUser);
            }

            StringBuffer queryParam = new StringBuffer();

            if (StringUtils.isNotBlank(userId)) {
                queryParam.append(" 登录名：").append(userId);
            }

            if (StringUtils.isNotBlank(userName)) {
                queryParam.append(" 真实姓名：").append(userName);
            }

            queryParam.append(" 角色：").append(conditionRoleName);
            queryParam.append(" 状态：").append(conditionStateName);

            Map<String, Object> dataMap = new HashMap<String, Object>();
            dataMap.put("param", queryParam.toString());

            String path = UserSearchAction.class.getResource("userSearchListExport.xlsx").getPath();
            ExcelUtil excelUtil = new ExcelUtil();
            inputStream = excelUtil.writeData(sysUserVoList, path, dataMap, false);

            String fileName = "用户权限查询-" + DateUtil.getDate(new Date(), "yyyyMMdd") + ".xlsx";
            downFileName = new String(fileName.getBytes("GBK"), "ISO8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            logger.error("用户权限查询导出列表错误：" + e.getMessage());
        }

        return inputStream;
    }

    public String checkUserFunction() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userOid", userOid);

        try {
            List<SysFunction> sysFunctions = sysPermissionService.doJoinTransQueryUserFunctionList(paramMap);
            SysUserVO sysUserVO = sysUserService.doJoinTransQueryUserByOid(userOid);

            List<SysRole> roleList = sysUserVO.getUserRoleList();
            if (roleList != null && !roleList.isEmpty()) {
                for (int i = 0; i < roleList.size(); i++) {
                    checkUserId += roleList.get(i).getRoleName();
                    if (i != roleList.size() - 1) {
                        checkUserId += ",";
                    }
                }
            }

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
            logger.error("用户权限查询查看用户功能错误：" + e.getMessage());
            setAlertMessage("用户权限查询查看用户功能错误！");
        }

        return "checkUserFunction";
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public String list() {
        initPageData(100);
        return goCurrent();
    }

    public List<SysUserVO> getSysUserVOList() {
        return sysUserVOList;
    }

    public void setSysUserVOList(List<SysUserVO> sysUserVOList) {
        this.sysUserVOList = sysUserVOList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleOid() {
        return roleOid;
    }

    public void setRoleOid(String roleOid) {
        this.roleOid = roleOid;
    }

    public List<SysRole> getSysRoleAll() {
        return sysRoleAll;
    }

    public void setSysRoleAll(List<SysRole> sysRoleAll) {
        this.sysRoleAll = sysRoleAll;
    }

    public String getFunctionStr() {
        return functionStr;
    }

    public void setFunctionStr(String functionStr) {
        this.functionStr = functionStr;
    }

    public String getUserOid() {
        return userOid;
    }

    public void setUserOid(String userOid) {
        this.userOid = userOid;
    }

    public String getConditionRoleName() {
        return conditionRoleName;
    }

    public void setConditionRoleName(String conditionRoleName) {
        this.conditionRoleName = conditionRoleName;
    }

    public String getConditionStateName() {
        return conditionStateName;
    }

    public void setConditionStateName(String conditionStateName) {
        this.conditionStateName = conditionStateName;
    }

    public String getCheckUserId() {
        return checkUserId;
    }

    public void setCheckUserId(String checkUserId) {
        this.checkUserId = checkUserId;
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

    public void setSysPermissionService(SysPermissionService sysPermissionService) {
        this.sysPermissionService = sysPermissionService;
    }
}
