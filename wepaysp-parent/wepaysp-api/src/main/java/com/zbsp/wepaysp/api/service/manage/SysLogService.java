/*
 * LogService.java
 * 创建者：杨帆
 * 创建日期：2015年6月10日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.api.service.manage;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.vo.manage.SysLogVo;


/**
 * 日志Service.
 * 
 * @author 杨帆
 */
public interface SysLogService {

    /**
     * 记录完整操作日志.
     * 记录一个操作的完整日志，操作开始与结束不分段.
     * 
     * @param logType 日志类型，取值参见{@link com.iwt.vasoss.prvnterminal.po.manage.SysLog.LogType}
     * @param operationUserOid 操作用户主键
     * @param logAbstract 日志描述
     * @param processBeginTime 操作开始时间
     * @param processEndTime 操作结束时间
     * @param dataBefore 操作前数据
     * @param dataAfter 操作后数据
     * @param logState 执行状态，取值参见{@link com.iwt.vasoss.prvnterminal.po.manage.SysLog.State}
     * @param recordOid 影响记录Oid，可选
     * @param functionOid 日志功能项Oid，可选
     * @param actionType 操作类型，可选，取值参见{@link com.iwt.vasoss.prvnterminal.po.manage.SysLog.ActionType}
     * @return 日志记录对象
     */
    public SysLog doTransSaveSysLog(Integer logType, String operationUserOid, String logAbstract, Date processBeginTime, 
        Date processEndTime, String dataBefore, String dataAfter, Integer logState, String recordOid, String functionOid, Integer actionType);
    
    /**
     * 查询符合条件的日志信息列表，查询结果按操作开始时间倒序、管理员登录名升序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      userId:                     String类型，管理员登录名，根据此参数模糊查询
     *      userName:                String类型，管理员姓名，根据此参数模糊查询
     *      roleOid:                    String类型，管理员所属角色Oid，根据此参数精确查询
     *      logType:                   Integer类型，日志类型，取值参见{@link com.iwt.vasoss.prvnterminal.po.manage.SysLog.LogType}
     *      functionOid:              String类型，功能项Oid，根据此参数精确查询
     *      actionType:              Integer类型，操作类型， 取值参见{@link com.iwt.vasoss.prvnterminal.po.manage.SysLog.ActionType}
     *      processBeginTime:      Date类型，操作开始时间，查询结果大于等于此参数
     *      processEndTime:        Date类型，操作结束时间，查询结果小于此参数 
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @return 符合条件的信息列表
     */
    public List<SysLogVo> doJoinTransQuerySysLogList(Map<String, Object> paramMap, int startIndex, int maxResult);
    
    /**
     * 统计符合条件的角色信息总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      userId:                     String类型，管理员登录名，根据此参数模糊查询
     *      userName:                 String类型，管理员姓名，根据此参数模糊查询
     *      roleOid:                    String类型，管理员所属角色Oid，根据此参数精确查询
     *      logType:                   Integer类型，日志类型，取值参见{@link com.iwt.vasoss.prvnterminal.po.manage.SysLog.LogType}
     *      functionOid:              String类型，功能项Oid，根据此参数精确查询
     *      actionType:              Integer类型， 操作类型，取值参见{@link com.iwt.vasoss.prvnterminal.po.manage.SysLog.ActionType}
     *      processBeginTime:      Date类型，操作开始时间，查询结果大于等于此参数
     *      processEndTime:        Date类型，操作结束时间，查询结果小于此参数 
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQuerySysLogCount(Map<String, Object> paramMap);
    
    /**
     * 导出符合条件的日志信息列表，查询结果按操作开始时间倒序、管理员登录名升序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      userId:                     String类型，管理员登录名，根据此参数模糊查询
     *      userName:                 String类型，管理员姓名，根据此参数模糊查询
     *      roleOid:                    String类型，管理员所属角色Oid，根据此参数精确查询
     *      logTypeLevel:            Integer类型，日志类型，取值参见{@link com.iwt.vasoss.prvnterminal.po.manage.SysLog.LogType}
     *      functionOid:              String类型，功能项Oid，根据此参数精确查询
     *      actionType:              Integer类型， 操作类型，取值参见{@link com.iwt.vasoss.prvnterminal.po.manage.SysLog.ActionType}
     *      processBeginTime:      Date类型，操作开始时间，查询结果大于等于此参数
     *      processEndTime:        Date类型，操作结束时间，查询结果小于此参数 
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param operationUserOid 操作者Oid
     * @param functionOid 功能项Oid
     * @return 符合条件的信息列表
     */
    public List<SysLogVo> doTransExportSysLogList(Map<String, Object> paramMap, String operationUserOid, String functionOid);
    
}
