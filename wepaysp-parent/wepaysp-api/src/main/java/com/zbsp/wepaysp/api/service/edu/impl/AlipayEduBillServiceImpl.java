package com.zbsp.wepaysp.api.service.edu.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.edu.AlipayEduBillService;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.po.edu.AlipayEduBill;
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
                resultList.add(billVO);
            }
        }
        resultMap.put("billList", resultList);
        return resultMap;
    }

    @Override
    public int doJoinTransQueryAlipayEduBillCount(Map<String, Object> paramMap) {
        StringBuilder jpqlBuilder = new StringBuilder("select count(1) from AlipayEduBill a where 1=1");
        Map<String, Object> jpqlMap = assembleQueryCondition(jpqlBuilder, paramMap);
        return commonDAO.queryObjectCount(jpqlBuilder.toString(), jpqlMap, false);
    }
    
    /** 返回jpql查询参数 */
    private Map<String, Object> assembleQueryCondition(StringBuilder jpqlBuilder, Map<String, Object> paramMap) {
        String schoolNo = MapUtils.getString(paramMap, "schoolNo");
        String billName = MapUtils.getString(paramMap, "billName");
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");
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
        if (StringUtils.isNotBlank(billName)) {
            jpqlBuilder.append(" and a.billName=:BILLNAME");
            jpqlMap.put("BILLNAME", billName);
        }
        if (beginTime != null) {
            jpqlBuilder.append(" and a.sendTime>=:BEGINTIME");
            jpqlMap.put("BEGINTIME", beginTime);
        }
        if (endTime != null) {
            jpqlBuilder.append(" and a.sendTime<:ENDTIME");
            jpqlMap.put("ENDTIME", endTime);
        }
        return jpqlMap;
    }

}
