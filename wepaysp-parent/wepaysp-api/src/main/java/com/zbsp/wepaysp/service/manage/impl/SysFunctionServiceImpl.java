/*
 * SysFunctionServiceImpl.java
 * 创建者：杨帆
 * 创建日期：2015年6月8日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.service.manage.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.po.manage.SysFunction;
import com.zbsp.wepaysp.service.BaseService;
import com.zbsp.wepaysp.service.manage.SysFunctionService;

/**
 * @author 杨帆
 */
public class SysFunctionServiceImpl extends BaseService implements SysFunctionService {

    @Override
    public SysFunction doJoinTransIsSysFunction(String url) {
        if (StringUtils.isBlank(url)) {
            return null;
        }
        
        String sql = "select f from SysFunction f where f.url = :URL and f.state = :STATE";
        
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("URL", url);
        queryMap.put("STATE", SysFunction.State.normal.getValue());
        
        return commonDAO.findObject(sql, queryMap, false);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<SysFunction> doJoinTransQuerySysFunctionList(Map<String, Object> paramMap) {
        Integer functionType = MapUtils.getInteger(paramMap, "functionType");
        Integer functionLevel = MapUtils.getInteger(paramMap, "functionLevel");
        Integer state = MapUtils.getInteger(paramMap, "state");
        
        String sql = "select f from SysFunction f where 1=1 ";

        Map<String, Object> queryMap = new HashMap<String, Object>();
        
        if(functionType!=null){
            sql +=" and f.functionType = :FUNCTIONTYPE ";
            queryMap.put("FUNCTIONTYPE", functionType);
        }
        
        if(functionLevel!=null){
            sql +=" and f.functionLevel = :FUNCTIONLEVEL ";
            queryMap.put("FUNCTIONLEVEL", functionLevel);
        }
        
        if (state != null) {
            sql += " and f.state = :STATE ";
            queryMap.put("STATE", state);
        }
        
        sql += " or f.parentFunctionOid is null order by f.displayOrder";
        
        return (List<SysFunction>) commonDAO.findObjectList(sql, queryMap, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SysFunction> doJoinTransQueryLogSysFunctionList(Map<String, Object> paramMap) {
        Integer functionType = MapUtils.getInteger(paramMap, "functionType");
        Integer functionLevel = MapUtils.getInteger(paramMap, "functionLevel");
        Integer state = MapUtils.getInteger(paramMap, "state");
        
        String sql = "select f from SysFunction f where f.logFunctionOid is not null ";

        Map<String, Object> queryMap = new HashMap<String, Object>();
        
        if(functionType!=null){
            sql +=" and f.functionType = :FUNCTIONTYPE ";
            queryMap.put("FUNCTIONTYPE", functionType);
        }
        
        if(functionLevel!=null){
            sql +=" and f.functionLevel = :FUNCTIONLEVEL ";
            queryMap.put("FUNCTIONLEVEL", functionLevel);
        }
        
        if (state != null) {
            sql += " and f.state = :STATE ";
            queryMap.put("STATE", state);
        }
        
        sql += " order by f.displayOrder";
        
        return (List<SysFunction>) commonDAO.findObjectList(sql, queryMap, false);
    }

}
