package com.zbsp.wepaysp.api.service.main.pay;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.zbsp.alipay.trade.model.ExtendParams;
import com.zbsp.alipay.trade.model.builder.AlipayTradePayRequestBuilder;
import com.zbsp.alipay.trade.model.result.AlipayF2FPayResult;
import com.zbsp.alipay.trade.service.AlipayTradeService;
import com.zbsp.wepaysp.api.service.BaseService;
import com.zbsp.wepaysp.api.service.pay.AliPayDetailsService;
import com.zbsp.wepaysp.api.util.AliPayUtil;
import com.zbsp.wepaysp.vo.pay.AliPayDetailsVO;

public class AliPayDetailsMainServiceImpl
    extends BaseService
    implements AliPayDetailsMainService {

    private AliPayDetailsService aliPayDetailsService;
    
    @Override
    public Map<String, Object> face2FaceBarPay(AliPayDetailsVO payDetailsVO) {
        // 生成保存支付明细；
        logger.info("生成支付宝支付明细 - 开始");
        /*String resCode = AliPayResult.ERROR;
        String resDesc = WxPayResult.ERROR.getDesc();*///TODO
        try {
            payDetailsVO = aliPayDetailsService.doTransCreatePayDetails(payDetailsVO);
            logger.error("生成支付宝支付明细 - 成功");
        } catch (Exception e) {
            logger.error("生成支付宝支付明细 - 失败");
            e.printStackTrace();
        } finally {
            logger.info("生成支付宝支付明细 - 结束");
        }
        
        // 创建成功后调用当面付接口
        AlipayTradeService service = AliPayUtil.getDefaultAlipayTradeService();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = new BigDecimal(payDetailsVO.getTotalAmount()).divide(new BigDecimal(100)).toString();

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        String providerId = "2088100200300400500";
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId(providerId);

        // 支付超时，线下扫码交易定义为5分钟
        String timeoutExpress = "5m";

        String appAuthToken = "应用授权令牌";//根据真实值填写

        // 创建条码支付请求builder，设置请求参数
        AlipayTradePayRequestBuilder builder = new AlipayTradePayRequestBuilder()
            //            .setAppAuthToken(appAuthToken)
            .setOutTradeNo(payDetailsVO.getOutTradeNo()).setSubject(payDetailsVO.getSubject()).setAuthCode(payDetailsVO.getAuthCode())
            .setTotalAmount(totalAmount).setStoreId(payDetailsVO.getStoreId())
            .setUndiscountableAmount(null).setBody(payDetailsVO.getBody()).setOperatorId(payDetailsVO.getOperatorId())
            .setExtendParams(extendParams).setSellerId(sellerId)
            .setGoodsDetailList(null).setTimeoutExpress(timeoutExpress);

        // 调用tradePay方法获取当面付应答
        AlipayF2FPayResult result = service.tradePay(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝支付成功: )");
                // 更新支付结果
                break;

            case FAILED:
                logger.error("支付宝支付失败!!!");
                // 更新支付结果
                break;

            case UNKNOWN:
                logger.error("系统异常，订单状态未知!!!");
                break;

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                break;
        }

        
        // 返回
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("aliPayDetailsVO", payDetailsVO);
        resultMap.put("resultCode", payDetailsVO);
        resultMap.put("resultDesc", payDetailsVO);
        
        return resultMap;
    }

    public void setAliPayDetailsService(AliPayDetailsService aliPayDetailsService) {
        this.aliPayDetailsService = aliPayDetailsService;
    }
    
}
