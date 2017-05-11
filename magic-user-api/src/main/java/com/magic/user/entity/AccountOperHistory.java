package com.magic.user.entity;

import com.magic.user.enums.AccountType;

/**
 * @Doc 资料修改记录
 */
public class AccountOperHistory {

    private Long id;

    private String username;    //修改的用户账号

    private Long userId;    //修改的用户id

    private String beforeInfo;  //修改前的信息

    private String afterInfo;   //修改后的信息

    private Long procUserId;    //操作人id

    private String procUsername;    //操作人账号

    private AccountType type;   //账号类型

    private Long ownerId;   //业主id

    private String ownerName;   //业主名称

    private Long createTime;    //操作时间

    public AccountOperHistory() {
    }

    public AccountOperHistory(String username, Long userId, String beforeInfo, String afterInfo, Long procUserId, String procUsername, AccountType type, Long createTime) {
        this.username = username;
        this.userId = userId;
        this.beforeInfo = beforeInfo;
        this.afterInfo = afterInfo;
        this.procUserId = procUserId;
        this.procUsername = procUsername;
        this.type = type;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

    public Long getProcUserId() {
        return procUserId;
    }

    public void setProcUserId(Long procUserId) {
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

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }
}