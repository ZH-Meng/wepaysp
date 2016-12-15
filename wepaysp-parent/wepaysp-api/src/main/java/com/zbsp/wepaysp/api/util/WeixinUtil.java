package com.zbsp.wepaysp.api.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.tencent.WXPay;
import com.tencent.protocol.send_template_msg_protocol.SendTemplateMsgReqData;
import com.tencent.protocol.send_template_msg_protocol.SendTemplateMsgResData;
import com.tencent.protocol.send_template_msg_protocol.TemplateData;
import com.zbsp.wepaysp.common.util.DateUtil;
import com.zbsp.wepaysp.common.util.JSONUtil;
import com.zbsp.wepaysp.common.util.Validator;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;

/**
 * 微信接口工具类
 * 
 * @author 孟郑宏
 */
public class WeixinUtil {
    
    /**FIXME 可以在数据库中灵活配置需要通知的业务和模版ID的对应关系，以及模版ID的启用停用*/
    private static String TEMPLATE_ID_PAY_SUCCESS = "nJWFUU8wDvd7elT4znZivLLHmMYl_ajID6cd4OujHa0";
    
    /**
     * 发送订单支付成功通知，使用微信模版库模版（编号：OPENTM406213232，templateId=nJWFUU8wDvd7elT4znZivLLHmMYl_ajID6cd4OujHa0）
     * 
     * <pre>
     * 构造模版消息包；
     * 调用模版消息发送接口；
     * 返回消息发送结果（不能确定是否下发至微信用户，需要查看事件推送结果）；
     * </pre>
     * 
     * @param touser 微信用户在此公众号的唯一标识openID
     * @param certLocalPath
     * @param certPassword
     * @param accessToken 基础支持的接口accessToken
     * @return 发送消息结果（不能判断是否下发至微信用户）
     * @throws Exception
     */
    public static SendTemplateMsgResData sendPaySuccessNotice(WeixinPayDetailsVO payResultVO, String touser, String certLocalPath, String certPassword, String accessToken) throws Exception {
        Validator.checkArgument(payResultVO == null, "发送支付通知payResultVO不能为空");
        Validator.checkArgument(StringUtils.isBlank(touser),"发送支付通知touser不能为空");
        Validator.checkArgument(StringUtils.isBlank(certLocalPath),"发送支付通知certLocalPath不能为空");
        Validator.checkArgument(StringUtils.isBlank(certPassword),"发送支付通知certPassword不能为空");
        Validator.checkArgument(StringUtils.isBlank(accessToken),"发送支付通知accessToken不能为空");
        Validator.checkArgument(StringUtils.isBlank(payResultVO.getOutTradeNo()),"发送支付通知系统订单号不能为空");
        Validator.checkArgument(payResultVO.getTotalFee() == null, "发送支付通知消费金额不能为空");
        
        // 构造模版数据
        Map<String, TemplateData> dataMap = new HashMap<String,TemplateData>();
        /*
         * {{first.DATA}} 订单编号：{{keyword1.DATA}} 订单金额：{{keyword2.DATA}} 实付金额：{{keyword3.DATA}} 消费地点：{{keyword4.DATA}} 消费时间：{{keyword5.DATA}} {{remark.DATA}}
         */
        TemplateData first = new TemplateData("收到一笔支付通知，订单信息如下", "#000000");
        dataMap.put("first", first);
        TemplateData keyword1 = new TemplateData(payResultVO.getOutTradeNo(), "#000000");
        TemplateData keyword2 = new TemplateData(payResultVO.getTotalFee() + "", "#000000");
        TemplateData keyword3 = new TemplateData(payResultVO.getTotalFee() + "", "#000000");
        TemplateData keyword4 = new TemplateData(payResultVO.getDealerName() + "-" + payResultVO.getStoreName(), "#000000");
        TemplateData keyword5 = new TemplateData(DateUtil.getDate(payResultVO.getTransBeginTime(), "yyyy-MM-dd HH:mm:ss"));
        dataMap.put("keyword1", keyword1);
        dataMap.put("keyword2", keyword2);
        dataMap.put("keyword3", keyword3);
        dataMap.put("keyword4", keyword4);
        dataMap.put("keyword5", keyword5);
        // 构造模版消息包
        SendTemplateMsgReqData templateMsg = new SendTemplateMsgReqData(touser, null, TEMPLATE_ID_PAY_SUCCESS, null, dataMap);
        // 调用模版消息发送接口
        String jsonResult = WXPay.requestSendTemplateMsgService(templateMsg, accessToken, certLocalPath, certPassword);
        // 返回消息发送结果（不能确定是否下发至微信用户，需要查看事件推送结果）
        return JSONUtil.parseObject(jsonResult, SendTemplateMsgResData.class);
    }
}
