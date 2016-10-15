/*
 * NotEnoughException.java
 * 创建者：杨帆
 * 创建日期：2014年1月24日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.common.exception;

/**
 * （资源、数量）不足异常
 * 
 * @author 杨帆
 */
public class NotEnoughException extends BaseException {

    private static final long serialVersionUID = -3839347181933563019L;

    public NotEnoughException(String msg) {
        setMsg(msg);
    }
}
