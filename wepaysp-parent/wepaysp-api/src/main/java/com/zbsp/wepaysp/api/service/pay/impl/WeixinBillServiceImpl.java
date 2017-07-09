package com.zbsp.wepaysp.api.service.pay.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.pay.WeixinBillService;
import com.zbsp.wepaysp.common.config.SysSequenceCode;
import com.zbsp.wepaysp.common.config.SysSequenceMultiple;
import com.zbsp.wepaysp.common.constant.SysEnums.TradeStatus;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.pay.WeixinBill;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.po.pay.WeixinRefundDetails;
import com.zbsp.wepaysp.vo.pay.WeixinRefundDetailsVO;


public class WeixinBillServiceImpl
    extends BaseService
    implements WeixinBillService { 
	
	
	@Override
    public void doTransSaveBill(String[] bills) {
        
		ArrayList<WeixinBill> list = new ArrayList<WeixinBill>();
        for(int i=2;i<bills.length-2;i++){
        	String bill = bills[i];
        	String[] column = bill.split(","); 
        	DateUtil.getDate(column[0], "yyyy-MM-dd HH:mm:ss"); 
//        	交易时间,公众账号ID,商户号,子商户号,设备号,微信订单号,商户订单号,用户标识,交易类型,
//        	交易状态,付款银行,货币种类,总金额,代金券或立减优惠金额,微信退款单号,商户退款单号,退款金额,代金券或立减优惠退款金额,退款类型,退款状态,商品名称,商户数据包,手续费,费率
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
        	weixinBill.setResultCode(column[9]);
        	weixinBill.setBankType(column[10]);
        	weixinBill.setFeeType(column[11]);
        	weixinBill.setTotalFee(((new BigDecimal(column[12])).multiply(new BigDecimal("100"))).intValue());
        	weixinBill.setCouponFee(((new BigDecimal(column[13])).multiply(new BigDecimal("100"))).intValue());
        	weixinBill.setOutRefundNo(column[14]);
        	weixinBill.setRefundId(column[15]);
        	weixinBill.setSettlementRefundFee(((new BigDecimal(column[16])).multiply(new BigDecimal("100"))).intValue());
        	weixinBill.setRefundType(column[17]);
        	weixinBill.setRefundCode(column[18]);
        	weixinBill.setGoodsTag(column[19]);
        	weixinBill.setAttach(column[20]);
        	weixinBill.setPoundage(column[21]);
        	weixinBill.setRate(column[22]);
        	list.add(weixinBill);
        }
        commonDAO.saveList(list);
    }
 

	@Override
	public WeixinRefundDetailsVO doTransCreateRefundDetails(WeixinPayDetails weixinPayDetails, String creator,
			String operatorUserOid, String logFunctionOid) {
        Validator.checkArgument(weixinPayDetails == null, "weixinPayDetails不能为空");
        Validator.checkArgument(StringUtils.isBlank(weixinPayDetails.getIwoid()), "weixinPayDetails.iwoid不能为空");

        WeixinRefundDetails refundDetail = new WeixinRefundDetails();
        Map<String, Object> sqlMap = new HashMap<String, Object>();
        // 获取 服务商员工ID下一个序列值
        String sql = "select nextval('" + SysSequenceCode.PAY_ORDER + "') as sequence_value";
        Object seqObj = commonDAO.findObject(sql, sqlMap, true);
        if (seqObj == null) {
            throw new IllegalArgumentException("支付订单Id对应序列记录不存在");
        }
        refundDetail.setOutTradeNo(Generator.generateSequenceYYYYMMddNum((Integer) seqObj, SysSequenceMultiple.PAY_ORDER));// 商户订单号

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
        refundDetail.setMchId(weixinPayDetails.getMchId());
        refundDetail.setSubMchId(weixinPayDetails.getSubMchId());

        refundDetail.setDeviceInfo(weixinPayDetails.getDeviceInfo());
		return null;
	}

 
    
}
