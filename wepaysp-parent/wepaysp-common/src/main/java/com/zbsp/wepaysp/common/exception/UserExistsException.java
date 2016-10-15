/*
 * UserExistsException.java
 * 创建者：杨帆
 * 创建日期：2014年8月2日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.common.exception;

/**
 * 用户已存在异常
 * 
 * @author 杨帆
 */
public class UserExistsException extends BaseException {

    private static final long serialVersionUID = 4572748740090380756L;
    
    public UserExistsException(String msg) {
        setMsg(msg);
    }
}
