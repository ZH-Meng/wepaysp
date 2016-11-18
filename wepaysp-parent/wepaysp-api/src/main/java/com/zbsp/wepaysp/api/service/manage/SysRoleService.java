/*
 * SysRoleService.java
 * 创建者：杨帆
 * 创建日期：2015年6月8日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.api.service.manage;

import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.po.manage.SysRole;

/**
 * 角色Service
 * 
 * @author 杨帆
 */
public interface SysRoleService {

    /**
     * 查询符合条件的角色信息列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      state:           Integer类型，角色状态，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysRole.State}
     *      stateList:      List<Integer>类型，角色状态，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysRole.State}
     *      buildType:     Integer类型，创建类型，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysUser.BuildType}
     *      roleName:      String类型，角色名称，根据此参数精确查询
     *      roleLevel:      Integer类型，角色级别，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysRole.Level}
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @return 符合条件的信息列表
     */
    public List<SysRole> doJoinTransQuerySysRoleList(Map<String, Object> paramMap, int startIndex, int maxResult);
    
    /**
     * 统计符合条件的角色信息总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      state:          Integer类型，角色状态，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysRole.State}
     *      stateList:     List<Integer>类型，角色状态，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysRole.State}
     *      buildType:    Integer类型，创建类型，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysUser.BuildType}
     *      roleName:     String类型，角色名称，根据此参数精确查询
     *      roleLevel:     Integer类型，角色级别，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysRole.Level}
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQuerySysRoleCount(Map<String, Object> paramMap);
    
    /**
     * 导出符合条件的角色信息列表，查询结果按最后修改时间倒序排列，并记录导出日志.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      state:          Integer类型，角色状态，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysRole.State}
     *      stateList:     List<Integer>类型，角色状态，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysRole.State}
     *      buildType:    Integer类型，创建类型，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysUser.BuildType}
     *      roleName:     String类型，角色名称，根据此参数精确查询
     *      roleLevel:     Integer类型，角色级别，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysRole.Level}
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录功能项Oid
     * @return 符合条件的信息列表
     */
    public List<SysRole> doTransExportSysRoleList(Map<String, Object> paramMap, String operatorUserOid, String logFunctionOid);
    
    /**
     * 查询符合条件的唯一角色名称列表，查询结果按角色名称排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      state:           Integer类型，角色状态，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysRole.State}
     *      buildType:      Integer类型，创建类型，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysUser.BuildType}
     *      roleLevel:       Integer类型，角色级别，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysRole.Level}
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息列表
     */
    public List<String> doJoinTransQueryUniqueRoleNameList(Map<String, Object> paramMap);
    
    /**
     * 创建角色信息并记录操作日志.
     * 
     * @param sysRole 要保存的角色信息对象
     * @param creator 创建人姓名 
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录功能项Oid
     * @return 创建成功返回保存后的角色信息
     * @throws AlreadyExistsException 如果角色Id已存在
     */
    public SysRole doTransAddRole(SysRole sysRole, String creator, String operatorUserOid, String logFunctionOid) throws AlreadyExistsException;
    
    /**
     * 根据Oid修改角色信息并记录操作日志.
     * 角色已被使用不能修改角色id.
     * 
     * @param sysRole 要修改的角色信息对象
     * @param modifier 修改人姓名
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录功能项Oid
     * @return 修改成功返回修改后的角色信息
     * @throws AlreadyExistsException 如果角色id已存在（排除当前角色）
     */
    public SysRole doTransUpdateRole(SysRole sysRole, String modifier, String operatorUserOid, String logFunctionOid) throws AlreadyExistsException, 
        IllegalArgumentException;
    
    /**
     * 根据主键查询角色信息.
     * 
     * @param sysRoleOid 角色Oid
     * @return 角色信息对象
     */
    public SysRole doJoinTransQuerySysRoleByOid(String sysRoleOid);
}
