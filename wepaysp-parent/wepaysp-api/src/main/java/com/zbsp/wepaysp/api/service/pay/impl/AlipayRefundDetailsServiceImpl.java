package com.zbsp.wepaysp.api.service.pay.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.pay.AlipayRefundDetailsService;
import com.zbsp.wepaysp.common.constant.SysEnums.TradeStatus;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.partner.Dealer;
import com.zbsp.wepaysp.po.partner.DealerEmployee;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.po.partner.PartnerEmployee;
import com.zbsp.wepaysp.po.partner.Store;
import com.zbsp.wepaysp.po.pay.AliPayDetails;
import com.zbsp.wepaysp.po.pay.AlipayRefundDetails;
import com.zbsp.wepaysp.vo.pay.RefundTotalVO;
import com.zbsp.wepaysp.vo.pay.AlipayRefundDetailsVO;


public class AlipayRefundDetailsServiceImpl
    extends BaseService
    implements AlipayRefundDetailsService {

    @Override
    public Map<String, Object> doJoinTransQueryAlipayRefundDetails(Map<String, Object> paramMap, int startIndex, int maxResult) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<AlipayRefundDetailsVO> resultList = new ArrayList<AlipayRefundDetailsVO>();
       
        String partnerEmployeeId = MapUtils.getString(paramMap, "partnerEmployeeId");
        String dealerId = MapUtils.getString(paramMap, "dealerId");
        String storeId = MapUtils.getString(paramMap, "storeId");
        String dealerEmployeeId = MapUtils.getString(paramMap, "dealerEmployeeId");
        
        String partner1Oid = MapUtils.getString(paramMap, "partner1Oid");
        String partner2Oid = MapUtils.getString(paramMap, "partner2Oid");
        String partner3Oid = MapUtils.getString(paramMap, "partner3Oid");
        String partnerEmployeeOid = MapUtils.getString(paramMap, "partnerEmployeeOid");
        String dealerOid = MapUtils.getString(paramMap, "dealerOid");
        String storeOid = MapUtils.getString(paramMap, "storeOid");
        String dealerEmployeeOid = MapUtils.getString(paramMap, "dealerEmployeeOid");
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");
        String outTradeNo = MapUtils.getString(paramMap, "outTradeNo");// 系统单号
        String tradeNo = MapUtils.getString(paramMap, "tradeNo");// 支付宝单号

        // StringBuffer sql = new StringBuffer("select distinct(w) from AlipayRefundDetails w, PartnerEmployee pe, Dealer d, Store s, DealerEmployee de where w.partnerEmployee=pe and w.dealer=d and w.store=s and w.dealerEmployee=de");
        StringBuffer sql = new StringBuffer("select distinct(w) from AlipayRefundDetails w LEFT JOIN w.partner LEFT JOIN w.partnerEmployee LEFT JOIN w.dealer LEFT JOIN w.store LEFT JOIN w.dealerEmployee where 1=1 ");
        // 只查交易成功的
        sql.append(" and w.tradeStatus=1");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(partner1Oid)) {
            sql.append(" and w.partner1Oid = :PARTNER1OID");
            sqlMap.put("PARTNER1OID", partner1Oid);
        }
        if (StringUtils.isNotBlank(partner2Oid)) {
            sql.append(" and w.partner2Oid = :PARTNER2OID");
            sqlMap.put("PARTNER2OID", partner2Oid);
        }
        if (StringUtils.isNotBlank(partner3Oid)) {
            sql.append(" and w.partner3Oid = :PARTNER3OID");
            sqlMap.put("PARTNER3OID", partner3Oid);
        }
        if (StringUtils.isNotBlank(partnerEmployeeOid)) {
            sql.append(" and w.partnerEmployee.iwoid = :PARTNEREMPLOYEEOID");
            sqlMap.put("PARTNEREMPLOYEEOID", partnerEmployeeOid);
        }
        if (StringUtils.isNotBlank(dealerOid)) {
            sql.append(" and w.dealer.iwoid = :DEALEROID");
            sqlMap.put("DEALEROID", dealerOid);
        }
        if (StringUtils.isNotBlank(storeOid)) {
            sql.append(" and w.store.iwoid = :STOREOID");
            sqlMap.put("STOREOID", storeOid);
        }
        if (StringUtils.isNotBlank(dealerEmployeeOid)) {
            sql.append(" and w.dealerEmployee.iwoid = :DEALEREMPLOYEEOID");
            sqlMap.put("DEALEREMPLOYEEOID", dealerEmployeeOid);
        }
        
        if (StringUtils.isNotBlank(partnerEmployeeId)) {
            sql.append(" and w.partnerEmployee.partnerEmployeeId like :PARTNEREMPLOYEEID");
            sqlMap.put("PARTNEREMPLOYEEID", "%" + partnerEmployeeId + "%");
        }
        if (StringUtils.isNotBlank(dealerId)) {
            sql.append(" and w.dealer.dealerId like :DEALERID");
            sqlMap.put("DEALERID", "%" + dealerId + "%");
        }
        if (StringUtils.isNotBlank(storeId)) {
            sql.append(" and w.store.storeId like :STOREID");
            sqlMap.put("STOREID", "%" + storeId + "%");
        }
        if (StringUtils.isNotBlank(dealerEmployeeId)) {
            sql.append(" and w.dealerEmployee.dealerEmployeeId like :DEALEREMPLOYEEID");
            sqlMap.put("DEALEREMPLOYEEID", "%" + dealerEmployeeId + "%");
        }
        
        if (beginTime != null ) {
            sql.append(" and w.transEndTime >=:BEGINTIME ");
            sqlMap.put("BEGINTIME", beginTime);
        }
        if (endTime != null ) {
            sql.append(" and w.transEndTime <=:ENDTIME ");
            sqlMap.put("ENDTIME", endTime);
        }
        if (StringUtils.isNotBlank(outTradeNo)) {
            sql.append(" and w.outTradeNo = :OUTTRADENO");
            sqlMap.put("OUTTRADENO", outTradeNo);
        }
        if (StringUtils.isNotBlank(tradeNo)) {
            sql.append(" and w.tradeNo = :TRADENO");
            sqlMap.put("TRADENO", tradeNo);
        }
        
        sql.append(" order by w.transBeginTime desc");

        List<AlipayRefundDetails> alipayRefundDetailsList = commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);
        
        Long totalRefundAmount = 0L;
        Long totalRefundMoney = 0L;
        // 总笔数为记录总数，总金额为交易成功的总金额
        if(alipayRefundDetailsList != null && !alipayRefundDetailsList.isEmpty()) {
            for (AlipayRefundDetails alipayRefundDetails : alipayRefundDetailsList) {
                totalRefundAmount++;
                if (alipayRefundDetails.getTradeStatus().intValue() == TradeStatus.TRADE_SUCCESS.getValue()) {
                    totalRefundMoney += alipayRefundDetails.getRefundFee();
                }
                
                AlipayRefundDetailsVO vo = new AlipayRefundDetailsVO();
                //BeanCopierUtil.copyProperties(alipayRefundDetails, vo);
                vo.setOutTradeNo(alipayRefundDetails.getOutTradeNo());
                
                DealerEmployee de = alipayRefundDetails.getDealerEmployee();
                vo.setDealerEmployeeName(de != null ? de.getEmployeeName() : "");
                vo.setDealerEmployeeId(de != null ? de.getDealerEmployeeId() : "");
                
                Store store = alipayRefundDetails.getStore();
                vo.setStoreName(store != null ? store.getStoreName() : (de != null ? de.getStore().getStoreName() : ""));
                vo.setStoreId(store != null ? store.getStoreId() : (de != null ? de.getStore().getStoreId() : ""));
                
                Dealer dealer = alipayRefundDetails.getDealer();
                vo.setDealerName(dealer != null ? dealer.getCompany() : (de != null ? de.getDealer().getCompany() : "" ));
                vo.setDealerId(dealer != null ? dealer.getDealerId() : (de != null ? de.getDealer().getDealerId() : "" ));
                
                PartnerEmployee pe = alipayRefundDetails.getPartnerEmployee();
                vo.setPartnerEmployeeName(pe != null ? pe.getEmployeeName() : (dealer != null ? dealer.getPartnerEmployee().getEmployeeName() : ""));
                vo.setPartnerEmployeeId(pe != null ? pe.getPartnerEmployeeId() : (dealer != null ? dealer.getPartnerEmployee().getPartnerEmployeeId() : ""));
                
                Partner p = alipayRefundDetails.getPartner();
                vo.setPartnerName(p != null ? p.getCompany() : (dealer != null ? dealer.getPartner().getCompany() : ""));
                vo.setPartnerId(p != null ? p.getPartnerId() : (dealer != null ? dealer.getPartner().getPartnerId() : ""));
                
                vo.setRefundEmployeeName(de != null ? de.getEmployeeName() : "");// 退款人
                
                vo.setTotalFee(alipayRefundDetails.getTotalFee());// 订单总金额
                vo.setRefundFee(alipayRefundDetails.getRefundFee());
                vo.setTransEndTime(alipayRefundDetails.getTransEndTime());
                vo.setTradeStatus(alipayRefundDetails.getTradeStatus());
                
                resultList.add(vo);
            }
        }
        
        RefundTotalVO totalVO = new RefundTotalVO();
        totalVO.setTotalRefundAmount(totalRefundAmount);
        totalVO.setTotalRefundMoney(totalRefundMoney);
        resultMap.put("refundList", resultList);
        resultMap.put("total", totalVO);
        return resultMap;
    }

    @Override
    public int doJoinTransQueryAlipayRefundDetailsCount(Map<String, Object> paramMap) {
       
        String partnerEmployeeId = MapUtils.getString(paramMap, "partnerEmployeeId");
        String dealerId = MapUtils.getString(paramMap, "dealerId");
        String storeId = MapUtils.getString(paramMap, "storeId");
        String dealerEmployeeId = MapUtils.getString(paramMap, "dealerEmployeeId");
        
        String partner1Oid = MapUtils.getString(paramMap, "partner1Oid");
        String partner2Oid = MapUtils.getString(paramMap, "partner2Oid");
        String partner3Oid = MapUtils.getString(paramMap, "partner3Oid");
        String partnerEmployeeOid = MapUtils.getString(paramMap, "partnerEmployeeOid");
        String dealerOid = MapUtils.getString(paramMap, "dealerOid");
        String storeOid = MapUtils.getString(paramMap, "storeOid");
        String dealerEmployeeOid = MapUtils.getString(paramMap, "dealerEmployeeOid");
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");
        String outTradeNo = MapUtils.getString(paramMap, "outTradeNo");// 系统单号
        String tradeNo = MapUtils.getString(paramMap, "tradeNo");// 支付宝单号

        // StringBuffer sql = new StringBuffer("select count(distinct w.iwoid) from AlipayRefundDetails w, PartnerEmployee pe, Dealer d, Store s, DealerEmployee de where w.partnerEmployee=pe and w.dealer=d and w.store=s and w.dealerEmployee=de");
        StringBuffer sql = new StringBuffer("select count(distinct w.iwoid) from AlipayRefundDetails w LEFT JOIN w.partner LEFT JOIN w.partnerEmployee LEFT JOIN w.dealer LEFT JOIN w.store LEFT JOIN w.dealerEmployee where 1=1 ");
        // 只查交易成功的
        sql.append(" and w.tradeStatus=1");
        
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(partner1Oid)) {
            sql.append(" and w.partner1Oid = :PARTNER1OID");
            sqlMap.put("PARTNER1OID", partner1Oid);
        }
        if (StringUtils.isNotBlank(partner2Oid)) {
            sql.append(" and w.partner2Oid = :PARTNER2OID");
            sqlMap.put("PARTNER2OID", partner2Oid);
        }
        if (StringUtils.isNotBlank(partner3Oid)) {
            sql.append(" and w.partner3Oid = :PARTNER3OID");
            sqlMap.put("PARTNER3OID", partner3Oid);
        }
        if (StringUtils.isNotBlank(partnerEmployeeOid)) {
            sql.append(" and w.partnerEmployee.iwoid = :PARTNEREMPLOYEEOID");
            sqlMap.put("PARTNEREMPLOYEEOID", partnerEmployeeOid);
        }
        if (StringUtils.isNotBlank(dealerOid)) {
            sql.append(" and w.dealer.iwoid = :DEALEROID");
            sqlMap.put("DEALEROID", dealerOid);
        }
        if (StringUtils.isNotBlank(storeOid)) {
            sql.append(" and w.store.iwoid = :STOREOID");
            sqlMap.put("STOREOID", storeOid);
        }
        if (StringUtils.isNotBlank(dealerEmployeeOid)) {
            sql.append(" and w.dealerEmployee.iwoid = :DEALEREMPLOYEEOID");
            sqlMap.put("DEALEREMPLOYEEOID", dealerEmployeeOid);
        }
        
        if (StringUtils.isNotBlank(partnerEmployeeId)) {
            sql.append(" and w.partnerEmployee.partnerEmployeeId like :PARTNEREMPLOYEEID");
            sqlMap.put("PARTNEREMPLOYEEID", "%" + partnerEmployeeId + "%");
        }
        if (StringUtils.isNotBlank(dealerId)) {
            sql.append(" and w.dealer.dealerId like :DEALERID");
            sqlMap.put("DEALERID", "%" + dealerId + "%");
        }
        if (StringUtils.isNotBlank(storeId)) {
            sql.append(" and w.store.storeId like :STOREID");
            sqlMap.put("STOREID", "%" + storeId + "%");
        }
        if (StringUtils.isNotBlank(dealerEmployeeId)) {
            sql.append(" and w.dealerEmployee.dealerEmployeeId like :DEALEREMPLOYEEID");
            sqlMap.put("DEALEREMPLOYEEID", "%" + dealerEmployeeId + "%");
        }
        
        if (beginTime != null ) {
            sql.append(" and w.transEndTime >=:BEGINTIME ");
            sqlMap.put("BEGINTIME", beginTime);
        }
        if (endTime != null ) {
            sql.append(" and w.transEndTime <=:ENDTIME ");
            sqlMap.put("ENDTIME", endTime);
        }
        if (StringUtils.isNotBlank(outTradeNo)) {
            sql.append(" and w.outTradeNo = :OUTTRADENO");
            sqlMap.put("OUTTRADENO", outTradeNo);
        }
        if (StringUtils.isNotBlank(tradeNo)) {
            sql.append(" and w.tradeNo = :TRADENO");
            sqlMap.put("TRADENO", tradeNo);
        }
        
        return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
    }

    @Override
    public void doTransBatchAdd(List<AlipayRefundDetails> refundList) {
        Validator.checkArgument(refundList == null, "refundList不能为空");
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        for (AlipayRefundDetails refundDetail : refundList) {
            // 查找原交易
            Validator.checkArgument(StringUtils.isBlank(refundDetail.getOutTradeNo()), "outTradeNo不能为空");
            jpqlMap.put("OUTTRADENO", refundDetail.getOutTradeNo());
            AliPayDetails payDetail = commonDAO.findObject("from AliPayDetails a where a.outTradeNo=:OUTTRADENO", jpqlMap, false);
            if (payDetail == null) {
                throw new NotExistsException("系统中不存在该支付宝支付订单, ouTradeNo=" + refundDetail.getOutTradeNo());
            }
            refundDetail.setPartner(payDetail.getPartner());
            refundDetail.setPartnerEmployee(payDetail.getPartnerEmployee());
            refundDetail.setDealer(payDetail.getDealer());
            refundDetail.setDealerEmployee(payDetail.getDealerEmployee());
            refundDetail.setStore(payDetail.getStore());
            refundDetail.setPartner1Oid(payDetail.getPartner1Oid());
            refundDetail.setPartner2Oid(payDetail.getPartner2Oid());
            refundDetail.setPartner3Oid(payDetail.getPartner3Oid());
            refundDetail.setPartnerLevel(payDetail.getPartnerLevel());
            refundDetail.setAliPayDetails(payDetail);
            refundDetail.setTotalFee(payDetail.getTotalAmount());// 订单金额
        }
        commonDAO.saveList(refundList, 100);
    }

}
