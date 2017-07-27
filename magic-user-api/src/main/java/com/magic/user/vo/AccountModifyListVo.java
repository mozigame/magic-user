package com.magic.user.vo;

import com.alibaba.fastjson.JSONObject;

/**
 * AccountModifyListVo
 *
 * @author zj
 * @date 2017/5/9
 */
public class AccountModifyListVo {

    private Long ownerId;//业主ID
    private String ownerName;//业主名称
    private Integer type;//账号类型,
    private Long accountId;//账号ID
    private String showType;//类型描述
    private String account;//账号名
    private JSONObject before;//修改前
    private JSONObject after;//修改后
    private Long operatorId;//操作人ID
    private String operatorName;//操作人名称
    private String operatorTime;//操作时间
    private String beforeString;//修改前
    private String afterString;//修改后

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public JSONObject getBefore() {
        return before;
    }

    public void setBefore(JSONObject before) {
        this.before = before;
    }

    public JSONObject getAfter() {
        return after;
    }

    public void setAfter(JSONObject after) {
        this.after = after;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperatorTime() {
        return operatorTime;
    }

    public void setOperatorTime(String operatorTime) {
        this.operatorTime = operatorTime;
    }

    public String getBeforeString() {
        return beforeString;
    }

    public void setBeforeString(String beforeString) {
        this.beforeString = beforeString;
    }

    public String getAfterString() {
        return afterString;
    }

    public void setAfterString(String afterString) {
        this.afterString = afterString;
    }
}
