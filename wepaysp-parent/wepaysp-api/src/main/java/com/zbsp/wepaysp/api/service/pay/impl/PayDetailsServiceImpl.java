package com.zbsp.wepaysp.api.service.pay.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.pay.PayDetailsService;
import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.common.constant.SysEnums.PayPlatform;
import com.zbsp.wepaysp.common.constant.SysEnums.TradeStatus;
import com.zbsp.wepaysp.common.constant.SysEnums.TradeStatusShow;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.mobile.result.CommonResult;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.Formatter;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.mo.paydetail.v1_0.PayDetailData;
import com.zbsp.wepaysp.mo.paydetail.v1_0.QueryPayDetailResponse;
import com.zbsp.wepaysp.mo.paydetailprint.v1_0.QueryPrintPayDetailResponse;
import com.zbsp.wepaysp.po.view.ViewPayDetailId;
import com.zbsp.wepaysp.vo.pay.PayDetailVO;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;
import com.zbsp.wepaysp.vo.report.RptDealerStatVO;

public class PayDetailsServiceImpl extends BaseService implements PayDetailsService {
	
	private WeixinPayDetailsService weixinPayDetailsService;

	@Override
	public QueryPayDetailResponse doJoinTransQueryPayDetails(String dealerEmployeeOid, Map<String, Object> paramMap,	int startIndex, int maxResult) throws IllegalArgumentException {
		Validator.checkArgument(StringUtils.isBlank(dealerEmployeeOid), "dealerEmployeeOid不能为空");
        
        QueryPayDetailResponse response = new QueryPayDetailResponse(CommonResult.SUCCESS.getCode(), CommonResult.SUCCESS.getDesc(), Generator.generateIwoid());
        List<PayDetailData> payDetailVOList = new ArrayList<PayDetailData>();
        
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");
        Integer payType = MapUtils.getInteger(paramMap, "payType");
        Integer tradeStatus = MapUtils.getInteger(paramMap, "tradeStatus");
        String outTradeNo = MapUtils.getString(paramMap, "outTradeNo");// 系统单号
        String transactionId = MapUtils.getString(paramMap, "transactionId");// 微信单号或支付宝单号
        boolean totalFlag = MapUtils.getBoolean(paramMap, "totalFlag");

        // 从视图ViewPayDetail中查询支付明细
        String listJpql = "select w.id.dealerEmployeeOid, w.id.payType, w.id.transactionId, w.id.outTradeNo, w.id.transBeginTime, w.id.transEndTime, w.id.tradeStatus, "
            + "w.id.totalFee, w.id.cashFee, w.id.couponFee, w.id.refundFee  from ViewPayDetail w where 1=1 ";
        String totalJpql = "select sum(case when w.id.tradeStatus=1 then w.id.totalFee else 0 end),count(w.id.totalFee) from ViewPayDetail w where 1=1 ";
        StringBuffer sql = new StringBuffer("");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(dealerEmployeeOid)) {
            sql.append(" and w.id.dealerEmployeeOid = :DEALEREMPLOYEEOID");
            sqlMap.put("DEALEREMPLOYEEOID", dealerEmployeeOid);
        }
        
        if (beginTime != null ) {
            sql.append(" and w.id.transBeginTime >=:BEGINTIME ");
            sqlMap.put("BEGINTIME", beginTime);
        }
        if (endTime != null ) {
            sql.append(" and w.id.transBeginTime <=:ENDTIME ");
            sqlMap.put("ENDTIME", endTime);
        }
        if (payType != null) {
        	sql.append(" and w.id.payType >= :PAYTYPE1");
        	sql.append(" and w.id.payType <= :PAYTYPE2");
        	if (payType == PayPlatform.WEIXIN.getValue()) {
        		sqlMap.put("PAYTYPE1", "1");
        		sqlMap.put("PAYTYPE2", "5");
        	} else if (payType == PayPlatform.ALI.getValue()) {
        		sqlMap.put("PAYTYPE1", "6");
        		sqlMap.put("PAYTYPE2", "10");
        	}
        }
        if (tradeStatus != null) {
            sql.append(" and w.id.tradeStatus = :TRADESTATUS");
            sqlMap.put("TRADESTATUS", tradeStatus);
        }
        if (StringUtils.isNotBlank(outTradeNo)) {
            sql.append(" and w.id.outTradeNo = :OUTTRADENO");
            sqlMap.put("OUTTRADENO", outTradeNo);
        }
        if (StringUtils.isNotBlank(transactionId)) {
            sql.append(" and w.id.transactionId = :TRANSACTIONID");
            sqlMap.put("TRANSACTIONID", transactionId);
        }
        sql.append(" order by w.id.transBeginTime desc");
        
        List<?> payDetailList = (List<?>) commonDAO.findObjectList(listJpql + sql.toString(), sqlMap, false, startIndex, maxResult);
  
        // 总笔数为记录总数，总金额为交易成功的总金额
        if(payDetailList != null && !payDetailList.isEmpty()) {
            Iterator<?> it = payDetailList.iterator();
            while (it.hasNext()) { 
                Object[] curRow = (Object[]) it.next();
                ViewPayDetailId payDetail = new ViewPayDetailId();
                payDetail.setDealerEmployeeOid((String) curRow[0]);
                payDetail.setPayType((String) curRow[1]);
                payDetail.setTransactionId((String) curRow[2]);
                payDetail.setOutTradeNo((String) curRow[3]);
                payDetail.setTransBeginTime((Timestamp) curRow[4]);
                payDetail.setTransEndTime((Timestamp) curRow[5]);
                payDetail.setTradeStatus((Integer) curRow[6]);
                
                payDetail.setTotalFee((Integer) curRow[7]);
                payDetail.setCashFee((Long) curRow[8]);
                payDetail.setCouponFee((Integer) curRow[9]);
                payDetail.setRefundFee((Integer) curRow[10]);
                
                PayDetailData data = new PayDetailData();
                data.setOutTradeNo(payDetail.getOutTradeNo());
				// 转换支付类型为支付平台
				int type = Integer.parseInt(payDetail.getPayType());
				if (1 <= type && type <= 5) {
					data.setPayType(PayPlatform.WEIXIN.getValue());
				} else if (6 <= type && type <= 10) {
					data.setPayType(PayPlatform.ALI.getValue());
				}
                data.setTradeStatus(payDetail.getTradeStatus());
                data.setTransTime(DateUtil.getDate(payDetail.getTransBeginTime(), SysEnvKey.TIME_PATTERN_YMD_SLASH_HMS_COLON));
                data.setCollectionMoney(payDetail.getTotalFee());// 实收金额 = 总金额
                data.setRefundMoney(payDetail.getRefundFee() == null ? 0L : payDetail.getRefundFee());
                payDetailVOList.add(data);
            }
        }
        if (totalFlag) {
            Object total = commonDAO.findObject(totalJpql + sql.toString(), sqlMap, false);
            Object[] totalArr = (Object[]) total;
            response.setTotalMoney(totalArr[0] == null ? 0L : (Long) totalArr[0]);
            response.setTotalAmount(totalArr[1] == null ? 0L : (Long) totalArr[1]);
        }
        
        response.setPayDetailListJSON(JSONUtil.toJSONString(payDetailVOList, true));
        return response;
	}

	@Override
	public QueryPrintPayDetailResponse doJoinTransQueryPaySuccessDetail(String outTradeNo, int payType) {
		Validator.checkArgument(StringUtils.isBlank(outTradeNo), "outTradeNo不能为空");
        
		QueryPrintPayDetailResponse response = null;
		//if (1 <= payType && payType <= 5) {// 微信支付
		if (payType == PayPlatform.WEIXIN.getValue()) {// 微信支付
			WeixinPayDetailsVO payDetailVO = weixinPayDetailsService.doJoinTransQueryWeixinPayDetailsVOByNum(outTradeNo, null);
			if (payDetailVO == null || payDetailVO.getTradeStatus().intValue() != TradeStatus.TRADE_SUCCESS.getValue()) {// 没有支付成功的暂不允许查询
				response = new QueryPrintPayDetailResponse(CommonResult.DATA_NOT_EXIST.getCode(), CommonResult.DATA_NOT_EXIST.getDesc(), Generator.generateIwoid());
			} else {
				response = new QueryPrintPayDetailResponse(CommonResult.SUCCESS.getCode(), CommonResult.SUCCESS.getDesc(), Generator.generateIwoid());
				response.setDealerCompany(payDetailVO.getDealerName());
				response.setDealerId(payDetailVO.getDealerId());
				response.setDealerEmployeeId(payDetailVO.getDealerEmployeeId());
				
                response.setMoney(Formatter.formatNumber("###,##0.00", new BigDecimal(payDetailVO.getTotalFee()).divide(new BigDecimal(100)).doubleValue()));
				response.setOutTradeNo(payDetailVO.getOutTradeNo());
				response.setPayType(PayPlatform.WEIXIN.getDesc());
				response.setTradeStatus(TradeStatusShow.PAY_SUCCESS.getDesc());
				response.setTransactionId(payDetailVO.getTransactionId());
				response.setTradeTime(DateUtil.getDate(payDetailVO.getTransBeginTime(), SysEnvKey.TIME_PATTERN_YMD_SLASH_HMS_COLON));
//				try {
//				    //if (Validator.contains(WxPayBank.class, payDetailVO.getBankType())) {
//				    //} else {}
//				    response.setPayBank(WxPayBank.valueOf(payDetailVO.getBankType()).getName());
//                } catch (Exception e) {
//                    response.setPayBank(payDetailVO.getBankType());
//		        }
			}
		//} else if (6 <= payType && payType <= 10) {// 支付宝支付
		} else if (payType == Integer.valueOf(PayPlatform.ALI.getValue())) {// 支付宝支付
			response = new QueryPrintPayDetailResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc(), Generator.generateIwoid());
			
		} else {
			response = new QueryPrintPayDetailResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc(), Generator.generateIwoid());
		}
        
        return response;
	}

	@Override
	public Map<String, Object> doJoinTransAppIdQueryList(Map<String, Object> paramMap, int startIndex, int maxResult) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
        List<PayDetailVO> payDetailVOList = new ArrayList<PayDetailVO>();
        
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");
        String dealerOid = MapUtils.getString(paramMap, "dealerOid");
        String storeOid = MapUtils.getString(paramMap, "storeOid");
        String dealerEmployeeOid = MapUtils.getString(paramMap, "dealerEmployeeOid");
        Integer payType = MapUtils.getInteger(paramMap, "payType");
        
        // 从视图ViewPayDetail中查询支付明细
        String listJpql = "select w.id.dealerEmployeeOid, w.id.payType, w.id.transactionId, w.id.outTradeNo, w.id.transBeginTime, w.id.transEndTime, w.id.tradeStatus, "
            + "w.id.totalFee, w.id.cashFee, w.id.couponFee, w.id.refundFee  from ViewPayDetail w where 1=1 ";
        StringBuffer sql = new StringBuffer("");
        Map<String, Object> sqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(dealerOid)) {
            sql.append(" and w.id.dealerOid = :DEALEROID");
            sqlMap.put("DEALEROID", dealerOid);
        }
        if (StringUtils.isNotBlank(storeOid)) {
            sql.append(" and w.id.storeOid = :STOREOID");
            sqlMap.put("STOREOID", storeOid);
        }
        if (StringUtils.isNotBlank(dealerEmployeeOid)) {
            sql.append(" and w.id.dealerEmployeeOid = :DEALEREMPLOYEEOID");
            sqlMap.put("DEALEREMPLOYEEOID", dealerEmployeeOid);
        }
        if (payType != null) {
            sql.append(" and w.id.payType = :PAYTYPE");
            sqlMap.put("PAYTYPE", payType.toString());
        }
        if (beginTime != null ) {
            sql.append(" and w.id.transBeginTime >=:BEGINTIME ");
            sqlMap.put("BEGINTIME", beginTime);
        }
        if (endTime != null ) {
            sql.append(" and w.id.transBeginTime <=:ENDTIME ");
            sqlMap.put("ENDTIME", endTime);
        }
        sql.append(" order by w.id.transBeginTime desc");
        
        List<?> payDetailList = (List<?>) commonDAO.findObjectList(listJpql + sql.toString(), sqlMap, false, startIndex, maxResult);
  
        int i = startIndex;
        // 总笔数为记录总数，总金额为交易成功的总金额
        if(payDetailList != null && !payDetailList.isEmpty()) {
            Iterator<?> it = payDetailList.iterator();
            while (it.hasNext()) { 
                Object[] curRow = (Object[]) it.next();
                ViewPayDetailId payDetail = new ViewPayDetailId();
                payDetail.setDealerEmployeeOid((String) curRow[0]);
                payDetail.setPayType((String) curRow[1]);
                payDetail.setTransactionId((String) curRow[2]);
                payDetail.setOutTradeNo((String) curRow[3]);
                payDetail.setTransBeginTime((Timestamp) curRow[4]);
                payDetail.setTransEndTime((Timestamp) curRow[5]);
                payDetail.setTradeStatus((Integer) curRow[6]);
                
                payDetail.setTotalFee((Integer) curRow[7]);
                payDetail.setCashFee((Long) curRow[8]);
                payDetail.setCouponFee((Integer) curRow[9]);
                payDetail.setRefundFee((Integer) curRow[10]);
                
                PayDetailVO data = new PayDetailVO();
                data.setIndex(++i);
                data.setOutTradeNo(payDetail.getOutTradeNo());
                data.setTransTime(DateUtil.getDate(payDetail.getTransBeginTime(), "HH:mm:ss"));
                data.setCollectionMoney("￥"+Formatter.formatNumber("#0.00", new BigDecimal(payDetail.getTotalFee()).divide(new BigDecimal(100)).doubleValue()));// 实收金额 = 总金额
                payDetailVOList.add(data);
            }
        }
        if (startIndex == 0) {
            String totalJpql = "select sum(case when w.id.tradeStatus=1 then w.id.totalFee else 0 end),count(w.id.totalFee) from ViewPayDetail w where 1=1 ";

            Object totalObj = commonDAO.findObject(totalJpql + sql.toString(), sqlMap, false);
            Object[] totalArr = (Object[]) totalObj;
            totalArr[0]  = totalArr[0] == null ? "￥0.00" : "￥"+Formatter.formatNumber("#0.00", new BigDecimal((Long)totalArr[0]).divide(new BigDecimal(100)).doubleValue());
            totalArr[1]  = totalArr[1] == null ? 0L : totalArr[1];
            resultMap.put("total", totalArr);
        }
        resultMap.put("payList", payDetailVOList);
        return resultMap;
	}

    @Override
    public RptDealerStatVO doJoinTransQueryTodyStat(Map<String, Object> paramMap) {
        RptDealerStatVO statVO = new RptDealerStatVO();
        String dealerOid = MapUtils.getString(paramMap, "dealerOid");
        String storeOid = MapUtils.getString(paramMap, "storeOid");
        String dealerEmployeeOid = MapUtils.getString(paramMap, "dealerEmployeeOid");

        String totalJpql = "select sum(case when w.id.tradeStatus=1 then w.id.totalFee else 0 end),count(w.id.totalFee) from ViewPayDetail w where 1=1 ";
        StringBuffer sql = new StringBuffer("");
        Map<String, Object> sqlMap = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(dealerOid)) {
            sql.append(" and w.id.dealerOid = :DEALEROID");
            sqlMap.put("DEALEROID", dealerOid);
        }
        if (StringUtils.isNotBlank(storeOid)) {
            sql.append(" and w.id.storeOid = :STOREOID");
            sqlMap.put("STOREOID", storeOid);
        }
        if (StringUtils.isNotBlank(dealerEmployeeOid)) {
            sql.append(" and w.id.dealerEmployeeOid = :DEALEREMPLOYEEOID");
            sqlMap.put("DEALEREMPLOYEEOID", dealerEmployeeOid);
        }
        sql.append(" and w.id.transBeginTime >=:BEGINTIME ");
        sqlMap.put("BEGINTIME", TimeUtil.getDayStart(new Date()));
        sql.append(" and w.id.transBeginTime <=:ENDTIME ");
        sqlMap.put("ENDTIME", TimeUtil.getDayEnd(new Date()));

        Object totalObj = commonDAO.findObject(totalJpql + sql.toString(), sqlMap, false);
        Object[] totalArr = (Object[]) totalObj;
        statVO.setTotalAmount(totalArr[1] == null ? 0L : (Long) totalArr[1]);
        statVO.setTotalMoney(totalArr[0] == null ? 0L : (Long) totalArr[0]);
        return statVO;
    }
	
	public void setWeixinPayDetailsService(WeixinPayDetailsService weixinPayDetailsService) {
		this.weixinPayDetailsService = weixinPayDetailsService;
	}

}
