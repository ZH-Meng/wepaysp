package com.zbsp.wepaysp.manage.web.action.pay;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.constant.EnumDefine.WxPayResult;
import com.zbsp.wepaysp.common.exception.InvalidValueException;
import com.zbsp.wepaysp.common.exception.NotExistsException;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.manage.web.action.BaseAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.manage.web.util.SysUserUtil;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails.PayType;
import com.zbsp.wepaysp.api.service.main.pay.WeixinPayDetailsMainService;
import com.zbsp.wepaysp.api.service.main.pay.WeixinRefundDetailsMainService;
import com.zbsp.wepaysp.api.service.pay.WeixinPayDetailsService;
import com.zbsp.wepaysp.vo.pay.WeixinPayDetailsVO;

/**
 * 收款
 * 
 * @author 孟郑宏
 */
public class PaymentAction
    extends BaseAction
    implements SessionAware {

    private static final long serialVersionUID = -7528241554406736862L;
    private Map<String, Object> session;
    
    private String money;// 收银台输入金额，单位为元
    private String authCode;// 扫码支付授权码，设备读取用户微信中的条码或者二维码信息
    
    private List<WeixinPayDetailsVO> weixinPayDetailsVoList;
    private WeixinPayDetailsService weixinPayDetailsService;
    private WeixinPayDetailsMainService weixinPayDetailsMainService;
    
    private WeixinRefundDetailsMainService weixinRefundDetailsMainService;
    private String payDetailsOid;
    /**
     * 访问收银台-限定收银员用户
     */
    public String goToCashierDesk() {
        logger.info("开始跳转到收银台.");

        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!SysUserUtil.isDealerEmployee(manageUser)) {
            logger.warn("非收银员不能收银！");
            setAlertMessage("非收银员不能收银！");
            return "accessDenied";
        }
        weixinPayDetailsVoList = listTodayPayDetails(manageUser.getDataDealerEmployee().getIwoid());

        return "cashierDesk";
    }

    /**
     * 收银员输入金额，扫描枪扫码，确认收银，方法执行动作：
     * 
     * <pre>
     *      校验权限；
     *      保存交易明细；
     *      微信刷卡支付（组装刷卡API请求包、调用API）；
     *      查询当日收银记录
     * </pre>
     */
    public String cashier() {
        logger.info("开始收银.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!SysUserUtil.isDealerEmployee(manageUser)) {
            logger.warn("非收银员不能收银！");
            setAlertMessage("非收银员不能收银！");
            return "accessDenied";
        }
        if (StringUtils.isBlank(money) && NumberUtils.isNumber(money)) {
            logger.warn("金额无效！");
            setAlertMessage("金额无效，请重新输入，单位为元！");
            return "cashierDesk";
        }
        if (StringUtils.isBlank(authCode)) {
            logger.warn("收款码(授权)为空！");
            setAlertMessage("收款码无效，请重试！");
            return "cashierDesk";
        }

        BigDecimal money1 = new BigDecimal(money);
        String dealerEmployeeOid = manageUser.getDataDealerEmployee().getIwoid();

        // 保存交易明细
        WeixinPayDetailsVO payDetailsVO = new WeixinPayDetailsVO();
        payDetailsVO.setPayType(WeixinPayDetails.PayType.MICROPAY.getValue());// 刷卡支付
        payDetailsVO.setDealerEmployeeOid(dealerEmployeeOid);
        payDetailsVO.setTotalFee(money1.multiply(new BigDecimal(100)).intValue());// 元转化为分
        payDetailsVO.setAuthCode(authCode);
        
        try {
            Map<String, Object> resultMap = weixinPayDetailsMainService.createPayAndInvokeWxPay(payDetailsVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
            String resCode = MapUtils.getString(resultMap, "resultCode");
            String resDesc = MapUtils.getString(resultMap, "resultDesc");
            payDetailsVO = (WeixinPayDetailsVO) MapUtils.getObject(resultMap, "wexinPayDetailsVO");
            
            if (!StringUtils.equalsIgnoreCase(WxPayResult.SUCCESS.getCode(), resCode)) {// 支付失败
                logger.warn("微信刷卡支付失败，错误码：" + resCode + "，错误描述：" + resDesc);
                setAlertMessage(resDesc);
            } else {
                logger.info("微信刷卡支付成功！");
                //setAlertMessage("支付成功！");
            }
            
        } catch (InvalidValueException e) {
            logger.warn(e.getMessage());
            setAlertMessage("支付失败！");
        } catch (IllegalArgumentException e) {
            logger.warn(e.getMessage());
            setAlertMessage("支付失败！");
        } catch (NotExistsException e) {
            logger.warn(e.getMessage());
            setAlertMessage("支付失败！");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            setAlertMessage("支付失败！");
        }

        weixinPayDetailsVoList = listTodayPayDetails(dealerEmployeeOid);

        return "cashierDesk";
    }

    /**
     * 收银员点击退款方法执行动作：
     * 
     * <pre>
     *      校验权限；
     *      保存保存退款明细；
     *      微信撤销支付（组装刷卡API请求包、调用API）；
     *      查询当日收银记录
     * </pre>
     */
    public String refund() {
    	logger.info("开始收银台退款.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!SysUserUtil.isDealerEmployee(manageUser)) {
            logger.warn("非收银员不能收银！");
            setAlertMessage("非收银员不能收银！");
            return "accessDenied";
        }
    	if (StringUtils.isBlank(payDetailsOid)) {
            logger.warn("支付明细Oid为空！");
            setAlertMessage("非法操作，必须选择一个支付明细进行退款！");
            return "cashierDesk";
        }
    	
    	String dealerEmployeeOid = manageUser.getDataDealerEmployee().getIwoid();
    	WeixinPayDetailsVO payDetailsVO = new WeixinPayDetailsVO();
    	payDetailsVO.setDealerEmployeeOid(dealerEmployeeOid);
    	payDetailsVO.setIwoid(payDetailsOid);
    	
    	weixinRefundDetailsMainService.cashierDeskRefund(payDetailsVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
    	
    	weixinPayDetailsVoList = listTodayPayDetails(manageUser.getDataDealerEmployee().getIwoid());
    	return "cashierDesk";
    }
    
    /**
     * 收银员在收银台查看当天刷卡支付收款记录
     * @param dealerEmployeeOid
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<WeixinPayDetailsVO> listTodayPayDetails(String dealerEmployeeOid) {
    	Map<String, Object> paramMap = new HashMap<String, Object>();
        // 当前收银员当天的收款记录
        Date today = new Date();
        paramMap.put("beginTime", TimeUtil.getDayStart(today));
        paramMap.put("endTime", TimeUtil.getDayEnd(today));
        paramMap.put("dealerEmployeeOid", dealerEmployeeOid);
        paramMap.put("payType", PayType.MICROPAY.toString());// 收银台刷卡支付
        return (List<WeixinPayDetailsVO>) MapUtils.getObject(weixinPayDetailsService.doJoinTransQueryWeixinPayDetails(paramMap, 0, -1), "payList");
    }
    
    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public List<WeixinPayDetailsVO> getWeixinPayDetailsVoList() {
		return weixinPayDetailsVoList;
	}

	public void setWeixinPayDetailsService(WeixinPayDetailsService weixinPayDetailsService) {
        this.weixinPayDetailsService = weixinPayDetailsService;
    }
    
    public void setWeixinPayDetailsMainService(WeixinPayDetailsMainService weixinPayDetailsMainService) {
        this.weixinPayDetailsMainService = weixinPayDetailsMainService;
    }

	public void setPayDetailsOid(String payDetailsOid) {
		this.payDetailsOid = payDetailsOid;
	}

	public void setWeixinRefundDetailsMainService(WeixinRefundDetailsMainService weixinRefundDetailsMainService) {
		this.weixinRefundDetailsMainService = weixinRefundDetailsMainService;
	}

}
