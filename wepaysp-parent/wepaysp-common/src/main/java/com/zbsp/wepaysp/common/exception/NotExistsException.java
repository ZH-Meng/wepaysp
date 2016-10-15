/*
 * NotExistsException.java
 * 创建者：杨帆
 * 创建日期：2014年1月24日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.common.exception;

/**
 * （实体、记录）不存在异常
 * 
 * @author 杨帆
 */
public class NotExistsException  extends BaseException {

    private static final long serialVersionUID = -5967327910318904065L;

    public NotExistsException(String msg) {
        setMsg(msg);
    }
}
