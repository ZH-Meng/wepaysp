/*
 * ZoneService.java
 * 创建者：杨帆
 * 创建日期：2016年5月2日
 *
 * 版权所有(C) 2015-2018。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.service.dic;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zbsp.wepaysp.po.dic.SysCity;
import com.zbsp.wepaysp.po.dic.SysCounty;
import com.zbsp.wepaysp.po.dic.SysProvince;

/**
 * 地域字典信息Service.
 * 
 * @author 杨帆
 */
public interface ZoneService {
	/**
     * 查询符合条件的省份信息列表，查询结果按照省份代码升序排序.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      dataType:         Integer类型，数据类别，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.dic.SysProvince.DataType}
     *      busiType:       	Integer类型，开通省级业务标志，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.dic.SysProvince.BusiType}
     *      virtualFlag:        Integer类型，虚拟标志，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.card.LotteryStation.VirtualFlag}
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息列表
     */
	public List<SysProvince> doJoinTransQuerySysProviceList(Map<String, Object> paramMap);
	
	/**
     * 查询符合条件的地市信息列表，查询结果按照地市代码升序排序.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      dataType:                   Integer类型，数据类别，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.dic.SysProvince.DataType}
     *      sysProvinceOidList:	  List<String>类型，省份信息，根据此参数精确查询
     *      virtualFlag:                  Integer类型，虚拟标志，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.card.LotteryStation.VirtualFlag}
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息列表
     */
	public List<SysCity> doJoinTransQuerySysCityList(Map<String, Object> paramMap);

	
	/**
     * 查询符合条件的区县信息列表，查询结果按照区县代码升序排序.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      dataType:             Integer类型，数据类别，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.dic.SysProvince.DataType}
     *      sysCityOidList:      List<String>类型，省份信息，根据此参数精确查询
     *      virtualFlag:           Integer类型，虚拟标志，根据此参数精确查询，取值参见{@link com.iwt.vasoss.prvnpoint.po.card.LotteryStation.VirtualFlag}
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息列表
     */
    public List<SysCounty> doJoinTransQuerySysCountyList(Map<String, Object> paramMap);
    
    
	/**
	 * 根据OID查找省份信息
	 * @param provinceOid
	 * @return
	 */
    public SysProvince doJoinQuerySysProvinceByOid(String provinceOid);

    /**
     * 根据OID查找地市信息
     * @param cityOid
     * @return
     */
    public SysCity doJoinQuerySysCityByOid(String cityOid);
    
	/**
     * 查询符合条件的省份ID.
     * 
     * @return 符合条件的信息列表
     */
	public Set<String> doTransQueryCalSysProviceSet();    
}
