package com.zbsp.wepaysp.manage.web.action.pay;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.interceptor.SessionAware;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tencent.WXPay;
import com.zbsp.wepaysp.common.util.TimeUtil;
import com.zbsp.wepaysp.common.util.WeixinPackConverter;
import com.zbsp.wepaysp.manage.web.action.BaseAction;
import com.zbsp.wepaysp.manage.web.listener.DefaultScanPayBusinessResultListener;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.po.manage.SysUser;
import com.zbsp.wepaysp.po.pay.WeixinPayDetails;
import com.zbsp.wepaysp.service.pay.WeixinPayDetailsService;
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

    private WeixinPayDetailsService weixinPayDetailsService;

    /**
     * 访问收银台-限定收银员用户
     */
    public String goToCashierDesk() {
        logger.info("开始跳转到收银台.");

        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!isDealerEmployee(manageUser)) {
            logger.warn("非收银员不能收银！");
            setAlertMessage("非收银员不能收银！");
            return "accessDenied";
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        
        // 当前收银员当天的收款记录
        Date today = new Date();
        paramMap.put("beginTime", TimeUtil.getDayStart(today));
        paramMap.put("endTime", TimeUtil.getDayEnd(today));
        paramMap.put("dealerEmployeeOid", manageUser.getDataDealerEmployee().getIwoid());
        weixinPayDetailsService.doJoinTransQueryWeixinPayDetailsList(paramMap, 0, -1);

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
        if (!isDealerEmployee(manageUser)) {
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
        payDetailsVO.setDealerEmployeeOid(manageUser.getDataDealerEmployee().getIwoid());
        payDetailsVO.setTotalFee(money1.multiply(new BigDecimal(100)).intValue());// 元转化为分
        payDetailsVO.setAuthCode(authCode);

        weixinPayDetailsService.doTransCreatePayDetails(payDetailsVO, manageUser.getUserId(), manageUser.getIwoid(), (String) session.get("currentLogFunctionOid"));
        logger.info("微信支付明细保存成功！");

        // 提交微信下单并刷卡支付
        logger.info("开始微信刷卡支付！");
        try {
            // 组包、调用刷卡API
            WXPay.doScanPayBusiness(WeixinPackConverter.weixinPayDetailsVO2ScanPayReq(payDetailsVO), new DefaultScanPayBusinessResultListener());
            //TODO 各种事件响应回调处理
            
            
            logger.info("微信刷卡支付成功！");
            setAlertMessage("支付成功！");
        } catch (Exception e) {
            logger.error("支付失败，" + e.getMessage());
            e.printStackTrace();
            setAlertMessage("支付失败，请重试！");
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        // 当前收银员当天的收款记录
        Date today = new Date();
        paramMap.put("beginTime", TimeUtil.getDayStart(today));
        paramMap.put("endTime", TimeUtil.getDayEnd(today));
        paramMap.put("dealerEmployeeOid", dealerEmployeeOid);
        weixinPayDetailsService.doJoinTransQueryWeixinPayDetailsList(paramMap, 0, -1);

        return "cashierDesk";
    }

    /**
     * 是否是商户员工
     * 
     * @return
     */
    private boolean isDealerEmployee(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if (level != SysUser.UserLevel.cashier.getValue() || manageUser.getDataDealerEmployee() == null) {
                return false;
            }
        }
        return true;
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

    public void setWeixinPayDetailsService(WeixinPayDetailsService weixinPayDetailsService) {
        this.weixinPayDetailsService = weixinPayDetailsService;
    }

}
