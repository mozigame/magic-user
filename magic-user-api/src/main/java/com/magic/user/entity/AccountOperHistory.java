package com.magic.user.entity;

import com.magic.user.enums.AccountType;

import java.util.Date;

/**
 * @Doc 资料修改记录
 */
public class AccountOperHistory {

    private long id;

    private String username;    //修改的用户账号

    private long userId;    //修改的用户id

    private String beforeInfo;  //修改前的信息

    private String afterInfo;   //修改后的信息

    private long procUserId;    //操作人id

    private String procUsername;    //操作人账号

    private AccountType type;   //账号类型

    private Date createTime;    //操作时间


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getBeforeInfo() {
        return beforeInfo;
    }

    public void setBeforeInfo(String beforeInfo) {
        this.beforeInfo = beforeInfo;
    }

    public String getAfterInfo() {
        return afterInfo;
    }

    public void setAfterInfo(String afterInfo) {
        this.afterInfo = afterInfo;
    }

    public long getProcUserId() {
        return procUserId;
    }

    public void setProcUserId(long procUserId) {
        this.procUserId = procUserId;
    }

    public String getProcUsername() {
        return procUsername;
    }

    public void setProcUsername(String procUsername) {
        this.procUsername = procUsername;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}