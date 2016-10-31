package com.zbsp.wepaysp.service.pay.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.service.BaseService;
import com.zbsp.wepaysp.service.manage.SysLogService;
import com.zbsp.wepaysp.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;


public class WeixinPayDetailsServiceImpl
    extends BaseService
    implements WeixinPayDetailsService {
    
    private SysLogService sysLogService;

    @SuppressWarnings("unchecked")
	@Override
    public List<WeixinPayDetailsVO> doJoinTransQueryWeixinPayDetailsList(Map<String, Object> paramMap, int startIndex, int maxResult) {
    	List<WeixinPayDetailsVO> resultList = new ArrayList<WeixinPayDetailsVO>();
 	   
        String partnerEmployeeName = MapUtils.getString(paramMap, "partnerEmployeeName");
        String dealerName = MapUtils.getString(paramMap, "dealerName");
        String storeName = MapUtils.getString(paramMap, "storeName");
        String dealerEmployeeName = MapUtils.getString(paramMap, "dealerEmployeeName");
        
        String partner1Oid = MapUtils.getString(paramMap, "partner1Oid");
        String partner2Oid = MapUtils.getString(paramMap, "partner2Oid");
        String partner3Oid = MapUtils.getString(paramMap, "partner3Oid");
        String partnerEmployeeOid = MapUtils.getString(paramMap, "partnerEmployeeOid");
        String dealerOid = MapUtils.getString(paramMap, "dealerOid");
        String dealerEmployeeOid = MapUtils.getString(paramMap, "dealerEmployeeOid");
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");

        StringBuffer sql = new StringBuffer("select distinct(w) from WeixinPayDetails w, PartnerEmployee pe, Dealer d, Store s, DealerEmployee de where w.partnerEmployee=pe and w.dealer=d and w.store=s and w.dealerEmployee=de");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(partner1Oid)) {
        	sql.append(" and w.partner1Oid = :PARTNER1OID");
        	sqlMap.put("PARTNER1OID", "%" + partner1Oid + "%");
        }
        if (StringUtils.isNotBlank(partner2Oid)) {
        	sql.append(" and w.partner2Oid = :PARTNER2OID");
        	sqlMap.put("PARTNER2OID", "%" + partner2Oid + "%");
        }
        if (StringUtils.isNotBlank(partner3Oid)) {
        	sql.append(" and w.partner3Oid = :PARTNER3OID");
        	sqlMap.put("PARTNER3OID", "%" + partner3Oid + "%");
        }
        if (StringUtils.isNotBlank(partnerEmployeeOid)) {
            sql.append(" and w.partnerEmployee.iwoid = :PARTNEREMPLOYEEOID");
            sqlMap.put("PARTNEREMPLOYEEOID", "%" + partnerEmployeeOid + "%");
        }
        if (StringUtils.isNotBlank(dealerOid)) {
            sql.append(" and w.dealer.iwoid = :DEALEROID");
            sqlMap.put("DEALEROID", "%" + dealerOid + "%");
        }
        if (StringUtils.isNotBlank(dealerEmployeeOid)) {
            sql.append(" and w.dealerEmployee.iwoid = :DEALEREMPLOYEEOID");
            sqlMap.put("DEALEREMPLOYEEOID", dealerEmployeeOid);
        }
        
        if (StringUtils.isNotBlank(partnerEmployeeName)) {
            sql.append(" and w.partnerEmployee.employeeName like :PARTNEREMPLOYEENAME");
            sqlMap.put("PARTNEREMPLOYEENAME", "%" + partnerEmployeeName + "%");
        }
        if (StringUtils.isNotBlank(dealerName)) {
            sql.append(" and w.dealer.company like :DEALERNAME");
            sqlMap.put("DEALERNAME", "%" + dealerName + "%");
        }
        if (StringUtils.isNotBlank(storeName)) {
            sql.append(" and w.store.storeName like :STORENAME");
            sqlMap.put("STORENAME", "%" + storeName + "%");
        }
        if (StringUtils.isNotBlank(dealerEmployeeName)) {
            sql.append(" and w.dealerEmployee.employeeName like :DEALEREMPLOYEENAME");
            sqlMap.put("DEALEREMPLOYEENAME", "%" + dealerEmployeeName + "%");
        }
        
        if (beginTime != null ) {
        	sql.append(" and w.timeEnd >=:BEGINTIME ");
            sqlMap.put("BEGINTIME", beginTime);
        }
        if (endTime != null ) {
        	sql.append(" and w.timeEnd <:ENDTIME ");
            sqlMap.put("ENDTIME", endTime);
        }

        sql.append(" order by w.timeEnd desc");
        List<WeixinPayDetails> weixinPayDetailsList = (List<WeixinPayDetails>) commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);
        
        if(weixinPayDetailsList != null && weixinPayDetailsList.isEmpty()) {
        	for (WeixinPayDetails weixinPayDetails : weixinPayDetailsList) {
        		WeixinPayDetailsVO vo = new WeixinPayDetailsVO();
        		BeanCopierUtil.copyProperties(weixinPayDetails, vo);
        		vo.setPartnerName(weixinPayDetails.getPartner().getCompany());
        		vo.setPartnerEmployeeName(weixinPayDetails.getPartnerEmployee().getEmployeeName());
        		vo.setDealerName(weixinPayDetails.getDealer().getCompany());
        		vo.setStoreName(weixinPayDetails.getStore().getStoreName());
        		vo.setDealerEmployeeName(weixinPayDetails.getDealerEmployee().getEmployeeName());
        		vo.setRefundEmployeeName(weixinPayDetails.getDealerEmployee().getEmployeeName());// 退款人
        		resultList.add(vo);
        	}
        }
        
        return resultList;
    }

    @Override
    public int doJoinTransQueryWeixinPayDetailsCount(Map<String, Object> paramMap) {
    	List<WeixinPayDetailsVO> resultList = new ArrayList<WeixinPayDetailsVO>();
  	   
        String partnerEmployeeName = MapUtils.getString(paramMap, "partnerEmployeeName");
        String dealerName = MapUtils.getString(paramMap, "dealerName");
        String storeName = MapUtils.getString(paramMap, "storeName");
        String dealerEmployeeName = MapUtils.getString(paramMap, "dealerEmployeeName");
        
        String partner1Oid = MapUtils.getString(paramMap, "partner1Oid");
        String partner2Oid = MapUtils.getString(paramMap, "partner2Oid");
        String partner3Oid = MapUtils.getString(paramMap, "partner3Oid");
        String partnerEmployeeOid = MapUtils.getString(paramMap, "partnerEmployeeOid");
        String dealerOid = MapUtils.getString(paramMap, "dealerOid");
        String dealerEmployeeOid = MapUtils.getString(paramMap, "dealerEmployeeOid");
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");

        StringBuffer sql = new StringBuffer("select count(distinct w.iwoid) from WeixinPayDetails w, PartnerEmployee pe, Dealer d, Store s, DealerEmployee de where w.partnerEmployee=pe and w.dealer=d and w.store=s and w.dealerEmployee=de");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(partner1Oid)) {
        	sql.append(" and w.partner1Oid = :PARTNER1OID");
        	sqlMap.put("PARTNER1OID", "%" + partner1Oid + "%");
        }
        if (StringUtils.isNotBlank(partner2Oid)) {
        	sql.append(" and w.partner2Oid = :PARTNER2OID");
        	sqlMap.put("PARTNER2OID", "%" + partner2Oid + "%");
        }
        if (StringUtils.isNotBlank(partner3Oid)) {
        	sql.append(" and w.partner3Oid = :PARTNER3OID");
        	sqlMap.put("PARTNER3OID", "%" + partner3Oid + "%");
        }
        if (StringUtils.isNotBlank(partnerEmployeeOid)) {
            sql.append(" and w.partnerEmployee.iwoid = :PARTNEREMPLOYEEOID");
            sqlMap.put("PARTNEREMPLOYEEOID", "%" + partnerEmployeeOid + "%");
        }
        if (StringUtils.isNotBlank(dealerOid)) {
            sql.append(" and w.dealer.iwoid = :DEALEROID");
            sqlMap.put("DEALEROID", "%" + dealerOid + "%");
        }
        if (StringUtils.isNotBlank(dealerEmployeeOid)) {
            sql.append(" and w.dealerEmployee.iwoid = :DEALEREMPLOYEEOID");
            sqlMap.put("DEALEREMPLOYEEOID", dealerEmployeeOid);
        }
        
        if (StringUtils.isNotBlank(partnerEmployeeName)) {
            sql.append(" and w.partnerEmployee.employeeName like :PARTNEREMPLOYEENAME");
            sqlMap.put("PARTNEREMPLOYEENAME", "%" + partnerEmployeeName + "%");
        }
        if (StringUtils.isNotBlank(dealerName)) {
            sql.append(" and w.dealer.company like :DEALERNAME");
            sqlMap.put("DEALERNAME", "%" + dealerName + "%");
        }
        if (StringUtils.isNotBlank(storeName)) {
            sql.append(" and w.store.storeName like :STORENAME");
            sqlMap.put("STORENAME", "%" + storeName + "%");
        }
        if (StringUtils.isNotBlank(dealerEmployeeName)) {
            sql.append(" and w.dealerEmployee.employeeName like :DEALEREMPLOYEENAME");
            sqlMap.put("DEALEREMPLOYEENAME", "%" + dealerEmployeeName + "%");
        }
        
        if (beginTime != null ) {
        	sql.append(" and w.timeEnd >=:BEGINTIME ");
            sqlMap.put("BEGINTIME", beginTime);
        }
        if (endTime != null ) {
        	sql.append(" and w.timeEnd <:ENDTIME ");
            sqlMap.put("ENDTIME", endTime);
        }
        
        return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }
    
}
