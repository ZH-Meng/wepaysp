package com.zbsp.wepaysp.api.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.zbsp.alipay.trade.model.ChargeItems;
import com.zbsp.alipay.trade.model.ExtendParams;
import com.zbsp.alipay.trade.model.UserDetails;
import com.zbsp.alipay.trade.model.builder.AlipayEcoEduKtBillingModifyRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayEcoEduKtBillingQueryRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayEcoEduKtBillingSendRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayEcoEduKtSchoolinfoModifyRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayTradePayRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.zbsp.alipay.trade.model.builder.AlipayTradeWapPayRequestBuilder;
import com.zbsp.alipay.trade.utils.Utils;
import com.zbsp.wepaysp.common.constant.AliPayEnums.TradeState4AliPay;
import com.zbsp.wepaysp.common.constant.SysEnums.TradeStatus;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.exception.ConvertPackException;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.po.edu.AlipayEduBill;
import com.zbsp.wepaysp.po.partner.School;
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
            // 扫码支付二维码2小时有效，扫码后订单需要设置
            
            // 手机网站支付超时 设定 30分钟
            String timeoutExpress = SysEnvKey.EXPIRE_TIME_ALI_PAY_30M;
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
                .setGoodsDetailList(null)
                .setTimeoutExpress(timeoutExpress);
        } catch (Exception e) {
            throw new ConvertPackException(e.getMessage());
        }
        return builder;
    }

    /**
     * @param school
     * @return
     */
	public static AlipayEcoEduKtSchoolinfoModifyRequestBuilder school2AlipayEcoEduKtSchoolinfoModifyRequestBuilder(
			School school) {

		AlipayEcoEduKtSchoolinfoModifyRequestBuilder builder = new AlipayEcoEduKtSchoolinfoModifyRequestBuilder();
		try {
			builder.setSchoolName(school.getShcoolName()).setSchoolIcon(school.getSchoolIcon())
					.setSchoolIconType(school.getSchoolIconType()).setSchoolStdcode(school.getSchoolStdcode())
					.setSchoolType(school.getSchoolType()).setProvinceCode(school.getProvinceCode())
					.setProvinceName(school.getProvinceName()).setCityCode(school.getCityCode())
					.setCityName(school.getCityName()).setDistrictCode(school.getDistrictCode())
					.setDistrictName(school.getDistrictName()).setIsvName(school.getIsvName())
					.setIsvNotifyUrl(school.getIsvNotifyUrl()).setIsvPid(school.getIsvPid())
					.setSchoolPid(school.getSchoolPid()).setIsvPhone(school.getIsvPhone())
					.setBankcardNo(school.getBankcardNo()).setBankUid(school.getBankUid())
					.setBankNotifyrl(school.getBankNotifyUrl());
		} catch (Exception e) {
			throw new ConvertPackException(e.getMessage());
		}
		return builder;
	}

    /**缴费账单发送请求包转换*/
	public static AlipayEcoEduKtBillingSendRequestBuilder alipayEduBill2AlipayEcoEduKtBillingSendRequestBuilder(AlipayEduBill alipayEduBill) {
		AlipayEcoEduKtBillingSendRequestBuilder builder = new AlipayEcoEduKtBillingSendRequestBuilder();
        try {
            UserDetails user = new UserDetails(alipayEduBill.getUserMobile(), alipayEduBill.getUserName(), alipayEduBill.getUserRelation(), alipayEduBill.getUserChangeMobile());
            
            List<UserDetails> users = new ArrayList<>();
            users.add(user);
            
        	builder.setUsers(users)
        	    .setSchoolPid(alipayEduBill.getSchoolPid())
        	    .setSchoolNo(alipayEduBill.getSchoolNo())
        	    .setChildName(alipayEduBill.getChildName())
        	    .setGrade(alipayEduBill.getGrade())
        	    .setClassIn(alipayEduBill.getClassIn())
        	    .setStudentCode(alipayEduBill.getStudentCode())
                .setStudentIdentify(alipayEduBill.getStudentIdentify())
                .setStatus(alipayEduBill.getStatus())
                .setOutTradeNo(alipayEduBill.getOutTradeNo())
                .setChargeBillTitle(alipayEduBill.getChargeBillTitle())
                .setChargeItem(JSONUtil.parseArray(alipayEduBill.getChargeItem(), ChargeItems.class))
                .setAmount(Utils.toAmount(alipayEduBill.getAmount()))
                // 必填项，所以为空时endEnable为N，设置时间也不会生效，但是设置当前时间又会有误差。所以设置当前时间加一天保证发送成功时间又不起作用
                .setGmtEnd(StringUtils.isBlank(alipayEduBill.getGmtEnd()) ? Utils.toDate(TimeUtil.plusSeconds(new Date(), 3600 * 24)) :alipayEduBill.getGmtEnd()) 
                .setEndEnable(alipayEduBill.getEndEnable())
                .setPartnerId(alipayEduBill.getIsvPartnerId());
        } catch (Exception e) {
            throw new ConvertPackException(e.getMessage());
        }
        return builder;
	}

	/**同步账单状态*/
    public static AlipayEcoEduKtBillingModifyRequestBuilder alipayEduBill2AlipayEcoEduKtBillingModifyRequestBuilder(AlipayEduBill alipayEduBill) {
        AlipayEcoEduKtBillingModifyRequestBuilder builder = new AlipayEcoEduKtBillingModifyRequestBuilder();
        try {
            builder.setOutTradeNo(alipayEduBill.getK12OrderNo());
        } catch (Exception e) {
            throw new ConvertPackException(e.getMessage());
        }
        return builder;
    }
    
    /**同步账单状态，1：缴费成功，2关闭账单，3退费，4同步网商状态返回的状态*/
    public static AlipayEcoEduKtBillingQueryRequestBuilder alipayEduBill2AlipayEcoEduKtBillingQueryRequestBuilder(AlipayEduBill alipayEduBill) {
        AlipayEcoEduKtBillingQueryRequestBuilder builder = new AlipayEcoEduKtBillingQueryRequestBuilder();
        try {
            builder.setOutTradeNo(alipayEduBill.getK12OrderNo())
               .setIsvPid(alipayEduBill.getIsvPartnerId())
               .setSchoolPid(alipayEduBill.getSchoolPid());
        } catch (Exception e) {
            throw new ConvertPackException(e.getMessage());
        }
        return builder;
    }

}
