package com.zbsp.wepaysp.manage.web.action.pay;

import java.math.BigDecimal;

import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.manage.web.action.BaseAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.po.manage.SysUser;

/**
 * 收款
 * 
 * @author 孟郑宏
 */
public class PaymentAction
    extends BaseAction {
    
    private static final long serialVersionUID = -7528241554406736862L;
    
    private String dealerEmployeeOid;
    private String money;
    private String code;

    public String goToCashierDesk() {
        logger.info("开始跳转到收银台.");
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!isDealerEmployee(manageUser)) {
            logger.warn("非收银员不能收银！");
            setAlertMessage("非收银员不能收银！");
            return "accessDenied";
        }
        dealerEmployeeOid = manageUser.getDataDealerEmployee().getIwoid();
        
        //TODO 收款记录
        
        return "cashierDesk";
    }
    
    /**
     * 收银
     * 
     * @return
     */
    public String cashier() {
        logger.info("开始收银.");
        BigDecimal money1 = new BigDecimal(money);
        // 保存交易明细
        
        // 提交微信支付
        
        //TODO 收款记录
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
    
    public String getDealerEmployeeOid() {
        return dealerEmployeeOid;
    }

    public void setDealerEmployeeOid(String dealerEmployeeOid) {
        this.dealerEmployeeOid = dealerEmployeeOid;
    }
    
    public void setMoney(String money) {
        this.money = money;
    }
    
    public void setCode(String code) {
        this.code = code;
    }

}
