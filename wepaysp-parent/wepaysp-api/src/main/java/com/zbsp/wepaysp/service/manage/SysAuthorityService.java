/*
 * SysAuthorityService.java
 * 创建者：杨帆
 * 创建日期：2015年6月8日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.service.manage;

import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.po.manage.SysRole;



/**
 * 用户角色Service
 * 
 * @author 杨帆
 */
public interface SysAuthorityService {

    /**
     * 查询符合条件的用户赋予角色信息列表.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      sysUserOid:     String类型，拥有角色用户Oid，根据此参数精确查询
     *      roleState:       Integer类型，用户拥有角色的状态，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息列表
     */
    public List<SysRole> doJoinTransQueryUserRoleList(Map<String, Object> paramMap);
}
