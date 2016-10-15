/*
 * SysPermissionService.java
 * 创建者：杨帆
 * 创建日期：2015年6月8日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.service.manage;

import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.po.manage.SysFunction;
import com.zbsp.wepaysp.po.manage.SysRole;

/**
 * 角色权限Service
 * 
 * @author 杨帆
 */
public interface SysPermissionService {
    
    /**
     * 查询符合条件的功能项授予角色信息列表.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      functionUrl:     String类型，功能项请求地址，根据此参数精确查询
     *      roleState:       Integer类型，功能项授予角色的状态，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysRole.State}
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息列表
     */
    public List<SysRole> doJoinTransQueryFunctionRoleList(Map<String, Object> paramMap);
    
    /**
     * 查询符合条件的角色拥有功能项信息列表，查询结果按功能项排序权重排列.
     * 多个角色拥有同一功能项时，返回值中不包含重复的功能项.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      roleOid:                String类型，角色Oid，根据此参数精确查询
     *      functionState:       Integer类型，角色拥有功能项的状态，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysFunction.State}
     *      functionType:        Integer类型，功能项类型，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysFunction.FunctionType}
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息列表
     */
    public List<SysFunction> doJoinTransQueryRoleFunctionList(Map<String, Object> paramMap);
    
    /**
     * 查询符合条件的用户拥有功能项信息列表，查询结果按功能项排序权重排列.
     * 用户拥有多个角色中含有同一功能项时，返回值中不包含重复的功能项.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      userOid:                String类型，用户Oid，根据此参数精确查询
     *      functionState:       Integer类型，角色拥有功能项的状态，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysFunction.State}
     *      functionType:        Integer类型，功能项类型，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysFunction.FunctionType}
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息列表
     */
    public List<SysFunction> doJoinTransQueryUserFunctionList(Map<String, Object> paramMap);
    
    /**
     * 角色功能项授权.
     * 
     * @param sysRoleOid 要修改的角色Oid
     * @param functionOidList 要赋权的功能项Oid列表
     * @param operatorOid 操作用户Oid
     * @param logFunctionOid 日志记录功能项Oid
     * @throws IllegalAccessException 如果要修改的角色为内置角色
     */
    public void doTransAssignRoleFunction(String sysRoleOid, List<String> functionOidList, String operatorOid, String logFunctionOid) throws IllegalAccessException;
    
}
