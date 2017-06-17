package com.zbsp.wepaysp.api.service.pay.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.LockModeType;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.api.service.manage.SysLogService;
import com.zbsp.wepaysp.api.service.pay.AliPayDetailsService;
import com.zbsp.wepaysp.common.config.SysSequenceCode;
import com.zbsp.wepaysp.common.config.SysSequenceMultiple;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.AliPayEnums.AliPayResult;
import com.zbsp.wepaysp.common.constant.AliPayEnums.GateWayResponse;
import com.zbsp.wepaysp.common.constant.SysEnums.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.SysEnums.PayType;
import com.zbsp.wepaysp.common.constant.SysEnums.TradeStatus;
import com.zbsp.wepaysp.common.exception.InvalidValueException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.StringHelper;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.alipay.AlipayApp;
import com.zbsp.wepaysp.po.alipay.AlipayAppAuthDetails;
import com.zbsp.wepaysp.po.manage.SysLog;
import com.zbsp.wepaysp.po.partner.Dealer;
import com.zbsp.wepaysp.po.partner.DealerEmployee;
import com.zbsp.wepaysp.po.partner.Partner;
import com.zbsp.wepaysp.po.partner.PartnerEmployee;
import com.zbsp.wepaysp.po.partner.Store;
import com.zbsp.wepaysp.po.pay.AliPayDetails;
import com.zbsp.wepaysp.vo.alipay.AlipayWapPayNotifyVO;
import com.zbsp.wepaysp.vo.pay.AliPayDetailsVO;
import com.zbsp.wepaysp.vo.pay.PayTotalVO;

public class AliPayDetailsServiceImpl
    extends BaseService
    implements AliPayDetailsService {
    
    private SysLogService sysLogService;
    
    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }
    
    /** 通过状态判断交易是否结束*/
    private boolean tradeIsEnd(int tradeStatus) {
        if (TradeStatus.TRADE_CLOSED.getValue() == tradeStatus 
            || TradeStatus.TRADE_REVERSED.getValue() == tradeStatus 
            || TradeStatus.TRADE_SUCCESS.getValue() == tradeStatus
            || TradeStatus.TRADE_FAIL.getValue() == tradeStatus) {
            return true;
        }
        return false;
    }
    
    @Override
    public AliPayDetailsVO doTransCreatePayDetails(AliPayDetailsVO payDetailsVO) {
        // 校验参数
        Validator.checkArgument(payDetailsVO == null, "payDetailsVO为空");
        Validator.checkArgument(payDetailsVO.getTotalAmount() == null, "totalAmount为空");
        Validator.checkArgument(StringUtils.isBlank(payDetailsVO.getPayType()), "payType为空");
        Validator.checkArgument(!NumberUtils.isCreatable(payDetailsVO.getPayType()), "payType应该是数字");
        final int payType = Integer.valueOf(payDetailsVO.getPayType());
        Validator.checkArgument(10 < payType || payType < 6, "payType超出范围");
        
        AliPayDetailsVO returnVO = null;
        if (StringUtils.equals(PayType.ALI_FACE_BAR.getValue(), payDetailsVO.getPayType())) {
        	logger.info("创建订单，支付方式：支付宝-当面付-条码支付");
            returnVO = createF2FBarPayDetail(payDetailsVO);
        } else if (StringUtils.equals(PayType.ALI_H5.getValue(), payDetailsVO.getPayType())) {
            logger.info("创建订单，支付方式：支付宝-手机网站支付");
            returnVO = createWapPayDetail(payDetailsVO);
        } else {
        	logger.warn("创建订单失败，不支持当前支付，payType={}", payType);
        } 

        return returnVO;
    }

    @Override
    public AliPayDetailsVO doTransUpdateFace2FacePayResult(String code, String subCode, AliPayDetailsVO payResultVO, Integer updateTradeStatus) {
        // 校验参数
        Validator.checkArgument(payResultVO == null, "payResultVO为空");
        Validator.checkArgument(StringUtils.isBlank(code), "code为空");
        
        Validator.checkArgument(StringUtils.isBlank(payResultVO.getOutTradeNo()),"outTradeNo为空");
        String outTradeNo = payResultVO.getOutTradeNo();
        logger.info("支付宝条码支付结果：outTradeNo : {}, code : {}, msg : {}, subCode : {}, subMsg : {}", outTradeNo, code, payResultVO.getMsg(), subCode, payResultVO.getSubMsg());
        
        Date processBeginTime = new Date();
        
        // 查找支付明细
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from AliPayDetails w where w.outTradeNo=:OUTTRADENO";
        jpqlMap.put("OUTTRADENO", outTradeNo);
        
        AliPayDetails payDetails = commonDAO.findObject(jpql, jpqlMap, false, LockModeType.PESSIMISTIC_WRITE);
        if (payDetails == null) {
            throw new NotExistsException("支付宝支付明细不存在，outTradeNo=" + outTradeNo);
        }
        
        AliPayDetailsVO returnPayDetailVO = null;
        
        payDetails.setCode(code);
        payDetails.setMsg(payResultVO.getMsg());
        
        String oldPayDetailStr = payDetails.toString();        
        StringBuffer logDescBuffer = new StringBuffer("修改支付宝支付明细[");
        logDescBuffer.append("code：");
        logDescBuffer.append(code);
        logDescBuffer.append("，msg：");
        logDescBuffer.append(payResultVO.getMsg());
        
        int tradeStatus = payDetails.getTradeStatus();
        
        if (StringUtils.equals(GateWayResponse.SUCCESS.getCode(), code)) {// 支付成功
            Validator.checkArgument(StringUtils.isBlank(payResultVO.getTradeNo()), "支付宝支付订单ID(tradeNo)为空");
            Validator.checkArgument(payResultVO.getTotalAmount() == null, "订单金额(totalAmount)为空");
            
            tradeStatus = updateTradeStatus == null ? TradeStatus.TRADE_SUCCESS.getValue() : updateTradeStatus;
            // 校验金额
            if (TradeStatus.TRADE_SUCCESS.getValue() == tradeStatus && payDetails.getTotalAmount().intValue() != payResultVO.getTotalAmount().intValue()) {
                logger.error(StringHelper.combinedString(AlarmLogPrefix.aliPayAPIMoneyException.getValue(), 
                    "金额不一致，需要人工处理，系统订单ID=" + payResultVO.getOutTradeNo(), 
                    "支付请求总金额："+ payDetails.getTotalAmount().intValue() + "，响应总金额：" + payResultVO.getTotalAmount().intValue()));
                tradeStatus = TradeStatus.MANUAL_HANDLING.getValue();
                payDetails.setRemark((StringUtils.isBlank(payDetails.getRemark()) ? "支付宝响应成功，但" : (payDetails.getRemark() +",")) + "金额不一致");
            }
            //TODO 其他金额校验
        } else {
            tradeStatus = updateTradeStatus == null ? TradeStatus.TRADE_FAIL.getValue() : updateTradeStatus;
            
            String errCode = StringUtils.isNotBlank(payResultVO.getSubCode()) ? payResultVO.getSubCode() : AliPayResult.FAIL.getCode();// 错误码
            String errCodeDes = payResultVO.getSubMsg();
            
            if (StringUtils.isBlank(errCodeDes)) {
                String codeTemp = errCode;
                if (StringUtils.startsWithIgnoreCase(errCode, "ACQ.")) {
                    codeTemp = StringUtils.substring(errCode, 4);
                }
                if (Validator.contains(AliPayResult.class, codeTemp)) {
                    errCodeDes = Enum.valueOf(AliPayResult.class, codeTemp).getDesc();
                }
            }
            payDetails.setSubCode(errCode);
            payDetails.setSubMsg(errCodeDes);
            
            if (tradeStatus == TradeStatus.MANUAL_HANDLING.getValue()) {
                logger.error(AlarmLogPrefix.handleAliPayResultException.getValue() + "支付宝条码支付(ouTradeNo={})，需要人工处理", outTradeNo);
            }
            
            logDescBuffer.append("，subCode：");
            logDescBuffer.append(payDetails.getSubCode());
            logDescBuffer.append("，subMsg：");
            logDescBuffer.append(payDetails.getSubMsg());
            
            // 支付异常时会记录备注
            if (StringUtils.isNotBlank(payResultVO.getRemark())) {
                payDetails.setRemark(payResultVO.getRemark());
            }
        }
        payDetails.setTradeStatus(tradeStatus);
        
        // 无论支付成功或失败、未知都更新结果信息
        payDetails.setTradeNo(payResultVO.getTradeNo());
        payDetails.setBuyerLogonId(payResultVO.getBuyerLogonId());
        payDetails.setBuyerUserId(payResultVO.getBuyerUserId());
        payDetails.setTotalAmount(payResultVO.getTotalAmount());
        payDetails.setReceiptAmount(payResultVO.getReceiptAmount());
        payDetails.setPointAmount(payResultVO.getPointAmount());
        payDetails.setInvoiceAmount(payResultVO.getInvoiceAmount());
        payDetails.setGmtPayment(payResultVO.getGmtPayment());
        //TODO fund_bill_list voucher_detail_list
        payDetails.setCardBalance(payResultVO.getCardBalance());
        payDetails.setStoreName(payResultVO.getStoreName());
        payDetails.setDiscountGoodsDetail(payResultVO.getDiscountGoodsDetail());
        
        
        logDescBuffer.append("tradeNo：");
        logDescBuffer.append(payResultVO.getTradeNo());
        logDescBuffer.append("，tradeStatus：");
        logDescBuffer.append(payDetails.getTradeStatus());
        logDescBuffer.append("，totalAmount：");
        logDescBuffer.append(payResultVO.getTotalAmount());
        logDescBuffer.append("，buyerLogonId：");
        logDescBuffer.append(payResultVO.getBuyerLogonId());
        logDescBuffer.append("，buyerUserId：");
        logDescBuffer.append(payResultVO.getBuyerUserId());
        logDescBuffer.append("，gmtPayment：");
        logDescBuffer.append(payResultVO.getGmtPayment());
        if (StringUtils.isNotBlank(payDetails.getRemark())) {
            logDescBuffer.append("，remark：");
            logDescBuffer.append(payDetails.getRemark());
        }
        logDescBuffer.append("]");
        
        /*logDescBuffer.append("，receiptAmount：");
        logDescBuffer.append(payResultVO.getReceiptAmount());
        logDescBuffer.append("，pointAmount：");
        logDescBuffer.append(payResultVO.getPointAmount());
        logDescBuffer.append("，invoiceAmount：");
        logDescBuffer.append(payResultVO.getInvoiceAmount());
        logDescBuffer.append("，invoiceAmount：");
        logDescBuffer.append(payResultVO.getInvoiceAmount());*/
        
        Date endTime = new Date();
        payDetails.setTransEndTime(new Timestamp(endTime.getTime()));
        commonDAO.update(payDetails);
        
        // 记录修改日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, "修改支付宝支付明细[" + logDescBuffer.toString() + "]", 
            processBeginTime, endTime, oldPayDetailStr, payDetails.toString(), SysLog.State.success.getValue(), payDetails.getIwoid(), null, SysLog.ActionType.modify.getValue());
        
        // 组装返回结果
        returnPayDetailVO = new AliPayDetailsVO();
        BeanCopierUtil.copyProperties(payDetails, returnPayDetailVO);
        if (payDetails.getTradeStatus().intValue() == TradeStatus.TRADE_SUCCESS.getValue()) {
	        returnPayDetailVO.setStoreOid(payDetails.getStore() != null ? payDetails.getStore().getIwoid() : "");
	        returnPayDetailVO.setStoreId(payDetails.getStore() != null ? payDetails.getStore().getStoreId() : "");
	        returnPayDetailVO.setDealerEmployeeOid(payDetails.getDealerEmployee() != null ? payDetails.getDealerEmployee().getIwoid() : "");
	        returnPayDetailVO.setDealerEmployeeId(payDetails.getDealerEmployee() != null ? payDetails.getDealerEmployee().getDealerEmployeeId() : "");
	        returnPayDetailVO.setDealerEmployeeName(payDetails.getDealerEmployee() != null ? payDetails.getDealerEmployee().getEmployeeName() : "");
	        returnPayDetailVO.setStoreName(payDetails.getStore() != null ? payDetails.getStore().getStoreName() : "");// FIXME storeName
	        returnPayDetailVO.setDealerName(payDetails.getDealer() != null ? payDetails.getDealer().getCompany() : "");
        }
        
        return returnPayDetailVO;
    }
    
    /**
     * 生成、保存条码支付明细
     * @param payDetailsVO
     * @return
     */
    private AliPayDetailsVO createF2FBarPayDetail(AliPayDetailsVO payDetailsVO) {
        Validator.checkArgument(StringUtils.isBlank(payDetailsVO.getDealerEmployeeOid()), "dealerEmployeeOid为空");
        Validator.checkArgument(StringUtils.isBlank(payDetailsVO.getAuthCode()), "authCode为空");
        
        // 生成明细
        AliPayDetails newPayOrder = newAliPayDetail(payDetailsVO, PayType.ALI_FACE_BAR.getValue());
        
        // 根据系统配置的支持当面付应用ID查找系统维护的蚂蚁平台应用
        newPayOrder.setAppId(SysConfig.appId4Face2FacePay);
        
        logger.info("查找应用({}) - 开始", newPayOrder.getAppId());
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from AlipayApp a where a.appId=:APPID";
        jpqlMap.put("APPID", newPayOrder.getAppId());

        AlipayApp app = commonDAO.findObject(jpql, jpqlMap, false);
        if (app == null) {
            throw new NotExistsException("AlipayApp不存在（appId=" + newPayOrder.getAppId() + "）");
        }
        logger.info("查找应用({}) - 结束", newPayOrder.getAppId());
        
        // 查找商户授权令牌
        jpqlMap.clear();
        jpql = "from AlipayAppAuthDetails a where a.dealer=:DEALER and a.alipayApp=:ALIPAYAPP and a.status=:STATUS order by a.createTime desc";
        jpqlMap.put("DEALER", newPayOrder.getDealer());
        jpqlMap.put("ALIPAYAPP", app);
        jpqlMap.put("STATUS", AlipayAppAuthDetails.AppAuthStatus.VALID.toString());
        AlipayAppAuthDetails appAuth = commonDAO.findObject(jpql, jpqlMap, false);
        if (appAuth == null) {
            throw new NotExistsException("AlipayAppAuthDetails不存在（appId=" + newPayOrder.getAppId() + "，商户ID=" + newPayOrder.getDealer().getDealerId() +"）");
        }
        
        // 代替商户发起当面付，设置商户授权令牌
        newPayOrder.setAppAuthToken(appAuth.getAppAuthToken());

        // 商户操作员和商户门店，FIXME 接口中描述，这些参数都可以做统计和精准定位
        newPayOrder.setOperatorId(newPayOrder.getDealerEmployee().getDealerEmployeeId());
        newPayOrder.setStoreId(newPayOrder.getStore().getStoreId());
        newPayOrder.setSellerId(newPayOrder.getDealer().getAlipayUserId());//FIXME
        // terminal_id
        // alipay_store_id
        // undiscountable_amount
        // discountable_amount
        
        newPayOrder.setScene(AliPayDetails.Scene.BAR_CODE.toString());
        // 订单标题，暂取 品牌(商户名)-门店
        newPayOrder.setSubject(newPayOrder.getDealer().getCompany() + (newPayOrder.getStore() == null ? "" : "-" + newPayOrder.getStore().getStoreName()));
        
        newPayOrder.setBody(newPayOrder.getSubject());
        newPayOrder.setPayType(PayType.ALI_FACE_BAR.getValue() + "");
        newPayOrder.setTotalAmount(payDetailsVO.getTotalAmount());
        newPayOrder.setAuthCode(payDetailsVO.getAuthCode());
        newPayOrder.setCreator(newPayOrder.getDealerEmployee().getIwoid());// 收银员的Oid
        commonDAO.save(newPayOrder, false);
        // 记录日志
        Date processTime = new Date();
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, 
            "新增支付宝支付明细[系统内部订单ID=" + newPayOrder.getOutTradeNo() + "支付方式=条码支付, 商户=" + newPayOrder.getDealer().getDealerId() + "，消费金额：" + newPayOrder.getTotalAmount() + ", 商品详情=" + newPayOrder.getBody() + "]", 
            processTime, processTime, null, newPayOrder.toString(), SysLog.State.success.getValue(), newPayOrder.getIwoid(), null, SysLog.ActionType.create.getValue());
        
        BeanCopierUtil.copyProperties(newPayOrder, payDetailsVO);
        return payDetailsVO;
    }
    
    /**
     * 生成、保存手机网站支付明细
     * @param payDetailsVO
     * @return
     */
    private AliPayDetailsVO createWapPayDetail(AliPayDetailsVO payDetailsVO) {
        Validator.checkArgument(StringUtils.isBlank(payDetailsVO.getStoreOid()), "storeOid为空");
        
        // 生成明细
        AliPayDetails newPayOrder = newAliPayDetail(payDetailsVO, PayType.ALI_H5.getValue());
        newPayOrder.setPayType(PayType.ALI_H5.getValue() + "");       
        
        // 根据系统配置的支持手机网站支付应用ID查找系统维护的蚂蚁平台应用
        newPayOrder.setAppId(SysConfig.appId4Face2FacePay);//FIXME
        
        logger.info("查找应用({}) - 开始", newPayOrder.getAppId());
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from AlipayApp a where a.appId=:APPID";
        jpqlMap.put("APPID", newPayOrder.getAppId());

        AlipayApp app = commonDAO.findObject(jpql, jpqlMap, false);
        if (app == null) {
            throw new NotExistsException("AlipayApp不存在（appId=" + newPayOrder.getAppId() + "）");
        }
        logger.info("查找应用({}) - 结束", newPayOrder.getAppId());
        
        // 查找商户授权令牌         // FIXME 手机网站支付不支持第三方授权，只能考虑设置seller_id试试
        
        //---------------请求必填项------------//
        // 订单标题，暂取 品牌(商户名)-门店
        newPayOrder.setSubject(newPayOrder.getDealer().getCompany() + (newPayOrder.getStore() == null ? "" : "-" + newPayOrder.getStore().getStoreName()));
        newPayOrder.setTotalAmount(payDetailsVO.getTotalAmount());
        //product_code 销售产品码，商家和支付宝签约的产品码
        
        //---------------请求非必填项------------//
        newPayOrder.setBody(newPayOrder.getSubject());
        newPayOrder.setStoreId(newPayOrder.getStore().getStoreId());
        
        // seller_id 默认为空，手机网站支付必填为商户的支付宝PID        
        newPayOrder.setSellerId(newPayOrder.getDealer().getAlipayUserId());// FIXME 需要验证能否支持以收款人不同类区分不同商户不用授权就可以手机网站支付
        
        //timeout_express //该笔订单允许的最晚付款时间，逾期将关闭交易。
        //auth_token // 针对用户授权接口，获取用户相关数据时，用于标识用户授权关系
        //goods_type //商品主类
        //passback_params //公用回传参数
        //promo_params //优惠参数
        //extend_params //业务扩展参数
        //enable_pay_channels //可用渠道
        //disable_pay_channels //禁用渠道
        
        //newPayOrder.setCreator(creator);// 考虑支付宝静默授权获取用户标识
        commonDAO.save(newPayOrder, false);
        // 记录日志
        Date processTime = new Date();
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, 
            "新增支付宝支付明细[系统内部订单ID=" + newPayOrder.getOutTradeNo() + "支付方式=手机网站支付, 商户=" + newPayOrder.getDealer().getDealerId() + "，下单金额：" + newPayOrder.getTotalAmount() + ", 商品详情=" + newPayOrder.getBody() + "]", 
            processTime, processTime, null, newPayOrder.toString(), SysLog.State.success.getValue(), newPayOrder.getIwoid(), null, SysLog.ActionType.create.getValue());
        
        BeanCopierUtil.copyProperties(newPayOrder, payDetailsVO);
        return payDetailsVO;
    }
    
    
    /** 
     * 创建包含公共属性的支付明细
     * @param payDetailsVO
     * param payType 针对支付类型做限制
     * @return
     */
    private AliPayDetails newAliPayDetail(AliPayDetailsVO payDetailsVO, String payType) {
        AliPayDetails aliPayDetails = new AliPayDetails();
        DealerEmployee dealerEmployee = null;
        Dealer dealer = null;
        Store store = null;
        
        if (StringUtils.isNotBlank(payDetailsVO.getDealerEmployeeOid())) {
            dealerEmployee = commonDAO.findObject(DealerEmployee.class, payDetailsVO.getDealerEmployeeOid());
            if (dealerEmployee == null) {
                throw new NotExistsException("收银员不存在！");
            }
            dealer = dealerEmployee.getDealer();
            store = dealerEmployee.getStore();
        }
        
        if (store == null && StringUtils.isNotBlank(payDetailsVO.getStoreOid())) {
            store = commonDAO.findObject(Store.class, payDetailsVO.getStoreOid());
            if (store == null) {
                logger.warn("门店不存在，storeOid=" + payDetailsVO.getStoreOid());
            }
            dealer = dealer == null ? store.getDealer() : dealer;
        }

        if (dealer == null && StringUtils.isNotBlank(payDetailsVO.getDealerOid())) {
            // 查找商户
            dealer = commonDAO.findObject(Dealer.class, payDetailsVO.getDealerOid());
        }
        if (dealer == null) {
            throw new NotExistsException("商户不存在！");
        } else if (StringUtils.isBlank(dealer.getAlipayUserId()) && StringUtils.equals(PayType.ALI_H5.getValue(), payType)) {// 支付宝商户的PID或UID
            throw new InvalidValueException("商户信息缺失：手机网站支付，alipayUserId不能为空！");
        } else if (StringUtils.isBlank(dealer.getPartner1Oid())) {
            throw new InvalidValueException("商户信息缺失：partner1Oid为空！");
        }

        // 查找服务商
        Partner topPartner = commonDAO.findObject(Partner.class, dealer.getPartner1Oid());
        if (topPartner == null) {
            throw new NotExistsException("服务商不存在！");
        } else if (StringUtils.isBlank(topPartner.getIsvPartnerId())) {// 支付宝服务商的PID
            throw new InvalidValueException("服务商信息缺失：isvPartnerId为空！");
        }
        
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        // 获取 服务商员工ID下一个序列值
        String sql = "select nextval('" + SysSequenceCode.PAY_ORDER + "') as sequence_value";
        Object seqObj = commonDAO.findObject(sql, jpqlMap, true);
        if (seqObj == null) {
            throw new IllegalArgumentException("支付订单Id对应序列记录不存在");
        }
        aliPayDetails.setOutTradeNo(Generator.generateSequenceYYYYMMddNum((Integer)seqObj, SysSequenceMultiple.PAY_ORDER));// 商户订单号
        
        aliPayDetails.setIwoid(Generator.generateIwoid());
        /*--------系统服务商、业务员、商户、门店、收银员-------*/
        aliPayDetails.setPartner(dealer.getPartner());
        aliPayDetails.setPartnerLevel(dealer.getPartnerLevel());
        aliPayDetails.setPartner1Oid(dealer.getPartner1Oid());
        aliPayDetails.setPartner2Oid(dealer.getPartner2Oid());
        aliPayDetails.setPartner3Oid(dealer.getPartner3Oid());
        aliPayDetails.setPartnerEmployee(dealer.getPartnerEmployee());
        aliPayDetails.setDealer(dealer);
        aliPayDetails.setStore(store);
        aliPayDetails.setDealerEmployee(dealerEmployee);
        aliPayDetails.setTradeStatus(TradeStatus.TRADEING.getValue());
        aliPayDetails.setTransBeginTime(new Timestamp(new Date().getTime()));
        aliPayDetails.setCreator(payDetailsVO.getDealerEmployeeOid());
        
        /*返佣必填项，支付请求设置 extend_params 中sys_service_provider_id参数的值*/
        aliPayDetails.setIsvPartnerId(topPartner.getIsvPartnerId());
        
        return aliPayDetails;
    }

    @Override
    public void doTransUpdatePayDetailState(String outTradeNo, int tradeStatus, String remark) {
        Validator.checkArgument(StringUtils.isBlank(outTradeNo), "outTradeNo为空");
        
        // 查找支付明细
        AliPayDetails payDetails = doJoinTransQueryAliPayDetailsByNum(outTradeNo, null, LockModeType.PESSIMISTIC_WRITE);
        if (payDetails == null) {
            throw new NotExistsException("支付宝支付明细不存在，outTradeNo=" + outTradeNo);
        }
        String oldPayDetailStr = payDetails.toString();
        StringBuffer logDescBuffer = new StringBuffer("修改支付宝明细[");
        
        payDetails.setTradeStatus(tradeStatus);
        payDetails.setRemark(payDetails.getRemark() + remark);
        
        logDescBuffer.append(", tradeStatus：");
        logDescBuffer.append(payDetails.getTradeStatus());
        if (StringUtils.isNotBlank(remark)) {
            payDetails.setRemark(payDetails.getRemark() + remark);
            logDescBuffer.append(", remark：");
            logDescBuffer.append(remark);
        }
        if (tradeIsEnd(tradeStatus)) {
            // 更新结束时间
            Date endDate = new Date();
            payDetails.setTransEndTime(DateUtil.getTimestamp(endDate));
            logDescBuffer.append(", endTime：");
            logDescBuffer.append(endDate);
        }
        logDescBuffer.append("]");
        
        commonDAO.update(payDetails);
        Date logTime = new Date();
        // 记录修改日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, logDescBuffer.toString(), 
            logTime, logTime, oldPayDetailStr, payDetails.toString(), SysLog.State.success.getValue(), payDetails.getIwoid(), null, SysLog.ActionType.modify.getValue());
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> doJoinTransQueryAliPayDetails(Map<String, Object> paramMap, int startIndex, int maxResult) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        List<AliPayDetailsVO> resultList = new ArrayList<AliPayDetailsVO>();
       
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
        String tradeNo = MapUtils.getString(paramMap, "tradeNo");// 支付宝单号
        
        String jpqlSelect = "select distinct(w) from AliPayDetails w LEFT JOIN w.partner LEFT JOIN w.partnerEmployee LEFT JOIN w.dealer LEFT JOIN w.store LEFT JOIN w.dealerEmployee where 1=1 ";
        
        StringBuffer conditionSB = new StringBuffer();
        
        Map<String, Object> jpqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(partner1Oid)) {
            conditionSB.append(" and w.partner1Oid = :PARTNER1OID");
            jpqlMap.put("PARTNER1OID", partner1Oid);
        }
        if (StringUtils.isNotBlank(partner2Oid)) {
            conditionSB.append(" and w.partner2Oid = :PARTNER2OID");
            jpqlMap.put("PARTNER2OID", partner2Oid);
        }
        if (StringUtils.isNotBlank(partner3Oid)) {
            conditionSB.append(" and w.partner3Oid = :PARTNER3OID");
            jpqlMap.put("PARTNER3OID", partner3Oid);
        }
        if (StringUtils.isNotBlank(partnerEmployeeOid)) {
            conditionSB.append(" and w.partnerEmployee.iwoid = :PARTNEREMPLOYEEOID");
            jpqlMap.put("PARTNEREMPLOYEEOID", partnerEmployeeOid);
        }
        if (StringUtils.isNotBlank(dealerOid)) {
            conditionSB.append(" and w.dealer.iwoid = :DEALEROID");
            jpqlMap.put("DEALEROID", dealerOid);
        }
        if (StringUtils.isNotBlank(storeOid)) {
            conditionSB.append(" and w.store.iwoid = :STOREOID");
            jpqlMap.put("STOREOID", storeOid);
        }
        if (StringUtils.isNotBlank(dealerEmployeeOid)) {
            conditionSB.append(" and w.dealerEmployee.iwoid = :DEALEREMPLOYEEOID");
            jpqlMap.put("DEALEREMPLOYEEOID", dealerEmployeeOid);
        }
        
        if (StringUtils.isNotBlank(partnerEmployeeId)) {
            conditionSB.append(" and w.partnerEmployee.partnerEmployeeId like :PARTNEREMPLOYEEID");
            jpqlMap.put("PARTNEREMPLOYEEID", "%" + partnerEmployeeId + "%");
        }
        if (StringUtils.isNotBlank(dealerId)) {
            conditionSB.append(" and w.dealer.dealerId like :DEALERID");
            jpqlMap.put("DEALERID", "%" + dealerId + "%");
        }
        if (StringUtils.isNotBlank(storeId)) {
            conditionSB.append(" and w.store.storeId like :STOREID");
            jpqlMap.put("STOREID", "%" + storeId + "%");
        }
        if (StringUtils.isNotBlank(dealerEmployeeId)) {
            conditionSB.append(" and w.dealerEmployee.dealerEmployeeId like :DEALEREMPLOYEEID");
            jpqlMap.put("DEALEREMPLOYEEID", "%" + dealerEmployeeId + "%");
        }
        
        if (beginTime != null ) {
            conditionSB.append(" and w.transBeginTime >=:BEGINTIME ");
            jpqlMap.put("BEGINTIME", beginTime);
        }
        if (endTime != null ) {
            conditionSB.append(" and w.transBeginTime <=:ENDTIME ");
            jpqlMap.put("ENDTIME", endTime);
        }
        if (StringUtils.isNotBlank(payType)) {// 支付类型
            conditionSB.append(" and w.payType = :PAYTYPE");
            jpqlMap.put("PAYTYPE", payType);
        }
        if (StringUtils.isNotBlank(outTradeNo)) {
            conditionSB.append(" and w.outTradeNo = :OUTTRADENO");
            jpqlMap.put("OUTTRADENO", outTradeNo);
        }
        if (StringUtils.isNotBlank(tradeNo)) {
            conditionSB.append(" and w.tradeNo = :TRADENO");
            jpqlMap.put("TRADENO", tradeNo);
        }

        conditionSB.append(" order by w.transBeginTime desc");
        List<AliPayDetails> aliPayDetailsList = (List<AliPayDetails>) commonDAO.findObjectList(jpqlSelect + conditionSB.toString(), jpqlMap, false, startIndex, maxResult);
  
        // 总笔数为记录总数，总金额为交易成功的总金额
        if(aliPayDetailsList != null && !aliPayDetailsList.isEmpty()) {
            for (AliPayDetails aliPayDetails : aliPayDetailsList) {
                AliPayDetailsVO vo = new AliPayDetailsVO();
                //BeanCopierUtil.copyProperties(aliPayDetails, vo);
                
                vo.setOutTradeNo(aliPayDetails.getOutTradeNo());
                vo.setTradeStatus(aliPayDetails.getTradeStatus());
                vo.setRefundFee(aliPayDetails.getRefundFee());
                
                DealerEmployee de = aliPayDetails.getDealerEmployee();
                vo.setDealerEmployeeName(de != null ? de.getEmployeeName() : "");
                vo.setDealerEmployeeId(de != null ? de.getDealerEmployeeId() : "");
                
                Store store = aliPayDetails.getStore();
                vo.setStoreName(store != null ? store.getStoreName() : (de != null ? de.getStore().getStoreName() : ""));
                vo.setStoreId(store != null ? store.getStoreId() : (de != null ? de.getStore().getStoreId() : ""));
                
                Dealer dealer = aliPayDetails.getDealer();
                vo.setDealerName(dealer != null ? dealer.getCompany() : (de != null ? de.getDealer().getCompany() : "" ));
                vo.setDealerId(dealer != null ? dealer.getDealerId() : (de != null ? de.getDealer().getDealerId() : "" ));
                
                PartnerEmployee pe = aliPayDetails.getPartnerEmployee();
                vo.setPartnerEmployeeName(pe != null ? pe.getEmployeeName() : (dealer != null ? dealer.getPartnerEmployee().getEmployeeName() : ""));
                vo.setPartnerEmployeeId(pe != null ? pe.getPartnerEmployeeId() : (dealer != null ? dealer.getPartnerEmployee().getPartnerEmployeeId() : ""));
                
                Partner p = aliPayDetails.getPartner();
                vo.setPartnerName(p != null ? p.getCompany() : (dealer != null ? dealer.getPartner().getCompany() : ""));
                vo.setPartnerId(p != null ? p.getPartnerId() : (dealer != null ? dealer.getPartner().getPartnerId() : ""));
                
                vo.setPayType(aliPayDetails.getPayType());
                vo.setTradeNo(aliPayDetails.getTradeNo());
                vo.setTotalAmount(aliPayDetails.getTotalAmount());
                
                vo.setTransBeginTime(aliPayDetails.getTransBeginTime());
                
                resultList.add(vo);
            }
        }
        
        //计算合计信息
        jpqlSelect = "select sum(case when w.tradeStatus=1 then w.totalAmount else 0 end),count(w.totalAmount) from AliPayDetails w LEFT JOIN w.partner LEFT JOIN w.partnerEmployee LEFT JOIN w.dealer LEFT JOIN w.store LEFT JOIN w.dealerEmployee where 1=1 ";
        
        long totalMoney = 0;
        long totalAmount = 0;
        List<?> aliTotalList = (List<?>) commonDAO.findObjectList(jpqlSelect + conditionSB.toString(), jpqlMap, false);
        for (Iterator<?> it = aliTotalList.iterator(); it.hasNext();) {
            Object[] curRow = (Object[]) it.next();
            totalMoney = curRow[0] == null ? 0L : (Long) curRow[0];
            totalAmount = curRow[1] == null ? 0L : (Long) curRow[1];
        }

        PayTotalVO totalVO = new PayTotalVO();
        totalVO.setTotalAmount(totalAmount);
        totalVO.setTotalMoney(totalMoney);
        resultMap.put("payList", resultList);
        resultMap.put("total", totalVO);
        return resultMap;
    }

    @Override
    public int doJoinTransQueryAliPayDetailsCount(Map<String, Object> paramMap) {
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
        String tradeNo = MapUtils.getString(paramMap, "tradeNo");// 支付宝单号

        StringBuffer sql = new StringBuffer("select count(distinct w.iwoid) from AliPayDetails w LEFT JOIN w.partner LEFT JOIN w.partnerEmployee LEFT JOIN w.dealer LEFT JOIN w.store LEFT JOIN w.dealerEmployee where 1=1 ");
        
        Map<String, Object> jpqlMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(partner1Oid)) {
            sql.append(" and w.partner1Oid = :PARTNER1OID");
            jpqlMap.put("PARTNER1OID", partner1Oid);
        }
        if (StringUtils.isNotBlank(partner2Oid)) {
            sql.append(" and w.partner2Oid = :PARTNER2OID");
            jpqlMap.put("PARTNER2OID", partner2Oid);
        }
        if (StringUtils.isNotBlank(partner3Oid)) {
            sql.append(" and w.partner3Oid = :PARTNER3OID");
            jpqlMap.put("PARTNER3OID", partner3Oid);
        }
        if (StringUtils.isNotBlank(partnerEmployeeOid)) {
            sql.append(" and w.partnerEmployee.iwoid = :PARTNEREMPLOYEEOID");
            jpqlMap.put("PARTNEREMPLOYEEOID", partnerEmployeeOid);
        }
        if (StringUtils.isNotBlank(dealerOid)) {
            sql.append(" and w.dealer.iwoid = :DEALEROID");
            jpqlMap.put("DEALEROID", dealerOid);
        }
        if (StringUtils.isNotBlank(storeOid)) {
            sql.append(" and w.store.iwoid = :STOREOID");
            jpqlMap.put("STOREOID", storeOid);
        }
        if (StringUtils.isNotBlank(dealerEmployeeOid)) {
            sql.append(" and w.dealerEmployee.iwoid = :DEALEREMPLOYEEOID");
            jpqlMap.put("DEALEREMPLOYEEOID", dealerEmployeeOid);
        }
        
        if (StringUtils.isNotBlank(partnerEmployeeId)) {
            sql.append(" and w.partnerEmployee.partnerEmployeeId like :PARTNEREMPLOYEEID");
            jpqlMap.put("PARTNEREMPLOYEEID", "%" + partnerEmployeeId + "%");
        }
        if (StringUtils.isNotBlank(dealerId)) {
            sql.append(" and w.dealer.dealerId like :DEALERID");
            jpqlMap.put("DEALERID", "%" + dealerId + "%");
        }
        if (StringUtils.isNotBlank(storeId)) {
            sql.append(" and w.store.storeId like :STOREID");
            jpqlMap.put("STOREID", "%" + storeId + "%");
        }
        if (StringUtils.isNotBlank(dealerEmployeeId)) {
            sql.append(" and w.dealerEmployee.dealerEmployeeId like :DEALEREMPLOYEEID");
            jpqlMap.put("DEALEREMPLOYEEID", "%" + dealerEmployeeId + "%");
        }
        
        if (beginTime != null ) {
            sql.append(" and w.transBeginTime >=:BEGINTIME ");
            jpqlMap.put("BEGINTIME", beginTime);
        }
        if (endTime != null ) {
            sql.append(" and w.transBeginTime <=:ENDTIME ");
            jpqlMap.put("ENDTIME", endTime);
        }
        if (StringUtils.isNotBlank(payType)) {// 支付类型
            sql.append(" and w.payType = :PAYTYPE");
            jpqlMap.put("PAYTYPE", payType);
        }
        if (StringUtils.isNotBlank(outTradeNo)) {
            sql.append(" and w.outTradeNo = :OUTTRADENO");
            jpqlMap.put("OUTTRADENO", outTradeNo);
        }
        if (StringUtils.isNotBlank(tradeNo)) {
            sql.append(" and w.tradeNo = :TRADENO");
            jpqlMap.put("TRADENO", tradeNo);
        }
        
        return commonDAO.queryObjectCount(sql.toString(), jpqlMap, false);
    }
    
    @Override
    public AliPayDetailsVO doJoinTransQueryAliPayDetailsVOByNum(String outTradeNo, String tradeNo) {
        Validator.checkArgument(StringUtils.isBlank(outTradeNo) && StringUtils.isBlank(tradeNo),"outTradeNo/tradeNo都为空");
        AliPayDetailsVO payDetailVO = null;
       
        // 查找支付明细
        AliPayDetails payDetails = doJoinTransQueryAliPayDetailsByNum(outTradeNo, tradeNo, null);
            
        if (payDetails != null) {
            payDetailVO = new AliPayDetailsVO();
            BeanCopierUtil.copyProperties(payDetails, payDetailVO);
            payDetailVO.setDealerName(payDetails.getDealer().getCompany());
            payDetailVO.setStoreName(payDetails.getStore().getStoreName());
        }
        
        return payDetailVO;
    }

    @Override
    public AliPayDetails doJoinTransQueryAliPayDetailsByNum(String outTradeNo, String tradeNo, LockModeType lockModeType) {
        Validator.checkArgument(StringUtils.isBlank(outTradeNo) && StringUtils.isBlank(tradeNo),"outTradeNo/tradeNo都为空");
        
        // 查找支付明细
        AliPayDetails payDetails = null;
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from AliPayDetails w where 1=1 ";
        if (StringUtils.isNotBlank(outTradeNo)) {
            jpql += " and w.outTradeNo=:OUTTRADENO";
            jpqlMap.put("OUTTRADENO", outTradeNo);
            if (lockModeType != null) {
                payDetails = commonDAO.findObject(jpql, jpqlMap, false, lockModeType);
            } else {
                payDetails = commonDAO.findObject(jpql, jpqlMap, false);
            }
        }
        
        if (payDetails == null && StringUtils.isNotBlank(tradeNo)) {
            jpql = "from AliPayDetails w where w.tradeNo=:TRADENO";
            jpqlMap.clear();
            jpqlMap.put("TRADENO", tradeNo);
            payDetails = commonDAO.findObject(jpql, jpqlMap, false);
        }
            
        return payDetails;
    }

    @Override
    public AliPayDetailsVO doTransUpdateQueryTradeSuccessResult(AliPayDetailsVO queryPayResultVO) {
        // 校验参数
        Validator.checkArgument(queryPayResultVO == null, "payResultVO为空");
        
        Validator.checkArgument(StringUtils.isBlank(queryPayResultVO.getOutTradeNo()),"outTradeNo为空");
        Validator.checkArgument(StringUtils.isBlank(queryPayResultVO.getTradeNo()), "支付宝支付订单ID(tradeNo)为空");
        Validator.checkArgument(queryPayResultVO.getTotalAmount() == null, "订单金额(totalAmount)为空");

        String outTradeNo = queryPayResultVO.getOutTradeNo();
        logger.info("支付宝查询支付结果：outTradeNo : {}, code : {}, msg : {}, subCode : {}, subMsg : {}", outTradeNo, queryPayResultVO.getCode(), queryPayResultVO.getMsg(), queryPayResultVO.getSubCode(), queryPayResultVO.getSubMsg());
        
        Date processBeginTime = new Date();
        
        // 查找支付明细
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        String jpql = "from AliPayDetails w where w.outTradeNo=:OUTTRADENO";
        jpqlMap.put("OUTTRADENO", outTradeNo);
        
        AliPayDetails payDetails = commonDAO.findObject(jpql, jpqlMap, false, LockModeType.PESSIMISTIC_WRITE);
        if (payDetails == null) {
            throw new NotExistsException("支付宝支付明细不存在，outTradeNo=" + outTradeNo);
        }
        
        AliPayDetailsVO returnPayDetailVO = null;
        
        // 查询的响应码，由于是交易成功时调用，所以结果应该都是成功
        payDetails.setCode(queryPayResultVO.getCode());
        payDetails.setMsg(queryPayResultVO.getMsg());
        
        String oldPayDetailStr = payDetails.toString();        
        StringBuffer logDescBuffer = new StringBuffer("修改支付宝支付明细[");
        
        logDescBuffer.append("code：");
        logDescBuffer.append(queryPayResultVO.getCode());
        logDescBuffer.append("，msg：");
        logDescBuffer.append(queryPayResultVO.getMsg());
        
        // 更新交易状态
        int tradeStatus = queryPayResultVO.getTradeStatus();
        
        // 校验金额
        if (payDetails.getTotalAmount().intValue() != queryPayResultVO.getTotalAmount().intValue()) {
            logger.error(StringHelper.combinedString(AlarmLogPrefix.aliPayAPIMoneyException.getValue(), 
                "金额不一致，需要人工处理，系统订单ID=" + queryPayResultVO.getOutTradeNo(), 
                "支付请求总金额："+ payDetails.getTotalAmount().intValue() + "，查询交易响应总金额：" + queryPayResultVO.getTotalAmount().intValue()));
            tradeStatus = TradeStatus.MANUAL_HANDLING.getValue();
            payDetails.setRemark((StringUtils.isBlank(payDetails.getRemark()) ? "查询支付宝交易响应成功，但" : (payDetails.getRemark() +",")) + "金额不一致");
        } else {
            payDetails.setRemark(payDetails.getRemark() + queryPayResultVO.getRemark());
        }
        //TODO 其他金额校验
        
        payDetails.setTradeStatus(tradeStatus);
        
        payDetails.setTradeNo(queryPayResultVO.getTradeNo());
        payDetails.setBuyerLogonId(queryPayResultVO.getBuyerLogonId());
        payDetails.setBuyerUserId(queryPayResultVO.getBuyerUserId());
        payDetails.setTotalAmount(queryPayResultVO.getTotalAmount());
        payDetails.setReceiptAmount(queryPayResultVO.getReceiptAmount());
        payDetails.setPointAmount(queryPayResultVO.getPointAmount());
        payDetails.setInvoiceAmount(queryPayResultVO.getInvoiceAmount());
        payDetails.setGmtPayment(queryPayResultVO.getGmtPayment());
        //TODO fund_bill_list voucher_detail_list
        //payDetails.setCardBalance(queryPayResultVO.getCardBalance());// 查询结果不会返回
        payDetails.setStoreName(queryPayResultVO.getStoreName());
        payDetails.setDiscountGoodsDetail(queryPayResultVO.getDiscountGoodsDetail());
        
        
        logDescBuffer.append("tradeNo：");
        logDescBuffer.append(queryPayResultVO.getTradeNo());
        logDescBuffer.append("，tradeStatus：");
        logDescBuffer.append(payDetails.getTradeStatus());
        logDescBuffer.append("，totalAmount：");
        logDescBuffer.append(queryPayResultVO.getTotalAmount());
        logDescBuffer.append("，buyerLogonId：");
        logDescBuffer.append(queryPayResultVO.getBuyerLogonId());
        logDescBuffer.append("，buyerUserId：");
        logDescBuffer.append(queryPayResultVO.getBuyerUserId());
        logDescBuffer.append("，gmtPayment：");
        logDescBuffer.append(queryPayResultVO.getGmtPayment());
        if (StringUtils.isNotBlank(payDetails.getRemark())) {
            logDescBuffer.append("，remark：");
            logDescBuffer.append(payDetails.getRemark());
        }
        Date endTime = new Date();
        
        if (tradeIsEnd(tradeStatus)) {
            // 更新结束时间
            Date endDate = new Date();
            payDetails.setTransEndTime(DateUtil.getTimestamp(endDate));
            logDescBuffer.append(", endTime：");
            logDescBuffer.append(endDate);
        }
        
        logDescBuffer.append("]");
        
        /*logDescBuffer.append("，receiptAmount：");
        logDescBuffer.append(payResultVO.getReceiptAmount());
        logDescBuffer.append("，pointAmount：");
        logDescBuffer.append(payResultVO.getPointAmount());
        logDescBuffer.append("，invoiceAmount：");
        logDescBuffer.append(payResultVO.getInvoiceAmount());
        logDescBuffer.append("，invoiceAmount：");
        logDescBuffer.append(payResultVO.getInvoiceAmount());*/

        commonDAO.update(payDetails);
        
        // 记录修改日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, logDescBuffer.toString(), 
            processBeginTime, endTime, oldPayDetailStr, payDetails.toString(), SysLog.State.success.getValue(), payDetails.getIwoid(), null, SysLog.ActionType.modify.getValue());
        
        // 组装返回结果
        returnPayDetailVO = new AliPayDetailsVO();
        BeanCopierUtil.copyProperties(payDetails, returnPayDetailVO);
        if (payDetails.getTradeStatus().intValue() == TradeStatus.TRADE_SUCCESS.getValue()) {
            returnPayDetailVO.setStoreOid(payDetails.getStore() != null ? payDetails.getStore().getIwoid() : "");
            returnPayDetailVO.setStoreId(payDetails.getStore() != null ? payDetails.getStore().getStoreId() : "");
            returnPayDetailVO.setDealerEmployeeOid(payDetails.getDealerEmployee() != null ? payDetails.getDealerEmployee().getIwoid() : "");
            returnPayDetailVO.setDealerEmployeeId(payDetails.getDealerEmployee() != null ? payDetails.getDealerEmployee().getDealerEmployeeId() : "");
            returnPayDetailVO.setDealerEmployeeName(payDetails.getDealerEmployee() != null ? payDetails.getDealerEmployee().getEmployeeName() : "");
            returnPayDetailVO.setStoreName(payDetails.getStore() != null ? payDetails.getStore().getStoreName() : "");// FIXME storeName
            returnPayDetailVO.setDealerName(payDetails.getDealer() != null ? payDetails.getDealer().getCompany() : "");
        }
        
        return returnPayDetailVO;
    }

    @Override
    public void doTransUpdateNotifyResult(AlipayWapPayNotifyVO notifyVO, TradeStatus tradeStatus, String remark) {
        // 校验参数
        Validator.checkArgument(notifyVO == null, "notifyVO为空");
        Validator.checkArgument(StringUtils.isBlank(notifyVO.getOut_trade_no()),"notifyVO.outTradeNo为空");
        
        AliPayDetails payDetails = doJoinTransQueryAliPayDetailsByNum(notifyVO.getOut_trade_no(), null, LockModeType.PESSIMISTIC_WRITE);
        
        if (payDetails == null) {
            throw new NotExistsException("支付宝支付明细不存在，outTradeNo=" + notifyVO.getOut_trade_no());
        }
        String oldPayDetailStr = payDetails.toString();
        StringBuffer logDescBuffer = new StringBuffer("修改支付宝明细[");

        // 更新通知内容
        payDetails.setNotifyId(notifyVO.getNotify_id());
        payDetails.setNotifyTime(DateUtil.getTimestamp(DateUtil.getDate(notifyVO.getNotify_time(), SysEnvKey.TIME_PATTERN_YMD_HYPHEN_HMS_COLON)));

        logDescBuffer.append("notifyId：");
        logDescBuffer.append(payDetails.getNotifyId());
        logDescBuffer.append(", notifyTime：");
        logDescBuffer.append(payDetails.getNotifyTime());
        if (notifyVO.getGmt_refund() != null) {
            payDetails.setGmtRefund(DateUtil.getTimestamp(DateUtil.getDate(notifyVO.getGmt_refund(), SysEnvKey.TIME_PATTERN_YMD_HYPHEN_HMS_COLON)));
            logDescBuffer.append(", gmtRefund：");
            logDescBuffer.append(payDetails.getGmtRefund());
        }
        if (notifyVO.getGmt_close() != null) {
            payDetails.setGmtClose(DateUtil.getTimestamp(DateUtil.getDate(notifyVO.getGmt_close(), SysEnvKey.TIME_PATTERN_YMD_HYPHEN_HMS_COLON)));
            logDescBuffer.append(", gmtClose：");
            logDescBuffer.append(payDetails.getGmtClose());
        }
        if (tradeStatus != null) {
            payDetails.setTradeStatus(tradeStatus.getValue());
            logDescBuffer.append(", tradeStatus：");
            logDescBuffer.append(payDetails.getTradeStatus());
            
            if (tradeIsEnd(tradeStatus.getValue())) {
                // 更新结束时间
                Date endDate = new Date();
                payDetails.setTransEndTime(DateUtil.getTimestamp(endDate));
                logDescBuffer.append(", endTime：");
                logDescBuffer.append(endDate);
            }
        }
        if (StringUtils.isNotBlank(remark)) {
            payDetails.setRemark(payDetails.getRemark() + remark);
            logDescBuffer.append(", remark：");
            logDescBuffer.append(remark);
        }
        logDescBuffer.append("]");
        
        commonDAO.update(payDetails);
        Date logDate = new Date();
        // 记录修改日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, logDescBuffer.toString(), 
            logDate, logDate, oldPayDetailStr, payDetails.toString(), SysLog.State.success.getValue(), payDetails.getIwoid(), null, SysLog.ActionType.modify.getValue());
    }

}
