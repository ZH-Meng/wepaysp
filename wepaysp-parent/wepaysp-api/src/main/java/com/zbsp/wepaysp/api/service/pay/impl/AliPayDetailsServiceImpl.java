package com.zbsp.wepaysp.api.service.pay.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.manage.SysLogService;
import com.zbsp.wepaysp.api.service.pay.AliPayDetailsService;
import com.zbsp.wepaysp.common.config.SysSequenceCode;
import com.zbsp.wepaysp.common.config.SysSequenceMultiple;
import com.zbsp.wepaysp.common.constant.EnumDefine.PayType;
import com.zbsp.wepaysp.common.exception.InvalidValueException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.BeanCopierUtil;
import com.zbsp.wepaysp.common.util.Generator;
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
        Validator.checkArgument(!NumberUtils.isCreatable(payDetailsVO.getPayType()), "payType为空");
        
        AliPayDetailsVO returnVO = null;
        
        PayType payType = null;
        try {
            payType = PayType.valueOf(payDetailsVO.getPayType());
        } catch (Exception e) {
            throw new IllegalArgumentException(" payType(" + payDetailsVO.getPayType() + ")无效");
        }
        
        switch (payType) {
            case ALI_FACE_BAR:
                logger.info("创建订单，支付方式：支付宝-当面付-条码支付");
                returnVO = createF2FBarPayDetail(payDetailsVO);
                break;

            default:
                logger.warn("创建订单失败，不支持当前支付，payType={}", payType);
                break;
        }

        return returnVO;
    }
    
    private AliPayDetailsVO createF2FBarPayDetail(AliPayDetailsVO payDetailsVO) {
        Validator.checkArgument(StringUtils.isBlank(payDetailsVO.getDealerEmployeeOid()), "dealerEmployeeOid为空");
        Validator.checkArgument(StringUtils.isBlank(payDetailsVO.getAuthCode()), "authCode为空");
        
        // 生成明细
        AliPayDetails newPayOrder = newAliPayDetail(payDetailsVO);
        
        
        //newPayOrder.setAppAuthToken(dealer.get);// TODO 授权令牌
        //newPayOrder.setAppid();// 应用ID
        //newPayOrder.setDealerPid(dealerPid);        
        //newPayOrder.setOperatorId(operatorId);
        //newPayOrder.setStoreId(storeId);
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
        newPayOrder.setPayType(PayType.ALI_FACE_BAR + "");
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
    
        /** 创建包含公共属性的支付明细 */
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
