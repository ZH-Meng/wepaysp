package com.zbsp.wepaysp.mobile.controller.pay;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zbsp.wepaysp.api.service.SysConfig;
import com.zbsp.wepaysp.common.constant.SysEnvKey;
import com.zbsp.wepaysp.common.constant.EnumDefine.PayClientType;
import com.zbsp.wepaysp.common.util.Generator;
import com.zbsp.wepaysp.mobile.common.constant.H5CommonResult;
import com.zbsp.wepaysp.mobile.controller.BaseController;
import com.zbsp.wepaysp.mobile.model.result.ErrResult;
import com.zbsp.wepaysp.mobile.model.vo.MobileH5PayIndexVO;

/**
 * 移动端支付入口
 * 
 * @author 孟郑宏
 */
@Controller
@RequestMapping("/pay/h5")
public class MobliePayIndexController extends BaseController {

    /**
     * 支付客户端检测
     * 
     * @return
     */
	@RequestMapping(value = "index", method = RequestMethod.GET)
    public ModelAndView payClientCheck(MobileH5PayIndexVO indexVO, HttpServletRequest httpRequest) {
        String logPrefix = "处理检查支付客户端请求 - ";
        logger.info(logPrefix + "开始");
        ModelAndView modelAndView = null;
        
        // 检查二维码包含的支付参数完整性（中转参数）
        if (indexVO == null || StringUtils.isBlank(indexVO.getPartnerOid()) || StringUtils.isBlank(indexVO.getDealerOid()) || StringUtils.isBlank(indexVO.getStoreOid())) {
            logger.warn(logPrefix + "支付参数不完整，indexVO：{}", indexVO == null ? null : indexVO.toString());
            modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.ARGUMENT_MISS.getCode(), H5CommonResult.ARGUMENT_MISS.getDesc()));
            return modelAndView;
        }
        
        ModelMap model=new ModelMap();
        String userAgent = httpRequest.getHeader("User-Agent").toLowerCase();
        if (userAgent.indexOf("micromessenger") != -1) {
            logger.info(logPrefix + "支付客户端为微信");
            Map<String, Object> partnerMap = SysConfig.partnerConfigMap.get(indexVO.getPartnerOid());
            if (partnerMap == null || partnerMap.isEmpty()) {
                logger.warn(logPrefix + "服务商信息配置不存在，partnerOid={}", indexVO.getPartnerOid());
                modelAndView = new ModelAndView("accessDeniedH5", "errResult", new ErrResult(H5CommonResult.INVALID_ARGUMENT.getCode(), H5CommonResult.INVALID_ARGUMENT.getDesc()));
                return modelAndView;
            } else {
            	String appid = MapUtils.getString(partnerMap, SysEnvKey.WX_APP_ID);// 微信公众号ID
                Map<String, String> urlParamMap = new HashMap<String, String>();
                urlParamMap.put("partnerOid", indexVO.getPartnerOid());
                urlParamMap.put("dealerOid", indexVO.getDealerOid());
                urlParamMap.put("storeOid", indexVO.getStoreOid());
                urlParamMap.put("dealerEmployeeOid", indexVO.getDealerEmployeeOid());
                urlParamMap.put("showwxpaytitle", "1");
                
                indexVO.setPayClient(PayClientType.APP_WEIXIN.getValue());
                indexVO.setPayUrl(Generator.generatePayURL(indexVO.getPayClient(), appid, SysConfig.wxPayCallBackURL, urlParamMap));
            }
        } else if (userAgent.indexOf("alipayclient") != -1) {
        	logger.info(logPrefix + "支付客户端为支付宝");
            indexVO.setPayClient(PayClientType.APP_ALI.getValue());
        } else {// 未知客户端
        	logger.info(logPrefix + "支付客户端未知");
        	indexVO.setPayClient(PayClientType.UN_KNOWN.getValue());
        }
        
        model.addAttribute("indexVO", indexVO);
        modelAndView = new ModelAndView("pay/h5PayIndex", model);
        
        logger.info(logPrefix + "结束");
        return modelAndView;
    }

}
