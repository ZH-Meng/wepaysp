package com.zbsp.wepaysp.api.service.report.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.ArrayUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.partner.DealerEmployee;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.po.partner.PartnerEmployee;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.report.RptDealerStatService;
import com.zbsp.wepaysp.vo.report.RptDealerStatVO;

public class RptDealerStatServiceImpl
    extends BaseService
    implements RptDealerStatService {
    
    private String dealerStatDayQueyName; 

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
            sql.append("select d.partnerOid, max(d.partnerId), max(d.partnerName), sum(d.totalAmount), sum(d.totalMoney), sum(d.partnerBonus), max(p.feeRate), sum(d.refundAmount), sum(d.refundMoney), sum(d.payAmount), sum(d.payMoney) from " + poName + " d, Partner p where d.partnerOid = p.iwoid");
            sql.append(" and d.partnerLevel =:PARTNERLEVEL");
            sql.append(" and d.partnerOid =:PARTNEROID");
            sql.append(" and d.startTime >=:BEGINTIME");
            sql.append(" and d.startTime <=:ENDTIME");
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

            sql.append("select d.partner" + subPartnerLevel + "Oid, max(d.partnerId), max(d.partnerName), sum(d.totalAmount), sum(d.totalMoney), sum(d.partnerBonus), max(p.feeRate), sum(d.refundAmount), sum(d.refundMoney), sum(d.payAmount), sum(d.payMoney) from  " + poName + "  d, Partner p where d.partner" + subPartnerLevel + "Oid = p.iwoid");
            sql.append(" and d.partnerLevel > :PARTNERLEVEL");
            sql.append(" and d.partner" + partnerLevel + "Oid =:PARTNERAOID");
            sql.append(" and d.partner" + subPartnerLevel + "Oid =:PARTNERBOID");
            sql.append(" and d.startTime >=:BEGINTIME");
            sql.append(" and d.startTime <=:ENDTIME");
            sql.append(" group by d.partner" + subPartnerLevel + "Oid");
            sqlMap.put("PARTNERLEVEL", partnerLevel);
            sqlMap.put("PARTNERAOID", partnerOid);
            sqlMap.put("PARTNERBOID", partner.getIwoid());
            sqlMap.put("BEGINTIME", beginTime);
            sqlMap.put("ENDTIME", endTime);
            statrList = commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);
        } else if ("selfAndSub".equals(resultFlag)) {
            sql.append("select d.partner_Oid as partner_Oid, max(d.partner_Id), max(d.partner_Name), sum(d.total_Amount), sum(d.total_Money), sum(d.partner_bonus), max(p.fee_rate), sum(d.refund_Amount), sum(d.refund_Money), sum(d.pay_Amount), sum(d.pay_Money) from " + tableName + " d, Partner_t  p where d.partner_Oid = p.iwoid");
            sql.append(" and d.partner_Level =:PARTNERLEVEL");
            sql.append(" and d.partner_Oid =:PARTNEROID");
            sql.append(" and d.start_Time >=:BEGINTIME");
            sql.append(" and d.start_Time <=:ENDTIME");
            sql.append(" group by d.partner_Oid");

            sql.append(" Union all ");

            sql.append("select d.partner" + subPartnerLevel + "_Oid as partner_Oid, max(d.partner_Id), max(d.partner_Name), sum(d.total_Amount), sum(d.total_Money), sum(d.partner_bonus), max(p.fee_rate), sum(d.refund_Amount), sum(d.refund_Money), sum(d.pay_Amount), sum(d.pay_Money) from  " + tableName + "  d, Partner_t  p  where d.partner" + subPartnerLevel + "_Oid = p.iwoid");
            sql.append(" and d.partner_Level > :PARTNERLEVEL");
            sql.append(" and d.partner" + partnerLevel + "_Oid =:PARTNERAOID");
            sql.append(" and d.start_Time >=:BEGINTIME");
            sql.append(" and d.start_Time <=:ENDTIME");
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
                //vo.setPartnerOid(String.valueOf(oArr[0]));
                vo.setPartnerId(String.valueOf(oArr[1]));
                vo.setPartnerName(String.valueOf(oArr[2]));
                if (tableName != null) {
                	vo.setTotalAmount(ArrayUtil.getBigDecimal(oArr, 3, 0).longValue());
                	vo.setTotalMoney(ArrayUtil.getBigDecimal(oArr, 4, 0).longValue());
                } else {
                	vo.setTotalAmount(ArrayUtil.getLong(oArr, 3, 0));
                    vo.setTotalMoney(ArrayUtil.getLong(oArr, 4, 0));
                }
                vo.setPartnerBonus(ArrayUtil.getBigDecimal(oArr, 5, 0));
                vo.setFeeRate(ArrayUtil.getInteger(oArr, 6, 0));
                
                if (tableName != null) {
                    vo.setRefundAmount(ArrayUtil.getBigDecimal(oArr, 7, 0).longValue());
                    vo.setRefundMoney(ArrayUtil.getBigDecimal(oArr, 8, 0).longValue());
                    vo.setPayAmount(ArrayUtil.getBigDecimal(oArr, 9, 0).longValue());
                    vo.setPayMoney(ArrayUtil.getBigDecimal(oArr, 10, 0).longValue());
                } else {
                    vo.setRefundAmount(ArrayUtil.getLong(oArr, 7, 0));
                    vo.setRefundMoney(ArrayUtil.getLong(oArr, 8, 0));
                    vo.setPayAmount(ArrayUtil.getLong(oArr, 9, 0));
                    vo.setPayMoney(ArrayUtil.getLong(oArr, 10, 0));
                }
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
            sql.append(" and d.startTime <=:ENDTIME");
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
            sql.append(" and d.startTime <=:ENDTIME");
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
            sql.append(" and d.start_Time <=:ENDTIME");
            //sql.append(" group by d.partner_Oid");

            sql.append(" Union all ");

            sql.append("select count(distinct d.partner" + subPartnerLevel + "_Oid) from  " + tableName + "  d where 1=1");
            sql.append(" and d.partner_Level > :PARTNERLEVEL");
            sql.append(" and d.partner" + partnerLevel + "_Oid =:PARTNERAOID");
            sql.append(" and d.start_Time >=:BEGINTIME");
            sql.append(" and d.start_Time <=:ENDTIME");
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
            sql.append("select max(d.partnerOid), max(d.partnerId), max(d.partnerName), sum(d.totalAmount), sum(d.totalMoney), sum(d.partnerEmployeeBonus), d.partnerEmployeeId, max(d.partnerEmployeeName), max(pe.feeRate), sum(d.refundAmount), sum(d.refundMoney), sum(d.payAmount), sum(d.payMoney) from " + poName + " d, PartnerEmployee pe  where d.partnerEmployeeOid = pe.iwoid");
            sql.append(" and d.partnerOid =:PARTNEROID");
            sql.append(" and d.startTime >=:BEGINTIME");
            sql.append(" and d.startTime <=:ENDTIME");
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
            sql.append("select max(d.partnerOid), max(d.partnerId), max(d.partnerName), sum(d.totalAmount), sum(d.totalMoney), sum(d.partnerEmployeeBonus), max(d.partnerEmployeeId), max(d.partnerEmployeeName), max(pe.feeRate), sum(d.refundAmount), sum(d.refundMoney), sum(d.payAmount), sum(d.payMoney) from " + poName + " d, PartnerEmployee pe where d.partnerEmployeeOid = pe.iwoid");
            sql.append(" and d.partnerEmployeeOid =:PARTNEREMPLOYEEOID");
            sql.append(" and d.startTime >=:BEGINTIME");
            sql.append(" and d.startTime <=:ENDTIME");

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
                //vo.setPartnerOid(String.valueOf(oArr[0]));
                vo.setPartnerId(String.valueOf(oArr[1]));
                vo.setPartnerName(String.valueOf(oArr[2]));
                vo.setTotalAmount(ArrayUtil.getLong(oArr, 3, 0));
                vo.setTotalMoney(ArrayUtil.getLong(oArr, 4, 0));
                vo.setPartnerEmployeeBonus(ArrayUtil.getBigDecimal(oArr, 5, 0));
                vo.setPartnerEmployeeId(String.valueOf(oArr[6]));
                vo.setPartnerEmployeeName(String.valueOf(oArr[7]));
                vo.setFeeRate(ArrayUtil.getInteger(oArr, 8, 0));
                vo.setRefundAmount(ArrayUtil.getLong(oArr, 9, 0));
                vo.setRefundMoney(ArrayUtil.getLong(oArr, 10, 0));
                vo.setPayAmount(ArrayUtil.getLong(oArr, 11, 0));
                vo.setPayMoney(ArrayUtil.getLong(oArr, 12, 0));
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
        sql.append(" and d.startTime <=:ENDTIME");
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
        if (StringUtils.isBlank(dealerOid) && StringUtils.isBlank(storeOid)) {
            throw new IllegalArgumentException("商户Oid和门店Oid 至少一个不能为空");
        }
        
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

        sql.append("select max(d.dealerId), max(d.dealerName), max(d.storeId), max(d.storeName), sum(d.totalAmount), sum(d.totalMoney), sum(d.refundAmount), sum(d.refundMoney), sum(d.payAmount), sum(d.payMoney) from " + poName + " d where 1=1");
        sql.append(" and d.startTime >=:BEGINTIME");
        sql.append(" and d.startTime <=:ENDTIME");
        if (StringUtils.isNotBlank(dealerOid)) {
        	sql.append(" and d.dealerOid =:DEALEROID");
        	sqlMap.put("DEALEROID", dealerOid);
        	if (StringUtils.isNotBlank(storeOid)) {
        		sql.append(" and d.storeOid =:STOREOID");
        		sqlMap.put("STOREOID", storeOid);
        	}
        } else if (StringUtils.isNotBlank(storeOid)) {
        	sql.append(" and d.storeOid =:STOREOID");
    		sqlMap.put("STOREOID", storeOid);
        }
        sql.append(" group by d.dealerOid, d.storeOid");
        sqlMap.put("BEGINTIME", beginTime);
        sqlMap.put("ENDTIME", endTime);
        statrList = commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);

        if (statrList != null && !statrList.isEmpty()) {
            for (Object o : statrList) {
                RptDealerStatVO vo = new RptDealerStatVO();
                Object[] oArr = (Object[]) o;
                vo.setDealerId(String.valueOf(oArr[0]));
                vo.setDealerName(String.valueOf(oArr[1]));
                vo.setStoreId(String.valueOf(oArr[2]));
                vo.setStoreName(String.valueOf(oArr[3]));
                vo.setTotalAmount(ArrayUtil.getLong(oArr, 4, 0));
                vo.setTotalMoney(ArrayUtil.getLong(oArr, 5, 0));
                vo.setRefundAmount(ArrayUtil.getLong(oArr, 6, 0));
                vo.setRefundMoney(ArrayUtil.getLong(oArr, 7, 0));
                vo.setPayAmount(ArrayUtil.getLong(oArr, 8, 0));
                vo.setPayMoney(ArrayUtil.getLong(oArr, 9, 0));
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
        if (StringUtils.isBlank(dealerOid) && StringUtils.isBlank(storeOid)) {
            throw new IllegalArgumentException("商户Oid和门店Oid 至少一个不能为空");
        }

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
        sql.append(" and d.startTime >=:BEGINTIME");
        sql.append(" and d.startTime <=:ENDTIME");
        if (StringUtils.isNotBlank(dealerOid)) {
        	sql.append(" and d.dealerOid =:DEALEROID");
        	sqlMap.put("DEALEROID", dealerOid);
        	if (StringUtils.isNotBlank(storeOid)) {
        		sql.append(" and d.storeOid =:STOREOID");
        		sqlMap.put("STOREOID", storeOid);
        	}
        } else if (StringUtils.isNotBlank(storeOid)) {
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

        if (StringUtils.isBlank(dealerEmployeeOid) && StringUtils.isBlank(dealerOid) && StringUtils.isBlank(storeOid)) {
            throw new IllegalArgumentException("商户Oid、门店Oid和商户员工Oid 至少一个不能为空");
        }

        Map<String, Object> sqlMap = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer();
        @SuppressWarnings("rawtypes")
        List statrList = null;

        sql.append("select max(d.dealerId), max(d.dealerName), max(d.storeId), max(d.storeName), max(d.dealerEmployeeId), max(d.dealerEmployeeName), sum(d.totalAmount), sum(d.totalMoney), sum(d.refundAmount), sum(d.refundMoney), sum(d.payAmount), sum(d.payMoney) from " + poName + " d where 1=1");
        sql.append(" and d.dealerEmployeeOid is not null and d.dealerEmployeeOid <>'' ");// 排除收银员为空
        sql.append(" and d.startTime >=:BEGINTIME");
        sql.append(" and d.startTime <=:ENDTIME");

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
        } else if (StringUtils.isNotBlank(storeOid)) {
        	sql.append(" and d.storeOid =:STOREOID");
            sqlMap.put("STOREOID", storeOid);
            if (StringUtils.isNotBlank(dealerEmployeeId)) {
                // 校验查询商户员工是否属于当前门店
                DealerEmployee dealerEmployee = commonDAO.findObject("from DealerEmployee p where p.dealerEmployeeId='" + dealerEmployeeId + "'", null, false);
                if (dealerEmployee == null || dealerEmployee.getStore() == null || !dealerEmployee.getStore().getIwoid().equals(storeOid)) {
                    throw new NotExistsException("当前门店不存在ID=" + dealerEmployeeId + "的员工");
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
                vo.setDealerId(String.valueOf(oArr[0]));
                vo.setDealerName(String.valueOf(oArr[1]));
                vo.setStoreId(String.valueOf(oArr[2]));
                vo.setStoreName(String.valueOf(oArr[3]));
                vo.setDealerEmployeeId(String.valueOf(oArr[4]));
                vo.setDealerEmployeeName(String.valueOf(oArr[5]));
                vo.setTotalAmount(ArrayUtil.getLong(oArr, 6, 0));
                vo.setTotalMoney(ArrayUtil.getLong(oArr, 7, 0));
                vo.setRefundAmount(ArrayUtil.getLong(oArr, 8, 0));
                vo.setRefundMoney(ArrayUtil.getLong(oArr, 9, 0));
                vo.setPayAmount(ArrayUtil.getLong(oArr, 10, 0));
                vo.setPayMoney(ArrayUtil.getLong(oArr, 11, 0));
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

        if (StringUtils.isBlank(dealerEmployeeOid) && StringUtils.isBlank(dealerOid) && StringUtils.isBlank(storeOid)) {
            throw new IllegalArgumentException("商户Oid、门店Oid和商户员工Oid 至少一个不能为空");
        }

        Map<String, Object> sqlMap = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer();

        sql.append("select count(distinct d.dealerEmployeeOid) from " + poName + " d where 1=1");
        sql.append(" and d.dealerEmployeeOid is not null and d.dealerEmployeeOid <>'' ");// 排除收银员为空
        sql.append(" and d.startTime >=:BEGINTIME");
        sql.append(" and d.startTime <=:ENDTIME");

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
        } else if (StringUtils.isNotBlank(storeOid)) {
        	sql.append(" and d.storeOid =:STOREOID");
            sqlMap.put("STOREOID", storeOid);
            if (StringUtils.isNotBlank(dealerEmployeeId)) {
                // 校验查询商户员工是否属于当前门店
                DealerEmployee dealerEmployee = commonDAO.findObject("from DealerEmployee p where p.dealerEmployeeId='" + dealerEmployeeId + "'", null, false);
                if (dealerEmployee == null || dealerEmployee.getStore() == null || !dealerEmployee.getStore().getIwoid().equals(storeOid)) {
                    throw new NotExistsException("当前门店不存在ID=" + dealerEmployeeId + "的员工");
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

    @Override
    public List<RptDealerStatVO> doJoinTransQueryRptDealerStatTodayList4Dealer(Map<String, Object> paramMap, int startIndex, int maxResult) {
        List<RptDealerStatVO> resultList = new ArrayList<RptDealerStatVO>();
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");
        String dealerOid = MapUtils.getString(paramMap, "dealerOid");

        Validator.checkArgument(beginTime == null, "开始时间不能为空");
        Validator.checkArgument(endTime == null, "结束时间不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerOid), "dealerOid不能为空");
        
        Map<String, Object> sqlMap = new HashMap<String, Object>();
        
        sqlMap.put("DEALEROID", dealerOid);
        sqlMap.put("BEGINTIME", beginTime);
        sqlMap.put("ENDTIME", endTime);
        @SuppressWarnings("rawtypes")
        List statrList  = commonDAO.findObjectListByNamedQuery(dealerStatDayQueyName, sqlMap);

        if (statrList != null && !statrList.isEmpty()) {
            for (Object o : statrList) {
                RptDealerStatVO vo = new RptDealerStatVO();
                
                Object[] oArr = (Object[]) o;
                vo.setPayAmount(ArrayUtil.getLong(oArr, 0, 0));
                vo.setPayMoney(ArrayUtil.getLong(oArr, 1, 0));
                vo.setRefundAmount(ArrayUtil.getLong(oArr, 2, 0));
                vo.setRefundMoney(ArrayUtil.getLong(oArr, 3, 0));
                vo.setTotalMoney(ArrayUtil.getLong(oArr, 4, 0));
                vo.setDealerId(String.valueOf(oArr[5]));
                vo.setDealerName(String.valueOf(oArr[6]));
                vo.setStoreId(String.valueOf(oArr[7]));
                vo.setStoreName(String.valueOf(oArr[8]));
                
                /*vo.setDealerOid(String.valueOf(oArr[9]));
                vo.setStoreOid(String.valueOf(oArr[10]));*/
                resultList.add(vo);
            }
        }

        return resultList;
    }

    @Override
    public List<RptDealerStatVO> doJoinTransQueryRptDealerStatTodayList4DealerE(Map<String, Object> paramMap, int i, int j) {
        List<RptDealerStatVO> resultList = new ArrayList<RptDealerStatVO>();
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");
        String queryType = MapUtils.getString(paramMap, "queryType");
        String dealerEmployeeOid = MapUtils.getString(paramMap, "dealerEmployeeOid");

        Validator.checkArgument(StringUtils.isBlank(queryType), "查询方式不能为空");
        Validator.checkArgument(beginTime == null, "开始时间不能为空");
        Validator.checkArgument(endTime == null, "结束时间不能为空");
        Validator.checkArgument(StringUtils.isBlank(dealerEmployeeOid), "dealerEmployeeOid不能为空");

        Map<String, Object> sqlMap = new HashMap<String, Object>();


        return resultList;
    }

    public void setDealerStatDayQueyName(String dealerStatDayQueyName) {
        this.dealerStatDayQueyName = dealerStatDayQueyName;
    }

}
