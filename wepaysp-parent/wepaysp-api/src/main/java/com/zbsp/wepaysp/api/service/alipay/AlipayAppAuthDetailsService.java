package com.zbsp.wepaysp.api.service.alipay;

import java.util.List;

import com.zbsp.wepaysp.po.alipay.AlipayAppAuthDetails;
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

    /**
     * 根据商户Oid查找有效的应用授权记录
     * @param dealerOid 商户Oid 
     * @param appId 应用ID 
     * @return 授权记录
     */
    public AlipayAppAuthDetailsVO doJoinTransQueryAppAuthDetailByDealer(String dealerOid, String appId);

    /***
     * 查找某应用过期的应用授权信息
     * @param appid 应用ID 
     * @return 过期的授权记录集合
     */
    public List<AlipayAppAuthDetails> doJoinTransQueryExpiredAppAuthDetails(String appid);

    /**更新刷新后的应用授权信息*/
    public void doTransUpdateAppAuthDetail(AlipayAppAuthDetails appAuthDetails);

    /***
     * 查找某应用有效的应用授权信息
     * @param appid 应用ID 
     * @param dealerId
     * @return 有效的授权记录集合
     */
	public List<AlipayAppAuthDetails> doJoinTransQueryValidAppAuthDetails(String appid, String dealerId);
    
}
