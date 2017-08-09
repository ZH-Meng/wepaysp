package com.zbsp.alipay.trade.model;

import com.google.gson.annotations.SerializedName;

/**
 * 孩子的家长信息
 */
public class UserDetails {

    /** 必填，孩子家长的手机号 */
    @SerializedName("user_mobile")
    private String userMobile;

    /** 可选，家长姓名,如果填写则新增或更新 */
    @SerializedName("user_name")
    private String userName;

    /** 可选，孩子与家长的关系： 1、爸爸 2、妈妈 3、爷爷 4、奶奶 5、外公 6、外婆 7、家长；如果填写则新增或更新,默认为家长 */
    @SerializedName("user_relation")
    private String userRelation;

    /** 可选，用户变更手机号，替换旧的手机号 */
    @SerializedName("user_change_mobile")
    private String userChangeMobile;

    public UserDetails() {
        super();
    }

    public UserDetails(String userMobile, String userName, String userRelation, String userChangeMobile) {
        super();
        this.userMobile = userMobile;
        this.userName = userName;
        this.userRelation = userRelation;
        this.userChangeMobile = userChangeMobile;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserDetail [userMobile=").append(userMobile).append(", userName=").append(userName).append(", userRelation=").append(userRelation).append(", userChangeMobile=")
            .append(userChangeMobile).append("]");
        return builder.toString();
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRelation() {
        return userRelation;
    }

    public void setUserRelation(String userRelation) {
        this.userRelation = userRelation;
    }

    public String getUserChangeMobile() {
        return userChangeMobile;
    }

    public void setUserChangeMobile(String userChangeMobile) {
        this.userChangeMobile = userChangeMobile;
    }

}
