package com.zbsp.wepaysp.api.service.edu.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.edu.AlipayEduNotifyService;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.AliPayEnums.TradeState4AliPay;
import com.zbsp.wepaysp.common.exception.AlreadyExistsException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.po.edu.AlipayEduBill;
import com.zbsp.wepaysp.po.edu.AlipayEduNotify;
import com.zbsp.wepaysp.vo.alipay.AlipayWapPayNotifyVO;


public class AlipayEduNotifyServiceImpl
    extends BaseService
    implements AlipayEduNotifyService {

    @Override
    public AlipayEduNotify doTransSaveEduNotify(AlipayWapPayNotifyVO notifyVO) {
        Validator.checkArgument(notifyVO == null, "notifyVO 不能为空");
        
        // 检查通知是否存在
        Map<String, Object> jpqlMap = new HashMap<String, Object>();
        jpqlMap.put("NOTIFYID", notifyVO.getNotify_id());
        AlipayEduNotify existNotify = commonDAO.findObject("from AlipayEduNotify a where a.notifyId=:NOTIFYID", jpqlMap, false);
        if (existNotify != null) {
            throw new AlreadyExistsException("缴费账单明细支付异步通知已存在，notifyId=" + notifyVO.getNotify_id());
        }
        
        // 回调链接里面的out_trade_no , 在不出现关闭交易帐单的情况下，就是发账单返回的order_no;
        // 交易关闭的账单，发账单时返回的orderNo参数，会通过passback_params这个参数带回，此参数通过base64（utf-8）编码，使用时需要反编码。反编译出来的样式orderNo=58acf6f8fc4fee266c866d44
        String orderNo = notifyVO.getOut_trade_no();// k12平台的outTradeNo
        if (StringUtils.isBlank(orderNo) && TradeState4AliPay.TRADE_CLOSED.name().equalsIgnoreCase(notifyVO.getTrade_status())) {// 交易账单关闭
            logger.warn("orderNo为空，交易账单关闭，passback_params={}", notifyVO.getPassback_params());
            String orderNoStr = new String(Base64.decodeBase64(notifyVO.getPassback_params()));
            if (orderNoStr != null && orderNoStr.lastIndexOf("orderNo=") != -1) {
                orderNo = orderNoStr.split("=")[1];
            }
        }
        logger.info("账单K12OrderNo:" + orderNo);
        
        String appId = notifyVO.getApp_id();
        String totalAmountStr = notifyVO.getTotal_amount();
        Validator.checkArgument(StringUtils.isBlank(orderNo), "orderNo为空");
        Validator.checkArgument(StringUtils.isBlank(appId), "appId为空");
        Validator.checkArgument(StringUtils.isBlank(totalAmountStr), "totalAmount为空");
        Validator.checkArgument(!NumberUtils.isCreatable(totalAmountStr) || !Pattern.matches(SysEnvKey.REGEX_￥_POSITIVE_FLOAT_2BIT, totalAmountStr), "totalAmount(" + totalAmountStr + ")格式不正确");
        Validator.checkArgument(StringUtils.isBlank(notifyVO.getTrade_status()), "trade_status为空");
        
        jpqlMap.clear();
        jpqlMap.put("ORDERNO", orderNo);
        AlipayEduBill bill = commonDAO.findObject("from AlipayEduBill a where a.k12OrderNo=:ORDERNO", jpqlMap, false);
        if (bill == null) {
            throw new NotExistsException("缴费账单明细不存在，k12OrderNo=" + orderNo);
        }
        
        AlipayEduNotify notify = new AlipayEduNotify();
        notify.setIwoid(Generator.generateIwoid());
        notify.setAlipayEduBill(bill);
        notify.setOutTradeNo(orderNo);// 非本系统的单号
        notify.setBody(notifyVO.getBody());
        notify.setBuyerId(notifyVO.getBuyer_id());
        notify.setBuyerLogonId(notifyVO.getBuyer_logon_id());
        notify.setSubject(notifyVO.getSubject());
        notify.setTradeStatus(notifyVO.getTrade_status());
        notify.setTradeNo(notifyVO.getTrade_no());
        
        notify.setNotifyId(notifyVO.getNotify_id());
        notify.setNotifyType(notifyVO.getNotify_type());
        notify.setNotifyTime(DateUtil.getDate(notifyVO.getNotify_time(), SysEnvKey.TIME_PATTERN_YMD_HYPHEN_HMS_COLON));
        notify.setSellerId(notifyVO.getSeller_id());
        notify.setSellerEmail(notifyVO.getSeller_email());
        notify.setReceiptAmount(BigDecimal.valueOf(NumberUtils.toDouble(notifyVO.getReceipt_amount())).multiply(SysEnvKey.BIG_100).intValue());
        notify.setInvoiceAmount(BigDecimal.valueOf(NumberUtils.toDouble(notifyVO.getInvoice_amount())).multiply(SysEnvKey.BIG_100).intValue());
        notify.setBuyerPayAmount(BigDecimal.valueOf(NumberUtils.toDouble(notifyVO.getBuyer_pay_amount())).multiply(SysEnvKey.BIG_100).intValue());
        notify.setPointAmount(BigDecimal.valueOf(NumberUtils.toDouble(notifyVO.getPoint_amount())).multiply(SysEnvKey.BIG_100).intValue());
        notify.setTotalAmoun(BigDecimal.valueOf(NumberUtils.toDouble(notifyVO.getTotal_amount())).multiply(SysEnvKey.BIG_100).intValue());
        notify.setRefundFee(BigDecimal.valueOf(NumberUtils.toDouble(notifyVO.getRefund_fee())).multiply(SysEnvKey.BIG_100).intValue());
        notify.setOutBizNo(notifyVO.getOut_biz_no());
        notify.setFundBillList(notifyVO.getFund_bill_list());
        notify.setVoucherDetailList(notifyVO.getVoucher_detail_list());
        
        if (notifyVO.getGmt_create() != null) {
            notify.setGmtCreate(DateUtil.getDate(notifyVO.getGmt_create(), SysEnvKey.TIME_PATTERN_YMD_HYPHEN_HMS_COLON));
        }
        if (notifyVO.getGmt_payment() != null) {
            notify.setGmtPayment(DateUtil.getDate(notifyVO.getGmt_payment(), SysEnvKey.TIME_PATTERN_YMD_HYPHEN_HMS_COLON));
        }
        if (notifyVO.getGmt_refund() != null) {
            notify.setGmtRefund(DateUtil.getDate(notifyVO.getGmt_refund(), SysEnvKey.TIME_PATTERN_YMD_HYPHEN_HMS_COLON));
        }
        if (notifyVO.getGmt_close() != null) {
            notify.setGmtClose(DateUtil.getDate(notifyVO.getGmt_close(), SysEnvKey.TIME_PATTERN_YMD_HYPHEN_HMS_COLON));
        }
        
        commonDAO.save(notify, false);
        return notify;
    }

}
