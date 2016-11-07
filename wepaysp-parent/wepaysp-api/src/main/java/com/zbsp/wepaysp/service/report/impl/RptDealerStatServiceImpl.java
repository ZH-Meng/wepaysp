package com.zbsp.wepaysp.service.report.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.partner.DealerEmployee;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.po.partner.PartnerEmployee;
import com.zbsp.wepaysp.service.BaseService;
import com.zbsp.wepaysp.service.report.RptDealerStatService;
import com.zbsp.wepaysp.vo.report.RptDealerStatVO;

public class RptDealerStatServiceImpl
    extends BaseService
    implements RptDealerStatService {

    @Override
    public List<RptDealerStatVO> doJoinTransQueryRptDealerStatList4Parnter(Map<String, Object> paramMap, int startIndex, int maxResult) {
        List<RptDealerStatVO> resultList = new ArrayList<RptDealerStatVO>();
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");

        String partnerOid = MapUtils.getString(paramMap, "partnerOid");
        Integer partnerL = MapUtils.getInteger(paramMap, "partnerLevel");
        String currentPartnerId = MapUtils.getString(paramMap, "currentPartnerId");

        String partnerId = MapUtils.getString(paramMap, "partnerId");

        String queryType = MapUtils.getString(paramMap, "queryType");
        Validator.checkArgument(StringUtils.isBlank(queryType), "查询方式不能为空");
        Validator.checkArgument(beginTime == null, "开始时间不能为空");
        Validator.checkArgument(endTime == null, "结束时间不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerOid), "服务商Oid不能为空");
        Validator.checkArgument(partnerL == null, "服务商级别不能为空");
        //Validator.checkArgument(StringUtils.isBlank(currentPartnerId), "当前服务商ID不能为空");
        int partnerLevel = partnerL.intValue();

        String poName = null;
        String tableName = null;
        if ("day".equals(queryType)) {
            poName = "RptDealerStatDay";
            tableName = "Rpt_Dealer_Stat_Day_t";
        } else if ("month".equals(queryType)) {
            poName = "RptDealerStatMonth";
            tableName = "Rpt_Dealer_Stat_Month_t";
        } else {
            throw new IllegalArgumentException("未指定正确的queryType");
        }

        Map<String, Object> sqlMap = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer();
        @SuppressWarnings("rawtypes")
        List statrList = null;
        String resultFlag = null;
        if (partnerLevel == 3) {
            resultFlag = "self";
        } else if (partnerLevel == 1 || partnerLevel == 2) {
            if (StringUtils.isBlank(partnerId)) {
                resultFlag = "selfAndSub";
            } else if (StringUtils.isNotBlank(partnerId) && partnerId.equals(currentPartnerId)) {
                resultFlag = "self";
            } else if (StringUtils.isNotBlank(partnerId) && !partnerId.equals(currentPartnerId)) {
                resultFlag = "sub";
            }
        } else {
            throw new IllegalArgumentException("未指定正确的partnerLevel");
        }

        int subPartnerLevel = partnerLevel + 1;
        if ("self".equals(resultFlag)) {
            sql.append("select d.partnerOid, max(d.partnerId), max(d.partnerName), sum(d.totalAmount), sum(d.totalMoney), round(sum(d.totalBonus)), max(p.feeRate) from " + poName + " d, Partner p where d.partnerOid = p.iwoid");
            sql.append(" and d.partnerLevel =:PARTNERLEVEL");
            sql.append(" and d.partnerOid =:PARTNEROID");
            sql.append(" and d.startTime >=:BEGINTIME");
            sql.append(" and d.startTime <:ENDTIME");
            sql.append(" group by d.partnerOid");
            sqlMap.put("PARTNERLEVEL", partnerLevel);
            sqlMap.put("PARTNEROID", partnerOid);
            sqlMap.put("BEGINTIME", beginTime);
            sqlMap.put("ENDTIME", endTime);
            statrList = commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);
        } else if ("sub".equals(resultFlag)) {
            // 校验查询服务商是否是当前服务商的直接下级
            Partner partner = commonDAO.findObject("from Partner p where p.partnerId='" + partnerId + "'", null, false);
            if (partner == null || partner.getParentPartner() == null || !partner.getParentPartner().getParentPartner().getIwoid().equals(partnerOid)) {
                throw new NotExistsException("不存在ID=" + partnerId + "的下级代理商");
            }

            sql.append("select d.partner" + subPartnerLevel + "Oid, max(d.partnerId), max(d.partnerName), sum(d.totalAmount), sum(d.totalMoney), round(sum(d.totalBonus)), max(p.feeRate) from  " + poName + "  d, Partner p where d.partner" + subPartnerLevel + "Oid = p.iwoid");
            sql.append(" and d.partnerLevel > :PARTNERLEVEL");
            sql.append(" and d.partner" + partnerLevel + "Oid =:PARTNERAOID");
            sql.append(" and d.partner" + subPartnerLevel + "Oid =:PARTNERBOID");
            sql.append(" and d.startTime >=:BEGINTIME");
            sql.append(" and d.startTime <:ENDTIME");
            sql.append(" group by d.partner" + subPartnerLevel + "Oid");
            sqlMap.put("PARTNERLEVEL", partnerLevel);
            sqlMap.put("PARTNERAOID", partnerOid);
            sqlMap.put("PARTNERBOID", partner.getIwoid());
            sqlMap.put("BEGINTIME", beginTime);
            sqlMap.put("ENDTIME", endTime);
            statrList = commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);
        } else if ("selfAndSub".equals(resultFlag)) {
            sql.append("select d.partner_Oid as partner_Oid, max(d.partner_Id), max(d.partner_Name), sum(d.total_Amount), sum(d.total_Money), round(sum(d.total_Bonus)), max(p.fee_rate) from " + tableName + " d, Partner_t  p where d.partner_Oid = p.iwoid");
            sql.append(" and d.partner_Level =:PARTNERLEVEL");
            sql.append(" and d.partner_Oid =:PARTNEROID");
            sql.append(" and d.start_Time >=:BEGINTIME");
            sql.append(" and d.start_Time <:ENDTIME");
            sql.append(" group by d.partner_Oid");

            sql.append(" Union all ");

            sql.append("select d.partner" + subPartnerLevel + "_Oid as partner_Oid, max(d.partner_Id), max(d.partner_Name), sum(d.total_Amount), sum(d.total_Money), round(sum(d.total_Bonus)), max(p.fee_rate) from  " + tableName + "  d, Partner_t  p  where d.partner" + subPartnerLevel + "_Oid = p.iwoid");
            sql.append(" and d.partner_Level > :PARTNERLEVEL");
            sql.append(" and d.partner" + partnerLevel + "_Oid =:PARTNERAOID");
            sql.append(" and d.start_Time >=:BEGINTIME");
            sql.append(" and d.start_Time <:ENDTIME");
            sql.append(" group by d.partner" + subPartnerLevel + "_Oid");

            sqlMap.put("PARTNERLEVEL", partnerLevel);
            sqlMap.put("PARTNEROID", partnerOid);
            sqlMap.put("BEGINTIME", beginTime);
            sqlMap.put("ENDTIME", endTime);

            sqlMap.put("PARTNERAOID", partnerOid);
            statrList = commonDAO.findObjectList(sql.toString(), sqlMap, true, startIndex, maxResult);
        }

        if (statrList != null && !statrList.isEmpty()) {
            for (Object o : statrList) {
                RptDealerStatVO vo = new RptDealerStatVO();
                Object[] oArr = (Object[]) o;
                // vo.setPartnerOid((String) oArr[0]);
                vo.setPartnerId((String) oArr[1]);
                vo.setPartnerName((String) oArr[2]);
                if (tableName != null) {
                	vo.setTotalAmount(((BigDecimal) oArr[3]).longValue());
                	vo.setTotalMoney(((BigDecimal) oArr[4]).longValue());
                } else {
                	vo.setTotalAmount((Long) oArr[3]);
                    vo.setTotalMoney((Long) oArr[4]);
                }
                vo.setTotalBonus((BigDecimal) oArr[5]);
                vo.setFeeRate((Integer) oArr[6]);
                resultList.add(vo);
            }
        }

        return resultList;
    }

    @Override
    public int doJoinTransQueryRptDealerStatCount4Parnter(Map<String, Object> paramMap) {
        int count = 0;
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");

        String partnerOid = MapUtils.getString(paramMap, "partnerOid");
        Integer partnerL = MapUtils.getInteger(paramMap, "partnerLevel");
        String currentPartnerId = MapUtils.getString(paramMap, "currentPartnerId");

        String partnerId = MapUtils.getString(paramMap, "partnerId");

        String queryType = MapUtils.getString(paramMap, "queryType");
        Validator.checkArgument(StringUtils.isBlank(queryType), "查询方式不能为空");
        Validator.checkArgument(beginTime == null, "开始时间不能为空");
        Validator.checkArgument(endTime == null, "结束时间不能为空");
        Validator.checkArgument(StringUtils.isBlank(partnerOid), "服务商Oid不能为空");
        Validator.checkArgument(partnerL == null, "服务商级别不能为空");
        //Validator.checkArgument(StringUtils.isBlank(currentPartnerId), "当前服务商ID不能为空");
        int partnerLevel = partnerL.intValue();

        String poName = null;
        String tableName = null;
        if ("day".equals(queryType)) {
            poName = "RptDealerStatDay";
            tableName = "Rpt_Dealer_Stat_Day_t";
        } else if ("month".equals(queryType)) {
            poName = "RptDealerStatMonth";
            tableName = "Rpt_Dealer_Stat_Month_t";
        } else {
            throw new IllegalArgumentException("未指定正确的queryType");
        }

        Map<String, Object> sqlMap = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer();
        String resultFlag = null;
        if (partnerLevel == 3) {
            resultFlag = "self";
        } else if (partnerLevel == 1 || partnerLevel == 2) {
            if (StringUtils.isBlank(partnerId)) {
                resultFlag = "selfAndSub";
            } else if (StringUtils.isNotBlank(partnerId) && partnerId.equals(currentPartnerId)) {
                resultFlag = "self";
            } else if (StringUtils.isNotBlank(partnerId) && !partnerId.equals(currentPartnerId)) {
                resultFlag = "sub";
            }
        } else {
            throw new IllegalArgumentException("未指定正确的partnerLevel");
        }

        int subPartnerLevel = partnerLevel + 1;
        if ("self".equals(resultFlag)) {
            sql = new StringBuffer("select count(distinct d.partnerOid) from " + poName + " d where 1=1");
            sql.append(" and d.partnerLevel =:PARTNERLEVEL");
            sql.append(" and d.partnerOid =:PARTNEROID");
            sql.append(" and d.startTime >=:BEGINTIME");
            sql.append(" and d.startTime <:ENDTIME");
            //sql.append(" group by d.partnerOid");
            sqlMap.put("PARTNERLEVEL", partnerLevel);
            sqlMap.put("PARTNEROID", partnerOid);
            sqlMap.put("BEGINTIME", beginTime);
            sqlMap.put("ENDTIME", endTime);
            count = commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
        } else if ("sub".equals(resultFlag)) {
            // 校验查询服务商是否是当前服务商的直接下级
            Partner partner = commonDAO.findObject("from Partner p where p.partnerId='" + partnerId + "'", null, false);
            if (partner == null || partner.getParentPartner() == null || !partner.getParentPartner().getParentPartner().getIwoid().equals(partnerOid)) {
                throw new NotExistsException("不存在ID=" + partnerId + "的下级代理商");
            }

            sql.append("select count(distinct d.partner" + subPartnerLevel + "Oid) from  " + poName + "  d where 1=1");
            sql.append(" and d.partnerLevel > :PARTNERLEVEL");
            sql.append(" and d.partner" + partnerLevel + "Oid =:PARTNERAOID");
            sql.append(" and d.partner" + subPartnerLevel + "Oid =:PARTNERBOID");
            sql.append(" and d.startTime >=:BEGINTIME");
            sql.append(" and d.startTime <:ENDTIME");
            //sql.append(" group by d.partner" + subPartnerLevel + "Oid");
            sqlMap.put("PARTNERLEVEL", partnerLevel);
            sqlMap.put("PARTNERAOID", partnerOid);
            sqlMap.put("PARTNERBOID", partner.getIwoid());
            sqlMap.put("BEGINTIME", beginTime);
            sqlMap.put("ENDTIME", endTime);
            count = commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
        } else if ("selfAndSub".equals(resultFlag)) {
            sql.append("select count(distinct d.partner_Oid)  from " + tableName + " d where 1=1");
            sql.append(" and d.partner_Level =:PARTNERLEVEL");
            sql.append(" and d.partner_Oid =:PARTNEROID");
            sql.append(" and d.start_Time >=:BEGINTIME");
            sql.append(" and d.start_Time <:ENDTIME");
            //sql.append(" group by d.partner_Oid");

            sql.append(" Union all ");

            sql.append("select count(distinct d.partner" + subPartnerLevel + "_Oid) from  " + tableName + "  d where 1=1");
            sql.append(" and d.partner_Level > :PARTNERLEVEL");
            sql.append(" and d.partner" + partnerLevel + "_Oid =:PARTNERAOID");
            sql.append(" and d.start_Time >=:BEGINTIME");
            sql.append(" and d.start_Time <:ENDTIME");
            //sql.append(" group by d.partner" + subPartnerLevel + "_Oid");

            sqlMap.put("PARTNERLEVEL", partnerLevel);
            sqlMap.put("PARTNEROID", partnerOid);
            sqlMap.put("BEGINTIME", beginTime);
            sqlMap.put("ENDTIME", endTime);

            sqlMap.put("PARTNERAOID", partnerOid);
            count = commonDAO.queryObjectCount(sql.toString(), sqlMap, true);
        }
        return count;
    }

    @Override
    public List<RptDealerStatVO> doJoinTransQueryRptDealerStatList4ParnterE(Map<String, Object> paramMap, int startIndex, int maxResult) {
        List<RptDealerStatVO> resultList = new ArrayList<RptDealerStatVO>();
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");
        String queryType = MapUtils.getString(paramMap, "queryType");
        String partnerOid = MapUtils.getString(paramMap, "partnerOid");
        String partnerEmployeeId = MapUtils.getString(paramMap, "partnerEmployeeId");
        String partnerEmployeeOid = MapUtils.getString(paramMap, "partnerEmployeeOid");

        Validator.checkArgument(StringUtils.isBlank(queryType), "查询方式不能为空");
        Validator.checkArgument(beginTime == null, "开始时间不能为空");
        Validator.checkArgument(endTime == null, "结束时间不能为空");

        String poName = null;
        if ("day".equals(queryType)) {
            poName = "RptDealerStatDay";
        } else if ("month".equals(queryType)) {
            poName = "RptDealerStatMonth";
        } else {
            throw new IllegalArgumentException("未指定正确的queryType");
        }

        if (StringUtils.isBlank(partnerEmployeeOid) && StringUtils.isBlank(partnerOid)) {
            throw new IllegalArgumentException("服务商Oid和服务商员工Oid 至少一个不能为空");
        }

        Map<String, Object> sqlMap = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer();
        @SuppressWarnings("rawtypes")
        List statrList = null;
        if (StringUtils.isNotBlank(partnerOid)) {
            sql.append("select max(d.partnerOid), max(d.partnerId), max(d.partnerName), sum(d.totalAmount), sum(d.totalMoney), round(sum(d.totalBonus)), d.partnerEmployeeId, max(d.partnerEmployeeName), max(pe.feeRate) from " + poName + " d, PartnerEmployee pe  where d.partnerEmployeeOid = pe.iwoid");
            sql.append(" and d.partnerOid =:PARTNEROID");
            sql.append(" and d.startTime >=:BEGINTIME");
            sql.append(" and d.startTime <:ENDTIME");
            if (StringUtils.isNotBlank(partnerEmployeeId)) {
                // 校验查询服务商员工是否是当前服务商的下属
                PartnerEmployee partnerEmployee = commonDAO.findObject("from PartnerEmployee p where p.partnerEmployeeId='" + partnerEmployeeId + "'", null, false);
                if (partnerEmployee == null || partnerEmployee.getPartner() == null || !partnerEmployee.getPartner().getIwoid().equals(partnerOid)) {
                    throw new NotExistsException("不存在ID=" + partnerEmployeeId + "的员工");
                }
                sql.append(" and d.partnerEmployeeId =:PARTNEREMPLOYEEID");
                sqlMap.put("PARTNEREMPLOYEEID", partnerEmployeeId);
            }
            sql.append(" group by d.partnerEmployeeId");
            sqlMap.put("PARTNEROID", partnerOid);
            sqlMap.put("BEGINTIME", beginTime);
            sqlMap.put("ENDTIME", endTime);
            statrList = commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);
        } else if (StringUtils.isNotBlank(partnerEmployeeOid)) {
            sql.append("select max(d.partnerOid), max(d.partnerId), max(d.partnerName), sum(d.totalAmount), sum(d.totalMoney), round(sum(d.totalBonus)), max(d.partnerEmployeeId), max(d.partnerEmployeeName), max(pe.feeRate) from " + poName + " d, PartnerEmployee pe where d.partnerEmployeeOid = pe.iwoid");
            sql.append(" and d.partnerEmployeeOid =:PARTNEREMPLOYEEOID");
            sql.append(" and d.startTime >=:BEGINTIME");
            sql.append(" and d.startTime <:ENDTIME");

            sql.append(" group by d.partnerEmployeeOid");
            sqlMap.put("PARTNEREMPLOYEEOID", partnerEmployeeOid);
            sqlMap.put("BEGINTIME", beginTime);
            sqlMap.put("ENDTIME", endTime);
            statrList = commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);
        }

        if (statrList != null && !statrList.isEmpty()) {
            for (Object o : statrList) {
                RptDealerStatVO vo = new RptDealerStatVO();
                Object[] oArr = (Object[]) o;
                // vo.setPartnerOid((String) oArr[0]);
                vo.setPartnerId((String) oArr[1]);
                vo.setPartnerName((String) oArr[2]);
                vo.setTotalAmount((Long) oArr[3]);
                vo.setTotalMoney((Long) oArr[4]);
                vo.setTotalBonus((BigDecimal) oArr[5]);
                vo.setPartnerEmployeeId((String) oArr[6]);
                vo.setPartnerEmployeeName((String) oArr[7]);
                vo.setFeeRate((Integer) oArr[8]);
                resultList.add(vo);
            }
        }

        return resultList;
    }

    @Override
    public int doJoinTransQueryRptDealerStatCount4ParnterE(Map<String, Object> paramMap) {
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");
        String queryType = MapUtils.getString(paramMap, "queryType");
        String partnerOid = MapUtils.getString(paramMap, "partnerOid");
        String partnerEmployeeId = MapUtils.getString(paramMap, "partnerEmployeeId");
        String partnerEmployeeOid = MapUtils.getString(paramMap, "partnerEmployeeOid");

        Validator.checkArgument(StringUtils.isBlank(queryType), "查询方式不能为空");
        Validator.checkArgument(beginTime == null, "开始时间不能为空");
        Validator.checkArgument(endTime == null, "结束时间不能为空");

        String poName = null;
        if ("day".equals(queryType)) {
            poName = "RptDealerStatDay";
        } else if ("month".equals(queryType)) {
            poName = "RptDealerStatMonth";
        } else {
            throw new IllegalArgumentException("未指定正确的queryType");
        }

        if (StringUtils.isBlank(partnerEmployeeOid) && StringUtils.isBlank(partnerOid)) {
            throw new IllegalArgumentException("服务商Oid和服务商员工Oid 至少一个不能为空");
        }

        Map<String, Object> sqlMap = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer();
        sql.append("select  count(distinct d.partnerEmployeeOid) from " + poName + " d where 1=1");
        sql.append(" and d.startTime >=:BEGINTIME");
        sql.append(" and d.startTime <:ENDTIME");
        if (StringUtils.isNotBlank(partnerOid)) {
            sql.append(" and d.partnerOid =:PARTNEROID");
            if (StringUtils.isNotBlank(partnerEmployeeId)) {
                // 校验查询服务商员工是否是当前服务商的下属
                PartnerEmployee partnerEmployee = commonDAO.findObject("from PartnerEmployee p where p.partnerEmployeeId='" + partnerEmployeeId + "'", null, false);
                if (partnerEmployee == null || partnerEmployee.getPartner() == null || !partnerEmployee.getPartner().getIwoid().equals(partnerOid)) {
                    throw new NotExistsException("不存在ID=" + partnerEmployeeId + "的员工");
                }
                sql.append(" and d.partnerEmployeeId =:PARTNEREMPLOYEEID");
                sqlMap.put("PARTNEREMPLOYEEID", partnerEmployeeId);
            }
            //sql.append(" group by d.partnerEmployeeId");
            sqlMap.put("PARTNEROID", partnerOid);
        } else if (StringUtils.isNotBlank(partnerEmployeeOid)) {
            sql.append(" and d.partnerEmployeeOid =:PARTNEREMPLOYEEOID");
           // sql.append(" group by d.partnerEmployeeOid");
            sqlMap.put("PARTNEREMPLOYEEOID", partnerEmployeeOid);
        }
        sqlMap.put("BEGINTIME", beginTime);
        sqlMap.put("ENDTIME", endTime);
        
        return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
    }

    @Override
    public List<RptDealerStatVO> doJoinTransQueryRptDealerStatList4Dealer(Map<String, Object> paramMap, int startIndex, int maxResult) {
        List<RptDealerStatVO> resultList = new ArrayList<RptDealerStatVO>();
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");
        String queryType = MapUtils.getString(paramMap, "queryType");
        String dealerOid = MapUtils.getString(paramMap, "dealerOid");
        String storeOid = MapUtils.getString(paramMap, "storeOid");

        Validator.checkArgument(StringUtils.isBlank(queryType), "查询方式不能为空");
        Validator.checkArgument(beginTime == null, "开始时间不能为空");
        Validator.checkArgument(endTime == null, "结束时间不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerOid), "商户Oid不能为空");

        String poName = null;
        if ("day".equals(queryType)) {
            poName = "RptDealerStatDay";
        } else if ("month".equals(queryType)) {
            poName = "RptDealerStatMonth";
        } else {
            throw new IllegalArgumentException("未指定正确的queryType");
        }

        Map<String, Object> sqlMap = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer();
        @SuppressWarnings("rawtypes")
        List statrList = null;

        sql.append("select max(d.dealerId), max(d.dealerName), max(d.storeId), max(d.storeName), sum(d.totalAmount), sum(d.totalMoney) from " + poName + " d where 1=1");
        sql.append(" and d.dealerOid =:DEALEROID");
        sql.append(" and d.startTime >=:BEGINTIME");
        sql.append(" and d.startTime <:ENDTIME");
        if (StringUtils.isNotBlank(storeOid)) {
            sql.append(" and d.storeOid =:STOREOID");
            sqlMap.put("STOREOID", storeOid);
        }
        sql.append(" group by d.dealerOid, d.storeOid");
        sqlMap.put("DEALEROID", dealerOid);
        sqlMap.put("BEGINTIME", beginTime);
        sqlMap.put("ENDTIME", endTime);
        statrList = commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);

        if (statrList != null && !statrList.isEmpty()) {
            for (Object o : statrList) {
                RptDealerStatVO vo = new RptDealerStatVO();
                Object[] oArr = (Object[]) o;
                vo.setDealerId((String) oArr[0]);
                vo.setDealerName((String) oArr[1]);
                vo.setStoreId((String) oArr[2]);
                vo.setStoreName((String) oArr[3]);
                vo.setTotalAmount((Long) oArr[4]);
                vo.setTotalMoney((Long) oArr[5]);
                resultList.add(vo);
            }
        }

        return resultList;
    }

    @Override
    public int doJoinTransQueryRptDealerStatCount4Dealer(Map<String, Object> paramMap) {
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");
        String queryType = MapUtils.getString(paramMap, "queryType");
        String dealerOid = MapUtils.getString(paramMap, "dealerOid");
        String storeOid = MapUtils.getString(paramMap, "storeOid");

        Validator.checkArgument(StringUtils.isBlank(queryType), "查询方式不能为空");
        Validator.checkArgument(beginTime == null, "开始时间不能为空");
        Validator.checkArgument(endTime == null, "结束时间不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerOid), "商户Oid不能为空");

        String poName = null;
        if ("day".equals(queryType)) {
            poName = "RptDealerStatDay";
        } else if ("month".equals(queryType)) {
            poName = "RptDealerStatMonth";
        } else {
            throw new IllegalArgumentException("未指定正确的queryType");
        }

        Map<String, Object> sqlMap = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer();

        sql.append("select count(distinct d.storeOid) from " + poName + " d where 1=1");
        sql.append(" and d.dealerOid =:DEALEROID");
        sql.append(" and d.startTime >=:BEGINTIME");
        sql.append(" and d.startTime <:ENDTIME");
        if (StringUtils.isNotBlank(storeOid)) {
            sql.append(" and d.storeOid =:STOREOID");
            sqlMap.put("STOREOID", storeOid);
        }
        //sql.append(" group by d.dealerOid, d.storeOid");
        sqlMap.put("DEALEROID", dealerOid);
        sqlMap.put("BEGINTIME", beginTime);
        sqlMap.put("ENDTIME", endTime);
        
        return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
    }

    @Override
    public List<RptDealerStatVO> doJoinTransQueryRptDealerStatList4DealerE(Map<String, Object> paramMap, int startIndex, int maxResult) {
        List<RptDealerStatVO> resultList = new ArrayList<RptDealerStatVO>();
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");
        String queryType = MapUtils.getString(paramMap, "queryType");
        String dealerOid = MapUtils.getString(paramMap, "dealerOid");
        String storeOid = MapUtils.getString(paramMap, "storeOid");
        String dealerEmployeeOid = MapUtils.getString(paramMap, "dealerEmployeeOid");
        String dealerEmployeeId = MapUtils.getString(paramMap, "dealerEmployeeId");

        Validator.checkArgument(StringUtils.isBlank(queryType), "查询方式不能为空");
        Validator.checkArgument(beginTime == null, "开始时间不能为空");
        Validator.checkArgument(endTime == null, "结束时间不能为空");

        String poName = null;
        if ("day".equals(queryType)) {
            poName = "RptDealerStatDay";
        } else if ("month".equals(queryType)) {
            poName = "RptDealerStatMonth";
        } else {
            throw new IllegalArgumentException("未指定正确的queryType");
        }

        if (StringUtils.isBlank(dealerEmployeeOid) && StringUtils.isBlank(dealerOid)) {
            throw new IllegalArgumentException("商户Oid和商户员工Oid 至少一个不能为空");
        }

        Map<String, Object> sqlMap = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer();
        @SuppressWarnings("rawtypes")
        List statrList = null;

        sql.append("select max(d.dealerId), max(d.dealerName), max(d.storeId), max(d.storeName), max(d.dealerEmployeeId), max(d.dealerEmployeeName), sum(d.totalAmount), sum(d.totalMoney) from " + poName + " d where 1=1");
        sql.append(" and d.startTime >=:BEGINTIME");
        sql.append(" and d.startTime <:ENDTIME");

        if (StringUtils.isNotBlank(dealerEmployeeOid)) {
            sql.append(" and d.dealerEmployeeOid =:DEALEREMPLOYEEOID");
            sqlMap.put("DEALEREMPLOYEEOID", dealerEmployeeOid);
        } else if (StringUtils.isNotBlank(dealerOid)) {
            sql.append(" and d.dealerOid =:DEALEROID");
            sqlMap.put("DEALEROID", dealerOid);
            if (StringUtils.isNotBlank(storeOid)) {
                sql.append(" and d.storeOid =:STOREOID");
                sqlMap.put("STOREOID", storeOid);
            }

            if (StringUtils.isNotBlank(dealerEmployeeId)) {
                // 校验查询商户员工是否是当前商户的下属
                DealerEmployee dealerEmployee = commonDAO.findObject("from DealerEmployee p where p.dealerEmployeeId='" + dealerEmployeeId + "'", null, false);
                if (dealerEmployee == null || dealerEmployee.getDealer() == null || !dealerEmployee.getDealer().getIwoid().equals(dealerOid)) {
                    throw new NotExistsException("不存在ID=" + dealerEmployeeId + "的员工");
                }
                sql.append(" and d.dealerEmployeeId =:DEALEREMPLOYEEID");
                sqlMap.put("DEALEREMPLOYEEID", dealerEmployeeId);
            }

        }

        sql.append(" group by d.dealerOid, d.storeOid, d.dealerEmployeeOid");
        sqlMap.put("BEGINTIME", beginTime);
        sqlMap.put("ENDTIME", endTime);

        statrList = commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);

        if (statrList != null && !statrList.isEmpty()) {
            for (Object o : statrList) {
                RptDealerStatVO vo = new RptDealerStatVO();
                Object[] oArr = (Object[]) o;
                vo.setDealerId((String) oArr[0]);
                vo.setDealerName((String) oArr[1]);
                vo.setStoreId((String) oArr[2]);
                vo.setStoreName((String) oArr[3]);
                vo.setDealerEmployeeId((String) oArr[4]);
                vo.setDealerEmployeeName((String) oArr[5]);
                vo.setTotalAmount((Long) oArr[6]);
                vo.setTotalMoney((Long) oArr[7]);
                resultList.add(vo);
            }
        }

        return resultList;
    }

    @Override
    public int doJoinTransQueryRptDealerStatCount4DealerE(Map<String, Object> paramMap) {
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");
        String queryType = MapUtils.getString(paramMap, "queryType");
        String dealerOid = MapUtils.getString(paramMap, "dealerOid");
        String storeOid = MapUtils.getString(paramMap, "storeOid");
        String dealerEmployeeOid = MapUtils.getString(paramMap, "dealerEmployeeOid");
        String dealerEmployeeId = MapUtils.getString(paramMap, "dealerEmployeeId");

        Validator.checkArgument(StringUtils.isBlank(queryType), "查询方式不能为空");
        Validator.checkArgument(beginTime == null, "开始时间不能为空");
        Validator.checkArgument(endTime == null, "结束时间不能为空");

        String poName = null;
        if ("day".equals(queryType)) {
            poName = "RptDealerStatDay";
        } else if ("month".equals(queryType)) {
            poName = "RptDealerStatMonth";
        } else {
            throw new IllegalArgumentException("未指定正确的queryType");
        }

        if (StringUtils.isBlank(dealerEmployeeOid) && StringUtils.isBlank(dealerOid)) {
            throw new IllegalArgumentException("商户Oid和商户员工Oid 至少一个不能为空");
        }

        Map<String, Object> sqlMap = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer();

        sql.append("select count(distinct d.dealerEmployeeOid) from " + poName + " d where 1=1");
        sql.append(" and d.startTime >=:BEGINTIME");
        sql.append(" and d.startTime <:ENDTIME");

        if (StringUtils.isNotBlank(dealerEmployeeOid)) {
            sql.append(" and d.dealerEmployeeOid =:DEALEREMPLOYEEOID");
            sqlMap.put("DEALEREMPLOYEEOID", dealerEmployeeOid);
        } else if (StringUtils.isNotBlank(dealerOid)) {
            sql.append(" and d.dealerOid =:DEALEROID");
            sqlMap.put("DEALEROID", dealerOid);
            if (StringUtils.isNotBlank(storeOid)) {
                sql.append(" and d.storeOid =:STOREOID");
                sqlMap.put("STOREOID", storeOid);
            }

            if (StringUtils.isNotBlank(dealerEmployeeId)) {
                // 校验查询商户员工是否是当前商户的下属
                DealerEmployee dealerEmployee = commonDAO.findObject("from DealerEmployee p where p.dealerEmployeeId='" + dealerEmployeeId + "'", null, false);
                if (dealerEmployee == null || dealerEmployee.getDealer() == null || !dealerEmployee.getDealer().getIwoid().equals(dealerOid)) {
                    throw new NotExistsException("不存在ID=" + dealerEmployeeId + "的员工");
                }
                sql.append(" and d.dealerEmployeeId =:DEALEREMPLOYEEID");
                sqlMap.put("DEALEREMPLOYEEID", dealerEmployeeId);
            }

        }

        //sql.append(" group by d.dealerOid, d.storeOid, d.dealerEmployeeOid");
        sqlMap.put("BEGINTIME", beginTime);
        sqlMap.put("ENDTIME", endTime);
        
        return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
    }

}
