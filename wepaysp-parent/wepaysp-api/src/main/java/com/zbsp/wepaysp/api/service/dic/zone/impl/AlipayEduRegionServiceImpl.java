package com.zbsp.wepaysp.api.service.dic.zone.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.dic.zone.AlipayEduRegionService;
import com.zbsp.wepaysp.vo.dic.zone.CityVo;
import com.zbsp.wepaysp.vo.dic.zone.DistrictVO;
import com.zbsp.wepaysp.vo.dic.zone.ProvinceVo;

/**
 * edu地域 Service
 */
public class   AlipayEduRegionServiceImpl  extends BaseService
implements  AlipayEduRegionService {
    
    /**
     * 查询省份信息.
     * 
     * @return 商户对象
     */
    public List<ProvinceVo> doJoinTransfindAllProvince() {
 
    	String sql = "select distinct provinceCode,provinceName  from AlipayEduRegion   ";
 
		List<?> list =   commonDAO.findObjectList(sql, null, false);
		List<ProvinceVo> provinceList = new ArrayList<ProvinceVo>();
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[]) list.get(i);
			ProvinceVo vo = new ProvinceVo();
			vo.setProvinceCode((String)obj[0]);
			vo.setProvinceName((String)obj[1]);
			provinceList.add(vo);
		}
		return provinceList;
	}
    
    /**
     * 查询地市信息.
     * 
     * @return 地市对象
     */
    public List<CityVo> doJoinTransfindAllCity(String provinceCode) {
    	String sql = "select distinct cityCode,cityName  from AlipayEduRegion where provinceCode = :provinceCode order by cityName ";
    	HashMap<String,Object> paramMap = new HashMap<String,Object> ();
    	paramMap.put("provinceCode", provinceCode);
		List<?> list =   commonDAO.findObjectList(sql, paramMap, false);
		List<CityVo> cityList = new ArrayList<CityVo>();
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[]) list.get(i);
			CityVo vo = new CityVo();
			vo.setCityCode((String)obj[0]);
			vo.setCityName((String)obj[1]);
			cityList.add(vo);
		}
		return cityList;
	}
    
    /**
     * 查询区县信息.
     * 
     * @return 区县对象
     */
    public List<DistrictVO> doJoinTransfindAllDistrict(String cityCode) {
    	String sql = "select  districtCode,districtName  from AlipayEduRegion where cityCode = :cityCode order by districtName ";
    	HashMap<String,Object> paramMap = new HashMap<String,Object> ();
    	paramMap.put("cityCode", cityCode);
		List<?> list =   commonDAO.findObjectList(sql, paramMap, false);
		List<DistrictVO> districtList = new ArrayList<DistrictVO>();
		for(int i=0;i<list.size();i++){
			Object[] obj = (Object[]) list.get(i);
			DistrictVO vo = new DistrictVO();
			vo.setDistrictCode((String)obj[0]);
			vo.setDistrictName((String)obj[1]);
			districtList.add(vo);
		}
		return districtList;
	} 
}
