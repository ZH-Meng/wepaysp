package com.zbsp.wepaysp.api.service.report;

import java.util.List;
import java.util.Map;

import com.zbsp.wepaysp.vo.report.RptDealerStatVO;

/**
 * 分润计算/资金结算（含服务商、服务商员工、商户、商户员工不同角色访问，分日期、月份查询模式）
 * 
 * @author 孟郑宏
 */
public interface RptDealerStatService {

    /**
     * 查询符合条件的代理商分润计算列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      queryType:                      String类型，查询类型，day：查询 RptDealerStatDay，month：查询 RptDealerStatMonth
     *      beginTime:                      Date类型，交易开始时间，根据此参数模糊查询
     *      endTime:                         Date类型，交易截止时间，根据此参数模糊查询
     *      partnerLevel:                   String类型，服务商级别，用来判断partnernOid，n 为等级
     *      partnerOid:                     String类型，服务商Oid，根据此参数精确查询
     *      currentPartnerId:             String类型，当前服务商ID，根据此参数精确查询
     *      partnerId:                       String类型，查询服务商ID，根据此参数模糊查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @return 符合条件的信息列表
     */
    public List<RptDealerStatVO> doJoinTransQueryRptDealerStatList4Parnter(Map<String, Object> paramMap, int startIndex, int maxResult);

    /**
     * 统计符合条件的总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      queryType:                      String类型，查询类型，day：查询 RptDealerStatDay，month：查询 RptDealerStatMonth
     *      beginTime:                      Date类型，交易开始时间，根据此参数模糊查询
     *      endTime:                         Date类型，交易截止时间，根据此参数模糊查询
     *      partnerLevel:                   String类型，服务商级别，用来判断partnernOid，n 为等级
     *      partnerOid:                     String类型，服务商Oid，根据此参数精确查询
     *      currentPartnerId:             String类型，当前服务商ID，根据此参数精确查询
     *      partnerId:                       String类型，查询服务商ID，根据此参数模糊查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQueryRptDealerStatCount4Parnter(Map<String, Object> paramMap);

    /**
     * 查询符合条件的代理商员工分润计算列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      queryType:                      String类型，查询类型，day：查询 RptDealerStatDay，month：查询 RptDealerStatMonth
     *      beginTime:                      Date类型，交易开始时间，根据此参数模糊查询
     *      endTime:                         Date类型，交易截止时间，根据此参数模糊查询
     *      partnerOid:                     String类型，服务商Oid，非空代表代理商查询，
     *      partnerEmployeeId:         String类型，服务商员工ID，根据此参数精确查询
     *      partnerEmployeeOid:       String类型，业务员Oid，非空代表员工查询，
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @return 符合条件的信息列表
     */
    public List<RptDealerStatVO> doJoinTransQueryRptDealerStatList4ParnterE(Map<String, Object> paramMap, int startIndex, int maxResult);
    
    /**
     * 统计符合条件的总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      queryType:                      String类型，查询类型，day：查询 RptDealerStatDay，month：查询 RptDealerStatMonth
     *      beginTime:                      Date类型，交易开始时间，根据此参数模糊查询
     *      endTime:                         Date类型，交易截止时间，根据此参数模糊查询
     *      partnerOid:                     String类型，服务商Oid，非空代表代理商查询，
     *      partnerEmployeeId:         String类型，服务商员工ID，根据此参数精确查询
     *      partnerEmployeeOid:       String类型，业务员Oid，非空代表员工查询，
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQueryRptDealerStatCount4ParnterE(Map<String, Object> paramMap);

    /**
     * 查询符合条件的商户门店结算列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      queryType:                      String类型，查询类型，day：查询 RptDealerStatDay，month：查询 RptDealerStatMonth
     *      beginTime:                      Date类型，交易开始时间，根据此参数模糊查询
     *      endTime:                         Date类型，交易截止时间，根据此参数模糊查询
     *      dealerOid:                      String类型，商户Oid，根据此参数精确查询
     *      storeOid:                        String类型，门店Oid，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @return 符合条件的信息列表
     */
    public List<RptDealerStatVO> doJoinTransQueryRptDealerStatList4Dealer(Map<String, Object> paramMap, int startIndex, int maxResult);

    /**
     * 统计符合条件的总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      queryType:                      String类型，查询类型，day：查询 RptDealerStatDay，month：查询 RptDealerStatMonth
     *      beginTime:                      Date类型，交易开始时间，根据此参数模糊查询
     *      endTime:                         Date类型，交易截止时间，根据此参数模糊查询
     *      dealerOid:                      String类型，商户Oid，根据此参数精确查询
     *      storeOid:                        String类型，门店Oid，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQueryRptDealerStatCount4Dealer(Map<String, Object> paramMap);

    /**
     * 查询符合条件的商户员工结算列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      queryType:                      String类型，查询类型，day：查询 RptDealerStatDay，month：查询 RptDealerStatMonth
     *      beginTime:                      Date类型，交易开始时间，根据此参数模糊查询
     *      endTime:                         Date类型，交易截止时间，根据此参数模糊查询
     *      dealerOid:                      String类型，商户Oid，根据此参数精确查询，非空代表商户查询
     *      storeOid:                        String类型，门店Oid，根据此参数精确查询
     *      dealerEmployeeOid:         String类型，商户员工Oid，根据此参数精确查询，非空代表商户员工查询
     *      dealerEmployeeId:           String类型，商户员工ID，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @return 符合条件的信息列表
     */
    public List<RptDealerStatVO> doJoinTransQueryRptDealerStatList4DealerE(Map<String, Object> paramMap, int startIndex, int maxResult);

    /**
     * 统计符合条件的总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      queryType:                      String类型，查询类型，day：查询 RptDealerStatDay，month：查询 RptDealerStatMonth
     *      beginTime:                      Date类型，交易开始时间，根据此参数模糊查询
     *      endTime:                         Date类型，交易截止时间，根据此参数模糊查询
     *      dealerOid:                      String类型，商户Oid，根据此参数精确查询，非空代表商户查询
     *      storeOid:                        String类型，门店Oid，根据此参数精确查询
     *      dealerEmployeeOid:         String类型，商户员工Oid，根据此参数精确查询，非空代表商户员工查询
     *      dealerEmployeeId:           String类型，商户员工ID，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQueryRptDealerStatCount4DealerE(Map<String, Object> paramMap);

}
