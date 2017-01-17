package com.zbsp.wepaysp.api.service.pay.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.LockModeType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.manage.SysLogService;
import com.zbsp.wepaysp.api.service.pay.AliPayDetailsService;
import com.zbsp.wepaysp.common.config.SysSequenceCode;
import com.zbsp.wepaysp.common.config.SysSequenceMultiple;
import com.zbsp.wepaysp.common.constant.AliPayEnums.AliPayResult;
import com.zbsp.wepaysp.common.constant.AliPayEnums.GateWayResponse;
import com.zbsp.wepaysp.common.constant.EnumDefine.AlarmLogPrefix;
import com.zbsp.wepaysp.common.constant.EnumDefine.PayType;
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
import com.zbsp.wepaysp.po.partner.Store;
import com.zbsp.wepaysp.po.pay.AliPayDetails;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails.TradeStatus;
import com.zbsp.wepaysp.vo.pay.AliPayDetailsVO;

public class AliPayDetailsServiceImpl
    extends BaseService
    implements AliPayDetailsService {
    
    private SysLogService sysLogService;
    
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
        if (PayType.ALI_FACE_BAR.getValue() == payType) {
        	logger.info("创建订单，支付方式：支付宝-当面付-条码支付");
            returnVO = createF2FBarPayDetail(payDetailsVO);
        } else {
        	logger.warn("创建订单失败，不支持当前支付，payType={}", payType);
        } 

        return returnVO;
    }
    
    @Override
    public AliPayDetailsVO doTransUpdateFace2FacePayResult(String code, String subCode, AliPayDetailsVO payResultVO) {
        // 校验参数
        Validator.checkArgument(payResultVO == null, "payResultVO为空");
        Validator.checkArgument(StringUtils.isBlank(code), "code为空");
        
        Validator.checkArgument(StringUtils.isBlank(payResultVO.getOutTradeNo()),"outTradeNo为空");
        String outTradeNo = payResultVO.getOutTradeNo();
        logger.info("支付宝条码支付结果：outTradeNo : {}, code : {}, subCode : {}", outTradeNo, code, subCode);
        
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
        
        String oldPayDetailStr = payDetails.toString();        
        payDetails.setCode(code);
        payDetails.setMsg(payResultVO.getMsg());
        String logDescTemp = "";
        if (StringUtils.equals(GateWayResponse.SUCCESS.getCode(), code)) {// 支付成功
            Validator.checkArgument(StringUtils.isBlank(payResultVO.getTradeNo()), "支付宝支付订单ID(tradeNo)为空");
            Validator.checkArgument(payResultVO.getTotalAmount() == null, "订单金额(totalAmount)为空");
            
            int tradeStatus = TradeStatus.TRADE_SUCCESS.getValue();
            // 校验金额
            if (payDetails.getTotalAmount().intValue() != payResultVO.getTotalAmount().intValue()) {
                logger.error(StringHelper.combinedString(AlarmLogPrefix.aliPayAPIMoneyException.getValue(), 
                    "金额不一致，系统订单ID=" + payResultVO.getOutTradeNo(), 
                    "支付请求总金额："+ payDetails.getTotalAmount().intValue() + "，响应总金额：" + payResultVO.getTotalAmount().intValue()));
                tradeStatus = TradeStatus.TRADE_FAIL.getValue();
                payDetails.setRemark((StringUtils.isBlank(payDetails.getRemark()) ? "支付宝响应成功，但" : (payDetails.getRemark() +",")) + "金额不一致");
            }
            //TODO 其他金额校验
            
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
            payDetails.setTradeStatus(tradeStatus);
            
            if (payDetails.getTradeStatus().intValue() == TradeStatus.TRADE_SUCCESS.getValue()) {
                logDescTemp += "支付结果：交易成功" + "，支付宝支付订单号：" + payResultVO.getTradeNo() + "，交易状态：" + tradeStatus + "，支付金额：" + payResultVO.getTotalAmount();
                
                // 组装返回结果
                returnPayDetailVO = new AliPayDetailsVO();
                BeanCopierUtil.copyProperties(payDetails, returnPayDetailVO);
                returnPayDetailVO.setStoreOid(payDetails.getStore() != null ? payDetails.getStore().getIwoid() : "");
                returnPayDetailVO.setStoreId(payDetails.getStore() != null ? payDetails.getStore().getStoreId() : "");
                returnPayDetailVO.setDealerEmployeeOid(payDetails.getDealerEmployee() != null ? payDetails.getDealerEmployee().getIwoid() : "");
                returnPayDetailVO.setDealerEmployeeId(payDetails.getDealerEmployee() != null ? payDetails.getDealerEmployee().getDealerEmployeeId() : "");
                returnPayDetailVO.setDealerEmployeeName(payDetails.getDealerEmployee() != null ? payDetails.getDealerEmployee().getEmployeeName() : "");
                returnPayDetailVO.setStoreName(payDetails.getStore() != null ? payDetails.getStore().getStoreName() : "");// FIXME storeName
                returnPayDetailVO.setDealerName(payDetails.getDealer() != null ? payDetails.getDealer().getCompany() : "");
            } else {
                logDescTemp += "，支付结果：交易失败，" + payDetails.getRemark() + "，支付宝支付订单号：" + payResultVO.getTradeNo() + "，交易状态：" + tradeStatus;
            }
        } else {
            String errCode = StringUtils.isNotBlank(payResultVO.getSubCode()) ? payResultVO.getSubCode() : AliPayResult.FAIL.getCode();// 错误码
            String errCodeDes = payResultVO.getSubMsg();
            
            if (StringUtils.isBlank(errCodeDes)) {
                if (Validator.contains(AliPayResult.class, errCode)) {
                    errCodeDes = Enum.valueOf(AliPayResult.class, errCode).getDesc();
                }
            }
            payDetails.setSubCode(errCode);
            payDetails.setSubMsg(errCodeDes);
            payDetails.setTradeStatus(TradeStatus.TRADE_FAIL.getValue());
            if (!StringUtils.equals(GateWayResponse.FAIL.getCode(), code)) {// 支付失败
                logger.error(AlarmLogPrefix.handleAliPayResultException.getValue() + "支付宝当面条码支付(ouTradeNo={})网关返回结果 - 错误 : {}", outTradeNo);
            }
            logDescTemp += "支付结果：交易失败，错误码：" + errCode + "，错误描述：" + errCodeDes;
        }
        Date endTime = new Date();
        payDetails.setTransEndTime(new Timestamp(endTime.getTime()));
        commonDAO.update(payDetails);
        
        // 记录修改日志
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, "修改支付宝支付明细[" + logDescTemp + "]", 
            processBeginTime, endTime, oldPayDetailStr, payDetails.toString(), SysLog.State.success.getValue(), payDetails.getIwoid(), null, SysLog.ActionType.modify.getValue());
        
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
        AliPayDetails newPayOrder = newAliPayDetail(payDetailsVO);
        
        
        //newPayOrder.setAppAuthToken(dealer.get);// TODO 授权令牌
        //newPayOrder.setAppid();// 应用ID
        //newPayOrder.setDealerPid(dealerPid);        
        //newPayOrder.setOperatorId(operatorId);
        newPayOrder.setStoreId(newPayOrder.getStore().getStoreId());//FIXME
        //newPayOrder.setSellerId
        //newPayOrder.providerId
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
        commonDAO.save(newPayOrder, false);
        // 记录日志
        Date processTime = new Date();
        sysLogService.doTransSaveSysLog(SysLog.LogType.userOperate.getValue(), null, 
            "新增支付宝支付明细[系统内部订单ID=" + newPayOrder.getOutTradeNo() + "支付方式=条码支付, 商户PID=" + newPayOrder.getDealerPid() + "，消费金额：" + newPayOrder.getTotalAmount() + ", 商品详情=" + newPayOrder.getBody() + "]", 
            processTime, processTime, null, newPayOrder.toString(), SysLog.State.success.getValue(), newPayOrder.getIwoid(), null, SysLog.ActionType.create.getValue());
        
        BeanCopierUtil.copyProperties(newPayOrder, payDetailsVO);
        return payDetailsVO;
    }
    
    /** 
     * 创建包含公共属性的支付明细
     * @param payDetailsVO
     * @return
     */
    private AliPayDetails newAliPayDetail(AliPayDetailsVO payDetailsVO) {
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
            /*
             * } else if (StringUtils.isBlank(dealer.getSubMchId())) { throw new InvalidValueException("商户信息缺失：sub_mch_id为空！");
             */ // TODO 签约商户的相关信息
        } else if (StringUtils.isBlank(dealer.getPartner1Oid())) {
            throw new InvalidValueException("商户信息缺失：partner1Oid为空！");
        }

        // 查找服务商
        Partner topPartner = commonDAO.findObject(Partner.class, dealer.getPartner1Oid());
        if (topPartner == null) {
            throw new NotExistsException("服务商不存在！");
        }
        
        Map<String, Object> sqlMap = new HashMap<String, Object>();
        // 获取 服务商员工ID下一个序列值
        String sql = "select nextval('" + SysSequenceCode.PAY_ORDER + "') as sequence_value";
        Object seqObj = commonDAO.findObject(sql, sqlMap, true);
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
        
        return aliPayDetails;
    }

    public void setSysLogService(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

}
