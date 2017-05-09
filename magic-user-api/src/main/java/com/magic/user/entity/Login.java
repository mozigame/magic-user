package com.magic.user.entity;

import com.magic.user.enums.LoginType;

import java.util.Date;

/**
 * @Doc 登录基础信息
 */
public class Login {
    private long id;

    private long userId;    //用户id

    private String username;    //账号

    private String password;    //密码

    private Date updateTime;    //修改时间，最后登录时间

    private int lastLoginIp;    //最后登录ip

    private LoginType status; //登录等出

    public Login(){}
    public Login(long userId, String username, String password) {
        this.userId = userId;
        this.username = username;
        this.password = password;
    }

    public Login(Date updateTime, int lastLoginIp, LoginType status) {
        this.updateTime = updateTime;
        this.lastLoginIp = lastLoginIp;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(int lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public LoginType getStatus() {
        return status;
    }

    public void setStatus(LoginType status) {
        this.status = status;
    }
}