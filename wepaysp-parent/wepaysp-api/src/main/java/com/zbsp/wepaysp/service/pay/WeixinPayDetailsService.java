package com.zbsp.wepaysp.service.pay;

import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;

/**
 * 微信交易明细service
 */
public interface WeixinPayDetailsService {
    
    /**
     * 查询符合条件的列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      partner1Oid:                       String类型，一级服务商Oid，根据此参数模糊查询
     *      partner2Oid:                       String类型，二级服务商Oid，根据此参数模糊查询
     *      partner3Oid:                       String类型，三级服务商Oid，根据此参数模糊查询
     *      partnerEmployeeName:     String类型，业务员姓名，根据此参数模糊查询
     *      dealerName:                     String类型，商家姓名，根据此参数模糊查询
     *      dealerEmployeeName:       String类型，收银员姓名，根据此参数模糊查询
     *      storeName:                      String类型，门店名称，根据此参数模糊查询
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
     *      partner1Oid:                       String类型，一级服务商Oid，根据此参数模糊查询
     *      partner2Oid:                       String类型，二级服务商Oid，根据此参数模糊查询
     *      partner3Oid:                       String类型，三级服务商Oid，根据此参数模糊查询
     *      partnerEmployeeName:     String类型，业务员姓名，根据此参数模糊查询
     *      dealerName:                     String类型，商家姓名，根据此参数模糊查询
     *      dealerEmployeeName:       String类型，收银员姓名，根据此参数模糊查询
     *      storeName:                      String类型，门店名称，根据此参数模糊查询
     *      beginTime:                       Date类型，交易开始时间，根据此参数模糊查询
     *      endTime:                         Date类型，交易截止时间，根据此参数模糊查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQueryWeixinPayDetailsCount(Map<String, Object> paramMap);
    
}
