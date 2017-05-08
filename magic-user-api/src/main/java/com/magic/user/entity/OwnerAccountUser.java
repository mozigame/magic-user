package com.magic.user.entity;

/**
 * @Doc 账户与用户id映射表
 */
public class OwnerAccountUser {
    private long id;

    private String assemAccount;    //聚合账号，格式：业主ID-账号名(股东或代理)

    private long userId;    //用户id

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAssemAccount() {
        return assemAccount;
    }

    public void setAssemAccount(String assemAccount) {
        this.assemAccount = assemAccount == null ? null : assemAccount.trim();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}