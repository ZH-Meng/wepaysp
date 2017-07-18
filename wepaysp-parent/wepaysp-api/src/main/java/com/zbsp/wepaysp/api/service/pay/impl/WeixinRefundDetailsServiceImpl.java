package com.zbsp.wepaysp.api.service.pay.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.po.partner.Dealer;
import com.zbsp.wepaysp.po.partner.DealerEmployee;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.po.partner.PartnerEmployee;
import com.zbsp.wepaysp.po.partner.Store;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.po.pay.WeixinRefundDetails;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.manage.SysLogService;
import com.zbsp.wepaysp.api.service.pay.WeixinRefundDetailsService;
import com.zbsp.wepaysp.common.config.SysSequenceCode;
import com.zbsp.wepaysp.common.config.SysSequenceMultiple;
import com.zbsp.wepaysp.common.constant.SysEnums;
import com.zbsp.wepaysp.common.constant.SysEnums.TradeStatus;
import com.zbsp.wepaysp.common.constant.WxEnums.ResultCode;
import com.zbsp.wepaysp.common.constant.WxEnums.ReturnCode;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.vo.pay.PayTotalVO;
import com.zbsp.wepaysp.vo.pay.WeixinRefundDetailsVO;


public class WeixinRefundDetailsServiceImpl
    extends BaseService
    implements WeixinRefundDetailsService {
    
    private SysLogService sysLogService;

    @SuppressWarnings("unchecked")
	@Override
    public Map<String, Object> doJoinTransQueryWeixinRefundDetails(Map<String, Object> paramMap, int startIndex, int maxResult) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
    	List<WeixinRefundDetailsVO> resultList = new ArrayList<WeixinRefundDetailsVO>();
 	   
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
        String transactionId = MapUtils.getString(paramMap, "transactionId");// 微信单号

        // StringBuffer sql = new StringBuffer("select distinct(w) from WeixinRefundDetails w, PartnerEmployee pe, Dealer d, Store s, DealerEmployee de where w.partnerEmployee=pe and w.dealer=d and w.store=s and w.dealerEmployee=de");
        StringBuffer sql = new StringBuffer("select distinct(w) from WeixinRefundDetails w LEFT JOIN w.partner LEFT JOIN w.partnerEmployee LEFT JOIN w.dealer LEFT JOIN w.store LEFT JOIN w.dealerEmployee where 1=1 ");
        
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
            sql.append(" and w.transBeginTime >=:BEGINTIME ");
            sqlMap.put("BEGINTIME", beginTime);
        }
        if (endTime != null ) {
            sql.append(" and w.transBeginTime <=:ENDTIME ");
            sqlMap.put("ENDTIME", endTime);
        }
        if (StringUtils.isNotBlank(outTradeNo)) {
            sql.append(" and w.outTradeNo = :OUTTRADENO");
            sqlMap.put("OUTTRADENO", outTradeNo);
        }
        if (StringUtils.isNotBlank(transactionId)) {
            sql.append(" and w.transactionId = :TRANSACTIONID");
            sqlMap.put("TRANSACTIONID", transactionId);
        }
        
        sql.append(" order by w.transBeginTime desc");

        List<WeixinRefundDetails> weixinRefundDetailsList = (List<WeixinRefundDetails>) commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);
        
        Long totalAmount = 0L;
        Long totalMoney = 0L;
        // 总笔数为记录总数，总金额为交易成功的总金额
        if(weixinRefundDetailsList != null && !weixinRefundDetailsList.isEmpty()) {
        	for (WeixinRefundDetails weixinRefundDetails : weixinRefundDetailsList) {
        	    totalAmount++;
                if (weixinRefundDetails.getTradeStatus().intValue() == TradeStatus.TRADE_SUCCESS.getValue()) {
                    totalMoney += weixinRefundDetails.getTotalFee();
                }
                
        		WeixinRefundDetailsVO vo = new WeixinRefundDetailsVO();
        		//BeanCopierUtil.copyProperties(weixinRefundDetails, vo);
        		vo.setOutTradeNo(weixinRefundDetails.getOutTradeNo());
        		
                DealerEmployee de = weixinRefundDetails.getDealerEmployee();
                vo.setDealerEmployeeName(de != null ? de.getEmployeeName() : "");
                vo.setDealerEmployeeId(de != null ? de.getDealerEmployeeId() : "");
                
                Store store = weixinRefundDetails.getStore();
                vo.setStoreName(store != null ? store.getStoreName() : (de != null ? de.getStore().getStoreName() : ""));
                vo.setStoreId(store != null ? store.getStoreId() : (de != null ? de.getStore().getStoreId() : ""));
                
                Dealer dealer = weixinRefundDetails.getDealer();
                vo.setDealerName(dealer != null ? dealer.getCompany() : (de != null ? de.getDealer().getCompany() : "" ));
                vo.setDealerId(dealer != null ? dealer.getDealerId() : (de != null ? de.getDealer().getDealerId() : "" ));
                
                PartnerEmployee pe = weixinRefundDetails.getPartnerEmployee();
                vo.setPartnerEmployeeName(pe != null ? pe.getEmployeeName() : (dealer != null ? dealer.getPartnerEmployee().getEmployeeName() : ""));
                vo.setPartnerEmployeeId(pe != null ? pe.getPartnerEmployeeId() : (dealer != null ? dealer.getPartnerEmployee().getPartnerEmployeeId() : ""));
                
                Partner p = weixinRefundDetails.getPartner();
                vo.setPartnerName(p != null ? p.getCompany() : (dealer != null ? dealer.getPartner().getCompany() : ""));
                vo.setPartnerId(p != null ? p.getPartnerId() : (dealer != null ? dealer.getPartner().getPartnerId() : ""));
        		
        		vo.setRefundEmployeeName(de != null ? de.getEmployeeName() : "");// 退款人
        		
        		vo.setTotalFee(weixinRefundDetails.getTotalFee());
        		vo.setRefundFee(weixinRefundDetails.getRefundFee());
        		vo.setResultCode(weixinRefundDetails.getResultCode());
        		vo.setTransBeginTime(weixinRefundDetails.getTransBeginTime());
        		resultList.add(vo);
        	}
        }
        
        PayTotalVO totalVO = new PayTotalVO();
        totalVO.setTotalAmount(totalAmount);
        totalVO.setTotalMoney(totalMoney);
        resultMap.put("refundList", resultList);
        resultMap.put("total", totalVO);
        return resultMap;
    }

    @Override
    public int doJoinTransQueryWeixinRefundDetailsCount(Map<String, Object> paramMap) {
  	   
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
        String transactionId = MapUtils.getString(paramMap, "transactionId");// 微信单号

        // StringBuffer sql = new StringBuffer("select count(distinct w.iwoid) from WeixinRefundDetails w, PartnerEmployee pe, Dealer d, Store s, DealerEmployee de where w.partnerEmployee=pe and w.dealer=d and w.store=s and w.dealerEmployee=de");
        StringBuffer sql = new StringBuffer("select count(distinct w.iwoid) from WeixinRefundDetails w LEFT JOIN w.partner LEFT JOIN w.partnerEmployee LEFT JOIN w.dealer LEFT JOIN w.store LEFT JOIN w.dealerEmployee where 1=1 ");

        
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
            sql.append(" and w.transBeginTime >=:BEGINTIME ");
            sqlMap.put("BEGINTIME", beginTime);
        }
        if (endTime != null ) {
            sql.append(" and w.transBeginTime <=:ENDTIME ");
            sqlMap.put("ENDTIME", endTime);
        }
        if (StringUtils.isNotBlank(outTradeNo)) {
            sql.append(" and w.outTradeNo = :OUTTRADENO");
            sqlMap.put("OUTTRADENO", outTradeNo);
        }
        if (StringUtils.isNotBlank(transactionId)) {
            sql.append(" and w.transactionId = :TRANSACTIONID");
            sqlMap.put("TRANSACTIONID", transactionId);
        }
        
        return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
    }

	@Override
	public WeixinRefundDetailsVO doTransCreateRefundDetails(WeixinPayDetails weixinPayDetails, String creator,
			String operatorUserOid, String logFunctionOid) {
        Validator.checkArgument(weixinPayDetails == null, "weixinPayDetails不能为空");
        Validator.checkArgument(StringUtils.isBlank(weixinPayDetails.getIwoid()), "weixinPayDetails.iwoid不能为空");

        WeixinRefundDetails refundDetail = new WeixinRefundDetails();
        Map<String, Object> sqlMap = new HashMap<String, Object>();
        // 获取 商户退款ID下一个序列值
        String sql = "select nextval('" + SysSequenceCode.REFUND_ORDER + "') as sequence_value";
        Object seqObj = commonDAO.findObject(sql, sqlMap, true);
        if (seqObj == null) {
            throw new IllegalArgumentException("退款订单Id对应序列记录不存在");
        }
        refundDetail.setOutRefundNo(Generator.generateSequenceYYYYMMddNum((Integer) seqObj, SysSequenceMultiple.REFUND_ORDER));// 商户退款订单号

        /*--------系统服务商、业务员、商户、门店、收银员-------*/
        refundDetail.setWeixinPayDetailsOid(weixinPayDetails.getIwoid());
        refundDetail.setPartner1Oid(weixinPayDetails.getPartner1Oid());
        refundDetail.setPartner2Oid(weixinPayDetails.getPartner2Oid());
        refundDetail.setPartner3Oid(weixinPayDetails.getPartner3Oid());
        refundDetail.setPartnerLevel(weixinPayDetails.getPartnerLevel());
        refundDetail.setPartner(weixinPayDetails.getPartner());
        refundDetail.setDealer(weixinPayDetails.getDealer());
        refundDetail.setStore(weixinPayDetails.getStore());
        refundDetail.setDealerEmployee(weixinPayDetails.getDealerEmployee());
        refundDetail.setTradeStatus(TradeStatus.TRADEING.getValue());
        refundDetail.setTransBeginTime(new Date());
        refundDetail.setCreator(creator);

        refundDetail.setIwoid(Generator.generateIwoid());
        refundDetail.setAppid(weixinPayDetails.getAppid());
        refundDetail.setSubAppid(weixinPayDetails.getSubAppid());
        refundDetail.setMchId(weixinPayDetails.getMchId());
        refundDetail.setSubMchId(weixinPayDetails.getSubMchId());
        refundDetail.setOutTradeNo(weixinPayDetails.getOutTradeNo());
        refundDetail.setTransactionId(weixinPayDetails.getTransactionId());
        refundDetail.setDeviceInfo(weixinPayDetails.getDeviceInfo());
        refundDetail.setRefundType(SysEnums.RefundType.REFUND.getValue());
        refundDetail.setRefundFee(weixinPayDetails.getTotalFee());//FIXME
        commonDAO.save(refundDetail, true);
        WeixinRefundDetailsVO refundVO = new WeixinRefundDetailsVO(); 
        BeanCopierUtil.copyProperties(refundDetail, refundVO);
		return refundVO;
	}

    @Override
    public WeixinRefundDetailsVO doTransCreateReverseDetails(WeixinPayDetails weixinPayDetails) {
        Validator.checkArgument(weixinPayDetails == null, "weixinPayDetails不能为空");
        Validator.checkArgument(StringUtils.isBlank(weixinPayDetails.getIwoid()), "weixinPayDetails.iwoid不能为空");
        WeixinRefundDetails refundDetail = new WeixinRefundDetails();
        /*--------系统服务商、业务员、商户、门店、收银员-------*/
        refundDetail.setWeixinPayDetailsOid(weixinPayDetails.getIwoid());
        refundDetail.setPartner1Oid(weixinPayDetails.getPartner1Oid());
        refundDetail.setPartner2Oid(weixinPayDetails.getPartner2Oid());
        refundDetail.setPartner3Oid(weixinPayDetails.getPartner3Oid());
        refundDetail.setPartnerLevel(weixinPayDetails.getPartnerLevel());
        refundDetail.setPartner(weixinPayDetails.getPartner());
        refundDetail.setDealer(weixinPayDetails.getDealer());
        refundDetail.setStore(weixinPayDetails.getStore());
        refundDetail.setDealerEmployee(weixinPayDetails.getDealerEmployee());
        refundDetail.setTradeStatus(TradeStatus.TRADEING.getValue());
        refundDetail.setTransBeginTime(new Date());
        refundDetail.setCreator("api");

        refundDetail.setIwoid(Generator.generateIwoid());
        refundDetail.setAppid(weixinPayDetails.getAppid());
        refundDetail.setSubAppid(weixinPayDetails.getSubAppid());
        refundDetail.setMchId(weixinPayDetails.getMchId());
        refundDetail.setSubMchId(weixinPayDetails.getSubMchId());
        refundDetail.setOutTradeNo(weixinPayDetails.getOutTradeNo());
        refundDetail.setTransactionId(weixinPayDetails.getTransactionId());
        refundDetail.setDeviceInfo(weixinPayDetails.getDeviceInfo());
        refundDetail.setRefundType(SysEnums.RefundType.REVERSE.getValue());
        refundDetail.setTotalFee(weixinPayDetails.getTotalFee());
        refundDetail.setRefundFee(weixinPayDetails.getRefundFee());
        commonDAO.save(refundDetail, true);
        WeixinRefundDetailsVO refundVO = new WeixinRefundDetailsVO(); 
        BeanCopierUtil.copyProperties(refundDetail, refundVO);
        return refundVO;
    }

    @Override
    public void doTransUpdateReverseResult(WeixinRefundDetailsVO refundDetailsVO) {
        Validator.checkArgument(refundDetailsVO == null, "refundDetailsVO不能为空");
        Validator.checkArgument(StringUtils.isBlank(refundDetailsVO.getOutTradeNo()), "refundDetailsVO.outTradeNo不能为空");
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from WeixinRefundDetails w where w.outTradeNo=:OUTTRADENO and w.tradeStatus=:TRADESTATUS ";
        jpqlMap.put("OUTTRADENO", refundDetailsVO.getOutTradeNo());
        jpqlMap.put("TRADESTATUS", TradeStatus.TRADEING.getValue());

        WeixinRefundDetails refundDetails = commonDAO.findObject(jpql, jpqlMap, false);
        if (StringUtils.equalsIgnoreCase(ReturnCode.SUCCESS.toString(), refundDetailsVO.getReturnCode())) {// 通信成功
            Validator.checkArgument(StringUtils.isBlank(refundDetailsVO.getMchId()), "微信商户号不能为空");
            Validator.checkArgument(StringUtils.isBlank(refundDetailsVO.getResultCode()), "微信撤销结果码不能为空");
            refundDetails.setReturnCode(refundDetailsVO.getResultCode());
            refundDetails.setReturnMsg(refundDetailsVO.getReturnMsg());
            refundDetails.setResultCode(refundDetailsVO.getResultCode());
            if (StringUtils.equalsIgnoreCase(ResultCode.SUCCESS.toString(), refundDetailsVO.getResultCode())) {// 撤销成功
                refundDetails.setTradeStatus(TradeStatus.TRADE_SUCCESS.getValue());
                
                // 更新原支付明细
                jpql =  "from WeixinPayDetails w where w.outTradeNo=:OUTTRADENO";
                jpqlMap.clear();
                jpqlMap.put("OUTTRADENO", refundDetailsVO.getOutTradeNo());
                WeixinPayDetails payDetails = commonDAO.findObject(jpql, jpqlMap, false);
                if (payDetails == null) {
                    throw new NotExistsException("系统支付订单不存在！");
                }
                payDetails.setRefundFee(payDetails.getTotalFee());
                //payDetails.setTradeStatus(TradeStatus.TRADE_REVERSED.getValue());// 原交易状态不变
                commonDAO.update(payDetails);
            } else {
                refundDetails.setErrCode(refundDetailsVO.getErrCode());
                refundDetails.setErrCodeDes(refundDetailsVO.getErrCodeDes());
                refundDetails.setTradeStatus(TradeStatus.TRADE_FAIL.getValue());
            }
            refundDetails.setEndTime(new Date());
        }// 通信失败不更新结果
        
        commonDAO.update(refundDetails);
    }

	public void setSysLogService(SysLogService sysLogService) {
	    this.sysLogService = sysLogService;
	}
    
}
