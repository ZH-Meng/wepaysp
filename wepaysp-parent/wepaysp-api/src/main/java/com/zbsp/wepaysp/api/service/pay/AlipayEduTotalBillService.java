package com.zbsp.wepaysp.api.service.pay;

import java.util.Map;

/**
 * 教育缴费总账单服务 
 * @author mengzh
 */
public interface AlipayEduTotalBillService {

    /**
     * 查询符合条件的列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      schoolNo:                     String类型，学校唯一编码，根据此参数精确查询
     *      billName:       				String类型，账单名称，根据此参数模糊查询
     *      beginTime:                    Date类型，交易开始时间，根据此参数精确查询
     *      endTime:                       Date类型，交易截止时间，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @return Map
     * <pre>
     *   billList：交易List<AlipayEduTotalBillVO>
     *   <pre>
     */
    public Map<String, Object> doJoinTransQueryAlipayEduTotalBill(Map<String, Object> paramMap, int startIndex, int maxResult);
    
    /**
     * 统计符合条件的总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      schoolNo:                     String类型，学校唯一编码，根据此参数精确查询
     *      billName:       				String类型，账单名称，根据此参数模糊查询
     *      beginTime:                    Date类型，交易开始时间，根据此参数精确查询
     *      endTime:                       Date类型，交易截止时间，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQueryAlipayEduTotalBillCount(Map<String, Object> paramMap);
    
}
