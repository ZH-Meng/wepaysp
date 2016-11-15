package com.zbsp.wepaysp.service.pay.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.security.SignHelper;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.po.partner.Dealer;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.po.partner.Store;
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
 	   
        String partnerEmployeeId = MapUtils.getString(paramMap, "partnerEmployeeId");
        String dealerId = MapUtils.getString(paramMap, "dealerId");
        String storeId = MapUtils.getString(paramMap, "storeId");
        String dealerEmployeeId = MapUtils.getString(paramMap, "dealerEmployeeId");
        
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
            sql.append(" and w.transBeginTime <:ENDTIME ");
            sqlMap.put("ENDTIME", endTime);
        }

        sql.append(" order by w.transBeginTime desc");
        List<WeixinPayDetails> weixinPayDetailsList = (List<WeixinPayDetails>) commonDAO.findObjectList(sql.toString(), sqlMap, false, startIndex, maxResult);
        
        if(weixinPayDetailsList != null && !weixinPayDetailsList.isEmpty()) {
        	for (WeixinPayDetails weixinPayDetails : weixinPayDetailsList) {
        		WeixinPayDetailsVO vo = new WeixinPayDetailsVO();
        		//BeanCopierUtil.copyProperties(weixinPayDetails, vo);
        		vo.setOutTradeNo(weixinPayDetails.getOutTradeNo());
        		vo.setPartnerName(weixinPayDetails.getPartner().getCompany());
        		
        		vo.setPartnerEmployeeName(weixinPayDetails.getPartnerEmployee().getEmployeeName());
                vo.setDealerName(weixinPayDetails.getDealer().getCompany());
                vo.setStoreName(weixinPayDetails.getStore().getStoreName());
                vo.setDealerEmployeeName(weixinPayDetails.getDealerEmployee().getEmployeeName());
                
                vo.setPartnerId(weixinPayDetails.getPartner().getPartnerId());
        		vo.setPartnerEmployeeId(weixinPayDetails.getPartnerEmployee().getPartnerEmployeeId());
        		vo.setDealerId(weixinPayDetails.getDealer().getDealerId());
        		vo.setStoreId(weixinPayDetails.getStore().getStoreId());
        		vo.setDealerEmployeeId(weixinPayDetails.getDealerEmployee().getDealerEmployeeId());
        		vo.setPayType(weixinPayDetails.getPayType());
        		vo.setTotalFee(weixinPayDetails.getTotalFee());
        		vo.setResultCode(weixinPayDetails.getResultCode());
        		vo.setTransBeginTime(weixinPayDetails.getTransBeginTime());
        		
        		resultList.add(vo);
        	}
        }
        
        return resultList;
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
        String dealerEmployeeOid = MapUtils.getString(paramMap, "dealerEmployeeOid");
        Date beginTime = (Date) MapUtils.getObject(paramMap, "beginTime");
        Date endTime = (Date) MapUtils.getObject(paramMap, "endTime");

        StringBuffer sql = new StringBuffer("select count(distinct w.iwoid) from WeixinPayDetails w, PartnerEmployee pe, Dealer d, Store s, DealerEmployee de where w.partnerEmployee=pe and w.dealer=d and w.store=s and w.dealerEmployee=de");
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
        	sql.append(" and w.transBeginTime <:ENDTIME ");
            sqlMap.put("ENDTIME", endTime);
        }
        
        return commonDAO.queryObjectCount(sql.toString(), sqlMap, false);
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    @Override
    public WeixinPayDetailsVO doTransCreatePayDetails(WeixinPayDetailsVO weixinPayDetailsVO, String creator, String operatorUserOid, String logFunctionOid) {
        Validator.checkArgument(weixinPayDetailsVO == null, "支付明细对象不能为空");
        Validator.checkArgument(StringUtils.isBlank(creator), "创建人不能为空");
        // Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
        // Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
        Validator.checkArgument(StringUtils.isBlank(weixinPayDetailsVO.getPayType()), "交易类型不能为空");
        Validator.checkArgument(StringUtils.isBlank(weixinPayDetailsVO.getDealerOid()), "商户Oid不能为空");
        Validator.checkArgument(weixinPayDetailsVO.getTotalFee() == null, "订单金额不能为空");
        Validator.checkArgument(StringUtils.isBlank(weixinPayDetailsVO.getNotifyUrl()), "通知地址不能为空");
        Validator.checkArgument(StringUtils.isBlank(weixinPayDetailsVO.getApiKey()), "APIkey不能为空");
        if (WeixinPayDetails.PayType.JSAPI.getValue().equals(weixinPayDetailsVO.getPayType())) {// 公众号支付
            Validator.checkArgument(StringUtils.isBlank(weixinPayDetailsVO.getOpenid()), "openId不能为空");
        }

        // 查找商户
        Dealer dealer = commonDAO.findObject(Dealer.class, weixinPayDetailsVO.getDealerOid());
        if (dealer == null) {
            throw new NotExistsException("商户不存在！");
        }
        Store store = null;
        if (StringUtils.isNotBlank(weixinPayDetailsVO.getStoreOid())) {
            store = commonDAO.findObject(Store.class, weixinPayDetailsVO.getStoreOid());
        }

        // 查找服务商
        Partner partner = commonDAO.findObject(Partner.class, dealer.getPartner1Oid());
        if (partner == null) {
            throw new NotExistsException("服务商不存在！");
        }
        // 创建订单
        WeixinPayDetails newPayOrder = new WeixinPayDetails();
        newPayOrder.setIwoid(Generator.generateIwoid());
        newPayOrder.setDealer(dealer);
        newPayOrder.setStore(store);
        newPayOrder.setPartner(dealer.getPartner());
        newPayOrder.setPartnerLevel(dealer.getPartnerLevel());
        newPayOrder.setPartner1Oid(dealer.getPartner1Oid());
        newPayOrder.setPartner2Oid(dealer.getPartner2Oid());
        newPayOrder.setPartner3Oid(dealer.getPartner3Oid());
        newPayOrder.setPartnerEmployee(dealer.getPartnerEmployee());
        newPayOrder.setTransBeginTime(new Date());

        // 下单需要传递
        // TODO 微信公众号、商户号
        // newPayOrder.setAppid(partner.getAppId());
        // newPayOrder.setAppid(partner.getMchId());
        newPayOrder.setSubAppid(dealer.getSubAppid());
        newPayOrder.setSubMchId(dealer.getSubMchId());
        // 终端设备号(门店号或收银设备ID)，注意：PC网页或公众号内支付请传"WEB"
        newPayOrder.setDeviceInfo("WEB");
        // 生成随机字符串
        newPayOrder.setNonceStr(Generator.generateRandomNumber(32));
        // 商品描述 线下门店——门店品牌名-城市分店名-实际商品名称
        newPayOrder.setBody(dealer.getCompany() + (store == null ? "" : "-" + store.getStoreName()));
        // 商品详情、附加数据

        // 商户订单号
        newPayOrder.setOutTradeNo(Generator.generateIwoid());
        // 货币类型
        newPayOrder.setFeeType("CNY");
        newPayOrder.setTotalFee(weixinPayDetailsVO.getTotalFee());
        // TODO 终端IP
        // 交易起始时间、交易结束时间、商品标记、通知地址

        // 交易类型
        newPayOrder.setPayType(weixinPayDetailsVO.getPayType());

        // 用户标识、用户子标识
        newPayOrder.setOpenid(weixinPayDetailsVO.getOpenid());
        
        // 签名，签名类型为MD5
        Map<String, String> signMap = new HashMap<String, String>();

        signMap.put("appid", newPayOrder.getAppid());
        signMap.put("mch_id", newPayOrder.getMchId());
        // signMap.put("sub_appid", newPayOrder.getSubAppid());
        signMap.put("sub_mch_id", newPayOrder.getSubMchId());
        signMap.put("device_info", newPayOrder.getDeviceInfo());
        signMap.put("nonce_str", newPayOrder.getNonceStr());
        signMap.put("body", newPayOrder.getBody());
        // signMap.put("detail", newPayOrder.getDetail());
        // signMap.put("attach", newPayOrder.getAttach());
        signMap.put("out_trade_no", newPayOrder.getOutTradeNo());
        // signMap.put("fee_type", newPayOrder.getFeeType());
        signMap.put("total_fee", newPayOrder.getTotalFee() + "");
        signMap.put("spbill_create_ip", newPayOrder.getSpbillCreateIp());
        // signMap.put("time_start", newPayOrder.getTransBeginTime());
        // signMap.put("time_expire", "");
        // signMap.put("goods_tag", newPayOrder.getGoodsTag());
        signMap.put("notify_url", weixinPayDetailsVO.getNotifyUrl());
        signMap.put("trade_type", newPayOrder.getPayType());
        // signMap.put("product_id", "");
        // signMap.put("limit_pay", "");
        signMap.put("openid", newPayOrder.getOpenid());
        // signMap.put("sub_openid", newPayOrder.getSubOpenid());

        newPayOrder.setSign(SignHelper.sign4WxPay(signMap, weixinPayDetailsVO.getApiKey()));

        commonDAO.save(newPayOrder, true);
        Date processTime = new Date();
        // 新增交易明细日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "新增交易明细[微信支付系统订单ID=" + newPayOrder.getOutTradeNo()+ ", 商户ID=" + dealer.getDealerId() + ", 商户姓名=" + dealer.getCompany() + "，消费金额：" + newPayOrder.getTotalFee() + ", 商品详情=" + newPayOrder.getBody() + "]", processTime, processTime, null, newPayOrder.toString(), SysLog.State.success.getValue(), newPayOrder.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());
        
        BeanCopierUtil.copyProperties(newPayOrder, weixinPayDetailsVO);
        return weixinPayDetailsVO;
    }
    
}
