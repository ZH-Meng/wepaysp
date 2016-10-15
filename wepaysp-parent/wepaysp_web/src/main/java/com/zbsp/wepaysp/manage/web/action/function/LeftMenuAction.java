/*
 * LeftMenuAction.java
 * 创建者：杨帆
 * 创建日期：2015年6月10日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.action.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.manage.web.action.BaseAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.manage.web.vo.TreeNode;
import com.zbsp.wepaysp.po.manage.SysFunction;
import com.zbsp.wepaysp.service.manage.SysPermissionService;

/**
 * 获取左侧菜单
 * 
 * @author 杨帆
 */
public class LeftMenuAction
    extends BaseAction {

    private static final long serialVersionUID = -1684063035403915300L;

    private String functionStr;

    private SysPermissionService sysPermissionService;

    @Override
    public String execute()
        throws Exception {
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<TreeNode> treeNodeList = new ArrayList<TreeNode>();
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userOid", manageUser.getIwoid());
        paramMap.put("functionState", SysFunction.State.normal.getValue());
        paramMap.put("functionType", SysFunction.FunctionType.menu.getValue());


        try {
            List<SysFunction> userFunctionList = sysPermissionService.doJoinTransQueryUserFunctionList(paramMap);

            if (userFunctionList != null && !userFunctionList.isEmpty()) {
                for (SysFunction sysFunction : userFunctionList) {
                    TreeNode treeNode = new TreeNode();
                    treeNode.setFatherId(sysFunction.getParentFunctionOid());
                    treeNode.setNodeId(sysFunction.getIwoid());
                    treeNode.setNodeName(sysFunction.getFunctionName());
                    treeNode.setNodeOrder(sysFunction.getDisplayOrder());
                    treeNode.setNodeUrl(sysFunction.getUrl());

                    treeNodeList.add(treeNode);
                }

                Map<String, List<TreeNode>> treeNodeMap = new HashMap<String, List<TreeNode>>();
                treeNodeMap.put("userNode", treeNodeList);

                functionStr = JSONUtil.toJSONString(treeNodeMap, true);
            }
        } catch (Exception e) {
            logger.error("查询用户功能项列表错误：" + e.getMessage());
            setAlertMessage("查询用户功能项列表错误：" + e.getMessage());
        }
        return SUCCESS;
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

}
