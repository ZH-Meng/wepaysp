package com.zbsp.wepaysp.api.util;

import com.zbsp.wepaysp.po.manage.SysUser;

public class SysUserUtil {
	
    /**
     * 是否是顶级服务商
     * 
     * @return
     */
	public static boolean isTopPartner(SysUser user) {
        int level = 0;
        if (user.getUserLevel() == null) {
            return false;
        } else {
            level = user.getUserLevel();
            if (level == SysUser.UserLevel.partner.getValue() && user.getPartner() != null && user.getPartner().getLevel() == 1) {// 顶级服务商
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是代理商
     * 
     * @return
     */
	public static boolean isPartner(SysUser user) {
        int level = 0;
        if (user.getUserLevel() == null) {
            return false;
        } else {
            level = user.getUserLevel();
            if (level != SysUser.UserLevel.partner.getValue() || user.getPartner() == null) {
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
    public static boolean isPartnerEmployee(SysUser user) {
        int level = 0;
        if (user.getUserLevel() == null) {
            return false;
        } else {
            level = user.getUserLevel();
            if (level != SysUser.UserLevel.salesman.getValue() || user.getPartnerEmployee() == null) {
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
    public static boolean isDealer(SysUser user) {
        int level = 0;
        if (user.getUserLevel() == null) {
            return false;
        } else {
            level = user.getUserLevel();
            if (level != SysUser.UserLevel.dealer.getValue() || user.getDealer() == null) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 是否是收银员
     * 
     * @return
     */
    public static boolean isDealerEmployee(SysUser user) {
        int level = 0;
        if (user.getUserLevel() == null) {
            return false;
        } else {
            level = user.getUserLevel();
            if ((level != SysUser.UserLevel.cashier.getValue() && level != SysUser.UserLevel.shopManager.getValue()) || user.getDealerEmployee() == null) {
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
    public static boolean isStoreManager(SysUser user) {
        int level = 0;
        if (user.getUserLevel() == null) {
            return false;
        } else {
            level = user.getUserLevel();
            if (level != SysUser.UserLevel.shopManager.getValue() || user.getDealerEmployee() == null) {
                return false;
            }
        }
        return true;
    }

}
