/*
 * IssueNotOpenException.java
 * 创建者：杨帆
 * 创建日期：2016年5月8日
 *
 * 版权所有(C) 2015-2018。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.common.exception;

/**
 * 未开期异常.
 * 
 * @author 杨帆
 */
public class IssueNotOpenException extends BaseException {

    private static final long serialVersionUID = 1462022621283627558L;
    
    public IssueNotOpenException(String msg) {
        setMsg(msg);
    }
}
