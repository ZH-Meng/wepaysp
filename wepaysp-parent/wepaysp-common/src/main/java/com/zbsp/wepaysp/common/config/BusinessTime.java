/*
 * BusinessTime.java
 * 创建者：Mengxh
 * 创建日期：2010-9-21
 *
 * 版权所有(C) 2008-2011。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.common.config;

/**
 * 业务等待时间类（同步、异步通讯等待时间）
 * 
 * @author Mengxh
 */
public class BusinessTime {

    // 短暂
    public static final long SHORT = 20000L;
    // 标准
    public static final long NORMAL = 30000L;
    // 延长
    public static final long LONG = 60000L;
    // 超长
    public static final long SUPERLONG = 180000L;
}
