package com.zbsp.wepaysp.api.service.pay.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.LockModeType;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.main.init.SysConfigService;
import com.zbsp.wepaysp.api.service.manage.SysLogService;
import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.common.config.SysSequenceCode;
import com.zbsp.wepaysp.common.config.SysSequenceMultiple;
import com.zbsp.wepaysp.common.constant.EnumDefine.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.EnumDefine.OrderClosedErr;
import com.zbsp.wepaysp.common.constant.EnumDefine.ResultCode;
import com.zbsp.wepaysp.common.constant.EnumDefine.ReturnCode;
import com.zbsp.wepaysp.common.constant.EnumDefine.TradeState;
import com.zbsp.wepaysp.common.constant.EnumDefine.TradeType;
import com.zbsp.wepaysp.common.constant.EnumDefine.WxPayResult;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.exception.DataStateException;
import com.zbsp.wepaysp.common.exception.InvalidValueException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.po.partner.Dealer;
import com.zbsp.wepaysp.po.partner.DealerEmployee;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.po.partner.PartnerEmployee;
import com.zbsp.wepaysp.po.partner.Store;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails.TradeStatus;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;
import com.zbsp.wepaysp.vo.pay.WeixinPayTotalVO;

public class WeixinPayDetailsServiceImpl
    extends BaseService
    implements WeixinPayDetailsService {
    
	private SysConfigService sysConfigService;
    private SysLogService sysLogService;

    @SuppressWarnings("unchecked")
	@Override
    public Map<String, Object> doJoinTransQueryWeixinPayDetails(Map<String, Object> paramMap, int startIndex, int maxResult) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
    	List<WeixinPayDetailsVO> resultList = new ArrayList<WeixinPayDetailsVO>();
 	   
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
        String payType = MapUtils.getString(paramMap, "payType");
        String outTradeNo = MapUtils.getString(paramMap, "outTradeNo");// 系统单号
        String transactionId = MapUtils.getString(paramMap, "transactionId");// 微信单号

        //StringBuffer sql = new StringBuffer("select distinct(w) from WeixinPayDetails w, Partner p, PartnerEmployee pe, Dealer d, Store s, DealerEmployee de where w.partner=p and w.partnerEmployee=pe and w.dealer=d and w.store=s and w.dealerEmployee=de");
        StringBuffer sql = new StringBuffer("select distinct(w) from WeixinPayDetails w LEFT JOIN w.partner LEFT JOIN w.partnerEmployee LEFT JOIN w.dealer LEFT JOIN w.store LEFT JOIN w.dealerEmployee where 1=1 ");
       
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
        if (StringUtils.isNotBlank(payType)) {// 支付类型
            sql.append(" and w.payType = :PAYTYPE");
            sqlMap.put("PAYTYPE", payType);
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
        List<WeixinPayDetails> weixinPayDetailsList = (List<WeixinPayDetails>) commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);
  
        Long totalAmount = 0L;
        Long totalMoney = 0L;
        
        // 总笔数为记录总数，总金额为交易成功的总金额
        if(weixinPayDetailsList != null && !weixinPayDetailsList.isEmpty()) {
        	for (WeixinPayDetails weixinPayDetails : weixinPayDetailsList) {
        	    totalAmount++;
                if (weixinPayDetails.getTradeStatus() != null && weixinPayDetails.getTradeStatus().intValue() == TradeStatus.TRADE_SUCCESS.getValue()) {
                    totalMoney += weixinPayDetails.getTotalFee();
                }
                
        		WeixinPayDetailsVO vo = new WeixinPayDetailsVO();
        		//BeanCopierUtil.copyProperties(weixinPayDetails, vo);
        		vo.setBankType(weixinPayDetails.getBankType());//FIXME 转换
        		vo.setDeviceInfo(weixinPayDetails.getDeviceInfo());
        		vo.setOutTradeNo(weixinPayDetails.getOutTradeNo());
        		vo.setTradeStatus(weixinPayDetails.getTradeStatus());
        		vo.setRefundFee(weixinPayDetails.getRefundFee());
        		
                DealerEmployee de = weixinPayDetails.getDealerEmployee();
                vo.setDealerEmployeeName(de != null ? de.getEmployeeName() : "");
                vo.setDealerEmployeeId(de != null ? de.getDealerEmployeeId() : "");
                
                Store store = weixinPayDetails.getStore();
                vo.setStoreName(store != null ? store.getStoreName() : (de != null ? de.getStore().getStoreName() : ""));
                vo.setStoreId(store != null ? store.getStoreId() : (de != null ? de.getStore().getStoreId() : ""));
                
                Dealer dealer = weixinPayDetails.getDealer();
                vo.setDealerName(dealer != null ? dealer.getCompany() : (de != null ? de.getDealer().getCompany() : "" ));
                vo.setDealerId(dealer != null ? dealer.getDealerId() : (de != null ? de.getDealer().getDealerId() : "" ));
                
                PartnerEmployee pe = weixinPayDetails.getPartnerEmployee();
                vo.setPartnerEmployeeName(pe != null ? pe.getEmployeeName() : (dealer != null ? dealer.getPartnerEmployee().getEmployeeName() : ""));
                vo.setPartnerEmployeeId(pe != null ? pe.getPartnerEmployeeId() : (dealer != null ? dealer.getPartnerEmployee().getPartnerEmployeeId() : ""));
                
                Partner p = weixinPayDetails.getPartner();
                vo.setPartnerName(p != null ? p.getCompany() : (dealer != null ? dealer.getPartner().getCompany() : ""));
                vo.setPartnerId(p != null ? p.getPartnerId() : (dealer != null ? dealer.getPartner().getPartnerId() : ""));
                
        		vo.setPayType(weixinPayDetails.getPayType());
        		vo.setTransactionId(weixinPayDetails.getTransactionId());
        		vo.setTotalFee(weixinPayDetails.getTotalFee());
        		vo.setResultCode(weixinPayDetails.getResultCode());
        		vo.setTransBeginTime(weixinPayDetails.getTransBeginTime());
        		
        		
        		resultList.add(vo);
        	}
        }
        
        //计算合计信息
        sql = new StringBuffer("select sum(case when w.tradeStatus=1 then w.totalFee else 0 end),count(w.totalFee) from WeixinPayDetails w LEFT JOIN w.partner LEFT JOIN w.partnerEmployee LEFT JOIN w.dealer LEFT JOIN w.store LEFT JOIN w.dealerEmployee where 1=1 ");
        

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
        if (StringUtils.isNotBlank(payType)) {// 支付类型
            sql.append(" and w.payType = :PAYTYPE");
            sqlMap.put("PAYTYPE", payType);
        }
        if (StringUtils.isNotBlank(outTradeNo)) {
            sql.append(" and w.outTradeNo = :OUTTRADENO");
            sqlMap.put("OUTTRADENO", outTradeNo);
        }
        if (StringUtils.isNotBlank(transactionId)) {
            sql.append(" and w.transactionId = :TRANSACTIONID");
            sqlMap.put("TRANSACTIONID", transactionId);
        }
        
        List<?> weixinTotalList = (List<?>) commonDAO.findObjectList(sql.toString(), sqlMap, false);
        for (Iterator<?> it = weixinTotalList.iterator(); it.hasNext();) {
        	Object[] curRow = (Object[]) it.next();
        	 totalMoney= curRow[0]==null?0L:(Long)curRow[0];
        	 totalAmount = curRow[1]==null?0L:(Long)curRow[1];  
        }
        
        WeixinPayTotalVO totalVO = new WeixinPayTotalVO();
        totalVO.setTotalAmount(totalAmount);
        totalVO.setTotalMoney(totalMoney);
        resultMap.put("payList", resultList);
        resultMap.put("total", totalVO);
        return resultMap;
    }

    @Override
    public int doJoinTransQueryWeixinPayDetailsCount(Map<String, Object> paramMap) {
  	   
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
        String payType = MapUtils.getString(paramMap, "payType");
        String outTradeNo = MapUtils.getString(paramMap, "outTradeNo");// 系统单号
        String transactionId = MapUtils.getString(paramMap, "transactionId");// 微信单号

        //StringBuffer sql = new StringBuffer("select count(distinct w.iwoid) from WeixinPayDetails w, Partner p, PartnerEmployee pe, Dealer d, Store s, DealerEmployee de where w.partner=p, w.partnerEmployee=pe and w.dealer=d and w.store=s and w.dealerEmployee=de");
        StringBuffer sql = new StringBuffer("select count(distinct w.iwoid) from WeixinPayDetails w LEFT JOIN w.partner LEFT JOIN w.partnerEmployee LEFT JOIN w.dealer LEFT JOIN w.store LEFT JOIN w.dealerEmployee where 1=1 ");
        
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
        if (StringUtils.isNotBlank(payType)) {// 支付类型
            sql.append(" and w.payType = :PAYTYPE");
            sqlMap.put("PAYTYPE", payType);
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
    public WeixinPayDetailsVO doTransCreatePayDetails(WeixinPayDetailsVO weixinPayDetailsVO, String creator, String operatorUserOid, String logFunctionOid) {
        Validator.checkArgument(weixinPayDetailsVO == null, "支付明细对象不能为空");
        // Validator.checkArgument(StringUtils.isBlank(creator), "创建人不能为空");

        Validator.checkArgument(StringUtils.isBlank(weixinPayDetailsVO.getPayType()), "交易类型不能为空");
        Validator.checkArgument(weixinPayDetailsVO.getTotalFee() == null, "订单金额不能为空");

        boolean unifyOrder = false, jsapiPay = false, microPay = false;
        
        Dealer dealer = null;
        Store store = null;
        DealerEmployee dealerEmployee = null;
        if (WeixinPayDetails.PayType.JSAPI.getValue().equals(weixinPayDetailsVO.getPayType())) {// 统一下单-公众号支付
            unifyOrder = true;
            jsapiPay = true;
            Validator.checkArgument(StringUtils.isBlank(weixinPayDetailsVO.getDealerOid()), "商户Oid不能为空");
            Validator.checkArgument(StringUtils.isBlank(weixinPayDetailsVO.getOpenid()), "openId不能为空");
            if (StringUtils.isNotBlank(weixinPayDetailsVO.getStoreOid())) {
                store = commonDAO.findObject(Store.class, weixinPayDetailsVO.getStoreOid());
                if (store == null) {
                    logger.warn("门店不存在，storeOid=" + weixinPayDetailsVO.getStoreOid());
                }
                if (StringUtils.isNotBlank(weixinPayDetailsVO.getDealerEmployeeOid())) {
                    dealerEmployee = commonDAO.findObject(DealerEmployee.class, weixinPayDetailsVO.getDealerEmployeeOid());
                    if (dealerEmployee == null) {
                        logger.warn("收银员不存在，dealerEmployeeOid=" + weixinPayDetailsVO.getDealerEmployeeOid());
                    }
                }
            }
        } else if (WeixinPayDetails.PayType.MICROPAY.getValue().equals(weixinPayDetailsVO.getPayType())) {// 刷卡支付
            microPay = true;
            /*Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
            Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");*/
            
            Validator.checkArgument(StringUtils.isBlank(weixinPayDetailsVO.getDealerEmployeeOid()), "收银员Oid不能为空");
            Validator.checkArgument(StringUtils.isBlank(weixinPayDetailsVO.getAuthCode()), "authCode不能为空");
            
            dealerEmployee = commonDAO.findObject(DealerEmployee.class, weixinPayDetailsVO.getDealerEmployeeOid());
            if (dealerEmployee == null) {
                throw new NotExistsException("收银员不存在！");
            }
            dealer = dealerEmployee.getDealer();
            store = dealerEmployee.getStore();
        }

        if (dealer == null && StringUtils.isNotBlank(weixinPayDetailsVO.getDealerOid())) {
            // 查找商户
            dealer = commonDAO.findObject(Dealer.class, weixinPayDetailsVO.getDealerOid());
        }
        if (dealer == null) {
            throw new NotExistsException("商户不存在！");
        } else if (StringUtils.isBlank(dealer.getSubMchId())) {
            throw new InvalidValueException("商户信息缺失：sub_mch_id为空！");
        } else if (StringUtils.isBlank(dealer.getPartner1Oid())) {
            throw new InvalidValueException("商户信息缺失：partner1Oid为空！");
        }
        
        // 查找服务商
        Partner topPartner = commonDAO.findObject(Partner.class, dealer.getPartner1Oid());
        if (topPartner == null) {
            throw new NotExistsException("服务商不存在！");
        }
        
        // 创建订单
        WeixinPayDetails newPayOrder = new WeixinPayDetails();
        Map<String, Object> sqlMap = new HashMap<String, Object>();
        // 获取 服务商员工ID下一个序列值
        String sql = "select nextval('" + SysSequenceCode.PAY_ORDER + "') as sequence_value";
        Object seqObj = commonDAO.findObject(sql, sqlMap, true);
        if (seqObj == null) {
            throw new IllegalArgumentException("支付订单Id对应序列记录不存在");
        }
        newPayOrder.setOutTradeNo(Generator.generateSequenceYYYYMMddNum((Integer)seqObj, SysSequenceMultiple.PAY_ORDER));// 商户订单号
        
        newPayOrder.setIwoid(Generator.generateIwoid());
        /*--------系统服务商、业务员、商户、门店、收银员-------*/
        newPayOrder.setPartner(dealer.getPartner());
        newPayOrder.setPartnerLevel(dealer.getPartnerLevel());
        newPayOrder.setPartner1Oid(dealer.getPartner1Oid());
        newPayOrder.setPartner2Oid(dealer.getPartner2Oid());
        newPayOrder.setPartner3Oid(dealer.getPartner3Oid());
        newPayOrder.setPartnerEmployee(dealer.getPartnerEmployee());
        newPayOrder.setDealer(dealer);
        newPayOrder.setStore(store);
        newPayOrder.setDealerEmployee(dealerEmployee);
        newPayOrder.setTradeStatus(TradeStatus.TRADEING.getValue());
        newPayOrder.setTransBeginTime(new Date());
        newPayOrder.setCreator(creator);

        /*----------微信支付参数-公有-必传--------*/
        
        // 从内存中获取服务商配置信息
        Map<String, Object> partnerMap = sysConfigService.getPartnerCofigInfoByPartnerOid(dealer.getPartner1Oid());
        
		if (partnerMap != null && !partnerMap.isEmpty()) {
			
			weixinPayDetailsVO.setCertLocalPath(MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_LOCAL_PATH));
			weixinPayDetailsVO.setCertPassword(MapUtils.getString(partnerMap, SysEnvKey.WX_CERT_PASSWORD));
			weixinPayDetailsVO.setKeyPartner(MapUtils.getString(partnerMap, SysEnvKey.WX_KEY));
	        newPayOrder.setAppid(MapUtils.getString(partnerMap, SysEnvKey.WX_APP_ID));// 服务商公众号ID
	        newPayOrder.setMchId(MapUtils.getString(partnerMap, SysEnvKey.WX_MCH_ID));// 商户号
	        // 这两项 newPayOrder 复制给 weixinPayDetailsVO   
		} else {
			throw new RuntimeException("系统数据异常，服务商配置信息不存在");
		}

        newPayOrder.setSubMchId(dealer.getSubMchId());// 子商户号
        // newPayOrder.setNonceStr(Generator.generateRandomString(32));// 随机字符串，sdk会自动生成
        newPayOrder.setBody(dealer.getCompany() + (store == null ? "" : "-" + store.getStoreName()));// 商品描述 线下门店——门店品牌名-城市分店名-实际商品名称
        
        // TODO APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP，刷卡支付：调用微信支付API的机器IP
        newPayOrder.setSpbillCreateIp(weixinPayDetailsVO.getSpbillCreateIp());// 终端IP
        newPayOrder.setTotalFee(weixinPayDetailsVO.getTotalFee());// 总金额，分
        // 交易类型，构造请求参数时，使用 TradeType
        newPayOrder.setPayType(weixinPayDetailsVO.getPayType());
        
        /*----------微信支付参数-公有-非必传--------*/
        
        // newPayOrder.setSubAppid(dealer.getSubAppid());// 商户公众号ID
        newPayOrder.setFeeType("CNY");// 货币类型
        // 暂不设置：商品详情、附加数据、商品标记、交易起始时间time_start、交易结束时间time_expire、签名类型
        
        /*----------微信支付参数-私有--------*/
        if (unifyOrder) {// 统一下单
            // 通知地址，发送请求时指定，由Action统一配置
            // 用户标识、用户子标识 二选一必传，如果传sub_openid，还需传sub_appid
            newPayOrder.setOpenid(weixinPayDetailsVO.getOpenid());
            newPayOrder.setDeviceInfo("WEB");// 非必传
//            newPayOrder.setLimitPay("no_credit");// 非必传，指定不能使用信用卡支付

            if (jsapiPay) {// 公众号支付
                newPayOrder.setTradeType(TradeType.JSAPI.toString());// 交易类型
            }
            // 商品ID，trade_type=NATIVE，此参数必传

        } else if (microPay) {// 刷卡下单+支付
            newPayOrder.setTradeType(TradeType.MICROPAY.toString());
            newPayOrder.setAuthCode(weixinPayDetailsVO.getAuthCode());
            newPayOrder.setDeviceInfo(store != null ? store.getStoreId() : null);// 非必传 终端设备号(门店号或收银设备ID)
        }

        commonDAO.save(newPayOrder, false);
        Date processTime = new Date();
        
        // 记录日志-创建微信支付交易明细
       	sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "新增微信支付明细[系统内部订单ID=" + newPayOrder.getOutTradeNo()+ ", 商户ID=" + dealer.getDealerId() + ", 商户姓名=" + dealer.getCompany() + "，消费金额：" + newPayOrder.getTotalFee() + ", 商品详情=" + newPayOrder.getBody() + "]", processTime, processTime, null, newPayOrder.toString(), SysLog.State.success.getValue(), newPayOrder.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());
        
        BeanCopierUtil.copyProperties(newPayOrder, weixinPayDetailsVO);
        return weixinPayDetailsVO;
    }

    @Override
    public WeixinPayDetailsVO doTransUpdatePayResult(String returnCode, String resultCode, WeixinPayDetailsVO payResultVO) {
        logger.debug("returnCode：" + returnCode + "resultCode：" + resultCode);
        Date processBeginTime = new Date();
        Validator.checkArgument(payResultVO == null, "支付结果对象不能为空");
        String outTradeNo = payResultVO.getOutTradeNo();// 系统订单号
        Validator.checkArgument(StringUtils.isBlank(outTradeNo), "系统订单ID不能为空");

        // 查找支付明细
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from WeixinPayDetails w where w.outTradeNo=:OUTTRADENO";
        jpqlMap.put("OUTTRADENO", outTradeNo);
        
        WeixinPayDetails payDetails = commonDAO.findObject(jpql, jpqlMap, false, LockModeType.PESSIMISTIC_WRITE);
        if (payDetails == null) {
            throw new NotExistsException("系统支付订单不存在！");
        }
        String oldPayDetailStr = payDetails.toString();
        String logDescTemp = "";
        WeixinPayDetailsVO returnPayDetailVO = null;
        if (StringUtils.equalsIgnoreCase(ReturnCode.SUCCESS.toString(), returnCode)) {// 通信成功
            Validator.checkArgument(StringUtils.isBlank(payResultVO.getMchId()), "微信商户号不能为空");
            Validator.checkArgument(StringUtils.isBlank(resultCode), "微信支付结果码不能为空");
            payDetails.setReturnCode(returnCode);
            payDetails.setReturnMsg(payResultVO.getReturnMsg());

            if (!WeixinPayDetails.PayType.MICROPAY.getValue().equals(payDetails.getPayType())) {
                // 统一下单- 微信结果通知，需要校验是否重复返回结果
                if (payDetails.getTradeStatus().intValue() != TradeStatus.TRADEING.getValue()) {
                    // 非处理中，直接返回
                    throw new DataStateException("交易已完成，不能重复更新结果");
                }
            }
            logDescTemp = "通讯结果：成功";
            
            if (StringUtils.equalsIgnoreCase(ResultCode.SUCCESS.toString(), resultCode)) {
                Validator.checkArgument(StringUtils.isBlank(payResultVO.getTransactionId()), "微信支付订单ID不能为空");
                Validator.checkArgument(payResultVO.getTotalFee() == null, "订单金额不能为空");
                
                int tradeStatus = TradeStatus.TRADE_SUCCESS.getValue();
                
                payDetails.setResultCode(resultCode);
                payDetails.setBankType(payResultVO.getBankType());
                payDetails.setTransactionId(payResultVO.getTransactionId());// 微信支付订单号
                payDetails.setOpenid(payResultVO.getOpenid());// 用户标识
                payDetails.setIsSubscribe(payResultVO.getIsSubscribe());
                
                //payDetails.setTradeType(payResultVO.getTradeType());
                // 比对关键信息
                if (!StringUtils.equalsIgnoreCase(payDetails.getMchId(), payResultVO.getMchId())) {
                	logger.error(StringHelper.combinedString(AlarmLogPrefix.handleWxPayResultException.getValue(), 
                    		"商户号不一致，系统订单ID=" + payResultVO.getOutTradeNo(), 
                    		"支付请求商户号："+ payDetails.getMchId() + "，响应商户号：" + payResultVO.getMchId()));
                    tradeStatus = TradeStatus.TRADE_FAIL.getValue();
                    payDetails.setRemark("微信响应成功，但商户号不一致");
                }
                if (payDetails.getTotalFee().intValue() != payResultVO.getTotalFee().intValue()) {
                	logger.error(StringHelper.combinedString(AlarmLogPrefix.wxPayAPIMoneyException.getValue(), 
                    		"金额不一致，系统订单ID=" + payResultVO.getOutTradeNo(), 
                    		"支付请求总金额："+ payDetails.getTotalFee().intValue() + "，响应总金额：" + payResultVO.getTotalFee().intValue()));
                    tradeStatus = TradeStatus.TRADE_FAIL.getValue();
                    payDetails.setRemark((StringUtils.isBlank(payDetails.getRemark()) ? "微信响应成功，但" : (payDetails.getRemark() +",")) + "金额不一致");
                }
                
                payDetails.setTotalFee(payResultVO.getTotalFee());
                //String attach = payResultVO.getAttach();// 商户数据包
                
                payDetails.setCashFeeType(StringUtils.isNotBlank(payResultVO.getCashFeeType()) ? payResultVO.getCashFeeType() : "CNY");
                payDetails.setCashFee(payResultVO.getCashFee());
                payDetails.setTimeEnd(payResultVO.getTimeEnd());// 支付完成时间
                payDetails.setTradeStatus(tradeStatus);
                if (payDetails.getTradeStatus().intValue() == TradeStatus.TRADE_SUCCESS.getValue()) {
                    logDescTemp += "，支付结果：交易成功" + "，微信支付订单号：" + payResultVO.getTransactionId() + "，交易状态：" + tradeStatus + "，支付金额：" + payResultVO.getTotalFee();
                    
                    // 组装返回结果
                    returnPayDetailVO = new WeixinPayDetailsVO();
                    BeanCopierUtil.copyProperties(payDetails, returnPayDetailVO);
                    returnPayDetailVO.setStoreOid(payDetails.getStore() != null ? payDetails.getStore().getIwoid() : "");
                    returnPayDetailVO.setStoreId(payDetails.getStore() != null ? payDetails.getStore().getStoreId() : "");
                    returnPayDetailVO.setDealerEmployeeOid(payDetails.getDealerEmployee() != null ? payDetails.getDealerEmployee().getIwoid() : "");
                    returnPayDetailVO.setDealerEmployeeId(payDetails.getDealerEmployee() != null ? payDetails.getDealerEmployee().getDealerEmployeeId() : "");
                    returnPayDetailVO.setDealerEmployeeName(payDetails.getDealerEmployee() != null ? payDetails.getDealerEmployee().getEmployeeName() : "");
                    returnPayDetailVO.setStoreName(payDetails.getStore() != null ? payDetails.getStore().getStoreName() : "");
                    returnPayDetailVO.setDealerName(payDetails.getDealer() != null ? payDetails.getDealer().getCompany() : "");
                } else {
                    logDescTemp += "，支付结果：交易失败，" + payDetails.getRemark() + "，微信支付订单号：" + payResultVO.getTransactionId() + "，交易状态：" + tradeStatus;
                }
            } else {
                payDetails.setResultCode(ResultCode.FAIL.toString());
                
                String errCode = StringUtils.isNotBlank(payResultVO.getErrCode()) ? payResultVO.getErrCode() : WxPayResult.FAIL.getCode();// 错误码
                String errCodeDes = payResultVO.getErrCodeDes();
                
                if (StringUtils.isBlank(errCodeDes)) {
                    if (Validator.contains(WxPayResult.class, errCode)) {
                        errCodeDes = Enum.valueOf(WxPayResult.class, "code").getDesc();
                    }
                }
                payDetails.setTradeStatus(TradeStatus.TRADE_FAIL.getValue());
                payDetails.setErrCode(errCode);
                payDetails.setErrCodeDes(errCodeDes);
                logDescTemp += "，支付结果：交易失败，错误码：" + errCode + "，错误描述：" + payDetails.getErrCodeDes();
            }
        } else {// 通讯失败，只有刷卡支付会走此分支，因为通信失败时，统一下单- 微信结果通知不会含有系统订单
            payDetails.setReturnCode(returnCode);
            String returnMsg = StringUtils.isNotBlank(payResultVO.getReturnMsg()) ? payResultVO.getReturnMsg() : "通信失败";
            payDetails.setReturnMsg(returnMsg);
            // 指定业务结果为系统错误
            payDetails.setErrCode(WxPayResult.ERROR.getCode());
            payDetails.setErrCodeDes(WxPayResult.ERROR.getCode());
            payDetails.setTradeStatus(TradeStatus.TRADE_FAIL.getValue());
            logDescTemp += "通信结果：失败";
        }
        
        Date processEndTime = new Date();
        payDetails.setTransEndTime(processEndTime);
        commonDAO.update(payDetails);
        
        // 记录日志-修改微信支付结果
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, "修改微信支付明细[" + logDescTemp + "]", processBeginTime, processEndTime, oldPayDetailStr, payDetails.toString(), SysLog.State.success.getValue(), payDetails.getIwoid(), null, SysLog.ActionType.modify.getValue());
        return returnPayDetailVO;
    }

    @Override
    public void doTransUpdateOrderResult(String returnCode, String resultCode, WeixinPayDetailsVO payResultVO) {
        logger.debug("returnCode：" + returnCode + "resultCode：" + resultCode);
        Date processBeginTime = new Date();
        Validator.checkArgument(payResultVO == null, "下单结果对象不能为空");
        String outTradeNo = payResultVO.getOutTradeNo();// 系统订单号
        Validator.checkArgument(StringUtils.isBlank(outTradeNo), "系统订单ID不能为空");

        // 查找支付明细
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from WeixinPayDetails w where w.outTradeNo=:OUTTRADENO";
        jpqlMap.put("OUTTRADENO", outTradeNo);
        
        WeixinPayDetails payDetails = commonDAO.findObject(jpql, jpqlMap, false);
        if (payDetails == null) {
            throw new NotExistsException("系统支付订单不存在！");
        }
        String oldPayDetailStr = payDetails.toString(); 
        String logDescTemp = "";
        
        if (StringUtils.equalsIgnoreCase(ReturnCode.SUCCESS.toString(), returnCode)) {// 通信成功
            Validator.checkArgument(StringUtils.isBlank(resultCode), "微信下单结果码不能为空");
            logDescTemp = "通讯结果：成功";
            payDetails.setReturnCode(returnCode);
            payDetails.setReturnMsg(payResultVO.getReturnMsg());
            if (StringUtils.equalsIgnoreCase(ResultCode.SUCCESS.toString(), resultCode)) {
                payDetails.setResultCode(resultCode);
                //payDetails.setTradeType(payResultVO.getTradeType());
                payDetails.setTradeStatus(TradeStatus.TRADEING.getValue());// 交易处理中
                logDescTemp += "，下单结果：交易成功" + "，微信预支付订单标识：" + payResultVO.getPrepayId();
            } else {
                payDetails.setResultCode(ResultCode.FAIL.toString());
                
                String errCode = StringUtils.isNotBlank(payResultVO.getErrCode()) ? payResultVO.getErrCode() : WxPayResult.FAIL.getCode();// 错误码
                String errCodeDes = payResultVO.getErrCodeDes();
                
                if (StringUtils.isBlank(errCodeDes)) {
                    if (Validator.contains(WxPayResult.class, errCode)) {
                        errCodeDes = Enum.valueOf(WxPayResult.class, "code").getDesc();
                    }
                }
                
                payDetails.setErrCode(errCode);
                payDetails.setErrCodeDes(errCodeDes);
                logDescTemp += "，下单结果：交易失败，错误码：" + errCode + "，错误描述：" + payDetails.getErrCodeDes();
            }
        } else if (StringUtils.equalsIgnoreCase(ReturnCode.FAIL.toString(), returnCode)) {
            payDetails.setReturnCode(returnCode);
            String returnMsg = StringUtils.isNotBlank(payResultVO.getReturnMsg()) ? payResultVO.getReturnMsg() : "通信失败";
            payDetails.setReturnMsg(returnMsg);
            // 指定业务结果为系统错误
            payDetails.setErrCode(WxPayResult.ERROR.getCode());
            payDetails.setErrCodeDes(WxPayResult.ERROR.getCode());
            logDescTemp += "通信结果：失败";
        } else {
            logDescTemp += "通信错误";
            payDetails.setReturnCode(returnCode);
            payDetails.setReturnMsg(logDescTemp);
            // 指定业务结果为系统错误
            payDetails.setErrCode(WxPayResult.ERROR.getCode());
            payDetails.setErrCodeDes(WxPayResult.ERROR.getCode());
        }
        
        commonDAO.update(payDetails);
        Date processEndTime = new Date();
        
        // 记录日志-修改微信支付结果
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, "修改微信支付明细[" + logDescTemp + "]", processBeginTime, processEndTime, oldPayDetailStr, payDetails.toString(), SysLog.State.success.getValue(), payDetails.getIwoid(), null, SysLog.ActionType.modify.getValue());
    }

    @Override
    public WeixinPayDetailsVO doJoinTransQueryWeixinPayDetailsByOid(String weixinPayDetailOid) {
        Validator.checkArgument(StringUtils.isBlank(weixinPayDetailOid), "weixinPayDetailOid不能为空");
        WeixinPayDetailsVO vo = new WeixinPayDetailsVO();
        WeixinPayDetails payDetail = commonDAO.findObject(WeixinPayDetails.class, weixinPayDetailOid);
        if (payDetail == null) {
            throw new NotExistsException("系统支付订单不存在");
        }
        BeanCopierUtil.copyProperties(payDetail, vo);
        vo.setDealerName(payDetail.getDealer() != null ? payDetail.getDealer().getCompany() : "");
        vo.setStoreName(payDetail.getStore() != null ? payDetail.getStore().getStoreName() : "");
        return vo;
    }

    @Override
    public void doTransCancelPay(String weixinPayDetailOid) {
        Validator.checkArgument(StringUtils.isBlank(weixinPayDetailOid), "weixinPayDetailOid不能为空");
        WeixinPayDetails payDetail = commonDAO.findObject(WeixinPayDetails.class, weixinPayDetailOid);
        if (payDetail == null) {
            throw new NotExistsException("系统支付订单不存在");
        }
        if (payDetail.getTradeStatus() != null && payDetail.getTradeStatus().intValue() == TradeStatus.TRADE_CLOSED.getValue()) {
            logger.info("订单已关闭，不需要再关闭");
        } else {
            String oldPayDetailStr = payDetail.toString();
            Date processBeginTime = new Date();
            // 设置状态为待关闭，定时任务会将订单关闭
            payDetail.setTradeStatus(TradeStatus.TRADE_TO_BE_CLOSED.getValue());
            payDetail.setTransEndTime(processBeginTime);
            commonDAO.update(payDetail);
            // 记录日志-修改微信支付结果
            sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, "修改微信支付明细[交易状态(tradeStatus)=待关闭(5)]", processBeginTime, processBeginTime, oldPayDetailStr, payDetail.toString(), SysLog.State.success.getValue(), weixinPayDetailOid, null, SysLog.ActionType.modify.getValue());
        }
    }

    @Override
    public WeixinPayDetailsVO doTransUpdateOrderQueryResult(WeixinPayDetailsVO orderQueryResultVO) {
        /*查询结果result_code=SUCCESS才更新结果；trade_state=SUCCESS时，比对total_fee与系统订单total_fee是否一致，不一致则更新结果为交易失败；*/ 
        // 关键信息 result_code trade_state total_fee mch_id；其他非关键字段由SDK 通过验签完成校验
        
    	Validator.checkArgument(orderQueryResultVO == null, "orderQueryResultVO不能为空");
    	Validator.checkArgument(!StringUtils.equalsIgnoreCase(orderQueryResultVO.getReturnCode(), ReturnCode.SUCCESS.toString()), "returnCode必须为SUCCESS");
    	Validator.checkArgument(!StringUtils.equalsIgnoreCase(orderQueryResultVO.getResultCode(), ResultCode.SUCCESS.toString()), "resultCode必须为SUCCESS");    	
        Validator.checkArgument(StringUtils.isBlank(orderQueryResultVO.getOutTradeNo()), "系统订单ID不能为空");
        Validator.checkArgument(StringUtils.isBlank(orderQueryResultVO.getMchId()), "微信商户号不能为空");
        
        String tradeState = orderQueryResultVO.getTradeState();
        Validator.checkArgument(StringUtils.isBlank(tradeState), "订单状态不能为空");
        
        // 查找支付明细
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from WeixinPayDetails w where w.outTradeNo=:OUTTRADENO";
        jpqlMap.put("OUTTRADENO", orderQueryResultVO.getOutTradeNo());
        
        // 查询订单并锁定，防止更新查询结果时，微信支付结果通知更新结果
        WeixinPayDetails payDetails = commonDAO.findObject(jpql, jpqlMap, false, LockModeType.PESSIMISTIC_WRITE);
        if (payDetails == null) {
            throw new NotExistsException("系统支付订单不存在！");
        }
        
        logger.info("系统支付订单（ID=" +orderQueryResultVO.getOutTradeNo() + "）查询结果成功，订单状态：" + tradeState);
        
        WeixinPayDetailsVO returnPayDetailVO = null;
        // 非处理中，代表系统已收到微信支付结果通知并处理
        if (payDetails.getTradeStatus().intValue() != TradeStatus.TRADEING.getValue()) {
        	logger.info("系统已收到微信支付结果通知并处理，无需更新订单查询结果");
        	if (StringUtils.equalsIgnoreCase(TradeState.SUCCESS.toString(), orderQueryResultVO.getTradeState())) {
        	    if (StringUtils.isBlank(orderQueryResultVO.getTransactionId())) {
        	        logger.error(StringHelper.combinedString(AlarmLogPrefix.handleWxPayResultException.getValue(), 
                    		"查询结果交易状态为成功，微信支付单号为空，系统订单ID=" + orderQueryResultVO.getOutTradeNo()));
        	    }
        	    if (payDetails.getTotalFee() == null) {
        	        logger.error(StringHelper.combinedString(AlarmLogPrefix.handleWxPayResultException.getValue(), 
                    		"查询结果交易状态为成功，支付金额为空，系统订单ID=" + orderQueryResultVO.getOutTradeNo()));
        	    }
        	    if (StringUtils.isNotBlank(orderQueryResultVO.getTransactionId()) && payDetails.getTotalFee() != null) {
        	        if (payDetails.getTotalFee().intValue() != orderQueryResultVO.getTotalFee().intValue()) {
        	            logger.error(StringHelper.combinedString(AlarmLogPrefix.handleWxPayResultException.getValue(), 
                        		"查询结果信息金额不一致，系统订单ID=" + orderQueryResultVO.getOutTradeNo(), 
                        		"支付结果通知总金额："+ payDetails.getTotalFee().intValue() + "，主动查询总金额：" + orderQueryResultVO.getTotalFee().intValue()));
        	        }
        	    }
        	}
        } else {
        	String oldPayDetailStr = payDetails.toString();
        	String logDescTemp = "修改微信支付明细[订单查询成功";
            // 比对关键信息
            if (!StringUtils.equalsIgnoreCase(payDetails.getMchId(), orderQueryResultVO.getMchId())) {
                logger.error(StringHelper.combinedString(AlarmLogPrefix.handleWxPayResultException.getValue(), 
                		"查询结果信息商户号不一致，系统订单ID=" + orderQueryResultVO.getOutTradeNo(), 
                		"主动查询请求商户号："+ payDetails.getMchId() + "，主动查询结果商户号：" + orderQueryResultVO.getMchId()));
                tradeState = TradeState.PAYERROR.toString();
            }
            if (StringUtils.equalsIgnoreCase(TradeState.SUCCESS.toString(), orderQueryResultVO.getTradeState())) {
                Validator.checkArgument(StringUtils.isBlank(orderQueryResultVO.getTransactionId()), "微信支付订单ID不能为空");
                Validator.checkArgument(orderQueryResultVO.getTotalFee() == null, "订单金额不能为空");
                if (payDetails.getTotalFee().intValue() != orderQueryResultVO.getTotalFee().intValue()) {
                	logger.error(StringHelper.combinedString(AlarmLogPrefix.wxPayAPIMoneyException.getValue(), 
                    		"查询结果信息金额不一致，系统订单ID=" + orderQueryResultVO.getOutTradeNo(), 
                    		"主动查询请求总金额："+ payDetails.getTotalFee().intValue() + "，主动查询总金额：" + orderQueryResultVO.getTotalFee().intValue()));
                    tradeState = TradeState.PAYERROR.toString();
                }
                // 更新结果信息
                payDetails.setTotalFee(orderQueryResultVO.getTotalFee());
                //String attach = payResultVO.getAttach();// 商户数据包
                
                payDetails.setBankType(orderQueryResultVO.getBankType());
                payDetails.setTransactionId(orderQueryResultVO.getTransactionId());// 微信支付订单号
                payDetails.setOpenid(orderQueryResultVO.getOpenid());// 用户标识
                payDetails.setIsSubscribe(orderQueryResultVO.getIsSubscribe());
                payDetails.setCashFeeType(StringUtils.isNotBlank(orderQueryResultVO.getCashFeeType()) ? orderQueryResultVO.getCashFeeType() : "CNY");
                payDetails.setCashFee(orderQueryResultVO.getCashFee());
                payDetails.setTimeEnd(orderQueryResultVO.getTimeEnd());// 支付完成时间
                logDescTemp += "，微信支付订单号：" + orderQueryResultVO.getTransactionId() + "，支付金额：" + orderQueryResultVO.getTotalFee() +"，支付完成时间：" + payDetails.getTimeEnd();
            }
            
            Date processEndTime = new Date();
            payDetails.setTransEndTime(processEndTime);
            if (StringUtils.equalsIgnoreCase(tradeState, TradeState.SUCCESS.toString())) {
            	payDetails.setTradeStatus(TradeStatus.TRADE_SUCCESS.getValue());
            } else if (StringUtils.equalsIgnoreCase(tradeState, TradeState.CLOSED.toString())) { 
            	payDetails.setTradeStatus(TradeStatus.TRADE_CLOSED.getValue());
            } else if (StringUtils.equalsIgnoreCase(tradeState, TradeState.REVOKED.toString())) { 
            	payDetails.setTradeStatus(TradeStatus.TRADE_REVERSED.getValue());
            } else if (StringUtils.equalsIgnoreCase(tradeState, TradeState.PAYERROR.toString())) { 
            	payDetails.setTradeStatus(TradeStatus.TRADE_FAIL.getValue());
            	logger.warn("系统支付订单（ID=" +orderQueryResultVO.getOutTradeNo() + "）状态为交易失败");
            } else {
                // 未支付，支付超时 用户支付中，对应交易处理中
            	//TODO 转入退款
                payDetails.setTransEndTime(null);
            }
            logDescTemp += "，交易状态：" + tradeState +"]";
            
            commonDAO.update(payDetails);
            // 记录日志-修改微信支付结果
            sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, logDescTemp, processEndTime, processEndTime, oldPayDetailStr, payDetails.toString(), SysLog.State.success.getValue(), payDetails.getIwoid(), null, SysLog.ActionType.modify.getValue());
            
            // 支付成功，组装发送消息的VO
            if (StringUtils.equalsIgnoreCase(tradeState, TradeState.SUCCESS.toString())) {
                // 组装返回结果
                returnPayDetailVO = new WeixinPayDetailsVO();
                BeanCopierUtil.copyProperties(payDetails, returnPayDetailVO);
                returnPayDetailVO.setStoreOid(payDetails.getStore() != null ? payDetails.getStore().getIwoid() : "");
                returnPayDetailVO.setStoreId(payDetails.getStore() != null ? payDetails.getStore().getStoreId() : "");
                returnPayDetailVO.setDealerEmployeeOid(payDetails.getDealerEmployee() != null ? payDetails.getDealerEmployee().getIwoid() : "");
                returnPayDetailVO.setDealerEmployeeId(payDetails.getDealerEmployee() != null ? payDetails.getDealerEmployee().getDealerEmployeeId() : "");
                returnPayDetailVO.setDealerEmployeeName(payDetails.getDealerEmployee() != null ? payDetails.getDealerEmployee().getEmployeeName() : "");
                returnPayDetailVO.setStoreName(payDetails.getStore() != null ? payDetails.getStore().getStoreName() : "");
                returnPayDetailVO.setDealerName(payDetails.getDealer() != null ? payDetails.getDealer().getCompany() : "");
            }
            
        }
        
        return returnPayDetailVO;
    }
    
    @Override
    public void doTransUpdateOrderCloseResult(String resultCode, WeixinPayDetailsVO closeResultVO) {
        Validator.checkArgument(closeResultVO == null, "订单查询结果queryResultVO不能为空");
        Validator.checkArgument(StringUtils.isBlank(closeResultVO.getOutTradeNo()), "系统订单ID不能为空");
        String outTradeNo = closeResultVO.getOutTradeNo();
        if (StringUtils.isBlank(resultCode)) {
            Validator.checkArgument(StringUtils.isBlank(closeResultVO.getResultCode()), "resultCode不能空");
            resultCode = closeResultVO.getResultCode();
        }
        
        // 查找支付明细
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from WeixinPayDetails w where w.outTradeNo=:OUTTRADENO";
        jpqlMap.put("OUTTRADENO", closeResultVO.getOutTradeNo());
        
        WeixinPayDetails payDetails = commonDAO.findObject(jpql, jpqlMap, false);
        if (payDetails == null) {
            throw new NotExistsException("系统支付订单(ID=" + outTradeNo + ")不存在！");
        }
        
        boolean canCloseFlag = false;
        String errCode = closeResultVO.getErrCode();
        if (StringUtils.equalsIgnoreCase(ResultCode.SUCCESS.toString(), resultCode)) {// 关闭成功
            logger.info("系统支付订单(ID=" + outTradeNo + ")调用关闭订单API结果为成功，准备更新系统订单状态为关闭");
            canCloseFlag = true;
        } else if (StringUtils.equalsIgnoreCase(OrderClosedErr.ORDERCLOSED.toString(), errCode)) {// 订单已关闭
            logger.warn("系统支付订单(ID=" + outTradeNo + ")调用关闭订单API结果为错误提示[订单已关闭]，检查系统订单状态");
            if (payDetails.getTradeStatus() != null && payDetails.getTradeStatus().intValue() == TradeStatus.TRADE_CLOSED.getValue()) {
                logger.info("系统支付订单(ID=" + outTradeNo + ")状态与返回结果一致，为已关闭");
            } else {
                logger.info("系统支付订单(ID=" + outTradeNo + ")状态为" + TradeStatus.TRADE_CLOSED.getValue() + "准备更新系统订单状态为关闭");
                canCloseFlag = true;
            }
        } else if (StringUtils.equalsIgnoreCase(OrderClosedErr.ORDERNOTEXIST.toString(), errCode)) {// 订单不存在，直接关闭系统订单
            logger.warn("系统支付订单(ID=" + outTradeNo + ")调用关闭订单API结果错误提示为[订单不存在]，准备直接关闭系统订单");
            canCloseFlag = true;
        } else if (StringUtils.equalsIgnoreCase(OrderClosedErr.ORDERPAID.toString(), errCode)) {// 订单已支付
            logger.warn("系统支付订单(ID=" + outTradeNo + ")调用关闭订单API结果错误提示为[订单已支付]，检查系统订单状态");
            if (payDetails.getTradeStatus() != null && payDetails.getTradeStatus().intValue() == TradeStatus.TRADE_SUCCESS.getValue()) {
                logger.info("系统支付订单(ID=" + outTradeNo + ")状态与返回结果一致，为支付成功");
            } else if (payDetails.getTradeStatus() != null && payDetails.getTradeStatus().intValue() == TradeStatus.TRADEING.getValue()) {
            	logger.info("系统支付订单(ID=" + outTradeNo + ")状态为处理中，关单返回结果为支付成功，准备更新订单状态为已支付");
            	String oldPayDetailStr = payDetails.toString();
                payDetails.setTradeStatus(TradeStatus.TRADE_SUCCESS.getValue());
                commonDAO.update(payDetails);
                logger.info("系统支付订单(ID=" + outTradeNo + ")更新状态为已支付");
                // 记录日志
                Date processEndTime = new Date();
                sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, "修改微信支付明细[系统支付订单号：" + closeResultVO.getOutTradeNo() + "，交易状态：" + payDetails.getTradeStatus() + "]", processEndTime, processEndTime, oldPayDetailStr, payDetails.toString(), SysLog.State.success.getValue(), payDetails.getIwoid(), null, SysLog.ActionType.modify.getValue());
            } else {
            	logger.error(StringHelper.combinedString(AlarmLogPrefix.handleWxPayResultException.getValue(), "系统订单(ID=" + outTradeNo + ")状态为" + TradeStatus.TRADE_CLOSED.getValue() + "与微信关单结果[已支付]不一致"));
            }
        } else {
            logger.warn("系统支付订单(ID=" + outTradeNo + ")调用关闭订单API结果错误码："+ errCode +"，错误描述：" + closeResultVO.getErrCodeDes() +"，【此API不处理此错误结果】");
            // SIGNERROR、REQUIRE_POST_METHOD、XML_FORMAT_ERROR 是请求错误，由监听器报警
            // SYSTEMERROR
            // TRADE_STATE_ERROR，订单状态错误
            // USERPAYING--用户支付中 支付锁定，扣款和撤销间隔要在10s以上 
        }
        
        if (canCloseFlag) {
        	String oldPayDetailStr = payDetails.toString();
            payDetails.setTradeStatus(TradeStatus.TRADE_CLOSED.getValue());
            commonDAO.update(payDetails);
            logger.info("系统支付订单(ID=" + outTradeNo + ")关闭成功");
            // 记录日志
            Date processEndTime = new Date();
            sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, "修改微信支付明细[关闭订单结果成功" + "，系统支付订单号：" + closeResultVO.getOutTradeNo() + "，交易状态：" + payDetails.getTradeStatus() + "]", processEndTime, processEndTime, oldPayDetailStr, payDetails.toString(), SysLog.State.success.getValue(), payDetails.getIwoid(), null, SysLog.ActionType.modify.getValue());
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<WeixinPayDetails> doJoinTransQueryWeixinPayDetailsByState(int[] stateArr, long intervalTime) {
        Validator.checkArgument(null == stateArr || stateArr.length == 0, "查询状态不能为空");
        String jpql = "from WeixinPayDetails w where w.transBeginTime <= :TRANSBEGINTIME";
        Date beginTime = new Date(new Date().getTime() - intervalTime * 1000);
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (stateArr.length == 1) {
            jpql += " and w.tradeStatus = :TRADESTATUS";
            paramMap.put("TRADESTATUS", stateArr[0]);
        } else if (stateArr.length > 1) {
            jpql += " and w.tradeStatus in (";
            for (int i = 0; i < stateArr.length; i++) {
                if (i != stateArr.length - 1) {
                    jpql += ":TRADESTATUS" + i + ",";
                } else {
                    jpql += ":TRADESTATUS" + i;
                }
                paramMap.put("TRADESTATUS" + i, stateArr[i]);
            }
            jpql += ")";
        }
        paramMap.put("TRANSBEGINTIME", beginTime);

        return (List<WeixinPayDetails>) super.commonDAO.findObjectList(jpql, paramMap, false);
    }
    
    @Override
	public WeixinPayDetailsVO doJoinTransQueryWeixinPayDetail(String outTradeNo) {
		Validator.checkArgument(StringUtils.isBlank(outTradeNo), "查询状态不能为空");
		
		Map<String, Object> jpqlMap = new HashMap<String, Object>(); 
		StringBuffer jpql = new StringBuffer("from WeixinPayDetails w where 1=1 ");
		jpql.append(" and w.outTradeNo=:OUTTRADENO");
		jpqlMap.put("OUTTRADENO", outTradeNo);
		
		WeixinPayDetails weixinPayDetails = commonDAO.findObject(jpql.toString(), jpqlMap, false);
		WeixinPayDetailsVO vo = null;
		
		if (weixinPayDetails != null) {
			vo = new WeixinPayDetailsVO();
			BeanCopierUtil.copyProperties(weixinPayDetails, vo);
			//vo.setBankType(weixinPayDetails.getBankType());//FIXME 转换
			
	        DealerEmployee de = weixinPayDetails.getDealerEmployee();
	        vo.setDealerEmployeeName(de != null ? de.getEmployeeName() : "");
	        vo.setDealerEmployeeId(de != null ? de.getDealerEmployeeId() : "");
	        
	        Store store = weixinPayDetails.getStore();
	        vo.setStoreName(store != null ? store.getStoreName() : (de != null ? de.getStore().getStoreName() : ""));
	        vo.setStoreId(store != null ? store.getStoreId() : (de != null ? de.getStore().getStoreId() : ""));
	        
	        Dealer dealer = weixinPayDetails.getDealer();
	        vo.setDealerName(dealer != null ? dealer.getCompany() : (de != null ? de.getDealer().getCompany() : "" ));
	        vo.setDealerId(dealer != null ? dealer.getDealerId() : (de != null ? de.getDealer().getDealerId() : "" ));
	        
	        PartnerEmployee pe = weixinPayDetails.getPartnerEmployee();
	        vo.setPartnerEmployeeName(pe != null ? pe.getEmployeeName() : (dealer != null ? dealer.getPartnerEmployee().getEmployeeName() : ""));
	        vo.setPartnerEmployeeId(pe != null ? pe.getPartnerEmployeeId() : (dealer != null ? dealer.getPartnerEmployee().getPartnerEmployeeId() : ""));
	        
	        Partner p = weixinPayDetails.getPartner();
	        vo.setPartnerName(p != null ? p.getCompany() : (dealer != null ? dealer.getPartner().getCompany() : ""));
	        vo.setPartnerId(p != null ? p.getPartnerId() : (dealer != null ? dealer.getPartner().getPartnerId() : ""));
		}
		
		return vo;
	}
    
    public void setSysConfigService(SysConfigService sysConfigService) {
		this.sysConfigService = sysConfigService;
	}

	public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

}
