package com.zbsp.wepaysp.api.service.dic.zone;

import java.util.List;

import com.zbsp.wepaysp.vo.dic.zone.CityVo;
import com.zbsp.wepaysp.vo.dic.zone.DistrictVO;
import com.zbsp.wepaysp.vo.dic.zone.ProvinceVo;

/**
 * edu地域 Service
 */
public interface AlipayEduRegionService {
    
    /**
     * 查询省份信息.
     * 
     * @return 商户对象
     */
    public List<ProvinceVo> doJoinTransfindAllProvince();
    
    /**
     * 查询地市信息.
     * 
     * @return 地市对象
     */
    public List<CityVo> doJoinTransfindAllCity(String provinceCode);
    
    /**
     * 查询区县信息.
     * 
     * @return 区县对象
     */
    public List<DistrictVO> doJoinTransfindAllDistrict(String cityCode) ; 
}
