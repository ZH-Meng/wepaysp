/*
 * SysFunctionService.java
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


/**
 * 功能项Serivce
 * 
 * @author 杨帆
 */
public interface SysFunctionService {
    
    /**
     * 查询请求地址是否在系统功能项中定义.
     * 
     * @param url 请求地址
     * @return 已定义返回功能项定义，未定义或地址为空返回null
     */
    public SysFunction doJoinTransIsSysFunction(String url);
    
    /**
     * 查询符合条件的功能项信息列表，查询结果按功能项排序权重排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      functionType:     Integer类型，功能项类型，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysFunction.FunctionType}
     *      functionLevel:     Integer类型，功能项级别，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysRole.Level}
     *      state:               Integer类型，功能项状态，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysFunction.State}   
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息列表
     */
    public List<SysFunction> doJoinTransQuerySysFunctionList(Map<String, Object> paramMap);
    
    /**
     * 查询符合条件的含日志记录的功能项信息列表，查询结果按功能项排序权重排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      functionType:     Integer类型，功能项类型，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysFunction.FunctionType}
     *      functionLevel:     Integer类型，功能项级别，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysRole.Level}
     *      state:               Integer类型，功能项状态，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.manage.SysFunction.State}   
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息列表
     */
    public List<SysFunction> doJoinTransQueryLogSysFunctionList(Map<String, Object> paramMap);
}
