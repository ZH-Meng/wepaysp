package com.zbsp.wepaysp.api.util;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang3.StringUtils;

import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.zbsp.alipay.trade.model.ExtendParams;
import com.zbsp.alipay.trade.model.builder.AlipayTradePayRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayTradeWapPayRequestBuilder;
import com.zbsp.wepaysp.common.constant.AliPayEnums.TradeState4AliPay;
import com.zbsp.wepaysp.common.constant.SysEnums.TradeStatus;
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

            // 业务扩展参数
            ExtendParams extendParams = new ExtendParams();
            // 设置返佣帐号，支付宝分配的系统商编号(通过setSysServiceProviderId方法)
            extendParams.setSysServiceProviderId(payDetailsVO.getIsvPartnerId());
            
            // FIXME 条码支付超时
            String timeoutExpress = SysEnvKey.EXPIRE_TIME_ALI_PAY_1M;
            
            // 应用授权令牌
            String appAuthToken = payDetailsVO.getAppAuthToken();

            // 创建条码支付请求builder，设置请求参数
            builder.setAppAuthToken(appAuthToken)
                .setOutTradeNo(payDetailsVO.getOutTradeNo())
                .setAuthCode(payDetailsVO.getAuthCode())
                .setSubject(payDetailsVO.getSubject())
                .setBody(payDetailsVO.getBody())
                .setSellerId(sellerId)
                .setTotalAmount(totalAmount)
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

    public static AlipayTradeWapPayRequestBuilder aliPayDetailsVO2AlipayTradeWapPayRequestBuilder(AliPayDetailsVO payDetailsVO) {
        AlipayTradeWapPayRequestBuilder builder = new AlipayTradeWapPayRequestBuilder();
        try {
            // (必填) 订单总金额，单位为元，不能超过1亿元
            String totalAmount = new BigDecimal(payDetailsVO.getTotalAmount()).divide(new BigDecimal(100)).toString();
        
            // 卖家支付宝账号ID
            String sellerId = payDetailsVO.getSellerId();

            // 业务扩展参数
            ExtendParams extendParams = new ExtendParams();
            // 设置返佣帐号，支付宝分配的系统商编号(通过setSysServiceProviderId方法)
            extendParams.setSysServiceProviderId(payDetailsVO.getIsvPartnerId());
            
            // 手机网站支付超时 设定 30分钟
            String timeoutExpress = SysEnvKey.EXPIRE_TIME_ALI_PAY_30M;
            
            // 创建条码支付请求builder，设置请求参数
            builder.setOutTradeNo(payDetailsVO.getOutTradeNo())
                .setSubject(payDetailsVO.getSubject())
                .setBody(payDetailsVO.getBody())
                .setSellerId(sellerId)
                .setTotalAmount(totalAmount)
                .setStoreId(payDetailsVO.getStoreId())
                .setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress);
        } catch (Exception e) {
            throw new ConvertPackException(e.getMessage());
        }
        return builder;
    }

    public static AliPayDetailsVO alipayTradeQueryResponse2AliPayDetailsVO(AlipayTradeQueryResponse queryResponse) {
        AliPayDetailsVO vo = new AliPayDetailsVO();
        try {
            // 公共响应参数
            vo.setCode(queryResponse.getCode());
            vo.setMsg(queryResponse.getMsg());
            vo.setSubCode(queryResponse.getSubCode());
            vo.setSubMsg(queryResponse.getSubMsg());

            // 必填
            // tradeStatus 交易状态转换
            if (TradeState4AliPay.WAIT_BUYER_PAY.toString().equals(queryResponse.getTradeStatus())) {
                vo.setTradeStatus(TradeStatus.TRADEING.getValue());
            } else if (TradeState4AliPay.TRADE_CLOSED.toString().equals(queryResponse.getTradeStatus())) {
                //FIXME 关闭含义有点区别
                vo.setTradeStatus(TradeStatus.TRADE_CLOSED.getValue());
            } else if (TradeState4AliPay.TRADE_FINISHED.toString().equals(queryResponse.getTradeStatus())) {
                //FIXME 
                vo.setTradeStatus(TradeStatus.TRADE_SUCCESS.getValue());
            } else if (TradeState4AliPay.TRADE_SUCCESS.toString().equals(queryResponse.getTradeStatus())) {
                vo.setTradeStatus(TradeStatus.TRADE_SUCCESS.getValue());
            }
            
            vo.setTradeNo(queryResponse.getTradeNo());
            vo.setOutTradeNo(queryResponse.getOutTradeNo());
            vo.setBuyerLogonId(queryResponse.getBuyerLogonId());// 买家支付宝账号
            if (StringUtils.isNotBlank(queryResponse.getTotalAmount())) {
                vo.setTotalAmount(new BigDecimal(queryResponse.getTotalAmount()).multiply(SysEnvKey.BIG_100).intValue());// 交易金额
            }
            if (StringUtils.isNotBlank(queryResponse.getReceiptAmount())) {
                vo.setReceiptAmount(new BigDecimal(queryResponse.getReceiptAmount()).multiply(SysEnvKey.BIG_100).intValue());// 实收金额
            }
            if (queryResponse.getSendPayDate() != null) {//本次交易打款给卖家的时间
                // 返回值没有gmt_payment，但是保存至gmt_payment字段中
                vo.setGmtPayment(new Timestamp(queryResponse.getSendPayDate().getTime()));
            }
            
            // fund_bill_list // 交易支付使用的资金渠道        
            //TODO discount_goods_detail //本次交易支付所使用的单品券优惠的商品优惠信息

            // 选填
            if (StringUtils.isNotBlank(queryResponse.getBuyerPayAmount())) {
                vo.setBuyerPayAmount(new BigDecimal(queryResponse.getBuyerPayAmount()).multiply(SysEnvKey.BIG_100).intValue());// 买家支付金额
            }
            if (StringUtils.isNotBlank(queryResponse.getPointAmount())) {
                vo.setPointAmount(new BigDecimal(queryResponse.getPointAmount()).multiply(SysEnvKey.BIG_100).intValue());// 使用积分宝付款的金额
            }
            if (StringUtils.isNotBlank(queryResponse.getInvoiceAmount())) {
                vo.setInvoiceAmount(new BigDecimal(queryResponse.getInvoiceAmount()).multiply(SysEnvKey.BIG_100).intValue());// 交易中可给用户开具发票的金额
            }
            
            vo.setStoreName(queryResponse.getStoreName());// 发生支付交易的商户门店名称        
            vo.setBuyerUserId(queryResponse.getBuyerUserId());//买家在支付宝的用户id
            
            // voucher_detail_list
            
            // 一些参数是交易请求参数，暂不处理
            //alipay_store_id 
            //alipay_store_id
            //terminal_id
        } catch (Exception e) {
            throw new ConvertPackException(e.getMessage());
        }
        return vo;
    }

    public static AlipayTradePrecreateRequestBuilder aliPayDetailsVO2AlipayTradePrecreateRequestBuilder(AliPayDetailsVO payDetailsVO) {
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder();
        try {
            // (必填) 订单总金额，单位为元，不能超过1亿元
            String totalAmount = new BigDecimal(payDetailsVO.getTotalAmount()).divide(new BigDecimal(100)).toString();
        
            // 卖家支付宝账号ID
            String sellerId = payDetailsVO.getSellerId();

            // 业务扩展参数
            ExtendParams extendParams = new ExtendParams();
            // 设置返佣帐号，支付宝分配的系统商编号(通过setSysServiceProviderId方法)
            extendParams.setSysServiceProviderId(payDetailsVO.getIsvPartnerId());
            // 扫码支付2小时有效
            
            // 创建条码支付请求builder，设置请求参数
            builder.setAppAuthToken(payDetailsVO.getAppAuthToken())
                .setOutTradeNo(payDetailsVO.getOutTradeNo())
                .setSubject(payDetailsVO.getSubject())
                .setBody(payDetailsVO.getBody())
                .setSellerId(sellerId)
                .setTotalAmount(totalAmount)
                .setUndiscountableAmount(null)
                .setStoreId(payDetailsVO.getStoreId())            
                .setExtendParams(extendParams)
                .setGoodsDetailList(null);
        } catch (Exception e) {
            throw new ConvertPackException(e.getMessage());
        }
        return builder;
    }
}
