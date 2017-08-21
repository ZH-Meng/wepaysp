package com.zbsp.wepaysp.api.service.edu;

import java.util.Map;

import com.zbsp.wepaysp.po.edu.AlipayEduBill;
import com.zbsp.wepaysp.po.edu.AlipayEduBill.OrderStatus;
import com.zbsp.wepaysp.po.edu.AlipayEduNotify;
import com.zbsp.wepaysp.vo.edu.AlipayEduBillVO;

import java.util.List;

/**
 * 教育缴费账单明细服务 
 * @author mengzh
 */
public interface AlipayEduBillService {

    /**
     * 查询符合条件的列表，查询结果按最后修改时间倒序排列.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      childName:                    String类型，学生姓名，根据此参数模糊查询
     *      userName:       				String类型，家长姓名，根据此参数模糊查询
     *      orderStatus:                  String类型，账单状态，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @param startIndex 记录起始位置
     * @param maxResult 返回记录最大数
     * @return 交易List<AlipayEduBillVO>
     */
    public List<AlipayEduBillVO> doJoinTransQueryAlipayEduBill(Map<String, Object> paramMap, int startIndex, int maxResult);
    
    /**
     * 统计符合条件的总数.
     * 查询参数Map中key的取值如下：
     * <pre>
     *      childName:                    String类型，学生姓名，根据此参数模糊查询
     *      userName:       				String类型，家长姓名，根据此参数模糊查询
     *      orderStatus:                  String类型，账单状态，根据此参数精确查询
     * </pre>
     * 
     * @param paramMap 查询参数
     * @return 符合条件的信息总数
     */
    public int doJoinTransQueryAlipayEduBillCount(Map<String, Object> paramMap);
    
    /**批量保存账单明细*/
    public void doTransBatchSaveAlipayEduBills(List<AlipayEduBill> billList);

    /** 根据状态查找账单明细 */
    public List<AlipayEduBill> doJoinTransQueryAlipayEduBillByStatus(String totalBillOid, OrderStatus status);

    /** 更新账单明细*/
    public void doTransUpdateAlipayEduBill(AlipayEduBill bill);

    /** 根据通知更新账单状态*/
    public AlipayEduBill doTransUpdateBillByAlipayEduNotify(AlipayEduNotify eduNotify);

	public AlipayEduBill doJoinTransQueryBillByOid(String billOid);

    public void doTransUpdateBillList(List<AlipayEduBill> closeSuccessBillList);
    
}
