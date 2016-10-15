/*
 * SysUserService.java
 * 创建者：杨帆
 * 创建日期：2015年6月8日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.service.manage;

import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.vo.manage.SysUserVO;

/**
 * 后台管理用户Service
 * 
 * @author 杨帆
 */
public interface SysUserService {

    /**
     * 后台管理用户登录.
     * 登录成功后返回Map的key如下:
     * <pre>
     *      sysUser:        后台管理用户对象{@link com.iwt.vasoss.prvnpoint.po.manage.SysUser}
     *      loginToken:    用户登录会话对象{@link com.iwt.vasoss.prvnpoint.po.manage.SysUserLoginToken} 
     * </pre>
     * 
     * @param userId 用户登录标识
     * @param password 登录密码
     * @param ip 用户登录ip
     * @return 登录成功返回包含后台管理用户对象、用户登录会话对象的Map
     * @throws IllegalStateException 如果用户已冻结
     * @throws IllegalAccessException 如果用户名或密码不正确，如果用户已注销
     * @throws AlreadyExistsException 如果用户重复登录
     */
    public Map<String, Object> doTransUserLogin(String userId, String password, String ip) throws IllegalStateException, IllegalAccessException, AlreadyExistsException;
    
    /**
     * 后台管理用户退出登录.
     * 记录退出登录日志.
     * 
     * @param userOid 用户Oid
     */
    public void doTransUserLogout(String userOid);
    
    /**
     * 用户修改登录密码.
     * 
     * @param userOid 管理员用户Oid
     * @param oldPwd 原登录密码
     * @param newPwd 新登录密码
     * @throws IllegalAccessException 如果原密码不正确
     */
    public void doTransModifyUserPwd(String userOid, String oldPwd, String newPwd) throws IllegalAccessException;
    
    /**
     * 查询符合条件的后台管理用户信息列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      userId:          String类型，登录名，根据此参数模糊查询
     *      userName:     String类型，真实姓名，根据此参数模糊查询
     *      roleOid:         String类型，用户所属角色Oid，根据此参数精确查询
     *      buildType:    Integer类型，创建类型，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysUser.BuildType}
     *      state:           Integer类型，用户状态，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysUser.State}
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @return 符合条件的信息列表
     */
    public List<SysUserVO> doJoinTransQuerySysUserList(Map<String, Object> paramMap, int startIndex, int maxResult);
    
    /**
     * 统计符合条件的用户信息总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      userId:          String类型，登录名，根据此参数模糊查询
     *      userName:     String类型，真实姓名，根据此参数模糊查询
     *      roleOid:         String类型，用户所属角色Oid，根据此参数精确查询
     *      buildType:    Integer类型，创建类型，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysUser.BuildType}
     *      state:           Integer类型，用户状态，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysUser.State}
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQuerySysUserCount(Map<String, Object> paramMap);
    
    /**
     * 导出符合条件的用户角色列表，查询结果按最后修改时间倒序排列，并记录导出日志.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      userId:          String类型，登录名，根据此参数模糊查询
     *      userName:     String类型，真实姓名，根据此参数模糊查询
     *      roleOid:         String类型，用户所属角色Oid，根据此参数精确查询
     *      buildType:    Integer类型，创建类型，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysUser.BuildType}
     *      state:           Integer类型，用户状态，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysUser.State}
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录功能项Oid
     * @return 符合条件的信息列表
     */
    public List<SysUserVO> doTransExportSysUserList(Map<String, Object> paramMap, String operatorUserOid, String logFunctionOid);
    
    /**
     * 根据用户Oid查询用户信息.
     * 
     * @param userOid 用户Oid
     * @return 用户信息VO
     */
    public SysUserVO doJoinTransQueryUserByOid(String userOid);
    
    /**
     * 重置后台用户密码.
     * 
     * @param userOid 要重置密码的用户Oid
     * @param newPwd 重置后的新密码
     * @param operatorOid 执行重置操作的用户Oid
     * @param operatorName 执行重置操作的用户名称
     * @param logFunctionOid 日志功能项Oid
     */
    public void doTransResetUserPwd(String userOid, String newPwd, String operatorOid, String operatorName, String logFunctionOid);

    /**
     * 创建系统管理用户.
     * 
     * @param sysUser 要保存的系统管理用户对象 
     * @param roleOidList 赋予用户的角色主键列表
     * @param creator 创建人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 保存成功返回保存后的系统管理用户对象VO
     * @throws AlreadyExistsException 如果用户登录名已存在
     */
    public SysUserVO doTransAddSysUser(SysUser sysUser, List<String> roleOidList, String creator, String operatorUserOid, 
    		String logFunctionOid) throws AlreadyExistsException;
    
    /**
     * 修改系统管理用户.
     * 
     * @param sysUser 要修改的系统管理用户对象
     * @param roleOidList 赋予用户的角色主键列表
     * @param modifier 修改人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 修改成功返回修改后的系统管理用户对象VO
     * @throws AlreadyExistsException 如果用户登录名已存在
     */
    public SysUserVO doTransUpdateSysUser(SysUser sysUser, List<String> roleOidList, String modifier, String operatorUserOid,
    		String logFunctionOid) throws AlreadyExistsException;
    
    /**
     * 判断用户是否拥有指定功能项的权限（用于审核权限的判断）.
     * 
     * @param userId 用户登录标识
     * @param password 登录密码
     * @param functionOid 要校验的功能项Oid
     * @return 如果用户拥有该功能项的权限返回用户信息，否则返回null
     * @throws IllegalStateException 如果用户状态为已冻结
     * @throws IllegalAccessException 如果用户名或密码错误
     * @throws NotExistsException 如果用户不具备审核权限
     */
    public SysUser doJoinTransIsUserFunction(String userId, String password, String functionOid) throws IllegalStateException, 
        IllegalAccessException, NotExistsException;
    
    /**
     * 刷新用户登录令牌，过期时间延长10分钟.
     * 
     * @param sysUserOid 要刷新的用户Oid
     * @param loginToken 用户的登录令牌
     * @return 刷新成功返回true，如果用户令牌不存在或登录超时返回false
     */
    public boolean doTransRefreshLoginToken(String sysUserOid, String loginToken);
}
