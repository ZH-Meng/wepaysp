package com.zbsp.wepaysp.manage.web.action.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;

import com.zbsp.wepaysp.common.security.DigestHelper;
import com.zbsp.wepaysp.manage.web.action.BaseAction;
import com.zbsp.wepaysp.manage.web.security.ManageUser;
import com.zbsp.wepaysp.service.manage.SysUserService;

public class ModifyPwdAction
    extends BaseAction {

    private static final long serialVersionUID = -2828176954618669417L;

    private String oldPwd;
    private String newPwd;
    private String confirmPwd;
    private SysUserService sysUserService;

    @Override
    public String execute()
        throws Exception {
        ManageUser manageUser = (ManageUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("sysUserOid", manageUser.getIwoid());

        String message = null;

        if (!newPwd.equals(confirmPwd)) {
            message = "用户新密码与确认密码不一致";
        } else {
            try {
                sysUserService.doTransModifyUserPwd(manageUser.getIwoid(), DigestHelper.sha512Hex(oldPwd), DigestHelper.sha512Hex(newPwd));
                message = "修改成功";
            } catch (IllegalArgumentException e) {
                logger.warn("系统用户修改密码失败:" + e.getMessage());
                message = "修改失败！";
            } catch (Exception e) {
                message = "修改失败！";
                logger.error("系统用户修改密码错误:" + e.getMessage());
            }
        }
        setAlertMessage(message);
        return SUCCESS;
    }

    public String getOldPwd() {
        return oldPwd;
    }

    public void setOldPwd(String oldPwd) {
        this.oldPwd = oldPwd;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }

    public String getConfirmPwd() {
        return confirmPwd;
    }

    public void setConfirmPwd(String confirmPwd) {
        this.confirmPwd = confirmPwd;
    }

    public void setSysUserService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

}
