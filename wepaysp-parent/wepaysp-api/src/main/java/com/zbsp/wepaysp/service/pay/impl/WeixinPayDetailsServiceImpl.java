package com.zbsp.wepaysp.service.pay.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.zbsp.wepaysp.common.constant.EnumDefine.ResultCode;
import com.zbsp.wepaysp.common.constant.EnumDefine.TradeType;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.security.SignHelper;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.po.partner.Dealer;
import com.zbsp.wepaysp.po.partner.DealerEmployee;
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
        } else if (WeixinPayDetails.PayType.MICROPAY.getValue().equals(weixinPayDetailsVO.getPayType())) {// 公众号支付
            microPay = true;
            Validator.checkArgument(StringUtils.isBlank(operatorUserOid), "操作用户Oid不能为空");
            Validator.checkArgument(StringUtils.isBlank(logFunctionOid), "日志记录项Oid不能为空");
            
            Validator.checkArgument(StringUtils.isBlank(weixinPayDetailsVO.getDealerEmployeeOid()), "收银员Oid不能为空");
            Validator.checkArgument(StringUtils.isBlank(weixinPayDetailsVO.getAuthCode()), "authCode不能为空");
            
            dealerEmployee = commonDAO.findObject(DealerEmployee.class, weixinPayDetailsVO.getDealerEmployeeOid());
            if (dealerEmployee == null) {
                throw new NotExistsException("收银员不存在！");
            }
            dealer = dealerEmployee.getDealer();
        }

        if (dealer == null && StringUtils.isNotBlank(weixinPayDetailsVO.getDealerOid())) {
            // 查找商户
            dealer = commonDAO.findObject(Dealer.class, weixinPayDetailsVO.getDealerOid());
        }
        if (dealer == null) {
            throw new NotExistsException("商户不存在！");
        }
        
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
        
        newPayOrder.setTransBeginTime(new Date());

        /*----------微信支付参数-公有-必传--------*/
        
        // 微信公众号、商户号，sdk会处理
        // newPayOrder.setAppid(partner.getAppId());// 服务商公众号ID
        // newPayOrder.setMchid(partner.getMchId());// 商户号
        newPayOrder.setSubMchId(dealer.getSubMchId());// 子商户号
        // newPayOrder.setNonceStr(Generator.generateRandomString(32));//FIXME 随机字符串，sdk会自动生成
        newPayOrder.setBody(dealer.getCompany() + (store == null ? "" : "-" + store.getStoreName()));// 商品描述 线下门店——门店品牌名-城市分店名-实际商品名称
        newPayOrder.setOutTradeNo(Generator.generateIwoid());// 商户订单号
        
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
            newPayOrder.setLimitPay("no_credit");// 非必传，指定不能使用信用卡支付

            if (jsapiPay) {// 公众号支付
                newPayOrder.setTradeType(TradeType.JSAPI.toString());// 交易类型
            }
            // 商品ID，trade_type=NATIVE，此参数必传

        } else if (microPay) {// 刷卡下单+支付
            newPayOrder.setAuthCode(weixinPayDetailsVO.getAuthCode());
            newPayOrder.setDeviceInfo(store != null ? store.getStoreId() : null);// 非必传 终端设备号(门店号或收银设备ID)
        }

        commonDAO.save(newPayOrder, true);
        Date processTime = new Date();
        
        // 记录日志-创建微信支付交易明细
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), operatorUserOid, "新增微信支付明细[系统内部订单ID=" + newPayOrder.getOutTradeNo()+ ", 商户ID=" + dealer.getDealerId() + ", 商户姓名=" + dealer.getCompany() + "，消费金额：" + newPayOrder.getTotalFee() + ", 商品详情=" + newPayOrder.getBody() + "]", processTime, processTime, null, newPayOrder.toString(), SysLog.State.success.getValue(), newPayOrder.getIwoid(), logFunctionOid, SysLog.ActionType.create.getValue());
        
        BeanCopierUtil.copyProperties(newPayOrder, weixinPayDetailsVO);
        return weixinPayDetailsVO;
    }

    @Override
    public void doTransUpdatePayResult(String resultCode, String errCode, WeixinPayDetailsVO payResultVO) {
        Date processBeginTime = new Date();
        // String tradeType = payResultVO.getTradeType();// 交易类型
        String outTradeNo = payResultVO.getOutTradeNo();// 系统订单号

        // 查找支付明细，成功时通过订单号，失败时？？？
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from WeixinPayDetails w where 1=1 ";
        
        //TODO
        
        WeixinPayDetails payDetails = commonDAO.findObject(jpql, jpqlMap, false);
        if (payDetails == null) {
            throw new NotExistsException("系统支付订单不存在！");
        }
        String logDescTemp = "";
        if (StringUtils.equalsIgnoreCase(ResultCode.SUCCESS.toString(), resultCode)) {
            payDetails.setResultCode(resultCode);
            payDetails.setBankType(payResultVO.getBankType());
            payDetails.setTransactionId(payResultVO.getTransactionId());// 微信支付订单号
            
            //String attach = payResultVO.getAttach();// 商户数据包

            /* 实际支付类型额金额 */
            payDetails.setCashFeeType(StringUtils.isNotBlank(payResultVO.getCashFeeType()) ? payResultVO.getCashFeeType() : "CNY");
            payDetails.setCashFee(payResultVO.getCashFee());
            payDetails.setTimeEnd(payResultVO.getTimeEnd());// 支付完成时间
            logDescTemp += "支付结果：交易成功" + "，微信支付订单号：" + payDetails.getTransactionId() + "，支付金额：" + payDetails.getCashFee();
        } else {
            payDetails.setResultCode(ResultCode.FAIL.toString());
            // errCode
            String errCodeDes = payResultVO.getErrCodeDes();
            payDetails.setErrCode(errCode);
            payDetails.setErrCodeDes(errCodeDes);
            logDescTemp += "支付结果：交易失败，错误码：" + errCode + "，错误描述：" + payDetails.getErrCodeDes();
        }
        
        Date processEndTime = new Date();
        payDetails.setTransEndTime(processEndTime);
        commonDAO.update(payDetails);
        
        // 记录日志-修改微信支付结果
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, "修改微信支付明细[" + logDescTemp + "]", processBeginTime, processEndTime, null, payDetails.toString(), SysLog.State.success.getValue(), payDetails.getIwoid(), null, SysLog.ActionType.modify.getValue());
    }
    
}
