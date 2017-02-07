package com.zbsp.alipay.trade.model.builder;

import org.apache.commons.lang.StringUtils;

import com.google.gson.annotations.SerializedName;
import com.zbsp.alipay.trade.model.ExtendParams;

public class AlipayTradeWapPayRequestBuilder
    extends RequestBuilder {

    private BizContent bizContent = new BizContent();

    @Override
    public BizContent getBizContent() {
        return bizContent;
    }

    @Override
    public boolean validate() {
        if (StringUtils.isEmpty(bizContent.outTradeNo)) {
            throw new NullPointerException("out_trade_no should not be NULL!");
        }
        if (StringUtils.isEmpty(bizContent.totalAmount)) {
            throw new NullPointerException("total_amount should not be NULL!");
        }
        if (StringUtils.isEmpty(bizContent.subject)) {
            throw new NullPointerException("subject should not be NULL!");
        }
        return true;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AlipayTradeWapPayRequestBuilder{");
        sb.append("bizContent=").append(bizContent);
        sb.append(", super=").append(super.toString());
        sb.append('}');
        return sb.toString();
    }

    public AlipayTradeWapPayRequestBuilder() {
        bizContent.productCode = "QUICK_WAP_PAY";
    }
    
    public String getSubject() {
        return bizContent.subject;
    }

    public AlipayTradeWapPayRequestBuilder setSubject(String subject) {
        bizContent.subject = subject;
        return this;
    }

    public String getBody() {
        return bizContent.body;
    }

    public AlipayTradeWapPayRequestBuilder setBody(String body) {
        bizContent.body = body;
        return this;
    }

    public String getOutTradeNo() {
        return bizContent.outTradeNo;
    }

    public AlipayTradeWapPayRequestBuilder setOutTradeNo(String outTradeNo) {
        bizContent.outTradeNo = outTradeNo;
        return this;
    }

    public String getTimeoutExpress() {
        return bizContent.timeoutExpress;
    }

    public AlipayTradeWapPayRequestBuilder setTimeoutExpress(String timeoutExpress) {
        bizContent.timeoutExpress = timeoutExpress;
        return this;
    }

    public String getTotalAmount() {
        return bizContent.totalAmount;
    }

    public AlipayTradeWapPayRequestBuilder setTotalAmount(String totalAmount) {
        bizContent.totalAmount = totalAmount;
        return this;
    }

    public String getProductCode() {
        return bizContent.productCode;
    }

    public AlipayTradeWapPayRequestBuilder setProductCode(String productCode) {
        bizContent.productCode = productCode;
        return this;
    }

    public String getSellerId() {
        return bizContent.sellerId;
    }

    public AlipayTradeWapPayRequestBuilder setSellerId(String sellerId) {
        bizContent.sellerId = sellerId;
        return this;
    }

    public String getAuthToken() {
        return bizContent.authToken;
    }

    public AlipayTradeWapPayRequestBuilder setAuthToken(String authToken) {
        bizContent.authToken = authToken;
        return this;
    }

    public String getGoodsType() {
        return bizContent.goodsType;
    }

    public AlipayTradeWapPayRequestBuilder setGoodsType(String goodsType) {
        bizContent.goodsType = goodsType;
        return this;
    }

    public String getPassbackParams() {
        return bizContent.passbackParams;
    }

    public AlipayTradeWapPayRequestBuilder setPassbackParams(String passbackParams) {
        bizContent.passbackParams = passbackParams;
        return this;
    }

    public String getPromoParams() {
        return bizContent.promoParams;
    }

    public AlipayTradeWapPayRequestBuilder setPromoParams(String promoParams) {
        bizContent.promoParams = promoParams;
        return this;
    }

    public ExtendParams getExtendParams() {
        return bizContent.extendParams;
    }

    public AlipayTradeWapPayRequestBuilder setExtendParams(ExtendParams extendParams) {
        bizContent.extendParams = extendParams;
        return this;
    }

    public String getEnablePayChannels() {
        return bizContent.enablePayChannels;
    }

    public AlipayTradeWapPayRequestBuilder setEnablePayChannels(String enablePayChannels) {
        bizContent.enablePayChannels = enablePayChannels;
        return this;
    }

    public String getDisablePayChannels() {
        return bizContent.disablePayChannels;
    }

    public AlipayTradeWapPayRequestBuilder setDisablePayChannels(String disablePayChannels) {
        bizContent.disablePayChannels = disablePayChannels;
        return this;
    }

    public String getStoreId() {
        return bizContent.storeId;
    }

    public AlipayTradeWapPayRequestBuilder setStoreId(String storeId) {
        bizContent.storeId = storeId;
        return this;
    }

    public static class BizContent {

        // 订单标题，粗略描述用户的支付目的。如“喜士多（浦东店）消费”
        private String subject;

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        private String body;

        // 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        @SerializedName("out_trade_no")
        private String outTradeNo;

        // (推荐使用，相对时间) 支付超时时间，5m 5分钟
        @SerializedName("timeout_express")
        private String timeoutExpress;

        // 订单总金额，整形，此处单位为元，精确到小数点后2位，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        @SerializedName("total_amount")
        private String totalAmount;

        @SerializedName("product_code")
        private String productCode;

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        @SerializedName("seller_id")
        private String sellerId;

        // 针对用户授权接口，获取用户相关数据时，用于标识用户授权关系
        @SerializedName("auth_token")
        private String authToken;

        // 商品主类型：0—虚拟类商品，1—实物类商品
        // 注：虚拟类商品不支持使用花呗渠道
        @SerializedName("goods_type")
        private String goodsType;

        // 公用回传参数，如果请求时传递了该参数，则返回给商户时会回传该参数。支付宝会在异步通知时将该参数原样返回。本参数必须进行UrlEncode之后才可以发送给支付宝
        @SerializedName("passback_params")
        private String passbackParams;

        // 优惠参数 注：仅与支付宝协商后可用
        @SerializedName("promo_params")
        private String promoParams;

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        @SerializedName("extend_params")
        private ExtendParams extendParams;

        // 可用渠道，用户只能在指定渠道范围内支付
        // 当有多个渠道时用“,”分隔
        // 注：与disable_pay_channels互斥
        @SerializedName("enable_pay_channels")
        private String enablePayChannels;

        // 禁用渠道，用户不可用指定渠道支付
        // 当有多个渠道时用“,”分隔
        // 注：与enable_pay_channels互斥
        @SerializedName("disable_pay_channels")
        private String disablePayChannels;

        // 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        @SerializedName("store_id")
        private String storeId;

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("BizContent{");
            sb.append("productCode='").append(productCode).append('\'');
            sb.append(", outTradeNo='").append(outTradeNo).append('\'');
            sb.append(", sellerId='").append(sellerId).append('\'');
            sb.append(", subject='").append(subject).append('\'');
            sb.append(", body='").append(body).append('\'');
            sb.append(", totalAmount='").append(totalAmount).append('\'');
            sb.append(", goodsType='").append(goodsType).append('\'');
            sb.append(", authToken='").append(authToken).append('\'');
            sb.append(", storeId='").append(storeId).append('\'');
            sb.append(", passbackParams='").append(passbackParams).append('\'');
            sb.append(", promoParams='").append(promoParams).append('\'');
            sb.append(", extendParams=").append(extendParams);
            sb.append(", enablePayChannels='").append(enablePayChannels).append('\'');
            sb.append(", disablePayChannels='").append(disablePayChannels).append('\'');
            sb.append(", timeoutExpress='").append(timeoutExpress).append('\'');
            sb.append('}');
            return sb.toString();
        }
        
    }

}
