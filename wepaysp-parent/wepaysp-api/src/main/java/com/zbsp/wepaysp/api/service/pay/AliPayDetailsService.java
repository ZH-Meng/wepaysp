package com.zbsp.wepaysp.api.service.pay;

import java.util.List;
import java.util.Map;

import javax.persistence.LockModeType;

import com.zbsp.wepaysp.common.constant.SysEnums.TradeStatus;
import com.zbsp.wepaysp.po.pay.AliPayDetails;
import com.zbsp.wepaysp.vo.alipay.AlipayWapPayNotifyVO;
import com.zbsp.wepaysp.vo.pay.AliPayDetailsVO;

/**
 * 支付宝支付明细Service
 * 
 * @author 孟郑宏
 */
public interface AliPayDetailsService {
    
    /***
     * 生成、保存支付明细
     * @param payDetailsVO 支付参数
     * @return 保存的支付明细
     */
    public AliPayDetailsVO doTransCreatePayDetails(AliPayDetailsVO payDetailsVO);

    /**
     * 更新当面付-条码支付结果
     * 
     * @param code 支付宝网关返回码
     * @param subCode 支付宝支付业务码
     * @param payResultVO 支付结果
     * @param tradeStatus 更新状态，如果不为空，则更新成指定状态
     * @return 更新的支付明细
     */
    public AliPayDetailsVO doTransUpdateFace2FacePayResult(String code, String subCode, AliPayDetailsVO payResultVO, Integer tradeStatus);

    /**
     * 更新支付状态
     * @param outTradeNo 系统支付订单号
     * @param tradeStatus 要更新的交易状态
     * @param remark 更新的备注
     */
    public void doTransUpdatePayDetailState(String outTradeNo, int tradeStatus, String remark);
    
    /**
     * 查询符合条件的列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      partner1Oid:                       String类型，一级服务商Oid，根据此参数精确查询
     *      partner2Oid:                       String类型，二级服务商Oid，根据此参数精确查询
     *      partner3Oid:                       String类型，三级服务商Oid，根据此参数精确查询
     *      partnerEmployeeOid:           String类型，业务员Oid，根据此参数精确查询
     *      dealerOid:                           String类型，商户Oid，根据此参数精确查询
     *      storeOid:                           String类型，门店Oid，根据此参数精确查询
     *      dealerEmployeeOid:           String类型，收银员Oid，根据此参数精确查询
     *      partnerEmployeeId:     String类型，业务员ID，根据此参数模糊查询
     *      dealerId:                     String类型，商家ID，根据此参数模糊查询
     *      dealerEmployeeId:       String类型，收银员ID，根据此参数模糊查询
     *      storeId:                      String类型，门店ID，根据此参数模糊查询
     *      beginTime:                       Date类型，交易开始时间，根据此参数精确查询
     *      endTime:                         Date类型，交易截止时间，根据此参数精确查询
     *      payType:                        支付类型，根据此参数精确查询
     *      outTradeNo                      系统支付订单号，根据此参数精确查询
     *      tradeNo                            支付宝支付订单号，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @return Map
     * <pre>
     *   payList：交易List<AliPayDetailsVO>
     *   total：合计 PayTotalVO
     *   <pre>
     */
    public Map<String, Object> doJoinTransQueryAliPayDetails(Map<String, Object> paramMap, int startIndex, int maxResult);
    
    /**
     * 统计符合条件的总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      partner1Oid:                       String类型，一级服务商Oid，根据此参数精确查询
     *      partner2Oid:                       String类型，二级服务商Oid，根据此参数精确查询
     *      partner3Oid:                       String类型，三级服务商Oid，根据此参数精确查询
     *      partnerEmployeeOid:           String类型，业务员Oid，根据此参数精确查询
     *      dealerOid:                           String类型，商户Oid，根据此参数精确查询
     *      storeOid:                           String类型，门店Oid，根据此参数精确查询
     *      dealerEmployeeOid:           String类型，收银员Oid，根据此参数精确查询
     *      partnerEmployeeId:     String类型，业务员Id，根据此参数模糊查询
     *      dealerId:                     String类型，商家ID，根据此参数模糊查询
     *      dealerEmployeeId:       String类型，收银员ID，根据此参数模糊查询
     *      storeId:                      String类型，门店ID，根据此参数模糊查询
     *      beginTime:                       Date类型，交易开始时间，根据此参数精确查询
     *      endTime:                         Date类型，交易截止时间，根据此参数精确查询
     *      payType:                        支付类型，根据此参数精确查询
     *      outTradeNo                      系统支付订单号，根据此参数精确查询
     *      tradeNo                            支付宝支付订单号，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQueryAliPayDetailsCount(Map<String, Object> paramMap);

    /**
     * 根据商户订单号或者支付宝交易号查询交易明细，outTradeNo查询为先，outTradeNo为空或者查询结果为空再以tradeNo查询
     * @param outTradeNo 商户订单号
     * @param tradeNo 支付宝交易号
     * @return AliPayDetailsVO 不存在时返回NULL
     */
    public AliPayDetailsVO doJoinTransQueryAliPayDetailsVOByNum(String outTradeNo, String tradeNo);
    
    /**
     * 根据商户订单号或者支付宝交易号查询交易明细，outTradeNo查询为先，outTradeNo为空或者查询结果为空再以tradeNo查询
     * @param outTradeNo 商户订单号
     * @param tradeNo 支付宝交易号
     * @param lockModeType
     * @return AliPayDetails 不存在时返回NULL
     */
    public AliPayDetails doJoinTransQueryAliPayDetailsByNum(String outTradeNo, String tradeNo, LockModeType lockModeType);

    /**
     * 更新查询交易结果，交易成功时调用，和当面付支付更新结果基本一致，可以考虑合并//TODO
     * @param queryPayResultVO 查询支付结果
     * @return AliPayDetailsVO 更新的支付明细
     */
    public AliPayDetailsVO doTransUpdateQueryTradeSuccessResult(AliPayDetailsVO queryPayResultVO);

    /**
     * 更新异步通知结果
     * @param notifyVO 通知信息
     * @param tradeStatus 指定更新交易的状态
     * @param remark 更新的备注
     */
    public void doTransUpdateNotifyResult(AlipayWapPayNotifyVO notifyVO, TradeStatus tradeStatus, String remark);

    /**更新扫码预下单结果*/
	public void doTransUpdateScanPrecreateResult(String outTradeNo, String code ,String msg, String subCode, String subMsg);
	
    /**
     * 根据交易状态和交易开始时间在当前时间-间隔时间之前的记录
     * @param state 交易状态
     * @param intervalTime 间隔时间，毫秒数
     * @return
     */
    public List<AliPayDetails> doJoinTransQueryAliPayDetailsByState(int[] stateArr, long intervalTime);

}
