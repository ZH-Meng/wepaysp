package com.zbsp.wepaysp.api.service.edu.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.edu.AlipayEduTotalBillService;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.po.edu.AlipayEduTotalBill;
import com.zbsp.wepaysp.vo.edu.AlipayEduTotalBillVO;

public class AlipayEduTotalBillServiceImpl
    extends BaseService
    implements AlipayEduTotalBillService {

    @SuppressWarnings("unchecked")
    @Override
    public List<AlipayEduTotalBillVO> doJoinTransQueryAlipayEduTotalBill(Map<String, Object> paramMap, int startIndex, int maxResult) {
        List<AlipayEduTotalBillVO> resultList = new ArrayList<AlipayEduTotalBillVO>();
        StringBuilder jpqlBuilder = new StringBuilder("from AlipayEduTotalBill a where 1=1");
        Map<String, Object> jpqlMap = assembleQueryCondition(jpqlBuilder, paramMap);

        List<AlipayEduTotalBill> totalBillList = (List<AlipayEduTotalBill>) commonDAO.findObjectList(jpqlBuilder.toString(), jpqlMap, false, startIndex, maxResult);
        if (totalBillList != null && !totalBillList.isEmpty()) {
            for (AlipayEduTotalBill totalBill : totalBillList) {
                AlipayEduTotalBillVO billVO = new AlipayEduTotalBillVO();
                BeanCopierUtil.copyProperties(totalBill, billVO);
                resultList.add(billVO);
            }
        }
        return resultList;
    }

    @Override
    public int doJoinTransQueryAlipayEduTotalBillCount(Map<String, Object> paramMap) {
        StringBuilder jpqlBuilder = new StringBuilder("select count(a.iwoid) from AlipayEduTotalBill a where 1=1");
        Map<String, Object> jpqlMap = assembleQueryCondition(jpqlBuilder, paramMap);
        return commonDAO.queryObjectCount(jpqlBuilder.toString(), jpqlMap, false);
    }

    /** 返回jpql查询参数 */
    private Map<String, Object> assembleQueryCondition(StringBuilder jpqlBuilder, Map<String, Object> paramMap) {
        String schoolNo = MapUtils.getString(paramMap, "schoolNo");
        String billName = MapUtils.getString(paramMap, "billName");
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");

        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(schoolNo)) {
            jpqlBuilder.append(" and a.schoolNo=:SCHOOLNO");
            jpqlMap.put("SCHOOLNO", schoolNo);
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

    @Override
    public Map<String, Object> doTransSaveTotalBill(String billName, String endTime, String excelPath,List<ArrayList<String>> dataList) {
        // TODO Auto-generated method stub
        
        // 检查表头及表格合法性

        // 封装账单数据

        // 保存总账单、批量保存账单明细
        AlipayEduTotalBill totalBill = new AlipayEduTotalBill();
        totalBill.setBillName(billName);
        totalBill.setCloseTime(DateUtil.getDate(endTime, "yyyy-MM-dd"));
        totalBill.setExcelPath(excelPath);
        return null;
    }

}
