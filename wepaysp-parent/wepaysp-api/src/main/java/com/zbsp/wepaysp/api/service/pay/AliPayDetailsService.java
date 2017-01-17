package com.zbsp.wepaysp.api.service.pay;

import com.zbsp.wepaysp.vo.pay.AliPayDetailsVO;

/**
 * 支付宝支付明细Service
 * 
 * @author 孟郑宏
 */
public interface AliPayDetailsService {

    /***
     * 生成、保存支付明细
     * @param payDetailsVO 支付参数
     * @return 保存的支付明细
     */
    public AliPayDetailsVO doTransCreatePayDetails(AliPayDetailsVO payDetailsVO);


    /**
     * 更新当面付-条码支付结果
     * 
     * @param code 支付宝网关返回码
     * @param subCode 支付宝支付业务码
     * @param payResultVO 支付结果
     * @return 更新的支付明细
     */
    public AliPayDetailsVO doTransUpdateFace2FacePayResult(String code, String subCode, AliPayDetailsVO payResultVO);

}
