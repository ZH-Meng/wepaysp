package com.zbsp.wepaysp.api.service.pay;

import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;

/**
 * 微信交易明细service
 */
public interface WeixinPayDetailsService {
    
    /**
     * 查询符合条件的列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      partner1Oid:                       String类型，一级服务商Oid，根据此参数精确查询
     *      partner2Oid:                       String类型，二级服务商Oid，根据此参数精确查询
     *      partner3Oid:                       String类型，三级服务商Oid，根据此参数精确查询
     *      partnerEmployeeOid:           String类型，业务员Oid，根据此参数精确查询
     *      dealerOid:          				 String类型，商户Oid，根据此参数精确查询
     *      dealerEmployeeOid:           String类型，收银员Oid，根据此参数精确查询
     *      partnerEmployeeId:     String类型，业务员ID，根据此参数模糊查询
     *      dealerId:                     String类型，商家ID，根据此参数模糊查询
     *      dealerEmployeeId:       String类型，收银员ID，根据此参数模糊查询
     *      storeId:                      String类型，门店ID，根据此参数模糊查询
     *      beginTime:                       Date类型，交易开始时间，根据此参数模糊查询
     *      endTime:                         Date类型，交易截止时间，根据此参数模糊查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @return 符合条件的信息列表
     */
    public List<WeixinPayDetailsVO> doJoinTransQueryWeixinPayDetailsList(Map<String, Object> paramMap, int startIndex, int maxResult);
    
    /**
     * 统计符合条件的总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      partner1Oid:                       String类型，一级服务商Oid，根据此参数精确查询
     *      partner2Oid:                       String类型，二级服务商Oid，根据此参数精确查询
     *      partner3Oid:                       String类型，三级服务商Oid，根据此参数精确查询
     *      partnerEmployeeOid:           String类型，业务员Oid，根据此参数精确查询
     *      dealerOid:                           String类型，商户Oid，根据此参数精确查询
     *      dealerEmployeeOid:           String类型，收银员Oid，根据此参数精确查询
     *      partnerEmployeeId:     String类型，业务员Id，根据此参数模糊查询
     *      dealerId:                     String类型，商家ID，根据此参数模糊查询
     *      dealerEmployeeId:       String类型，收银员ID，根据此参数模糊查询
     *      storeId:                      String类型，门店ID，根据此参数模糊查询
     *      beginTime:                       Date类型，交易开始时间，根据此参数模糊查询
     *      endTime:                         Date类型，交易截止时间，根据此参数模糊查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQueryWeixinPayDetailsCount(Map<String, Object> paramMap);
    
    /**
     * 创建支付明细（交易订单）
     * @param weixinPayDetailsVO 要保存的支付明细对象 
     * @param creator 创建人名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 保存成功返回保存后的支付明细VO
     * @throws AlreadyExistsException 如果支付明细已存在
     */
    public WeixinPayDetailsVO doTransCreatePayDetails(WeixinPayDetailsVO weixinPayDetailsVO, String creator, String operatorUserOid, String logFunctionOid);

    /**
     * 更新支付结果
     * 
     * @param returnCode 业务结果 SUCCESS/FAIL，FAIL时错误信息详见errCode
     * @param resultCode 业务结果码
     * @param payResultVO 封装的支付结果VO
     */
    public void doTransUpdatePayResult(String returnCode, String resultCode, WeixinPayDetailsVO payResultVO);

    /**
     * 更新统一下单结果
     * 
     * @param returnCode 业务结果 SUCCESS/FAIL，FAIL时错误信息详见errCode
     * @param errCode 业务错误码
     * @param payResultVO 封装的支付结果VO
     */
    public void doTransUpdateOrderResult(String returnCode, String resultCode, WeixinPayDetailsVO payResultVO);

    /**
     * 根据Oid 查找支付明细
     * @param weixinPayDetailOid
     * @return WeixinPayDetailsVO
     */
    public WeixinPayDetailsVO doJoinTransQueryWeixinPayDetailsByOid(String weixinPayDetailOid);

    /**
     * 根据Oid 取消支付
     * @param weixinPayDetailOid
     */
    public void doTransCancelPay(String weixinPayDetailOid);

}
