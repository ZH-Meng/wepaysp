package com.zbsp.alipay.trade.model;

import com.google.gson.annotations.SerializedName;

/**
 * 缴费详情
 */
public class ChargeItems {

    /** 必填，缴费项名称 */
    @SerializedName("item_name")
    private String itemName;

    /** 必填，缴费的金额，保留2位小数 */
    @SerializedName("item_price")
    private String itemPrice;
    
    public ChargeItems() {
        super();
    }

    public ChargeItems(String itemName, String itemPrice) {
        super();
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ChargeItem [itemName=").append(itemName).append(", itemPrice=").append(itemPrice).append("]");
        return builder.toString();
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

}
