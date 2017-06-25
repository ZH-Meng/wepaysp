package com.zbsp.wepaysp.manage.web.util;

import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.po.manage.SysUser;

public class SysUserUtil {
	
    /**
     * 是否是顶级服务商
     * 
     * @return
     */
	public static boolean isTopPartner(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if (level == SysUser.UserLevel.partner.getValue() && manageUser.getDataPartner() != null && manageUser.getDataPartner().getLevel() == 1) {// 顶级服务商
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是代理商（含顶级代理商）
     * 
     * @return
     */
	public static boolean isPartner(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if (level != SysUser.UserLevel.partner.getValue() || manageUser.getDataPartner() == null) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 是否是业务员
     * 
     * @return
     */
    public static boolean isPartnerEmployee(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if (level != SysUser.UserLevel.salesman.getValue() || manageUser.getDataPartnerEmployee() == null) {
                return false;
            }
        }
        return true;
    }
	
    /**
     * 是否是商户
     * 
     * @return
     */
    public static boolean isDealer(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if (level != SysUser.UserLevel.dealer.getValue() || manageUser.getDataDealer() == null) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 是否是收银员（含店长）
     * 
     * @return
     */
    public static boolean isDealerEmployee(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if ((level != SysUser.UserLevel.cashier.getValue() && level != SysUser.UserLevel.shopManager.getValue()) || manageUser.getDataDealerEmployee() == null) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 是否是店长
     * 
     * @return
     */
    public static boolean isStoreManager(ManageUser manageUser) {
        int level = 0;
        if (manageUser.getUserLevel() == null) {
            return false;
        } else {
            level = manageUser.getUserLevel();
            if (level != SysUser.UserLevel.shopManager.getValue() || manageUser.getDataDealerEmployee() == null || manageUser.getDataDealerEmployee().getStore() == null) {
                return false;
            }
        }
        return true;
    }

}
