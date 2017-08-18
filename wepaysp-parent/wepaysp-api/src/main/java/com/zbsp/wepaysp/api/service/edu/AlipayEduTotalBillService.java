package com.zbsp.wepaysp.api.service.edu;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zbsp.wepaysp.po.edu.AlipayEduTotalBill;
import com.zbsp.wepaysp.po.edu.AlipayEduTotalBill.OrderStatus;
import com.zbsp.wepaysp.vo.edu.AlipayEduTotalBillVO;

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
     * @return List<AlipayEduTotalBillVO>
     */
    public List<AlipayEduTotalBillVO> doJoinTransQueryAlipayEduTotalBill(Map<String, Object> paramMap, int startIndex, int maxResult);
    
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

    /**
     * 保存缴费账单及明细（检查合法后方可保存）
     * 
     * @param schoolNo 学校唯一编码，必填
     * @param billName 缴费账单名称，必填
     * @param endTime 过期时间，非必填
     * @param excelPath excel账单文件存储路径，必填
     * @param dataList excel内容数据，必填
     * @return
     */
    public Map<String, Object> doTransSaveTotalBill(String schoolNo, String billName, String endTime, String excelPath, List<ArrayList<String>> dataList);

    public AlipayEduTotalBillVO doJoinTransQueryAlipayEduTotalBillByOid(String totalBillOid);

    /**
     * 更新缴费账单为已发送
     * @param totalBillOids
     * @param time
     */
    public void doTransTotalBillSent(Set<String> totalBillOids, Date time);
    
    /**
     * 查询有效分钟内待发送的缴费账单
     * @param validMins
     * @return
     */
    public List<AlipayEduTotalBill> doJoinTransQueryTotalBillOfWaitingSend(Integer validMins);

    public void doTransUpdateTotalBillList(List<AlipayEduTotalBill> sendSuceessList);
    
}
