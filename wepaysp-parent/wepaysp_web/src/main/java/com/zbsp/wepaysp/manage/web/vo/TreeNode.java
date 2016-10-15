/*
 * TreeNode.java
 * 创建者：杨帆
 * 创建日期：2015年6月10日
 *
 * 版权所有(C) 2011-2014。英泰伟业科技(北京)有限公司。
 * 保留所有权利。
 */
package com.zbsp.wepaysp.manage.web.vo;

import java.io.Serializable;

/**
 * 菜单节点对象
 * 
 * @author 杨帆
 */
public class TreeNode implements Serializable {

    private static final long serialVersionUID = 725027597661693119L;
    
    // 节点ID
    private String nodeId;
    // 父节点ID
    private String fatherId;
    // 节点名称（菜单名称）
    private String nodeName;
    // 节点链接地址
    private String nodeUrl;
    // 节点顺序
    private int nodeOrder;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public int getNodeOrder() {
        return nodeOrder;
    }

    public void setNodeOrder(int nodeOrder) {
        this.nodeOrder = nodeOrder;
    }
}
