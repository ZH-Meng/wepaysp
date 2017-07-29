package com.zbsp.wepaysp.api.service.pay.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.pay.WeixinBillService;
import com.zbsp.wepaysp.common.constant.SysEnums;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.po.pay.WeixinBill;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.po.pay.WeixinRefundDetails;


public class WeixinBillServiceImpl
    extends BaseService
    implements WeixinBillService { 
	
	/**
	 * 保存微信对账单
	 * 并生成退款明细记录
	 * 更新交易记录中的退款金额
	 *   
	 */
	@Override
    public void doTransSaveBill(String[] bills) {
//    	交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型8,
//    	交易状态,付款银行,货币种类,总金额,代金券或立减优惠金额,微信退款单号,商户退款单号15,退款金额,代金券或立减优惠退款金额,17
//    	退款类型,退款状态,商品名称,商户数据包,手续费,费率
		
		ArrayList<WeixinBill> list = new ArrayList<WeixinBill>();
		ArrayList<String[]> refundList =  new ArrayList<String[]>();
		ArrayList<WeixinRefundDetails> refundDetails = new ArrayList<WeixinRefundDetails>();
		ArrayList<WeixinPayDetails> weixinPayDetails = new ArrayList<WeixinPayDetails>();
		//保存下载的对账单明细信息	
        for(int i=1;i<bills.length-2;i++){
        	String bill = bills[i];
        	String[] column = bill.split(","); 
        	DateUtil.getDate(column[0], "yyyy-MM-dd HH:mm:ss"); 

        	WeixinBill weixinBill = new WeixinBill();
        	weixinBill.setIwoid(Generator.generateIwoid());
        	weixinBill.setTradeTime(DateUtil.getDate(column[0], "yyyy-MM-dd HH:mm:ss"));
        	weixinBill.setAppid(column[1]);
//        	weixinBill.setSubAppid(column[2]);
        	weixinBill.setMchId(column[2]);
        	weixinBill.setSubMchId(column[3]);
        	weixinBill.setDeviceInfo(column[4]);
        	weixinBill.setTransactionId(column[5]);
        	weixinBill.setOutTradeNo(column[6]);
        	weixinBill.setOpenid(column[7]);
        	weixinBill.setPayType(column[8]);
        	String resultCode = column[9];
        	weixinBill.setResultCode(column[9]);
        	if(resultCode.equals("REFUND")||resultCode.equals("REVOKED")){
        		refundList.add(column);
        	}
        	weixinBill.setBankType(column[10]);
        	weixinBill.setFeeType(column[11]);
        	weixinBill.setTotalFee(((new BigDecimal(column[12])).multiply(new BigDecimal("100"))).intValue());
        	weixinBill.setCouponFee(((new BigDecimal(column[13])).multiply(new BigDecimal("100"))).intValue());
        	weixinBill.setOutRefundNo(column[15]);
        	weixinBill.setRefundId(column[14]);
        	weixinBill.setSettlementRefundFee(((new BigDecimal(column[16])).multiply(new BigDecimal("100"))).intValue());
        	weixinBill.setCouponRefundFee(((new BigDecimal(column[17])).multiply(new BigDecimal("100"))).intValue());
        	weixinBill.setRefundType(column[18]);
        	weixinBill.setRefundCode(column[19]);
        	weixinBill.setGoodsTag(column[20]);
        	weixinBill.setAttach(column[21]);
        	weixinBill.setPoundage(column[22]);
        	weixinBill.setRate(column[23]);
        	list.add(weixinBill);        	
        }
        commonDAO.saveList(list);
        
        //根据对账单生成退款明细记录
        for(String[] refundColumn:refundList){
        	//根据订单号获取消费数据
        	Map<String,Object> paramMap = new HashMap<String,Object>();
        	paramMap.put("outTradeNo", refundColumn[6]);
        	WeixinPayDetails weixinPayDetail = commonDAO.findObject(" from WeixinPayDetails where outTradeNo = :outTradeNo", paramMap, false);

        	//根据消费数据和下载的退款账单数据生成系统的退款记录表信息
        	WeixinRefundDetails refundDetail = new WeixinRefundDetails();
        	refundDetail.setIwoid(Generator.generateIwoid());
            refundDetail.setWeixinPayDetailsOid(weixinPayDetail.getIwoid());
            refundDetail.setPartner1Oid(weixinPayDetail.getPartner1Oid());
            refundDetail.setPartner2Oid(weixinPayDetail.getPartner2Oid());
            refundDetail.setPartner3Oid(weixinPayDetail.getPartner3Oid());
            refundDetail.setPartnerLevel(weixinPayDetail.getPartnerLevel());
            refundDetail.setPartner(weixinPayDetail.getPartner());
            refundDetail.setDealer(weixinPayDetail.getDealer());
            refundDetail.setStore(weixinPayDetail.getStore());
            refundDetail.setDealerEmployee(weixinPayDetail.getDealerEmployee());
            refundDetail.setAppid(weixinPayDetail.getAppid());
            refundDetail.setSubAppid(weixinPayDetail.getSubAppid());
            refundDetail.setMchId(weixinPayDetail.getMchId());
            refundDetail.setSubMchId(weixinPayDetail.getSubMchId());
            refundDetail.setDeviceInfo(weixinPayDetail.getDeviceInfo());
            refundDetail.setNonceStr(weixinPayDetail.getNonceStr());
            refundDetail.setSign(weixinPayDetail.getSign());
            refundDetail.setOutTradeNo(weixinPayDetail.getOutTradeNo());
            refundDetail.setTransactionId(weixinPayDetail.getTransactionId());
            refundDetail.setOutRefundNo(refundColumn[15]);
            refundDetail.setRefundId(refundColumn[14]);
            refundDetail.setCouponRefundType(WeixinRefundDetails.CouponRefundType.refund.getValue());
            refundDetail.setTotalFee(weixinPayDetail.getTotalFee());
            Integer refundFee = ((new BigDecimal(refundColumn[16])).multiply(new BigDecimal("100"))).intValue();
            refundDetail.setRefundFee(refundFee);
//            refundDetail.setRefundFeeType(refundColumn[18]);
            refundDetail.setReturnCode(refundColumn[19]);
            refundDetail.setCouponRefundFee(((new BigDecimal(refundColumn[17])).multiply(new BigDecimal("100"))).intValue());
            refundDetail.setTransBeginTime(DateUtil.getDate(refundColumn[0], "yyyy-MM-dd HH:mm:ss"));
            refundDetail.setTradeStatus(SysEnums.TradeStatus.TRADE_SUCCESS.getValue());
            //
            weixinPayDetail.setRefundFee(refundFee);
            weixinPayDetails.add(weixinPayDetail);
            refundDetails.add(refundDetail);
        }
        commonDAO.updateList(weixinPayDetails);
        commonDAO.saveList(refundDetails);
    }
  
 
    
}
