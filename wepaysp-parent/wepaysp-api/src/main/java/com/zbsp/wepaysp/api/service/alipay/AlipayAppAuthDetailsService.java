package com.zbsp.wepaysp.api.service.alipay;

import com.zbsp.wepaysp.vo.alipay.AlipayAppAuthDetailsVO;

/**
 * 蚂蚁平台商户对第三方应用（即：本系统在蚂蚁平台的应用）授权服务
 * 
 * @author 孟郑宏
 */
public interface AlipayAppAuthDetailsService {

    /**
     * 创建第三方应用授权记录
     * @param appAuthDetailsVO 授权信息VO
     * @return AlipayAppAuthDetailsVO
     */
    public AlipayAppAuthDetailsVO doTransCreateAppAuthDetail(AlipayAppAuthDetailsVO appAuthDetailsVO);
    
}
