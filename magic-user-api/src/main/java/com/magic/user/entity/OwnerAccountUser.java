package com.magic.user.entity;

/**
 * @Doc 账户与用户id映射表
 */
public class OwnerAccountUser {
    private Long id;

    private String assemAccount;    //聚合账号，格式：业主ID-账号名(股东或代理)

    private Long userId;    //用户id

    public OwnerAccountUser(){}

    public OwnerAccountUser(String assemAccount, Long userId) {
        this.assemAccount = assemAccount;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAssemAccount() {
        return assemAccount;
    }

    public void setAssemAccount(String assemAccount) {
        this.assemAccount = assemAccount == null ? null : assemAccount.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}