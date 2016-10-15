/*
 * ZoneServiceImpl.java
 * 创建者：侯建玮
 * 创建日期：2016年5月3日 下午2:32:34
 *
 * 版权所有(C) 2015-2018。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.service.dic.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.dic.SysCity;
import com.zbsp.wepaysp.po.dic.SysCounty;
import com.zbsp.wepaysp.po.dic.SysProvince;
import com.zbsp.wepaysp.service.BaseService;
import com.zbsp.wepaysp.service.dic.ZoneService;

/**
 * 地域字典信息
 * @author： 侯建玮
 */
public class ZoneServiceImpl extends BaseService implements ZoneService{
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SysProvince> doJoinTransQuerySysProviceList(Map<String, Object> paramMap) {
		Integer dataType = MapUtils.getInteger(paramMap, "dataType");
		Integer busiType = MapUtils.getInteger(paramMap, "busiType");
		Integer virtualFlag = MapUtils.getInteger(paramMap, "virtualFlag");
		
		Map<String, Object> queryMap = new HashMap<String, Object>();
		StringBuilder sql = new StringBuilder("select sp from SysProvince sp where 1=1");
		
		if(dataType != null){
			sql.append(" and sp.dataType = :DATATYPE");
			queryMap.put("DATATYPE", dataType);
		}
		
		if(busiType != null){
			sql.append(" and sp.busiType = :BUSITYPE");
			queryMap.put("BUSITYPE", busiType);
		}
		
		if(virtualFlag != null){
            sql.append(" and sp.virtualFlag = :VIRTUALFLAG");
            queryMap.put("VIRTUALFLAG", virtualFlag);
        }
		
		sql.append(" order by sp.provinceCode asc");
		
		return (List<SysProvince>) this.commonDAO.findObjectList(sql.toString(), queryMap, false);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<SysCity> doJoinTransQuerySysCityList(Map<String, Object> paramMap) {
		Integer dataType = MapUtils.getInteger(paramMap, "dataType");
		List<String> sysProvinceOidList = (List<String>) paramMap.get("sysProvinceOidList");
		Integer virtualFlag = MapUtils.getInteger(paramMap, "virtualFlag");
		
		Map<String, Object> queryMap = new HashMap<String, Object>();
		StringBuilder sql = new StringBuilder("select sc from SysCity sc left join fetch sc.sysProvince where 1=1");
		
		if(dataType != null){
			sql.append(" and sc.dataType = :DATATYPE");
			queryMap.put("DATATYPE", dataType);
		}
		
		if(sysProvinceOidList != null && !sysProvinceOidList.isEmpty()){
			sql.append(" and sc.sysProvince.iwoid in (:SYSPROVINCEOIDLIST)");
			queryMap.put("SYSPROVINCEOIDLIST", sysProvinceOidList);
		}
		
		if(virtualFlag != null){
            sql.append(" and sc.virtualFlag = :VIRTUALFLAG");
            queryMap.put("VIRTUALFLAG", virtualFlag);
        }
		
		sql.append(" order by sc.cityCode asc");
		
		return (List<SysCity>) this.commonDAO.findObjectList(sql.toString(), queryMap, false);
	}
	
	@SuppressWarnings("unchecked")
    @Override
    public List<SysCounty> doJoinTransQuerySysCountyList(Map<String, Object> paramMap) {
        Integer dataType = MapUtils.getInteger(paramMap, "dataType");
        List<String> sysCityOidList = (List<String>) paramMap.get("sysCityOidList");
        Integer virtualFlag = MapUtils.getInteger(paramMap, "virtualFlag");
        
        Map<String, Object> queryMap = new HashMap<String, Object>();
        StringBuilder sql = new StringBuilder("select sc from SysCounty sc left join fetch sc.sysCity syscity left join fetch syscity.sysProvince where 1=1");
        
        if(dataType != null){
            sql.append(" and sc.dataType = :DATATYPE");
            queryMap.put("DATATYPE", dataType);
        }
        
        if(sysCityOidList != null && !sysCityOidList.isEmpty()){
            sql.append(" and sc.sysCity.iwoid in (:SYSCITYOIDLIST)");
            queryMap.put("SYSCITYOIDLIST", sysCityOidList);
        }
        
        if(virtualFlag != null){
            sql.append(" and sc.virtualFlag = :VIRTUALFLAG");
            queryMap.put("VIRTUALFLAG", virtualFlag);
        }
        
        sql.append(" order by sc.countyCode asc");
        
        return (List<SysCounty>) this.commonDAO.findObjectList(sql.toString(), queryMap, false);
    }

    @Override
    public SysProvince doJoinQuerySysProvinceByOid(String provinceOid) {
        Validator.checkArgument(StringUtils.isBlank(provinceOid), "省份Oid不能为空");
        String sql = "select p from SysProvince p where p.iwoid=:provinceOid";
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("provinceOid", provinceOid);
        return (SysProvince)this.commonDAO.findObject(sql, queryMap, false);
    }
    
    @Override
    public SysCity doJoinQuerySysCityByOid(String cityOid) {
        Validator.checkArgument(StringUtils.isBlank(cityOid), "地市Oid不能为空");
        String sql = "select p from SysCity p left join fetch p.sysProvince where p.iwoid=:cityOid";
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("cityOid", cityOid);
        return (SysCity)this.commonDAO.findObject(sql, queryMap, false);
    }
    
    @SuppressWarnings("unchecked")
	@Override
	public Set<String> doTransQueryCalSysProviceSet() { 

		StringBuilder sql = new StringBuilder("select provinceCode from SysProvince sp where pointHandleStatus = 1");
		List<String> provinceList = (List<String>) this.commonDAO.findObjectList(sql.toString(), null, false);
		HashSet<String> provinceSet  = new HashSet<String>();
		for(String provinceId:provinceList){
			provinceSet.add(provinceId.substring(0, 2));
		}
		return provinceSet;
	}    
}
