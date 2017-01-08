package com.zbsp.wepaysp.api.service.pay.impl;

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
import com.zbsp.wepaysp.common.mobile.result.CommonResult;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.mo.paydetail.v1_0.PayDetailData;
import com.zbsp.wepaysp.mo.paydetail.v1_0.QueryPayDetailResponse;
import com.zbsp.wepaysp.mo.paydetailprint.v1_0.QueryPrintPayDetailResponse;
import com.zbsp.wepaysp.po.view.ViewPayDetailId;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;

public class PayDetailsServiceImpl extends BaseService implements PayDetailsService {
	
	private WeixinPayDetailsService weixinPayDetailsService;

	@Override
	public QueryPayDetailResponse doJoinTransQueryPayDetails(String dealerEmployeeOid, Map<String, Object> paramMap,	int startIndex, int maxResult) throws IllegalArgumentException {
		Validator.checkArgument(StringUtils.isBlank(dealerEmployeeOid), "dealerEmployeeOid不能为空");
        
        QueryPayDetailResponse response = new QueryPayDetailResponse(CommonResult.SUCCESS.getCode(), CommonResult.SUCCESS.getDesc(), Generator.generateIwoid());
        List<PayDetailData> payDetailList = new ArrayList<PayDetailData>();
        
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");
        String payType = MapUtils.getString(paramMap, "payType");
        String tradeStatus = MapUtils.getString(paramMap, "tradeStatus");
        String outTradeNo = MapUtils.getString(paramMap, "outTradeNo");// 系统单号
        String transactionId = MapUtils.getString(paramMap, "transactionId");// 微信单号
        boolean totalFlag = MapUtils.getBoolean(paramMap, "totalFlag");

        //String listJpql = "from ViewPayDetail w where 1=1 ";
        
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
        
        if (StringUtils.isNotBlank(payType)) {
            sql.append(" and w.id.payType = :PAYTYPE");
            sqlMap.put("PAYTYPE", payType);
        }
        
        if (StringUtils.isNotBlank(tradeStatus)) {
            sql.append(" and w.id.tradeStatus = :TRADESTATUS");
            sqlMap.put("TRADESTATUS", Integer.valueOf(tradeStatus));
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
        //List<WeixinPayDetails> weixinPayDetailsList = (List<WeixinPayDetails>) commonDAO.findObjectList(listJpql + sql.toString(), sqlMap, false, startIndex, maxResult);
        
        // List<ViewPayDetail> weixinPayDetailsList = (List<ViewPayDetail>) commonDAO.findObjectList(listJpql + sql.toString(), sqlMap, false, startIndex, maxResult);
        
        List<?> weixinPayDetailsList = (List<?>) commonDAO.findObjectList(listJpql + sql.toString(), sqlMap, false, startIndex, maxResult);
  
        // 总笔数为记录总数，总金额为交易成功的总金额
        if(weixinPayDetailsList != null && !weixinPayDetailsList.isEmpty()) {
            Iterator<?> it = weixinPayDetailsList.iterator();
            while (it.hasNext()) { 
                Object[] curRow = (Object[]) it.next();
                ViewPayDetailId weixinPayDetails = new ViewPayDetailId();
                weixinPayDetails.setDealerEmployeeOid((String) curRow[0]);
                weixinPayDetails.setPayType((String) curRow[1]);
                weixinPayDetails.setTransactionId((String) curRow[2]);
                weixinPayDetails.setOutTradeNo((String) curRow[3]);
                weixinPayDetails.setTransBeginTime((Timestamp) curRow[4]);
                weixinPayDetails.setTransEndTime((Timestamp) curRow[5]);
                weixinPayDetails.setTradeStatus((Integer) curRow[6]);
                
                weixinPayDetails.setTotalFee((Integer) curRow[7]);
                weixinPayDetails.setCashFee((Long) curRow[8]);
                weixinPayDetails.setCouponFee((Integer) curRow[9]);
                weixinPayDetails.setRefundFee((Integer) curRow[10]);
                
                
                PayDetailData data = new PayDetailData();
                data.setOutTradeNo(weixinPayDetails.getOutTradeNo());
                data.setPayType(Integer.valueOf(weixinPayDetails.getPayType()));
                data.setTradeStatus(weixinPayDetails.getTradeStatus());
                data.setTransTime(DateUtil.getDate(weixinPayDetails.getTransBeginTime(), "yyyy-MM-dd HH:mm:ss"));
                data.setCollectionMoney(weixinPayDetails.getTotalFee());// 实收金额 = 总金额
                data.setRefundMoney(weixinPayDetails.getRefundFee() == null ? 0L : weixinPayDetails.getRefundFee());
                payDetailList.add(data);
            }
        }
        if (totalFlag) {
            Object total = commonDAO.findObject(totalJpql + sql.toString(), sqlMap, false);
            Object[] totalArr = (Object[]) total;
            response.setTotalMoney(totalArr[0] == null ? 0L : (Long) totalArr[0]);
            response.setTotalAmount(totalArr[1] == null ? 0L : (Long) totalArr[1]);
        }
        
        response.setPayDetailListJSON(JSONUtil.toJSONString(payDetailList, true));
        return response;
	}

	@Override
	public QueryPrintPayDetailResponse doJoinTransQueryPayDetail(String outTradeNo, int payType) {
		Validator.checkArgument(StringUtils.isBlank(outTradeNo), "outTradeNo不能为空");
        
		QueryPrintPayDetailResponse response = null;
		if (1 <= payType && payType <= 5) {// 微信支付
			WeixinPayDetailsVO payDetailVO = weixinPayDetailsService.doJoinTransQueryWeixinPayDetail(outTradeNo);
			if (payDetailVO == null) {
				response = new QueryPrintPayDetailResponse(CommonResult.DATA_NOT_EXIST.getCode(), CommonResult.DATA_NOT_EXIST.getDesc(), Generator.generateIwoid());
			} else {
				response = new QueryPrintPayDetailResponse(CommonResult.SUCCESS.getCode(), CommonResult.SUCCESS.getDesc(), Generator.generateIwoid());
				response.setDealerCompany(payDetailVO.getDealerEmployeeName());
				response.setDealerId(payDetailVO.getDealerId());
				response.setDealerEmployeeId(payDetailVO.getDealerEmployeeId());
				response.setDeviceId(payDetailVO.getDeviceInfo());//FIXME
				response.setMoney(payDetailVO.getTotalFee());
				response.setOutTradeNo(payDetailVO.getOutTradeNo());
				response.setPayType(Integer.valueOf(payDetailVO.getPayType()));
				response.setTransactionId(payDetailVO.getTransactionId());
				response.setTradeStatus(payDetailVO.getTradeStatus());
				response.setTradeTime(DateUtil.getDate(payDetailVO.getTransBeginTime(), "yyyy/MM/dd HH:mm:ss"));
				response.setPayBank(payDetailVO.getBankType());
			}
		} else if (6 <= payType && payType <= 10) {// 支付宝支付
			response = new QueryPrintPayDetailResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc(), Generator.generateIwoid());
			
		} else {
			response = new QueryPrintPayDetailResponse(CommonResult.INVALID_ARGUMENT.getCode(), CommonResult.INVALID_ARGUMENT.getDesc(), Generator.generateIwoid());
		}
        
        return response;
	}

	public void setWeixinPayDetailsService(WeixinPayDetailsService weixinPayDetailsService) {
		this.weixinPayDetailsService = weixinPayDetailsService;
	}

}
