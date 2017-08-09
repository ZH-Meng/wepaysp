package com.zbsp.wepaysp.api.service.edu.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.zbsp.alipay.trade.model.ChargeItems;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.edu.AlipayEduBillService;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.edu.AlipayEduBill;
import com.zbsp.wepaysp.po.edu.AlipayEduBill.OrderStatus;
import com.zbsp.wepaysp.vo.edu.AlipayEduBillVO;


public class AlipayEduBillServiceImpl
    extends BaseService
    implements AlipayEduBillService {

    @Override
    public Map<String, Object> doJoinTransQueryAlipayEduBill(Map<String, Object> paramMap, int startIndex, int maxResult) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<AlipayEduBillVO> resultList = new ArrayList<AlipayEduBillVO>();
        StringBuilder jpqlBuilder = new StringBuilder("from AlipayEduBill a where 1=1");
        Map<String, Object> jpqlMap = assembleQueryCondition(jpqlBuilder, paramMap);

        @SuppressWarnings("unchecked")
        List<AlipayEduBill> billList = (List<AlipayEduBill>) commonDAO.findObjectList(jpqlBuilder.toString(), jpqlMap, false, startIndex, maxResult);
        if (billList != null && !billList.isEmpty()) {
            for (AlipayEduBill bill : billList) {
                AlipayEduBillVO billVO = new AlipayEduBillVO();
                BeanCopierUtil.copyProperties(bill, billVO);
                
                List<ChargeItems> chargeItems = JSONUtil.parseArray(bill.getChargeItem(), ChargeItems.class);
                billVO.setChargeItems(chargeItems);
                resultList.add(billVO);
            }
        }
        resultMap.put("billList", resultList);
        return resultMap;
    }

    @Override
    public int doJoinTransQueryAlipayEduBillCount(Map<String, Object> paramMap) {
        StringBuilder jpqlBuilder = new StringBuilder("select count(a.iwoid) from AlipayEduBill a where 1=1");
        Map<String, Object> jpqlMap = assembleQueryCondition(jpqlBuilder, paramMap);
        return commonDAO.queryObjectCount(jpqlBuilder.toString(), jpqlMap, false);
    }
    
    /** 返回jpql查询参数 */
    private Map<String, Object> assembleQueryCondition(StringBuilder jpqlBuilder, Map<String, Object> paramMap) {
        String schoolNo = MapUtils.getString(paramMap, "schoolNo");
        String childName = MapUtils.getString(paramMap, "childName");
        String userName = MapUtils.getString(paramMap, "userName");
        String orderStatus = MapUtils.getString(paramMap, "orderStatus");
        String totalBillOid = MapUtils.getString(paramMap, "totalBillOid");// 总账单Oid

        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(schoolNo)) {
            jpqlBuilder.append(" and a.schoolNo=:SCHOOLNO");
            jpqlMap.put("SCHOOLNO", schoolNo);
        }
        if (StringUtils.isNotBlank(totalBillOid)) {
            jpqlBuilder.append(" and a.alipayEduTotalBillOid=:TOTALBILLOID");
            jpqlMap.put("TOTALBILLOID", totalBillOid);
        }
        if (StringUtils.isNotBlank(childName)) {
            jpqlBuilder.append(" and a.childName like :CHILDNAME");
            jpqlMap.put("CHILDNAME", "%" + childName + "%");
        }
        if (StringUtils.isNotBlank(userName)) {
            jpqlBuilder.append(" and a.userName like :USERNAME");
            jpqlMap.put("USERNAME", "%" + userName + "%");
        }
        if (StringUtils.isNotBlank(orderStatus)) {
            jpqlBuilder.append(" and a.orderStatus=:ORDERSTATUS");
            jpqlMap.put("ORDERSTATUS", orderStatus);
        }
        return jpqlMap;
    }

    @Override
    public void doTransBatchSaveAlipayEduBills(List<AlipayEduBill> billList) {
        Validator.checkArgument(billList == null, "billList 不能为空");
        commonDAO.saveList(billList, 100);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<AlipayEduBill> doJoinTransQueryAlipayEduBillByStatus(OrderStatus status) {
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from AlipayEduBill a where 1=1";
        if (status != null) {
            jpql += " and a.orderStatus=:STATUS";
            jpqlMap.put("STATUS", status.name());
        }
        return (List<AlipayEduBill>) commonDAO.findObjectList(jpql, jpqlMap, false);
    }

    @Override
    public void doTransUpdateAlipayEduBill(AlipayEduBill bill) {
        Validator.checkArgument(bill == null, "bill 不能为空");
        commonDAO.update(bill);
    }

}
