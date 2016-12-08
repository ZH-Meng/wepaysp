package com.zbsp.wepaysp.api.service.pay;

import java.util.Map;

import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.vo.pay.WeixinRefundDetailsVO;

/**
 * 微信退款明细service
 */
public interface WeixinRefundDetailsService {
    
    /**
     * 查询符合条件的列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      partner1Oid:                       String类型，一级服务商Oid，根据此参数精确查询
     *      partner2Oid:                       String类型，二级服务商Oid，根据此参数精确查询
     *      partner3Oid:                       String类型，三级服务商Oid，根据此参数精确查询
     *      partnerEmployeeOid:           String类型，业务员Oid，根据此参数精确查询
     *      dealerOid:                           String类型，商户Oid，根据此参数精确查询
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
     * @return Map
     * <pre>
     *   refundList：交易List<WeixinRefundDetailsVO>
     *   total：合计WeixinPayTotalVO
     *   <pre>
     */
    public Map<String, Object> doJoinTransQueryWeixinRefundDetails(Map<String, Object> paramMap, int startIndex, int maxResult);
    
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
     *      partnerEmployeeId:     String类型，业务员ID，根据此参数模糊查询
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
    public int doJoinTransQueryWeixinRefundDetailsCount(Map<String, Object> paramMap);
    
    /**
     * 创建退款明细
     * @param weixinPayDetails 要退款的支付订单
     * @param creator 操作名称
     * @param operatorUserOid 操作用户Oid
     * @param logFunctionOid 日志记录项Oid
     * @return 保存成功返回保存后的退款明细VO
     */
    public WeixinRefundDetailsVO doTransCreateRefundDetails(WeixinPayDetails weixinPayDetails, String creator, String operatorUserOid, String logFunctionOid);

    
}
