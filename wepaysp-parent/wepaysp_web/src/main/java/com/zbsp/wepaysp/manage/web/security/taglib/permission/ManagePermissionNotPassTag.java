/*
 * ManagePermissionNotPassTag.java
 * 创建者：杨帆
 * 创建日期：2015年6月12日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.security.taglib.permission;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 权限控制验证未通过标签
 * 
 * @author 杨帆
 */
public class ManagePermissionNotPassTag extends TagSupport {

    private static final long serialVersionUID = 3521147373478746738L;

    @Override
    public int doStartTag() throws JspException {
        ManagePermissionTag parent = (ManagePermissionTag) findAncestorWithClass(this, ManagePermissionTag.class);
        if (parent != null && !parent.isValidate()) {
            return EVAL_PAGE;
        }

        return SKIP_BODY;
    }
}
