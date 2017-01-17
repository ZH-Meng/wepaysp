package com.zbsp.wepaysp.api.util;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;

import com.alipay.api.response.AlipayTradePayResponse;
import com.zbsp.alipay.trade.model.ExtendParams;
import com.zbsp.alipay.trade.model.builder.AlipayTradePayRequestBuilder;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.exception.ConvertPackException;
import com.zbsp.wepaysp.vo.pay.AliPayDetailsVO;

/**
 * 支付宝接口请求/响应包转换器
 * 
 * @author 孟郑宏
 */
public class AliPayPackConverter {
    
    /**
     * 支付宝支付明细VO为支付宝支付请求构造器
     * @param payDetailsVO AliPayDetailsVO
     * @return AlipayTradePayRequestBuilder
     */
    public static AlipayTradePayRequestBuilder aliPayDetailsVO2AlipayTradePayRequestBuilder(AliPayDetailsVO payDetailsVO) throws ConvertPackException {
        AlipayTradePayRequestBuilder builder = new AlipayTradePayRequestBuilder();
        try {
            // (必填) 订单总金额，单位为元，不能超过1亿元
            // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
            String totalAmount = new BigDecimal(payDetailsVO.getTotalAmount()).divide(new BigDecimal(100)).toString();
        
            // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
            // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
            String sellerId = payDetailsVO.getSellerId();

            // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
            String providerId = payDetailsVO.getSysServiceProviderId();
            ExtendParams extendParams = new ExtendParams();
            extendParams.setSysServiceProviderId(providerId);
            
            String timeoutExpress = "5m"; // 支付超时，线下扫码交易定义为5分钟
            String appAuthToken = payDetailsVO.getAppAuthToken(); // 应用授权令牌

            // 创建条码支付请求builder，设置请求参数
            builder.setAppAuthToken(appAuthToken)
                .setAppAuthToken(appAuthToken)
                .setOutTradeNo(payDetailsVO.getOutTradeNo())
                .setAuthCode(payDetailsVO.getAuthCode())
                .setSubject(payDetailsVO.getSubject())
                .setBody(payDetailsVO.getBody())
                .setSellerId(sellerId).setTotalAmount(totalAmount)
                .setUndiscountableAmount(null)
                .setStoreId(payDetailsVO.getStoreId())
                .setOperatorId(payDetailsVO.getOperatorId())
                .setExtendParams(extendParams)
                .setGoodsDetailList(null)
                .setTimeoutExpress(timeoutExpress);
        } catch (Exception e) {
            throw new ConvertPackException(e.getMessage());
        }
        
        return builder;
    }
    
    /**
     * 支付响应转换为支付宝支付明细VO
     * @param alipayTradePayResponse AlipayTradePayResponse
     * @return AliPayDetailsVO
     */
    public static AliPayDetailsVO alipayTradePayResponse2AliPayDetailsVO(AlipayTradePayResponse alipayTradePayResponse) throws ConvertPackException {
        AliPayDetailsVO vo = new AliPayDetailsVO();
        try {
            // 公共响应参数
            vo.setCode(alipayTradePayResponse.getCode());
            vo.setMsg(alipayTradePayResponse.getMsg());
            vo.setSubCode(alipayTradePayResponse.getSubCode());
            vo.setSubMsg(alipayTradePayResponse.getSubMsg());

            // 必填
            vo.setTradeNo(alipayTradePayResponse.getTradeNo());
            vo.setOutTradeNo(alipayTradePayResponse.getOutTradeNo());
            vo.setBuyerLogonId(alipayTradePayResponse.getBuyerLogonId());// 买家支付宝账号
            if (StringUtils.isNotBlank(alipayTradePayResponse.getTotalAmount())) {
                vo.setTotalAmount(new BigDecimal(alipayTradePayResponse.getTotalAmount()).multiply(SysEnvKey.BIG_100).intValue());// 交易金额
            }
            if (StringUtils.isNotBlank(alipayTradePayResponse.getReceiptAmount())) {
                vo.setReceiptAmount(new BigDecimal(alipayTradePayResponse.getReceiptAmount()).multiply(SysEnvKey.BIG_100).intValue());// 实收金额
            }
            if (alipayTradePayResponse.getGmtPayment() != null) {
                vo.setGmtPayment(new Timestamp(alipayTradePayResponse.getGmtPayment().getTime()));
            }
            
            // fund_bill_list // 交易支付使用的资金渠道        
            //TODO discount_goods_detail //本次交易支付所使用的单品券优惠的商品优惠信息

            // 选填
            if (StringUtils.isNotBlank(alipayTradePayResponse.getBuyerPayAmount())) {
                vo.setBuyerPayAmount(new BigDecimal(alipayTradePayResponse.getBuyerPayAmount()).multiply(SysEnvKey.BIG_100).intValue());// 买家支付金额
            }
            if (StringUtils.isNotBlank(alipayTradePayResponse.getPointAmount())) {
                vo.setPointAmount(new BigDecimal(alipayTradePayResponse.getPointAmount()).multiply(SysEnvKey.BIG_100).intValue());// 使用积分宝付款的金额
            }
            if (StringUtils.isNotBlank(alipayTradePayResponse.getInvoiceAmount())) {
                vo.setInvoiceAmount(new BigDecimal(alipayTradePayResponse.getInvoiceAmount()).multiply(SysEnvKey.BIG_100).intValue());// 交易中可给用户开具发票的金额
            }
            if (StringUtils.isNotBlank(alipayTradePayResponse.getCardBalance())) {
                vo.setCardBalance(new BigDecimal(alipayTradePayResponse.getCardBalance()).multiply(SysEnvKey.BIG_100).longValue());// 支付宝卡余额
            }
            vo.setStoreName(alipayTradePayResponse.getStoreName());// 发生支付交易的商户门店名称        
            vo.setBuyerUserId(alipayTradePayResponse.getBuyerUserId());//买家在支付宝的用户id
            
            // voucher_detail_list
        } catch (Exception e) {
            throw new ConvertPackException(e.getMessage());
        }
        return vo;
    }
}
