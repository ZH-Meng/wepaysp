/*
 * AreaNotAllowedException.java
 * 创建者：杨帆
 * 创建日期：2014年8月2日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.common.exception;

/**
 * 地区禁止异常
 * 
 * @author 杨帆
 */
public class AreaNotAllowedException extends BaseException {

    private static final long serialVersionUID = -5800159393571685116L;

    public AreaNotAllowedException(String msg) {
        setMsg(msg);
    }
}
