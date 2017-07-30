package com.zbsp.wepaysp.api.service.weixin;

import java.util.List;
import java.util.Map;

import com.tencent.protocol.appid.sns_userinfo_protocol.GetUserinfoResData;
import com.zbsp.wepaysp.po.weixin.PayNoticeBindWeixin;
import com.zbsp.wepaysp.vo.weixin.PayNoticeBindWeixinVO;

/**
 * 微信支付（门店/收银员级别）通知绑定收银员服务
 * 
 * @author 孟郑宏
 */
public interface PayNoticeBindWeixinService {
    
    /**
     * 查询符合条件的列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      type:                           String类型，绑定类型，必填，根据此参数精确查询
     *      storeOid:                     String类型，门店Oid，type=1必填，根据此参数精确查询
     *      dealerEmployeeOid:   String类型，收银员Oid，type=2必填，根据此参数精确查询
     *      state:                          Integer类型，开关状态，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息列表
     */
    public List<PayNoticeBindWeixinVO> doJoinTransQueryPayNoticeBindWeixinList(Map<String, Object> paramMap);
    
    /**
     * 批量更新支付通知绑定信息
     * @param bindVOList 前台传的绑定List
     * @param modifier
     * @param operatorUserOid
     * @param logFunctionOid
     */
    public void doTransUpdatePayNoticeBindWeixinList(List<PayNoticeBindWeixinVO> bindVOList, String modifier, String operatorUserOid, String logFunctionOid);
    
    /**
     * 删除支付通知绑定信息
     * @param payNoticeBindWeixinOid
     */
    public void doTransDeletePayNoticeBindWeixin(String payNoticeBindWeixinOid);

    /**
     * 绑定微信账号和门店/收银员，bindType=1 绑定门店，bindType=2绑定门店
     * @param bindType 绑定类型
     * @param toRelateOid bindType=1 为storeOid，bindType=2为 dealerEmployeeOid
     * @param userinfoResData
     * @return PayNoticeBindWeixinVO
     */
    public PayNoticeBindWeixinVO doTransAddPayNoticeBindWeixin(String bindType, String toRelateOid, GetUserinfoResData userinfoResData);

    /**
     * 根据微信唯一标识查找绑定用户和绑定信息
     * @param openid
     * @return
     */
    public Map<String, Object> doJoinTransQueryBindInfo(String openid);

    /**
     * 通过绑定信息oid更新绑定状态
     * @param bindOid
     * @param state
     */
    public void doTransUpdateBindWeixinState(String bindOid, PayNoticeBindWeixin.State state);

    /**
     * 查找商户绑定微信
     * @param dealerOid
     * @param state 指定状态查询，缺省查找所有绑定
     * @return
     */
    public List<PayNoticeBindWeixinVO> doJoinTransQueryDealerBind(String dealerOid, String state);
    
}
